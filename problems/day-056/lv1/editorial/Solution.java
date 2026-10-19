import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

class Solution {
    public int[] solution(int n, int[][] pairs) {
        // 인접 리스트: 양방향으로 넣는다
        List<Integer>[] adj = new List[n];
        for (int i = 0; i < n; i++) adj[i] = new ArrayList<>();
        for (int[] p : pairs) {
            adj[p[0]].add(p[1]);
            adj[p[1]].add(p[0]);
        }

        boolean[] lit = new boolean[n];
        Deque<Integer> queue = new ArrayDeque<>();
        lit[0] = true;
        queue.add(0);
        int count = 0;
        while (!queue.isEmpty()) {
            int cur = queue.poll();
            count++;
            for (int next : adj[cur]) {
                if (lit[next]) continue;
                lit[next] = true; // 큐에 넣을 때 표시해야 중복으로 들어가지 않는다
                queue.add(next);
            }
        }

        int firstUnlit = -1;
        for (int i = 0; i < n; i++) {
            if (!lit[i]) {
                firstUnlit = i;
                break;
            }
        }
        return new int[]{count, firstUnlit};
    }
}
