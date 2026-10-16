class Solution {
    private int n;
    private int[] cnt;      // cnt[ch] = s 에서 글자 ch 의 개수
    private int[][] pre;    // pre[ch][i] = s 의 앞 i 글자 중 ch 의 개수

    public long[] solution(String s, String[] queries) {
        n = s.length();
        cnt = new int[26];
        pre = new int[26][n + 1];
        for (int i = 0; i < n; i++) {
            int ch = s.charAt(i) - 'a';
            cnt[ch]++;
            for (int c = 0; c < 26; c++) pre[c][i + 1] = pre[c][i];
            pre[ch][i + 1]++;
        }

        long[] answer = new long[queries.length];
        for (int qi = 0; qi < queries.length; qi++) {
            String[] parts = queries[qi].split(" ");
            long a = Long.parseLong(parts[0]), b = Long.parseLong(parts[1]);
            int c = parts[2].charAt(0) - 'a';
            long l = Math.min(a, b), r = Math.max(a, b);
            answer[qi] = countPrefix(r, c) - countPrefix(l - 1, c);
        }
        return answer;
    }

    /** 띠의 앞 x 글자 안에 글자 c 가 몇 번 나오는가 */
    private long countPrefix(long x, int c) {
        long q = x / n;          // 완전한 조각 수
        int rem = (int) (x % n); // 마지막(번호 q) 조각의 앞 rem 글자
        long total = (q / 26) * n;                 // 조각 26개마다 c 는 정확히 n 번
        int leftover = (int) (q % 26);
        for (int k = 0; k < leftover; k++) {       // 남은 조각: 원래 글자 (c - k) 가 c 로 바뀐다
            total += cnt[Math.floorMod(c - k, 26)];
        }
        total += pre[Math.floorMod(c - leftover, 26)][rem]; // 번호 q 조각의 일부 (q ≡ leftover mod 26)
        return total;
    }
}
