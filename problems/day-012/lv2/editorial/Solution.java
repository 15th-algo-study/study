import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

class Solution {
    public int[] solution(String[] words) {
        Map<String, Integer> groups = new HashMap<>();
        for (String word : words) {
            char[] letters = word.toCharArray();
            Arrays.sort(letters);                  // 같은 타일 묶음이면 정렬 결과가 같다
            groups.merge(new String(letters), 1, Integer::sum);
        }
        int largest = 0;
        for (int size : groups.values()) largest = Math.max(largest, size);
        return new int[]{groups.size(), largest};
    }
}
