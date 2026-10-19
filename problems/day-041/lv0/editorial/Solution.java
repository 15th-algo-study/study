class Solution {
    public int solution(int[] times, int t) {
        for (int time : times) {
            if (time >= t) return time; // 오름차순이므로 처음 만난 t 이상 값이 가장 이른 버스
        }
        return -1;
    }
}
