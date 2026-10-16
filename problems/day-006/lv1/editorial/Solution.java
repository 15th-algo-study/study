class Solution {
    public String solution(String amount) {
        StringBuilder sb = new StringBuilder(amount.length() + amount.length() / 3 + 1);
        int start = 0;
        if (amount.charAt(0) == '-') {
            sb.append('-');
            start = 1;
        }
        int len = amount.length() - start; // 숫자 부분의 길이
        for (int i = 0; i < len; i++) {
            if (i > 0 && (len - i) % 3 == 0) sb.append(','); // 뒤에 남은 자릿수가 3의 배수인 곳
            sb.append(amount.charAt(start + i));
        }
        return sb.toString();
    }
}
