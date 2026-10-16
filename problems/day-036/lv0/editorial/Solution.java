class Solution {
    public long solution(int[] inflow, int a, int b) {
        long sum = 0; // 10^12 까지 커질 수 있으므로 long
        for (int i = a; i <= b; i++) {
            sum += inflow[i];
        }
        return sum;
    }
}
