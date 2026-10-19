class Solution {
    public int solution(int[] charge, int target) {
        int best = Integer.MAX_VALUE;
        long sum = 0; // 창 [l, r] 의 합 (int 범위를 넘을 수 있다)
        int l = 0;
        for (int r = 0; r < charge.length; r++) {
            sum += charge[r];
            while (sum >= target) {            // 끝이 r 인 유효 구간을 가장 짧게 줄인다
                best = Math.min(best, r - l + 1);
                sum -= charge[l++];
            }
        }
        return best == Integer.MAX_VALUE ? -1 : best;
    }
}
