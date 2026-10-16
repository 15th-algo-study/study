import java.util.Arrays;

class Solution {
    public int[] solution(int length, int[] positions, String directions) {
        int n = positions.length;
        // 개미 번호를 위치 순으로 정렬 (충돌해도 개미들의 좌우 순서는 바뀌지 않는다)
        Integer[] order = new Integer[n];
        for (int i = 0; i < n; i++) order[i] = i;
        Arrays.sort(order, (a, b) -> Integer.compare(positions[a], positions[b]));

        // 충돌을 "서로 지나쳐 간다"로 보면 떨어지는 시각의 집합은 바로 계산된다
        int[] leftTimes = new int[n], rightTimes = new int[n];
        int left = 0, right = 0;
        for (int i = 0; i < n; i++) {
            if (directions.charAt(i) == 'L') leftTimes[left++] = positions[i];
            else rightTimes[right++] = length - positions[i];
        }
        Arrays.sort(leftTimes, 0, left);
        Arrays.sort(rightTimes, 0, right);

        int[] answer = new int[n];
        // 왼쪽 끝으로는 왼쪽에서부터 left 마리가, 빨리 떨어지는 순서대로 떨어진다
        for (int k = 0; k < left; k++) answer[order[k]] = leftTimes[k];
        // 오른쪽 끝으로는 오른쪽에서부터 right 마리가 떨어진다
        for (int k = 0; k < right; k++) answer[order[n - 1 - k]] = rightTimes[k];
        return answer;
    }
}
