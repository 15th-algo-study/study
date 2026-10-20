class Solution {
    public double solution(String board, int k) {
        int n = board.length() - 1;

        int[] stop = new int[n + 1]; // stop[i] = i번 칸에 도착했을 때 최종적으로 멈추는 칸 (-1 = 짐)
        for (int i = n; i >= 0; i--) {
            char ch = board.charAt(i);
            if (ch == '.') stop[i] = i;
            else if (ch == 'X') stop[i] = -1;
            else {
                int j = i + (ch - '0');
                stop[i] = (j > n) ? -1 : stop[j]; // 뒤쪽은 이미 구해 둠
            }
        }

        double[] prob = new double[n + 1]; // prob[i] = i번 칸에 멈춰 설 확률
        prob[0] = 1.0;
        for (int i = 0; i < n; i++) {
            if (prob[i] == 0) continue;
            double share = prob[i] / k;
            for (int d = 1; d <= k && i + d <= n; d++) {
                int s = stop[i + d];
                if (s >= 0) prob[s] += share;
            }
        }
        return prob[n];
    }
}
