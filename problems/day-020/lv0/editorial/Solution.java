import java.util.Arrays;

class Solution {
    public int[][] solution(int[][] stars) {
        int[][] answer = stars.clone(); // 행 참조만 복사 — 원본 배열의 순서는 유지된다
        Arrays.sort(answer, (a, b) -> {
            // a[0] - b[0] 은 ±10^9 범위에서 넘칠 수 있으므로 Integer.compare 를 쓴다
            if (a[0] != b[0]) return Integer.compare(a[0], b[0]);
            return Integer.compare(a[1], b[1]);
        });
        return answer;
    }
}
