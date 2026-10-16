import java.util.Arrays;

class Solution {
    public long solution(int[] gems) {
        int n = gems.length, half = n / 2;
        long total = 0;
        for (int g : gems) total += g;

        long[] left = subsetSums(gems, 0, half);   // 앞 절반의 모든 부분집합 합
        long[] right = subsetSums(gems, half, n);  // 뒤 절반의 모든 부분집합 합
        Arrays.sort(left);
        Arrays.sort(right);

        long best = Long.MAX_VALUE;
        int i = 0, j = right.length - 1;
        while (i < left.length && j >= 0) {
            long sumA = left[i] + right[j];      // 상자 A 의 합
            long diff = Math.abs(total - 2 * sumA); // A − B = 2·sumA − total
            if (diff < best) best = diff;
            if (2 * sumA < total) i++;           // A 가 가벼우면 키운다
            else j--;                            // A 가 무거우면 줄인다
        }
        return best;
    }

    /** gems[from..to) 의 모든 부분집합 합 (비트마스크 열거) */
    private long[] subsetSums(int[] gems, int from, int to) {
        int k = to - from;
        long[] sums = new long[1 << k];
        for (int mask = 1; mask < (1 << k); mask++) {
            int low = Integer.numberOfTrailingZeros(mask);
            sums[mask] = sums[mask & (mask - 1)] + gems[from + low]; // 가장 낮은 비트를 뺀 합 + 그 보석
        }
        return sums;
    }
}
