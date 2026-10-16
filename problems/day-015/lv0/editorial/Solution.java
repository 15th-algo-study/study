class Solution {
    public int[] solution(int[] scores) {
        int[] count = new int[10];
        for (int s : scores) {
            int band = Math.min(s / 10, 9); // 100점은 90점대(9번)로
            count[band]++;
        }
        return count;
    }
}
