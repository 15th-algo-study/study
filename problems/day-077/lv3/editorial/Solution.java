import java.util.Arrays;
import java.util.PriorityQueue;

class Solution {
    public long solution(int n, int[][] highways, int k) {
        int[] head = new int[n + 1];
        for (int[] h : highways) {
            head[h[0] + 1]++;
            head[h[1] + 1]++;
        }
        for (int v = 0; v < n; v++) head[v + 1] += head[v];
        int[] to = new int[2 * highways.length], toll = new int[2 * highways.length];
        int[] fill = Arrays.copyOf(head, n);
        for (int[] h : highways) {
            int p = fill[h[0]]++;
            to[p] = h[1];
            toll[p] = h[2];
            p = fill[h[1]]++;
            to[p] = h[0];
            toll[p] = h[2];
        }

        // 상태 = used * n + city (used: 지금까지 쓴 통행권 수)
        final long INF = Long.MAX_VALUE;
        long[] dist = new long[n * (k + 1)];
        Arrays.fill(dist, INF);
        dist[0] = 0;
        PriorityQueue<long[]> pq = new PriorityQueue<>((a, b) -> Long.compare(a[0], b[0]));
        pq.add(new long[]{0, 0});
        while (!pq.isEmpty()) {
            long[] cur = pq.poll();
            int state = (int) cur[1];
            if (cur[0] > dist[state]) continue;
            int used = state / n, v = state % n;
            for (int p = head[v]; p < head[v + 1]; p++) {
                int u = to[p];
                long full = cur[0] + toll[p];                 // 통행권 없이
                int s1 = used * n + u;
                if (full < dist[s1]) {
                    dist[s1] = full;
                    pq.add(new long[]{full, s1});
                }
                if (used < k) {                               // 통행권 한 장 사용
                    long half = cur[0] + toll[p] / 2;
                    int s2 = (used + 1) * n + u;
                    if (half < dist[s2]) {
                        dist[s2] = half;
                        pq.add(new long[]{half, s2});
                    }
                }
            }
        }
        long best = INF;
        for (int used = 0; used <= k; used++) best = Math.min(best, dist[used * n + n - 1]);
        return best == INF ? -1 : best;
    }
}
