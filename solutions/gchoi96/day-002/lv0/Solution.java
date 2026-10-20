import java.util.*;

class Solution {
    public int solution(String s) {
        int answer = 0;
        Set<Character> vowel = new HashSet<>(List.of('A', 'E', 'I', 'O', 'U'));
        for(char ch : s.toUpperCase().toCharArray()){
            if(vowel.contains(ch))
            answer++;
        }
        return answer;
    }
}
