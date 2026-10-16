class Solution {
    public int solution(int[][] board, int k) {
        int rows = board.length, cols = board[0].length;
        int count = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                // 오른쪽 칸과 가로로 놓는 자리
                if (c + 1 < cols && (board[r][c] + board[r][c + 1]) % k == 0) count++;
                // 아래 칸과 세로로 놓는 자리
                if (r + 1 < rows && (board[r][c] + board[r + 1][c]) % k == 0) count++;
            }
        }
        return count;
    }
}
