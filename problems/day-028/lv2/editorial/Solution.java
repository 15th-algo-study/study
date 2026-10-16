import java.util.Arrays;

class Solution {
    private int[] weights;
    private long limit;
    private int count;

    public int solution(int[] weights, long limit) {
        this.weights = weights.clone();
        this.limit = limit;
        Arrays.sort(this.weights); // 가벼운 것부터: 한 번 넘치면 뒤는 전부 넘친다
        dfs(0, 0);
        return count;
    }

    /** 현재 조합(합 current)은 유효하다. start 이상에서 다음 장비를 골라 확장한다. */
    private void dfs(int start, long current) {
        count++; // 이 호출 자체가 유효한 조합 하나
        for (int j = start; j < weights.length; j++) {
            if (current + weights[j] > limit) break; // 가지치기: 뒤쪽 장비는 더 무겁다
            dfs(j + 1, current + weights[j]);
        }
    }
}
