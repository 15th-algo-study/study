class Solution {
    public int solution(String s) {
        int answer = 0;
        for (char ch : s.toCharArray()) {
            if ("aeiouAEIOU".indexOf(ch) >= 0) answer++;
        }
        return answer;
    }
}
