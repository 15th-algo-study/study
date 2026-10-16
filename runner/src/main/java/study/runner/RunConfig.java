package study.runner;

import java.nio.file.Path;

/**
 * 채점 설정. Gradle -P 값이 runner.* 시스템 프로퍼티로 넘어온다.
 *
 * @param user      GitHub login 또는 "editorial"
 * @param daySpec   "001", "1", "1-5", "1,3", "all"(기본)
 * @param levelSpec "2", "0-3", "all"(기본)
 * @param basicOnly -Pcases=basic 이면 true ("실행" 버튼)
 * @param source    지정 시 이 파일을 풀이로 사용 (앱의 작성 중 코드)
 * @param jsonOut   지정 시 결과 JSON 을 이 경로에도 저장
 * @param timeScale large 시간 제한 배수 (CI 1.0, 앱의 로컬 제출은 config.json 의 local_time_scale)
 */
public record RunConfig(Path root, String user, String daySpec, String levelSpec,
                        boolean basicOnly, Path source, Path jsonOut, Path outDir, double timeScale) {

    public static final String EDITORIAL = "editorial";

    public static RunConfig fromSystemProperties() {
        Path root = Path.of(prop("runner.root", "..")).toAbsolutePath().normalize();
        String user = prop("runner.user", EDITORIAL);
        String cases = prop("runner.cases", "all");
        if (!cases.equals("all") && !cases.equals("basic")) {
            throw new IllegalArgumentException("-Pcases 는 basic 또는 all 이어야 합니다: " + cases);
        }
        String source = prop("runner.source", null);
        String json = prop("runner.json", null);
        double scale;
        try {
            scale = Double.parseDouble(prop("runner.timeScale", "1"));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("-PtimeScale 은 숫자여야 합니다: " + prop("runner.timeScale", ""));
        }
        if (scale < 1) throw new IllegalArgumentException("-PtimeScale 은 1 이상이어야 합니다: " + scale);
        return new RunConfig(root, user,
                prop("runner.day", "all"), prop("runner.level", "all"),
                cases.equals("basic"),
                source == null ? null : Path.of(source).toAbsolutePath(),
                json == null ? null : Path.of(json).toAbsolutePath(),
                Path.of(prop("runner.outDir", "build/runner")), scale);
    }

    public boolean isEditorial() {
        return EDITORIAL.equals(user);
    }

    private static String prop(String key, String def) {
        String v = System.getProperty(key);
        return v == null || v.isBlank() ? def : v.trim();
    }
}
