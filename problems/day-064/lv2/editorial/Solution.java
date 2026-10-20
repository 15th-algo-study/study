class Solution {
    public String solution(String[] field) {
        int rows = field.length, cols = field[0].length();
        char[][] map = new char[rows][];
        int start = -1, finish = -1;
        for (int r = 0; r < rows; r++) {
            map[r] = field[r].toCharArray();
            for (int c = 0; c < cols; c++) {
                if (map[r][c] == 'A') start = r * cols + c;
                else if (map[r][c] == 'B') finish = r * cols + c;
            }
        }
        // 알파벳 순서대로: E, N, S, W
        char[] letter = {'E', 'N', 'S', 'W'};
        int[] dr = {0, -1, 1, 0};
        int[] dc = {1, 0, 0, -1};

        // 1) 도착 지점에서 거꾸로 BFS: toFinish[칸] = 그 칸에서 도착까지 남은 최소 걸음
        int[] toFinish = new int[rows * cols];
        java.util.Arrays.fill(toFinish, -1);
        int[] queue = new int[rows * cols];
        int head = 0, tail = 0;
        toFinish[finish] = 0;
        queue[tail++] = finish;
        while (head < tail) {
            int cur = queue[head++];
            int r = cur / cols, c = cur % cols;
            for (int d = 0; d < 4; d++) {
                int nr = r + dr[d], nc = c + dc[d];
                if (nr < 0 || nr >= rows || nc < 0 || nc >= cols || map[nr][nc] == '#') continue;
                int next = nr * cols + nc;
                if (toFinish[next] != -1) continue;
                toFinish[next] = toFinish[cur] + 1;
                queue[tail++] = next;
            }
        }
        if (toFinish[start] == -1) return "";

        // 2) 출발 지점부터, 남은 걸음이 1 줄어드는 방향 중 가장 앞 글자를 고른다
        StringBuilder sb = new StringBuilder();
        int cur = start;
        while (cur != finish) {
            int r = cur / cols, c = cur % cols;
            for (int d = 0; d < 4; d++) {
                int nr = r + dr[d], nc = c + dc[d];
                if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) continue;
                int next = nr * cols + nc;
                if (toFinish[next] == toFinish[cur] - 1) { // 최단 경로에서 벗어나지 않는 첫 글자
                    sb.append(letter[d]);
                    cur = next;
                    break;
                }
            }
        }
        return sb.toString();
    }
}
