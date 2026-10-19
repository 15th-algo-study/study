import java.util.Arrays;

class Solution {
    public int solution(int[] weights) {
        Arrays.sort(weights);
        int n = weights.length;
        int limit = 0;
        for (int i = 0, j = n - 1; i < j; i++, j--) {   // 가장 가벼운 사람 + 가장 무거운 사람
            limit = Math.max(limit, weights[i] + weights[j]);
        }
        return limit;
    }
}
