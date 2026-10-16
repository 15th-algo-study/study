class Solution {
    public int solution(int[] weights) {
        int n = weights.length;
        long total = 0, rightTorque = 0;
        for (int i = 0; i < n; i++) {
            total += weights[i];
            rightTorque += (long) weights[i] * i; // 받침점이 0 일 때 오른쪽 토크
        }
        long leftTorque = 0;
        long leftWeight = 0;           // 0..p 번 추의 무게 합
        for (int p = 0; p < n; p++) {
            if (leftTorque == rightTorque) return p;
            leftWeight += weights[p];
            leftTorque += leftWeight;             // 왼쪽 추들의 거리가 모두 1 씩 늘어남
            rightTorque -= total - leftWeight;    // 오른쪽 추들의 거리가 모두 1 씩 줄어듦
        }
        return -1;
    }
}
