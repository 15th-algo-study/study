package study.runner;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

/** Solution.java 를 런타임에 컴파일한 결과. 컴파일 실패 시 error 에 진단 메시지가 담긴다. */
public final class CompiledSolution {
    public final Path source;
    public final String error;
    private final Path classesDir;
    private final String className;

    private CompiledSolution(Path source, Path classesDir, String className, String error) {
        this.source = source;
        this.classesDir = classesDir;
        this.className = className;
        this.error = error;
    }

    public boolean ok() {
        return error == null;
    }

    public String className() {
        return className;
    }

    public static CompiledSolution compile(Path source) {
        if (!Files.isRegularFile(source)) {
            return new CompiledSolution(source, null, null, "풀이 파일이 없습니다: " + source);
        }
        try {
            Path work = Files.createTempDirectory("solution-");
            Path src = work.resolve("src");
            Path out = work.resolve("classes");
            Files.createDirectories(src);
            Files.createDirectories(out);
            // 파일명이 무엇이든 Solution.java 로 복사해서 컴파일 (public class Solution 대응)
            Path copy = src.resolve("Solution.java");
            Files.copy(source, copy);

            JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
            DiagnosticCollector<JavaFileObject> diags = new DiagnosticCollector<>();
            try (StandardJavaFileManager fm = javac.getStandardFileManager(diags, Locale.getDefault(), StandardCharsets.UTF_8)) {
                List<String> opts = List.of("-d", out.toString(), "-encoding", "UTF-8", "--release", "21",
                        "-parameters", "-proc:none", "-g", "-nowarn", "-Xlint:none");
                boolean success = javac.getTask(null, fm, diags, opts, null, fm.getJavaFileObjects(copy.toFile())).call();
                if (!success) return new CompiledSolution(source, null, null, format(diags, copy));
            }
            Optional<Path> cls;
            try (Stream<Path> s = Files.walk(out)) {
                cls = s.filter(p -> p.getFileName().toString().equals("Solution.class")).findFirst();
            }
            if (cls.isEmpty()) {
                return new CompiledSolution(source, null, null, "class Solution 을 찾을 수 없습니다. 클래스 이름이 Solution 인지 확인하세요.");
            }
            String rel = out.relativize(cls.get()).toString();
            String name = rel.substring(0, rel.length() - ".class".length()).replace(java.io.File.separatorChar, '.');
            return new CompiledSolution(source, out, name, null);
        } catch (IOException e) {
            return new CompiledSolution(source, null, null, "컴파일 준비 중 오류: " + e);
        }
    }

    /** 케이스마다 새 클래스로더로 불러와 static 상태가 케이스 사이에 새지 않게 한다. */
    public Loaded load() throws ReflectiveOperationException {
        try {
            URLClassLoader loader = new URLClassLoader(new URL[]{classesDir.toUri().toURL()}, CompiledSolution.class.getClassLoader());
            Class<?> cls = Class.forName(className, true, loader);
            Method[] methods = Arrays.stream(cls.getDeclaredMethods())
                    .filter(m -> m.getName().equals("solution") && !m.isSynthetic())
                    .toArray(Method[]::new);
            if (methods.length == 0) throw new NoSuchMethodException("solution 메서드가 없습니다.");
            if (methods.length > 1) throw new NoSuchMethodException("solution 메서드가 여러 개입니다. 하나만 남겨 주세요.");
            Method m = methods[0];
            m.setAccessible(true);
            Constructor<?> ctor = Modifier.isStatic(m.getModifiers()) ? null : cls.getDeclaredConstructor();
            if (ctor != null) ctor.setAccessible(true);
            return new Loaded(cls, m, ctor);
        } catch (java.net.MalformedURLException e) {
            throw new IllegalStateException(e);
        }
    }

    public record Loaded(Class<?> cls, Method method, Constructor<?> ctor) {
        public Object newInstance() throws ReflectiveOperationException {
            return ctor == null ? null : ctor.newInstance();
        }

        public String paramName(int i) {
            var p = method.getParameters()[i];
            return p.isNamePresent() ? p.getName() : "arg" + (i + 1);
        }
    }

    private static String format(DiagnosticCollector<JavaFileObject> diags, Path file) {
        List<String> lines;
        try {
            lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            lines = List.of();
        }
        StringBuilder sb = new StringBuilder();
        for (Diagnostic<? extends JavaFileObject> d : diags.getDiagnostics()) {
            if (d.getKind() != Diagnostic.Kind.ERROR) continue;
            long line = d.getLineNumber();
            sb.append("Solution.java:").append(line).append(": ").append(d.getMessage(Locale.getDefault())).append('\n');
            if (line >= 1 && line <= lines.size()) {
                String code = lines.get((int) line - 1).replace('\t', ' ');
                sb.append("    ").append(code).append('\n');
                long col = d.getColumnNumber();
                if (col >= 1) sb.append("    ").append(" ".repeat((int) col - 1)).append("^\n");
            }
        }
        return sb.toString().stripTrailing();
    }
}
