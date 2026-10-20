class Solution {
    public String solution(String a, String b) {
        int len = Math.max(a.length(), b.length());
        char[] result = new char[len];
        // 일의 자리(오른쪽 끝)부터 같은 자리끼리 더한다
        for (int k = 0; k < len; k++) {
            int i = a.length() - 1 - k, j = b.length() - 1 - k;
            int x = i >= 0 ? a.charAt(i) - '0' : 0; // 글자 → 숫자
            int y = j >= 0 ? b.charAt(j) - '0' : 0;
            result[len - 1 - k] = (char) ('0' + (x + y) % 10); // 숫자 → 글자
        }
        int start = 0;
        while (start < len - 1 && result[start] == '0') start++; // 맨 앞의 0 지우기 (마지막 한 자리는 남김)
        return new String(result, start, len - start);
    }
}
