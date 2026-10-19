class Solution {
    public int[] solution(int[] readings, int limit) {
        int max = Integer.MIN_VALUE, min = Integer.MAX_VALUE, over = 0;
        for (int r : readings) {
            int shown = (int) Math.round(r / 10.0); // 10 으로 나누면 정수 나눗셈 → 10.0; round 는 long 을 돌려준다
            max = Math.max(max, shown);
            min = Math.min(min, shown);
            if (shown > limit) over++;
        }
        return new int[]{max, min, over};
    }
}
