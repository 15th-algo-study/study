import java.util.*;

class Solution {
    public List<Integer> solution(int[] priority, int[] general, int k) {
        List<Integer> list = new ArrayList<>();
        int i = 0, u = 0;
        while (i < priority.length || u < general.length) {
            for (int h = 0; h < k && i < priority.length; h++) list.add(priority[i++]);
            if (u < general.length) list.add(general[u++]);
        }
        return list;
    }
}