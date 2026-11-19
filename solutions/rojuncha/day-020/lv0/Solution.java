import java.util.*;

class Solution {
    public int[][] solution(int[][] stars) {
        int[][] answer = stars.clone();
        Arrays.sort(answer,(a,b)->{
            if(a[0] != b[0])return Integer.compare(a[0],b[0]);
            return Integer.compare(a[1],b[1]);
        });
        
        return answer;
    }
}
