import java.util.*;

class Solution {
    public int[] solution(int[] items, String[] commands) {
        
        int itemlength = items.length;
        List<Integer> list = new ArrayList<>();
        int start = 0;
        for(String command :commands){
            char a = command.charAt(0);
            int num = Integer.parseInt(command.substring(2));
            if(a =='L'){
                start = (start+num)%itemlength;
            }else if(a=='R'){
                start = ((start - num) % itemlength + itemlength) % itemlength;
            }else if(a=='Q'){
                list.add(items[(start+num)%itemlength]);
            }
        }
        int []answer = new int[list.size()];
        for(int i=0; i<list.size();i++){
            answer[i] = list.get(i);
        }

        return answer;
    }
}
