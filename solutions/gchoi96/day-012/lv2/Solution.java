import java.util.*;

class Solution {
    private static String generateKey(String word){
        char[] chars = word.toCharArray();
        Arrays.sort(chars);
        StringBuilder sb = new StringBuilder();
        for(char ch : chars){
            sb.append(ch);
        }

        return sb.toString();
    }
    public int[] solution(String[] words) {
        Map<String, Integer> map = new HashMap<>();
        int groupCount = 0;
        int maxCount = 0;

        for(String word : words){
            String key = generateKey(word);
            map.put(key, map.getOrDefault(key, 0) + 1);
        }

        for(int value : map.values()){
            groupCount++;
            maxCount = Math.max(maxCount, value);
        }
        
        return new int[]{groupCount, maxCount};
    }
}
