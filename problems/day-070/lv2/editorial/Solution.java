class Solution {
    private static final int MOD = 1_000_000_007;

    public int[] solution(String[] slope) {
        int rows = slope.length, cols = slope[0].length();
        int[][] best = new int[rows][cols];  // 모을 수 있는 깃발 최댓값 (-1 = 도달 불가)
        int[][] count = new int[rows][cols]; // 그 최댓값을 모으는 경로 수
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                best[r][c] = -1;
                char ch = slope[r].charAt(c);
                if (ch == '#') continue;
                if (r == 0 && c == 0) {
                    best[r][c] = ch - '0';
                    count[r][c] = 1;
                    continue;
                }
                int top = -1;
                long ways = 0;
                if (r > 0 && best[r - 1][c] >= 0) { // 위에서 내려옴
                    top = best[r - 1][c];
                    ways = count[r - 1][c];
                }
                if (c > 0 && best[r][c - 1] >= 0) { // 왼쪽에서 옴
                    if (best[r][c - 1] > top) {
                        top = best[r][c - 1];
                        ways = count[r][c - 1];
                    } else if (best[r][c - 1] == top) {
                        ways += count[r][c - 1]; // 같은 최댓값이면 경로 수를 더함
                    }
                }
                if (top < 0) continue; // 들어올 곳이 없음
                best[r][c] = top + (ch - '0');
                count[r][c] = (int) (ways % MOD);
            }
        }
        int b = best[rows - 1][cols - 1];
        return b < 0 ? new int[]{-1, 0} : new int[]{b, count[rows - 1][cols - 1]};
    }
}
