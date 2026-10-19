class Solution {
    public int[] solution(int[] kinds, int k) {
        int n = kinds.length;
        int[] cnt = new int[200_001];
        int distinct = 0;
        int[] answer = new int[n - k + 1];
        for (int r = 0; r < n; r++) {
            if (cnt[kinds[r]]++ == 0) distinct++;      // 오른쪽 끝 하루가 들어온다
            if (r >= k) {
                if (--cnt[kinds[r - k]] == 0) distinct--; // 왼쪽 끝 하루가 빠진다
            }
            if (r >= k - 1) answer[r - k + 1] = distinct;
        }
        return answer;
    }
}
