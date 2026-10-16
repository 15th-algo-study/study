class Solution {
    public int[] solution(int[][] ops) {
        int n = ops.length;
        int[] kept = new int[n]; // 뒤에서부터 모은 "살아남은 획"
        int cnt = 0;
        int skip = 0;            // 앞으로 몇 개의 명령을 취소된 것으로 볼지
        for (int i = n - 1; i >= 0; i--) {
            if (skip > 0) {
                skip--;          // 취소된 명령: 되돌리기여도 효력이 없다
            } else if (ops[i][0] == 2) {
                skip += ops[i][1];
            } else {
                kept[cnt++] = ops[i][1];
            }
        }
        int[] answer = new int[cnt];
        for (int i = 0; i < cnt; i++) answer[i] = kept[cnt - 1 - i]; // 그린 순서로 뒤집기
        return answer;
    }
}
