package study.runner;

/** 케이스 하나의 채점 결과. 문자열 필드는 사람이 읽는 표시용(앞부분만). */
public record CaseResult(String name, String kind, Status status, double timeMs, Long limitMs,
                         String note, String input, String expected, String actual, String message) {

    public enum Status {
        PASS("통과", "✔"), WRONG("오답", "✘"), TLE("시간 초과", "⏱"), RUNTIME_ERROR("런타임 오류", "✘"),
        COMPILE_ERROR("컴파일 오류", "✘"), ERROR("오류", "!");

        public final String label;
        public final String mark;

        Status(String label, String mark) {
            this.label = label;
            this.mark = mark;
        }
    }

    public boolean passed() {
        return status == Status.PASS;
    }
}
