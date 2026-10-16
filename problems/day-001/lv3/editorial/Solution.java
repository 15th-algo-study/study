class Solution {
    public long[] solution(int n, int[][] queries) {
        long[] answer = new long[queries.length];
        for (int q = 0; q < queries.length; q++) {
            answer[q] = seat(n, queries[q][0], queries[q][1]);
        }
        return answer;
    }

    private long seat(long n, long r, long c) {
        // 바깥에서 몇 번째 껍질인지 (0부터)
        long d = Math.min(Math.min(r - 1, c - 1), Math.min(n - r, n - c));
        long m = n - 2 * d;            // 그 껍질의 한 변 길이
        long before = n * n - m * m;   // 바깥 껍질들에 쓰인 번호 개수
        long top = d + 1, bottom = n - d, left = d + 1, right = n - d;
        long offset;
        if (r == top) offset = c - left;                              // 윗변: 오른쪽으로
        else if (c == right) offset = (m - 1) + (r - top);           // 오른쪽 변: 아래로
        else if (r == bottom) offset = 2 * (m - 1) + (right - c);    // 아랫변: 왼쪽으로
        else offset = 3 * (m - 1) + (bottom - r);                    // 왼쪽 변: 위로
        return before + offset + 1;
    }
}
