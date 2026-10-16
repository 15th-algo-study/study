import java.util.HashMap;
import java.util.Map;

class Solution {
    /** 브랜드마다 지금까지 본 매장 수와 위치 합 */
    private static final class Acc {
        long count = 0, sum = 0;
    }

    public long solution(int[] positions, String[] brands) {
        Map<String, Acc> byBrand = new HashMap<>();
        long answer = 0;
        for (int j = 0; j < positions.length; j++) {
            Acc acc = byBrand.computeIfAbsent(brands[j], k -> new Acc());
            long p = positions[j];
            answer += p * acc.count - acc.sum; // 왼쪽 같은 브랜드 매장들과의 거리 합
            acc.count++;
            acc.sum += p;
        }
        return answer;
    }
}
