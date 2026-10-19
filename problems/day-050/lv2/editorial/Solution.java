import java.util.Arrays;

class Solution {
    public String solution(String[] tiles) {
        String[] sorted = tiles.clone();
        // a + b 가 b + a 보다 크면 a 가 앞 (내림차순)
        Arrays.sort(sorted, (a, b) -> (b + a).compareTo(a + b));
        StringBuilder sb = new StringBuilder();
        for (String tile : sorted) sb.append(tile);
        return sb.toString();
    }
}
