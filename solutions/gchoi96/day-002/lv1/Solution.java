class Solution {
    public String solution(String s) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < s.length()) {
            char ch = s.charAt(i);
            int j = i;
            while (j < s.length() && s.charAt(j) == ch) j++;
            sb.append(ch);
            if (j - i > 1) sb.append(j - i);
            i = j; // 다음 덩어리의 시작
        }
        return sb.toString();
    }
}
