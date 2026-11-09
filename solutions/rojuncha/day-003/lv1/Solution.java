class Solution {
    private static final int[] DR = {-1, -1, -1, 0, 0, 1, 1, 1};
    private static final int[] DC = {-1, 0, 1, -1, 1, -1, 0, 1};

    public String[] solution(String[] board) {
        String[] answer = new String[board.length];
         int rows = board.length;
         int cols = board[0].length();
        
        for(int i=0; i<board.length; i++){
            StringBuilder sb = new StringBuilder();
            for(int j=0; j<board[i].length();j++){
                char c = board[i].charAt(j);
                if (c =='*'){
                    sb.append('*');
                    continue;
                }
                int count =0;
                for (int d = 0; d < 8; d++) {
                    int nr = i + DR[d], nc = j + DC[d];
                    if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && board[nr].charAt(nc) == '*') {
                        count++;
                    }
                }
                sb.append((char) ('0' + count));
            }
             answer[i] = sb.toString();
        }

        return answer;
    }
}
