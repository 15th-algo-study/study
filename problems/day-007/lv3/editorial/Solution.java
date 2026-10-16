class Solution {
    public int solution(String log) {
        int n = log.length();
        int[] bal = new int[n]; // bal[j] = 0..j 까지의 (I 수 - O 수)
        int running = 0;
        for (int j = 0; j < n; j++) {
            running += log.charAt(j) == 'I' ? 1 : -1;
            bal[j] = running;
        }
        int total = bal[n - 1];
        if (total != 1 && total != -1) return 0; // 글자 하나를 지워 0이 될 수 없다

        int[] sufMin = new int[n + 1]; // sufMin[i] = min(bal[i..n-1])
        sufMin[n] = Integer.MAX_VALUE;
        for (int j = n - 1; j >= 0; j--) sufMin[j] = Math.min(bal[j], sufMin[j + 1]);

        char target = total == 1 ? 'I' : 'O';   // 지워야 하는 글자 종류
        int need = total == 1 ? 1 : -1;         // 지운 뒤에도 뒤쪽 접두 합이 0 이상이려면 sufMin >= need
        int count = 0;
        boolean prefixOk = true;                // bal[0..i-1] 이 모두 0 이상인가
        for (int i = 0; i < n && prefixOk; i++) {
            if (log.charAt(i) == target && sufMin[i] >= need) count++;
            if (bal[i] < 0) prefixOk = false;   // 이 뒤의 위치는 앞쪽 조건을 만족하지 못한다
        }
        return count;
    }
}
