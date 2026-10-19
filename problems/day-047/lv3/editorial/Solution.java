import java.util.Arrays;

class Solution {
    public int solution(int[] pens, int r) {
        Arrays.sort(pens);
        int n = pens.length;
        int troughs = 0;
        int i = 0;                                   // 아직 물이 닿지 않은 가장 왼쪽 우리
        while (i < n) {
            long reach = (long) pens[i] + r;         // 이 우리를 덮을 수 있는 급수대 위치의 오른쪽 한계
            int j = i;
            while (j + 1 < n && pens[j + 1] <= reach) j++;   // 한계 안에서 가장 오른쪽 우리 위치
            long covered = (long) pens[j] + r;       // 그 자리에 설치하면 여기까지 덮인다
            troughs++;
            while (i < n && pens[i] <= covered) i++; // 덮인 우리는 건너뛴다
        }
        return troughs;
    }
}
