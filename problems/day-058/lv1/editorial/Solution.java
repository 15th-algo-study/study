class Solution {
    public int solution(String[] canvas, int r, int c) {
        int rows = canvas.length, cols = canvas[0].length();
        char target = canvas[r].charAt(c);
        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};
        boolean[] visited = new boolean[rows * cols];
        int[] queue = new int[rows * cols]; // 각 픽셀은 한 번만 들어가므로 배열 큐로 충분
        int head = 0, tail = 0;
        queue[tail++] = r * cols + c;
        visited[r * cols + c] = true;
        while (head < tail) {
            int cur = queue[head++];
            int cr = cur / cols, cc = cur % cols;
            for (int d = 0; d < 4; d++) {
                int nr = cr + dr[d], nc = cc + dc[d];
                if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) continue;
                if (canvas[nr].charAt(nc) != target) continue;
                int next = nr * cols + nc;
                if (visited[next]) continue;
                visited[next] = true; // 넣을 때 표시
                queue[tail++] = next;
            }
        }
        return tail; // 큐에 들어간 픽셀 수
    }
}
