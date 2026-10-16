class Solution {
    private int[] power; // 현재 힘 (승자의 힘을 제자리에서 갱신한다)

    public int[] solution(int[] power) {
        this.power = power.clone();
        int champion = winner(0, power.length - 1);
        return new int[]{champion, this.power[champion]};
    }

    /** [l, r] 구간 토너먼트의 우승자 번호. 우승자의 남은 힘은 power[번호]에 들어 있다. */
    private int winner(int l, int r) {
        if (l == r) return l;                 // 혼자면 대결 없이 올라간다
        int m = (l + r) / 2;
        int a = winner(l, m);                 // 왼쪽 절반 우승자 (번호가 더 작다)
        int b = winner(m + 1, r);             // 오른쪽 절반 우승자
        if (power[a] >= power[b]) {           // 같으면 번호가 작은 a 가 이긴다
            power[a] -= power[b] / 2;
            return a;
        }
        power[b] -= power[a] / 2;
        return b;
    }
}
