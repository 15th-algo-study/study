class Solution {
    private long[][] ways; // ways[u][d]: 오르막 u개, 내리막 d개가 남았을 때 가능한 이어 붙이기 수

    public String[] solution(int n, long[] ks) {
        ways = new long[n + 1][n + 1];
        for (int u = 0; u <= n; u++) {
            for (int d = u; d <= n; d++) {       // 높이 = d - u ≥ 0 인 상태만 의미 있다
                if (u == 0) ways[u][d] = 1;      // 내리막만 남았으면 한 가지
                else {
                    ways[u][d] = ways[u - 1][d];                 // 오르막을 놓는 경우
                    if (d > u) ways[u][d] += ways[u][d - 1];     // 높이가 0보다 크면 내리막도 가능
                }
            }
        }

        String[] answer = new String[ks.length];
        for (int i = 0; i < ks.length; i++) answer[i] = kth(n, ks[i]);
        return answer;
    }

    /** 사전순(D < U) k번째 트랙. */
    private String kth(int n, long k) {
        char[] track = new char[2 * n];
        int u = n, d = n;
        for (int pos = 0; pos < 2 * n; pos++) {
            if (d > u) {                          // 내리막을 놓을 수 있다 (D 가 사전순으로 먼저)
                long withD = ways[u][d - 1];      // 여기에 D 를 놓고 시작하는 트랙 수
                if (k <= withD) {
                    track[pos] = 'D';
                    d--;
                    continue;
                }
                k -= withD;                       // D 로 시작하는 것들을 건너뛴다
            }
            track[pos] = 'U';
            u--;
        }
        return new String(track);
    }
}
