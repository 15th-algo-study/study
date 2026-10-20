class Solution {
    public String[] solution(int n, int[][] paths, int[][] queries) {
        final long INF = Long.MAX_VALUE / 4;
        final long FLOOR = -1_000_000_000_000_000L; // 음수 사이클로 값이 폭주하지 않게 고정하는 하한
        long[][] dist = new long[n][n];
        for (int i = 0; i < n; i++) {
            java.util.Arrays.fill(dist[i], INF);
            dist[i][i] = 0;
        }
        for (int[] p : paths) dist[p[0]][p[1]] = Math.min(dist[p[0]][p[1]], p[2]);

        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                long ik = dist[i][k];
                if (ik == INF) continue;
                for (int j = 0; j < n; j++) {
                    if (dist[k][j] == INF) continue; // INF + 음수 가 INF 보다 작아 보이는 버그 방지
                    long via = Math.max(FLOOR, ik + dist[k][j]);
                    if (via < dist[i][j]) dist[i][j] = via;
                }
            }
        }

        // unbounded[i][j]: i → (음수 사이클 위의 k) → j 로 갈 수 있는가
        boolean[][] unbounded = new boolean[n][n];
        for (int k = 0; k < n; k++) {
            if (dist[k][k] >= 0) continue;
            for (int i = 0; i < n; i++) {
                if (dist[i][k] == INF) continue;
                for (int j = 0; j < n; j++) {
                    if (dist[k][j] != INF) unbounded[i][j] = true;
                }
            }
        }

        String[] answer = new String[queries.length];
        for (int q = 0; q < queries.length; q++) {
            int s = queries[q][0], e = queries[q][1];
            if (dist[s][e] == INF) answer[q] = "UNREACHABLE";
            else if (unbounded[s][e]) answer[q] = "UNBOUNDED";
            else answer[q] = Long.toString(dist[s][e]);
        }
        return answer;
    }
}
