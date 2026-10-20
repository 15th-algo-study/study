class Solution {
    public boolean[] solution(String[] park, int[][] missions) {
        int rows = park.length, cols = park[0].length();
        char[][] map = new char[rows][];
        int start = -1;
        for (int r = 0; r < rows; r++) {
            map[r] = park[r].toCharArray();
            for (int c = 0; c < cols; c++) if (map[r][c] == 'S') start = r * cols + c;
        }

        // S 에서 모든 칸까지의 최단 걸음 수 (BFS 한 번)
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
            for (int d = 0; d < 4; d++) {
                int nr = r + dr[d], nc = c + dc[d];
                if (nr < 0 || nr >= rows || nc < 0 || nc >= cols || map[nr][nc] == '#') continue;
                int next = nr * cols + nc;
                if (dist[next] != -1) continue;
                dist[next] = dist[cur] + 1;
                queue[tail++] = next;
            }
        }
        boolean canMove = tail > 1; // S 에서 한 걸음이라도 움직일 수 있는가

        boolean[] answer = new boolean[missions.length];
        for (int i = 0; i < missions.length; i++) {
            int d = dist[missions[i][0] * cols + missions[i][1]];
            int k = missions[i][2];
            if (d == -1 || k < d || (k - d) % 2 != 0) continue; // 못 가거나, 모자라거나, 홀짝이 다름
            if (k > d && !canMove) continue;                    // 남는 걸음을 왕복으로 채울 수 없음
            answer[i] = true;
        }
        return answer;
    }
}
