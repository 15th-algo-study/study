class Solution {
    public int solution(String[] mosaic) {
        int rows = mosaic.length, cols = mosaic[0].length();
        int[] dr = {0, 1, 1, 1};  // 오른쪽, 아래, 오른쪽 아래, 왼쪽 아래 — 절반만 보면 쌍이 한 번씩 세어진다
        int[] dc = {1, 0, 1, -1};
        int answer = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                char color = mosaic[r].charAt(c);
                for (int d = 0; d < 4; d++) {
                    int nr = r + dr[d], nc = c + dc[d];
                    if (nr >= rows || nc < 0 || nc >= cols) continue; // 왼쪽 아래 방향은 nc 가 음수가 될 수 있다
                    if (mosaic[nr].charAt(nc) == color) answer++;
                }
            }
        }
        return answer;
    }
}
