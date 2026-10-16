class Solution {
    private static final int MAX_DAY = 45;

    public long[] solution(int[] days) {
        // 표를 한 번만 채운다: leaf[1] = 1, leaf[2] = 2, leaf[n] = 2 * leaf[n-1] + leaf[n-2]
        long[] leaf = new long[MAX_DAY + 1];
        leaf[1] = 1;
        leaf[2] = 2;
        for (int day = 3; day <= MAX_DAY; day++) {
            leaf[day] = 2 * leaf[day - 1] + leaf[day - 2]; // long 이어야 넘치지 않는다
        }

        long[] answer = new long[days.length];
        for (int i = 0; i < days.length; i++) {
            answer[i] = leaf[days[i]];
        }
        return answer;
    }
}
