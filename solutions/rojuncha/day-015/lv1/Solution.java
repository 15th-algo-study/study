import java.util.*;

class Solution {
    public int solution(int[][] darts) {
        int answer = 0;
        
        HashMap<Integer,Integer> map = new HashMap<>();
        for(int i=0; i<darts.length; i++){
            int first = darts[i][0];
            int second = darts[i][1];
            map.merge(first*100000000+second,1,Integer::sum);
        }
        for (int count : map.values()) {
            if (count >= 2) answer++;
        }
        return answer;
    }
}
