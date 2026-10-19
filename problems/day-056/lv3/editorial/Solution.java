import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

class Solution {
    public int[] solution(int n, int[][] bridges, int[][] plans) {
        List<Integer>[] adj = new List[n];
        for (int i = 0; i < n; i++) adj[i] = new ArrayList<>();
        for (int[] b : bridges) {
            adj[b[0]].add(b[1]);
            adj[b[1]].add(b[0]);
        }

        // comp[v] = v 가 속한 연결 요소 번호, size[c] = 요소 c 의 크기
        int[] comp = new int[n];
        java.util.Arrays.fill(comp, -1);
        int[] size = new int[n];
        int components = 0;
        Deque<Integer> queue = new ArrayDeque<>();
        for (int s = 0; s < n; s++) {
            if (comp[s] != -1) continue;
            int c = components++;
            comp[s] = c;
            queue.add(s);
            while (!queue.isEmpty()) {
                int v = queue.poll();
                size[c]++;
                for (int u : adj[v]) {
                    if (comp[u] == -1) {
                        comp[u] = c;
                        queue.add(u);
                    }
                }
            }
        }

        int[] answer = new int[plans.length];
        for (int i = 0; i < plans.length; i++) {
            int a = comp[plans[i][0]], b = comp[plans[i][1]];
            answer[i] = a == b ? size[a] : size[a] + size[b]; // 같은 요소면 그대로, 다르면 두 크기의 합
        }
        return answer;
    }
}
