class Solution {
    public int[] solution(int[] speeds, int[] limits) {
        int[] answer = new int[limits.length];
        for (int i = 0; i < limits.length; i++) {
            answer[i] = countAtMost(speeds, limits[i]);
        }
        return answer;
    }

    /** x 이하인 원소의 개수 = x 보다 큰 값이 처음 나오는 위치 */
    private int countAtMost(int[] a, int x) {
        int lo = 0, hi = a.length;
        while (lo < hi) {
            int mid = (lo + hi) / 2;
            if (a[mid] > x) hi = mid;
            else lo = mid + 1;
        }
        return lo;
    }
}
