class Solution {
    public int[] solution(int[] heights, int x) {
        int first = -1, last = -1;
        for (int i = 0; i < heights.length; i++) {
            if (heights[i] == x) {
                if (first == -1) first = i; // 처음 만난 위치만 기록
                last = i;                   // 마지막 위치는 계속 갱신
            } else if (heights[i] > x) {
                break;                      // 정렬되어 있으므로 더 볼 필요 없음
            }
        }
        return new int[]{first, last};
    }
}
