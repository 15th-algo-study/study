import java.util.*;

class Solution {
    public int solution(String s) {
        int answer = 0;
        int sSize = s.length();
        Map<String,Integer>map = new HashMap<>();
        map.put("a",0);
        map.put("e",0);
        map.put("i",0);
        map.put("o",0);
        map.put("u",0);        

        String sLow = s.toLowerCase();
        for(int i=0;i<sSize;i++){
            String key = String.valueOf(sLow.charAt(i));
            if(map.containsKey(key)){
                map.put(key,map.get(key)+1);
                answer++;
            }
                
        }
        //answer = map.get("a")+map.get("e")+map.get("i")+map.get("o")+map.get("u");

        return answer;
    }
}
