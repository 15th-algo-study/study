class Solution {
    public int solution(int[][] packets) {
        int broken = 0;
        for (int[] p : packets) {
            int x = 0;
            for (int j = 0; j < p.length - 1; j++) x ^= p[j]; // 데이터 XOR 누적
            if (x != p[p.length - 1]) broken++;
        }
        return broken;
    }
}
