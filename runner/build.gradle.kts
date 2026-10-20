plugins {
    java
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.code.gson:gson:2.13.1")
    testImplementation(platform("org.junit:junit-bom:5.13.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<JavaCompile>().configureEach {
    options.release = 21
    options.encoding = "UTF-8"
}

// 레포 루트 = runner/ 의 상위 폴더
val repoRoot: File = projectDir.parentFile

// 앱이 채점을 동시에 여러 개 돌릴 때 슬롯마다 build 폴더를 따로 쓰도록 바꿀 수 있다 (-PrunnerBuildDir=...)
findProperty("runnerBuildDir")?.let { layout.buildDirectory.set(file(it.toString())) }
val runnerOut: File = layout.buildDirectory.dir("runner").get().asFile

// -Puser -Pday -Plevel -Pcases(basic|all) -Psource -Pjson -PtimeScale 을 테스트 JVM 시스템 프로퍼티로 전달
val runnerProps: Map<String, String> = listOf("user", "day", "level", "cases", "source", "json", "timeScale")
    .mapNotNull { key -> findProperty(key)?.toString()?.let { "runner.$key" to it } }
    .toMap()

tasks.test {
    useJUnitPlatform()
    outputs.upToDateWhen { false }
    // 채점 결과는 아래 doLast 에서 직접 출력하고 성공/실패를 판정한다
    ignoreFailures = true
    maxHeapSize = "1g"
    jvmArgs("-Xss64m", "-XX:+UseParallelGC", "-Dfile.encoding=UTF-8", "-Dstdout.encoding=UTF-8")
    systemProperty("runner.root", repoRoot.absolutePath)
    systemProperty("runner.outDir", runnerOut.absolutePath)
    systemProperties(runnerProps)
    testLogging {
        events()
        showExceptions = false
        showStackTraces = false
    }
    doFirst {
        runnerOut.mkdirs()
        File(runnerOut, "status").delete()
        File(runnerOut, "report.txt").delete()
    }
    doLast {
        val report = File(runnerOut, "report.txt")
        val status = File(runnerOut, "status")
        if (report.exists()) println(report.readText()) else println("채점 리포트가 생성되지 않았습니다.")
        if (!status.exists() || status.readText().trim() != "PASS") {
            throw GradleException("채점 실패 — 위 리포트를 확인하세요.")
        }
    }
}

// 문제 출제용: editorial 풀이를 실행해 expected 가 비어 있는 케이스를 채운다
// ./gradlew fillExpected -Pday=001 [-Plevel=2] [-Pforce=true]
tasks.register<JavaExec>("fillExpected") {
    group = "study"
    description = "editorial 풀이로 cases/*.json 의 expected 를 채운다"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass = "study.runner.FillExpected"
    maxHeapSize = "2g"
    jvmArgs("-Xss64m")
    args(
        repoRoot.absolutePath,
        findProperty("day")?.toString() ?: "all",
        findProperty("level")?.toString() ?: "all",
        findProperty("force")?.toString() ?: "false",
    )
}
