class Solution {
    private long[][] choose; // choose[i][j] = C(i, j), j > i 이면 0

    public int[][] solution(int n, int r, long[] ks) {
        choose = new long[n + 1][n + 1];
        for (int i = 0; i <= n; i++) {
            choose[i][0] = 1;
            for (int j = 1; j <= i; j++) {
                choose[i][j] = choose[i - 1][j - 1] + choose[i - 1][j]; // 파스칼의 삼각형
            }
        }

        int[][] answer = new int[ks.length][];
        for (int i = 0; i < ks.length; i++) answer[i] = kth(n, r, ks[i]);
        return answer;
    }

    /** 1..n 중 r개를 고르는 조합 중 사전순 k번째. */
    private int[] kth(int n, int r, long k) {
        int[] picked = new int[r];
        int x = 1;                                  // 다음에 고를 수 있는 가장 작은 번호
        for (int slot = 0; slot < r; slot++) {
            int remain = r - slot - 1;              // 이 자리 뒤에 더 골라야 하는 개수
            while (true) {
                long startingWithX = choose[n - x][remain]; // x 를 고르고 나머지를 x 보다 큰 번호에서 고르는 수
                if (k <= startingWithX) break;      // k번째는 x 로 시작한다
                k -= startingWithX;                 // x 로 시작하는 조합들을 건너뛴다
                x++;
            }
            picked[slot] = x++;
        }
        return picked;
    }
}
