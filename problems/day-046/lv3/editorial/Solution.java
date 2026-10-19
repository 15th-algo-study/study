import java.util.Arrays;

class Solution {
    public long solution(int[] weight, int[] strength) {
        int n = weight.length;
        long[][] people = new long[n][2];
        for (int i = 0; i < n; i++) {
            people[i][0] = weight[i];
            people[i][1] = strength[i];
        }
        // 몸무게 + 힘 이 작은 사람이 위로 (교환 논법으로 정당화)
        Arrays.sort(people, (a, b) -> Long.compare(a[0] + a[1], b[0] + b[1]));

        long above = 0;                    // 지금까지 세운 사람들의 몸무게 합
        long worst = Long.MIN_VALUE;
        for (long[] p : people) {
            worst = Math.max(worst, above - p[1]);
            above += p[0];
        }
        return worst;
    }
}
