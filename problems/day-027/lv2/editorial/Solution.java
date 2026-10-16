class Solution {
    public int[] solution(int[] weights, int[][] queries) {
        int n = weights.length;
        int total = 0;
        for (int w : weights) total += w;

        int[] sum = new int[1 << n];
        int[][] count = new int[n + 1][total + 1]; // count[r][t] = r개 골라 합이 t 인 가짓수
        count[0][0] = 1;                            // 아무것도 고르지 않는 경우
        for (int mask = 1; mask < (1 << n); mask++) {
            int low = Integer.numberOfTrailingZeros(mask);      // 가장 낮은 비트의 인덱스
            sum[mask] = sum[mask & (mask - 1)] + weights[low];   // 그 비트를 뺀 부분집합 + 그 돌
            count[Integer.bitCount(mask)][sum[mask]]++;
        }

        int[] answer = new int[queries.length];
        for (int i = 0; i < queries.length; i++) {
            int r = queries[i][0], t = queries[i][1];
            answer[i] = (r <= n && t <= total) ? count[r][t] : 0;
        }
        return answer;
    }
}
