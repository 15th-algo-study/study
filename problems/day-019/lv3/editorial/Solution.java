import java.util.Arrays;

class Solution {
    public int solution(int[] cars) {
        int n = cars.length;
        // 1) 좌표 압축: 정렬 순서로 각 차량이 가야 할 자리를 구한다
        Integer[] order = new Integer[n];
        for (int i = 0; i < n; i++) order[i] = i;
        Arrays.sort(order, (a, b) -> Integer.compare(cars[a], cars[b]));
        int[] target = new int[n]; // target[i] = 자리 i 의 차량이 가야 할 자리
        for (int k = 0; k < n; k++) target[order[k]] = k;

        // 2) 순열의 사이클 수를 센다 — 답은 n - (사이클 수)
        boolean[] visited = new boolean[n];
        int cycles = 0;
        for (int i = 0; i < n; i++) {
            if (visited[i]) continue;
            cycles++;
            for (int j = i; !visited[j]; j = target[j]) visited[j] = true; // 재귀 대신 반복문
        }
        return n - cycles;
    }
}
