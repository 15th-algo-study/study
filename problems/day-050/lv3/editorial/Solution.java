import java.util.Arrays;
import java.util.TreeMap;

class Solution {
    public int solution(int[][] works, int k) {
        int n = works.length;
        long[][] spans = new long[n][2];
        for (int i = 0; i < n; i++) {
            spans[i][0] = works[i][0];
            spans[i][1] = (long) works[i][0] + works[i][1];
        }
        Arrays.sort(spans, (a, b) -> Long.compare(a[1], b[1]));    // 끝나는 시각 오름차순

        TreeMap<Long, Integer> freeAt = new TreeMap<>();             // 가마가 비는 시각 -> 그 시각에 비는 가마 수
        freeAt.put(0L, k);

        int fired = 0;
        for (long[] span : spans) {
            Long slot = freeAt.floorKey(span[0]);                    // 시작 이하에서 가장 늦게 비는 가마
            if (slot == null) continue;                              // 받을 수 없는 작품
            if (freeAt.merge(slot, -1, Integer::sum) == 0) freeAt.remove(slot);
            freeAt.merge(span[1], 1, Integer::sum);                  // 이 가마는 이제 span[1] 에 빈다
            fired++;
        }
        return fired;
    }
}
