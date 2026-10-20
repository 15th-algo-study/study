class Solution {
    public int solution(int[] heights) {
        int max = 1;
        int curr = 1;
        
        for(int i = 1; i < heights.length; i++){
            if(heights[i - 1] < heights[i]){
                curr++;
                continue;
            }

            max = Math.max(max, curr);
            curr = 1;
        }

        max = Math.max(max, curr);

        return max;
    }
}
