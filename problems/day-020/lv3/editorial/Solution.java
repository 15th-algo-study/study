import java.util.Arrays;

class Solution {
    public long solution(int[][] mats) {
        int n = mats.length;

        // 1) 세로 경계(x, x+w)를 모아 정렬 — 이웃한 경계 사이가 하나의 "띠"
        long[] xs = new long[2 * n];
        for (int i = 0; i < n; i++) {
            xs[2 * i] = mats[i][0];
            xs[2 * i + 1] = (long) mats[i][0] + mats[i][2];
        }
        Arrays.sort(xs);

        // 2) 돗자리를 y 오름차순으로 한 번만 정렬해 두면 띠마다 구간 합치기를 바로 할 수 있다
        int[][] byY = mats.clone();
        Arrays.sort(byY, (a, b) -> Integer.compare(a[1], b[1]));

        long area = 0;
        for (int i = 0; i + 1 < xs.length; i++) {
            long left = xs[i], right = xs[i + 1];
            if (left == right) continue; // 너비 0인 띠

            // 3) 이 띠에 걸친 돗자리들의 [y, y+h) 구간을 합쳐 덮인 길이를 구한다
            long covered = 0, curStart = -1, curEnd = -1;
            for (int[] m : byY) {
                long mx = m[0], mxEnd = mx + m[2];
                if (mx > left || mxEnd < right) continue; // 띠 전체를 덮지 않으면 이 띠와 무관
                long y1 = m[1], y2 = y1 + m[3];
                if (curEnd < 0) {
                    curStart = y1;
                    curEnd = y2;
                } else if (y1 <= curEnd) {
                    curEnd = Math.max(curEnd, y2); // 겹치거나 맞닿음 → 구간을 늘린다
                } else {
                    covered += curEnd - curStart;
                    curStart = y1;
                    curEnd = y2;
                }
            }
            if (curEnd >= 0) covered += curEnd - curStart;

            area += covered * (right - left); // 띠 너비 × 덮인 세로 길이
        }
        return area;
    }
}
