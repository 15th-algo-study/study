import java.util.HashMap;
import java.util.Map;

class Solution {
    public long solution(String[] blocks) {
        Map<Integer, Integer> count = new HashMap<>(); // 홀짝 마스크 → 묶음 수
        for (String block : blocks) {
            int mask = 0;
            for (int i = 0; i < block.length(); i++) mask ^= 1 << (block.charAt(i) - 'a');
            count.merge(mask, 1, Integer::sum);
        }

        long answer = 0;
        for (Map.Entry<Integer, Integer> e : count.entrySet()) {
            int mask = e.getKey();
            long c = e.getValue();
            answer += c * (c - 1) / 2;                 // 마스크가 같은 쌍: 합치면 모든 글자가 짝수 개
            for (int k = 0; k < 26; k++) {
                int other = mask ^ (1 << k);           // 글자 하나만 홀짝이 다른 쌍
                if (other > mask) {                    // 두 번 세지 않도록 한쪽에서만
                    answer += c * count.getOrDefault(other, 0);
                }
            }
        }
        return answer;
    }
}
