import java.util.Arrays;
import java.util.PriorityQueue;

class Solution {
    public long[] solution(int n, int[] from, int[] to, int[] time, int start) {
        int m = from.length;
        // CSR: head[v] ~ head[v+1]-1 가 v 에서 나가는 간선 번호
        int[] head = new int[n + 1];
        for (int f : from) head[f + 1]++;
        for (int v = 0; v < n; v++) head[v + 1] += head[v];
        int[] adjTo = new int[m], adjW = new int[m], fill = Arrays.copyOf(head, n);
        for (int i = 0; i < m; i++) {
            int p = fill[from[i]]++;
            adjTo[p] = to[i];
            adjW[p] = time[i];
        }

        final long INF = Long.MAX_VALUE;
        long[] dist = new long[n];
        Arrays.fill(dist, INF);
        dist[start] = 0;
        PriorityQueue<long[]> pq = new PriorityQueue<>((a, b) -> Long.compare(a[0], b[0]));
        pq.add(new long[]{0, start});
        while (!pq.isEmpty()) {
            long[] cur = pq.poll();
            int v = (int) cur[1];
            if (cur[0] > dist[v]) continue; // 이미 더 짧게 확정된 정점 (오래된 항목)
            for (int p = head[v]; p < head[v + 1]; p++) {
                long nd = cur[0] + adjW[p];
                if (nd < dist[adjTo[p]]) {
                    dist[adjTo[p]] = nd;
                    pq.add(new long[]{nd, adjTo[p]});
                }
            }
        }
        for (int v = 0; v < n; v++) if (dist[v] == INF) dist[v] = -1;
        return dist;
    }
}
