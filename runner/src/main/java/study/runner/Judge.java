package study.runner;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import study.runner.CaseResult.Status;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/** 케이스 하나를 채점한다. */
public final class Judge {
    /** basic 케이스는 시간 제한이 없지만 무한 루프 대비 안전장치. */
    public static final long BASIC_SAFETY_MS = 10_000;
    private static final int SHOW = 300;

    private final TimeLimiter limiter;
    private final double timeScale;
    private final boolean captureOutput;
    private final Map<Path, CompiledSolution> compiled = new HashMap<>();

    public Judge(TimeLimiter limiter) {
        this(limiter, 1.0, false);
    }

    /** @param captureOutput 케이스마다 풀이의 콘솔 출력을 모아 결과에 넣는다 (실행 = basic 만일 때) */
    public Judge(TimeLimiter limiter, double timeScale, boolean captureOutput) {
        this.limiter = limiter;
        this.timeScale = timeScale;
        this.captureOutput = captureOutput;
    }

    public CompiledSolution compiled(Path source) {
        return compiled.computeIfAbsent(source, CompiledSolution::compile);
    }

    public CaseResult run(Plan.Item item) {
        return switch (item.kind()) {
            case ERROR -> new CaseResult(item.name(), "error", Status.ERROR, 0, null, null, null, null, null, item.message());
            case TEMPLATE -> template(item);
            case CASE -> captureOutput ? judgeCaseWithOutput(item) : judgeCase(item);
        };
    }

    private CaseResult judgeCaseWithOutput(Plan.Item item) {
        compiled(item.solution());   // 컴파일러 메시지가 출력에 섞이지 않게 먼저 컴파일해 둔다 (결과는 캐시)
        ConsoleCapture.Buffer out = ConsoleCapture.begin();
        CaseResult r;
        try {
            r = judgeCase(item);
        } finally {
            ConsoleCapture.end();
        }
        return r.withOutput(out.text());
    }

    /** 실행 결과 (FillExpected 도 사용). */
    public record Outcome(Object value, double timeMs) {}

    public Outcome invoke(CompiledSolution.Loaded loaded, Object[] args, long limitMs) throws Throwable {
        Object inst = loaded.newInstance();
        Method m = loaded.method();
        Object[] box = new Object[2];
        limiter.call(limitMs, () -> {
            long t0 = System.nanoTime();
            box[0] = m.invoke(inst, args);
            box[1] = (System.nanoTime() - t0) / 1e6;
            return null;
        });
        return new Outcome(box[0], (double) box[1]);
    }

    public static Object[] convertArgs(CompiledSolution.Loaded loaded, JsonArray args) {
        Type[] types = loaded.method().getGenericParameterTypes();
        if (args.size() != types.length) {
            throw new IllegalArgumentException("케이스 인자 " + args.size() + "개, solution 파라미터 " + types.length + "개로 개수가 다릅니다.");
        }
        Object[] out = new Object[types.length];
        for (int i = 0; i < types.length; i++) {
            try {
                out[i] = Args.convert(args.get(i), types[i]);
            } catch (RuntimeException e) {
                throw new IllegalArgumentException(loaded.paramName(i) + " 를 " + types[i].getTypeName() + " 로 변환할 수 없습니다: " + e.getMessage(), e);
            }
        }
        return out;
    }

