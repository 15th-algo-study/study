class Solution {
    public int[] solution(long[] numbers) {
        int[] answer = new int[numbers.length];
        for (int i = 0; i < numbers.length; i++) {
            answer[i] = lucky(numbers[i]);
        }
        return answer;
    }

    /** 한 자리가 될 때까지 자릿수 합을 반복한다 (재귀). */
    private int lucky(long n) {
        if (n < 10) return (int) n;          // 종료 조건: 이미 한 자리
        return lucky(digitSum(n));           // 자릿수 합에 같은 일을 다시 한다
    }

    private long digitSum(long n) {
        long sum = 0;
        while (n > 0) {
            sum += n % 10;
            n /= 10;
        }
        return sum;
    }
}
