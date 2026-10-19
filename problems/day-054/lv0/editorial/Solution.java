class Solution {
    public double[] solution(int[] heights, int rate, int years, int limit) {
        double factor = Math.pow(1 + rate / 100.0, years); // rate / 100 은 정수 나눗셈이라 0 이 된다
        double[] answer = new double[heights.length];
        for (int i = 0; i < heights.length; i++) {
            answer[i] = Math.min(limit, heights[i] * factor); // 한계를 넘으면 limit
        }
        return answer;
    }
}
