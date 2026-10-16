class Solution {
    public long[] solution(int[] changes) {
        long[] balance = new long[changes.length];
        long running = 0; // 지금까지의 합 = 오늘 잔액
        for (int i = 0; i < changes.length; i++) {
            running += changes[i];
            balance[i] = running;
        }
        return balance;
    }
}