    private CaseResult judgeCase(Plan.Item item) {
        String kind = item.large() ? "large" : "basic";
        Long limit = item.large() ? Math.round(item.problem().timeLimitMs() * timeScale) : null;
        String note = item.data().has("note") ? item.data().get("note").getAsString() : null;
        JsonArray args = item.data().getAsJsonArray("args");
        JsonElement expected = item.data().get("expected");
        String expectedHash = item.data().has("expected_sha256") ? item.data().get("expected_sha256").getAsString() : null;
        String expectedShow = expected != null ? Json.truncate(Json.compact(expected), SHOW)
                : item.data().has("expected_preview") ? item.data().get("expected_preview").getAsString() + " (sha256 비교)" : "(없음)";

        CompiledSolution sol = compiled(item.solution());
        if (!sol.ok()) {
            return new CaseResult(item.name(), kind, Status.COMPILE_ERROR, 0, limit, note, describe(null, args), expectedShow, null, sol.error);
        }
        CompiledSolution.Loaded loaded;
        Object[] values;
        try {
            loaded = sol.load();
        } catch (Throwable e) {
            return new CaseResult(item.name(), kind, Status.ERROR, 0, limit, note, describe(null, args), expectedShow, null,
                    "Solution 을 불러올 수 없습니다: " + e.getMessage());
        }
        String input = describe(loaded, args);
        if (expected == null && expectedHash == null) {
            return new CaseResult(item.name(), kind, Status.ERROR, 0, limit, note, input, expectedShow, null,
                    "케이스에 expected 가 없습니다. (출제자: ./gradlew fillExpected)");
        }
        try {
            values = convertArgs(loaded, args);
        } catch (RuntimeException e) {
            return new CaseResult(item.name(), kind, Status.ERROR, 0, limit, note, input, expectedShow, null, e.getMessage());
        }

        Outcome out;
        try {
            out = invoke(loaded, values, limit != null ? limit : BASIC_SAFETY_MS);
        } catch (TimeLimiter.Exceeded e) {
            long shown = limit != null ? limit : BASIC_SAFETY_MS;
            return new CaseResult(item.name(), kind, Status.TLE, shown, limit, note, input, expectedShow, null,
                    "제한 시간 " + shown + " ms 를 넘겼습니다.");
        } catch (InvocationTargetException e) {
            return new CaseResult(item.name(), kind, Status.RUNTIME_ERROR, 0, limit, note, input, expectedShow, null,
                    describeThrowable(e.getCause(), loaded.cls().getName()));
        } catch (Throwable e) {
            return new CaseResult(item.name(), kind, Status.ERROR, 0, limit, note, input, expectedShow, null, e.toString());
        }

        JsonElement actual = Json.GSON.toJsonTree(out.value());
        String actualShow = Json.truncate(Json.compact(actual), SHOW);
        boolean ok = expected != null ? Compare.equal(expected, actual) : Compare.sha256(actual).equals(expectedHash);
        return new CaseResult(item.name(), kind, ok ? Status.PASS : Status.WRONG, out.timeMs(), limit, note, input,
                expectedShow, actualShow, ok ? null : "기대값과 다릅니다.");
    }

    /** 템플릿(problems/.../Solution.java)이 컴파일되고 editorial 과 시그니처가 같은지 확인. */
    private CaseResult template(Plan.Item item) {
        CompiledSolution tpl = compiled(item.problem().template());
        CompiledSolution ed = compiled(item.solution());
        String msg;
        if (!tpl.ok()) msg = "템플릿 컴파일 실패:\n" + tpl.error;
        else if (!ed.ok()) msg = "editorial 컴파일 실패 (케이스 결과 참고)";
        else {
            try {
                Method a = tpl.load().method(), b = ed.load().method();
                boolean same = a.getGenericReturnType().equals(b.getGenericReturnType())
                        && Arrays.equals(a.getGenericParameterTypes(), b.getGenericParameterTypes());
                msg = same ? null : "템플릿과 editorial 의 solution 시그니처가 다릅니다:\n  템플릿  : " + a.toGenericString() + "\n  editorial: " + b.toGenericString();
            } catch (Throwable e) {
                msg = "시그니처 확인 실패: " + e.getMessage();
            }
        }
        return new CaseResult("template", "template", msg == null ? Status.PASS : Status.ERROR, 0, null,
                "템플릿 시그니처", null, null, null, msg);
    }

    private static String describe(CompiledSolution.Loaded loaded, JsonArray args) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(loaded != null && i < loaded.method().getParameterCount() ? loaded.paramName(i) : "#" + (i + 1)).append('=');
            JsonElement a = args.get(i);
            if (Json.isGenerator(a)) {
                var copy = a.getAsJsonObject().deepCopy();
                String gen = copy.remove("$gen").getAsString();
                sb.append("⟨생성:").append(gen).append(' ').append(Json.compact(copy)).append('⟩');
            } else {
                sb.append(Json.truncate(Json.compact(a), SHOW));
            }
        }
        return sb.toString();
    }

    static String describeThrowable(Throwable t, String className) {
        String head = switch (t) {
            case StackOverflowError e -> "StackOverflowError — 재귀가 너무 깊습니다.";
            case OutOfMemoryError e -> "OutOfMemoryError — 메모리 초과.";
            default -> t.toString();
        };
        for (StackTraceElement el : t.getStackTrace()) {
            if (el.getClassName().equals(className) || el.getClassName().startsWith(className + "$")) {
                return head + " (Solution.java:" + el.getLineNumber() + ", " + el.getMethodName() + ")";
            }
        }
        return head;
    }
}
