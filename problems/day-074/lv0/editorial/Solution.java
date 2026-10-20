class Solution {
    public int[][] solution(int rows, int cols) {
        int[][] order = new int[rows][cols];
        int num = 1;
        for (int s = 0; s <= rows + cols - 2; s++) {      // s = r + c 인 줄
            int rStart = Math.max(0, s - (cols - 1));      // 열이 cols-1 을 넘지 않게
            int rEnd = Math.min(rows - 1, s);              // 열이 0 아래로 내려가지 않게
            for (int r = rStart; r <= rEnd; r++) {
                order[r][s - r] = num++;                   // 위쪽 행부터
            }
        }
        return order;
    }
}
