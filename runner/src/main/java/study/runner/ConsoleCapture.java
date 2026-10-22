package study.runner;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * 실행(basic 만)에서 케이스마다 풀이가 찍은 콘솔 출력(System.out·System.err)을 모은다.
 *
 * System.out/err 를 한 번 바꿔 두고, 쓰는 스레드가 어느 케이스 소속인지(InheritableThreadLocal)로 버퍼를 고른다.
 * 시간 제한 실행기의 스레드와 풀이가 만든 스레드는 케이스를 시작한 스레드의 버퍼를 물려받으므로,
 * 시간 초과 뒤에도 계속 도는 이전 케이스의 스레드가 다음 케이스 출력에 섞이지 않는다.
 * 어느 케이스에도 속하지 않은 출력(러너 자신의 리포트 등)은 원래 콘솔로 간다.
 */
final class ConsoleCapture {
    /** 케이스 하나에서 모으는 최대 바이트. 넘는 부분은 버리고 잘렸다고 표시한다. */
    static final int LIMIT = 32 * 1024;

    private static final InheritableThreadLocal<Buffer> CURRENT = new InheritableThreadLocal<>();
    private static boolean installed;

    private ConsoleCapture() {}

    /** 현재 스레드(와 앞으로 만들 스레드)의 출력을 새 버퍼로 모으기 시작한다. */
    static Buffer begin() {
        install();
        Buffer b = new Buffer();
        CURRENT.set(b);
        return b;
    }

    static void end() {
        CURRENT.remove();
    }

    private static synchronized void install() {
        if (installed) return;
        installed = true;
        System.setOut(new PrintStream(new Router(System.out), true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(new Router(System.err), true, StandardCharsets.UTF_8));
    }

    /** 앞 LIMIT 바이트만 남기는 버퍼. 풀이가 여러 스레드에서 찍을 수 있어 동기화한다. */
    static final class Buffer {
        private final byte[] data = new byte[LIMIT];
        private int size;
        private long total;

        synchronized void write(byte[] b, int off, int len) {
            total += len;
            int n = Math.min(len, LIMIT - size);
            if (n > 0) {
                System.arraycopy(b, off, data, size, n);
                size += n;
            }
        }

        /** 모은 출력. 아무것도 찍지 않았으면 null. */
        synchronized String text() {
            if (total == 0) return null;
            String s = new String(data, 0, size, StandardCharsets.UTF_8);
            return total > size ? s + "\n… (출력이 " + LIMIT / 1024 + "KB 를 넘어 뒷부분은 생략했습니다)" : s;
        }
    }

    /** 쓰는 스레드의 버퍼가 있으면 거기로, 없으면 원래 스트림으로 보낸다. */
    private static final class Router extends OutputStream {
        private final PrintStream original;

        Router(PrintStream original) {
            this.original = original;
        }

        @Override
        public void write(int b) {
            write(new byte[] {(byte) b}, 0, 1);
        }

        @Override
        public void write(byte[] b, int off, int len) {
            Buffer buf = CURRENT.get();
            if (buf != null) buf.write(b, off, len);
            else original.write(b, off, len);
        }

        @Override
        public void flush() throws IOException {
            if (CURRENT.get() == null) original.flush();
        }
    }
}
