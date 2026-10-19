class Solution {
    public long solution(int[] gaps, long k) {
        long total = 0;
        for (int g : gaps) total += g;
        long lo = 1, hi = total;
        while (lo < hi) {
            long mid = lo + (hi - lo) / 2;
            if (countAtMost(gaps, mid) >= k) hi = mid; // X 이하인 쌍이 k 개 이상이면 답은 X 이하
            else lo = mid + 1;
        }
        return lo;
    }

    /** 합이 limit 이하인 연속 부분 배열의 개수 — 슬라이딩 윈도우 */
    private long countAtMost(int[] gaps, long limit) {
        long count = 0, sum = 0;
        int l = 0;
        for (int r = 0; r < gaps.length; r++) {
            sum += gaps[r];
            while (sum > limit) sum -= gaps[l++]; // 양수라 창을 줄이면 반드시 합이 준다
            count += r - l + 1;
        }
        return count;
    }
}
