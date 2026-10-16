class Solution {
    private static final int MAX_DIFF = 1000;
    private int[] heights;
    private int[] order;
    private boolean[] used;
    private int[] count; // count[d] = 이웃 키 차이의 최댓값이 d 인 줄의 수

    public int[] solution(int[] heights, int[] limits) {
        this.heights = heights;
        int n = heights.length;
        order = new int[n];
        used = new boolean[n];
        count = new int[MAX_DIFF + 1];

        permute(0);

        // prefix[x] = 최댓값이 x 이하인 줄의 수
        int[] prefix = new int[MAX_DIFF + 1];
        prefix[0] = count[0];
        for (int d = 1; d <= MAX_DIFF; d++) prefix[d] = prefix[d - 1] + count[d];

        int[] answer = new int[limits.length];
        for (int i = 0; i < limits.length; i++) answer[i] = prefix[limits[i]];
        return answer;
    }

    private void permute(int depth) {
        int n = heights.length;
        if (depth == n) {
            int maxDiff = 0;
            for (int i = 0; i + 1 < n; i++) {
                maxDiff = Math.max(maxDiff, Math.abs(heights[order[i]] - heights[order[i + 1]]));
            }
            count[maxDiff]++;
            return;
        }
        for (int i = 0; i < n; i++) {
            if (used[i]) continue; // 같은 키라도 다른 아이이므로 인덱스로 구분
            used[i] = true;
            order[depth] = i;
            permute(depth + 1);
            used[i] = false;
        }
    }
}
