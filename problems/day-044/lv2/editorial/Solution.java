class Solution {
    public long solution(int[] cap, int[] trip, long volume) {
        int minTrip = Integer.MAX_VALUE;
        for (int t : trip) minTrip = Math.min(minTrip, t);
        long lo = 1, hi = volume * minTrip; // 가장 빠른 두레박 혼자서도 충분한 시각
        while (lo < hi) {
            long mid = lo + (hi - lo) / 2;
            if (enough(cap, trip, volume, mid)) hi = mid;
            else lo = mid + 1;
        }
        return lo;
    }

    /** 시각 T 까지 모인 물이 volume 이상인가 — 넘치지 않도록 도중에 끊는다 */
    private boolean enough(int[] cap, int[] trip, long volume, long T) {
        long water = 0;
        for (int i = 0; i < cap.length; i++) {
            water += (T / trip[i]) * cap[i];
            if (water >= volume) return true;
        }
        return false;
    }
}
