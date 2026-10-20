import java.util.ArrayList;
import java.util.List;

class Solution {
    private int rows, cols;
    private char[][] map;
    private int[][] table; // table[i][j] = 지점 i 에서 지점 j 까지의 최소 이동 횟수
    private int shops, best;

    public int solution(String[] market) {
        rows = market.length;
        cols = market[0].length();
        map = new char[rows][];
        int start = -1, exit = -1;
        List<Integer> shopCells = new ArrayList<>();
        for (int r = 0; r < rows; r++) {
            map[r] = market[r].toCharArray();
            for (int c = 0; c < cols; c++) {
                if (map[r][c] == 'S') start = r * cols + c;
                else if (map[r][c] == 'E') exit = r * cols + c;
                else if (map[r][c] == 'P') shopCells.add(r * cols + c);
            }
        }
        // 지점 번호: 0 = 입구, 1..shops = 가게, shops+1 = 출구
        shops = shopCells.size();
        int[] points = new int[shops + 2];
        points[0] = start;
        for (int i = 0; i < shops; i++) points[i + 1] = shopCells.get(i);
        points[shops + 1] = exit;

        // 입구와 가게마다 BFS 한 번씩 → 지점 사이 거리표
        table = new int[shops + 2][shops + 2];
        for (int i = 0; i <= shops; i++) {
            int[] dist = bfs(points[i]);
            for (int j = 0; j < shops + 2; j++) {
                if (dist[points[j]] == -1) return -1; // 들를 수 없는 가게나 출구가 있다
                table[i][j] = dist[points[j]];
            }
        }

        best = Integer.MAX_VALUE;
        visit(0, 0, 0);
        return best;
    }

    /** 지금 지점 cur, 들른 가게 집합 used, 지금까지 이동 횟수 sum 에서 남은 순서를 모두 시도 */
    private void visit(int cur, int used, int sum) {
        if (sum >= best) return;
        if (used == (1 << shops) - 1) {
            best = Math.min(best, sum + table[cur][shops + 1]);
            return;
        }
        for (int s = 0; s < shops; s++) {
            if ((used & (1 << s)) != 0) continue;
            visit(s + 1, used | (1 << s), sum + table[cur][s + 1]);
        }
    }

    private int[] bfs(int from) {
        int[] dist = new int[rows * cols];
        java.util.Arrays.fill(dist, -1);
        int[] queue = new int[rows * cols];
        int head = 0, tail = 0;
        dist[from] = 0;
        queue[tail++] = from;
        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};
        while (head < tail) {
            int cur = queue[head++];
            int r = cur / cols, c = cur % cols;
            for (int d = 0; d < 4; d++) {
                int nr = r + dr[d], nc = c + dc[d];
                if (nr < 0 || nr >= rows || nc < 0 || nc >= cols || map[nr][nc] == '#') continue;
                int next = nr * cols + nc;
                if (dist[next] != -1) continue;
                dist[next] = dist[cur] + 1;
                queue[tail++] = next;
            }
        }
        return dist;
    }
}
