class Solution {
    private static final int INF = 1_000_000_000; // INF + 10,000 + INF 도 int 범위 안

    public int[] solution(int n, int[][] roads, int[][] candidates) {
        int[][] dist = new int[n][n];
        for (int i = 0; i < n; i++) {
            java.util.Arrays.fill(dist[i], INF);
            dist[i][i] = 0;
        }
        for (int[] r : roads) {
            dist[r[0]][r[1]] = Math.min(dist[r[0]][r[1]], r[2]);
            dist[r[1]][r[0]] = Math.min(dist[r[1]][r[0]], r[2]);
        }
        for (int k = 0; k < n; k++) {               // 기존 도로만으로 플로이드–워셜 한 번
            for (int i = 0; i < n; i++) {
                if (dist[i][k] != INF) relax(dist[i], dist[k], dist[i][k]);
            }
        }

        int[] answer = new int[candidates.length];
        for (int c = 0; c < candidates.length; c++) {
            int a = candidates[c][0], b = candidates[c][1], len = candidates[c][2];
            int[] fromA = dist[a], fromB = dist[b]; // 양방향이라 dist[x][a] = dist[a][x]
            int count = 0;
            for (int i = 0; i < n; i++) {
                // i → a → (새 도로) → b → j  또는  i → b → (새 도로) → a → j
                count += countImproved(dist[i], fromA, fromB, fromA[i] + len, fromB[i] + len, i + 1);
            }
            answer[c] = count;
        }
        return answer;
    }

    /** rowI[j] = min(rowI[j], ik + rowK[j]) : 경유지 k 를 허용 */
    private static void relax(int[] rowI, int[] rowK, int ik) {
        for (int j = 0; j < rowI.length; j++) rowI[j] = Math.min(rowI[j], ik + rowK[j]);
    }

    /** j ≥ from 인 j 중 새 도로를 거치는 길이 기존 최단 거리보다 짧은 개수 */
    private static int countImproved(int[] rowI, int[] fromA, int[] fromB, int ia, int ib, int from) {
        int count = 0;
        for (int j = from; j < rowI.length; j++) {
            if (Math.min(ia + fromB[j], ib + fromA[j]) < rowI[j]) count++;
        }
        return count;
    }
}
