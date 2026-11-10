package ham.citybuildersim;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * What the player did in a sitting, as a file (TR1, 2026-10-31): the mouse, the keys and the markers beside them - the window's
 * size, the screen open, the expanded map's view, the clock, and the moment each month turned - each with its time in
 * milliseconds of real play, so the replay's ghost can show where the player went and re-enact what he pressed. This is the
 * format, its writer and its reader; the interface logs through it and the model never reads it. No JavaFX: a key is a code,
 * a screen a name.
 *
 * <p>File: {@code input-<epochMs>.bin.gz} in the recording's folder (Recording), one per sitting. A gzip stream of a header
 * (the magic, the format, the sitting's start in epoch milliseconds, the recording's id) and then one record per event: a
 * byte for its {@link Kind}, its {@code ms} as the step from the record before (zig-zag, as a varint), then that kind's fields
 * in a fixed layout (see {@link #write}). Screen points are floats: the factories round them to one, so what an Event holds is
 * what a file gives back. Mods: shift 1, ctrl 2, alt 4, meta 8. A key's {@code code} is {@code name.hashCode()} of the
 * toolkit's key name ({@link #keyCode}): unlike an ordinal it does not move between toolkit versions.
 *
 * <p>WHY A THREAD OF ITS OWN AND NOT SaveWriter'S (TR1): SaveWriter writes whole files, and a job for a file replaces one still
 * waiting - an appended chunk of a log would be lost. The log is one stream appended for hours, and only this thread ever
 * touches it. {@link Writer#add} never blocks the caller; the thread drains the queue every 250 ms and syncs the gzip at
 * least every two seconds, so a crash loses at most that and the file reads up to it ({@link #read} tolerates a cut tail).
 */
public final class InputLog {

    private InputLog() { }

    /** The first four bytes of a log: "CINP". */
    static final int MAGIC = 0x43494E50;

    /** The log's layout; 1 is the one below. */
    static final int FORMAT = 1;

    /** The closest two mouse moves (or drags) are kept, in milliseconds: at most 60 a second. */
    public static final long MOVE_EVERY_MS = 16;

    /** How often the writer's thread drains the queue, in milliseconds. */
    static final long DRAIN_MS = 250;

    /** The longest a written event waits before it is flushed through the gzip to the disk, in milliseconds: what a crash can lose. */
    static final long FLUSH_MS = 2_000;

    /** The longest {@link Writer#close} waits for the thread to finish the file, in milliseconds. */
    static final long CLOSE_WAIT_MS = 2_000;

    /** A modifier mask's bits. */
    public static final int SHIFT = 1, CTRL = 2, ALT = 4, META = 8;

    /** What an event is. The order is the file's: a new kind goes at the end. */
    public enum Kind { MOVE, DRAG, PRESS, RELEASE, WHEEL, KEY_DOWN, KEY_UP, WINDOW, SCREEN, MAP_VIEW, CLOCK, MONTH }

    /**
     * One thing the player did or the game marked. Which fields a kind uses: MOVE x y; DRAG x y, code the button; PRESS x y, code
     * the button, a the click count, mods; RELEASE x y, code, mods; WHEEL x y, a dx, b dy, mods; KEY_DOWN and KEY_UP code
     * ({@link #keyCode}), mods, words the typed text or null; WINDOW a width, b height; SCREEN words the screen's name;
     * MAP_VIEW code 1 for the globe and 0 for the plane, a b the centre (latitude and longitude on the globe, plot x and y on
     * the plane), c the scale, d the roll; CLOCK code the speed's index, a 1 while running; MONTH code the month. Unused fields are 0.
     */
    public record Event(long ms, Kind kind, double x, double y, int code, int mods, double a, double b, double c, double d, String words) {

        public static Event move(long ms, double x, double y) {
            return new Event(ms, Kind.MOVE, f(x), f(y), 0, 0, 0, 0, 0, 0, null);
        }

        public static Event drag(long ms, double x, double y, int button) {
            return new Event(ms, Kind.DRAG, f(x), f(y), button, 0, 0, 0, 0, 0, null);
        }

        public static Event press(long ms, double x, double y, int button, int clicks, int mods) {
            return new Event(ms, Kind.PRESS, f(x), f(y), button, mods, clicks, 0, 0, 0, null);
        }

        public static Event release(long ms, double x, double y, int button, int mods) {
            return new Event(ms, Kind.RELEASE, f(x), f(y), button, mods, 0, 0, 0, 0, null);
        }

        public static Event wheel(long ms, double x, double y, double dx, double dy, int mods) {
            return new Event(ms, Kind.WHEEL, f(x), f(y), 0, mods, f(dx), f(dy), 0, 0, null);
        }

        /** {@code code} from {@link InputLog#keyCode}; {@code text} may be null. */
        public static Event key(long ms, boolean down, int code, int mods, String text) {
            return new Event(ms, down ? Kind.KEY_DOWN : Kind.KEY_UP, 0, 0, code, mods, 0, 0, 0, 0, text);
        }

        public static Event window(long ms, double w, double h) {
            return new Event(ms, Kind.WINDOW, 0, 0, 0, 0, f(w), f(h), 0, 0, null);
        }

        public static Event screen(long ms, String name) {
            return new Event(ms, Kind.SCREEN, 0, 0, 0, 0, 0, 0, 0, 0, name);
        }

        /** The expanded map's view: {@code a} and {@code b} are the centre (latitude, longitude on the globe; plot x, y on the plane). */
        public static Event mapView(long ms, boolean globe, double a, double b, double scale, double roll) {
            return new Event(ms, Kind.MAP_VIEW, 0, 0, globe ? 1 : 0, 0, a, b, scale, roll, null);
        }

        public static Event clock(long ms, int speedIndex, boolean running) {
            return new Event(ms, Kind.CLOCK, 0, 0, speedIndex, 0, running ? 1 : 0, 0, 0, 0, null);
        }

        public static Event month(long ms, int month) {
            return new Event(ms, Kind.MONTH, 0, 0, month, 0, 0, 0, 0, 0, null);
        }

        /** A value as the file keeps it: a float, widened. */
        private static double f(double v) { return (double) (float) v; }
    }

    /** The code a key is logged under: its toolkit name's hash. */
    public static int keyCode(String keyName) {
        return keyName == null ? 0 : keyName.hashCode();
    }

    /* --------------------------------- the file --------------------------------- */

    /** One event's bytes, after its kind and its step in time. */
    private static void write(DataOutputStream out, Event e) throws IOException {
        switch (e.kind()) {
            case MOVE -> { out.writeFloat((float) e.x()); out.writeFloat((float) e.y()); }
            case DRAG -> { out.writeFloat((float) e.x()); out.writeFloat((float) e.y()); out.writeByte(e.code()); }
            case PRESS -> {
                out.writeFloat((float) e.x()); out.writeFloat((float) e.y());
                out.writeByte(e.code()); out.writeByte(e.mods()); out.writeShort((int) e.a());
            }
            case RELEASE -> {
                out.writeFloat((float) e.x()); out.writeFloat((float) e.y());
                out.writeByte(e.code()); out.writeByte(e.mods());
            }
            case WHEEL -> {
                out.writeFloat((float) e.x()); out.writeFloat((float) e.y());
                out.writeFloat((float) e.a()); out.writeFloat((float) e.b()); out.writeByte(e.mods());
            }
            case KEY_DOWN, KEY_UP -> {
                out.writeInt(e.code()); out.writeByte(e.mods());
                out.writeBoolean(e.words() != null);
                if (e.words() != null) writeText(out, e.words());
            }
            case WINDOW -> { out.writeFloat((float) e.a()); out.writeFloat((float) e.b()); }
            case SCREEN -> writeText(out, e.words() == null ? "" : e.words());
            case MAP_VIEW -> {
                out.writeByte(e.code());
                out.writeDouble(e.a()); out.writeDouble(e.b()); out.writeDouble(e.c()); out.writeDouble(e.d());
            }
            case CLOCK -> { out.writeByte(e.code()); out.writeByte((int) e.a()); }
            case MONTH -> out.writeInt(e.code());
        }
    }

    /** ...and read back, the step in time already added to {@code ms}. */
    private static Event read(DataInputStream in, Kind kind, long ms) throws IOException {
        switch (kind) {
            case MOVE: return new Event(ms, kind, in.readFloat(), in.readFloat(), 0, 0, 0, 0, 0, 0, null);
            case DRAG: return new Event(ms, kind, in.readFloat(), in.readFloat(), in.readUnsignedByte(), 0, 0, 0, 0, 0, null);
            case PRESS: {
                double x = in.readFloat(), y = in.readFloat();
                int button = in.readUnsignedByte(), mods = in.readUnsignedByte(), clicks = in.readUnsignedShort();
                return new Event(ms, kind, x, y, button, mods, clicks, 0, 0, 0, null);
            }
            case RELEASE: {
                double x = in.readFloat(), y = in.readFloat();
                int button = in.readUnsignedByte(), mods = in.readUnsignedByte();
                return new Event(ms, kind, x, y, button, mods, 0, 0, 0, 0, null);
            }
            case WHEEL: {
                double x = in.readFloat(), y = in.readFloat(), dx = in.readFloat(), dy = in.readFloat();
                return new Event(ms, kind, x, y, 0, in.readUnsignedByte(), dx, dy, 0, 0, null);
            }
            case KEY_DOWN: case KEY_UP: {
                int code = in.readInt(), mods = in.readUnsignedByte();
                String text = in.readBoolean() ? readText(in) : null;
                return new Event(ms, kind, 0, 0, code, mods, 0, 0, 0, 0, text);
            }
            case WINDOW: return new Event(ms, kind, 0, 0, 0, 0, in.readFloat(), in.readFloat(), 0, 0, null);
            case SCREEN: return new Event(ms, kind, 0, 0, 0, 0, 0, 0, 0, 0, readText(in));
            case MAP_VIEW: {
                int globe = in.readUnsignedByte();
                double a = in.readDouble(), b = in.readDouble(), c = in.readDouble(), d = in.readDouble();
                return new Event(ms, kind, 0, 0, globe, 0, a, b, c, d, null);
            }
            case CLOCK: {
                int speed = in.readUnsignedByte(), running = in.readUnsignedByte();
                return new Event(ms, kind, 0, 0, speed, 0, running, 0, 0, 0, null);
            }
            case MONTH: return new Event(ms, kind, 0, 0, in.readInt(), 0, 0, 0, 0, 0, null);
            default: throw new IOException("a kind this reader does not know: " + kind);
        }
    }

    /** A string as its UTF-8 bytes with the length as a varint: DataOutputStream.writeUTF stops at 64 KB, and a typed text can be anything. */
    private static void writeText(DataOutputStream out, String s) throws IOException {
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        writeVarLong(out, bytes.length);
        out.write(bytes);
    }

    private static String readText(DataInputStream in) throws IOException {
        long n = readVarLong(in);
        if (n < 0 || n > (1 << 24)) throw new IOException("a text of " + n + " bytes is not one this log wrote");
        byte[] bytes = new byte[(int) n];
        in.readFully(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private static void writeVarLong(DataOutputStream out, long v) throws IOException {
        while ((v & ~0x7FL) != 0) {
            out.writeByte((int) ((v & 0x7F) | 0x80));
            v >>>= 7;
        }
        out.writeByte((int) v);
    }

    private static long readVarLong(DataInputStream in) throws IOException {
        long v = 0;
        for (int shift = 0; shift < 64; shift += 7) {
            int b = in.readUnsignedByte();
            v |= (long) (b & 0x7F) << shift;
            if ((b & 0x80) == 0) return v;
        }
        throw new IOException("a number longer than a long");
    }

    private static long zigzag(long v) { return (v << 1) ^ (v >> 63); }

    private static long unzigzag(long v) { return (v >>> 1) ^ -(v & 1); }

    /* --------------------------------- reading ---------------------------------- */

    /**
     * Everything readable in a log: a file cut off anywhere - a crash, a copy that stopped - gives back the events before the
     * cut, and a record cut through is left out. A file that is not a log, or does not exist, gives none.
     */
    public static List<Event> read(Path file) {
        List<Event> events = new ArrayList<>();
        try (InputStream raw = new BufferedInputStream(Files.newInputStream(file));
             DataInputStream in = new DataInputStream(new BufferedInputStream(new GZIPInputStream(raw)))) {
            if (in.readInt() != MAGIC || in.readInt() != FORMAT) return events;
            in.readLong();
            in.readUTF();
            Kind[] kinds = Kind.values();
            long ms = 0;
            while (true) {
                int k = in.read();
                if (k < 0) break;
                if (k >= kinds.length) break;
                ms += unzigzag(readVarLong(in));
                events.add(read(in, kinds[k], ms));
            }
        } catch (IOException | RuntimeException e) {
            // A cut tail (EOF in the gzip or in a record) is the expected end of a crashed sitting: what came before it stands.
        }
        return events;
    }

    /** The start of a sitting as the file records it, in epoch milliseconds, or -1 when the file has no readable header. */
    public static long startOf(Path file) {
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(new GZIPInputStream(Files.newInputStream(file))))) {
            if (in.readInt() != MAGIC || in.readInt() != FORMAT) return -1;
            return in.readLong();
        } catch (IOException | RuntimeException e) {
            return -1;
        }
    }

    /** The sittings' logs in a recording's folder, oldest first (by the moment in the name). */
    public static List<Path> sittings(Path folder) {
        List<Path> found = new ArrayList<>();
        if (!Files.isDirectory(folder)) return found;
        try (DirectoryStream<Path> entries = Files.newDirectoryStream(folder, Recording.INPUT_PREFIX + "*" + Recording.INPUT_SUFFIX)) {
            for (Path entry : entries) if (startedAt(entry) >= 0) found.add(entry);
        } catch (IOException | RuntimeException e) {
            System.out.println("The input logs could not be listed: " + e);
        }
        found.sort(java.util.Comparator.comparingLong(InputLog::startedAt));
        return found;
    }

    /** The moment in a log's file name, or -1 when the name is not one. */
    private static long startedAt(Path file) {
        String name = file.getFileName().toString();
        if (!name.startsWith(Recording.INPUT_PREFIX) || !name.endsWith(Recording.INPUT_SUFFIX)) return -1;
        String digits = name.substring(Recording.INPUT_PREFIX.length(), name.length() - Recording.INPUT_SUFFIX.length());
        if (digits.isEmpty() || digits.length() > 18) return -1;
        for (int i = 0; i < digits.length(); i++) if (digits.charAt(i) < '0' || digits.charAt(i) > '9') return -1;
        return Long.parseLong(digits);
    }

    /* --------------------------------- writing ---------------------------------- */

    /** Opens a sitting's log in {@code folder} (made if it is missing); a log that cannot be opened is said once and returns a writer that keeps nothing, because the game never waits on it. */
    public static Writer open(Path folder, String recordingId, long epochMs) {
        Path file = folder.resolve(Recording.INPUT_PREFIX + epochMs + Recording.INPUT_SUFFIX);
        try {
            Files.createDirectories(folder);
            return new Writer(file, recordingId, epochMs);
        } catch (IOException | RuntimeException e) {
            System.out.println("The input log could not be opened: " + file + " - " + e);
            return new Writer(file);
        }
    }

    /**
     * One sitting's log being written. {@link #add} may be called from any thread and never blocks; one daemon thread, named
     * {@code input-log}, owns the file.
     */
    public static final class Writer implements AutoCloseable {

        private final Path file;
        private final ConcurrentLinkedQueue<Event> queue = new ConcurrentLinkedQueue<>();
        private final Object moveLock = new Object();
        private final Object wake = new Object();
        private final OutputStream raw;
        private final DataOutputStream out;
        private final GZIPOutputStream gzip;
        private final Thread thread;
        private long lastMoveMs = Long.MIN_VALUE;
        private long lastWrittenMs;
        private volatile boolean closing;
        private volatile boolean finished;
        private volatile long written;
        /** For ReplayCheck (TR2): the next drain throws as a failed write does. */
        private volatile boolean failNextDrain;

        /** A writer that keeps nothing: the log would not open. */
        private Writer(Path file) {
            this.file = file;
            this.raw = null;
            this.out = null;
            this.gzip = null;
            this.thread = null;
            this.finished = true;
        }

        private Writer(Path file, String recordingId, long epochMs) throws IOException {
            this.file = file;
            this.raw = new BufferedOutputStream(Files.newOutputStream(file), 1 << 16);
            this.gzip = new GZIPOutputStream(raw, 1 << 14, true);
            this.out = new DataOutputStream(gzip);
            out.writeInt(MAGIC);
            out.writeInt(FORMAT);
            out.writeLong(epochMs);
            out.writeUTF(recordingId == null ? "" : recordingId);
            out.flush();
            this.thread = new Thread(this::run, "input-log");
            thread.setDaemon(true);
            thread.start();
        }

        /** The file this sitting writes. */
        public Path path() { return file; }

        /** How many events the thread has written so far (a harness waits on it). */
        public long written() { return written; }

        /**
         * Queues an event and returns at once. A MOVE or DRAG within {@link #MOVE_EVERY_MS} of the last one kept is dropped;
         * anything added after close(), or after a write has failed (the thread has stopped and nothing would drain it), is ignored.
         */
        public void add(Event e) {
            if (e == null || closing || finished || thread == null) return;
            if (e.kind() == Kind.MOVE || e.kind() == Kind.DRAG) {
                synchronized (moveLock) {
                    if (lastMoveMs != Long.MIN_VALUE && e.ms() - lastMoveMs < MOVE_EVERY_MS) return;
                    lastMoveMs = e.ms();
                }
            }
            queue.add(e);
        }

        private void run() {
            long flushed = System.nanoTime();
            try {
                while (true) {
                    boolean last = closing;
                    drain();
                    long now = System.nanoTime();
                    if (last || now - flushed >= FLUSH_MS * 1_000_000L) {
                        out.flush();
                        flushed = now;
                    }
                    if (last) break;
                    synchronized (wake) {
                        if (!closing) wake.wait(DRAIN_MS);
                    }
                }
                gzip.finish();
                out.close();
            } catch (IOException e) {
                System.out.println("The input log stopped: " + file + " - " + e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                // A failed write leaves the file open (a gzip that cannot finish does not close what it wraps): shut every layer before
                // saying so, so the handle is released - on Windows an open file cannot be deleted - and drop what no thread will now write (TR2).
                closeQuietly(out);
                closeQuietly(raw);
                finished = true;
                queue.clear();
                synchronized (wake) { wake.notifyAll(); }
            }
        }

        private static void closeQuietly(OutputStream stream) {
            try {
                if (stream != null) stream.close();
            } catch (IOException e) {
                // the write that failed has said so already
            }
        }

        /** For ReplayCheck (TR2): makes the next drain fail as a full disk would, so the failing path can be walked. */
        void failNextDrainForChecks() { failNextDrain = true; }

        /** For ReplayCheck (TR2): whether the thread has ended, by close() or by a failure. */
        boolean stopped() { return finished; }

        /** For ReplayCheck (TR2): how many events are queued and not yet written. */
        int queued() { return queue.size(); }

        private void drain() throws IOException {
            if (failNextDrain) {
                failNextDrain = false;
                throw new IOException("a write the check made fail");
            }
            Event e;
            while ((e = queue.poll()) != null) {
                out.writeByte(e.kind().ordinal());
                writeVarLong(out, zigzag(e.ms() - lastWrittenMs));
                lastWrittenMs = e.ms();
                InputLog.write(out, e);
                written++;
            }
        }

        /** Drains what is queued, finishes the gzip and closes the file; waits for the thread at most {@link #CLOSE_WAIT_MS}. */
        @Override
        public void close() {
            if (thread == null) return;
            closing = true;
            long until = System.nanoTime() + CLOSE_WAIT_MS * 1_000_000L;
            synchronized (wake) {
                wake.notifyAll();
                try {
                    while (!finished) {
                        long left = (until - System.nanoTime()) / 1_000_000L;
                        if (left <= 0) break;
                        wake.wait(left);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
