class Solution {
    public int[] solution(long[] starts) {
        int[] answer = new int[starts.length];
        for (int i = 0; i < starts.length; i++) {
            answer[i] = moves(starts[i]);
        }
        return answer;
    }

    /** floor 층에서 1층까지 움직이는 횟수. */
    private int moves(long floor) {
        if (floor == 1) return 0;                     // 도착
        if (floor % 2 == 0) return 1 + moves(floor / 2); // 짝수: 미끄럼틀로 절반 층
        return 1 + moves(floor - 1);                  // 홀수: 계단으로 한 층
    }
}
