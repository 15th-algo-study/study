class Solution {
    public int solution(int[][] paddy) {
        int rows = paddy.length, cols = paddy[0].length;
        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};
        int answer = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                boolean low = true;
                for (int d = 0; d < 4; d++) {
                    int nr = r + dr[d], nc = c + dc[d];
                    if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) continue; // 격자 밖은 비교하지 않는다
                    if (paddy[nr][nc] <= paddy[r][c]) { // 같거나 낮은 이웃이 있으면 고이지 않는다
                        low = false;
                        break;
                    }
                }
                if (low) answer++;
            }
        }
        return answer;
    }
}
