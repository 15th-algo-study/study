import java.util.*;
class Solution {
    public int[] solution(String[] soccer, String[] band) {
        int[] answer = {0, 0, 0};

        Set<String> soccerSet = new HashSet<>(Arrays.asList(soccer));
        Set<String> bandSet = new HashSet<>(Arrays.asList(band));
        Set<String> both = new HashSet<>(soccerSet);

        both.retainAll(bandSet);
        answer[2] = both.size();

        soccerSet.removeAll(both);
        answer[0] = soccerSet.size();

        bandSet.removeAll(both);
        answer[1] = bandSet.size();

        return answer;
    }
}
