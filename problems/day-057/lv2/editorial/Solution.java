class Solution {
    public int solution(String[] ruins) {
        int rows = ruins.length, cols = ruins[0].length();
        char[][] map = new char[rows][];
        int start = -1, target = -1;
        for (int r = 0; r < rows; r++) {
            map[r] = ruins[r].toCharArray();
            for (int c = 0; c < cols; c++) {
                if (map[r][c] == 'S') start = r * cols + c;
                else if (map[r][c] == 'T') target = r * cols + c;
            }
        }

        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};
        int[] dist = new int[rows * cols];
        java.util.Arrays.fill(dist, -1); // -1 = 아직 방문하지 않음
        int[] queue = new int[rows * cols]; // 칸은 한 번씩만 들어가므로 배열 큐로 충분
        int head = 0, tail = 0;
        queue[tail++] = start;
        dist[start] = 0;
        while (head < tail) {
            int cur = queue[head++];
            if (cur == target) return dist[cur];
            int r = cur / cols, c = cur % cols;
            for (int d = 0; d < 4; d++) {
                int nr = r + dr[d], nc = c + dc[d];
                if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) continue;
                if (map[nr][nc] == '#') continue;
                int next = nr * cols + nc;
                if (dist[next] != -1) continue;
                dist[next] = dist[cur] + 1;
                queue[tail++] = next;
            }
        }
        return -1;
    }
}
