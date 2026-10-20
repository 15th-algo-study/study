import java.util.Arrays;
import java.util.PriorityQueue;

class Solution {
    public long solution(int n, int[][] ferries) {
        int m = ferries.length;
        int[] head = new int[n + 1];
        for (int[] f : ferries) head[f[0] + 1]++;
        for (int v = 0; v < n; v++) head[v + 1] += head[v];
        int[] order = new int[m], fill = Arrays.copyOf(head, n); // order[p] = 노선 번호
        for (int i = 0; i < m; i++) order[fill[ferries[i][0]]++] = i;

        final long INF = Long.MAX_VALUE;
        long[] arrive = new long[n];
        Arrays.fill(arrive, INF);
        arrive[0] = 0;
        PriorityQueue<long[]> pq = new PriorityQueue<>((a, b) -> Long.compare(a[0], b[0]));
        pq.add(new long[]{0, 0});
        while (!pq.isEmpty()) {
            long[] cur = pq.poll();
            int v = (int) cur[1];
            long t = cur[0];
            if (t > arrive[v]) continue;
            if (v == n - 1) return t;
            for (int p = head[v]; p < head[v + 1]; p++) {
                int[] f = ferries[order[p]];
                long offset = f[2], interval = f[3];
                long dep = t <= offset ? offset
                        : offset + (t - offset + interval - 1) / interval * interval; // 다음 출발 시각
                long at = dep + f[4];
                if (at < arrive[f[1]]) {
                    arrive[f[1]] = at;
                    pq.add(new long[]{at, f[1]});
                }
            }
        }
        return -1;
    }
}
