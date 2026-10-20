class Solution {
    public long solution(int[] power, int[] heat, int start) {
        final long INF = Long.MAX_VALUE / 4;
        long on = INF; // 직전 시간에 켜져 있을 때의 최소 비용 (시작 전엔 켜져 있을 수 없음)
        long off = 0;  // 직전 시간에 꺼져 있을 때의 최소 비용
        for (int i = 0; i < power.length; i++) {
            long nextOn = power[i] + Math.min(on, off + start); // 꺼져 있었으면 기동 비용
            long nextOff = heat[i] + Math.min(on, off);         // 끄는 건 공짜
            on = nextOn;
            off = nextOff;
        }
        return Math.min(on, off);
    }
}
