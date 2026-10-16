import java.util.Arrays;

class Solution {
    public long solution(int[] minutes, int[] penalties) {
        int n = minutes.length;
        Integer[] order = new Integer[n];
        for (int i = 0; i < n; i++) order[i] = i;

        // tA/wA < tB/wB  ⇔  tA*wB < tB*wA  (교차 곱셈으로 정수 비교)
        Arrays.sort(order, (a, b) ->
                Long.compare((long) minutes[a] * penalties[b], (long) minutes[b] * penalties[a]));

        long time = 0, total = 0;
        for (int idx : order) {
            time += minutes[idx];            // 이 주문의 완성 시각
            total += time * penalties[idx];  // 기다린 시간 × 단가
        }
        return total;
    }
}
