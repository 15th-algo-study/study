import java.util.HashMap;
import java.util.Map;

class Solution {
    private final Map<Long, Long> memo = new HashMap<>(); // 크기 → 최종 세포 수

    public long[] solution(long[] queries) {
        long[] answer = new long[queries.length];
        for (int i = 0; i < queries.length; i++) answer[i] = cells(queries[i]);
        return answer;
    }

    private long cells(long x) {
        if (x <= 2) return 1;
        Long saved = memo.get(x);
        if (saved != null) return saved; // 이미 구한 크기
        long result = (x % 3 == 0)
                ? 3 * cells(x / 3)
                : cells(x / 2) + cells(x - x / 2); // 내림 + 올림
        memo.put(x, result);
        return result;
    }
}
