package study.runner;

/** 제한 시간 안에 작업을 실행한다. 초과 시 {@link Exceeded} 를 던진다. (테스트에서는 assertTimeoutPreemptively) */
@FunctionalInterface
public interface TimeLimiter {
    Object call(long limitMs, Task task) throws Throwable;

    @FunctionalInterface
    interface Task {
        Object run() throws Throwable;
    }

    final class Exceeded extends RuntimeException {
        public Exceeded() {
            super("시간 초과", null, false, false);
        }
    }

    /** 시간 제한 없이 실행 (FillExpected 용). */
    TimeLimiter NONE = (limitMs, task) -> task.run();
}
