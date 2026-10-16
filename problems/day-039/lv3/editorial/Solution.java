class Solution {
    public int solution(int[][] cost, long budget) {
        int rows = cost.length, cols = cost[0].length;
        int best = 0;
        long[] colSum = new long[cols];
        for (int top = 0; top < rows; top++) {
            java.util.Arrays.fill(colSum, 0);
            for (int bottom = top; bottom < rows; bottom++) {
                for (int c = 0; c < cols; c++) colSum[c] += cost[bottom][c]; // top..bottom 행의 열 합
                int height = bottom - top + 1;
                if ((long) height * cols <= best) continue; // 이 행 쌍으로는 기록을 못 깬다
                // colSum 에서 합이 budget 이하인 가장 긴 구간 (투 포인터)
                long sum = 0;
                int left = 0;
                for (int right = 0; right < cols; right++) {
                    sum += colSum[right];
                    while (sum > budget) sum -= colSum[left++];
                    best = Math.max(best, height * (right - left + 1));
                }
            }
        }
        return best;
    }
}
