class Solution {
    public long[] solution(long[] lengths, long[] limits) {
        long[] answer = new long[lengths.length];
        for (int i = 0; i < lengths.length; i++) {
            answer[i] = pieces(lengths[i], limits[i]);
        }
        return answer;
    }

    /** 길이 length 를 limit 기준으로 끝까지 쪼갰을 때의 조각 수. */
    private long pieces(long length, long limit) {
        // 같은 깊이의 조각은 길이가 lo 또는 lo+1 뿐이다: lo 가 cLo 개, lo+1 이 cHi 개
        long lo = length, cLo = 1, cHi = 0;
        long done = 0;
        while (true) {
            if (lo + 1 <= limit) return done + cLo + cHi; // 모두 limit 이하
            if (lo <= limit) {                             // 길이 lo 조각만 끝, lo+1 은 계속
                done += cLo;
                if (cHi == 0) return done;                 // lo+1 조각이 없으면 끝 (없으면 무한 루프)
                lo = lo + 1;
                cLo = cHi;
                cHi = 0;
            }
            // 길이 lo 와 lo+1 을 각각 반으로 쪼갠다
            if (lo % 2 == 0) {        // lo → (lo/2, lo/2), lo+1 → (lo/2, lo/2 + 1)
                cLo = 2 * cLo + cHi;
                lo = lo / 2;
            } else {                  // lo → ((lo-1)/2, (lo+1)/2), lo+1 → ((lo+1)/2, (lo+1)/2)
                cHi = cLo + 2 * cHi;
                lo = (lo - 1) / 2;
            }
        }
    }
}
