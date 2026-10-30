class Solution {
    public int solution(String[] passwords) {
        int answer = 0;
        for(String s : passwords){
            int count = 1;
            int length = s.length();
            if(length>=8 && length<=20){
                if(s.matches("[0-9a-zA-Z!@#$%^&*]+")       
                && s.matches(".*[0-9].*") && s.matches(".*[a-z].*")
                && s.matches(".*[A-Z].*") && s.matches(".*[!@#$%^&*].*")){
                    boolean ok = true;                        
                    for(int i=1; i<length; i++){
                        if(s.charAt(i) == s.charAt(i-1)){
                            count++;
                        }else{
                            count = 1;
                        }
                        if(count > 2){
                            ok = false;
                            break;
                        }
                    }
                    if(ok){
                        answer += 1;                          
                    }
                }
            }
        }
        return answer;
    }
}