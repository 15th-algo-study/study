class Solution {
    public int[] solution(int[] queries) {
        int max = 0;
        for (int q : queries) max = Math.max(max, q);

        int[] steps = new int[max + 1]; // steps[x] = x의 빼기 횟수 (메모)
        for (int x = 1; x <= max; x++) {
            int biggest = 0;
            for (int t = x; t > 0; t /= 10) biggest = Math.max(biggest, t % 10);
            steps[x] = steps[x - biggest] + 1; // 더 작은 수의 답은 이미 적어 둠
        }

        int[] answer = new int[queries.length];
        for (int i = 0; i < queries.length; i++) answer[i] = steps[queries[i]];
        return answer;
    }
}
