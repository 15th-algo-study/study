class Solution {
    private int n;
    private int[] weights;
    private int[] balanced; // balanced[mask] = 참가 선수 집합이 mask 인 균형 배치 수

    public int[] solution(int[] weights, int[] players) {
        this.n = weights.length;
        this.weights = weights;
        balanced = new int[1 << n];

        assign(0, 0, 0, 0, 0); // 3^n 배치를 한 번만 열거

        // perPlayer[p] = p 가 참가하는 균형 배치 수
        int[] perPlayer = new int[n];
        for (int mask = 1; mask < (1 << n); mask++) {
            if (balanced[mask] == 0) continue;
            for (int p = 0; p < n; p++) {
                if ((mask >> p & 1) == 1) perPlayer[p] += balanced[mask];
            }
        }

        int[] answer = new int[players.length];
        for (int i = 0; i < players.length; i++) answer[i] = perPlayer[players[i]];
        return answer;
    }

    /** i 번 선수부터 배정. leftMask/rightMask 는 각 팀의 선수 집합, left/right 는 몸무게 합 */
    private void assign(int i, int leftMask, int rightMask, int left, int right) {
        if (i == n) {
            if (leftMask != 0 && rightMask != 0 && left == right) {
                balanced[leftMask | rightMask]++;
            }
            return;
        }
        int bit = 1 << i, w = weights[i];
        assign(i + 1, leftMask | bit, rightMask, left + w, right); // 왼쪽 팀
        assign(i + 1, leftMask, rightMask | bit, left, right + w); // 오른쪽 팀
        assign(i + 1, leftMask, rightMask, left, right);           // 쉼
    }
}
