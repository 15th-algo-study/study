class Solution {
    public int solution(int[] numbers) {
        int answer = 0;

        for(int a:numbers){
            //+- 떠나서 짝수인 경우
            if(a%2==0)
                answer += a;
            //홀수인경우
            else
                answer-= a;
        }
        
        return answer;
    }
}
