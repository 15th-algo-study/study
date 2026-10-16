package study.runner;

/** SplitMix64. JDK 버전과 무관하게 같은 시드에서 같은 수열을 낸다 (large 케이스 재현성). */
final class Rng {
    private long state;

    Rng(long seed) {
        this.state = seed;
    }

    long next() {
        long z = (state += 0x9E3779B97F4A7C15L);
        z = (z ^ (z >>> 30)) * 0xBF58476D1CE4E5B9L;
        z = (z ^ (z >>> 27)) * 0x94D049BB133111EBL;
        return z ^ (z >>> 31);
    }

    /** [lo, hi] 균등(근사) 난수. */
    long range(long lo, long hi) {
        if (lo > hi) throw new IllegalArgumentException("범위 오류: " + lo + " > " + hi);
        long span = hi - lo + 1;
        if (span <= 0) return next();
        return lo + Math.floorMod(next(), span);
    }

    int index(int n) {
        return (int) range(0, n - 1);
    }
}
