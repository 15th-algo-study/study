import java.util.Arrays;

class Solution {
    private int n;
    private int[][] handoff;
    private int[] order;
    private boolean[] used;
    private int[][] best; // best[a][b] = a 바로 다음 b 인 순서 중 최소 총 전달 시간

    public int[] solution(int[][] handoff, int[][] queries) {
        this.n = handoff.length;
        this.handoff = handoff;
        order = new int[n];
        used = new boolean[n];
        best = new int[n][n];
        for (int[] row : best) Arrays.fill(row, Integer.MAX_VALUE);

        permute(0); // 모든 순열을 한 번만 열거하며 표를 채운다

        int[] answer = new int[queries.length];
        for (int i = 0; i < queries.length; i++) {
            answer[i] = best[queries[i][0]][queries[i][1]];
        }
        return answer;
    }

    private void permute(int depth) {
        if (depth == n) {
            int cost = 0;
            for (int i = 0; i + 1 < n; i++) cost += handoff[order[i]][order[i + 1]];
            // 이 순열은 안에 있는 모든 이웃 쌍 (a, b) 의 후보다
            for (int i = 0; i + 1 < n; i++) {
                int a = order[i], b = order[i + 1];
                if (cost < best[a][b]) best[a][b] = cost;
            }
            return;
        }
        for (int v = 0; v < n; v++) {
            if (used[v]) continue;
            used[v] = true;
            order[depth] = v;
            permute(depth + 1);
            used[v] = false;
        }
    }
}
