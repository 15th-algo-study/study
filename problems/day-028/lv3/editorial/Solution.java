class Solution {
    public long[] solution(String[] needs, int[] scores) {
        int k = needs[0].length();
        int full = 1 << k;

        // 1) 같은 필요 집합의 레시피는 점수를 합쳐 하나로 압축
        long[] exact = new long[full];
        for (int i = 0; i < needs.length; i++) {
            int mask = 0;
            for (int j = 0; j < k; j++) {
                if (needs[i].charAt(j) == '1') mask |= 1 << j;
            }
            exact[mask] += scores[i];
        }

        // 2) 챙길 집합 S 마다 부분집합 점수 합 → 집합 크기별 최댓값
        long[] best = new long[k + 1];
        for (int s = 0; s < full; s++) {
            long total = 0;
            for (int sub = s; ; sub = (sub - 1) & s) { // S 의 모든 부분집합 (0 포함)
                total += exact[sub];
                if (sub == 0) break;
            }
            int size = Integer.bitCount(s);
            if (total > best[size]) best[size] = total;
        }

        // 3) "정확히 c종류" → "최대 c종류"
        for (int c = 1; c <= k; c++) best[c] = Math.max(best[c], best[c - 1]);
        return best;
    }
}
