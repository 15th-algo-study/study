import java.util.*;

class Solution {
    public int solution(String[] movies) {
        int answer = 0;
        Map<String, Integer> map = new HashMap<>();

        for(int i=0; i<movies.length;i++){
            String s = movies[i];
            if(map.containsKey(s)){
                answer = Math.max(answer, i - map.get(s));
                map.replace(s,i);
            }
            map.put(s,i);
        }
        
        return answer;
    }
}
