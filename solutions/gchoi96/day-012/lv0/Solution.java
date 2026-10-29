class Solution {
    public int[] solution(int[] rolls) {
        int[] answer = new int[6];
        for(int roll : rolls){
            answer[roll - 1]++;
        }
        return answer;
    }
}
