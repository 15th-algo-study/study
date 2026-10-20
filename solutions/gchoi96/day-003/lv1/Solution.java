class Solution {
    private static final int[] DR = {-1, -1, -1, 0, 0, 1, 1, 1};
    private static final int[] DC = {-1, 0, 1, -1, 1, -1, 0, 1};

    public String[] solution(String[] board) {
        int rows = board.length, cols = board[0].length();
        String[] answer = new String[rows];
        for (int r = 0; r < rows; r++) {
            char[] line = new char[cols];
            for (int c = 0; c < cols; c++) {
                if (board[r].charAt(c) == '*') {
                    line[c] = '*';
                    continue;
                }
                int mines = 0;
                for (int d = 0; d < 8; d++) {
                    int nr = r + DR[d], nc = c + DC[d];
                    if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && board[nr].charAt(nc) == '*') mines++;
                }
                line[c] = (char) ('0' + mines);
            }
            answer[r] = new String(line);
        }
        return answer;
    }
}
