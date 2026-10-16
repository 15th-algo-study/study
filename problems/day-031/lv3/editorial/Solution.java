class Solution {
    public long solution(int[] heights) {
        int n = heights.length;
        int[] left = new int[n];   // 왼쪽으로 가장 가까운 "높이 ≥ 내 높이"인 칸 (없으면 -1)
        int[] right = new int[n];  // 오른쪽으로 가장 가까운 "높이 > 내 높이"인 칸 (없으면 n)
        int[] stack = new int[n];
        int top = 0;
        for (int i = 0; i < n; i++) {
            // 나보다 낮은 칸은 모두 꺼낸다 → 그 칸들의 오른쪽 경계는 i
            while (top > 0 && heights[stack[top - 1]] < heights[i]) {
                right[stack[--top]] = i;
            }
            // 남은 꼭대기(높이 ≥ 내 높이)가 왼쪽 경계. 같은 높이는 꺼내지 않았으므로 자연히 왼쪽 것이 된다
            left[i] = top == 0 ? -1 : stack[top - 1];
            stack[top++] = i;
        }
        while (top > 0) right[stack[--top]] = n; // 오른쪽에 더 높은 칸이 없는 것들

        long total = 0;
        for (int i = 0; i < n; i++) {
            // i번 칸에 성이 세워질 때의 구간 길이 = right - left - 1
            total += (long) heights[i] * (right[i] - left[i] - 1);
        }
        return total;
    }
}
