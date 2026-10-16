class Solution {
    private static final int MAX = 300;

    public long[] solution(int[][] queries) {
        // ways[n][m]: 사탕 n개를 봉지당 최대 m개로 나누는 방법 수
        long[][] ways = new long[MAX + 1][MAX + 1];
        for (int m = 0; m <= MAX; m++) ways[0][m] = 1;       // 사탕이 없으면 "아무것도 안 담기" 한 가지
        for (int n = 1; n <= MAX; n++) {
            for (int m = 1; m <= MAX; m++) {
                ways[n][m] = ways[n][m - 1];                  // m개 봉지를 하나도 안 쓰는 경우
                if (n >= m) ways[n][m] += ways[n - m][m];     // m개 봉지를 하나 이상 쓰는 경우
            }
        }

        long[] answer = new long[queries.length];
        for (int i = 0; i < queries.length; i++) {
            answer[i] = ways[queries[i][0]][queries[i][1]];
        }
        return answer;
    }
}
