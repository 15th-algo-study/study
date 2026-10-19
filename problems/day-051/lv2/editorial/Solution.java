import java.util.Collections;
import java.util.PriorityQueue;

class Solution {
    public int[] solution(int[] bids) {
        PriorityQueue<Integer> low = new PriorityQueue<>(Collections.reverseOrder()); // 작은 쪽 절반 (최대 힙)
        PriorityQueue<Integer> high = new PriorityQueue<>();                          // 큰 쪽 절반 (최소 힙)
        int[] answer = new int[bids.length];
        for (int i = 0; i < bids.length; i++) {
            int x = bids[i];
            if (low.isEmpty() || x <= low.peek()) low.offer(x);
            else high.offer(x);
            // low 가 high 와 같거나 하나 더 많게 유지한다
            if (low.size() > high.size() + 1) high.offer(low.poll());
            else if (low.size() < high.size()) low.offer(high.poll());
            answer[i] = low.peek(); // 개수가 짝수면 가운데 두 값 중 작은 쪽
        }
        return answer;
    }
}
