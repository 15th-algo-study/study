class Solution {
    public int[] solution(int[] first, int[] second) {
        int n = first.length;
        int[] answer = new int[n];
        for (int i = 0; i < n; i++) {
            int mixed = 0;
            for (int shift = 0; shift < 32; shift += 8) { // B, G, R, A 순서
                int x = (first[i] >>> shift) & 0xFF;     // >>> : 음수여도 0 으로 채움
                int y = (second[i] >>> shift) & 0xFF;
                mixed |= ((x + y) / 2) << shift;
            }
            answer[i] = mixed;
        }
        return answer;
    }
}
