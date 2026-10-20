class Solution {
    public String solution(String pattern) {
        StringBuilder sb = new StringBuilder();
        int count = 0; // 지금까지 읽은 반복 횟수 (0이면 수가 없었던 것)
        for (int i = 0; i < pattern.length(); i++) {
            char ch = pattern.charAt(i);
            if (Character.isDigit(ch)) {
                count = count * 10 + (ch - '0'); // 여러 자리 수를 한 자리씩 쌓는다
            } else {
                int times = count == 0 ? 1 : count;
                for (int t = 0; t < times; t++) sb.append(ch);
                count = 0;
            }
        }
        return sb.toString();
    }
}
