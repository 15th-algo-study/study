import java.util.*;

class Solution {
    public long solution(int[] weights, int k) {
        long answer = 0;
        Arrays.sort(weights);

        for(int i =weights.length-k; i< weights.length; i++){
           answer += weights[i];
        }
        return answer;
    }
}
