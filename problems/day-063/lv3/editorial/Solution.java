class Solution {
    public int solution(String[] camp, int[] powers) {
        int rows = camp.length, cols = camp[0].length();
        int cells = rows * cols;
        char[][] map = new char[rows][];
        int[] coilCell = new int[powers.length];
        int k = 0;
        for (int r = 0; r < rows; r++) {
            map[r] = camp[r].toCharArray();
            for (int c = 0; c < cols; c++) if (map[r][c] == 'M') coilCell[k++] = r * cols + c;
        }

        // 세기를 칸 수로 잘라도 닿는 칸은 같다 (한 칸에서 다른 칸까지 cells 걸음 이상은 필요 없다)
        int maxPower = 0;
        int[] power = new int[powers.length];
        for (int i = 0; i < powers.length; i++) {
            power[i] = Math.min(powers[i], cells);
            maxPower = Math.max(maxPower, power[i]);
        }
        // 세기별 모기향 목록 (계수 정렬): byPower[p] 에 세기 p 인 모기향 번호들
        int[] startIdx = new int[maxPower + 2];
        for (int p : power) startIdx[p + 1]++;
        for (int p = 0; p <= maxPower; p++) startIdx[p + 1] += startIdx[p];
        int[] byPower = new int[powers.length];
        int[] fill = startIdx.clone();
        for (int i = 0; i < powers.length; i++) byPower[fill[power[i]]++] = i;

        // left[칸] = 그 칸에 닿은 연기의 남은 세기 중 최댓값 (-1: 아직 안 닿음)
        int[] left = new int[cells];
        java.util.Arrays.fill(left, -1);
        int[] cur = new int[cells], next = new int[cells];
        int curSize = 0, covered = 0;
        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};

        // 남은 세기가 큰 것부터 한 단계씩: 세기 level 인 모기향은 연기 물결의 남은 세기가 level 이 될 때 합류
        for (int level = maxPower; level >= 0; level--) {
            for (int j = startIdx[level]; j < startIdx[level + 1]; j++) {
                int cell = coilCell[byPower[j]];
                if (left[cell] != -1) continue; // 이미 더 센(같은) 연기가 닿은 칸
                left[cell] = level;
                covered++;
                cur[curSize++] = cell;
            }
            if (level == 0) break;
            int nextSize = 0;
            for (int i = 0; i < curSize; i++) {
                int r = cur[i] / cols, c = cur[i] % cols;
                for (int d = 0; d < 4; d++) {
                    int nr = r + dr[d], nc = c + dc[d];
                    if (nr < 0 || nr >= rows || nc < 0 || nc >= cols || map[nr][nc] == '#') continue;
                    int nb = nr * cols + nc;
                    if (left[nb] != -1) continue; // 처음 닿은 값이 곧 최댓값
                    left[nb] = level - 1;
                    covered++;
                    next[nextSize++] = nb;
                }
            }
            int[] tmp = cur; cur = next; next = tmp;
            curSize = nextSize;
        }
        return covered;
    }
}
