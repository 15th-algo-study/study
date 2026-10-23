class Solution {
    public int solution(int[][] matrix) {
        int answer = 0;

        int row = matrix.length;

        for(int i=0; i<row; i++){
            answer += matrix[i][i];
            if(i != row-i-1){
                answer += matrix[i][row-i-1];
            }
        }
        
        return answer;
    }
}
