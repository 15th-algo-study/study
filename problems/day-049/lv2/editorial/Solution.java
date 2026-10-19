import java.util.Arrays;

class Solution {
    public int solution(int[] ours, int[] theirs) {
        Arrays.sort(ours);
        Arrays.sort(theirs);
        int wins = 0;
        int j = 0;                                   // 아직 상대를 정하지 않은 가장 약한 상대 선수
        for (int score : ours) {                     // 우리 선수를 약한 순으로
            if (j < theirs.length && score > theirs[j]) {
                wins++;                              // 이길 수 있는 가장 약한 상대를 잡는다
                j++;
            }
            // 못 이기면 이 선수는 아무 패배 대결에나 보낸다
        }
        return wins;
    }
}
