import java.util.Arrays;

class Solution {
    private static final int MOD = 1_000_000_007;

    public int solution(int n, int k, String[] banned) {
        boolean[][] bad = new boolean[k][k]; // bad[x][y]: x 바로 다음 y 금지
        for (String b : banned) bad[b.charAt(0) - 'a'][b.charAt(1) - 'a'] = true;

        long[] end = new long[k]; // end[c] = 마지막 색이 c인 순서의 수
        Arrays.fill(end, 1);      // 1발째
        for (int shot = 2; shot <= n; shot++) {
            long[] next = new long[k];
            for (int y = 0; y < k; y++) {
                long sum = 0;
                for (int x = 0; x < k; x++) if (!bad[x][y]) sum += end[x];
                next[y] = sum % MOD;
            }
            end = next;
        }
        long total = 0;
        for (long v : end) total += v;
        return (int) (total % MOD);
    }
}
