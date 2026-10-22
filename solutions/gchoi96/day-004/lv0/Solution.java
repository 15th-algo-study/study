class Solution {
    public int solution(int[] floors) {
        int curr = 1;
        int total = 0;
        for(int floor : floors){
            total += Math.abs(curr - floor);
            System.out.println(total);
            curr = floor;
        }
        return total;
    }
}
