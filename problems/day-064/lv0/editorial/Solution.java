class Solution {
    public int solution(String[] codes) {
        int answer = 0;
        for (String code : codes) {
            // 하이픈을 빼고 숫자만 모은다
            int[] digits = new int[code.length()];
            int m = 0;
            for (int i = 0; i < code.length(); i++) {
                char ch = code.charAt(i);
                if (ch != '-') digits[m++] = ch - '0'; // 글자 '7' → 숫자 7
            }
            int sum = 0;
            for (int i = 0; i < m - 1; i++) sum += (i + 1) * digits[i];
            if (sum % 10 == digits[m - 1]) answer++;
        }
        return answer;
    }
}
