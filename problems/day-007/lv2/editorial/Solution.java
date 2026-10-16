class Solution {
    public String solution(String keys) {
        int n = keys.length();
        char[] left = new char[n];  // 커서 왼쪽 (순서대로)
        char[] right = new char[n]; // 커서 오른쪽 (커서에 가까운 글자가 맨 위 = 뒤집힌 순서)
        int l = 0, r = 0;
        for (int i = 0; i < n; i++) {
            char key = keys.charAt(i);
            switch (key) {
                case '<' -> { if (l > 0) l--; }
                case '[' -> { if (l > 0) right[r++] = left[--l]; }
                case ']' -> { if (r > 0) left[l++] = right[--r]; }
                default -> left[l++] = key;
            }
        }
        StringBuilder sb = new StringBuilder(l + r);
        sb.append(left, 0, l);
        for (int i = r - 1; i >= 0; i--) sb.append(right[i]); // 맨 위부터 꺼내면 원래 순서
        return sb.toString();
    }
}
