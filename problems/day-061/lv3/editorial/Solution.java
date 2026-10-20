class Solution {
    public long[] solution(String[] floor) {
        int rows = floor.length, cols = floor[0].length();
        char[][] map = new char[rows][];
        for (int r = 0; r < rows; r++) map[r] = floor[r].toCharArray();

        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};
        int[] dist = new int[rows * cols];
        java.util.Arrays.fill(dist, -1);
        int[] queue = new int[rows * cols];
        int head = 0, tail = 0;
        // 모든 출구에서 거꾸로 출발 (다중 시작점)
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (map[r][c] == 'E') {
                    dist[r * cols + c] = 0;
                    queue[tail++] = r * cols + c;
                }
            }
        }

        while (head < tail) {
            int cur = queue[head++];
            int r = cur / cols, c = cur % cols;
            // cur 로 한 번에 들어올 수 있는 이웃 u 를 찾는다 (간선을 거꾸로 따라감)
            for (int d = 0; d < 4; d++) {
                int ur = r + dr[d], uc = c + dc[d];
                if (ur < 0 || ur >= rows || uc < 0 || uc >= cols) continue;
                int u = ur * cols + uc;
                if (dist[u] != -1 || map[ur][uc] == '#') continue;
                if (canMove(map[ur][uc], ur, uc, r, c)) {
                    dist[u] = dist[cur] + 1;
                    queue[tail++] = u;
                }
            }
        }

        long sum = 0, stuck = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                char ch = map[r][c];
                if (ch == '#' || ch == 'E') continue;
                int d = dist[r * cols + c];
                if (d == -1) stuck++;
                else sum += d;
            }
        }
        return new long[]{sum, stuck};
    }

    /** (ur, uc) 칸에서 바로 옆 (r, c) 칸으로 한 번에 움직일 수 있는가 */
    private boolean canMove(char ch, int ur, int uc, int r, int c) {
        return switch (ch) {
            case '.' -> true;
            case '>' -> ur == r && uc + 1 == c;
            case '<' -> ur == r && uc - 1 == c;
            case '^' -> ur - 1 == r && uc == c;
            case 'v' -> ur + 1 == r && uc == c;
            default -> false;
        };
    }
}
