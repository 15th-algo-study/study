package study.runner;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.TreeSet;

/**
 * 출제 도구: editorial 풀이를 실행해 expected 가 없는 케이스를 채운다.
 * 출력 JSON 이 2,000자를 넘으면 expected_sha256 + expected_preview 로 저장한다.
 * args: root day level force
 */
public final class FillExpected {
    private static final int INLINE_LIMIT = 2000;

    public static void main(String[] a) throws Throwable {
        Path root = Path.of(a[0]);
        TreeSet<Integer> days = Plan.parseSpec(a[1], 1, 999, "day");
        TreeSet<Integer> levels = Plan.parseSpec(a[2], 0, 3, "level");
        boolean force = Boolean.parseBoolean(a[3]);
        Judge judge = new Judge(TimeLimiter.NONE);
        int filled = 0;
        for (Problem p : Plan.allProblems(root)) {
            if (!days.contains(p.day()) || !levels.contains(p.level())) continue;
            CompiledSolution sol = judge.compiled(p.editorial());
            if (!sol.ok()) {
                System.out.println("✘ " + p.id() + " editorial 컴파일 실패\n" + sol.error);
                continue;
            }
            for (Path f : Plan.caseFiles(p)) {
                JsonObject data = JsonParser.parseString(Files.readString(f, StandardCharsets.UTF_8)).getAsJsonObject();
                boolean has = data.has("expected") || data.has("expected_sha256");
                if (has && !(force && f.getFileName().toString().startsWith("large"))) continue;
                data.remove("expected");
                data.remove("expected_sha256");
                data.remove("expected_preview");
                var loaded = sol.load();
                Object[] args = Judge.convertArgs(loaded, data.getAsJsonArray("args"));
                Judge.Outcome out = judge.invoke(loaded, args, 0);
                JsonElement actual = Json.GSON.toJsonTree(out.value());
                String compact = Json.compact(actual);
                if (compact.length() <= INLINE_LIMIT) {
                    data.add("expected", actual);
                } else {
                    data.addProperty("expected_sha256", Compare.sha256(actual));
                    data.addProperty("expected_preview", Json.truncate(compact, 120));
                }
                Files.writeString(f, Json.formatCase(data), StandardCharsets.UTF_8);
                filled++;
                System.out.printf("✔ %s %s  (%.0f ms)%n", p.id(), f.getFileName(), out.timeMs());
            }
        }
        System.out.println("채운 케이스: " + filled + "개");
    }
}
