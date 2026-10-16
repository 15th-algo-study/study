class Solution {
    public int[] solution(String[] rolls) {
        int[] result = new int[rolls.length];
        for (int i = 0; i < rolls.length; i++) result[i] = shortestUnit(rolls[i]);
        return result;
    }

    private int shortestUnit(String t) {
        int n = t.length();
        for (int d = 1; d < n; d++) {
            if (n % d != 0) continue;   // 단위 길이는 n 의 약수여야 한다
            if (isPeriod(t, d)) return d;
        }
        return n;                        // 자기 자신은 항상 단위가 된다
    }

    /** 모든 i >= d 에 대해 t[i] == t[i - d] 인가 */
    private boolean isPeriod(String t, int d) {
        for (int i = d; i < t.length(); i++) {
            if (t.charAt(i) != t.charAt(i - d)) return false;
        }
        return true;
    }
}
