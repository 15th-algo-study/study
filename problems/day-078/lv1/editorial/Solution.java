import java.util.Arrays;

class Solution {
    public long[] solution(int[][] fare) {
        int n = fare.length;
        final long INF = Long.MAX_VALUE;
        long[] dist = new long[n];
        boolean[] done = new boolean[n];
        Arrays.fill(dist, INF);
        dist[0] = 0;
        for (int iter = 0; iter < n; iter++) {
            int v = -1;
            for (int u = 0; u < n; u++) { // 확정되지 않은 정점 중 가장 가까운 정점
                if (!done[u] && (v == -1 || dist[u] < dist[v])) v = u;
            }
            if (dist[v] == INF) break;      // 남은 정점은 모두 도달 불가
            done[v] = true;
            int[] row = fare[v];
            for (int u = 0; u < n; u++) {
                if (row[u] > 0 && !done[u] && dist[v] + row[u] < dist[u]) {
                    dist[u] = dist[v] + row[u]; // 0 은 직항 없음
                }
            }
        }
        for (int u = 0; u < n; u++) if (dist[u] == INF) dist[u] = -1;
        return dist;
    }
}
