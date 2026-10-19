class Solution {
    public int solution(String[] wetland) {
        int rows = wetland.length, cols = wetland[0].length();
        char[][] map = new char[rows][];
        for (int r = 0; r < rows; r++) map[r] = wetland[r].toCharArray();

        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};
        int[] id = new int[rows * cols];        // 칸이 속한 육지 번호 (물은 0)
        int[] size = new int[rows * cols + 1];  // 번호별 크기
        int[] queue = new int[rows * cols];
        int regions = 0;
        boolean hasWater = false;

        // 1) 육지 번호 매기기
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (map[r][c] == 'W') { hasWater = true; continue; }
                if (id[r * cols + c] != 0) continue;
                int number = ++regions;
                int head = 0, tail = 0;
                queue[tail++] = r * cols + c;
                id[r * cols + c] = number;
                while (head < tail) {
                    int cur = queue[head++];
                    int cr = cur / cols, cc = cur % cols;
                    for (int d = 0; d < 4; d++) {
                        int nr = cr + dr[d], nc = cc + dc[d];
                        if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) continue;
                        int next = nr * cols + nc;
                        if (map[nr][nc] == 'W' || id[next] != 0) continue;
                        id[next] = number;
                        queue[tail++] = next;
                    }
                }
                size[number] = tail;
            }
        }

        if (!hasWater) return rows * cols; // 메울 곳이 없으면 전체가 하나의 육지

        // 2) 물 칸마다 서로 다른 이웃 육지의 크기 합 + 1
        int best = 1;
        int[] seen = new int[4];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (map[r][c] != 'W') continue;
                int total = 1, count = 0;
                for (int d = 0; d < 4; d++) {
                    int nr = r + dr[d], nc = c + dc[d];
                    if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) continue;
                    int number = id[nr * cols + nc];
                    if (number == 0) continue;
                    boolean dup = false;
                    for (int i = 0; i < count; i++) if (seen[i] == number) dup = true; // 같은 육지는 한 번만
                    if (dup) continue;
                    seen[count++] = number;
                    total += size[number];
                }
                best = Math.max(best, total);
            }
        }
        return best;
    }
}
