class Solution {
    public int[] solution(int[] rolls) {
        int[] count = new int[6];
        for (int r : rolls) count[r - 1]++; // 눈 1 → 인덱스 0
        return count;
    }
}
