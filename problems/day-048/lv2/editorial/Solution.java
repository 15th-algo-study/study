import java.util.Arrays;

class Solution {
    public long solution(int[] cards, int k) {
        Arrays.sort(cards);
        // 1) 절댓값이 큰 음수부터 뒤집는다
        for (int i = 0; i < cards.length && k > 0 && cards[i] < 0; i++) {
            cards[i] = -cards[i];
            k--;
        }
        long sum = 0;
        int smallest = Integer.MAX_VALUE;        // 뒤집은 뒤 기준으로 절댓값이 가장 작은 카드
        for (int card : cards) {
            sum += card;
            smallest = Math.min(smallest, card); // 횟수가 남아 있다면 이 시점의 카드는 모두 0 이상이다
        }
        // 2) 남은 횟수가 홀수면 가장 작은 카드 하나를 뒤집은 채 끝내야 한다
        if (k % 2 == 1) sum -= 2L * smallest;
        return sum;
    }
}
