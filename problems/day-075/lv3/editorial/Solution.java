import java.util.HashMap;
import java.util.Map;

class Solution {
    public int solution(int[] routeA, int[] routeB) {
        Map<Integer, Integer> posB = new HashMap<>(routeB.length * 2);
        for (int j = 0; j < routeB.length; j++) posB.put(routeB[j], j); // 번호가 유일하므로 위치도 하나

        int[] tails = new int[routeA.length]; // tails[k] = 길이 k+1 증가 부분 수열의 가장 작은 끝값
        int len = 0;
        for (int island : routeA) {
            Integer p = posB.get(island);
            if (p == null) continue; // routeB 에 없는 섬은 공통 항로에 못 들어간다
            int lo = 0, hi = len;    // p 이상인 첫 위치
            while (lo < hi) {
                int mid = (lo + hi) >>> 1;
                if (tails[mid] < p) lo = mid + 1;
                else hi = mid;
            }
            tails[lo] = p;
            if (lo == len) len++;
        }
        return len;
    }
}
