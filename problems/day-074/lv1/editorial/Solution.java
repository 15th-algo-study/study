class Solution {
    public int solution(int[] altitudes) {
        int up = 1;   // 마지막이 올라감으로 끝나는 오르내림 기록의 최대 길이
        int down = 1; // 마지막이 내려감으로 끝나는 오르내림 기록의 최대 길이
        for (int i = 1; i < altitudes.length; i++) {
            if (altitudes[i] > altitudes[i - 1]) up = down + 1;        // 내려감 뒤에 올라감을 붙인다
            else if (altitudes[i] < altitudes[i - 1]) down = up + 1;   // 올라감 뒤에 내려감을 붙인다
        }
        return Math.max(up, down);
    }
}
