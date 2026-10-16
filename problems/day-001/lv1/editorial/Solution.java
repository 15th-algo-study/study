class Solution {
    public int solution(int[] heights) {
        int best = 1;
        int run = 1; // heights[i] 에서 끝나는 오르막 구간의 길이
        for (int i = 1; i < heights.length; i++) {
            run = heights[i] > heights[i - 1] ? run + 1 : 1;
            best = Math.max(best, run);
        }
        return best;
    }
}
