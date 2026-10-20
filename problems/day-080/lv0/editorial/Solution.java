class Solution {
    public int[] solution(int[] codes) {
        int[] answer = new int[codes.length];
        for (int i = 0; i < codes.length; i++) {
            int p = 0;
            for (int x = codes[i]; x != 0; x >>= 1) p ^= x; // g ^ (g>>1) ^ (g>>2) ^ ...
            answer[i] = p;
        }
        return answer;
    }
}
