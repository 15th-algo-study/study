class Solution {
    public long[] solution(int[] usage, int[][] queries) {
        int n = usage.length;
        long[] prefix = new long[n + 1]; // prefix[i] = usage[0..i-1] 의 합
        for (int i = 0; i < n; i++) {
            prefix[i + 1] = prefix[i] + usage[i];
        }
        long[] answer = new long[queries.length];
        for (int q = 0; q < queries.length; q++) {
            int l = Math.min(queries[q][0], queries[q][1]);
            int r = Math.max(queries[q][0], queries[q][1]);
            answer[q] = prefix[r + 1] - prefix[l];
        }
        return answer;
    }
}
