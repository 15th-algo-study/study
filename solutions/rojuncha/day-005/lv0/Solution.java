class Solution {
    public String solution(String s) {
        int max =0;
        int [] count = new int[26];
        for(int i=0;i<s.length();i++){
            count[s.charAt(i)-'a']++;
        }
        for(int i=1;i<26;i++){
            if(count[i]>count[max]){
                max = i;
            }
        }
    
    return String.valueOf((char) ('a' + max));   
    }
     
}