class Solution {
    public int[] solution(String[] garden) {
        int rows = garden.length, cols = garden[0].length();
        int[] day = new int[rows * cols];
        int[] queue = new int[rows * cols];
        int head = 0, tail = 0;
        java.util.Arrays.fill(day, -1);
        // 처음 이끼 칸을 모두 0일째로 큐에 넣는다 (다중 시작점)
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (garden[r].charAt(c) == 'M') {
                    day[r * cols + c] = 0;
                    queue[tail++] = r * cols + c;
                }
            }
        }

        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};
        int lastDay = 0;
        while (head < tail) {
            int cur = queue[head++];
            int r = cur / cols, c = cur % cols;
            for (int d = 0; d < 4; d++) {
                int nr = r + dr[d], nc = c + dc[d];
                if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) continue;
                int next = nr * cols + nc;
                if (garden[nr].charAt(nc) != '.' || day[next] != -1) continue;
                day[next] = day[cur] + 1; // 하루 늦게 덮인다
                lastDay = Math.max(lastDay, day[next]);
                queue[tail++] = next;
            }
        }

        int untouched = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (garden[r].charAt(c) == '.' && day[r * cols + c] == -1) untouched++;
            }
        }
        return new int[]{lastDay, untouched};
    }
}
