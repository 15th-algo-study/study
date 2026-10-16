class Solution {
    public long[] solution(int[][] panels, int[][] queries) {
        int rows = panels.length, cols = panels[0].length;
        long[][] sum = new long[rows + 1][cols + 1]; // sum[i][j] = (0,0)~(i-1,j-1) 의 합
        for (int i = 1; i <= rows; i++) {
            for (int j = 1; j <= cols; j++) {
                sum[i][j] = panels[i - 1][j - 1] + sum[i - 1][j] + sum[i][j - 1] - sum[i - 1][j - 1];
            }
        }
        long[] answer = new long[queries.length];
        for (int q = 0; q < queries.length; q++) {
            int r1 = Math.min(queries[q][0], queries[q][2]), r2 = Math.max(queries[q][0], queries[q][2]);
            int c1 = Math.min(queries[q][1], queries[q][3]), c2 = Math.max(queries[q][1], queries[q][3]);
            // 포함-배제: 큰 사각형 - 위쪽 - 왼쪽 + 겹친 왼쪽 위
            answer[q] = sum[r2 + 1][c2 + 1] - sum[r1][c2 + 1] - sum[r2 + 1][c1] + sum[r1][c1];
        }
        return answer;
    }
}
