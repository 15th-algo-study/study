import java.util.Arrays;
import java.util.PriorityQueue;

class Solution {
    public long solution(int[][] lights) {
        int n = lights.length;
        long[][] byStart = new long[n][]; // [시작, 끝, 밝기]
        long[] raw = new long[2 * n];     // 이벤트 좌표 (시작점과 끝점)
        for (int i = 0; i < n; i++) {
            long s = lights[i][0], e = s + lights[i][1];
            byStart[i] = new long[]{s, e, lights[i][2]};
            raw[2 * i] = s;
            raw[2 * i + 1] = e;
        }
        Arrays.sort(byStart, (a, b) -> Long.compare(a[0], b[0]));
        long[] xs = Arrays.stream(raw).sorted().distinct().toArray(); // 중복 좌표 제거

        // 켜진 조명: 밝기 큰 순 (지연 삭제: 끝난 조명은 맨 위에 올라왔을 때 버린다)
        PriorityQueue<long[]> on = new PriorityQueue<>((a, b) -> Long.compare(b[2], a[2]));
        long area = 0;
        int next = 0; // 아직 힙에 넣지 않은 조명 중 첫 번째
        for (int i = 0; i + 1 < xs.length; i++) {
            long x = xs[i];
            while (!on.isEmpty() && on.peek()[1] <= x) on.poll(); // x 에서 이미 끝난 조명
            while (next < n && byStart[next][0] == x) on.offer(byStart[next++]);
            if (!on.isEmpty()) area += (xs[i + 1] - x) * on.peek()[2]; // [x, 다음 좌표) 의 밝기는 최대 밝기
        }
        return area;
    }
}
