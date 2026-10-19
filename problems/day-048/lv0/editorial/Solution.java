class Solution {
    public int solution(long[] readings) {
        int count = 0;
        for (long reading : readings) {
            if (reading == reverse(reading)) count++;
        }
        return count;
    }

    /** 자릿수를 거꾸로 쌓아 뒤집은 수를 만든다. 0 → 0, 120 → 21 */
    private long reverse(long n) {
        long reversed = 0;
        while (n > 0) {
            reversed = reversed * 10 + n % 10;
            n /= 10;
        }
        return reversed;
    }
}
