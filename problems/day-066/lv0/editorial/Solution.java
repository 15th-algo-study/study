import java.util.HashSet;
import java.util.Set;

class Solution {
    public int[] solution(String[] words) {
        Set<String> memo = new HashSet<>(); // 이미 번역해 적어 둔 단어
        int seconds = 0, found = 0;
        for (String word : words) {
            if (memo.contains(word)) {
                seconds += 1; // 메모장에서 찾기
                found++;
            } else {
                seconds += word.length(); // 직접 번역하고 적어 둔다
                memo.add(word);
            }
        }
        return new int[]{seconds, found};
    }
}
