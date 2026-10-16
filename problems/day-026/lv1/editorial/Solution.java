class Solution {
    public int[] solution(int[] prices, int[] budgets) {
        int n = prices.length;
        int total = 0;
        for (int p : prices) total += p;

        // 1) 모든 조합(비트마스크)의 총액을 표시
        boolean[] reachable = new boolean[total + 1];
        for (int mask = 0; mask < (1 << n); mask++) {
            int sum = 0;
            for (int i = 0; i < n; i++) {
                if ((mask >> i & 1) == 1) sum += prices[i];
            }
            reachable[sum] = true;
        }

        // 2) best[b] = b원 이하로 만들 수 있는 최대 총액
        int[] best = new int[total + 1];
        for (int b = 1; b <= total; b++) {
            best[b] = reachable[b] ? b : best[b - 1];
        }

        // 3) 손님마다 표 조회
        int[] answer = new int[budgets.length];
        for (int i = 0; i < budgets.length; i++) {
            answer[i] = best[Math.min(budgets[i], total)];
        }
        return answer;
    }
}
