class Solution {
    public long solution(int[] mags, long limit) {
        long answer = 0;
        long prod = 1; // 창 [l, r) 의 곱, 항상 limit 이하로 유지
        int l = 0;
        for (int r = 0; r < mags.length; r++) {
            long x = mags[r];
            if (x > limit) {           // 이 렌즈 하나만으로 초과 → 창을 비운다
                prod = 1;
                l = r + 1;
                continue;
            }
            while (prod > limit / x) { // prod * x > limit 을 오버플로 없이 판정
                prod /= mags[l++];
            }
            prod *= x;
            answer += r - l + 1;       // l..r 에서 시작해 r 에서 끝나는 구간들
        }
        return answer;
    }
}
