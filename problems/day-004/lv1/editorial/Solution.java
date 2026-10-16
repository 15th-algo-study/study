class Solution {
    public int solution(int[] weights, int capacity) {
        int trucks = 1;
        long load = 0; // load + w 가 int 범위를 넘을 수 있다
        for (int w : weights) {
            if (load + w > capacity) {
                trucks++;
                load = 0;
            }
            load += w;
        }
        return trucks;
    }
}
