import java.util.Arrays;

class Solution {
    public int[] solution(int[] board, int[] patch, int pos) {
        int[] result = Arrays.copyOf(board, board.length); // 원본은 그대로
        System.arraycopy(patch, 0, result, pos, patch.length); // pos 부터 덮어쓰기
        return result;
    }
}
