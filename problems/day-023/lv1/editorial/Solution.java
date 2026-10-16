import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

class Solution {
    public int[] solution(int[][] commands) {
        Deque<Integer> pile = new ArrayDeque<>();
        boolean reversed = false; // false: 뒤쪽이 맨 위, true: 앞쪽이 맨 위
        List<Integer> handled = new ArrayList<>();
        for (int[] c : commands) {
            switch (c[0]) {
                case 1 -> {
                    if (reversed) pile.addFirst(c[1]); else pile.addLast(c[1]);
                }
                case 2 -> {
                    if (pile.isEmpty()) handled.add(-1);
                    else handled.add(reversed ? pile.pollFirst() : pile.pollLast());
                }
                default -> reversed = !reversed; // 실제로 뒤집지 않고 방향만 바꾼다
            }
        }
        int[] answer = new int[handled.size()];
        for (int i = 0; i < answer.length; i++) answer[i] = handled.get(i);
        return answer;
    }
}
