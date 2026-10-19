import java.util.Arrays;

class Solution {
    public int solution(int[] requests, long budget) {
        Arrays.sort(requests);                  // 적게 요청한 사람부터
        long spent = 0;
        int count = 0;
        for (int request : requests) {
            if (spent + request > budget) break; // 더 주면 예산 초과
            spent += request;
            count++;
        }
        return count;
    }
}
