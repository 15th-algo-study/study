class Solution {
    private static final int MAX = 999_999;

    public int[] solution(int start, int[] targets) {
        // start 에서 BFS 를 한 번만 돌려 0 ~ 999,999 모든 수의 최소 횟수를 구해 둔다
        int[] presses = new int[MAX + 1];
        java.util.Arrays.fill(presses, -1);
        int[] queue = new int[MAX + 1];
        int head = 0, tail = 0;
        presses[start] = 0;
        queue[tail++] = start;
        while (head < tail) {
            int x = queue[head++];
            int[] nexts = {x + 1, x * 2, reverse(x)};
            for (int y : nexts) {
                if (y > MAX || presses[y] != -1) continue; // 고장 나는 버튼은 누를 수 없다
                presses[y] = presses[x] + 1;
                queue[tail++] = y;
            }
        }

        int[] answer = new int[targets.length];
        for (int i = 0; i < targets.length; i++) answer[i] = presses[targets[i]];
        return answer;
    }

    /** 숫자 순서 뒤집기. 맨 앞의 0 은 수로 바꾸면 저절로 사라진다 (1230 → 321) */
    private int reverse(int x) {
        int r = 0;
        while (x > 0) {
            r = r * 10 + x % 10;
            x /= 10;
        }
        return r;
    }
}
