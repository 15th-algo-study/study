class Solution {
    public long[] solution(int n, int[] turns) {
        long signed = 0; // 부호 있는 합 (시계 +, 반시계 -)
        long total = 0;  // 돌린 칸 수의 합
        for (int t : turns) {
            signed += t;
            total += Math.abs(t);
        }
        return new long[]{Math.floorMod(signed, n), total}; // % 대신 floorMod: 음수도 0~n-1
    }
}
