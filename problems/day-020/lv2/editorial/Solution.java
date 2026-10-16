import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Solution {
    public int[] solution(int[][] rows, String[] commands) {
        int n = rows.length, m = rows[0].length;

        // 1) 명령을 뒤에서부터 읽어 열마다 마지막 명령만 남긴다 (마지막 명령이 1순위 기준)
        List<int[]> keys = new ArrayList<>(); // {열, 방향(+1 asc / -1 desc)}
        boolean[] used = new boolean[m];
        for (int i = commands.length - 1; i >= 0; i--) {
            String[] parts = commands[i].split(" ");
            int col = Integer.parseInt(parts[0]);
            if (used[col]) continue; // 같은 열의 앞선 명령은 결과에 영향을 주지 않는다
            used[col] = true;
            keys.add(new int[]{col, parts[1].equals("asc") ? 1 : -1});
        }

        // 2) 다중 기준 비교자로 한 번만 정렬. 모든 기준이 같으면 원래 행 번호 순
        Integer[] order = new Integer[n];
        for (int i = 0; i < n; i++) order[i] = i;
        Arrays.sort(order, (a, b) -> {
            for (int[] key : keys) {
                int cmp = Integer.compare(rows[a][key[0]], rows[b][key[0]]); // ±10^9 라 뺄셈 비교는 위험
                if (cmp != 0) return key[1] * cmp;
            }
            return Integer.compare(a, b);
        });

        int[] answer = new int[n];
        for (int i = 0; i < n; i++) answer[i] = order[i];
        return answer;
    }
}
