import java.util.Arrays;

class Solution {
    public int solution(int[] heights) {
        Arrays.sort(heights); 
        int best = Integer.MAX_VALUE;
        for (int i = 0; i + 1 < heights.length; i++) {
            best = Math.min(best, heights[i + 1] - heights[i]);
        }
        return best;
    }
}

