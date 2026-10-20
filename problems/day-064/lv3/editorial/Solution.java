class Solution {
    public int solution(String[] plaza, int k) {
        int rows = plaza.length, cols = plaza[0].length();
        char[][] map = new char[rows][];
        int start = -1, target = -1;
        for (int r = 0; r < rows; r++) {
            map[r] = plaza[r].toCharArray();
            for (int c = 0; c < cols; c++) {
                if (map[r][c] == 'S') start = r * cols + c;
                else if (map[r][c] == 'T') target = r * cols + c;
            }
        }

        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};
        int[] dist = new int[rows * cols];
        java.util.Arrays.fill(dist, -1);
        int[] queue = new int[rows * cols];
        int head = 0, tail = 0;
        dist[start] = 0;
        queue[tail++] = start;
        while (head < tail) {
            int cur = queue[head++];
            int r = cur / cols, c = cur % cols;
            int nd = dist[cur] + 1;
            for (int d = 0; d < 4; d++) {
                for (int step = 1; step <= k; step++) {
                    int nr = r + dr[d] * step, nc = c + dc[d] * step;
                    if (nr < 0 || nr >= rows || nc < 0 || nc >= cols || map[nr][nc] == '#') break;
                    int next = nr * cols + nc;
                    if (dist[next] == -1) {
                        dist[next] = nd;
                        if (next == target) return nd;
                        queue[tail++] = next;
                    } else if (dist[next] < nd) {
                        // 이 칸에서 이미 더 빨리 같은 방향으로 미끄러질 수 있었다 → 그 너머는 볼 필요 없음
                        break;
                    }
                    // dist[next] == nd: 같은 시각에 닿은 칸. 너머에 아직 안 닿은 칸이 있을 수 있으니 계속
                }
            }
        }
        return -1;
    }
}
