import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Solution {
    public int[] solution(int n, int[][] papers, int[][] queries) {
        List<Integer>[] adj = new List[n];
        for (int i = 0; i < n; i++) adj[i] = new ArrayList<>();
        for (int[] p : papers) {
            if (p[0] == p[1]) continue; // 잘못된 기록
            adj[p[0]].add(p[1]);
            adj[p[1]].add(p[0]);
        }

        // 정렬 후 인접한 중복을 건너뛰며 배열로 옮긴다
        int[][] coauthors = new int[n][];
        for (int v = 0; v < n; v++) {
            List<Integer> list = adj[v];
            Collections.sort(list);
            int[] arr = new int[list.size()];
            int len = 0;
            for (int x : list) {
                if (len == 0 || arr[len - 1] != x) arr[len++] = x;
            }
            coauthors[v] = java.util.Arrays.copyOf(arr, len);
        }

        int[] answer = new int[queries.length];
        for (int i = 0; i < queries.length; i++) {
            int v = queries[i][0], k = queries[i][1];
            answer[i] = k <= coauthors[v].length ? coauthors[v][k - 1] : -1;
        }
        return answer;
    }
}
