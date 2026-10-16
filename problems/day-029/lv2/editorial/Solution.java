class Solution {
    private int[] prices;
    private int[] count; // count[c] = 총액이 c 인 구성의 수

    public int[] solution(int[] prices, int n, int[] budgets) {
        this.prices = prices;
        int maxPrice = 0;
        for (int p : prices) maxPrice = Math.max(maxPrice, p);
        int maxCost = maxPrice * n;
        count = new int[maxCost + 1];

        fill(0, n, 0); // 중복조합을 한 번만 열거

        int[] prefix = new int[maxCost + 1];
        prefix[0] = count[0];
        for (int c = 1; c <= maxCost; c++) prefix[c] = prefix[c - 1] + count[c];

        int[] answer = new int[budgets.length];
        for (int i = 0; i < budgets.length; i++) {
            answer[i] = prefix[Math.min(budgets[i], maxCost)];
        }
        return answer;
    }

    /** flavor 번 맛부터 배정한다. left 는 아직 담지 않은 개수, cost 는 지금까지의 총액 */
    private void fill(int flavor, int left, int cost) {
        if (flavor == prices.length - 1) {          // 마지막 맛은 남은 개수를 전부 받는다
            count[cost + left * prices[flavor]]++;
            return;
        }
        for (int c = 0; c <= left; c++) {
            fill(flavor + 1, left - c, cost + c * prices[flavor]);
        }
    }
}
