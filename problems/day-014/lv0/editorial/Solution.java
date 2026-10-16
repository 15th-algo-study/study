class Solution {
    public int solution(int[] records) {
        int first = records[0];
        int second = -1; // 아직 없음
        for (int i = 1; i < records.length; i++) {
            int v = records[i];
            if (v > first) {          // 새 1등: 이전 1등이 2등으로 내려간다
                second = first;
                first = v;
            } else if (v < first && v > second) { // 같은 기록은 건너뛴다
                second = v;
            }
        }
        return second;
    }
}
