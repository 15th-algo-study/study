class Solution {
    public String solution(String s, int k) {
        StringBuilder sb = new StringBuilder();

        for(int i=0; i<s.length();i++){
            for(int j=0; j<k;j++){
                sb.append(s.charAt(i));
            }
        }

        return sb.toString();
    }
}
