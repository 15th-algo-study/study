class Solution {
    private static final int MOD = 1_000_000_007;

    public int solution(int[] prices, int k, int budget) {
        int[][] ways = new int[k + 1][budget + 1]; // ways[c][s] = c개를 골라 합이 s 인 방법 수
        ways[0][0] = 1;
        int seen = 0;
        for (int p : prices) {
            seen++;
            for (int c = Math.min(seen, k); c >= 1; c--) { // 큰 쪽부터: 같은 상품을 두 번 고르지 않게
                int[] to = ways[c], from = ways[c - 1];
                for (int s = budget; s >= p; s--) {
                    int v = to[s] + from[s - p];
                    to[s] = v >= MOD ? v - MOD : v;
                }
            }
        }
        return ways[k][budget];
    }
}
