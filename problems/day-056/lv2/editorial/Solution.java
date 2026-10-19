import java.util.Arrays;

class Solution {
    public int[] solution(int n, int[][] logs) {
        // 1차 훑기: 차수 세기 (일지의 연속한 두 방이 간선, 양쪽 모두 +1)
        int[] deg = new int[n + 1];
        int edges = 0;
        for (int[] log : logs) {
            for (int j = 0; j + 1 < log.length; j++) {
                deg[log[j]]++;
                deg[log[j + 1]]++;
                edges++;
            }
        }
        // start[v] = v 의 이웃이 to 배열에서 시작하는 위치
        int[] start = new int[n + 1];
        for (int v = 0; v < n; v++) start[v + 1] = start[v] + deg[v];
        int[] to = new int[2 * edges];
        int[] pos = Arrays.copyOf(start, n);
        for (int[] log : logs) {
            for (int j = 0; j + 1 < log.length; j++) {
                int a = log[j], b = log[j + 1];
                to[pos[a]++] = b;
                to[pos[b]++] = a;
            }
        }

        // 반복문 DFS 로 연결 요소 크기 세기 (재귀는 긴 사슬에서 스택이 넘친다)
        boolean[] visited = new boolean[n];
        int[] stack = new int[n];
        int[] sizes = new int[n];
        int count = 0;
        for (int s = 0; s < n; s++) {
            if (visited[s]) continue;
            int size = 0, top = 0;
            stack[top++] = s;
            visited[s] = true;
            while (top > 0) {
                int v = stack[--top];
                size++;
                for (int i = start[v]; i < start[v + 1]; i++) {
                    int u = to[i];
                    if (!visited[u]) {
                        visited[u] = true;
                        stack[top++] = u;
                    }
                }
            }
            sizes[count++] = size;
        }

        int[] answer = Arrays.copyOf(sizes, count);
        Arrays.sort(answer);
        for (int i = 0, j = count - 1; i < j; i++, j--) { // 내림차순으로 뒤집기
            int t = answer[i]; answer[i] = answer[j]; answer[j] = t;
        }
        return answer;
    }
}
