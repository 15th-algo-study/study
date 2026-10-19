class Solution {
    public int[] solution(String[] photo) {
        int rows = photo.length, cols = photo[0].length();
        char[][] map = new char[rows][];
        for (int r = 0; r < rows; r++) map[r] = photo[r].toCharArray();

        int[] dr = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dc = {-1, 0, 1, -1, 1, -1, 0, 1};
        int[] queue = new int[rows * cols];
        int regions = 0, largest = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (map[r][c] != 'F') continue;
                // 새 구역: BFS 로 이어진 숲을 모두 방문하며 센다
                int head = 0, tail = 0;
                queue[tail++] = r * cols + c;
                map[r][c] = '.'; // 방문 표시 = 숲을 지운다
                while (head < tail) {
                    int cur = queue[head++];
                    int cr = cur / cols, cc = cur % cols;
                    for (int d = 0; d < 8; d++) {
                        int nr = cr + dr[d], nc = cc + dc[d];
                        if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) continue;
                        if (map[nr][nc] != 'F') continue;
                        map[nr][nc] = '.';
                        queue[tail++] = nr * cols + nc;
                    }
                }
                regions++;
                largest = Math.max(largest, tail);
            }
        }
        return new int[]{regions, largest};
    }
}
