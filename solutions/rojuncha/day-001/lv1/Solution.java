class Solution {
    public int solution(int[] heights) {
        
        int answer = 1;
        int len = 1;

        for (int i = 1; i < heights.length; i++) {
            len = heights[i - 1] < heights[i] ? len + 1 : 1;
            answer = Math.max(answer, len);
        }
        return answer;
    }
}
