import java.util.Collections;
import java.util.PriorityQueue;

class Solution {
    public long solution(int[] coins, int k) {
        PriorityQueue<Integer> piles = new PriorityQueue<>(Collections.reverseOrder()); // 최대 힙
        for (int c : coins) piles.offer(c);
        long stolen = 0;
        for (int i = 0; i < k && piles.peek() > 0; i++) {
            int x = piles.poll();
            stolen += (x + 1) / 2; // 절반 올림을 훔친다
            piles.offer(x / 2);    // 절반 내림이 남는다
        }
        return stolen;
    }
}
