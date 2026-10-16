import java.util.Arrays;

class Solution {
    public long solution(int[] weights, int k) {
        Arrays.sort(weights); // 오름차순 → 가장 무거운 k개는 배열 뒤쪽에 있다
        long sum = 0; // 10^9 × 200,000 은 int 범위를 넘는다
        for (int i = weights.length - k; i < weights.length; i++) {
            sum += weights[i];
        }
        return sum;
    }
}
