import java.util.Arrays;
import java.util.PriorityQueue;

class Solution {
    public long[] solution(int n, int[][] roads, int[] stations, int[] houses) {
        // 양방향 도로 → 간선 2개씩 CSR 로
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
        Arrays.fill(dist, INF);
        PriorityQueue<long[]> pq = new PriorityQueue<>((a, b) -> Long.compare(a[0], b[0]));
        for (int s : stations) { // 모든 소방서를 거리 0 으로 한꺼번에 시작
            dist[s] = 0;
            pq.add(new long[]{0, s});
        }
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

        long[] answer = new long[houses.length];
        for (int i = 0; i < houses.length; i++) {
            long d = dist[houses[i]];
            answer[i] = d == INF ? -1 : d;
        }
        return answer;
    }
}
