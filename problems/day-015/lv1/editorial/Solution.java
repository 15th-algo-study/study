import java.util.HashMap;
import java.util.Map;

class Solution {
    public int solution(int[][] darts) {
        Map<Long, Integer> count = new HashMap<>();
        for (int[] d : darts) {
            long key = d[0] * 1_000_000_001L + d[1]; // (x, y) → 겹치지 않는 long 키
            count.merge(key, 1, Integer::sum);
        }
        int answer = 0;
        for (int c : count.values()) {
            if (c >= 2) answer++;
        }
        return answer;
    }
}
