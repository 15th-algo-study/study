import java.util.Arrays;

class Solution {
    public int solution(int[] weights, int k) {
        int n = weights.length;
        Arrays.sort(weights);

        int[] gaps = new int[n - 1]; // 이웃한 무게의 차이(틈)
        for (int i = 0; i + 1 < n; i++) gaps[i] = weights[i + 1] - weights[i];
        Arrays.sort(gaps);

        int total = weights[n - 1] - weights[0]; // 상자 하나에 다 담았을 때의 편차
        // 가장 큰 틈 k-1 개에서 잘라 내면 그만큼 편차가 줄어든다
        for (int i = 0; i < k - 1 && i < gaps.length; i++) {
            total -= gaps[gaps.length - 1 - i];
        }
        return total;
    }
}
