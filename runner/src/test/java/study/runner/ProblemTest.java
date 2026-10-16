package study.runner;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.opentest4j.AssertionFailedError;

import java.time.Duration;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * cases/*.json 하나가 테스트 하나.
 * ./gradlew test -Puser=<login|editorial> -Pday=<NNN> -Plevel=<n> [-Pcases=basic] [-Psource=파일] [-Pjson=결과경로]
 */
class ProblemTest {
    static final RunConfig CONFIG = RunConfig.fromSystemProperties();
    static final Report REPORT = new Report(CONFIG);

    /** large 케이스 시간 제한은 assertTimeoutPreemptively 로 강제한다. */
    static final Judge JUDGE = new Judge((limitMs, task) -> {
        try {
            return assertTimeoutPreemptively(Duration.ofMillis(limitMs), task::run, () -> "시간 초과");
        } catch (AssertionFailedError e) {
            throw new TimeLimiter.Exceeded();
        }
    }, CONFIG.timeScale());

    static Stream<Arguments> cases() {
        return Plan.build(CONFIG).stream().map(item -> Arguments.of(Named.of(item.displayName(), item)));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("cases")
    void judge(Plan.Item item) {
        CaseResult r = JUDGE.run(item);
        REPORT.add(item, r);
        if (!r.passed()) fail(r.status().label + (r.message() == null ? "" : ": " + r.message()));
    }

    @AfterAll
    static void writeReport() throws Exception {
        REPORT.write();
    }
}
