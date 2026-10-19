class Solution {
    public int[] solution(int n, int m, String moves) {
        String dirs = "UDLR";
        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};
        int r = 0, c = 0;
        for (char ch : moves.toCharArray()) {
            int d = dirs.indexOf(ch);
            int nr = r + dr[d], nc = c + dc[d];
            if (nr < 0 || nr >= n || nc < 0 || nc >= m) continue; // 밖으로 나가는 명령은 무시
            r = nr;
            c = nc;
        }
        return new int[]{r, c};
    }
}
