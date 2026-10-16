import java.util.Arrays;

class Solution {
    public int[] solution(int[] stamps) {
        Arrays.sort(stamps);
        int groups = 1, length = 1, longest = 1; // 첫 우표가 첫 묶음을 연다
        for (int i = 1; i < stamps.length; i++) {
            int gap = stamps[i] - stamps[i - 1];
            if (gap == 0) continue;            // 같은 번호 — 한 번만 센다
            if (gap == 1) {
                length++;                      // 번호가 이어진다
            } else {
                longest = Math.max(longest, length);
                groups++;                      // 번호가 빠져 묶음이 끊긴다
                length = 1;
            }
        }
        longest = Math.max(longest, length); // 마지막 묶음
        return new int[]{groups, longest};
    }
}
