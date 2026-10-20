import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

class Solution {
    private static final long INF = Long.MAX_VALUE;

    public int[] solution(int n, int[][] roads) {
        int[] head = new int[n + 1];
        for (int[] r : roads) {
            head[r[0] + 1]++;
            head[r[1] + 1]++;
        }
        for (int v = 0; v < n; v++) head[v + 1] += head[v];
        int[] to = new int[2 * roads.length], w = new int[2 * roads.length];
        int[] fill = Arrays.copyOf(head, n);
        for (int[] r : roads) {
            int p = fill[r[0]]++;
            to[p] = r[1];
            w[p] = r[2];
            p = fill[r[1]]++;
            to[p] = r[0];
            w[p] = r[2];
        }
        long[] d1 = dijkstra(n, head, to, w, 0);
        long[] d2 = dijkstra(n, head, to, w, n - 1);
        if (d1[n - 1] == INF) return new int[]{};
        long total = d1[n - 1];

        // 1. 최단 경로 위 정점의 d1 값을 좌표 압축
        boolean[] on = new boolean[n];
        long[] values = new long[n];
        int k = 0;
        for (int v = 0; v < n; v++) {
            if (d1[v] != INF && d2[v] != INF && d1[v] + d2[v] == total) {
                on[v] = true;
                values[k++] = d1[v];
            }
        }
        values = Arrays.copyOf(values, k);
        Arrays.sort(values);
        int distinct = 0;
        for (int i = 0; i < k; i++) if (i == 0 || values[i] != values[i - 1]) values[distinct++] = values[i];
        values = Arrays.copyOf(values, distinct);

        // 2. 같은 시각의 최단 경로 정점 수
        int[] cnt = new int[distinct];
        for (int v = 0; v < n; v++) if (on[v]) cnt[Arrays.binarySearch(values, d1[v])]++;

        // 3. 시각을 건너뛰는 최단 경로 간선: 번호 구간 (iu, ix) 의 안쪽을 차분 배열로 +1
        int[] cover = new int[distinct + 1];
        for (int[] r : roads) {
            for (int dir = 0; dir < 2; dir++) {
                int u = dir == 0 ? r[0] : r[1], x = dir == 0 ? r[1] : r[0];
                if (!on[u] || !on[x] || d1[u] + r[2] + d2[x] != total) continue;
                int iu = Arrays.binarySearch(values, d1[u]), ix = Arrays.binarySearch(values, d1[x]);
                if (ix - iu >= 2) {
                    cover[iu + 1]++;
                    cover[ix]--;
                }
            }
        }
        for (int i = 1; i <= distinct; i++) cover[i] += cover[i - 1];

        // 4. 그 시각의 유일한 정점이고 건너뛰는 간선도 없으면 반드시 지난다
        List<Integer> result = new ArrayList<>();
        for (int v = 1; v < n - 1; v++) {
            if (!on[v]) continue;
            int idx = Arrays.binarySearch(values, d1[v]);
            if (cnt[idx] == 1 && cover[idx] == 0) result.add(v);
        }
        return result.stream().mapToInt(Integer::intValue).toArray();
    }

    private long[] dijkstra(int n, int[] head, int[] to, int[] w, int src) {
        long[] dist = new long[n];
        Arrays.fill(dist, INF);
        dist[src] = 0;
        PriorityQueue<long[]> pq = new PriorityQueue<>((a, b) -> Long.compare(a[0], b[0]));
        pq.add(new long[]{0, src});
        while (!pq.isEmpty()) {
            long[] cur = pq.poll();
            int v = (int) cur[1];
            if (cur[0] > dist[v]) continue;
            for (int p = head[v]; p < head[v + 1]; p++) {
                long nd = cur[0] + w[p];
                if (nd < dist[to[p]]) {
                    dist[to[p]] = nd;
                    pq.add(new long[]{nd, to[p]});
                }
            }
        }
        return dist;
    }
}
