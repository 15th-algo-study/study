class Solution {
    private static final int NONE = 0, RED = 1, BLUE = 2, PURPLE = 3;

    public int[] solution(String[] paper) {
        int rows = paper.length, cols = paper[0].length();
        char[][] map = new char[rows][];
        int[] color = new int[rows * cols];
        int[] minute = new int[rows * cols];
        int[] queue = new int[rows * cols];
        int head = 0, tail = 0;
        // 두 색의 잉크 방울을 모두 0분으로 한 큐에 넣는다
        for (int r = 0; r < rows; r++) {
            map[r] = paper[r].toCharArray();
            for (int c = 0; c < cols; c++) {
                char ch = map[r][c];
                if (ch == 'R' || ch == 'B') {
                    color[r * cols + c] = ch == 'R' ? RED : BLUE;
                    queue[tail++] = r * cols + c;
                }
            }
        }

        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};
        while (head < tail) {
            int cur = queue[head++];
            if (color[cur] == PURPLE) continue; // 보라는 번지지 않는다 (같은 분에 보라가 된 칸 포함)
            int r = cur / cols, c = cur % cols;
            for (int d = 0; d < 4; d++) {
                int nr = r + dr[d], nc = c + dc[d];
                if (nr < 0 || nr >= rows || nc < 0 || nc >= cols || map[nr][nc] != '.') continue;
                int next = nr * cols + nc;
                if (color[next] == NONE) {
                    color[next] = color[cur];
                    minute[next] = minute[cur] + 1;
                    queue[tail++] = next;
                } else if (minute[next] == minute[cur] + 1 && color[next] != color[cur]) {
                    color[next] = PURPLE; // 같은 분에 다른 색이 스며듦
                }
            }
        }

        int[] answer = new int[3];
        for (int x : color) if (x != NONE) answer[x - 1]++;
        return answer;
    }
}
