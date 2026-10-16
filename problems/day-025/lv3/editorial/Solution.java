class Solution {
    public int[] solution(int[] snow, int[][] queries) {
        int n = snow.length, q = queries.length;

        // 질의를 d 별로 묶는다 (연결 리스트 버킷: head[d] -> 질의 번호, next[j] -> 같은 d 의 다음 질의)
        int[] head = new int[n], next = new int[q];
        java.util.Arrays.fill(head, -1);
        for (int j = 0; j < q; j++) {
            int d = queries[j][0];
            next[j] = head[d];
            head[d] = j;
        }

        int[] answer = new int[q];
        int[] stack = new int[n]; // 날짜 인덱스. 아래(0)로 갈수록 날짜가 크고 적설량이 엄격히 크다
        int top = 0;
        for (int i = n - 1; i >= 0; i--) {
            // 스택에는 i+1..n-1 의 "다음으로 더 높은 날" 사슬이 들어 있다
            for (int j = head[i]; j != -1; j = next[j]) {
                int x = queries[j][1];
                if (top == 0 || snow[stack[0]] < x) {
                    answer[j] = -1;
                    continue;
                }
                // 값 >= x 인 원소 중 가장 위(인덱스가 큰 쪽)를 찾는다: snow[stack[p]] 는 p 가 커질수록 작아진다
                int lo = 0, hi = top - 1;
                while (lo < hi) {
                    int mid = (lo + hi + 1) >>> 1;
                    if (snow[stack[mid]] >= x) lo = mid; else hi = mid - 1;
                }
                answer[j] = stack[lo];
            }
            while (top > 0 && snow[stack[top - 1]] <= snow[i]) top--; // i 에 가려지는 날은 후보가 아니다
            stack[top++] = i;
        }
        return answer;
    }
}
