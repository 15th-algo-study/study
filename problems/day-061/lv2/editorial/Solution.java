class Solution {
    private int rows, cols;
    private char[][] map;

    public int solution(String[] maze) {
        rows = maze.length;
        cols = maze[0].length();
        map = new char[rows][];
        int kid = -1, dad = -1;
        for (int r = 0; r < rows; r++) {
            map[r] = maze[r].toCharArray();
            for (int c = 0; c < cols; c++) {
                if (map[r][c] == 'A') kid = r * cols + c;
                else if (map[r][c] == 'B') dad = r * cols + c;
            }
        }
        int[] fromKid = bfs(kid, 'a'); // 아이는 'a'(낮은 틈)를 지나갈 수 있다
        int[] fromDad = bfs(dad, 'b'); // 아빠는 'b'(진흙 웅덩이)를 지나갈 수 있다

        int best = Integer.MAX_VALUE;
        for (int cell = 0; cell < rows * cols; cell++) {
            char ch = map[cell / cols][cell % cols];
            if (ch == 'a' || ch == 'b' || ch == '#') continue; // 둘 다 설 수 있는 칸만
            if (fromKid[cell] == -1 || fromDad[cell] == -1) continue;
            // 먼저 온 사람이 기다리면 늦게 온 사람의 도착 시각에 만난다
            best = Math.min(best, Math.max(fromKid[cell], fromDad[cell]));
        }
        return best == Integer.MAX_VALUE ? -1 : best;
    }

    /** start 에서 '.', 'A', 'B', special 칸만 밟는 BFS 거리. 못 가면 -1. */
    private int[] bfs(int start, char special) {
        int[] dist = new int[rows * cols];
        java.util.Arrays.fill(dist, -1);
        int[] queue = new int[rows * cols];
        int head = 0, tail = 0;
        dist[start] = 0;
        queue[tail++] = start;
        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};
        while (head < tail) {
            int cur = queue[head++];
            int r = cur / cols, c = cur % cols;
            for (int d = 0; d < 4; d++) {
                int nr = r + dr[d], nc = c + dc[d];
                if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) continue;
                char ch = map[nr][nc];
                boolean passable = ch == '.' || ch == 'A' || ch == 'B' || ch == special;
                int next = nr * cols + nc;
                if (!passable || dist[next] != -1) continue;
                dist[next] = dist[cur] + 1;
                queue[tail++] = next;
            }
        }
        return dist;
    }
}
