class Solution {
    public int[][] solution(int rows, int cols) {
        int[][] field = new int[rows][cols];
        int num = 1;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int col = (r % 2 == 0) ? c : cols - 1 - c; // 홀수 행은 오른쪽부터
                field[r][col] = num++;
            }
        }
        return field;
    }
}
