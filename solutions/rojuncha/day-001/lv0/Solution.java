class Solution {
    public int solution(int[] numbers) {
        int answer = 0;
        for(int i=0; i<numbers.length;i++){
            if(numbers[i]%2!=0){
                answer -=numbers[i];
            }else
            answer += numbers[i];
        }
        return answer;
    }
}
