import java.util.Arrays;

class Solution {
    public long[] solution(int[][] sections) {
        // 시작 위치 오름차순 — 겹치는 구간은 정렬 뒤 반드시 이웃한다
        Arrays.sort(sections, (a, b) -> Integer.compare(a[0], b[0]));

        long zones = 0, total = 0;
        long curStart = sections[0][0], curEnd = sections[0][0] + sections[0][1];
        for (int i = 1; i < sections.length; i++) {
            long s = sections[i][0], e = s + sections[i][1];
            if (s <= curEnd) {
                curEnd = Math.max(curEnd, e); // 겹치거나 맞닿음 → 구역을 늘린다 (포함되는 경우를 위해 max)
            } else {
                zones++;
                total += curEnd - curStart;
                curStart = s;
                curEnd = e;
            }
        }
        zones++; // 마지막 구역
        total += curEnd - curStart;
        return new long[]{zones, total};
    }
}
