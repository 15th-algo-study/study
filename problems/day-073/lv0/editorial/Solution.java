class Solution {
    public int[][] solution(int rows, int cols) {
        int[][] ring = new int[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                // 위·왼쪽·아래·오른쪽 변까지의 거리 중 최솟값 + 1
                int toEdge = Math.min(Math.min(r, c), Math.min(rows - 1 - r, cols - 1 - c));
                ring[r][c] = toEdge + 1;
            }
        }
        return ring;
    }
}
