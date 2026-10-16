class Solution {
    public long[] solution(int green, int red, int[] arrivals) {
        long cycle = (long) green + red; // int 끼리 더하면 넘칠 수 있다
        long[] answer = new long[arrivals.length];
        long prev = -1;
        for (int i = 0; i < arrivals.length; i++) {
            long t = Math.max(arrivals[i], prev + 1);
            long phase = t % cycle;
            if (phase >= green) t += cycle - phase; // 빨간불이면 다음 초록 시작으로 점프
            answer[i] = t;
            prev = t;
        }
        return answer;
    }
}
