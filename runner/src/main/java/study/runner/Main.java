package study.runner;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Gradle·JUnit 없이 바로 채점하는 진입점 (앱이 사용). 채점 내용은 ProblemTest 와 같다:
 * 같은 Plan·Judge·Report 를 쓰고, 시간 제한도 JUnit assertTimeoutPreemptively 와 같은 방식으로 건다.
 *
 * java -Xmx1g -Xss64m -XX:+UseParallelGC -Drunner.root=.. -Drunner.user=.. -Drunner.day=.. ... -cp 'runner-lib/*' study.runner.Main
 * (시스템 프로퍼티는 Gradle 의 -P 값과 같은 runner.* 이름. CI 는 계속 ./gradlew test 를 쓴다)
 */
public final class Main {
    private static final AtomicInteger THREADS = new AtomicInteger();

    private Main() {}

    public static void main(String[] args) throws Exception {
        RunConfig cfg = RunConfig.fromSystemProperties();
        Report report = new Report(cfg);
        Judge judge = new Judge(Main::preemptive, cfg.timeScale());
        for (Plan.Item item : Plan.build(cfg)) report.add(item, judge.run(item));
        report.write();
        System.out.print(Files.readString(cfg.outDir().resolve("report.txt"), StandardCharsets.UTF_8));
        System.out.flush();
        // 시간 초과로 멈추지 않은 풀이 스레드가 남아 있어도 바로 끝낸다 (Gradle 테스트 워커가 끝날 때와 같음)
        System.exit(report.passed() ? 0 : 1);
    }

    /**
     * JUnit 의 assertTimeoutPreemptively 와 같은 동작:
     * 새 데몬 스레드 하나짜리 실행기에서 작업을 돌리고, 제한 시간까지 기다렸다가 넘으면 인터럽트하고 시간 초과로 본다.
     * 작업이 던진 예외(InvocationTargetException 등)는 그대로 다시 던진다.
     */
    static Object preemptive(long limitMs, TimeLimiter.Task task) throws Throwable {
        ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "runner-timeout-thread-" + THREADS.incrementAndGet());
            t.setDaemon(true);
            return t;
        });
        Callable<Object> callable = () -> {
            try {
                return task.run();
            } catch (Exception e) {
                throw e;
            } catch (Throwable t) {
                throw new WrappedError(t);
            }
        };
        try {
            Future<Object> future = executor.submit(callable);
            try {
                return future.get(limitMs, TimeUnit.MILLISECONDS);
            } catch (TimeoutException e) {
                future.cancel(true);
                throw new TimeLimiter.Exceeded();
            } catch (ExecutionException e) {
                Throwable cause = e.getCause();
                throw cause instanceof WrappedError w ? w.getCause() : cause;
            }
        } finally {
            executor.shutdownNow();
        }
    }

    private static final class WrappedError extends Exception {
        WrappedError(Throwable cause) {
            super(cause);
        }
    }
}
