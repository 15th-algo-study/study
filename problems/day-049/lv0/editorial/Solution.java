class Solution {
    public long[] solution(long[] codes) {
        long[] answer = new long[codes.length];
        for (int i = 0; i < codes.length; i++) {
            long n = codes[i];
            long result = 0;
            long place = 1;                    // 다음 홀수 자릿수가 들어갈 자리 (1, 10, 100, ...)
            while (n > 0) {
                long digit = n % 10;
                if (digit % 2 == 1) {          // 홀수만 남긴다
                    result += digit * place;
                    place *= 10;
                }
                n /= 10;
            }
            answer[i] = result;
        }
        return answer;
    }
}
