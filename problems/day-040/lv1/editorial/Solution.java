class Solution {
    public int[] solution(int[] pos, int d) {
        int n = pos.length;
        int[] answer = new int[n];
        int lo = 0, hi = 0; // [lo, hi] = i 에서 보이는 등대들의 인덱스 범위
        for (int i = 0; i < n; i++) {
            while (pos[i] - pos[lo] > d) lo++;                 // 너무 먼 왼쪽은 버린다
            if (hi < i) hi = i;
            while (hi + 1 < n && pos[hi + 1] - pos[i] <= d) hi++; // 오른쪽으로 보이는 데까지
            answer[i] = hi - lo; // 구간 길이 - 1 (자기 자신 제외)
        }
        return answer;
    }
}
