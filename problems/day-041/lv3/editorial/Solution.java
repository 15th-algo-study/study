class Solution {
    public int solution(int[] colors, int k) {
        int[] cnt = new int[500_001];
        int l = 0, maxFreq = 0, best = 0;
        for (int r = 0; r < colors.length; r++) {
            maxFreq = Math.max(maxFreq, ++cnt[colors[r]]);
            // 창 안에서 바꿔야 하는 개수 = 길이 - 최다 색 개수. k 를 넘으면 왼쪽 끝을 올린다.
            // maxFreq 는 줄이지 않는다: 과거에 달성한 값이라 답을 부풀리지 않는다.
            while (r - l + 1 - maxFreq > k) {
                cnt[colors[l++]]--;
            }
            best = Math.max(best, r - l + 1);
        }
        return best;
    }
}
