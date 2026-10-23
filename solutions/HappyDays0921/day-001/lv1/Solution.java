class Solution {
    public int solution(int[] heights) {
        int answer = 0;
        //가장 긴 오르만 구간의 길이를 반환

        //heights 배열을 돌면서
        //오르막인상태면 길이 측정
        //오르막이 끝난 시점에 max와 비교해서 크면 max 수정

        //제일 긴 오르막 구간길이
        int max=1;
        int cnt=1;
        for(int i=1;i<heights.length;i++){
            int compare = heights[i-1]-heights[i];
            //같은경우
            
            //오르막인 경우
            if(compare<0){
                cnt++;
            }
            //오르막이 끝난경우
            //if(compare>=0)
            else{
                //max값보다 크면 max변경
                if(max<cnt){
                    max = cnt;
                }
                //오르막이 끝났기때문에 cnt 초기화
                cnt=1;
            }
        }

        //이거는 루프 끝났을때 오르막으로 끝나면 max를 안넣어주는 문제때문에
        if(max<cnt)
            max = cnt;
        
        answer = max;
        return answer;
    }
}
