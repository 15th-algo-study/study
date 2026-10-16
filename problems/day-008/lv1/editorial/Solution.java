import java.util.ArrayList;
import java.util.List;

class Solution {
    public String[] solution(String text, int width) {
        List<String> lines = new ArrayList<>();
        StringBuilder line = new StringBuilder();
        for (String word : text.split(" ")) {
            if (line.isEmpty()) {
                line.append(word); // 빈 줄에는 길이와 상관없이 넣는다
            } else if (line.length() + 1 + word.length() <= width) {
                line.append(' ').append(word);
            } else {
                lines.add(line.toString());
                line.setLength(0);
                line.append(word);
            }
        }
        lines.add(line.toString()); // 마지막 줄
        return lines.toArray(new String[0]);
    }
}
