import java.util.*;

class Solution {
    public int[] solution(int[] ops) {
        Deque<Integer> stack = new ArrayDeque<>();

        for(int value : ops){
            if(value>0){
                stack.add(value);
            }else if(!stack.isEmpty()){
                stack.pollLast();
            }
        }
        int[] answer = new int[stack.size()];
        int i = 0;
        for (int plate : stack) answer[i++] = plate; 
        return answer;
    }
}
