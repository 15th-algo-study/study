import java.util.PriorityQueue;

class Solution {
    public int[] solution(String ops, int[] urgency) {
        // [긴급도, 요청 번호]: 긴급도 큰 순, 같으면 번호 작은 순
        PriorityQueue<int[]> queue = new PriorityQueue<>((a, b) ->
                a[0] != b[0] ? Integer.compare(b[0], a[0]) : Integer.compare(a[1], b[1]));
        int processes = 0;
        for (int i = 0; i < ops.length(); i++) if (ops.charAt(i) == 'P') processes++;

        int[] answer = new int[processes];
        int number = 0, out = 0;
        for (int i = 0; i < ops.length(); i++) {
            if (ops.charAt(i) == 'A') {
                queue.offer(new int[]{urgency[i], ++number});
            } else {
                answer[out++] = queue.isEmpty() ? 0 : queue.poll()[1];
            }
        }
        return answer;
    }
}
