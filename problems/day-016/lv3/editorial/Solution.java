import java.util.Arrays;

class Solution {
    public long solution(int[] heights, int cut, int extend) {
        int n = heights.length;
        Arrays.sort(heights);

        long[] prefix = new long[n + 1]; // prefix[i] = 앞 i개의 높이 합
        for (int i = 0; i < n; i++) prefix[i + 1] = prefix[i] + heights[i];

        long best = Long.MAX_VALUE;
        for (int i = 0; i < n; i++) {
            long t = heights[i]; // 최적 높이는 항상 말뚝 높이 중 하나
            long raise = t * i - prefix[i];                         // 앞쪽 i개를 t까지 늘리는 양
            long trim = (prefix[n] - prefix[i + 1]) - t * (n - i - 1); // 뒤쪽을 t까지 자르는 양
            long cost = raise * extend + trim * cut;
            best = Math.min(best, cost);
        }
        return best;
    }
}
