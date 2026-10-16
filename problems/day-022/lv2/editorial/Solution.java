import java.util.ArrayDeque;
import java.util.Deque;

class Solution {
    public int[] solution(int[][] events) {
        final int MAX_ID = 1_000_000;
        boolean[] waiting = new boolean[MAX_ID + 1];
        int[] latest = new int[MAX_ID + 1];   // 그 환자의 가장 최근 입장 사건 번호
        Deque<Integer> queue = new ArrayDeque<>(); // 입장 사건 번호를 넣는다

        int calls = 0;
        for (int[] e : events) if (e[0] == 3) calls++;
        int[] answer = new int[calls];
        int idx = 0;

        for (int i = 0; i < events.length; i++) {
            int type = events[i][0], id = events[i][1];
            if (type == 1) {
                if (waiting[id]) continue;          // 이미 대기 중이면 무시
                waiting[id] = true;
                latest[id] = i;
                queue.offer(i);
            } else if (type == 2) {
                waiting[id] = false;                // 표시만 해 둔다 (지연 삭제)
            } else {
                int called = -1;
                while (!queue.isEmpty()) {
                    int e = queue.poll();
                    int pid = events[e][1];
                    if (waiting[pid] && latest[pid] == e) { // 나갔거나 낡은 항목은 건너뛴다
                        waiting[pid] = false;
                        called = pid;
                        break;
                    }
                }
                answer[idx++] = called;
            }
        }
        return answer;
    }
}
