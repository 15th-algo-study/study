class Solution {
    public long[] solution(int[] need, int bar) {
        long bars = 0, scrap = 0;
        for (int length : need) {
            long count = ((long) length + bar - 1) / bar; // 올림 나눗셈 (정수만 사용)
            // 같은 값: (long) Math.ceil((double) length / bar)
            bars += count;
            scrap += count * bar - length;
        }
        return new long[]{bars, scrap};
    }
}
