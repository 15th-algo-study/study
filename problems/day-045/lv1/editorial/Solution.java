class Solution {
    public long[] solution(long[] photos) {
        long[] answer = new long[photos.length];
        for (int i = 0; i < photos.length; i++) {
            answer[i] = minSide(photos[i]);
        }
        return answer;
    }

    /** s*s >= p 인 가장 작은 s. mid <= 10^9 이므로 mid*mid 는 long 범위 안 */
    private long minSide(long p) {
        long lo = 1, hi = 1_000_000_000L;
        while (lo < hi) {
            long mid = (lo + hi) / 2;
            if (mid * mid >= p) hi = mid;
            else lo = mid + 1;
        }
        return lo;
    }
}
