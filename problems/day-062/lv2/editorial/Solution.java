class Solution {
    public int solution(String[] museum, int[] start, int[] goal) {
        int rows = museum.length, cols = museum[0].length();
        char[][] map = new char[rows][];
        for (int r = 0; r < rows; r++) map[r] = museum[r].toCharArray();
        int[] dr = {-1, 0, 1, 0}; // 0 북, 1 동, 2 남, 3 서
        int[] dc = {0, 1, 0, -1};

        // 상태 번호 = (칸 번호) * 4 + 방향
        int states = rows * cols * 4;
        int[] time = new int[states];
        java.util.Arrays.fill(time, -1);
        int[] queue = new int[states];
        int head = 0, tail = 0;
        int from = (start[0] * cols + start[1]) * 4 + start[2];
        int to = (goal[0] * cols + goal[1]) * 4 + goal[2];
        time[from] = 0;
        queue[tail++] = from;
        while (head < tail) {
            int state = queue[head++];
            if (state == to) return time[state];
            int cell = state / 4, dir = state % 4;
            int r = cell / cols, c = cell % cols;
            // 세 가지 명령: 좌회전, 우회전, 전진 (모두 1초)
            int left = cell * 4 + (dir + 3) % 4;
            int right = cell * 4 + (dir + 1) % 4;
            int forward = -1;
            int nr = r + dr[dir], nc = c + dc[dir];
            if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && map[nr][nc] == '.') {
                forward = (nr * cols + nc) * 4 + dir;
            }
            for (int next : new int[]{left, right, forward}) {
                if (next == -1 || time[next] != -1) continue;
                time[next] = time[state] + 1;
                queue[tail++] = next;
            }
        }
        return -1;
    }
}
