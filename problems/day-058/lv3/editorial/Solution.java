class Solution {
    public int solution(String[] forest) {
        int rows = forest.length, cols = forest[0].length();
        int cells = rows * cols;
        char[][] map = new char[rows][];
        int start = -1, target = -1;
        for (int r = 0; r < rows; r++) {
            map[r] = forest[r].toCharArray();
            for (int c = 0; c < cols; c++) {
                if (map[r][c] == 'S') start = r * cols + c;
                else if (map[r][c] == 'T') target = r * cols + c;
            }
        }

        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};
        // 상태 = used * cells + 칸 번호. used 0: 도끼 안 씀, 1: 이미 씀
        int[] dist = new int[2 * cells];
        java.util.Arrays.fill(dist, -1);
        int[] queue = new int[2 * cells];
        int head = 0, tail = 0;
        dist[start] = 0;
        queue[tail++] = start;
        while (head < tail) {
            int state = queue[head++];
            int used = state / cells, cell = state % cells;
            if (cell == target) return dist[state]; // 거리 순 방문이므로 처음 만난 것이 최솟값
            int r = cell / cols, c = cell % cols;
            for (int d = 0; d < 4; d++) {
                int nr = r + dr[d], nc = c + dc[d];
                if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) continue;
                int nextUsed = used;
                if (map[nr][nc] == '#') {
                    if (used == 1) continue; // 도끼는 한 번뿐
                    nextUsed = 1;
                }
                int nextState = nextUsed * cells + nr * cols + nc;
                if (dist[nextState] != -1) continue;
                dist[nextState] = dist[state] + 1;
                queue[tail++] = nextState;
            }
        }
        return -1;
    }
}
