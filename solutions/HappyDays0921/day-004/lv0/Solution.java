class Solution {
    public int solution(int[] floors) {
        int answer = 0;
        int sum;
        for(int i=1;i<floors.length;i++){
            sum = floors[i]-floors[i-1];
            if(sum<0)
                sum*=-1;
            answer+=sum;
        }
        answer+=floors[0]-1;

        return answer;
    }
}
