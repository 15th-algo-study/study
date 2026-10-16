class Solution {
    public long[] solution(int[] visitors, int k) {
        int n = visitors.length;
        long[] prefix = new long[n + 1]; // prefix[i] = visitors[0..i-1] 의 합
        for (int i = 0; i < n; i++) {
            prefix[i + 1] = prefix[i] + visitors[i];
        }
        long best = -1;
        int bestStart = 0;
        for (int s = 0; s + k <= n; s++) {
            long sum = prefix[s + k] - prefix[s]; // s 부터 k 일의 합
            if (sum > best) { // 같은 값이면 앞선 시작을 유지
                best = sum;
                bestStart = s;
            }
        }
        return new long[]{best, bestStart};
    }
}
