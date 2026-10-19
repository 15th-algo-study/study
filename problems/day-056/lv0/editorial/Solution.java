class Solution {
    public long solution(int[][] quilt) {
        int rows = quilt.length, cols = quilt[0].length;
        long answer = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                boolean border = r == 0 || r == rows - 1 || c == 0 || c == cols - 1;
                if (border) answer += quilt[r][c]; // 칸마다 한 번만 판단하므로 모서리가 중복되지 않는다
            }
        }
        return answer;
    }
}
