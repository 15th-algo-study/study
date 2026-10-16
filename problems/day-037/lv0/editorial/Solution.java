class Solution {
    public int[] solution(int[] heights, int l, int r) {
        int[] result = heights.clone(); // 원본은 건드리지 않는다
        for (int i = l, j = r; i < j; i++, j--) {
            int tmp = result[i];
            result[i] = result[j];
            result[j] = tmp;
        }
        return result;
    }
}
