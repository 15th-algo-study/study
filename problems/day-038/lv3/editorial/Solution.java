class Solution {
    private static final int MAX_GENRE = 1_000_000;

    public long solution(int[] genres, int k) {
        return atMost(genres, k) - atMost(genres, k - 1);
    }

    /** 서로 다른 장르가 limit 가지 이하인 구간 수 */
    private long atMost(int[] genres, int limit) {
        if (limit <= 0) return 0;
        int[] count = new int[MAX_GENRE + 1];
        int distinct = 0;
        long total = 0;
        int left = 0;
        for (int right = 0; right < genres.length; right++) {
            if (count[genres[right]]++ == 0) distinct++;
            while (distinct > limit) {                 // 넘치면 왼쪽을 버린다
                if (--count[genres[left++]] == 0) distinct--;
            }
            total += right - left + 1;                 // 끝이 right 인 유효 구간 수
        }
        return total;
    }
}
