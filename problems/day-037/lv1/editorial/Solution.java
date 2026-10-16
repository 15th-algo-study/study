class Solution {
    public long[] solution(int n, int[][] watering) {
        long[] diff = new long[n + 1];
        for (int[] w : watering) {
            int s = w[0];
            int e = Math.min(n - 1, s + w[1] - 1); // 꽃밭 끝에서 자른다
            long amount = w[2];
            diff[s] += amount;      // 여기부터 켜고
            diff[e + 1] -= amount;  // 끝 다음에서 끈다
        }
        long[] water = new long[n];
        long running = 0;
        for (int i = 0; i < n; i++) {
            running += diff[i];
            water[i] = running;
        }
        return water;
    }
}
