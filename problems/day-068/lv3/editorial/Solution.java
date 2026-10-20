class Solution {
    private static final int MOD = 1_000_000_007;

    public int[] solution(int rows, int cols, int[][] blocked, int[][] queries) {
        int cells = rows * cols;
        boolean[] wall = new boolean[cells];
        for (int[] b : blocked) wall[b[0] * cols + b[1]] = true;

        long[] from = new long[cells]; // 출발 → 칸 경로 수
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int id = r * cols + c;
                if (wall[id]) continue;
                if (r == 0 && c == 0) { from[id] = 1; continue; }
                long v = 0;
                if (r > 0) v += from[id - cols];
                if (c > 0) v += from[id - 1];
                from[id] = v % MOD;
            }
        }
        long[] to = new long[cells]; // 칸 → 도착 경로 수
        for (int r = rows - 1; r >= 0; r--) {
            for (int c = cols - 1; c >= 0; c--) {
                int id = r * cols + c;
                if (wall[id]) continue;
                if (r == rows - 1 && c == cols - 1) { to[id] = 1; continue; }
                long v = 0;
                if (r < rows - 1) v += to[id + cols];
                if (c < cols - 1) v += to[id + 1];
                to[id] = v % MOD;
            }
        }

        long total = from[cells - 1];
        int[] answer = new int[queries.length];
        for (int i = 0; i < queries.length; i++) {
            int id = queries[i][0] * cols + queries[i][1];
            long through = from[id] * to[id] % MOD; // 이 칸을 지나는 경로 수
            answer[i] = (int) ((total - through + MOD) % MOD);
        }
        return answer;
    }
}
