import java.util.Arrays;

class Solution {
    public int solution(int[][] bookings) {
        int n = bookings.length;
        long[][] spans = new long[n][2];
        for (int i = 0; i < n; i++) {
            spans[i][0] = bookings[i][0];
            spans[i][1] = (long) bookings[i][0] + bookings[i][1]; // 끝 시각은 int 를 넘을 수 있다
        }
        Arrays.sort(spans, (a, b) -> Long.compare(a[1], b[1])); // 끝나는 시각 오름차순

        int accepted = 0;
        long last = Long.MIN_VALUE;                              // 마지막으로 받은 예약의 끝
        for (long[] span : spans) {
            if (span[0] >= last) {                               // 겹치지 않으면 받는다
                accepted++;
                last = span[1];
            }
        }
        return accepted;
    }
}
