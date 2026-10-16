import java.util.HashMap;
import java.util.Map;

class Solution {
    public String solution(String[] orders) {
        Map<String, Integer> count = new HashMap<>();
        for (String menu : orders) count.merge(menu, 1, Integer::sum);

        String best = orders[0];
        int bestCount = 0;
        for (String menu : orders) {            // 주문 순서대로 보면 동률에서 먼저 주문된 것이 남는다
            int c = count.get(menu);
            if (c > bestCount) {
                bestCount = c;
                best = menu;
            }
        }
        return best;
    }
}
