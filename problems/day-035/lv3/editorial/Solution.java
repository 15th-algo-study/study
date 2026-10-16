import java.util.Arrays;

class Solution {
    public long solution(int[] weights, long lo, long hi) {
        int n = weights.length;
        int half = n / 2;
        long[] left = subsetSums(weights, 0, half);      // 앞 절반의 모든 부분집합 합 (빈 집합 포함)
        long[] right = subsetSums(weights, half, n);     // 뒤 절반
        Arrays.sort(left);
        Arrays.sort(right);

        long count = pairsAtMost(left, right, hi) - pairsAtMost(left, right, lo - 1);
        if (lo <= 0 && 0 <= hi) count--;                 // 빈 집합끼리의 쌍(총량 0)은 레시피가 아니다
        return count;
    }

    /** weights[from..to) 로 만들 수 있는 2^(to-from) 개의 부분집합 합. */
    private long[] subsetSums(int[] weights, int from, int to) {
        long[] sums = new long[1 << (to - from)];
        int size = 1;                                     // sums[0] = 0 (빈 집합)
        for (int i = from; i < to; i++) {
            for (int j = 0; j < size; j++) sums[size + j] = sums[j] + weights[i]; // 기존 합에 새 통을 더한 복사본
            size *= 2;
        }
        return sums;
    }

    /** 정렬된 a, b 에서 a[i] + b[j] ≤ limit 인 쌍의 수 (투 포인터). */
    private long pairsAtMost(long[] a, long[] b, long limit) {
        long pairs = 0;
        int j = b.length - 1;
        for (long x : a) {                                // x 가 커질수록 허용되는 b 는 줄어든다
            while (j >= 0 && x + b[j] > limit) j--;
            if (j < 0) break;
            pairs += j + 1;
        }
        return pairs;
    }
}
