class Solution {
    public long solution(int[] fees) {
        long take = 0; // 직전 자리를 빌려줬을 때의 최대 합
        long skip = 0; // 직전 자리를 비워 뒀을 때의 최대 합
        for (int fee : fees) {
            long nextTake = skip + fee;          // 이번 자리를 빌려주려면 앞 자리는 비어 있어야 함
            long nextSkip = Math.max(take, skip); // 이번 자리를 비워 둠
            take = nextTake;
            skip = nextSkip;
        }
        return Math.max(take, skip);
    }
}
