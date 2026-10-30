import java.util.*;

class Solution {
    public int solution(String[] movies) {

        Map<String, Integer> map = new HashMap<>();
        int answer = 0;

        for (int i = 0; i < movies.length; i++) {

            if (map.containsKey(movies[i])) {
                answer = Math.max(answer, i - map.get(movies[i]));
            }

            map.put(movies[i], i);
        }

        return answer;
    }
}