class Solution {
    public int[] solution(int[] weights, int lo, int hi) {
        int small = 0, normal = 0, large = 0;
        for (int w : weights) {
            if (w < lo) small++;
            else if (w > hi) large++;
            else normal++; // lo <= w <= hi
        }
        return new int[]{small, normal, large};
    }
}
