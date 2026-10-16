class Solution {
    public long[] solution(int[] arrivals, int c, int t) {
        int n = arrivals.length;
        long[] answer = new long[n];
        long dep = 0;   // 지금 보고 있는 출발 시각
        int seats = c;  // 그 출발의 남은 자리
        for (int i = 0; i < n; i++) {
            long a = arrivals[i];
            if (a > dep) {
                dep = (a + t - 1) / t * t; // a 이후 첫 출발로 한 번에 건너뛴다
                seats = c;
            } else if (seats == 0) {
                dep += t;                  // 자리가 없으면 다음 출발
                seats = c;
            }
            answer[i] = dep;
            seats--;
        }
        return answer;
    }
}
