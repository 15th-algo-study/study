import java.util.Arrays;
import java.util.Collections;
import java.util.PriorityQueue;

class Solution {
    public long solution(int fund, int k, int[][] sites) {
        int[][] byReq = sites.clone();
        Arrays.sort(byReq, (a, b) -> Integer.compare(a[0], b[0])); // 필요 자금 오름차순
        PriorityQueue<Integer> gains = new PriorityQueue<>(Collections.reverseOrder()); // 지금 가능한 유적의 수익 (최대 힙)
        long money = fund;
        int p = 0;
        for (int dig = 0; dig < k; dig++) {
            while (p < byReq.length && byReq[p][0] <= money) gains.offer(byReq[p++][1]); // 새로 가능해진 유적
            if (gains.isEmpty()) break;    // 더는 발굴할 곳이 없다
            money += gains.poll();         // 가능한 것 중 가장 많이 버는 곳
        }
        return money;
    }
}
