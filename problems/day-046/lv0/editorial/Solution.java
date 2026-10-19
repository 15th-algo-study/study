class Solution {
    public int[] solution(long[] codes) {
        int[] answer = new int[codes.length];
        for (int i = 0; i < codes.length; i++) {
            long n = codes[i];
            int max = 0, min = 9;
            do {                              // 0 도 자릿수 하나로 취급하기 위해 do-while
                int digit = (int) (n % 10);   // 마지막 자릿수
                max = Math.max(max, digit);
                min = Math.min(min, digit);
                n /= 10;                      // 마지막 자리를 떼어 낸다
            } while (n > 0);
            answer[i] = max - min;
        }
        return answer;
    }
}
