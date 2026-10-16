class Solution {
    public long[] solution(String[] rules, String start, int years) {
        int k = rules.length;
        long[] cnt = new long[k];
        for (char ch : start.toCharArray()) cnt[ch - 'a']++; // 지금의 종류별 개수

        for (int year = 0; year < years; year++) {
            long[] next = new long[k];               // 1년 뒤 개수는 새 배열에
            for (int x = 0; x < k; x++) {
                for (char ch : rules[x].toCharArray()) {
                    next[ch - 'a'] += cnt[x];        // 종류 x 가지 하나마다 ch 가지가 하나씩 생긴다
                }
            }
            cnt = next;
        }
        return cnt;
    }
}
