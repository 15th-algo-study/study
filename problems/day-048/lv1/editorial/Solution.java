import java.util.Arrays;

class Solution {
    public int solution(int[] heights, int d) {
        Arrays.sort(heights);
        int pairs = 0;
        int i = 0;
        while (i + 1 < heights.length) {
            if (heights[i + 1] - heights[i] <= d) {   // 가장 작은 학생은 바로 다음 학생과 짝짓는 것이 최선
                pairs++;
                i += 2;
            } else {
                i++;                                   // 이 학생은 누구와도 짝이 될 수 없다
            }
        }
        return pairs;
    }
}
