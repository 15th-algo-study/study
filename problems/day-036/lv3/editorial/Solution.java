class Solution {
    public long solution(int[] coins) {
        int n = coins.length;
        long total = 0;
        for (int c : coins) total += c;
        if (total % 3 != 0) return 0;
        long target = total / 3;

        long answer = 0;
        long firstCuts = 0; // 지금까지 본 위치 중 prefix == target 인 첫 번째 끊기 후보 수
        long prefix = 0;
        for (int i = 1; i <= n - 1; i++) { // i 개를 앞 묶음으로 떼어낸 뒤의 틈
            prefix += coins[i - 1];
            if (prefix == 2 * target) answer += firstCuts; // 먼저 더하고
            if (prefix == target) firstCuts++;             // 그 다음 후보에 추가 (i < j 보장)
        }
        return answer;
    }
}
