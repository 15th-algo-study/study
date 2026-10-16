class Solution {
    public long solution(String s) {
        long sum = 0;
        long current = 0; // 지금 읽고 있는 수
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch >= '0' && ch <= '9') {
                current = current * 10 + (ch - '0');
            } else {
                sum += current; // 수가 끝났다
                current = 0;
            }
        }
        return sum + current; // 문자열이 숫자로 끝나는 경우
    }
}
