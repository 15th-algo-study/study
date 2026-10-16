class Solution {
    public String solution(int[] skewer) {
        int n = skewer.length;
        int[] pos = new int[n + 1];
        for (int i = 0; i < n; i++) pos[skewer[i]] = i;

        char[] answer = new char[n];
        answer[0] = 'L';
        int l = pos[1], r = pos[1]; // 지금까지 꽂힌 1..k 번이 차지한 구간
        for (int k = 2; k <= n; k++) {
            int p = pos[k];
            if (p == l - 1) {
                answer[k - 1] = 'L';
                l--;
            } else if (p == r + 1) {
                answer[k - 1] = 'R';
                r++;
            } else {
                return ""; // k 번이 구간 바로 옆에 없으면 만들 수 없다
            }
        }
        return new String(answer);
    }
}
