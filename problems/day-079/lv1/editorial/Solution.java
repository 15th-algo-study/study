class Solution {
    public int[] solution(int n, int[][] cables, int[][] queries) {
        int[][] cap = new int[n + 1][n + 1]; // 0 = 경로 없음
        for (int[] c : cables) {             // 같은 쌍이면 큰 대역폭만
            cap[c[0]][c[1]] = Math.max(cap[c[0]][c[1]], c[2]);
            cap[c[1]][c[0]] = Math.max(cap[c[1]][c[0]], c[2]);
        }
        for (int k = 1; k <= n; k++) {
            for (int i = 1; i <= n; i++) {
                int ik = cap[i][k];
                if (ik == 0) continue;
                int[] rowI = cap[i], rowK = cap[k];
                for (int j = 1; j <= n; j++) {
                    int via = Math.min(ik, rowK[j]); // i → k → j 경로의 대역폭
                    if (via > rowI[j]) rowI[j] = via;
                }
            }
        }
        int[] answer = new int[queries.length];
        for (int q = 0; q < queries.length; q++) answer[q] = cap[queries[q][0]][queries[q][1]];
        return answer;
    }
}
