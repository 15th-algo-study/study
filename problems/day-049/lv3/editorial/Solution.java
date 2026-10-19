import java.util.ArrayList;
import java.util.List;

class Solution {
    public long solution(int[] wash, int[] dry) {
        List<Integer> front = new ArrayList<>();   // wash < dry : 앞쪽, 세탁 짧은 순
        List<Integer> back = new ArrayList<>();    // wash >= dry : 뒤쪽, 건조 긴 순
        for (int i = 0; i < wash.length; i++) {
            if (wash[i] < dry[i]) front.add(i);
            else back.add(i);
        }
        front.sort((a, b) -> Integer.compare(wash[a], wash[b]));
        back.sort((a, b) -> Integer.compare(dry[b], dry[a]));

        long washEnd = 0, dryEnd = 0;
        for (List<Integer> group : List.of(front, back)) {
            for (int i : group) {
                washEnd += wash[i];                             // 세탁은 쉬지 않고 이어진다
                dryEnd = Math.max(dryEnd, washEnd) + dry[i];    // 세탁이 끝나고 건조기가 비면 시작
            }
        }
        return dryEnd;
    }
}
