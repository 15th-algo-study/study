import java.util.Arrays;

class Solution {
    public int[] solution(int[] members, int[] queries) {
        int[] answer = new int[queries.length];
        for (int i = 0; i < queries.length; i++) {
            int idx = Arrays.binarySearch(members, queries[i]); // 없으면 음수
            answer[i] = idx < 0 ? -1 : idx;
        }
        return answer;
    }
}
