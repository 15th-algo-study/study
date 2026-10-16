class Solution {
    public String solution(String s, int k) {
        StringBuilder sb = new StringBuilder(s.length() * k); // 결과 길이를 미리 확보
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            for (int j = 0; j < k; j++) sb.append(ch);
        }
        return sb.toString();
    }
}
