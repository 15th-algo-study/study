import java.util.Arrays;
import java.util.PriorityQueue;

class Solution {
    private static final long INF = Long.MAX_VALUE;

    public long solution(int n, int[][] roads, int farmA, int farmB, int market) {
        int[][] forward = csr(n, roads, false);
        int[][] backward = csr(n, roads, true); // 모든 도로를 뒤집은 그래프
        long[] dA = dijkstra(n, forward, farmA);
        long[] dB = dijkstra(n, forward, farmB);
        long[] dM = dijkstra(n, backward, market); // dM[x] = 원래 그래프의 x → market 거리

        long best = INF;
        for (int x = 0; x < n; x++) {
            if (dA[x] == INF || dB[x] == INF || dM[x] == INF) continue;
            best = Math.min(best, dA[x] + dB[x] + dM[x]);
        }
        return best == INF ? -1 : best;
    }

    /** {head, to, weight} 형태의 압축 인접 배열. reverse 면 a ← b 방향으로 넣는다. */
    private int[][] csr(int n, int[][] roads, boolean reverse) {
        int m = roads.length;
        int[] head = new int[n + 1];
        for (int[] r : roads) head[(reverse ? r[1] : r[0]) + 1]++;
        for (int v = 0; v < n; v++) head[v + 1] += head[v];
        int[] to = new int[m], w = new int[m], fill = Arrays.copyOf(head, n);
        for (int[] r : roads) {
            int s = reverse ? r[1] : r[0], e = reverse ? r[0] : r[1];
            int p = fill[s]++;
            to[p] = e;
            w[p] = r[2];
        }
        return new int[][]{head, to, w};
    }

    private long[] dijkstra(int n, int[][] g, int src) {
        int[] head = g[0], to = g[1], w = g[2];
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
