import java.util.HashMap;
import java.util.Map;

class Solution {
    public String[] solution(String[] votes) {
        Map<String, Integer> count = new HashMap<>();
        String leader = null;
        int top = 0;
        String[] answer = new String[votes.length];
        for (int i = 0; i < votes.length; i++) {
            int c = count.merge(votes[i], 1, Integer::sum);
            if (c > top) {           // 같은 표수면 먼저 도달한 현재 1위가 유지된다
                top = c;
                leader = votes[i];
            }
            answer[i] = leader;
        }
        return answer;
    }
}
