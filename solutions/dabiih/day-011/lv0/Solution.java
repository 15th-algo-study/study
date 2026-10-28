import java.util.*;
class Solution {
    public int[] solution(int[] temps) {
        int[] answer = new int[2];
        Map<Integer, Integer> map = new HashMap<>();

        for (int i=0 ; i<temps.length ; i++){
            if (!map.containsKey(temps[i])){map.put(temps[i],i);}
        }

        Arrays.sort(temps);
        answer[0] = map.get(temps[temps.length-1]);
        answer[1] = map.get(temps[0]);

        return answer;
    }
}
