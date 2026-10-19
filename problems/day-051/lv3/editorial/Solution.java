import java.util.Arrays;
import java.util.Collections;
import java.util.PriorityQueue;

class Solution {
    public int solution(int dist, int water, int[][] oases) {
        int n = oases.length;
        int[][] sorted = Arrays.copyOf(oases, n + 1);
        sorted[n] = new int[]{dist, 0}; // 도시를 마지막 오아시스처럼 취급
        Arrays.sort(sorted, (a, b) -> Integer.compare(a[0], b[0]));

        PriorityQueue<Integer> passed = new PriorityQueue<>(Collections.reverseOrder()); // 지나친 오아시스의 물 (최대 힙)
        long supply = water;
        int stops = 0;
        for (int[] oasis : sorted) {
            while (supply < oasis[0]) {          // 다음 지점까지 물이 모자라면
                if (passed.isEmpty()) return -1; // 소급해서 받을 오아시스도 없다
                supply += passed.poll();         // 지나친 것 중 가장 큰 오아시스에서 멈춘 셈 친다
                stops++;
            }
            passed.offer(oasis[1]);
        }
        return stops;
    }
}
