class Solution {
    private int[] a;
    private int[] tmp;
    private long t;

    public long solution(int[] heights, int t) {
        this.a = heights.clone();
        this.tmp = new int[heights.length];
        this.t = t;
        return count(0, heights.length - 1);
    }

    /** [l, r] 안의 가리는 쌍 수를 세고, a[l..r] 을 오름차순으로 정렬해 둔다. */
    private long count(int l, int r) {
        if (l >= r) return 0;
        int m = (l + r) >>> 1;
        long pairs = count(l, m) + count(m + 1, r);

        // 앞 절반 a[l..m], 뒤 절반 a[m+1..r] 이 각각 정렬되어 있다.
        // 뒤 절반의 j 를 작은 값부터 보면 기준 a[j] + t 가 커지므로, 앞 절반 포인터 p 는 오른쪽으로만 움직인다.
        int p = l;
        for (int j = m + 1; j <= r; j++) {
            long need = a[j] + t;                 // long: 최대 2×10^9
            while (p <= m && a[p] < need) p++;    // a[p] >= need 인 첫 위치
            pairs += m - p + 1;                   // 그 뒤쪽은 모두 조건을 만족
        }

        // 병합
        int i = l, j = m + 1, k = l;
        while (i <= m && j <= r) tmp[k++] = a[i] <= a[j] ? a[i++] : a[j++];
        while (i <= m) tmp[k++] = a[i++];
        while (j <= r) tmp[k++] = a[j++];
        System.arraycopy(tmp, l, a, l, r - l + 1);
        return pairs;
    }
}
