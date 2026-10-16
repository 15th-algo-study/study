import java.util.HashMap;
import java.util.Map;

class Solution {
    public int solution(String[] movies) {
        Map<String, Integer> lastDay = new HashMap<>(); // 제목 → 마지막으로 본 날
        int best = 0;
        for (int day = 0; day < movies.length; day++) {
            Integer last = lastDay.put(movies[day], day); // 이전 값을 돌려주며 오늘로 갱신
            if (last != null) best = Math.max(best, day - last);
        }
        return best;
    }
}
