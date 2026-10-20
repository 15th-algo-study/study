class Solution {
    public int[] solution(int[] arrows) {
        int n = arrows.length;
        int[] moves = new int[n]; // moves[i] = i번 칸에서 출발했을 때의 이동 횟수 (메모)
        for (int i = n - 1; i >= 0; i--) {   // 뒤쪽 칸의 답을 먼저 구해 둔다
            int next = i + arrows[i];
            moves[i] = (next >= n) ? 1 : 1 + moves[next];
        }
        return moves;
    }
}
