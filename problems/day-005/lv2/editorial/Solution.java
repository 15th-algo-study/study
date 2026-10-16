class Solution {
    private static final int DAY = 86_400;

    public int[] solution(String[] meetings) {
        int[] diff = new int[DAY + 1];
        for (String m : meetings) {
            int start = Integer.parseInt(m.substring(0, 2)) * 3600
                    + Integer.parseInt(m.substring(3, 5)) * 60
                    + Integer.parseInt(m.substring(6, 8));
            int duration = Integer.parseInt(m.substring(9));
            int end = start + duration; // [start, end) — DAY 를 넘으면 자정에서 이어진다
            if (end <= DAY) {
                diff[start]++;
                diff[end]--;
            } else {
                diff[start]++;
                diff[DAY]--;
                diff[0]++;
                diff[end - DAY]--;
            }
        }
        int max = 0, seconds = 0, running = 0;
        for (int t = 0; t < DAY; t++) {
            running += diff[t];
            if (running > max) {
                max = running;
                seconds = 1;
            } else if (running == max) {
                seconds++;
            }
        }
        return new int[]{max, seconds};
    }
}
