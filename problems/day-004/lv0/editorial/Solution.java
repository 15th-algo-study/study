class Solution {
    public int solution(int[] floors) {
        int answer = 0;
        int current = 1;
        for (int floor : floors) {
            answer += Math.abs(floor - current);
            current = floor;
        }
        return answer;
    }
}
