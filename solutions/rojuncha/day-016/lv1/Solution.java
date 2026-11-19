import java.util.*;

class Solution {
    public int[] solution(int[][] catches) {
        Integer[] answer = new Integer[catches.length];
         for (int i = 0; i < catches.length; i++) {
                answer[i] = i;
            }

        Arrays.sort(answer,(a,b)->{
            if(catches[a][0] != catches[b][0]) return Integer.compare(catches[b][0], catches[a][0]);
            if(catches[a][1] != catches[b][1]) return Integer.compare(catches[b][1], catches[a][1]);
            return Integer.compare(a,b);
        });
        int[] answers = new int[catches.length];
        for(int i =0; i<catches.length;i++){
            answers[i] = answer[i];
        }
        return answers;
    }
}
