class Solution {
    public long[] solution(int n, int[][] works) {
        long[] d2 = new long[n + 2]; // 2차 차분
        for (int[] w : works) {
            int s = w[0];
            int e = Math.min(n - 1, s + w[1] - 1); // 땅 끝에서 자른 마지막 칸
            long t = w[2];
            long len = e - s + 1;                  // 잘린 뒤의 실제 길이
            d2[s] += t;
            d2[e + 1] -= (len + 1) * t;
            d2[e + 2] += len * t;
        }
        long[] height = new long[n];
        long slope = 0;   // 1차 차분의 누적 (현재 칸에서 더해지는 양의 증가분)
        long current = 0; // 2차 누적 = 실제 높이
        for (int i = 0; i < n; i++) {
            slope += d2[i];
            current += slope;
            height[i] = current;
        }
        return height;
    }
}
