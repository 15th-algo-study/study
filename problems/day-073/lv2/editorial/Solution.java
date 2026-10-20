import java.util.ArrayList;
import java.util.List;

class Solution {
    public int solution(int[][] menus, int budget) {
        List<List<int[]>> corners = new ArrayList<>();
        for (int i = 0; i <= 1000; i++) corners.add(new ArrayList<>());
        for (int[] menu : menus) corners.get(menu[0]).add(menu); // 코너별로 묶는다

        int[] best = new int[budget + 1]; // best[b] = 가격 합 b 이하에서 만족도 최댓값
        for (List<int[]> corner : corners) {
            if (corner.isEmpty()) continue;
            int[] before = best.clone(); // 이 코너에서 아직 아무것도 고르지 않은 상태
            for (int[] menu : corner) {
                int price = menu[1], joy = menu[2];
                for (int b = price; b <= budget; b++) {
                    best[b] = Math.max(best[b], before[b - price] + joy); // 항상 before 를 본다 → 코너당 하나
                }
            }
        }
        return best[budget];
    }
}
