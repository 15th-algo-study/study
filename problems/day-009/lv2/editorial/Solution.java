import java.util.ArrayList;
import java.util.List;

class Solution {
    public String[] solution(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean inQuotes = false;
        int i = 0, n = line.length();
        while (i < n) {
            char ch = line.charAt(i);
            if (inQuotes) {
                if (ch != '"') {
                    field.append(ch);            // 따옴표 안에서는 쉼표도 글자
                } else if (i + 1 < n && line.charAt(i + 1) == '"') {
                    field.append('"');           // "" → 큰따옴표 글자 하나
                    i++;                         // 두 글자를 함께 소비
                } else {
                    inQuotes = false;            // 홑 따옴표 → 칸 닫힘
                }
            } else if (ch == ',') {
                fields.add(field.toString());
                field.setLength(0);
            } else if (ch == '"') {
                inQuotes = true;                 // 칸 시작의 여는 따옴표
            } else {
                field.append(ch);
            }
            i++;
        }
        fields.add(field.toString()); // 마지막 칸 (쉼표로 끝났으면 빈 칸)
        return fields.toArray(new String[0]);
    }
}
