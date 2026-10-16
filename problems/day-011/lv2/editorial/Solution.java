import java.util.HashMap;
import java.util.Map;

class Solution {
    public long solution(int[] records, int target) {
        Map<Long, Integer> seen = new HashMap<>(); // 누적합 값 → 지금까지 나온 횟수
        seen.put(0L, 1);                           // 빈 접두사
        long prefix = 0, answer = 0;
        for (int value : records) {
            prefix += value;
            answer += seen.getOrDefault(prefix - target, 0); // 먼저 세고
            seen.merge(prefix, 1, Integer::sum);             // 그 다음에 넣는다
        }
        return answer;
    }
}
