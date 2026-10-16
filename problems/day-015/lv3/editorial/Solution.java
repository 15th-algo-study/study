import java.util.HashSet;
import java.util.Set;

class Solution {
    public int solution(int[] scores, int k) {
        Set<Long> seen = new HashSet<>(); // 마지막으로 고른 묶음 이후에 나온 누적합들
        seen.add(0L);
        long prefix = 0;
        int answer = 0;
        for (int s : scores) {
            prefix += s;
            if (seen.contains(prefix - k)) {   // 여기서 끝나는 목표 묶음이 있다 → 끝이 가장 빠르므로 고른다
                answer++;
                seen.clear();                  // 다음 묶음은 이 뒤에서 시작해야 한다
            }
            seen.add(prefix);
        }
        return answer;
    }
}
