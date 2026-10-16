import java.util.ArrayList;
import java.util.List;

class Solution {
    public int[][] solution(int n, long[] ks) {
        long[] fact = new long[n + 1];
        fact[0] = 1;
        for (int i = 1; i <= n; i++) fact[i] = fact[i - 1] * i; // 20! 까지는 long 범위

        int[][] answer = new int[ks.length][n];
        for (int q = 0; q < ks.length; q++) {
            long k = ks[q] - 1; // 0부터 세는 번호
            List<Integer> remaining = new ArrayList<>();
            for (int d = 1; d <= n; d++) remaining.add(d);
            for (int pos = 0; pos < n; pos++) {
                long block = fact[n - 1 - pos];        // 이 자리의 드론 하나당 묶음 크기
                int idx = (int) (k / block);            // 남은 드론 중 몇 번째로 작은 것인가
                answer[q][pos] = remaining.remove(idx); // 인덱스로 제거
                k %= block;
            }
        }
        return answer;
    }
}
