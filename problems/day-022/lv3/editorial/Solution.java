class Solution {
    public long solution(int[] heights, int[] widths) {
        int n = heights.length;
        long[] prefix = new long[n + 1]; // prefix[i] = 0..i-1 번 패널 폭의 합
        for (int i = 0; i < n; i++) prefix[i + 1] = prefix[i] + widths[i];

        int[] stack = new int[n + 1]; // 높이 오름차순 인덱스 스택
        int top = 0;
        long best = 0;
        for (int j = 0; j <= n; j++) {
            int h = j < n ? heights[j] : 0; // 끝에 높이 0 인 가상 패널
            while (top > 0 && heights[stack[top - 1]] > h) {
                int i = stack[--top];
                long left = top > 0 ? prefix[stack[top - 1] + 1] : 0; // i 가 왼쪽으로 뻗는 시작점
                long width = prefix[j] - left;                          // j 직전까지가 오른쪽 끝
                best = Math.max(best, heights[i] * width);
            }
            stack[top++] = j;
        }
        return best;
    }
}
