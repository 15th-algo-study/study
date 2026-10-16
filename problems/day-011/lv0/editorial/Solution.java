class Solution {
    public int[] solution(int[] temps) {
        int hot = 0, cold = 0; // 0번 날을 기준으로 시작
        for (int i = 1; i < temps.length; i++) {
            if (temps[i] > temps[hot]) hot = i;   // 더 클 때만 바꿔야 가장 앞선 날이 남는다
            if (temps[i] < temps[cold]) cold = i;
        }
        return new int[]{hot, cold};
    }
}
