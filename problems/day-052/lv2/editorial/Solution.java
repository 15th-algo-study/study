import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

class Solution {
    public int[] solution(int[][] events) {
        int n = events.length;
        boolean[] removed = new boolean[n + 2]; // 등록 번호 ≤ 기록 수
        PriorityQueue<int[]> heap = new PriorityQueue<>((a, b) -> Integer.compare(a[0], b[0])); // [가격, 번호]
        int registered = 0;
        List<Integer> answer = new ArrayList<>();
        for (int[] e : events) {
            int t = e[0], v = e[1];
            if (t == 0) {
                heap.offer(new int[]{v, ++registered});
            } else if (t == 1) {
                if (v >= 1 && v <= registered) removed[v] = true; // 힙은 건드리지 않고 표시만 (지연 삭제)
            } else {
                while (!heap.isEmpty() && removed[heap.peek()[1]]) heap.poll(); // 내려간 상품은 이제 버린다
                answer.add(heap.isEmpty() ? -1 : heap.peek()[0]);
            }
        }
        return answer.stream().mapToInt(Integer::intValue).toArray();
    }
}
