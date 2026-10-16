import java.util.Arrays;

class Solution {
    private int[] digits;
    private boolean[] used;
    private int[] values; // 모든 순열이 만드는 수
    private int size;

    public int[] solution(int[] digits, int[] ks) {
        this.digits = digits;
        int n = digits.length;
        used = new boolean[n];
        int total = 1;
        for (int i = 2; i <= n; i++) total *= i; // n!
        values = new int[total];
        permute(0, 0);

        // 정렬 후 중복 제거 → distinct[0..count)
        Arrays.sort(values);
        int count = 0;
        for (int i = 0; i < values.length; i++) {
            if (i == 0 || values[i] != values[i - 1]) values[count++] = values[i];
        }

        int[] answer = new int[ks.length];
        for (int i = 0; i < ks.length; i++) {
            answer[i] = ks[i] <= count ? values[ks[i] - 1] : -1;
        }
        return answer;
    }

    private void permute(int depth, int value) {
        if (depth == digits.length) {
            values[size++] = value;
            return;
        }
        for (int i = 0; i < digits.length; i++) {
            if (used[i]) continue;
            used[i] = true;
            permute(depth + 1, value * 10 + digits[i]);
            used[i] = false;
        }
    }
}
