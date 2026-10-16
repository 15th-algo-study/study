import java.util.ArrayDeque;
import java.util.Deque;

class Solution {
    public int[] solution(int[] priority, int[] general, int k) {
        Deque<Integer> priorityLine = new ArrayDeque<>();
        Deque<Integer> generalLine = new ArrayDeque<>();
        for (int ticket : priority) priorityLine.offer(ticket);
        for (int ticket : general) generalLine.offer(ticket);

        int[] answer = new int[priority.length + general.length];
        int idx = 0;
        while (!priorityLine.isEmpty() || !generalLine.isEmpty()) {
            // 우대 줄에서 최대 k명 — 비면 바로 멈춰야 헛돌지 않는다
            for (int i = 0; i < k && !priorityLine.isEmpty(); i++) answer[idx++] = priorityLine.poll();
            if (!generalLine.isEmpty()) answer[idx++] = generalLine.poll();
        }
        return answer;
    }
}
