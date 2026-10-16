class Solution {
    public int[] solution(int[] arrivals) {
        int n = arrivals.length;
        int[] suffixMin = new int[n + 1]; // suffixMin[i] = arrivals[i..n-1] 의 최솟값
        suffixMin[n] = Integer.MAX_VALUE;
        for (int i = n - 1; i >= 0; i--) suffixMin[i] = Math.min(arrivals[i], suffixMin[i + 1]);

        int[] stack = new int[n];
        int top = 0;
        int[] answer = new int[n];
        int out = 0;
        for (int i = 0; i < n; i++) {
            stack[top++] = arrivals[i];
            // 앞으로 올 것 중에 맨 위보다 작은 번호가 없으면 지금 내보내는 것이 최선
            while (top > 0 && stack[top - 1] < suffixMin[i + 1]) answer[out++] = stack[--top];
        }
        return answer;
    }
}
