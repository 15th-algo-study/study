import java.util.Arrays;
import java.util.PriorityQueue;

class Solution {
    public int[] solution(int[][] dates) {
        int k = dates.length;
        int[][] sorted = new int[k][];
        for (int i = 0; i < k; i++) {
            sorted[i] = dates[i].clone();
            Arrays.sort(sorted[i]);
        }
        // [날짜, 도시, 그 도시 안의 인덱스] — 날짜 작은 순
        PriorityQueue<int[]> heap = new PriorityQueue<>((a, b) -> Integer.compare(a[0], b[0]));
        int curMax = Integer.MIN_VALUE;
        for (int i = 0; i < k; i++) {
            heap.offer(new int[]{sorted[i][0], i, 0});
            curMax = Math.max(curMax, sorted[i][0]);
        }

        int bestL = -1, bestR = -1;
        long bestLen = Long.MAX_VALUE;
        while (true) {
            int[] top = heap.poll();       // 지금 기간의 가장 이른 날 L
            int city = top[1], idx = top[2];
            if ((long) curMax - top[0] < bestLen) { // 엄격히 짧을 때만 → 같은 길이면 이른 L 유지
                bestLen = (long) curMax - top[0];
                bestL = top[0];
                bestR = curMax;
            }
            if (idx + 1 == sorted[city].length) break; // 이 도시는 더 늦출 날짜가 없다
            int next = sorted[city][idx + 1];
            heap.offer(new int[]{next, city, idx + 1});
            curMax = Math.max(curMax, next);
        }
        return new int[]{bestL, bestR};
    }
}
