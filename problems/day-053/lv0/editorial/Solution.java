class Solution {
    public int solution(int[][] ships, int r) {
        long radius2 = (long) r * r;
        int count = 0;
        for (int[] ship : ships) {
            long x = ship[0], y = ship[1];       // 곱하기 전에 long 으로
            if (x * x + y * y <= radius2) count++; // sqrt 없이 제곱으로 비교
        }
        return count;
    }
}
