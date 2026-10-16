import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

class Solution {
    // 10^18 보다 크면 모두 같은 값으로 취급 (k ≤ 10^18 이므로 정확한 길이가 필요 없다)
    private static final long CAP = 2_000_000_000_000_000_000L;

    /** 글자 하나(ch) 또는 반복 블록(times, body) */
    private record Node(char ch, long times, List<Node> body, long length) {}

    public String solution(String s, long k) {
        List<Node> root = parse(s);
        List<Node> seq = root;
        while (true) {
            for (Node node : seq) {
                if (k > node.length()) {
                    k -= node.length();
                    continue;
                }
                if (node.body() == null) return String.valueOf(node.ch());
                long inner = sum(node.body());
                k = (k - 1) % inner + 1; // 반복 블록 안에서는 한 번 분량 안의 위치만 중요
                seq = node.body();
                break;
            }
        }
    }

    /** 스택으로 파싱하며 각 블록의 (상한이 걸린) 길이를 계산한다. 재귀를 쓰지 않아 깊은 중첩도 안전하다. */
    private List<Node> parse(String s) {
        Deque<List<Node>> stack = new ArrayDeque<>();
        Deque<Long> times = new ArrayDeque<>();
        List<Node> cur = new ArrayList<>();
        int i = 0;
        while (i < s.length()) {
            char ch = s.charAt(i);
            if (Character.isDigit(ch)) {
                long x = 0;
                while (Character.isDigit(s.charAt(i))) x = x * 10 + (s.charAt(i++) - '0');
                i++; // '['
                stack.push(cur);
                times.push(x);
                cur = new ArrayList<>();
            } else if (ch == ']') {
                long x = times.pop();
                long len = mul(sum(cur), x);
                List<Node> body = cur;
                cur = stack.pop();
                cur.add(new Node(' ', x, body, len));
                i++;
            } else {
                cur.add(new Node(ch, 1, null, 1));
                i++;
            }
        }
        return cur;
    }

    private long sum(List<Node> seq) {
        long total = 0;
        for (Node n : seq) total = Math.min(CAP, total + n.length());
        return total;
    }

    private long mul(long a, long b) {
        return a > CAP / b ? CAP : Math.min(CAP, a * b);
    }
}
