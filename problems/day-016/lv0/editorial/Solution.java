import java.util.Arrays;

class Solution {
    public int solution(int[] heights) {
        Arrays.sort(heights); // 오름차순 정렬 — 가장 가까운 두 값은 반드시 이웃한다
        int best = Integer.MAX_VALUE;
        for (int i = 0; i + 1 < heights.length; i++) {
            best = Math.min(best, heights[i + 1] - heights[i]);
        }
        return best;
    }
}
