class Solution {
    public int solution(String draft, String target, int ins, int del, int rep) {
        int n = draft.length(), m = target.length();
        int[] prev = new int[m + 1]; // cost[i-1][*]
        int[] cur = new int[m + 1];  // cost[i][*]
        for (int j = 0; j <= m; j++) prev[j] = j * ins; // 빈 문자열 → j글자 삽입
        for (int i = 1; i <= n; i++) {
            char a = draft.charAt(i - 1);
            cur[0] = i * del; // i글자 모두 삭제
            for (int j = 1; j <= m; j++) {
                int match = prev[j - 1] + (a == target.charAt(j - 1) ? 0 : rep);
                int delete = prev[j] + del;
                int insert = cur[j - 1] + ins;
                cur[j] = Math.min(match, Math.min(delete, insert));
            }
            int[] tmp = prev; prev = cur; cur = tmp;
        }
        return prev[m];
    }
}
