import java.util.ArrayDeque;
import java.util.Deque;

class Solution {
    public int[] solution(int[] candies) {
        Deque<Integer> line = new ArrayDeque<>();
        for (int c : candies) line.addLast(c);

        int[] answer = new int[candies.length];
        int i = 0;
        while (!line.isEmpty()) {
            // 같으면 왼쪽 (<=). 하나만 남으면 양 끝이 같은 원소라 역시 왼쪽
            if (line.peekFirst() <= line.peekLast()) answer[i++] = line.pollFirst();
            else answer[i++] = line.pollLast();
        }
        return answer;
    }
}
