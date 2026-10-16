class Solution {
    public int solution(String[] names, String pattern) {
        int star = pattern.indexOf('*');
        boolean hasStar = star >= 0;
        String pre = hasStar ? pattern.substring(0, star) : pattern;
        String suf = hasStar ? pattern.substring(star + 1) : "";

        int count = 0;
        for (String name : names) {
            int n = name.length();
            // '*' 가 없으면 길이가 같아야 하고, 있으면 앞뒤를 덮고도 남아야 한다
            if (hasStar ? n < pre.length() + suf.length() : n != pre.length()) continue;
            if (matchAt(name, 0, pre) && matchAt(name, n - suf.length(), suf)) count++;
        }
        return count;
    }

    /** name 의 from 위치부터 part 를 글자별로 비교한다. '?' 는 어떤 글자와도 맞는다. */
    private boolean matchAt(String name, int from, String part) {
        for (int i = 0; i < part.length(); i++) {
            char p = part.charAt(i);
            if (p != '?' && p != name.charAt(from + i)) return false;
        }
        return true;
    }
}
