class Solution {
    public String solution(String s) {
        StringBuilder sb = new StringBuilder(s.length());
        for (int i = s.length() - 1; i >= 0; i--) { // 뒤에서부터
            char ch = s.charAt(i);
            if (Character.isUpperCase(ch)) sb.append(Character.toLowerCase(ch));
            else if (Character.isLowerCase(ch)) sb.append(Character.toUpperCase(ch));
            else sb.append(ch); // 숫자는 그대로
        }
        return sb.toString();
    }
}
