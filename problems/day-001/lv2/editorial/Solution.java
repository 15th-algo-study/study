import java.util.ArrayList;
import java.util.List;

class Solution {
    public int[] solution(int[] items, String[] commands) {
        int n = items.length;
        long start = 0; // 현재 맨 앞에 있는 상품의 원래 인덱스
        List<Integer> answer = new ArrayList<>();
        for (String command : commands) {
            char op = command.charAt(0);
            long value = Long.parseLong(command.substring(2));
            switch (op) {
                case 'L' -> start = (start + value) % n;
                case 'R' -> start = Math.floorMod(start - value, n);
                default -> answer.add(items[(int) ((start + value) % n)]);
            }
        }
        return answer.stream().mapToInt(Integer::intValue).toArray();
    }
}
