import java.util.Arrays;

class Solution {
    private int[] next;   // next[d] = d 이하에서 가장 늦은 빈 날 (0 이면 없음)

    public long solution(int[] deadline, int[] fee) {
        int n = deadline.length;
        Integer[] order = new Integer[n];
        for (int i = 0; i < n; i++) order[i] = i;
        Arrays.sort(order, (a, b) -> Integer.compare(fee[b], fee[a]));   // 출연료 큰 순

        next = new int[n + 1];
        for (int d = 0; d <= n; d++) next[d] = d;                        // 처음엔 모든 날이 비어 있다

        long total = 0;
        for (int i : order) {
            int day = find(Math.min(deadline[i], n));                      // 마감일 이하 가장 늦은 빈 날
            if (day == 0) continue;                                        // 빈 날이 없다
            total += fee[i];
            next[day] = day - 1;                                           // 그 날을 쓴다
        }
        return total;
    }

    /** 경로 압축: 지나온 날들이 모두 최종 빈 날을 가리키게 한다 */
    private int find(int d) {
        int root = d;
        while (next[root] != root) root = next[root];
        while (next[d] != root) {
            int parent = next[d];
            next[d] = root;
            d = parent;
        }
        return root;
    }
}
