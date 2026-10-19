class Solution {
    public int[] solution(int[] tiles, int t) {
        int[] cnt = new int[200_001];
        int bestLen = 0, bestStart = 0, l = 0;
        for (int r = 0; r < tiles.length; r++) {
            int v = tiles[r];
            cnt[v]++;
            while (cnt[v] > t) {            // 방금 들어온 무늬만 t 를 넘을 수 있다
                cnt[tiles[l++]]--;
            }
            if (r - l + 1 > bestLen) {      // 엄격히 길 때만 갱신 → 같은 길이면 가장 왼쪽 시작이 남는다
                bestLen = r - l + 1;
                bestStart = l;
            }
        }
        return new int[]{bestLen, bestStart};
    }
}
