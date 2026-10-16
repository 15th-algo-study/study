class Solution {
    private int n;
    private int[] candidates;   // 팀별 후보 순번 비트마스크 (중복 제거)
    private boolean[] used;     // 순번 사용 여부

    public int solution(int n, int[][] wishes) {
        this.n = n;
        this.candidates = new int[n];
        this.used = new boolean[n + 1];
        for (int i = 0; i < n; i++) {
            for (int s : wishes[i]) candidates[i] |= 1 << s; // 같은 순번은 한 번만
        }
        return count(0);
    }

    /** team번 팀부터 끝까지 배정하는 방법의 수. */
    private int count(int team) {
        if (team == n) return 1;                  // 모든 팀 배정 완료
        int ways = 0;
        for (int s = 1; s <= n; s++) {
            if ((candidates[team] & (1 << s)) == 0 || used[s]) continue; // 후보가 아니거나 이미 배정됨
            used[s] = true;
            ways += count(team + 1);
            used[s] = false;                      // 원상 복구
        }
        return ways;
    }
}
