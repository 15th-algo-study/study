class Solution {
    public int solution(int[][] matrix) {
        int answer = 0;
        int matrixSize = matrix.length;
        
        for(int i=0;i<matrixSize;i++){
            answer += matrix[i][i];
            if(i == matrixSize-1-i){
                continue;
            }
            answer += matrix[i][matrixSize-1-i];
            System.out.println(matrix[i][i]+" | "+matrix[matrixSize-1-i][matrixSize-1-i]);
        }
        
        return answer;
    }
}
