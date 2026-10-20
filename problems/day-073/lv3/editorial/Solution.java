class Solution {
    public int solution(String original, String copy, int k) {
        char[] a = original.toCharArray(), b = copy.toCharArray();
        int n = a.length, m = b.length;
        if (Math.abs(n - m) > k) return -1; // 길이 차이만큼은 반드시 실수
        int width = 2 * k + 1, inf = k + 1;  // 띠 위치 d = j - i + k
        int[] prev = new int[width], cur = new int[width];

        for (int d = 0; d < width; d++) {    // 0행: dist[0][j] = j
            int j = d - k;
            prev[d] = (j < 0 || j > m) ? inf : Math.min(j, inf);
        }
        for (int i = 1; i <= n; i++) {
            int rowMin = inf;
            for (int d = 0; d < width; d++) {
                int j = i + d - k;
                int v;
                if (j < 0 || j > m) v = inf;          // 표 밖
                else if (j == 0) v = Math.min(i, inf); // 0열: i글자 모두 빠뜨림
                else {
                    v = prev[d] + (a[i - 1] == b[j - 1] ? 0 : 1); // (i-1, j-1): 일치 또는 잘못 씀
                    if (d + 1 < width) v = Math.min(v, prev[d + 1] + 1); // (i-1, j): 빠뜨림
                    if (d > 0) v = Math.min(v, cur[d - 1] + 1);         // (i, j-1): 끼워 넣음
                    if (v > inf) v = inf;
                }
                cur[d] = v;
                rowMin = Math.min(rowMin, v);
            }
            if (rowMin > k) return -1; // 이 행이 모두 k 초과면 이후도 모두 k 초과
            int[] tmp = prev; prev = cur; cur = tmp;
        }
        int result = prev[m - n + k];
        return result <= k ? result : -1;
    }
}
