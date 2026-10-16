class Solution {
    public int solution(int[][] matrix) {
        int n = matrix.length;
        int answer = 0;
        for (int i = 0; i < n; i++) {
            answer += matrix[i][i];
            if (i != n - 1 - i) answer += matrix[i][n - 1 - i]; // 가운데 칸 중복 방지
        }
        return answer;
    }
}
