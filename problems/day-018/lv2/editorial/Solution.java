import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Solution {
    public int[] solution(int[][] athletes) {
        int n = athletes.length;
        Integer[] order = new Integer[n];
        for (int i = 0; i < n; i++) order[i] = i;

        // 1) 팀별로 묶이도록 (팀 ↑, 점수 ↓, 번호 ↑) 정렬
        Arrays.sort(order, (a, b) -> {
            if (athletes[a][0] != athletes[b][0]) return Integer.compare(athletes[a][0], athletes[b][0]);
            if (athletes[a][1] != athletes[b][1]) return Integer.compare(athletes[b][1], athletes[a][1]);
            return Integer.compare(a, b);
        });

        // 2) 각 팀의 앞 두 명만 고른다
        List<Integer> finalists = new ArrayList<>();
        int prevTeam = -1, taken = 0;
        for (int idx : order) {
            if (athletes[idx][0] != prevTeam) { // 새 팀 시작
                prevTeam = athletes[idx][0];
                taken = 0;
            }
            if (taken < 2) {
                finalists.add(idx);
                taken++;
            }
        }

        // 3) 명단은 (점수 ↓, 팀 ↑, 번호 ↑)
        finalists.sort((a, b) -> {
            if (athletes[a][1] != athletes[b][1]) return Integer.compare(athletes[b][1], athletes[a][1]);
            if (athletes[a][0] != athletes[b][0]) return Integer.compare(athletes[a][0], athletes[b][0]);
            return Integer.compare(a, b);
        });
        return finalists.stream().mapToInt(Integer::intValue).toArray();
    }
}
