class Solution {
    public int solution(int[] numbers) {
        int oddTotal = 0;
        int evenTotal = 0;
        for(int num : numbers){
            if(num % 2 != 0){
                oddTotal += num;
                continue;
            }
            evenTotal += num;
        }
        return evenTotal - oddTotal;
    }
}
