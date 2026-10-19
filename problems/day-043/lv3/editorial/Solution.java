class Solution {
    public int solution(String s, int m) {
        int n = s.length();
        int best = 0;
        for (int h = 1; h <= 26; h++) {          // 조각에 쓰인 색의 종류 수를 h 로 고정
            int[] cnt = new int[26];
            int distinct = 0, satisfied = 0, l = 0;
            for (int r = 0; r < n; r++) {
                int c = s.charAt(r) - 'a';
                if (cnt[c]++ == 0) distinct++;
                if (cnt[c] == m) satisfied++;    // m 번째가 들어온 순간
                while (distinct > h) {            // 종류 수가 h 를 넘으면 왼쪽을 줄인다
                    int d = s.charAt(l++) - 'a';
                    if (cnt[d] == m) satisfied--; // m 에서 m-1 로 떨어지는 순간
                    if (--cnt[d] == 0) distinct--;
                }
                if (distinct == h && satisfied == h) best = Math.max(best, r - l + 1);
            }
        }
        return best;
    }
}
