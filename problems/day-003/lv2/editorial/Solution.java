class Solution {
    // 방향: 0=위, 1=오른쪽, 2=아래, 3=왼쪽
    private static final int[] DR = {-1, 0, 1, 0};
    private static final int[] DC = {0, 1, 0, -1};
    private static final int[] SLASH = {1, 0, 3, 2};     // '/' : 위↔오른쪽, 아래↔왼쪽
    private static final int[] BACKSLASH = {3, 2, 1, 0}; // '\' : 위↔왼쪽, 오른쪽↔아래

    public int solution(String[] grid) {
        int rows = grid.length, cols = grid[0].length();
        boolean[][] visited = new boolean[rows][cols];
        int r = 0, c = 0, dir = 1, count = 0;
        while (r >= 0 && r < rows && c >= 0 && c < cols) {
            if (!visited[r][c]) {
                visited[r][c] = true;
                count++;
            }
            char cell = grid[r].charAt(c);
            if (cell == '/') dir = SLASH[dir];
            else if (cell == '\\') dir = BACKSLASH[dir];
            r += DR[dir];
            c += DC[dir];
        }
        return count;
    }
}
