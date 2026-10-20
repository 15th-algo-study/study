import java.util.Arrays;

class Solution {
    public int[][] solution(String[] town, int k) {
        int rows = town.length, cols = town[0].length();
        int cells = rows * cols;
        char[][] map = new char[rows][];
        int start = -1;
        for (int r = 0; r < rows; r++) {
            map[r] = town[r].toCharArray();
            for (int c = 0; c < cols; c++) if (map[r][c] == 'S') start = r * cols + c;
        }

        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};
        int[] dist = new int[cells];
        Arrays.fill(dist, -1);
        int[] queue = new int[cells];
        long[] found = new long[cells]; // (거리, 칸 번호)를 long 하나로: 정렬하면 거리 → 행 → 열 순
        int count = 0, head = 0, tail = 0;
        dist[start] = 0;
        queue[tail++] = start;
        while (head < tail) {
            int cur = queue[head++];
            int r = cur / cols, c = cur % cols;
            if (map[r][c] == 'C') found[count++] = (long) dist[cur] * cells + cur;
            for (int d = 0; d < 4; d++) {
                int nr = r + dr[d], nc = c + dc[d];
                if (nr < 0 || nr >= rows || nc < 0 || nc >= cols || map[nr][nc] == '#') continue;
                int next = nr * cols + nc;
                if (dist[next] != -1) continue;
                dist[next] = dist[cur] + 1;
                queue[tail++] = next;
            }
        }

        // BFS 순서는 같은 거리 안에서 행·열 순이 아니므로 직접 정렬한다
        Arrays.sort(found, 0, count);
        int size = Math.min(k, count);
        int[][] answer = new int[size][];
        for (int i = 0; i < size; i++) {
            int cell = (int) (found[i] % cells);
            answer[i] = new int[]{cell / cols, cell % cols};
        }
        return answer;
    }
}
