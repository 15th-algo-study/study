import java.util.ArrayList;
import java.util.List;

class Solution {
    public int solution(int n, int[][] contacts, int start, int k) {
        List<Integer>[] adj = new List[n];
        for (int i = 0; i < n; i++) adj[i] = new ArrayList<>();
        for (int[] c : contacts) {
            adj[c[0]].add(c[1]);
            adj[c[1]].add(c[0]);
        }

        int[] dist = new int[n];
        java.util.Arrays.fill(dist, -1);
        int[] queue = new int[n];
        int head = 0, tail = 0;
        dist[start] = 0;
        queue[tail++] = start;
        while (head < tail) {
            int v = queue[head++];
            if (dist[v] == k) continue; // 거리 k 에서는 더 퍼뜨리지 않는다
            for (int u : adj[v]) {
                if (dist[u] != -1) continue;
                dist[u] = dist[v] + 1;
                queue[tail++] = u;
            }
        }
        return tail; // 큐에 들어간 직원 수 = 거리 k 이하인 직원 수
    }
}
