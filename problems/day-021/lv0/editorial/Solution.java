import java.util.ArrayList;
import java.util.List;

class Solution {
    public int[] solution(int n, int[] leave) {
        List<Integer> line = new ArrayList<>();
        for (int i = 1; i <= n; i++) line.add(i);
        for (int position : leave) {
            line.remove(position - 1); // int 를 넘기면 "인덱스" 삭제 (Integer 면 값 삭제가 되니 주의)
        }
        int[] answer = new int[line.size()];
        for (int i = 0; i < answer.length; i++) answer[i] = line.get(i);
        return answer;
    }
}
