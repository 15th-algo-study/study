class Solution {
    public int solution(String s) {
        int answer = 0;
        String result = s.replace(" ", "").toUpperCase();

        for(char c : result.toCharArray()){
          answer += c-'A'+1;
        }
        return answer;
    }
}
