import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Solution {
    public int[] solution(int[] items, int[][] queries) {
        // 품목 → 들어온 위치 목록 (앞에서부터 넣으므로 오름차순)
        Map<Integer, List<Integer>> positions = new HashMap<>();
        for (int i = 0; i < items.length; i++) {
            positions.computeIfAbsent(items[i], k -> new ArrayList<>()).add(i);
        }

        int[] answer = new int[queries.length];
        for (int k = 0; k < queries.length; k++) {
            int l = queries[k][0], r = queries[k][1], x = queries[k][2];
            List<Integer> pos = positions.get(x);
            if (pos == null) continue;
            answer[k] = lowerBound(pos, r + 1) - lowerBound(pos, l); // [l, r] 안의 위치 수
        }
        return answer;
    }

    /** value 이상인 첫 원소의 인덱스 (= value 미만인 원소의 개수) */
    private static int lowerBound(List<Integer> list, int value) {
        int lo = 0, hi = list.size();
        while (lo < hi) {
            int mid = (lo + hi) >>> 1;
            if (list.get(mid) < value) lo = mid + 1;
            else hi = mid;
        }
        return lo;
    }
}
