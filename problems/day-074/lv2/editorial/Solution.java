class Solution {
    public long solution(int[] weights) {
        int n = weights.length;
        long[] prefix = new long[n + 1];
        for (int i = 0; i < n; i++) prefix[i + 1] = prefix[i] + weights[i];

        long[][] cost = new long[n][n]; // cost[l][r] = 장식 l..r 로 만든 모빌의 최소 흔들림 합 (길이 1 은 0)
        for (int len = 2; len <= n; len++) {
            for (int l = 0; l + len - 1 < n; l++) {
                int r = l + len - 1;
                long best = Long.MAX_VALUE;
                for (int k = l; k < r; k++) { // 맨 위 막대가 l..k 와 k+1..r 을 잇는다
                    long left = prefix[k + 1] - prefix[l];
                    long right = prefix[r + 1] - prefix[k + 1];
                    long total = cost[l][k] + cost[k + 1][r] + Math.abs(left - right);
                    if (total < best) best = total;
                }
                cost[l][r] = best;
            }
        }
        return cost[0][n - 1];
    }
}
