class Solution {
    public int solution(int[][] field) {
        int rows = field.length, cols = field[0].length;
        int[] dr = {-1, 1, 0, 0}; // 위, 아래, 왼쪽, 오른쪽
        int[] dc = {0, 0, -1, 1};
        int answer = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (field[r][c] != 2) continue;
                for (int d = 0; d < 4; d++) {
                    int nr = r + dr[d], nc = c + dc[d];
                    if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) continue; // 범위 확인이 먼저
                    if (field[nr][nc] == 1) answer++;
                }
            }
        }
        return answer;
    }
}
