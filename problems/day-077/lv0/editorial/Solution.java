class Solution {
    public int solution(int n, String[] commands) {
        int state = 0;
        int all = (1 << n) - 1; // 0 ~ n-1 번 비트가 모두 1
        for (String cmd : commands) {
            if (cmd.equals("INVERT")) {
                state ^= all;
                continue;
            }
            String[] parts = cmd.split(" ");
            int bit = 1 << Integer.parseInt(parts[1]);
            switch (parts[0]) {
                case "ON" -> state |= bit;
                case "OFF" -> state &= ~bit;
                default -> state ^= bit; // FLIP
            }
        }
        return state;
    }
}
