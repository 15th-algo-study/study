import java.util.Arrays;
import java.util.Collections;
import java.util.PriorityQueue;

class Solution {
    public int solution(int[][] jobs) {
        int[][] byDeadline = jobs.clone();
        Arrays.sort(byDeadline, (a, b) -> Integer.compare(a[1], b[1])); // 마감 오름차순으로 처리한다
        PriorityQueue<Integer> chosen = new PriorityQueue<>(Collections.reverseOrder()); // 고른 작업의 소요 시간 (최대 힙)
        long total = 0; // 고른 작업의 총 소요 시간
        for (int[] job : byDeadline) {
            chosen.offer(job[0]);
            total += job[0];
            if (total > job[1]) total -= chosen.poll(); // 마감을 넘기면 가장 오래 걸리는 것을 되돌려 뺀다
        }
        return chosen.size();
    }
}
