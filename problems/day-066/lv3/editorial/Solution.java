class Solution {
    private static final int MOD = 1_000_000_007;

    public int solution(int[] low, int[] high) {
        int n = low.length;
        long[] diff = new long[n + 1]; // 차분 배열: 누적하면 각 트램펄린에 도착하는 경로 수
        long arrive = 0;
        for (int i = 0; i < n; i++) {
            arrive = (arrive + diff[i]) % MOD;
            long ways = (i == 0) ? 1 : arrive; // i번까지 오는 경로 수 확정
            if (i == n - 1) return (int) ways;
            int from = i + low[i];
            if (ways == 0 || from > n - 1) continue;
            int to = Math.min(i + high[i], n - 1);
            diff[from] = (diff[from] + ways) % MOD;           // [from, to] 구간에 ways 더하기
            diff[to + 1] = (diff[to + 1] - ways + MOD) % MOD;
        }
        return 0; // n ≥ 2 이므로 위에서 끝난다
    }
}
