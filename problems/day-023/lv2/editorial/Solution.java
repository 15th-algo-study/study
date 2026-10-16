class Solution {
    public int[] solution(int[] ops) {
        int queries = 0;
        for (int op : ops) if (op == -1) queries++;
        int[] answer = new int[queries];

        int[] maxStack = new int[ops.length]; // maxStack[i] = 아래에서 i 번째 상자까지 중 가장 무거운 무게
        int top = 0, idx = 0;
        for (int op : ops) {
            if (op > 0) {
                int prevMax = top > 0 ? maxStack[top - 1] : 0;
                maxStack[top++] = Math.max(op, prevMax);
            } else if (op == 0) {
                if (top > 0) top--;
            } else {
                answer[idx++] = top > 0 ? maxStack[top - 1] : 0;
            }
        }
        return answer;
    }
}
