class Solution {
    public int solution(int[][] pads) {
        int rows = pads.length, cols = pads[0].length;
        int goal = rows * cols - 1;
        int[] jumps = new int[rows * cols];
        java.util.Arrays.fill(jumps, -1);
        int[] queue = new int[rows * cols];
        int head = 0, tail = 0;
        jumps[0] = 0;
        queue[tail++] = 0;
        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};
        while (head < tail) {
            int cur = queue[head++];
            if (cur == goal) return jumps[cur];
            int r = cur / cols, c = cur % cols;
            int len = pads[r][c]; // 이 연잎에서 뛰는 거리
            if (len == 0) continue;
            for (int d = 0; d < 4; d++) {
                int nr = r + dr[d] * len, nc = c + dc[d] * len;
                if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) continue;
                int next = nr * cols + nc;
                if (jumps[next] != -1) continue;
                jumps[next] = jumps[cur] + 1;
                queue[tail++] = next;
            }
        }
        return -1;
    }
}
