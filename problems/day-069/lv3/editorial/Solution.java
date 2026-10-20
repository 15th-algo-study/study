class Solution {
    private static final int MOD = 1_000_000_007;

    public int solution(String beads) {
        long[] end = new long[26]; // end[c] = 마지막 글자가 c인 서로 다른 무늬 수
        long total = 0;            // end 의 합 = 지금까지의 서로 다른 무늬 수
        for (int i = 0; i < beads.length(); i++) {
            int c = beads.charAt(i) - 'a';
            // c 한 글자 + (c로 끝나지 않는 무늬) 뒤에 c — 예전 end[c]를 모두 포함하므로 바꿔치기
            long updated = (total - end[c] + 1 + MOD) % MOD;
            total = (total - end[c] + updated + MOD) % MOD;
            end[c] = updated;
        }
        return (int) total;
    }
}
