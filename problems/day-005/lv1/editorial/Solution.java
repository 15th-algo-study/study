class Solution {
    private static final String SPECIALS = "!@#$%^&*";

    public int solution(String[] passwords) {
        int answer = 0;
        for (String p : passwords) {
            if (isValid(p)) answer++;
        }
        return answer;
    }

    private boolean isValid(String p) {
        if (p.length() < 8 || p.length() > 20) return false;
        boolean upper = false, lower = false, digit = false, special = false;
        for (int i = 0; i < p.length(); i++) {
            char ch = p.charAt(i);
            if (ch >= 'A' && ch <= 'Z') upper = true;
            else if (ch >= 'a' && ch <= 'z') lower = true;
            else if (ch >= '0' && ch <= '9') digit = true;
            else if (SPECIALS.indexOf(ch) >= 0) special = true;
            else return false; // 허용되지 않는 문자
            if (i >= 2 && ch == p.charAt(i - 1) && ch == p.charAt(i - 2)) return false;
        }
        return upper && lower && digit && special;
    }
}
