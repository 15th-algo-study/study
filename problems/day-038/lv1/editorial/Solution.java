class Solution {
    public int[] solution(int[] runners, int[] stations) {
        int[] answer = new int[runners.length];
        int j = 0; // stations[0..j-1] 은 현재 주자 위치 이하
        for (int i = 0; i < runners.length; i++) {
            while (j < stations.length && stations[j] <= runners[i]) j++;
            answer[i] = j == 0 ? -1 : runners[i] - stations[j - 1];
        }
        return answer;
    }
}
