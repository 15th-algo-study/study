class Solution {
    public int[] solution(int[] stamps, int day) {
        int bit = day - 1; // 1일 = 0번 비트
        int attended = 0, total = 0;
        for (int x : stamps) {
            if (((x >> bit) & 1) == 1) attended++; // day 번째 비트 확인
            total += Integer.bitCount(x);         // 켜진 비트 수 = 출석 일수
        }
        return new int[]{attended, total};
    }
}
