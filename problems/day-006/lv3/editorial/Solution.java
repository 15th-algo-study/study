class Solution {
    // 방향 0=위, 1=오른쪽, 2=아래, 3=왼쪽 에 대한 단위 벡터
    private static final int[] DX = {0, 1, 0, -1};
    private static final int[] DY = {1, 0, -1, 0};

    public long[] solution(String program, long k) {
        // 1) 프로그램을 한 번 실행해 (dx, dy, r) 로 요약한다. 시작 방향은 0(위).
        long dx = 0, dy = 0;
        int r = 0;
        int i = 0, n = program.length();
        while (i < n) {
            char op = program.charAt(i++);
            int value = 0;
            while (i < n && Character.isDigit(program.charAt(i))) value = value * 10 + (program.charAt(i++) - '0');
            switch (op) {
                case 'F' -> { dx += (long) DX[r] * value; dy += (long) DY[r] * value; }
                case 'R' -> r = (r + value) % 4;
                default -> r = Math.floorMod(r - value, 4);
            }
        }

        // 2) 요약값으로 최대 4번 실행한다. pos[t] = t번 실행 뒤의 위치
        long[][] pos = new long[5][2];
        long x = 0, y = 0;
        int dir = 0;
        for (int t = 1; t <= 4; t++) {
            long[] moved = rotate(dx, dy, dir); // 현재 방향 기준으로 이동량을 회전
            x += moved[0];
            y += moved[1];
            dir = (dir + r) % 4;
            pos[t][0] = x;
            pos[t][1] = y;
        }

        // 3) 4번 실행은 순수한 평행 이동이므로 (k / 4) 번 묶어서 더한다.
        long full = k / 4;
        int rest = (int) (k % 4);
        return new long[]{full * pos[4][0] + pos[rest][0], full * pos[4][1] + pos[rest][1]};
    }

    /** (x, y) 를 오른쪽으로 90° × times 회전한다. 한 번 돌리면 (x, y) → (y, -x). */
    private long[] rotate(long x, long y, int times) {
        for (int t = 0; t < times; t++) {
            long nx = y, ny = -x;
            x = nx;
            y = ny;
        }
        return new long[]{x, y};
    }
}
