class Solution {
    public long[] solution(int[][] time, int[] order) {
        int n = time.length;
        // dist[i][j]: 지금까지 개통한 역만 "거쳐서" i → j 로 가는 최단 시간 (끝점 i, j 는 개통 여부 무관)
        int[][] dist = new int[n][];
        for (int i = 0; i < n; i++) {
            dist[i] = time[i].clone();
            dist[i][i] = 0;
        }
        int[] mask = new int[n]; // 개통한 역이면 -1(모든 비트 1), 아니면 0
        long[] answer = new long[n];
        for (int t = 0; t < n; t++) {
            int k = order[t];
            mask[k] = -1;
            // 플로이드–워셜의 k 단계 한 번 = 경유지로 k 를 허용
            for (int i = 0; i < n; i++) relax(dist[i], dist[k], dist[i][k]);
            long sum = 0;
            for (int i = 0; i < n; i++) {
                if (mask[i] != 0) sum += openedSum(dist[i], mask); // 출발역도 개통한 역일 때만
            }
            answer[t] = sum; // dist[i][i] = 0 이라 자기 자신과의 쌍은 0
        }
        return answer;
    }

    /** rowI[j] = min(rowI[j], ik + rowK[j]) — i → k → j 로 돌아가는 편이 빠르면 바꾼다. */
    private static void relax(int[] rowI, int[] rowK, int ik) {
        for (int j = 0; j < rowI.length; j++) rowI[j] = Math.min(rowI[j], ik + rowK[j]);
    }

    /** 개통한 열만 더한다. v & -1 = v, v & 0 = 0. 한 행의 합은 최대 1,200 × 100,000 이라 int 로 충분. */
    private static int openedSum(int[] row, int[] mask) {
        int s = 0;
        for (int j = 0; j < row.length; j++) s += row[j] & mask[j];
        return s;
    }
}
