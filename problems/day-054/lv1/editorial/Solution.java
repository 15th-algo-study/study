import java.util.PriorityQueue;

class Solution {
    public String[] solution(String[] names, int[] stock, int m) {
        // 상자 수 적은 순 → 이름 사전순 → 번호 작은 순
        PriorityQueue<Integer> heap = new PriorityQueue<>((a, b) -> {
            if (stock[a] != stock[b]) return Integer.compare(stock[a], stock[b]);
            int byName = names[a].compareTo(names[b]);
            return byName != 0 ? byName : Integer.compare(a, b);
        });
        for (int i = 0; i < names.length; i++) heap.offer(i);

        String[] answer = new String[m];
        for (int box = 0; box < m; box++) {
            int i = heap.poll();   // 꺼낸 뒤에 값을 바꾸고
            answer[box] = names[i];
            stock[i]++;
            heap.offer(i);         // 다시 넣는다
        }
        return answer;
    }
}
