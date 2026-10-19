import java.util.PriorityQueue;

class Solution {
    public long solution(int[] herbs) {
        PriorityQueue<Long> heap = new PriorityQueue<>(); // 묶인 다발은 int 를 넘을 수 있다
        for (int w : herbs) heap.offer((long) w);
        long cost = 0;
        while (heap.size() > 1) {
            long merged = heap.poll() + heap.poll(); // 가장 가벼운 둘을 묶는다
            cost += merged;
            heap.offer(merged);                      // 묶인 다발도 다음 후보
        }
        return cost;
    }
}
