class Solution {
    public long solution(String s) {
        long answer = 0;
        long now = 0;
        for(int i=0; i<s.length();i++){
            char ch = s.charAt(i);
            if(ch >= '0' && ch <= '9'){
                now = now *10 + (ch-'0');
            }else{
                answer += now;
                now = 0;
            }

        }
        return answer+now;
    }
}
