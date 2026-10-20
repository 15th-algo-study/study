class Solution {
    public long solution(int[] values, int[] rates) {
        int n = values.length;
        long[][] best = new long[n][n]; // best[l][r] = 물건 l..r 이 남았을 때 앞으로 얻을 최대 금액
        for (int len = 1; len <= n; len++) {
            int d = n - len; // 이미 판 물건 수 = 오늘의 시세 인덱스
            for (int l = 0; l + len - 1 < n; l++) {
                int r = l + len - 1;
                if (len == 1) {
                    best[l][r] = (long) values[l] * rates[d];
                    continue;
                }
                long sellLeft = (long) values[l] * rates[d] + best[l + 1][r];
                long sellRight = (long) values[r] * rates[d] + best[l][r - 1];
                best[l][r] = Math.max(sellLeft, sellRight);
            }
        }
        return best[0][n - 1];
    }
}
