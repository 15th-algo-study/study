import java.util.ArrayDeque;
import java.util.Deque;

class Solution {
    public int[] solution(int[] ops) {
        Deque<Integer> stack = new ArrayDeque<>(); // addLast 쪽이 "맨 위"
        for (int op : ops) {
            if (op > 0) {
                stack.addLast(op);
            } else if (!stack.isEmpty()) {
                stack.pollLast(); // 비어 있으면 무시
            }
        }
        int[] answer = new int[stack.size()];
        int i = 0;
        for (int plate : stack) answer[i++] = plate; // 앞(맨 아래)부터 순회
        return answer;
    }
}
