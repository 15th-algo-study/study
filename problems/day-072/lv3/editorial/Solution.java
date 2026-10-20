import java.util.Arrays;

class Solution {
    public int[] solution(int[] weights, int[] values, int limit) {
        int totalValue = 0;
        for (int v : values) totalValue += v;

        long inf = Long.MAX_VALUE / 2;
        long[] minWeight = new long[totalValue + 1]; // minWeight[v] = 가치 합이 정확히 v 일 때 최소 무게
        Arrays.fill(minWeight, inf);
        minWeight[0] = 0;
        for (int i = 0; i < weights.length; i++) {
            int w = weights[i], val = values[i];
            for (int v = totalValue; v >= val; v--) { // 큰 쪽부터: 같은 화물을 두 번 쓰지 않게
                long cand = minWeight[v - val] + w;
                if (cand < minWeight[v]) minWeight[v] = cand;
            }
        }
        for (int v = totalValue; v >= 0; v--) {
            if (minWeight[v] <= limit) return new int[]{v, (int) minWeight[v]};
        }
        return new int[]{0, 0}; // v = 0 에서 항상 끝나므로 도달하지 않는다
    }
}
