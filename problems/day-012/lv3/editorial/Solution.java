import java.util.HashMap;
import java.util.Map;

class Solution {
    public int solution(String beads) {
        int n = beads.length();
        Map<Long, Integer> firstSeen = new HashMap<>(); // (r-g, r-b) 키 → 처음 나온 위치
        firstSeen.put(key(0, 0, n), -1);
        int r = 0, g = 0, b = 0, best = 0;
        for (int i = 0; i < n; i++) {
            switch (beads.charAt(i)) {
                case 'R' -> r++;
                case 'G' -> g++;
                default -> b++;
            }
            long key = key(r - g, r - b, n);
            Integer first = firstSeen.putIfAbsent(key, i); // 처음 본 위치만 남긴다
            if (first != null) best = Math.max(best, i - first);
        }
        return best;
    }

    /** 두 차이(각각 [-n, n])를 long 하나로 합친다. */
    private static long key(int d1, int d2, int n) {
        return (long) (d1 + n) * (2L * n + 1) + (d2 + n);
    }
}
