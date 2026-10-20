import java.util.Arrays;
import java.util.PriorityQueue;

class Solution {
    private static final long MOD = 1_000_000_007L;

    public int solution(int n, int[][] roads) {
        int[] head = new int[n + 2];
        for (int[] r : roads) {
            head[r[0] + 1]++;
            head[r[1] + 1]++;
        }
        for (int v = 0; v <= n; v++) head[v + 1] += head[v];
        int[] to = new int[2 * roads.length], w = new int[2 * roads.length];
        int[] fill = Arrays.copyOf(head, n + 1);
        for (int[] r : roads) {
            int p = fill[r[0]]++;
            to[p] = r[1];
            w[p] = r[2];
            p = fill[r[1]]++;
            to[p] = r[0];
            w[p] = r[2];
        }

        final long INF = Long.MAX_VALUE;
        long[] dist = new long[n + 1];
        long[] ways = new long[n + 1];
        Arrays.fill(dist, INF);
        dist[1] = 0;
        ways[1] = 1;
        PriorityQueue<long[]> pq = new PriorityQueue<>((a, b) -> Long.compare(a[0], b[0]));
        pq.add(new long[]{0, 1});
        while (!pq.isEmpty()) {
            long[] cur = pq.poll();
            int v = (int) cur[1];
            if (cur[0] > dist[v]) continue; // 오래된 항목: 건너뛰지 않으면 ways 를 두 번 넘긴다
            for (int p = head[v]; p < head[v + 1]; p++) {
                int u = to[p];
                long nd = dist[v] + w[p];
                if (nd < dist[u]) {          // 더 짧은 길 → 경로 수를 새로 시작
                    dist[u] = nd;
                    ways[u] = ways[v];
                    pq.add(new long[]{nd, u});
                } else if (nd == dist[u]) {  // 같은 길이의 다른 길 → 누적
                    ways[u] = (ways[u] + ways[v]) % MOD;
                }
            }
        }
        return (int) ways[n];
    }
}
