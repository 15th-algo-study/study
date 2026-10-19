class Solution {
    public int[] solution(int[] standard, int[] measured) {
        int n = standard.length;
        int[] answer = new int[measured.length];
        for (int q = 0; q < measured.length; q++) {
            int x = measured[q];
            int i = lowerBound(standard, x); // x 이상인 첫 위치
            if (i == 0) answer[q] = standard[0];
            else if (i == n) answer[q] = standard[n - 1];
            else {
                // 같은 차이면 작은 쪽(standard[i-1]) 을 고른다
                answer[q] = standard[i] - x < x - standard[i - 1] ? standard[i] : standard[i - 1];
            }
        }
        return answer;
    }

    private int lowerBound(int[] a, int x) {
        int lo = 0, hi = a.length;
        while (lo < hi) {
            int mid = (lo + hi) >>> 1;
            if (a[mid] >= x) hi = mid;
            else lo = mid + 1;
        }
        return lo;
    }
}
