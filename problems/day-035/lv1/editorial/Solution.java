class Solution {
    public long[] solution(long[] widths, long[] heights) {
        long[] answer = new long[widths.length];
        for (int i = 0; i < widths.length; i++) {
            answer[i] = squares(Math.max(widths[i], heights[i]), Math.min(widths[i], heights[i]));
        }
        return answer;
    }

    /** w × h (w ≥ h) 원단에서 나오는 정사각형 수. */
    private long squares(long w, long h) {
        if (h == 0) return 0;                    // 남은 원단이 없다
        return w / h + squares(h, w % h);        // 한 변 h 짜리 w/h 장을 한꺼번에 잘라낸다
    }
}
