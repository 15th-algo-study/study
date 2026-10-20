class Solution {
    public long solution(int[][] goods, int capacity) {
        long[] best = new long[capacity + 1]; // best[v] = 부피 합 v 이하에서 이익 최댓값
        for (int[] g : goods) {
            int size = g[0], profit = g[1];
            int count = Math.min(g[2], capacity / size); // 트럭에 들어갈 수 있는 만큼만
            for (int bundle = 1; count > 0; bundle *= 2) {
                int take = Math.min(bundle, count);          // 1, 2, 4, ... 그리고 나머지
                count -= take;
                int w = size * take;
                long p = (long) profit * take;
                for (int v = capacity; v >= w; v--) {         // 0/1 배낭: 큰 쪽부터
                    if (best[v - w] + p > best[v]) best[v] = best[v - w] + p;
                }
            }
        }
        return best[capacity];
    }
}
