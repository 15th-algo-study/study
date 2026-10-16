class Solution {
    public long[] solution(int rows, int cols, int[][] ops) {
        // 2차원 차분 배열: 직사각형 하나를 네 모서리 표시로 기록한다
        long[][] diff = new long[rows + 1][cols + 1];
        for (int[] op : ops) {
            int top = Math.min(op[0], op[2]), bottom = Math.max(op[0], op[2]);
            int left = Math.min(op[1], op[3]), right = Math.max(op[1], op[3]);
            int v = op[4];
            diff[top][left] += v;
            diff[top][right + 1] -= v;
            diff[bottom + 1][left] -= v;
            diff[bottom + 1][right + 1] += v;
        }
        // 가로 누적 → 세로 누적으로 각 칸의 실제 값을 복원
        for (int r = 0; r < rows; r++)
            for (int c = 1; c < cols; c++) diff[r][c] += diff[r][c - 1];
        for (int r = 1; r < rows; r++)
            for (int c = 0; c < cols; c++) diff[r][c] += diff[r - 1][c];

        long max = Long.MIN_VALUE, count = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (diff[r][c] > max) {
                    max = diff[r][c];
                    count = 1;
                } else if (diff[r][c] == max) {
                    count++;
                }
            }
        }
        return new long[]{max, count};
    }
}
