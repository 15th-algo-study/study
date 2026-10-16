package study.runner;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** 결과를 모아 report.txt(사람용), result.json(앱·CI용), status(PASS/FAIL)로 저장한다. */
public final class Report {
    private final RunConfig cfg;
    private final Map<String, Group> groups = new LinkedHashMap<>();

    private static final class Group {
        final Problem problem;
        final Path solution;
        final List<CaseResult> cases = new ArrayList<>();
        String compileError;

        Group(Problem problem, Path solution) {
            this.problem = problem;
            this.solution = solution;
        }

        boolean passed() {
            return cases.stream().allMatch(CaseResult::passed);
        }
    }

    public Report(RunConfig cfg) {
        this.cfg = cfg;
    }

    public synchronized void add(Plan.Item item, CaseResult r) {
        String key = item.problem() == null ? "(설정)" : item.problem().id();
        Group g = groups.computeIfAbsent(key, k -> new Group(item.problem(), item.solution()));
        if (r.status() == CaseResult.Status.COMPILE_ERROR) {
            g.compileError = r.message();
        }
        g.cases.add(r);
    }

    public boolean passed() {
        return !groups.isEmpty() && groups.values().stream().allMatch(Group::passed);
    }

    public void write() throws IOException {
        Files.createDirectories(cfg.outDir());
        Files.writeString(cfg.outDir().resolve("report.txt"), text(), StandardCharsets.UTF_8);
        String json = Json.GSON.toJson(json());
        Files.writeString(cfg.outDir().resolve("result.json"), json, StandardCharsets.UTF_8);
        if (cfg.jsonOut() != null) {
            if (cfg.jsonOut().getParent() != null) Files.createDirectories(cfg.jsonOut().getParent());
            Files.writeString(cfg.jsonOut(), json, StandardCharsets.UTF_8);
        }
        Files.writeString(cfg.outDir().resolve("status"), passed() ? "PASS" : "FAIL", StandardCharsets.UTF_8);
    }

    private String rel(Path p) {
        if (p == null) return "";
        try {
            return cfg.root().relativize(p.toAbsolutePath().normalize()).toString().replace('\\', '/');
        } catch (IllegalArgumentException e) {
            return p.toString();
        }
    }

    String text() {
        StringBuilder sb = new StringBuilder();
        sb.append('\n');
        int problemsPassed = 0, casesPassed = 0, casesTotal = 0;
        for (Group g : groups.values()) {
            String title = g.problem == null ? "설정" : g.problem.id() + " · " + cfg.user();
            sb.append("━━ ").append(title).append(" ━━ ").append(rel(g.solution)).append(cfg.basicOnly() ? "  [basic 만]" : "")
                    .append(cfg.timeScale() != 1.0 ? "  [시간 제한 ×" + cfg.timeScale() + "]" : "").append('\n');
            if (g.compileError != null) {
                sb.append("  ✘ 컴파일 오류\n");
                g.compileError.lines().forEach(l -> sb.append("      ").append(l).append('\n'));
            }
            int ok = 0, total = 0;
            for (CaseResult r : g.cases) {
                if (r.kind().equals("template")) {
                    if (!r.passed()) appendDetail(sb.append("  ! template                 템플릿 오류\n"), r);
                    continue;
                }
                total++;
                if (r.passed()) ok++;
                if (r.status() == CaseResult.Status.COMPILE_ERROR) continue;
                String time = r.status() == CaseResult.Status.TLE ? ">" + (long) r.timeMs() + " ms" : fmtTime(r.timeMs());
                sb.append(String.format("  %s %-14s %10s", r.status().mark, r.name(), r.status().ordinal() == 0 || r.timeMs() > 0 ? time : ""));
                if (r.limitMs() != null) sb.append(String.format("  (제한 %d ms)", r.limitMs()));
                if (!r.passed()) sb.append("  ").append(r.status().label);
                if (r.note() != null) sb.append("  — ").append(r.note());
                sb.append('\n');
                if (!r.passed()) appendDetail(sb, r);
            }
            casesPassed += ok;
            casesTotal += total;
            if (g.passed()) problemsPassed++;
            sb.append("  → ").append(ok).append('/').append(total).append(" 통과").append(g.passed() ? "" : "  ✘ 실패").append("\n\n");
        }
        sb.append(passed() ? "✅ " : "❌ ").append("문제 ").append(problemsPassed).append('/').append(groups.size())
                .append(" 통과 · 케이스 ").append(casesPassed).append('/').append(casesTotal).append('\n');
        return sb.toString();
    }

    private static void appendDetail(StringBuilder sb, CaseResult r) {
        if (r.input() != null) sb.append("      입력  : ").append(r.input()).append('\n');
        if (r.expected() != null && r.status() == CaseResult.Status.WRONG) sb.append("      기대값: ").append(r.expected()).append('\n');
        if (r.actual() != null) sb.append("      실제값: ").append(r.actual()).append('\n');
        if (r.message() != null && r.status() != CaseResult.Status.WRONG) {
            r.message().lines().forEach(l -> sb.append("      ").append(l).append('\n'));
        }
    }

    private static String fmtTime(double ms) {
        return ms < 1 ? "<1 ms" : String.format("%d ms", Math.round(ms));
    }

    JsonObject json() {
        JsonObject root = new JsonObject();
        root.addProperty("user", cfg.user());
        root.addProperty("mode", cfg.basicOnly() ? "basic" : "all");
        root.addProperty("timeScale", cfg.timeScale());
        root.addProperty("status", passed() ? "PASS" : "FAIL");
        JsonArray problems = new JsonArray();
        int casesPassed = 0, casesTotal = 0;
        for (Group g : groups.values()) {
            JsonObject p = new JsonObject();
            if (g.problem != null) {
                p.addProperty("day", g.problem.dayName());
                p.addProperty("level", g.problem.level());
            }
            p.addProperty("solution", rel(g.solution));
            p.addProperty("status", g.passed() ? "PASS" : g.compileError != null ? "COMPILE_ERROR" : "FAIL");
            p.addProperty("compileError", g.compileError);
            JsonArray cases = new JsonArray();
            for (CaseResult r : g.cases) {
                if (r.kind().equals("template") && r.passed()) continue;
                JsonObject c = (JsonObject) Json.GSON.toJsonTree(r);
                c.addProperty("status", r.status().name());
                c.addProperty("statusLabel", r.status().label);
                c.addProperty("timeMs", Math.round(r.timeMs() * 10) / 10.0);
                cases.add(c);
                if (!r.kind().equals("template")) {
                    casesTotal++;
                    if (r.passed()) casesPassed++;
                }
            }
            p.add("cases", cases);
            problems.add(p);
        }
        root.add("problems", problems);
        JsonObject summary = new JsonObject();
        summary.addProperty("passed", casesPassed);
        summary.addProperty("total", casesTotal);
        root.add("summary", summary);
        return root;
    }
}
