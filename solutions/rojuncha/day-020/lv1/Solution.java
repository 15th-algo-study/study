import java.util.*;

class Solution {
    public long solution(int[] sopranos, int[] altos) {
        long answer = 0;
        int size = sopranos.length;
        Arrays.sort(sopranos);
        Arrays.sort(altos);

        for(int i=0; i<size;i++){
            answer += Math.abs(sopranos[i]-altos[i]);
        }

        return answer;
    }
}
