import java.util.Arrays;

class Solution {
    public int[] solution(int[][] catches) {
        int n = catches.length;
        Integer[] order = new Integer[n]; // 비교자를 쓰려면 Integer[] 이어야 한다
        for (int i = 0; i < n; i++) order[i] = i;

        Arrays.sort(order, (a, b) -> {
            if (catches[a][0] != catches[b][0]) return Integer.compare(catches[b][0], catches[a][0]); // 무게 내림차순
            if (catches[a][1] != catches[b][1]) return Integer.compare(catches[b][1], catches[a][1]); // 마릿수 내림차순
            return Integer.compare(a, b); // 번호 오름차순
        });

        int[] answer = new int[n];
        for (int i = 0; i < n; i++) answer[i] = order[i];
        return answer;
    }
}
