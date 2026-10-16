package study.runner;

import java.nio.file.Path;

/** problems/day-NNN/lvN 하나. */
public record Problem(int day, int level, Path dir) {
    public String dayName() {
        return String.format("day-%03d", day);
    }

    public String id() {
        return dayName() + "/lv" + level;
    }

    public Path editorial() {
        return dir.resolve("editorial").resolve("Solution.java");
    }

    public Path template() {
        return dir.resolve("Solution.java");
    }

    /** large 케이스 시간 제한. lv0·lv1 1초, lv2 2초, lv3 3초. */
    public long timeLimitMs() {
        return switch (level) {
            case 0, 1 -> 1000;
            case 2 -> 2000;
            default -> 3000;
        };
    }
}
