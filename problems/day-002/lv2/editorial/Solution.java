class Solution {
    public int solution(String[] grid, String commands) {
        int rows = grid.length, cols = grid[0].length();
        boolean[][] visited = new boolean[rows][cols];
        int r = 0, c = 0;
        visited[0][0] = true;
        int count = 1;
        for (int i = 0; i < commands.length(); i++) {
            int nr = r, nc = c;
            switch (commands.charAt(i)) {
                case 'U' -> nr--;
                case 'D' -> nr++;
                case 'L' -> nc--;
                default -> nc++;
            }
            if (nr < 0 || nr >= rows || nc < 0 || nc >= cols || grid[nr].charAt(nc) == '#') continue;
            r = nr;
            c = nc;
            if (!visited[r][c]) {
                visited[r][c] = true;
                count++;
            }
        }
        return count;
    }
}
