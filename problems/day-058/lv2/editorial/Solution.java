import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Solution {
    public int[] solution(int n, int[][] friends, int start, int[] people) {
        List<Integer>[] adj = new List[n];
        for (int i = 0; i < n; i++) adj[i] = new ArrayList<>();
        for (int[] f : friends) {
            adj[f[0]].add(f[1]);
            adj[f[1]].add(f[0]);
        }

        // BFS 한 번으로 모든 사람의 최단 거리(= 처음 듣는 날)를 구한다
        int[] dist = new int[n];
        Arrays.fill(dist, -1);
        int[] queue = new int[n];
        int head = 0, tail = 0;
        dist[start] = 0;
        queue[tail++] = start;
        while (head < tail) {
            int v = queue[head++];
            for (int u : adj[v]) {
                if (dist[u] != -1) continue;
                dist[u] = dist[v] + 1; // 처음 발견했을 때의 거리가 최단 거리
                queue[tail++] = u;
            }
        }

        int[] answer = new int[people.length];
        for (int i = 0; i < people.length; i++) answer[i] = dist[people[i]];
        return answer;
    }
}
