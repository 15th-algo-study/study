class Solution {
    public int solution(int[] heights) {
        int n = heights.length;
        int[] up = new int[n];   // i 에서 끝나는 엄격 증가 부분 수열의 최대 길이
        int[] down = new int[n]; // i 에서 시작하는 엄격 감소 부분 수열의 최대 길이
        for (int i = 0; i < n; i++) {
            up[i] = 1;
            for (int j = 0; j < i; j++) {
                if (heights[j] < heights[i] && up[j] + 1 > up[i]) up[i] = up[j] + 1;
            }
        }
        for (int i = n - 1; i >= 0; i--) {
            down[i] = 1;
            for (int j = n - 1; j > i; j--) {
                if (heights[j] < heights[i] && down[j] + 1 > down[i]) down[i] = down[j] + 1;
            }
        }
        int best = 0;
        for (int i = 0; i < n; i++) best = Math.max(best, up[i] + down[i] - 1); // 꼭대기는 한 번만
        return best;
    }
}
