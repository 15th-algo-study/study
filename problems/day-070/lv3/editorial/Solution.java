import java.util.ArrayList;
import java.util.List;

class Solution {
    private static final int MOD = 1_000_000_007;
    // NEXT[filled] = 열의 찬 칸이 filled 일 때, 남은 칸을 채우고 다음 열로 내미는 가로 패널 비트들
    private static final int[][] NEXT = new int[8][];

    static {
        for (int filled = 0; filled < 8; filled++) {
            List<Integer> out = new ArrayList<>();
            fill(filled, 0, 0, out);
            NEXT[filled] = out.stream().mapToInt(Integer::intValue).toArray();
        }
    }

    private static void fill(int filled, int row, int push, List<Integer> out) {
        if (row == 3) {
            out.add(push);
            return;
        }
        if ((filled >> row & 1) == 1) { // 이미 찬 칸
            fill(filled, row + 1, push, out);
            return;
        }
        fill(filled, row + 1, push | 1 << row, out); // 가로 패널: 다음 열로 내밈
        if (row + 1 < 3 && (filled >> (row + 1) & 1) == 0) {
            fill(filled, row + 2, push, out); // 세로 패널: 아래 칸과 함께
        }
    }

    public int solution(String[] wall) {
        int n = wall[0].length();
        long[] dp = new long[8]; // dp[s] = 이번 열에서 s 칸이 왼쪽 가로 패널로 이미 덮인 경우의 수
        dp[0] = 1;
        for (int c = 0; c < n; c++) {
            int window = 0;
            for (int r = 0; r < 3; r++) if (wall[r].charAt(c) == '#') window |= 1 << r;
            long[] next = new long[8];
            for (int s = 0; s < 8; s++) {
                if (dp[s] == 0 || (s & window) != 0) continue; // 가로 패널이 창문에 걸치면 불가
                for (int t : NEXT[s | window]) next[t] = (next[t] + dp[s]) % MOD;
            }
            dp = next;
        }
        return (int) dp[0]; // 벽 밖으로 내민 패널이 없어야 함
    }
}
