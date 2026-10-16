import java.util.Arrays;

class Solution {
    public int solution(int[][] shelves, int k) {
        int total = 0;
        for (int[] shelf : shelves) total += shelf.length; // 선반마다 길이가 다를 수 있다

        int[] all = new int[total];
        int pos = 0;
        for (int[] shelf : shelves) {
            for (int price : shelf) all[pos++] = price; // 2차원 → 1차원으로 펼친다
        }
        Arrays.sort(all);
        return all[k - 1]; // k 는 1부터 센다
    }
}
