import java.util.Arrays;

class Solution {
    public int solution(int[] scoops, int target) {
        int inf = target + 1; // 답은 target 이하이므로 이 값은 "불가능"을 뜻한다
        int[] best = new int[target + 1];
        Arrays.fill(best, inf);
        best[0] = 0;
        for (int x = 1; x <= target; x++) {
            for (int s : scoops) {
                // 마지막에 s L 바가지를 부었다고 보면 그 전까지는 x - s L
                if (s <= x && best[x - s] + 1 < best[x]) best[x] = best[x - s] + 1;
            }
        }
        return best[target] == inf ? -1 : best[target];
    }
}
