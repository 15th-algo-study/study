import java.util.Arrays;

class Solution {
    public long solution(int[][] requests) {
        int n = requests.length;
        long[][] jobs = new long[n][]; // {시작, 끝, 금액}
        for (int i = 0; i < n; i++) {
            long start = requests[i][0];
            jobs[i] = new long[]{start, start + requests[i][1], requests[i][2]};
        }
        Arrays.sort(jobs, (a, b) -> Long.compare(a[1], b[1])); // 끝나는 시각 순

        long[] ends = new long[n];
        for (int i = 0; i < n; i++) ends[i] = jobs[i][1];

        long[] best = new long[n + 1]; // best[i] = 앞의 i개 요청만 볼 때의 최대 수입
        for (int i = 0; i < n; i++) {
            int p = countEndsAtMost(ends, jobs[i][0]); // 이 요청과 겹치지 않는 앞쪽 요청 수
            best[i + 1] = Math.max(best[i], best[p] + jobs[i][2]);
        }
        return best[n];
    }

    // 정렬된 ends 에서 x 이하인 값의 개수 (upper bound)
    private int countEndsAtMost(long[] ends, long x) {
        int lo = 0, hi = ends.length;
        while (lo < hi) {
            int mid = (lo + hi) >>> 1;
            if (ends[mid] <= x) lo = mid + 1;
            else hi = mid;
        }
        return lo;
    }
}
