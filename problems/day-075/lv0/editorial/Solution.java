class Solution {
    public int[][] solution(int days, int week, int start) {
        int rows = (start + days + week - 1) / week; // 올림 나눗셈
        int[][] calendar = new int[rows][week];      // 빈칸은 0 그대로
        for (int d = 1; d <= days; d++) {
            int pos = start + d - 1;                 // 처음부터 센 칸 번호 (0부터)
            calendar[pos / week][pos % week] = d;
        }
        return calendar;
    }
}
