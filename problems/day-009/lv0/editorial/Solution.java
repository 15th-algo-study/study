class Solution {
    public String solution(String s) {
        StringBuilder sb = new StringBuilder(s.length());
        int count = 0; // 지금까지 본 영문자 수
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (Character.isLetter(ch)) {
                sb.append(count % 2 == 0 ? Character.toUpperCase(ch) : Character.toLowerCase(ch));
                count++;
            } else {
                sb.append(ch); // 숫자·공백은 그대로, 순번에도 넣지 않는다
            }
        }
        return sb.toString();
    }
}
