import java.util.Arrays;

class Solution {
    public int solution(int[] camps, int k) {
        Arrays.sort(camps);
        int lo = 0, hi = (camps[camps.length - 1] - camps[0] + 1) / 2; // 탑 하나로도 가능한 r
        while (lo < hi) {
            int mid = (lo + hi) >>> 1;
            if (enough(camps, k, mid)) hi = mid;
            else lo = mid + 1;
        }
        return lo;
    }

    /** 최대 급수 거리 r 로 탑 k 개 이하가 되는가 — 가장 왼쪽 미급수 캠프 + r 에 탑을 세우는 그리디 */
    private boolean enough(int[] camps, int k, int r) {
        int towers = 0;
        int i = 0, n = camps.length;
        while (i < n) {
            towers++;
            if (towers > k) return false;
            long cover = camps[i] + 2L * r;          // 이 탑이 덮는 오른쪽 끝
            while (i < n && camps[i] <= cover) i++;  // 덮인 캠프 건너뛰기
        }
        return true;
    }
}
