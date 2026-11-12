import java.util.*;

class Solution {
    public int[] solution(String[] soccer, String[] band) {
        int[] answer = new int[3];

        Set<String> soccerSet = new HashSet<>(Arrays.asList(soccer));
        Set<String> bandSet = new HashSet<>(Arrays.asList(band));
        
        for(String s: soccerSet){
            if(bandSet.contains(s)){
                answer[2]+=1;
            }else
            answer[0]+=1;
        }
        for(String s: bandSet){
            if(!soccerSet.contains(s)){
                answer[1]+=1;
            }
        }

        return answer;
    }
}
