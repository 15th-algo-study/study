class Solution {
    public int[] solution(int[] levels) {
        int n = levels.length;
        int[] answer = new int[n]; // 기본값 0 = 더 높아지는 날 없음
        int[] stack = new int[n];  // 아직 답을 못 찾은 날짜 인덱스 (위로 갈수록 수위가 낮거나 같다)
        int top = 0;
        for (int j = 0; j < n; j++) {
            while (top > 0 && levels[stack[top - 1]] < levels[j]) {
                int i = stack[--top];
                answer[i] = j - i; // j 가 i 보다 처음으로 높아진 날
            }
            stack[top++] = j;
        }
        return answer;
    }
}
