import java.util.Arrays;

class Solution {
    public int[] solution(int[] fees, int k) {
        int n = fees.length;
        long[] best = new long[n]; // best[i] = i번에서 묵고 끝까지 가는 최소 숙박비 (i번 포함)
        best[n - 1] = fees[n - 1];
        for (int i = n - 2; i >= 0; i--) {
            long min = Long.MAX_VALUE;
            for (int j = i + 1; j <= Math.min(n - 1, i + k); j++) min = Math.min(min, best[j]);
            best[i] = fees[i] + min;
        }

        // 앞에서부터, 최적을 유지하는 가장 작은 번호로 이동하면 사전순 최소
        int[] route = new int[n];
        int length = 0, cur = 0;
        route[length++] = 0;
        while (cur != n - 1) {
            long need = best[cur] - fees[cur];
            int next = cur + 1;
            while (best[next] != need) next++;
            route[length++] = next;
            cur = next;
        }
        return Arrays.copyOf(route, length);
    }
}
