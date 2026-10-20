class Solution {
    private static final int NEVER = Integer.MAX_VALUE; // 끝내 잠기지 않는 칸
    private int rows, cols, start;
    private char[][] map;
    private int[] flood;           // flood[칸] = 그 칸이 잠기는 시각
    private int[] arrive, queue;   // 탈출 확인용 BFS 배열 (재사용)
    private final int[] dr = {-1, 1, 0, 0};
    private final int[] dc = {0, 0, -1, 1};

    public int solution(String[] flat, int limit) {
        rows = flat.length;
        cols = flat[0].length();
        map = new char[rows][];
        int cells = rows * cols;
        flood = new int[cells];
        java.util.Arrays.fill(flood, NEVER);
        queue = new int[cells];
        arrive = new int[cells];
        int head = 0, tail = 0;
        for (int r = 0; r < rows; r++) {
            map[r] = flat[r].toCharArray();
            for (int c = 0; c < cols; c++) {
                if (map[r][c] == 'S') start = r * cols + c;
                if (map[r][c] == 'W') {
                    flood[r * cols + c] = 0;
                    queue[tail++] = r * cols + c;
                }
            }
        }
        // 1) 밀물: 모든 바닷물 칸에서 동시에 출발하는 BFS
        int lastFlood = 0;
        while (head < tail) {
            int cur = queue[head++];
            int r = cur / cols, c = cur % cols;
            for (int d = 0; d < 4; d++) {
                int nr = r + dr[d], nc = c + dc[d];
                if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) continue;
                char ch = map[nr][nc];
                int next = nr * cols + nc;
                if ((ch == '.' || ch == 'S') && flood[next] == NEVER) {
                    flood[next] = flood[cur] + 1;
                    lastFlood = flood[next];
                    queue[tail++] = next;
                }
            }
        }

        // 2) 늦게 떠날수록 불리하다(단조) → 떠나는 시각 w 를 이분 탐색
        int hi = flood[start] == NEVER ? limit : Math.min(limit, flood[start] - 1);
        if (canEscape(hi)) return hi;
        if (!canEscape(0)) return -1;
        // hi 에서 실패했다면 답은 마지막으로 잠기는 시각보다 작다 (그 뒤로는 잠기는 칸을 아예 못 쓰므로)
        hi = Math.min(hi, lastFlood);
        int lo = 0; // canEscape(lo) = true, canEscape(hi) = false
        while (hi - lo > 1) {
            int mid = lo + (hi - lo) / 2;
            if (canEscape(mid)) lo = mid;
            else hi = mid;
        }
        return lo;
    }

    /** w 분에 S 를 떠나 육지에 갈 수 있는가 (각 칸에 가장 일찍 도착하는 BFS) */
    private boolean canEscape(int w) {
        if (flood[start] <= w) return false;
        java.util.Arrays.fill(arrive, -1);
        int head = 0, tail = 0;
        arrive[start] = w;
        queue[tail++] = start;
        while (head < tail) {
            int cur = queue[head++];
            int r = cur / cols, c = cur % cols;
            long t = (long) arrive[cur] + 1; // 다음 칸에 도착하는 시각
            for (int d = 0; d < 4; d++) {
                int nr = r + dr[d], nc = c + dc[d];
                if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) continue;
                char ch = map[nr][nc];
                if (ch == 'L') return true;
                int next = nr * cols + nc;
                if ((ch != '.' && ch != 'S') || arrive[next] != -1) continue;
                if (flood[next] <= t) continue; // 물이 먼저(또는 동시에) 온다
                arrive[next] = (int) t;
                queue[tail++] = next;
            }
        }
        return false;
    }
}
