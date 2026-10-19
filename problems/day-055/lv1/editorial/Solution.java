import java.util.Arrays;
import java.util.PriorityQueue;

class Solution {
    public int solution(int[][] bookings) {
        int[][] sorted = bookings.clone();
        Arrays.sort(sorted, (a, b) -> Integer.compare(a[0], b[0]));
        PriorityQueue<Long> ends = new PriorityQueue<>(); // 사용 중인 가마들의 끝나는 시각
        int kilns = 0;
        for (int[] b : sorted) {
            long start = b[0], end = start + b[1];
            while (!ends.isEmpty() && ends.peek() <= start) ends.poll(); // 이미 끝난 가마는 다시 쓴다
            ends.offer(end);
            kilns = Math.max(kilns, ends.size()); // 지금 동시에 쓰이는 가마 수
        }
        return kilns;
    }
}
