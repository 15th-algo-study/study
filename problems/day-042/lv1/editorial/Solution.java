import java.util.Arrays;

class Solution {
    public int[] solution(int[] sizes, int[][] ranges) {
        Arrays.sort(sizes);
        int[] answer = new int[ranges.length];
        for (int i = 0; i < ranges.length; i++) {
            // hi 이하 개수 - lo 미만 개수
            answer[i] = upperBound(sizes, ranges[i][1]) - lowerBound(sizes, ranges[i][0]);
        }
        return answer;
    }

    /** x 이상인 첫 인덱스 (= x 미만인 원소의 개수) */
    private int lowerBound(int[] a, int x) {
        int lo = 0, hi = a.length;
        while (lo < hi) {
            int mid = (lo + hi) / 2;
            if (a[mid] >= x) hi = mid;
            else lo = mid + 1;
        }
        return lo;
    }

    /** x 초과인 첫 인덱스 (= x 이하인 원소의 개수) */
    private int upperBound(int[] a, int x) {
        int lo = 0, hi = a.length;
        while (lo < hi) {
            int mid = (lo + hi) / 2;
            if (a[mid] > x) hi = mid;
            else lo = mid + 1;
        }
        return lo;
    }
}
