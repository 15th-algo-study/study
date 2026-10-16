import java.util.Arrays;

class Solution {
    public int solution(int[] pots, int[] saucers) {
        Arrays.sort(pots);
        Arrays.sort(saucers);
        int i = 0, j = 0, matched = 0;
        while (i < pots.length && j < saucers.length) {
            if (saucers[j] >= pots[i]) { // 가장 작은 화분에 맞는 가장 작은 받침을 준다
                matched++;
                i++;
            }
            j++; // 못 맞춘 받침은 남은 어떤 화분에도 작으므로 버린다
        }
        return matched;
    }
}
