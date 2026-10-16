import java.util.ArrayDeque;
import java.util.Deque;

class Solution {
    public int[] solution(int[] boxes) {
        Deque<Integer> line = new ArrayDeque<>();
        for (int box : boxes) {
            if (box % 2 == 1) line.addFirst(box); // 홀수는 왼쪽 끝
            else line.addLast(box);               // 짝수는 오른쪽 끝
        }
        int[] answer = new int[line.size()];
        int i = 0;
        for (int box : line) answer[i++] = box;
        return answer;
    }
}
