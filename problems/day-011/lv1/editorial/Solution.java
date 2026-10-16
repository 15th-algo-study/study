import java.util.HashSet;
import java.util.Set;

class Solution {
    public int[] solution(String[] soccer, String[] band) {
        Set<String> a = new HashSet<>();
        for (String name : soccer) a.add(name); // 같은 이름은 한 번만 들어간다
        Set<String> b = new HashSet<>();
        for (String name : band) b.add(name);

        int both = 0;
        for (String name : a) {
            if (b.contains(name)) both++;
        }
        return new int[]{a.size() - both, b.size() - both, both};
    }
}
