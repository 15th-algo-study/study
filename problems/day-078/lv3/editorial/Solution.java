import java.util.Arrays;
import java.util.PriorityQueue;

class Solution {
    public long solution(int n, int[][] roads, int[][] helipads) {
        int sky = n; // 가상의 하늘 정점: 모든 헬기장이 여기를 거쳐 이어진다
        int nodes = n + 1;
        int edges = 2 * roads.length + 2 * helipads.length;
        int[] src = new int[edges], dst = new int[edges], cost = new int[edges];
        int e = 0;
        for (int[] r : roads) {
            src[e] = r[0]; dst[e] = r[1]; cost[e++] = r[2];
            src[e] = r[1]; dst[e] = r[0]; cost[e++] = r[2];
        }
        for (int[] h : helipads) {
            src[e] = h[0]; dst[e] = sky; cost[e++] = h[1]; // 이륙: 마을 → 하늘
            src[e] = sky; dst[e] = h[0]; cost[e++] = h[2]; // 착륙: 하늘 → 마을
        }
        int[] head = new int[nodes + 1];
        for (int i = 0; i < edges; i++) head[src[i] + 1]++;
        for (int v = 0; v < nodes; v++) head[v + 1] += head[v];
        int[] to = new int[edges], w = new int[edges], fill = Arrays.copyOf(head, nodes);
        for (int i = 0; i < edges; i++) {
            int p = fill[src[i]]++;
            to[p] = dst[i];
            w[p] = cost[i];
        }

        final long INF = Long.MAX_VALUE;
        long[] dist = new long[nodes];
        Arrays.fill(dist, INF);
        dist[0] = 0;
        PriorityQueue<long[]> pq = new PriorityQueue<>((a, b) -> Long.compare(a[0], b[0]));
        pq.add(new long[]{0, 0});
        while (!pq.isEmpty()) {
            long[] cur = pq.poll();
            int v = (int) cur[1];
            if (cur[0] > dist[v]) continue;
            if (v == n - 1) break; // 목적지가 확정되면 끝
            for (int p = head[v]; p < head[v + 1]; p++) {
                long nd = cur[0] + w[p];
                if (nd < dist[to[p]]) {
                    dist[to[p]] = nd;
                    pq.add(new long[]{nd, to[p]});
                }
            }
        }
        return dist[n - 1] == INF ? -1 : dist[n - 1];
    }
}
