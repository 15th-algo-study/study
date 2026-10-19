import java.util.ArrayList;
import java.util.List;

class Solution {
    public int solution(int n, int[][] alleys) {
        List<Integer>[] adj = new List[n]; // 정방향
        List<Integer>[] rev = new List[n]; // 역방향
        for (int i = 0; i < n; i++) {
            adj[i] = new ArrayList<>();
            rev[i] = new ArrayList<>();
        }
        for (int[] a : alleys) {
            adj[a[0]].add(a[1]);
            rev[a[1]].add(a[0]);
        }

        boolean[] reach = bfs(adj, n); // 0 에서 갈 수 있는 교차로
        boolean[] back = bfs(rev, n);  // 역그래프에서 0 에서 갈 수 있는 = 원래 그래프에서 0 으로 올 수 있는
        int answer = 0;
        for (int v = 0; v < n; v++) if (reach[v] && back[v]) answer++;
        return answer;
    }

    private boolean[] bfs(List<Integer>[] graph, int n) {
        boolean[] visited = new boolean[n];
        int[] queue = new int[n];
        int head = 0, tail = 0;
        visited[0] = true;
        queue[tail++] = 0;
        while (head < tail) {
            int v = queue[head++];
            for (int u : graph[v]) {
                if (!visited[u]) {
                    visited[u] = true;
                    queue[tail++] = u;
                }
            }
        }
        return visited;
    }
}
