package ham.citybuildersim;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

/**
 * Everything the game prints, written somewhere a player can find it.
 *
 * WHY
 *
 * This codebase talks. It prints monthly reports, "Buildings loaded from...",
 * "Autosave failed", the construction-format warning, the reason a load was
 * refused. All of it goes to System.out - and in a packaged build there is no
 * console attached, so every one of those lines is written to nowhere.
 *
 * That is the difference between a bug report and a shrug. A player says "it
 * broke"; without this there is nothing to read, and the most useful thing the
 * game already knew about its own failure was thrown away at the moment it
 * happened.
 *
 * HOW
 *
 * System.out and System.err are replaced with streams that write to BOTH the
 * original destination and a file. Nothing else in the game changes - every
 * existing println is now a log line, which is the point: a logging system that
 * needs a thousand call sites edited does not get adopted.
 *
 * TWO FILES
 *
 * On start, the previous log is moved aside rather than appended to. So there
 * are always exactly two: the run happening now, and the one before it. That
 * second file is the important one - the usual sequence is that something goes
 * wrong, the player restarts, and only then thinks to ask for the log.
 *
 * PAST THE CAP, FAILURES STILL GET THROUGH (0.7.50)
 *
 * Jerus's 0.7.49 game froze after its log had reached the cap, throwing on
 * every frame, and the file showed none of it: the trigger and the freeze
 * were both past the end. Ordinary output still stops at the cap; a failure
 * - the crash handler's, or any other failure() - is still written, each one
 * once by its first lines, until FAILURE_BYTES more have gone in.
 *
 * NEVER THROWS
 *
 * If the log cannot be opened - read-only folder, disk full, no permission -
 * the game keeps its ordinary streams and carries on. A game that refuses to
 * start because it could not open its own log file would be a worse bug than
 * any it was meant to help diagnose.
 */
public final class GameLog {

    public static final String LOG_FILE = "log.txt";
    public static final String PREVIOUS_LOG_FILE = "log-previous.txt";

    /**
     * The cap on ordinary output: once either stream has written more than
     * this to the file, the file says so and from then takes failures alone,
     * FAILURE_BYTES more of them (0.7.50; until then, nothing more). It is
     * not rotated mid-run: the previous run's log is moved aside at start
     * (TWO FILES).
     *
     * A hundred-year fast-forward prints a report per month; without a cap the
     * log would be the largest thing the game ever wrote. Two megabytes is
     * thousands of months and still opens instantly in Notepad.
     */
    private static final long MAX_BYTES = 2_000_000;

    /**
     * How much more the file takes past the cap, in failures alone (0.7.50):
     * a tenth of the cap.
     */
    static final long FAILURE_BYTES = 200_000;

    /**
     * How many of a failure's lines make it the same failure as one already
     * written past the cap: what it says (without the time), the exception
     * and its message, and the first two frames.
     */
    static final int FIRST_LINES = 4;

    private static final DateTimeFormatter STAMP =
            DateTimeFormatter.ofPattern("HH:mm:ss");

    private static PrintStream originalOut;
    private static PrintStream originalErr;
    private static Path logPath;
    private static boolean started;

    /** The file both streams write to, and what it takes past the cap; null until start(). */
    private static volatile Sink sink;

    private GameLog() { }

    /** Where the log is, or null if logging never started. */
    public static Path file() {
        return logPath;
    }

    public static boolean isRunning() {
        return started;
    }

    /**
     * Begins logging into the save folder. Safe to call more than once.
     */
    public static synchronized void start(GameFiles files) {

        if (started || files == null) return;

        try {
            Path directory = files.getDirectory();
            Files.createDirectories(directory);

            Path current = directory.resolve(LOG_FILE);
            rotate(current, directory.resolve(PREVIOUS_LOG_FILE));

            OutputStream fileStream = new FileOutputStream(current.toFile(), true);
            Sink shared = new Sink(fileStream, FAILURE_BYTES);

            originalOut = System.out;
            originalErr = System.err;

            // autoflush, because the log matters most in the run that crashed -
            // and a buffered tail is exactly the part that would be lost.
            System.setOut(new PrintStream(
                    new TeeStream(originalOut, shared, MAX_BYTES), true, StandardCharsets.UTF_8));
            System.setErr(new PrintStream(
                    new TeeStream(originalErr, shared, MAX_BYTES), true, StandardCharsets.UTF_8));
            sink = shared;

            logPath = current;
            started = true;

            System.out.println();
            System.out.println("================================================");
            System.out.println(GameVersion.title() + " started "
                    + LocalDateTime.now());
            System.out.println("Java " + System.getProperty("java.version")
                    + " on " + System.getProperty("os.name"));
            System.out.println("Saves: " + directory);
            System.out.println("================================================");

        } catch (IOException | RuntimeException e) {
            // Keep the ordinary streams and say so on them. Not fatal.
            System.out.println("Could not open the log file: " + e.getMessage());
            started = false;
        }
    }

    /** A line that is worth finding later, stamped with the time. */
    public static void note(String message) {
        System.out.println("[" + LocalDateTime.now().format(STAMP) + "] " + message);
    }

