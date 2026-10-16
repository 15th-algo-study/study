import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

class Solution {
    public int[] solution(int[] events) {
        Deque<Integer> queue = new ArrayDeque<>();
        List<Integer> entered = new ArrayList<>();
        for (int e : events) {
            if (e > 0) {
                queue.offer(e);                      // 맨 뒤에 선다
            } else if (!queue.isEmpty()) {
                entered.add(queue.poll());           // 맨 앞이 입장한다
            }
        }
        int[] answer = new int[entered.size()];
        for (int i = 0; i < answer.length; i++) answer[i] = entered.get(i);
        return answer;
    }
}
