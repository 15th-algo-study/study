import java.util.*;

class Solution {
    public String solution(String[] orders) {
        
        Map<String,Integer> map =new HashMap<>();
        for(String s: orders){
            map.merge(s, 1, Integer::sum);
        }
        String menu = orders[0];
        int count = 0;
        for(String s:orders){
            int nowNum = map.get(s);
            if(nowNum>count){
                count = nowNum;
                menu = s;
            }
        }
        return menu;
    }
    
}
