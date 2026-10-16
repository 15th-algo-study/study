import java.util.HashMap;
import java.util.Map;

class Solution {
    public String solution(String[] names, int[] points) {
        Map<String, Long> total = new HashMap<>(); // 합이 int 를 넘으므로 Long
        for (int i = 0; i < names.length; i++) {
            total.merge(names[i], (long) points[i], Long::sum);
        }

        String best = null;
        long bestTotal = -1;
        for (Map.Entry<String, Long> e : total.entrySet()) {
            long sum = e.getValue();
            String name = e.getKey();
            if (sum > bestTotal || (sum == bestTotal && name.compareTo(best) < 0)) {
                bestTotal = sum;
                best = name;
            }
        }
        return best;
    }
}
