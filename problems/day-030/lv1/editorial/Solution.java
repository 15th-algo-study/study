class Solution {
    private int n, r;
    private boolean[][] fought; // fought[a][b] = a 와 b 가 다툰 적 있음
    private int[] chosen;

    public int solution(int n, int r, int[][] fights) {
        this.n = n;
        this.r = r;
        fought = new boolean[n][n];
        for (int[] f : fights) {
            fought[f[0]][f[1]] = true; // 기록은 표에 한 번만 정리 (양방향)
            fought[f[1]][f[0]] = true;
        }
        chosen = new int[r];
        return pick(0, 0);
    }

    /** start 이상의 학생 중에서 count 번째 조원을 고른다 (번호가 커지는 순서 → 조합) */
    private int pick(int start, int count) {
        if (count == r) return 1;
        int ways = 0;
        for (int s = start; s < n; s++) {
            if (conflicts(s, count)) continue; // 이미 고른 조원과 다툰 적 있으면 가지치기
            chosen[count] = s;
            ways += pick(s + 1, count + 1);
        }
        return ways;
    }

    private boolean conflicts(int s, int count) {
        for (int i = 0; i < count; i++) {
            if (fought[chosen[i]][s]) return true;
        }
        return false;
    }
}
