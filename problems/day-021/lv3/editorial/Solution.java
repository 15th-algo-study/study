import java.util.ArrayDeque;
import java.util.Deque;

class Solution {
    public long solution(int[] arrivals, int k, int s) {
        Deque<Long> starts = new ArrayDeque<>(); // 최근 k 명의 심사 시작 시각
        long totalWait = 0;
        for (int arrival : arrivals) {
            long start = arrival;
            if (starts.size() == k) {
                // k 명 앞 승객이 쓰던 심사대가 가장 먼저 빈다
                long free = starts.poll() + s;
                start = Math.max(start, free);
            }
            starts.offer(start);
            totalWait += start - arrival;
        }
        return totalWait;
    }
}
