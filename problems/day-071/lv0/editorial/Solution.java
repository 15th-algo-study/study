class Solution {
    public int[][] solution(int[] positions) {
        int n = positions.length;
        int[][] table = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                table[i][j] = Math.abs(positions[i] - positions[j]); // 정렬되지 않았으니 절댓값
            }
        }
        return table;
    }
}
