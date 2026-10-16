import java.util.HashMap;
import java.util.Map;

class Solution {
    public long solution(int[] weights, int target) {
        Map<Integer, Integer> seen = new HashMap<>(); // 무게 → 앞에서 본 개수
        long answer = 0;
        for (int w : weights) {
            answer += seen.getOrDefault(target - w, 0); // 앞에 있는 짝의 수를 먼저 더하고
            seen.merge(w, 1, Integer::sum);             // 자신을 넣는다
        }
        return answer;
    }
}
