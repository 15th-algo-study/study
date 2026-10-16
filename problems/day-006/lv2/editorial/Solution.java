import java.util.ArrayDeque;
import java.util.Deque;

class Solution {
    public int solution(String doc) {
        Deque<String> open = new ArrayDeque<>(); // 아직 닫히지 않은 태그 이름
        int maxDepth = 0;
        int i = 0, n = doc.length();
        while (i < n) {
            if (doc.charAt(i) != '<') { // 본문 글자
                i++;
                continue;
            }
            int close = doc.indexOf('>', i);
            if (doc.charAt(i + 1) == '/') {                 // </name>
                String name = doc.substring(i + 2, close);
                if (open.isEmpty() || !open.peek().equals(name)) return -1;
                open.pop();
            } else if (doc.charAt(close - 1) == '/') {      // <name/>
                maxDepth = Math.max(maxDepth, open.size() + 1);
            } else {                                        // <name>
                open.push(doc.substring(i + 1, close));
                maxDepth = Math.max(maxDepth, open.size());
            }
            i = close + 1;
        }
        return open.isEmpty() ? maxDepth : -1;
    }
}
