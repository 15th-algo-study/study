class Solution {
    public int[] solution(int[] colors) {
        int n = colors.length;
        int[] color = new int[n]; // 스택: 색
        int[] count = new int[n]; // 스택: 같은 색이 연속으로 쌓인 개수 (1 또는 2)
        int top = 0;
        for (int c : colors) {
            if (top > 0 && color[top - 1] == c) {
                if (++count[top - 1] == 3) top--; // 세 개가 되면 통째로 터진다
            } else {
                color[top] = c;
                count[top] = 1;
                top++;
            }
        }
        int total = 0;
        for (int i = 0; i < top; i++) total += count[i];
        int[] answer = new int[total];
        int idx = 0;
        for (int i = 0; i < top; i++) {
            for (int k = 0; k < count[i]; k++) answer[idx++] = color[i];
        }
        return answer;
    }
}
