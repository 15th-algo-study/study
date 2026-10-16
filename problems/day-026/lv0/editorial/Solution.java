class Solution {
    public int solution(int n, int k) {
        int count = 0;
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= n; j++) {
                if ((i * j) % k == 0) count++; // i행 j열 칸의 값은 i × j
            }
        }
        return count;
    }
}
