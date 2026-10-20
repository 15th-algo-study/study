class Solution {
    public long solution(int n, int[][] paths, int[] route) {
        final int INF = 1_000_000_000;
        int[][] dist = new int[n][n];
        for (int i = 0; i < n; i++) {
            java.util.Arrays.fill(dist[i], INF);
            dist[i][i] = 0;
        }
        for (int[] p : paths) dist[p[0]][p[1]] = Math.min(dist[p[0]][p[1]], p[2]); // 한 방향만
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                int ik = dist[i][k];
                if (ik == INF) continue;
                int[] rowI = dist[i], rowK = dist[k];
                for (int j = 0; j < n; j++) {
                    if (ik + rowK[j] < rowI[j]) rowI[j] = ik + rowK[j];
                }
            }
        }
        long total = 0;
        for (int s = 0; s + 1 < route.length; s++) {
            int d = dist[route[s]][route[s + 1]];
            if (d == INF) return -1; // 갈 수 없는 구간
            total += d;
        }
        return total;
    }
}
