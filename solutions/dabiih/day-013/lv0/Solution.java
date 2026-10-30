import java.util.*;
class Solution {
    public int[] solution(int n, int[] tickets) {

       List<Integer> answerList = new ArrayList<>();
        Set<Integer> ticketsSet = new HashSet<>(Arrays.stream(tickets).boxed().toList());

       for (int i=1 ; i<=n ; i++){
            if(!ticketsSet.contains(i)){answerList.add(i);}
       }

       int[] answer = answerList.stream().mapToInt(Integer::intValue).toArray();
       return answer;

    }
}
