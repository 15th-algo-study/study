
class Solution {
    public int solution(int[] numbers) {
        int answer = 0;
        for (int x : numbers) {
            // 음수 홀수는 x % 2 == -1 이므로 == 0 으로 짝수를 판별한다
            if (x % 2 == 0) answer += x;
            else answer -= x;
        }
        return answer;
    }
}