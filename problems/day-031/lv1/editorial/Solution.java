class Solution {
    private int[] salary;
    private long[] total;

    public long[] solution(int[] salary) {
        this.salary = salary;
        this.total = new long[salary.length];
        team(0); // 대표의 팀을 계산하면 모든 사원의 팀이 함께 계산된다
        return total;
    }

    /** i번 사원의 팀 급여 총액을 계산해 저장하고 돌려준다. */
    private long team(int i) {
        if (i >= salary.length) return 0; // 없는 사원
        long sum = salary[i];
        sum += team(2 * i + 1);          // 왼쪽 부하의 팀
        sum += team(2 * i + 2);          // 오른쪽 부하의 팀
        total[i] = sum;
        return sum;
    }
}
