class Solution {
    public int solution(String[] tower) {
        int rows = tower.length, cols = tower[0].length();
        char[][] map = new char[rows][];
        int start = -1;
        int[] count = new int[26];
        for (int r = 0; r < rows; r++) {
            map[r] = tower[r].toCharArray();
            for (int c = 0; c < cols; c++) {
                char ch = map[r][c];
                if (ch == 'S') start = r * cols + c;
                else if (ch >= 'a' && ch <= 'z') count[ch - 'a']++;
            }
        }
        // 무늬별 마법진 칸 목록
        int[][] pads = new int[26][];
        for (int k = 0; k < 26; k++) pads[k] = new int[count[k]];
        int[] filled = new int[26];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                char ch = map[r][c];
                if (ch >= 'a' && ch <= 'z') pads[ch - 'a'][filled[ch - 'a']++] = r * cols + c;
            }
        }

        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};
        int[] dist = new int[rows * cols];
        java.util.Arrays.fill(dist, -1);
        boolean[] used = new boolean[26]; // 이 무늬의 순간이동을 이미 펼쳤는가
        int[] queue = new int[rows * cols];
        int head = 0, tail = 0;
        dist[start] = 0;
        queue[tail++] = start;
        while (head < tail) {
            int cur = queue[head++];
            int r = cur / cols, c = cur % cols;
            char ch = map[r][c];
            if (ch == 'T') return dist[cur];
            for (int d = 0; d < 4; d++) {
                int nr = r + dr[d], nc = c + dc[d];
                if (nr < 0 || nr >= rows || nc < 0 || nc >= cols || map[nr][nc] == '#') continue;
                int next = nr * cols + nc;
                if (dist[next] != -1) continue;
                dist[next] = dist[cur] + 1;
                queue[tail++] = next;
            }
            if (ch >= 'a' && ch <= 'z' && !used[ch - 'a']) {
                // 같은 무늬의 순간이동은 처음 한 번만 펼친다.
                // 나중에 같은 무늬 칸에서 펼쳐 봐야 거리가 더 줄지 않는다.
                used[ch - 'a'] = true;
                for (int next : pads[ch - 'a']) {
                    if (dist[next] != -1) continue;
                    dist[next] = dist[cur] + 1;
                    queue[tail++] = next;
                }
            }
        }
        return -1;
    }
}
