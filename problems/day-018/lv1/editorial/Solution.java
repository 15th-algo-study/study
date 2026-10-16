import java.util.Arrays;

class Solution {
    public int[] solution(int[] records) {
        int n = records.length;
        Integer[] order = new Integer[n];
        for (int i = 0; i < n; i++) order[i] = i;
        Arrays.sort(order, (a, b) -> Integer.compare(records[a], records[b])); // 기록이 짧은 순

        int[] answer = new int[n];
        int rank = 1;
        for (int k = 0; k < n; k++) {
            // 바로 앞 선수와 기록이 다르면 순위는 (앞선 선수 수 + 1) = k + 1, 같으면 앞 선수의 순위를 물려받는다
            if (k > 0 && records[order[k]] != records[order[k - 1]]) rank = k + 1;
            answer[order[k]] = rank; // 원래 선수 번호 자리에 적는다
        }
        return answer;
    }
}
