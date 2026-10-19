import java.util.Arrays;

class Solution {
    public int solution(int[] logs, int d) {
        Arrays.sort(logs);
        int bundles = 1;
        long start = logs[0];                 // 현재 묶음에서 가장 짧은 장작
        for (int length : logs) {
            if (length > start + d) {         // 현재 묶음에 넣을 수 없으면 새 묶음
                bundles++;
                start = length;
            }
        }
        return bundles;
    }
}
