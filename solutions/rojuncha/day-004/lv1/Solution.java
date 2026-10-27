class Solution {
    public int solution(int[] weights, int capacity) {
        int truck = 1;
        long weigth = 0;
        for(int i = 0; i<weights.length;i++){
            weigth += weights[i];
            if(weigth > capacity){
                weigth = weights[i];
                truck+=1;
            }
        }

        return truck;
    }
}
