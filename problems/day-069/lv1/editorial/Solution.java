class Solution {
    public long solution(int[] changes) {
        long best = changes[0];
        long ending = changes[0]; // 지금 분에서 끝나는 구간 합의 최댓값
        for (int i = 1; i < changes.length; i++) {
            // 앞 구간에 이어 붙이거나, 여기서 새로 시작하거나
            ending = Math.max(ending + changes[i], changes[i]);
            best = Math.max(best, ending);
        }
        return best;
    }
}
