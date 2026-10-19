import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

class Solution {
    public String solution(int n, int[] from, int[] to) {
        int m = from.length;
        List<Integer>[] adj = new List[n];
        for (int i = 0; i < n; i++) adj[i] = new ArrayList<>();
        int[] deg = new int[n];
        for (int i = 0; i < m; i++) {
            adj[from[i]].add(to[i]);
            adj[to[i]].add(from[i]);
            deg[from[i]]++;
            deg[to[i]]++; // from == to 이면 같은 정류장이 2 증가
        }

        // 1) 차수 조건으로 후보 결정
        int deg1 = 0, deg2 = 0;
        for (int v = 0; v < n; v++) {
            if (deg[v] == 1) deg1++;
            else if (deg[v] == 2) deg2++;
        }
        String candidate;
        if (n == 1 && m == 0) return "LINE";
        if (m == n - 1 && deg1 == 2 && deg2 == n - 2) candidate = "LINE";
        else if (n >= 3 && m == n && deg2 == n) candidate = "RING";
        else return "NONE";

        // 2) 연결성: 0번에서 BFS 로 모두 닿는지
        boolean[] visited = new boolean[n];
        Deque<Integer> queue = new ArrayDeque<>();
        visited[0] = true;
        queue.add(0);
        int reached = 0;
        while (!queue.isEmpty()) {
            int v = queue.poll();
            reached++;
            for (int u : adj[v]) {
                if (!visited[u]) {
                    visited[u] = true;
                    queue.add(u);
                }
            }
        }
        return reached == n ? candidate : "NONE";
    }
}
