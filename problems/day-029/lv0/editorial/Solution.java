class Solution {
    public int[] solution(int n) {
        int[] count = new int[n + 1]; // count[m] = m개를 직사각형으로 놓는 배치 수
        for (int w = 1; w * w <= n; w++) {
            for (int h = w; w * h <= n; h++) { // h 는 w 부터: w×h 와 h×w 는 같은 배치
                count[w * h]++;
            }
        }
        int bestCount = 0, bestM = 1;
        for (int m = 1; m <= n; m++) {
            if (count[m] > bestCount) { // '>' 라서 같은 가짓수면 작은 m 이 남는다
                bestCount = count[m];
                bestM = m;
            }
        }
        return new int[]{bestM, bestCount};
    }
}
