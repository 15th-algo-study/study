class Solution {
    private static final int MOD = 1_000_000_007;

    public int solution(String[] rooms) {
        int rows = rooms.length, cols = rooms[0].length();
        int[][] ways = new int[rows][cols]; // ways[r][c] = 입구에서 (r, c)까지 오는 동선 수
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (rooms[r].charAt(c) == '#') continue; // 공사 중 → 0
                if (r == 0 && c == 0) {
                    ways[r][c] = 1;
                    continue;
                }
                long sum = 0;
                if (r > 0) sum += ways[r - 1][c]; // 위에서 내려옴
                if (c > 0) sum += ways[r][c - 1]; // 왼쪽에서 옴
                ways[r][c] = (int) (sum % MOD);
            }
        }
        return ways[rows - 1][cols - 1];
    }
}
