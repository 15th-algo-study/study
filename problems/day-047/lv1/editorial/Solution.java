import java.util.Arrays;

class Solution {
    public long solution(int[] jobs) {
        Arrays.sort(jobs);                 // 짧은 복사부터
        long elapsed = 0;                  // 지금까지 흐른 시간 = 다음 손님의 대기 시간
        long totalWait = 0;
        for (int job : jobs) {
            totalWait += elapsed;          // 이 손님이 기다린 시간
            elapsed += job;
        }
        return totalWait;
    }
}
