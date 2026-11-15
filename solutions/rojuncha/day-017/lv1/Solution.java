import java.util.*;

class Solution {
    public int solution(int[] pots, int[] saucers) {
        Arrays.sort(pots);
        Arrays.sort(saucers);
        int i = 0;
        int j = 0;
        int answer = 0;
         while (i < pots.length && j < saucers.length) {
            if (saucers[j] >= pots[i]) { 
                answer++;
                i++;
            }
            j++; 
        }
        return answer;
    }
}
