import java.util.Arrays;

class Solution {
    private static final int MOD = 1_000_000_007;
    private int[] memo;

    public int solution(int n) {
        memo = new int[n + 1];
        Arrays.fill(memo, -1); // -1 = 아직 계산하지 않음
        return calc(n);
    }

    private int calc(int k) {
        if (k <= 1) return 1;
        if (memo[k] != -1) return memo[k]; // 적어 둔 값을 재사용
        return memo[k] = (calc(k - 1) + calc(k / 2)) % MOD;
    }
}
