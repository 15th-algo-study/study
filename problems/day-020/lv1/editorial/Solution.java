import java.util.Arrays;

class Solution {
    public long solution(int[] sopranos, int[] altos) {
        Arrays.sort(sopranos);
        Arrays.sort(altos);
        long total = 0; // 20만 × 10^9 는 int 범위를 넘는다
        for (int i = 0; i < sopranos.length; i++) {
            total += Math.abs(sopranos[i] - altos[i]); // 같은 순번끼리 짝짓는 것이 최적
        }
        return total;
    }
}
