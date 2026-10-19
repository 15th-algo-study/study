import java.util.Arrays;

class Solution {
    public long solution(int[][] orders) {
        Arrays.sort(orders, (a, b) -> Integer.compare(a[1], b[1]));   // 약속이 급한 순서
        long finish = 0;
        long worst = 0;                                               // 지각이 없으면 0
        for (int[] order : orders) {
            finish += order[0];
            worst = Math.max(worst, finish - order[1]);
        }
        return worst;
    }
}
