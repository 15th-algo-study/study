class Solution {
    public int solution(int n, int[][] roads) {
        final int INF = 1_000_000_000;
        int[][] dist = new int[n + 1][n + 1];
        for (int i = 1; i <= n; i++) {
            java.util.Arrays.fill(dist[i], INF);
            dist[i][i] = 0;
        }
        for (int[] r : roads) { // 같은 쌍의 도로가 여러 개면 가장 짧은 것만
            dist[r[0]][r[1]] = Math.min(dist[r[0]][r[1]], r[2]);
            dist[r[1]][r[0]] = Math.min(dist[r[1]][r[0]], r[2]);
        }
        for (int k = 1; k <= n; k++) {          // 경유지가 가장 바깥
            for (int i = 1; i <= n; i++) {
                int ik = dist[i][k];
                if (ik == INF) continue;
                for (int j = 1; j <= n; j++) {
                    if (ik + dist[k][j] < dist[i][j]) dist[i][j] = ik + dist[k][j];
                }
            }
        }
        int best = 1, bestWorst = Integer.MAX_VALUE;
        for (int v = 1; v <= n; v++) {
            int worst = 0;
            for (int u = 1; u <= n; u++) worst = Math.max(worst, dist[v][u]);
            if (worst < bestWorst) { // 더 작을 때만 바꿔 동률이면 작은 번호 유지
                bestWorst = worst;
                best = v;
            }
        }
        return best;
    }
}
