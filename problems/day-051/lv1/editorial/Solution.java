import java.util.PriorityQueue;

class Solution {
    public int[] solution(int[] bright, int k) {
        PriorityQueue<Integer> topK = new PriorityQueue<>(); // 큰 값 k개만 보관하는 최소 힙
        int[] answer = new int[bright.length];
        for (int i = 0; i < bright.length; i++) {
            topK.offer(bright[i]);
            if (topK.size() > k) topK.poll(); // k개를 넘으면 가장 작은 값은 버린다
            answer[i] = topK.size() == k ? topK.peek() : -1;
        }
        return answer;
    }
}
