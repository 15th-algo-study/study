class Solution {
    public int[] solution(int[] next, int[] starts) {
        int n = next.length;
        int[] count = new int[n];   // 각 방에서 출발했을 때 지나는 서로 다른 방의 수
        byte[] state = new byte[n]; // 0 = 미방문, 1 = 지금 탐색 중, 2 = 완료
        int[] pos = new int[n];     // 탐색 중인 방이 스택의 몇 번째에 있는지
        int[] stack = new int[n];

        for (int s = 0; s < n; s++) {
            if (state[s] != 0) continue;
            int top = 0;
            int v = s;
            while (state[v] == 0) { // 새 방을 따라가며 스택에 쌓는다
                state[v] = 1;
                pos[v] = top;
                stack[top++] = v;
                v = next[v];
            }
            if (state[v] == 1) {
                // v 부터 스택 꼭대기까지가 사이클: 모두 사이클 길이가 답
                int cycleLen = top - pos[v];
                while (top > pos[v]) {
                    int u = stack[--top];
                    count[u] = cycleLen;
                    state[u] = 2;
                }
            }
            // 남은 스택은 사이클 밖의 꼬리: 다음 방의 답 + 1
            while (top > 0) {
                int u = stack[--top];
                count[u] = count[next[u]] + 1;
                state[u] = 2;
            }
        }

        int[] answer = new int[starts.length];
        for (int i = 0; i < starts.length; i++) answer[i] = count[starts[i]];
        return answer;
    }
}
