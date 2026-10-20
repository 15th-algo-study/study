class Solution {
    public String[] solution(long[] numbers) {
        String[] answer = new String[numbers.length];
        for (int i = 0; i < numbers.length; i++) {
            String digits = String.valueOf(numbers[i]); // 4305 → "4305"
            if (numbers[i] == 0) {
                answer[i] = "0";
                continue;
            }
            StringBuilder sb = new StringBuilder();
            int len = digits.length();
            for (int k = 0; k < len; k++) {
                char d = digits.charAt(k);
                if (d == '0') continue; // 0인 자리는 쓰지 않는다
                if (sb.length() > 0) sb.append('+');
                sb.append(d).append("0".repeat(len - 1 - k)); // 뒤에 남은 자릿수만큼 0
            }
            answer[i] = sb.toString();
        }
        return answer;
    }
}
