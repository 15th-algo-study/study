class Solution {
    public int[] solution(long[] plates) {
        int[] answer = new int[plates.length];
        for (int i = 0; i < plates.length; i++) {
            int[] count = new int[10];          // 숫자 0~9 가 나온 횟수
            long n = plates[i];
            do {                                 // 0 도 숫자 '0' 하나로 센다
                count[(int) (n % 10)]++;
                n /= 10;
            } while (n > 0);

            int best = 0;
            for (int digit = 1; digit < 10; digit++) {
                if (count[digit] > count[best]) best = digit;   // 동률이면 작은 숫자를 유지
            }
            answer[i] = best;
        }
        return answer;
    }
}
