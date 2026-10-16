class Solution {
    public int solution(String s) {
        int sum = 0;
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch == ' ') continue;
            sum += Character.toLowerCase(ch) - 'a' + 1; // a=1, b=2, ..., z=26
        }
        return sum;
    }
}
