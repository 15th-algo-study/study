class Solution {
    public int[] solution(String scroll, String[] queries) {
        int n = scroll.length();
        // next[c][i] = 인덱스 i 이상에서 글자 c 가 처음 나오는 인덱스 (없으면 n)
        int[][] next = new int[26][n + 1];
        for (int c = 0; c < 26; c++) next[c][n] = n;
        for (int i = n - 1; i >= 0; i--) {
            for (int c = 0; c < 26; c++) next[c][i] = next[c][i + 1];
            next[scroll.charAt(i) - 'a'][i] = i;
        }

        int[] answer = new int[queries.length];
        for (int q = 0; q < queries.length; q++) {
            String query = queries[q];
            int space = query.indexOf(' ');
            int pos = Integer.parseInt(query.substring(0, space)) - 1; // 0-based 시작 인덱스
            int last = -1;
            for (int k = space + 1; k < query.length(); k++) {
                int j = next[query.charAt(k) - 'a'][pos];
                if (j == n) { last = -1; break; } // 더 이상 없다
                last = j;
                pos = j + 1;                      // 다음 글자는 그 다음부터
            }
            answer[q] = last == -1 ? -1 : last + 1;
        }
        return answer;
    }
}
