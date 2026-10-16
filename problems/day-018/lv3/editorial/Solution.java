import java.util.Arrays;

class Solution {
    public int[] solution(int[][] stations, int[] cars) {
        int n = stations.length, m = cars.length;
        long[] lefts = new long[n], rights = new long[n]; // p + d 는 int 범위를 넘을 수 있다
        for (int i = 0; i < n; i++) {
            lefts[i] = (long) stations[i][0] - stations[i][1];
            rights[i] = (long) stations[i][0] + stations[i][1];
        }
        Arrays.sort(lefts);
        Arrays.sort(rights);

        // 자동차도 위치 순으로 정렬해 두 포인터를 앞으로만 움직인다 (오프라인 질의)
        Integer[] order = new Integer[m];
        for (int j = 0; j < m; j++) order[j] = j;
        Arrays.sort(order, (x, y) -> Integer.compare(cars[x], cars[y]));

        int[] answer = new int[m];
        int a = 0, b = 0; // a: left <= q 인 방송국 수, b: right < q 인 방송국 수
        for (int idx : order) {
            long q = cars[idx];
            while (a < n && lefts[a] <= q) a++;
            while (b < n && rights[b] < q) b++;
            answer[idx] = a - b; // (왼쪽 끝이 q 이하) − (오른쪽 끝이 q 미만) = q 를 덮는 수
        }
        return answer;
    }
}
