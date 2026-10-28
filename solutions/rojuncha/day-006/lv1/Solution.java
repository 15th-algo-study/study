class Solution {
    public String solution(String amount) {
        StringBuilder sb = new StringBuilder();
        int start = 0;
        if(amount.charAt(0) =='-'){
            sb.append('-');
            start++;
        }
        int length = amount.length()-start;

        for(int i=0; i<length;i++){
            if (i > 0 && (length - i) % 3 == 0) sb.append(','); 
            sb.append(amount.charAt(start + i));
        }
        

        return sb.toString();
    }
}
