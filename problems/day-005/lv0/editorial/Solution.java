class Solution {
    public String solution(String s) {
        int[] count = new int[26];
        for (int i = 0; i < s.length(); i++) count[s.charAt(i) - 'a']++;
        int best = 0;
        for (int c = 1; c < 26; c++) {
            if (count[c] > count[best]) best = c; // 같으면 앞선 글자를 유지
        }
        return String.valueOf((char) ('a' + best));
    }
}
