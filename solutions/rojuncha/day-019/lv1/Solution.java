import java.util.*;

class Solution {
    public int[] solution(int[] stamps) {
        Arrays.sort(stamps);
        int total = 1, cur = 1, max = 1;
        for (int i = 1; i < stamps.length; i++) {
            if (stamps[i] == stamps[i - 1]) continue;      
            if (stamps[i] == stamps[i - 1] + 1) cur++;     
            else { total++; cur = 1; }                     
            max = Math.max(max, cur);
        }
        return new int[]{total, max};
    }
}