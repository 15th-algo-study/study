class Solution {
    public String[] solution(String[] lines) {
        int[] counter = new int[7]; // counter[1..6]: 수준별 번호 칸
        String[] result = new String[lines.length];
        for (int idx = 0; idx < lines.length; idx++) {
            String line = lines[idx];
            int d = line.indexOf(' ');          // '#' 의 개수 = 수준
            counter[d]++;
            for (int lv = d + 1; lv <= 6; lv++) counter[lv] = 0; // 더 깊은 수준은 처음부터 다시
            StringBuilder sb = new StringBuilder();
            for (int lv = 1; lv <= d; lv++) {
                if (lv > 1) sb.append('.');
                sb.append(counter[lv]);
            }
            sb.append(' ').append(line, d + 1, line.length()); // 제목
            result[idx] = sb.toString();
        }
        return result;
    }
}
