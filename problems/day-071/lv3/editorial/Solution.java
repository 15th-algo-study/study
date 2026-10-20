import java.util.Arrays;

class Solution {
    public int solution(int[][] trays) {
        int n = trays.length;
        int[][] norm = new int[n][];
        for (int i = 0; i < n; i++) {
            int s = Math.min(trays[i][0], trays[i][1]);
            int l = Math.max(trays[i][0], trays[i][1]);
            norm[i] = new int[]{s, l}; // 회전을 없앤 (짧은 변, 긴 변)
        }
        // 짧은 변 오름차순, 같으면 긴 변 내림차순 (같은 짧은 변끼리 이어지지 않게)
        Arrays.sort(norm, (x, y) -> x[0] != y[0] ? Integer.compare(x[0], y[0]) : Integer.compare(y[1], x[1]));

        int[] tails = new int[n]; // tails[k] = 길이 k+1 증가 부분 수열의 가장 작은 끝값
        int len = 0;
        for (int[] t : norm) {
            int l = t[1];
            int lo = 0, hi = len; // l 이상인 첫 위치 (엄격 증가이므로 lower bound)
            while (lo < hi) {
                int mid = (lo + hi) >>> 1;
                if (tails[mid] < l) lo = mid + 1;
                else hi = mid;
            }
            tails[lo] = l;
            if (lo == len) len++;
        }
        return len;
    }
}
