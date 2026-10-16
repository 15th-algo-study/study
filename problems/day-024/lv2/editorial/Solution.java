class Solution {
    public int[] solution(int[][] commands) {
        int m = commands.length;
        int queries = 0;
        for (int[] c : commands) if (c[0] == 3) queries++;
        int[] answer = new int[queries];

        int[] buf = new int[2 * m + 1]; // 가운데에서 시작해 양쪽으로 자란다
        int head = m, tail = m;         // [head, tail) 가 현재 돌들
        int idx = 0;
        for (int[] c : commands) {
            switch (c[0]) {
                case 1 -> buf[--head] = c[1];
                case 2 -> buf[tail++] = c[1];
                case 3 -> answer[idx++] = c[1] <= tail - head ? buf[head + c[1] - 1] : -1;
                case 4 -> { if (head < tail) head++; }
                default -> { if (head < tail) tail--; }
            }
        }
        return answer;
    }
}
