import java.util.ArrayDeque;
import java.util.Deque;

class Solution {
    public int[][] solution(int[] cups, int[][] moves) {
        @SuppressWarnings("unchecked")
        Deque<Integer>[] piles = new Deque[3];
        for (int j = 0; j < 3; j++) piles[j] = new ArrayDeque<>();
        for (int cup : cups) piles[0].addLast(cup); // 뒤쪽이 "맨 위"

        for (int[] m : moves) {
            Deque<Integer> from = piles[m[0]], to = piles[m[1]];
            for (int i = 0; i < m[2] && !from.isEmpty(); i++) {
                to.addLast(from.pollLast()); // 한 개씩 옮기므로 순서가 뒤집힌다
            }
        }

        int[][] answer = new int[3][];
        for (int j = 0; j < 3; j++) {
            answer[j] = new int[piles[j].size()];
            int i = 0;
            for (int cup : piles[j]) answer[j][i++] = cup; // 앞(아래)부터 순회
        }
        return answer;
    }
}
