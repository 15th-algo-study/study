class Solution {
    private static final int MOD = 1_000_000_007;
    private static final int MAX_CODE = 1_000_000;

    public int solution(String signal, int[] codes) {
        boolean[] isCode = new boolean[MAX_CODE + 1];
        for (int c : codes) isCode[c] = true;

        int n = signal.length();
        long[] ways = new long[n + 1]; // ways[i] = 앞 i글자를 나누는 방법 수
        ways[0] = 1;
        for (int i = 1; i <= n; i++) {
            long sum = 0;
            int value = 0, place = 1;
            // 마지막 조각 signal[j..i-1]을 뒤에서부터 한 글자씩 늘린다 (번호는 최대 7자리)
            for (int j = i - 1; j >= 0 && i - j <= 7; j--) {
                value += (signal.charAt(j) - '0') * place;
                place *= 10;
                if (value > MAX_CODE) break;
                if (signal.charAt(j) != '0' && isCode[value]) sum += ways[j];
            }
            ways[i] = sum % MOD;
        }
        return (int) ways[n];
    }
}
