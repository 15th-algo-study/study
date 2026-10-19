import java.util.ArrayList;
import java.util.List;

class Solution {
    public int solution(int n, int[][] cables) {
        List<Integer>[] adj = new List[n];
        for (int i = 0; i < n; i++) adj[i] = new ArrayList<>();
        for (int[] c : cables) {
            adj[c[0]].add(c[1]);
            adj[c[1]].add(c[0]); // a == b 이면 같은 리스트에 두 번 들어간다
        }

        boolean[] visited = new boolean[n];
        int[] queue = new int[n];
        int answer = 0;
        for (int s = 0; s < n; s++) {
            if (visited[s]) continue;
            int head = 0, tail = 0;
            queue[tail++] = s;
            visited[s] = true;
            long degreeSum = 0; // 요소 안 모든 정점의 인접 리스트 길이 합 = 간선 수 × 2
            while (head < tail) {
                int v = queue[head++];
                degreeSum += adj[v].size();
                for (int u : adj[v]) {
                    if (!visited[u]) {
                        visited[u] = true;
                        queue[tail++] = u;
                    }
                }
            }
            int vertices = tail;
            long edges = degreeSum / 2;
            if (edges >= vertices) answer++; // 간선이 정점 수 이상이면 루프가 있다
        }
        return answer;
    }
}
