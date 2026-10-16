import java.util.Arrays;

class Solution {
    public int solution(int[] wines, int[] cheeses, int budget) {
        Arrays.sort(wines);
        Arrays.sort(cheeses);
        long best = -1;
        int i = 0, j = cheeses.length - 1;
        while (i < wines.length && j >= 0) {
            long price = (long) wines[i] + cheeses[j]; // 합이 int 를 넘을 수 있다
            if (price <= budget) {
                best = Math.max(best, price); // 이 와인과 짝지을 수 있는 가장 비싼 치즈
                i++;
            } else {
                j--;                          // 치즈가 너무 비싸다
            }
        }
        return (int) best;
    }
}