    /**
     * A failure, with its stack trace, in one place in the file - and past
     * the cap (0.7.50), still in the file, once for its first lines.
     */
    public static void failure(String context, Throwable cause) {
        String entry = entry(LocalDateTime.now().format(STAMP), context, cause);
        System.out.print(entry);
        Sink s = sink;
        if (s != null && s.capped()) s.failure(firstLines(context, cause), entry);
    }

    /** The lines failure() writes, as one string: the same lines, in the same order, it always printed one by one. */
    static String entry(String time, String context, Throwable cause) {
        String nl = System.lineSeparator();
        StringBuilder s = new StringBuilder();
        s.append(nl);
        s.append("!!! ").append(time).append("  ").append(context).append(nl);
        if (cause != null) {
            s.append("    ").append(cause.getClass().getName()).append(": ").append(cause.getMessage()).append(nl);
            for (StackTraceElement frame : cause.getStackTrace()) {
                s.append("      at ").append(frame).append(nl);
            }
            Throwable inner = cause.getCause();
            while (inner != null && inner != cause) {
                s.append("    caused by ").append(inner.getClass().getName()).append(": ")
                        .append(inner.getMessage()).append(nl);
                inner = inner.getCause();
            }
        }
        s.append(nl);
        return s.toString();
    }

    /**
     * What makes two failures the same one past the cap: FIRST_LINES of it -
     * what it says without the time, the exception and its message, and the
     * first frames. A freeze that throws on every frame is one entry.
     */
    static String firstLines(String context, Throwable cause) {
        StringBuilder s = new StringBuilder(String.valueOf(context));
        if (cause != null) {
            s.append('\n').append(cause.getClass().getName()).append(": ").append(cause.getMessage());
            StackTraceElement[] frames = cause.getStackTrace();
            for (int i = 0; i < frames.length && i < FIRST_LINES - 2; i++) s.append('\n').append(frames[i]);
        }
        return s.toString();
    }

    private static void rotate(Path current, Path previous) {
        try {
            if (Files.exists(current)) {
                Files.move(current, previous, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            // Appending to a big old log beats not logging.
        }
    }

    /**
     * The log file, shared by the two streams (0.7.50): everything until the
     * cap, then failures alone - each once by its first lines, until
     * `failureRoom` more bytes have gone in, when it says so and closes.
     */
    static final class Sink {

        private OutputStream file;
        private final long failureRoom;
        private boolean capped;
        private long failureBytes;
        private final Set<String> seen = new HashSet<>();

        Sink(OutputStream file, long failureRoom) {
            this.file = file;
            this.failureRoom = failureRoom;
        }

        /** Ordinary output: written until the cap, and false once nothing ordinary is. */
        synchronized boolean write(byte[] b, int off, int len) {
            if (file == null || capped) return false;
            try {
                file.write(b, off, len);
                return true;
            } catch (IOException e) {
                // The disk went away mid-run. Carry on with the console.
                file = null;
                return false;
            }
        }

        /** The cap reached: said once in the file, which takes failures alone from here. */
        synchronized void reachCap(long cap) {
            if (capped || file == null) return;
            capped = true;
            try {
                // Stop rather than grow without limit. The console half
                // keeps working, and a truncated log says so.
                file.write(("\n... log size limit reached (" + cap
                        + " bytes). Further output is not being written, except failures:"
                        + " each one once, up to " + failureRoom + " bytes more.\n")
                        .getBytes(StandardCharsets.UTF_8));
                file.flush();
            } catch (IOException e) {
                file = null;
            }
        }

        synchronized boolean capped() {
            return capped;
        }

        /**
         * A failure past the cap: written whole unless one with the same
         * first lines already was, while there is room. True if it went in.
         */
        synchronized boolean failure(String firstLines, String entry) {
            if (!capped || file == null || !seen.add(firstLines)) return false;
            byte[] bytes = entry.getBytes(StandardCharsets.UTF_8);
            try {
                if (failureBytes + bytes.length > failureRoom) {
                    file.write(("\n... and the " + failureRoom + " bytes for failures past the limit"
                            + " are used up. Nothing more is written.\n").getBytes(StandardCharsets.UTF_8));
                    file.flush();
                    file.close();
                    file = null;
                    return false;
                }
                file.write(bytes);
                file.flush();
                failureBytes += bytes.length;
                return true;
            } catch (IOException e) {
                file = null;
                return false;
            }
        }

        synchronized void flush() {
            if (file != null) {
                try { file.flush(); } catch (IOException ignored) { }
            }
        }
    }

    /**
     * Writes to two places at once.
     *
     * The console half is what makes this invisible when run from NetBeans -
     * the output window still works exactly as it did.
     */
    static final class TeeStream extends OutputStream {

        private final OutputStream console;
        private final Sink file;
        private final long cap;
        private long written;

        TeeStream(OutputStream console, Sink file, long cap) {
            this.console = console;
            this.file = file;
            this.cap = cap;
        }

        @Override public void write(int b) throws IOException {
            console.write(b);
            writeToFile(new byte[] { (byte) b }, 0, 1);
        }

        @Override public void write(byte[] b, int off, int len) throws IOException {
            console.write(b, off, len);
            writeToFile(b, off, len);
        }

        private void writeToFile(byte[] b, int off, int len) {
            if (!file.write(b, off, len)) return;
            written += len;
            if (written > cap) file.reachCap(cap);
        }

        @Override public void flush() throws IOException {
            console.flush();
            file.flush();
        }
    }
}
