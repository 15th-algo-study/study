class Solution {
    private int[] remain = new int[4]; // 색별 남은 깃발 수

    public int solution(String flags) {
        for (char ch : flags.toCharArray()) remain[ch - 'a']++;
        return place(flags.length(), -1);
    }

    /** left 개를 더 놓아야 하고 바로 앞 자리의 색이 last 일 때, 조건을 만족하는 배열의 수 */
    private int place(int left, int last) {
        if (left == 0) return 1;
        int ways = 0;
        for (int c = 0; c < 4; c++) {
            if (c == last || remain[c] == 0) continue; // 같은 색이 이웃하면 가지치기
            remain[c]--;
            ways += place(left - 1, c);
            remain[c]++;
        }
        return ways;
    }
}
