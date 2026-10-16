import java.util.Arrays;

class Solution {
    public long solution(int[] weights, int limit) {
        Arrays.sort(weights);
        long count = 0; // 최대 n(n-1)/2 ≈ 1.25×10^11
        int lo = 0, hi = weights.length - 1;
        while (lo < hi) {
            if ((long) weights[lo] + weights[hi] <= limit) {
                count += hi - lo; // lo 는 lo+1..hi 모두와 짝이 된다
                lo++;
            } else {
                hi--; // 가장 가벼운 사람과도 안 되면 hi 는 누구와도 못 탄다
            }
        }
        return count;
    }
}
