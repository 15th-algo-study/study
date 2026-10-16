class Solution {
    private int wheels, s;
    private boolean[][] forbidden; // forbidden[p][d] = p번 다이얼에 d 가 올 수 없음

    public int solution(int wheels, int[][] hints, int s) {
        this.wheels = wheels;
        this.s = s;
        forbidden = new boolean[wheels][10];
        for (int[] h : hints) forbidden[h[0]][h[1]] = true; // 힌트는 표에 한 번만 정리
        return fill(0, 0);
    }

    /** p번 다이얼부터 채운다. sum 은 지금까지 놓은 숫자의 합 */
    private int fill(int p, int sum) {
        if (p == wheels) return sum % s == 0 ? 1 : 0;
        int count = 0;
        for (int d = 0; d <= 9; d++) {
            if (forbidden[p][d]) continue;
            count += fill(p + 1, sum + d);
        }
        return count;
    }
}
