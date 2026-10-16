class Solution {
    public long[] solution(long[] numbers) {
        long[] answer = new long[numbers.length];
        for (int i = 0; i < numbers.length; i++) {
            answer[i] = reverse(numbers[i], 0);
        }
        return answer;
    }

    /** n 의 마지막 자리를 떼어 acc 뒤에 붙이는 일을 n 이 0이 될 때까지 반복한다. */
    private long reverse(long n, long acc) {
        if (n == 0) return acc;                      // 더 뗄 자리가 없다
        return reverse(n / 10, acc * 10 + n % 10);   // 마지막 자리를 acc 뒤에 붙인다
    }
}
