import java.util.HashMap;
import java.util.Map;

class Solution {
    public int solution(String[] cards, String[] shops, int[] times, int limit) {
        Map<String, Integer> lastTime = new HashMap<>(); // "카드#가게" → 마지막 결제 시각
        int answer = 0;
        for (int i = 0; i < cards.length; i++) {
            String key = cards[i] + "#" + shops[i];        // 구분자가 있어야 조합이 섞이지 않는다
            Integer last = lastTime.put(key, times[i]);    // 이전 시각을 받고 지금 시각으로 갱신
            if (last != null && times[i] - last <= limit) answer++;
        }
        return answer;
    }
}
