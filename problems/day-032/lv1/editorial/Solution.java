class Solution {
    private int[] numbers;
    private int[] order;
    private int count = 0; // 지금까지 호명한 학생 수 (다음에 채울 위치)

    public int[] solution(int[] numbers) {
        this.numbers = numbers;
        this.order = new int[numbers.length];
        call(0, numbers.length - 1);
        return order;
    }

    /** [l, r] 구간을 규칙대로 호명한다. */
    private void call(int l, int r) {
        if (l > r) return;               // 빈 구간
        int mid = (l + r) / 2;           // 짝수 길이면 왼쪽 가운데
        order[count++] = numbers[mid];   // 가운데 먼저
        call(l, mid - 1);                // 왼쪽 구간 전체
        call(mid + 1, r);                // 오른쪽 구간 전체
    }
}
