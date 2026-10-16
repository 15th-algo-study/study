import java.util.Arrays;

class Solution {
    public long solution(int[] tops, int[] legs, int[] bases, int[] wheels, long target) {
        long[] ab = pairSums(tops, legs);     // 상판 + 다리
        long[] cd = pairSums(bases, wheels);  // 받침 + 바퀴
        Arrays.sort(ab);
        Arrays.sort(cd);

        long count = 0;
        int i = 0, j = cd.length - 1;
        while (i < ab.length && j >= 0) {
            long sum = ab[i] + cd[j];
            if (sum < target) {
                i++;
            } else if (sum > target) {
                j--;
            } else {
                // 같은 값의 묶음 크기를 각각 세어 곱한다
                long a = ab[i], c = cd[j];
                long x = 0, y = 0;
                while (i < ab.length && ab[i] == a) { i++; x++; }
                while (j >= 0 && cd[j] == c) { j--; y++; }
                count += x * y;
            }
        }
        return count;
    }

    private long[] pairSums(int[] p, int[] q) {
        long[] sums = new long[p.length * q.length];
        int idx = 0;
        for (int x : p) {
            for (int y : q) sums[idx++] = (long) x + y; // int 합이 넘치지 않도록 long 으로
        }
        return sums;
    }
}
