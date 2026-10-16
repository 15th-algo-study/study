class Solution {
    public String solution(int n, long[] positions) {
        StringBuilder sb = new StringBuilder(positions.length);
        for (long p : positions) {
            sb.append(crease(n, p));
        }
        return sb.toString();
    }

    /** n번 접은 종이의 p번 주름 ('V' 또는 'A'). */
    private char crease(int n, long p) {
        long mid = 1L << (n - 1);                 // 가운데 주름 번호 (long!)
        if (p == mid) return 'V';                  // 마지막에 접힌 가운데 주름은 항상 골
        if (p < mid) return crease(n - 1, p);      // 왼쪽 절반은 S(n-1) 그대로
        char c = crease(n - 1, (mid << 1) - p);    // 오른쪽 절반은 거울상: 2^n - p 번과 짝
        return c == 'V' ? 'A' : 'V';               // 거울상은 골과 산이 뒤바뀐다
    }
}
