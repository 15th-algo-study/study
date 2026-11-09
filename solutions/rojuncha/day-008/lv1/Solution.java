import java.util.*;

class Solution {
    public String[] solution(String text, int width) {
        
        List<String> list = new ArrayList<>();
        StringBuilder sb =new StringBuilder();

        for(String s : text.split(" ")){
            if(sb.isEmpty()){
                sb.append(s);
            }else if (sb.length() +1 +s.length() <= width){
                sb.append(" ").append(s);
            }else{
                list.add(sb.toString());
                sb.setLength(0);
                sb.append(s);
            }

        }
        list.add(sb.toString());
        return list.toArray(new String[0]);
    }
}
