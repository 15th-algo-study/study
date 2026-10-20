class Solution {
    public int solution(String strip) {
        char[] s = strip.toCharArray();
        int n = s.length;
        int count = 0;
        for (int center = 0; center < 2 * n - 1; center++) {
            int l = center / 2;           // 짝수 center: 글자 위, 홀수 center: 두 글자 사이
            int r = l + center % 2;
            while (l >= 0 && r < n && s[l] == s[r]) { // 양 끝이 같으면 [l, r] 도 대칭
                count++;
                l--;
                r++;
            }
        }
        return count;
    }
}
