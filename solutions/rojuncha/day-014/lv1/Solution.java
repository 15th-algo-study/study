import java.util.*;

class Solution {
    public String solution(String[] names, int[] points) {
        String answer = "";
        Map<String, Long> map = new HashMap<>();
        
        for(int i=0; i<names.length;i++){
            map.merge(names[i],(long) points[i],Long::sum);
        }
        
        long maxNum = -1;
        for (String key : map.keySet()) {
            long value = map.get(key);
            if(value > maxNum || (value == maxNum && key.compareTo(answer) < 0)){
                maxNum = value;
                answer = key;
            }
        }

        return answer;
    }
}
