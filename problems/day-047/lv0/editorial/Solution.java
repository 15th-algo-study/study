class Solution {
    public int solution(long[] tickets, int lucky) {
        int count = 0;
        for (long ticket : tickets) {
            long n = ticket;
            do {                               // 번호 0 도 자릿수 '0' 하나로 센다
                if (n % 10 == lucky) count++;
                n /= 10;
            } while (n > 0);
        }
        return count;
    }
}
