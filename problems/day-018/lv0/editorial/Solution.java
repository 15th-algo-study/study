import java.util.Arrays;

class Solution {
    public int[][] solution(int[][] shelves) {
        int[][] answer = new int[shelves.length][];
        for (int i = 0; i < shelves.length; i++) {
            answer[i] = shelves[i].clone(); // 원본을 바꾸지 않도록 복사
            Arrays.sort(answer[i]);         // 각 칸(1차원 배열)을 따로 정렬
        }
        return answer;
    }
}
