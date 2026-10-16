class Solution {
    public long solution(int[] ribbons) {
        long g = 0;    // gcd(0, x) = x 이므로 0에서 시작해도 된다
        long sum = 0;  // 길이 합은 long
        for (int length : ribbons) {
            g = gcd(g, length);
            sum += length;
        }
        return sum / g;
    }

    /** 유클리드 호제법 (재귀): b가 0이면 a, 아니면 gcd(b, a % b). */
    private long gcd(long a, long b) {
        if (b == 0) return a;
        return gcd(b, a % b);
    }
}
