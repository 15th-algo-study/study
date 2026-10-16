class Solution {
    public int solution(int[] prices, long budget) {
        int n = prices.length;
        int best = 0;
        long sum = 0;
        int left = 0;
        for (int right = 0; right < 2 * n; right++) { // 두 바퀴를 직선처럼 훑는다
            sum += prices[right % n];
            while (sum > budget) {               // 예산 초과면 왼쪽을 버린다
                sum -= prices[left++ % n];
            }
            if (right - left + 1 > n) {          // 한 바퀴(n 개)를 넘지 않게
                sum -= prices[left++ % n];
            }
            best = Math.max(best, right - left + 1);
        }
        return best;
    }
}
