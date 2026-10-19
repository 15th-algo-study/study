class Solution {
    public int solution(int[] flowers) {
        int[] cnt = new int[200_001];
        int total = 0;
        for (int f : flowers) if (cnt[f]++ == 0) total++; // 실제로 등장하는 종류 수
        java.util.Arrays.fill(cnt, 0);

        int have = 0, best = flowers.length, l = 0;
        for (int r = 0; r < flowers.length; r++) {
            if (cnt[flowers[r]]++ == 0) have++;
            while (have == total) {                 // 모든 종류를 포함하는 동안 왼쪽을 줄인다
                best = Math.min(best, r - l + 1);
                if (--cnt[flowers[l++]] == 0) have--;
            }
        }
        return best;
    }
}
