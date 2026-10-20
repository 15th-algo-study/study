class Solution {
    public int solution(String songA, String songB) {
        char[] a = songA.toCharArray(), b = songB.toCharArray();
        int n = a.length, m = b.length;
        int[][] len = new int[n + 1][m + 1]; // 0행·0열은 빈 문자열 → 0
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                if (a[i - 1] == b[j - 1]) {
                    len[i][j] = len[i - 1][j - 1] + 1; // 같은 음을 공통 가락 끝에 붙인다
                } else {
                    len[i][j] = Math.max(len[i - 1][j], len[i][j - 1]); // 한쪽 음을 버린다
                }
            }
        }
        return len[n][m];
    }
}
