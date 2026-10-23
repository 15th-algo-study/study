import java.util.*;

class Solution {
    public int solution(String s) {
       int count = 0;
        for (char c : s.toLowerCase().toCharArray()) {
            if ("aeiou".indexOf(c) >= 0) count++;
        }
        return count;
    }
}
