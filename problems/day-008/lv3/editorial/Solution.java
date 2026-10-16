class Solution {
    public String solution(String s, int[] p, int k) {
        int n = s.length();
        char[] result = new char[n];
        boolean[] visited = new boolean[n];
        int[] cycle = new int[n]; // 현재 고리의 위치들을 임시로 담는 버퍼
        for (int start = 0; start < n; start++) {
            if (visited[start]) continue;
            // 1) start 에서 출발하는 고리를 뽑는다
            int len = 0;
            for (int cur = start; !visited[cur]; cur = p[cur]) {
                visited[cur] = true;
                cycle[len++] = cur;
            }
            // 2) 고리 위에서는 k 번이 k mod len 번과 같다
            int shift = k % len;
            for (int j = 0; j < len; j++) {
                int to = cycle[(j + shift) % len]; // cycle[j] 에 있던 글자가 k 번 뒤에 놓이는 위치
                result[to] = s.charAt(cycle[j]);
            }
        }
        return new String(result);
    }
}
