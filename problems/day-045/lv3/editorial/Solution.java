class Solution {
    public int solution(int[] scores, int k) {
        int lo = 1, hi = 0;
        for (int s : scores) hi = Math.max(hi, s);
        while (lo < hi) {                       // 참인 최대 X 를 찾는다
            int mid = lo + (hi - lo + 1) / 2;
            if (exists(scores, k, mid)) lo = mid;
            else hi = mid - 1;
        }
        return lo;
    }

    /** 길이 k 이상이면서 중앙값이 x 이상인 구간이 있는가 — (+1/-1 변환) 합이 양수인 구간 찾기 */
    private boolean exists(int[] scores, int k, int x) {
        int n = scores.length;
        int[] prefix = new int[n + 1];
        for (int i = 0; i < n; i++) prefix[i + 1] = prefix[i] + (scores[i] >= x ? 1 : -1);
        int minPrefix = Integer.MAX_VALUE;
        for (int r = k; r <= n; r++) {
            minPrefix = Math.min(minPrefix, prefix[r - k]); // 왼쪽 끝 후보: 길이가 k 이상이 되는 것만
            if (prefix[r] - minPrefix > 0) return true;
        }
        return false;
    }
}
