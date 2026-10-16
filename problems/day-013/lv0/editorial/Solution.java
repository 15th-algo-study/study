class Solution {
    public int[] solution(int n, int[] tickets) {
        boolean[] returned = new boolean[n + 1]; // 번호를 인덱스로 그대로 쓴다
        for (int t : tickets) returned[t] = true;

        int[] missing = new int[n - tickets.length]; // 빠진 개수는 미리 안다
        int idx = 0;
        for (int number = 1; number <= n; number++) {
            if (!returned[number]) missing[idx++] = number;
        }
        return missing;
    }
}
