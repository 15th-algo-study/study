package study.runner;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.stream.Stream;

/** 채점 대상 (문제 × 케이스) 목록을 만든다. */
public final class Plan {
    public enum Kind { CASE, TEMPLATE, ERROR }

    /** 채점 단위 하나. */
    public record Item(Kind kind, Problem problem, Path solution, String name, JsonObject data, String message) {
        public boolean large() {
            return name != null && name.startsWith("large");
        }

        public String displayName() {
            return (problem == null ? "" : problem.id() + " ") + (name == null ? "" : name);
        }
    }

    private static final java.util.regex.Pattern DAY_DIR = java.util.regex.Pattern.compile("day-(\\d{3})");
    private static final java.util.regex.Pattern LV_DIR = java.util.regex.Pattern.compile("lv(\\d)");

    private Plan() {}

    public static List<Item> build(RunConfig cfg) {
        List<Item> items = new ArrayList<>();
        List<Problem> problems;
        try {
            problems = targets(cfg);
        } catch (IllegalArgumentException | IOException e) {
            items.add(new Item(Kind.ERROR, null, null, "설정", null, e.getMessage()));
            return items;
        }
        if (problems.isEmpty()) {
            items.add(new Item(Kind.ERROR, null, null, "설정", null,
                    "채점할 문제가 없습니다. (user=" + cfg.user() + ", day=" + cfg.daySpec() + ", level=" + cfg.levelSpec() + ")"));
            return items;
        }
        for (Problem p : problems) {
            Path solution = solutionPath(cfg, p);
            if (cfg.isEditorial() && cfg.source() == null) {
                items.add(new Item(Kind.TEMPLATE, p, solution, "template", null, null));
            }
            try {
                for (Path f : caseFiles(p)) {
                    String name = f.getFileName().toString().replaceFirst("\\.json$", "");
                    if (cfg.basicOnly() && !name.startsWith("basic")) continue;
                    JsonObject data = JsonParser.parseString(Files.readString(f, StandardCharsets.UTF_8)).getAsJsonObject();
                    items.add(new Item(Kind.CASE, p, solution, name, data, null));
                }
            } catch (Exception e) {
                items.add(new Item(Kind.ERROR, p, solution, "cases", null, "케이스 파일을 읽을 수 없습니다: " + e.getMessage()));
            }
        }
        return items;
    }

    public static Path solutionPath(RunConfig cfg, Problem p) {
        if (cfg.source() != null) return cfg.source();
        if (cfg.isEditorial()) return p.editorial();
        return cfg.root().resolve("solutions").resolve(cfg.user()).resolve(p.dayName()).resolve("lv" + p.level()).resolve("Solution.java");
    }

    public static List<Path> caseFiles(Problem p) throws IOException {
        Path dir = p.dir().resolve("cases");
        if (!Files.isDirectory(dir)) return List.of();
        try (Stream<Path> s = Files.list(dir)) {
            return s.filter(f -> f.getFileName().toString().endsWith(".json")).sorted().toList();
        }
    }

    /** day/level 지정에 맞는 문제 목록. 사용자 풀이 채점에서 범위를 지정하지 않으면 그 사용자가 푼 문제만. */
    public static List<Problem> targets(RunConfig cfg) throws IOException {
        TreeSet<Integer> days = parseSpec(cfg.daySpec(), 1, 999, "day");
        TreeSet<Integer> levels = parseSpec(cfg.levelSpec(), 0, 3, "level");
        boolean explicit = !cfg.daySpec().equals("all") && !cfg.levelSpec().equals("all");
        if (cfg.source() != null && !(explicit && days.size() == 1 && levels.size() == 1)) {
            throw new IllegalArgumentException("-Psource 를 쓸 때는 -Pday 와 -Plevel 을 하나씩 지정해야 합니다.");
        }
        List<Problem> out = new ArrayList<>();
        for (Problem p : allProblems(cfg.root())) {
            if (!days.contains(p.day()) || !levels.contains(p.level())) continue;
            boolean needsFile = !cfg.isEditorial() && cfg.source() == null && !explicit;
            if (needsFile && !Files.exists(solutionPath(cfg, p))) continue;
            out.add(p);
        }
        return out;
    }

    public static List<Problem> allProblems(Path root) throws IOException {
        Path problems = root.resolve("problems");
        List<Problem> out = new ArrayList<>();
        if (!Files.isDirectory(problems)) return out;
        try (Stream<Path> ds = Files.list(problems)) {
            for (Path d : ds.sorted().toList()) {
                Matcher dm = DAY_DIR.matcher(d.getFileName().toString());
                if (!dm.matches() || !Files.isDirectory(d)) continue;
                try (Stream<Path> ls = Files.list(d)) {
                    for (Path l : ls.sorted().toList()) {
                        Matcher lm = LV_DIR.matcher(l.getFileName().toString());
                        if (lm.matches() && Files.isDirectory(l)) {
                            out.add(new Problem(Integer.parseInt(dm.group(1)), Integer.parseInt(lm.group(1)), l));
                        }
                    }
                }
            }
        }
        return out;
    }

    /** "all" | "3" | "003" | "1-5" | "1,3,7-9" */
    static TreeSet<Integer> parseSpec(String spec, int min, int max, String what) {
        TreeSet<Integer> out = new TreeSet<>();
        if (spec == null || spec.equals("all")) {
            for (int i = min; i <= max; i++) out.add(i);
            return out;
        }
        try {
            for (String part : spec.split(",")) {
                part = part.trim();
                if (part.isEmpty()) continue;
                int dash = part.indexOf('-');
                int a = Integer.parseInt(dash < 0 ? part : part.substring(0, dash));
                int b = dash < 0 ? a : Integer.parseInt(part.substring(dash + 1));
                for (int i = a; i <= b; i++) out.add(i);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(what + " 지정이 올바르지 않습니다: " + spec);
        }
        if (out.isEmpty() || out.first() < min || out.last() > max) {
            throw new IllegalArgumentException(what + " 범위는 " + min + "~" + max + " 입니다: " + spec);
        }
        return out;
    }
}
