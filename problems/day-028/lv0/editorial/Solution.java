class Solution {
    public int[] solution(int n, int a, int b) {
        int either = 0, both = 0;
        for (int day = 1; day <= n; day++) {
            boolean clean = day % a == 0;
            boolean shop = day % b == 0;
            if (clean || shop) either++;
            if (clean && shop) both++;
        }
        return new int[]{either, both};
    }
}
