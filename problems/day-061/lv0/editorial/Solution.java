class Solution {
    public int solution(int[] numbers) {
        int answer = 0;
        for (int number : numbers) {
            // 수를 문자열로 바꾸면 음수의 '-' 까지 한 글자로 들어 있다
            answer += String.valueOf(number).length();
        }
        return answer;
    }
}
