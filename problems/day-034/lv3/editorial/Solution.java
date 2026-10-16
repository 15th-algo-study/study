class Solution {
    private long[] pow3;   // pow3[k] = 3^k
    private long[] full;   // full[k] = 현재 행(r)을 3^k 로 나눈 나머지 행이 k단계 "온전한" 카펫에서 갖는 채워진 칸 수

    public long[] solution(int n, long[][] queries) {
        pow3 = new long[n + 1];
        pow3[0] = 1;
        for (int k = 1; k <= n; k++) pow3[k] = pow3[k - 1] * 3;
        full = new long[n + 1];

        long[] answer = new long[queries.length];
        for (int i = 0; i < queries.length; i++) {
            long r = queries[i][0];
            long lo = Math.min(queries[i][1], queries[i][2]);
            long hi = Math.max(queries[i][1], queries[i][2]);
            prepareRow(n, r);
            answer[i] = prefix(n, r, hi + 1) - prefix(n, r, lo);
        }
        return answer;
    }

    /** full[k] 를 채운다: r 의 3진법 아래 k자리를 보며 자리가 1이면 ×2, 아니면 ×3. */
    private void prepareRow(int n, long r) {
        full[0] = 1;
        long rest = r;
        for (int k = 1; k <= n; k++) {
            full[k] = full[k - 1] * (rest % 3 == 1 ? 2 : 3);
            rest /= 3;
        }
    }

    /** k단계 카펫의 r번 행에서 열 [0, x) 안의 채워진 칸 수 (0 ≤ x ≤ 3^k). */
    private long prefix(int k, long r, long x) {
        if (k == 0) return x;                      // 1칸짜리 카펫: x 가 1이면 그 칸 하나
        long s = pow3[k - 1];
        long rr = r / s;                           // 행이 속한 블록 행 (0, 1, 2)
        long rSub = r % s;
        long cx = x / s;                           // 통째로 덮이는 블록 열 개수
        long xSub = x % s;
        long count = 0;
        for (long j = 0; j < cx; j++) {
            if (!(rr == 1 && j == 1)) count += full[k - 1];   // 가운데 블록만 구멍
        }
        if (xSub > 0 && !(rr == 1 && cx == 1)) {
            count += prefix(k - 1, rSub, xSub);    // 부분적으로 덮인 블록 하나만 재귀
        }
        return count;
    }
}
