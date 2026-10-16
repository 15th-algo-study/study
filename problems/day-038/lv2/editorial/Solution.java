class Solution {
    public long solution(int[] weights, int k) {
        long[] count = new long[k]; // 누적합 나머지별 등장 횟수
        count[0] = 1;               // 빈 누적합 P[0] = 0
        int remainder = 0;
        long answer = 0;
        for (int w : weights) {
            remainder = (int) ((remainder + (long) w) % k);
            answer += count[remainder]; // 같은 나머지였던 이전 위치마다 묶음 하나
            count[remainder]++;
        }
        return answer;
    }
}
