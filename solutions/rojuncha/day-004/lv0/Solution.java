class Solution {
    public int solution(int[] floors) {
        int answer = 0;
        int now = 1;
        for(int i =0;i<floors.length ;i++){
            answer += Math.abs(now-floors[i]);
            now = floors[i];
        }
        return answer;
    }
}
