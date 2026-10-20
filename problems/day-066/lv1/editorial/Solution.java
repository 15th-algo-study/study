class Solution {
    private static final int MOD = 1_000_000_007;

    public int solution(int n, int[] drains) {
        boolean[] blocked = new boolean[n + 1];
        for (int d : drains) blocked[d] = true;

        long[] ways = new long[n + 1]; // ways[i] = 위치 i에서 블록이 딱 끝나도록 덮는 방법 수
        ways[0] = 1;
        for (int i = 1; i <= n; i++) {
            if (blocked[i]) continue; // 이음매 금지 → 0
            long sum = ways[i - 1];   // 마지막 블록 길이 1
            if (i >= 2) sum += ways[i - 2]; // 길이 2
            if (i >= 3) sum += ways[i - 3]; // 길이 3
            ways[i] = sum % MOD;
        }
        return (int) ways[n];
    }
}
