import java.util.ArrayDeque;
import java.util.Deque;

class Solution {
    private static final long MOD = 1_000_000_007L;

    public int solution(String expression) {
        Deque<Long> values = new ArrayDeque<>();
        Deque<Character> ops = new ArrayDeque<>();
        int i = 0, n = expression.length();
        while (i < n) {
            char ch = expression.charAt(i);
            if (Character.isDigit(ch)) {
                long v = 0;
                while (i < n && Character.isDigit(expression.charAt(i))) v = v * 10 + (expression.charAt(i++) - '0');
                values.push(v % MOD);
                continue;
            }
            if (ch == '(') {
                ops.push(ch);
            } else if (ch == ')') {
                while (ops.peek() != '(') apply(values, ops.pop());
                ops.pop(); // '(' 제거
            } else {
                // 우선순위가 같거나 높은 연산을 먼저 처리 → 왼쪽 결합
                while (!ops.isEmpty() && ops.peek() != '(' && precedence(ops.peek()) >= precedence(ch)) {
                    apply(values, ops.pop());
                }
                ops.push(ch);
            }
            i++;
        }
        while (!ops.isEmpty()) apply(values, ops.pop());
        return (int) values.pop().longValue();
    }

    private int precedence(char op) {
        return op == '*' ? 2 : 1;
    }

    /** 모든 값을 [0, MOD) 로 유지한다. */
    private void apply(Deque<Long> values, char op) {
        long b = values.pop(), a = values.pop();
        long r = switch (op) {
            case '+' -> (a + b) % MOD;
            case '-' -> (a - b + MOD) % MOD;
            default -> a * b % MOD;
        };
        values.push(r);
    }
}
