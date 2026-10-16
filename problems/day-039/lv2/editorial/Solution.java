class Solution {
    public long[] solution(int n, int[][] works, int[][] queries) {
        long[] diff = new long[n + 1];
        for (int[] w : works) {
            int l = Math.min(w[0], w[1]), r = Math.max(w[0], w[1]);
            diff[l] += w[2];
            diff[r + 1] -= w[2];
        }
        // 1차 누적: 구간별 층수, 2차 누적: 층수의 누적합
        long[] prefix = new long[n + 1];
        long height = 0;
        for (int i = 0; i < n; i++) {
            height += diff[i];
            prefix[i + 1] = prefix[i] + height;
        }
        long[] answer = new long[queries.length];
        for (int q = 0; q < queries.length; q++) {
            int l = Math.min(queries[q][0], queries[q][1]), r = Math.max(queries[q][0], queries[q][1]);
            answer[q] = prefix[r + 1] - prefix[l];
        }
        return answer;
    }
}
