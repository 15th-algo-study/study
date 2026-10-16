class Solution {
    public int solution(int[] power, long a, long b) {
        return Math.max(split(power, a, b), split(power, b, a));
    }

    /** 왼쪽 구간은 배터리 leftCap, 오른쪽 구간은 배터리 rightCap 으로 맡을 때의 최댓값 */
    private int split(int[] power, long leftCap, long rightCap) {
        int n = power.length;
        // bestLeft[s] = [0, s) 안에서 합이 leftCap 이하인 가장 긴 구간
        int[] bestLeft = new int[n + 1];
        long sum = 0;
        for (int l = 0, r = 0; r < n; r++) {
            sum += power[r];
            while (sum > leftCap) sum -= power[l++];
            bestLeft[r + 1] = Math.max(bestLeft[r], r - l + 1);
        }
        // bestRight[s] = [s, n) 안에서 합이 rightCap 이하인 가장 긴 구간
        int[] bestRight = new int[n + 1];
        sum = 0;
        for (int l = n - 1, r = n - 1; l >= 0; l--) {
            sum += power[l];
            while (sum > rightCap) sum -= power[r--];
            bestRight[l] = Math.max(bestRight[l + 1], r - l + 1);
        }
        int best = 0;
        for (int s = 0; s <= n; s++) {
            best = Math.max(best, bestLeft[s] + bestRight[s]);
        }
        return best;
    }
}
