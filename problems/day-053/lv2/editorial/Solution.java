import java.util.PriorityQueue;

class Solution {
    public int[] solution(int[] arrive, int[] need, int m) {
        PriorityQueue<Integer> free = new PriorityQueue<>(); // 빈 충전기 번호
        for (int id = 1; id <= m; id++) free.offer(id);
        // 충전 중: [끝나는 시각, 번호] — 끝나는 시각, 같으면 번호 순
        PriorityQueue<long[]> busy = new PriorityQueue<>((a, b) ->
                a[0] != b[0] ? Long.compare(a[0], b[0]) : Long.compare(a[1], b[1]));

        int n = arrive.length;
        int[] answer = new int[n];
        for (int i = 0; i < n; i++) {
            while (!busy.isEmpty() && busy.peek()[0] <= arrive[i]) free.offer((int) busy.poll()[1]); // 이미 끝난 충전기 회수
            long start;
            int id;
            if (!free.isEmpty()) {
                id = free.poll();           // 빈 충전기 중 번호가 가장 작은 것
                start = arrive[i];
            } else {
                long[] first = busy.poll(); // 가장 먼저 비는 충전기를 그 시각에 바로 쓴다
                id = (int) first[1];
                start = first[0];
            }
            busy.offer(new long[]{start + need[i], id});
            answer[i] = id;
        }
        return answer;
    }
}
