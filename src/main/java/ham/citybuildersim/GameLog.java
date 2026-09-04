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
     * Above this, the file is rotated mid-run.
     *
     * A hundred-year fast-forward prints a report per month; without a cap the
     * log would be the largest thing the game ever wrote. Two megabytes is
     * thousands of months and still opens instantly in Notepad.
     */
    private static final long MAX_BYTES = 2_000_000;

    private static final DateTimeFormatter STAMP =
            DateTimeFormatter.ofPattern("HH:mm:ss");

    private static PrintStream originalOut;
    private static PrintStream originalErr;
    private static Path logPath;
    private static boolean started;

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

            originalOut = System.out;
            originalErr = System.err;

            // autoflush, because the log matters most in the run that crashed -
            // and a buffered tail is exactly the part that would be lost.
            System.setOut(new PrintStream(
                    new TeeStream(originalOut, fileStream), true, StandardCharsets.UTF_8));
            System.setErr(new PrintStream(
                    new TeeStream(originalErr, fileStream), true, StandardCharsets.UTF_8));

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

    /** A failure, with its stack trace, in one place in the file. */
    public static void failure(String context, Throwable cause) {

        System.out.println();
        System.out.println("!!! " + LocalDateTime.now().format(STAMP) + "  " + context);

        if (cause != null) {
            System.out.println("    " + cause.getClass().getName()
                    + ": " + cause.getMessage());
            for (StackTraceElement frame : cause.getStackTrace()) {
                System.out.println("      at " + frame);
            }
            Throwable inner = cause.getCause();
            while (inner != null && inner != cause) {
                System.out.println("    caused by " + inner.getClass().getName()
                        + ": " + inner.getMessage());
                inner = inner.getCause();
            }
        }
        System.out.println();
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
     * Writes to two places at once.
     *
     * The console half is what makes this invisible when run from NetBeans -
     * the output window still works exactly as it did.
     */
    private static final class TeeStream extends OutputStream {

        private final OutputStream console;
        private OutputStream file;
        private long written;

        TeeStream(OutputStream console, OutputStream file) {
            this.console = console;
            this.file = file;
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
            if (file == null) return;
            try {
                file.write(b, off, len);
                written += len;
                if (written > MAX_BYTES) {
                    // Stop rather than grow without limit. The console half
                    // keeps working, and a truncated log says so.
                    file.write(("\n... log size limit reached (" + MAX_BYTES
                            + " bytes). Further output is not being written.\n")
                            .getBytes(StandardCharsets.UTF_8));
                    file.flush();
                    file.close();
                    file = null;
                }
            } catch (IOException e) {
                // The disk went away mid-run. Carry on with the console.
                file = null;
            }
        }

        @Override public void flush() throws IOException {
            console.flush();
            if (file != null) {
                try { file.flush(); } catch (IOException ignored) { }
            }
        }
    }
}
