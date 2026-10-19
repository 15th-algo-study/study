import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

class Solution {
    private final PriorityQueue<Integer> low = new PriorityQueue<>(Collections.reverseOrder()); // 작은 쪽 절반
    private final PriorityQueue<Integer> high = new PriorityQueue<>();                          // 큰 쪽 절반
    private final Map<Integer, Integer> delayed = new HashMap<>(); // 값 → 아직 힙에 남은 "떠난" 개수
    private int lowSize = 0, highSize = 0;                         // 유효 원소 수 (쓰레기 제외)

    public int[] solution(int[] weights, int k) {
        int n = weights.length;
        int[] answer = new int[n - k + 1];
        for (int i = 0; i < n; i++) {
            add(weights[i]);
            if (i >= k) remove(weights[i - k]);
            if (i >= k - 1) answer[i - k + 1] = low.peek(); // 짝수면 가운데 두 값 중 작은 쪽
        }
        return answer;
    }

    private void add(int x) {
        if (low.isEmpty() || x <= low.peek()) { low.offer(x); lowSize++; }
        else { high.offer(x); highSize++; }
        rebalance();
    }

    private void remove(int x) {
        delayed.merge(x, 1, Integer::sum);
        if (x <= low.peek()) {           // x 는 low 에 들어 있다
            lowSize--;
            if (x == low.peek()) prune(low);
        } else {
            highSize--;
            if (x == high.peek()) prune(high);
        }
        rebalance();
    }

    /** 맨 위가 유효한 값이 될 때까지 쓰레기를 걷어 낸다 */
    private void prune(PriorityQueue<Integer> heap) {
        while (!heap.isEmpty()) {
            int top = heap.peek();
            Integer cnt = delayed.get(top);
            if (cnt == null) break;
            if (cnt == 1) delayed.remove(top); else delayed.put(top, cnt - 1);
            heap.poll();
        }
    }

    /** lowSize == highSize 또는 lowSize == highSize + 1 로 맞춘다 */
    private void rebalance() {
        if (lowSize > highSize + 1) {
            high.offer(low.poll()); lowSize--; highSize++;
            prune(low);
        } else if (lowSize < highSize) {
            low.offer(high.poll()); highSize--; lowSize++;
            prune(high);
        }
    }
}
