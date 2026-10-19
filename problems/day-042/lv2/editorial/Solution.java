class Solution {
    public int[] solution(int[] honey, int[][] orders) {
        int n = honey.length;
        long[] prefix = new long[n + 1]; // prefix[i] = honey[0..i-1] 의 합 (단조 증가)
        for (int i = 0; i < n; i++) prefix[i + 1] = prefix[i] + honey[i];

        int[] answer = new int[orders.length];
        for (int j = 0; j < orders.length; j++) {
            int s = orders[j][0];
            long need = prefix[s] + orders[j][1]; // prefix[d+1] >= need 인 첫 d
            int lo = s + 1, hi = n + 1;           // prefix 인덱스 [s+1, n] 에서 lower bound, 없으면 n+1
            while (lo < hi) {
                int mid = (lo + hi) >>> 1;
                if (prefix[mid] >= need) hi = mid;
                else lo = mid + 1;
            }
            answer[j] = lo == n + 1 ? -1 : lo - 1;
        }
        return answer;
    }
}
