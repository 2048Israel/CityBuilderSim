# InputLog.java - 438 lines · 35 methods · 7 constants · model

`ham/citybuildersim/InputLog.java` - generated 2026-11-01 by CodeMap; line numbers are as of that run.

> What the player did in a sitting, as a file (TR1, 2026-10-31): the mouse, the keys and the markers beside them - the window's
> size, the screen open, the expanded map's view, the clock, and the moment each month turned - each with its time in
> milliseconds of real play, so the replay's ghost can show where the player went and re-enact what he pressed. This is the
> format, its writer and its reader; the interface logs through it and the model never reads it. No JavaFX: a key is a code,
> a screen a name.
> 
> <p>File: {@code input-<epochMs>.bin.gz} in the recording's folder (Recording), one per sitting. A gzip stream of a header
> (the magic, the format, the sitting's start in epoch milliseconds, the recording's id) and then one record per event: a
> byte for its {@link Kind}, its {@code ms} as the step from the record before (zig-zag, as a varint), then that kind's fields
> in a fixed layout (see {@link #write}). Screen points are floats: the factories round them to one, so what an Event holds is
> what a file gives back. Mods: shift 1, ctrl 2, alt 4, meta 8. A key's {@code code} is {@code name.hashCode()} of the
> toolkit's key name ({@link #keyCode}): unlike an ordinal it does not move between toolkit versions.
> 
> <p>WHY A THREAD OF ITS OWN AND NOT SaveWriter'S (TR1): SaveWriter writes whole files, and a job for a file replaces one still
> waiting - an appended chunk of a log would be lost. The log is one stream appended for hours, and only this thread ever
> touches it. {@link Writer#add} never blocks the caller; the thread drains the queue every 250 ms and syncs the gzip at
> least every two seconds, so a crash loses at most that and the file reads up to it ({@link #read} tolerates a cut tail).

**Uses:** [Recording](Recording.md) (8)

**Used by (2):** [InputRecorder](InputRecorder.md), [ReplayCheck](ReplayCheck.md)

## Sections

| line | section |
|---:|---|
| 131 | · the file |
| 243 | · reading |
| 304 | · writing |

## Enum constants

| line | constant | says |
|---:|---|---|
| 65 | `InputLog.Kind.MOVE` |  |
| 65 | `InputLog.Kind.DRAG` |  |
| 65 | `InputLog.Kind.PRESS` |  |
| 65 | `InputLog.Kind.RELEASE` |  |
| 65 | `InputLog.Kind.WHEEL` |  |
| 65 | `InputLog.Kind.KEY_DOWN` |  |
| 65 | `InputLog.Kind.KEY_UP` |  |
| 65 | `InputLog.Kind.WINDOW` |  |
| 65 | `InputLog.Kind.SCREEN` |  |
| 65 | `InputLog.Kind.MAP_VIEW` |  |
| 65 | `InputLog.Kind.CLOCK` |  |
| 65 | `InputLog.Kind.MONTH` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 44 | `InputLog.MAGIC` | `0x43494E50` | The first four bytes of a log: "CINP". |
| 47 | `InputLog.FORMAT` | `1` | The log's layout; 1 is the one below. |
| 50 | `InputLog.MOVE_EVERY_MS` | `16` | The closest two mouse moves (or drags) are kept, in milliseconds: at most 60 a second. |
| 53 | `InputLog.DRAIN_MS` | `250` | How often the writer's thread drains the queue, in milliseconds. |
| 56 | `InputLog.FLUSH_MS` | `2_000` | The longest a written event waits before it is flushed through the gzip to the disk, in milliseconds: what a crash can lose. |
| 59 | `InputLog.CLOSE_WAIT_MS` | `2_000` | The longest {@link Writer#close} waits for the thread to finish the file, in milliseconds. |
| 62 | `InputLog.SHIFT` | `1, CTRL = 2, ALT = 4, META = 8` | A modifier mask's bits. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 324 | `private final Path file` |  |
| 325 | `private final ConcurrentLinkedQueue<Event> queue` |  |
| 326 | `private final Object moveLock` |  |
| 327 | `private final Object wake` |  |
| 328 | `private final DataOutputStream out` |  |
| 329 | `private final GZIPOutputStream gzip` |  |
| 330 | `private final Thread thread` |  |
| 331 | `private long lastMoveMs` |  |
| 332 | `private long lastWrittenMs` |  |
| 333 | `private volatile boolean closing` |  |
| 334 | `private volatile boolean finished` |  |
| 335 | `private volatile long written` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 39 | 400 | **type** `public final class InputLog` | What the player did in a sitting, as a file (TR1, 2026-10-31): the mouse, the keys and the markers beside them - the window's size, the screen open, the expanded map's view, the clock, and the moment each month turned... |
| 41 | 1 | `private InputLog()` |  |
| 65 | 1 | **type** `public enum Kind` | What an event is. |
| 74 | 51 | **type** `public record Event(long ms, Kind kind, double x, double y, int code, int mods, double a, double b, double ...` | One thing the player did or the game marked. |
| 76 | 3 | `public static Event move(long ms, double x, double y)` _(in InputLog.Event)_ |  |
| 80 | 3 | `public static Event drag(long ms, double x, double y, int button)` _(in InputLog.Event)_ |  |
| 84 | 3 | `public static Event press(long ms, double x, double y, int button, int clicks, int mods)` _(in InputLog.Event)_ |  |
| 88 | 3 | `public static Event release(long ms, double x, double y, int button, int mods)` _(in InputLog.Event)_ |  |
| 92 | 3 | `public static Event wheel(long ms, double x, double y, double dx, double dy, int mods)` _(in InputLog.Event)_ |  |
| 97 | 3 | `public static Event key(long ms, boolean down, int code, int mods, String text)` _(in InputLog.Event)_ | {@code code} from {@link InputLog#keyCode}; {@code text} may be null. |
| 101 | 3 | `public static Event window(long ms, double w, double h)` _(in InputLog.Event)_ |  |
| 105 | 3 | `public static Event screen(long ms, String name)` _(in InputLog.Event)_ |  |
| 110 | 3 | `public static Event mapView(long ms, boolean globe, double a, double b, double scale, double roll)` _(in InputLog.Event)_ | The expanded map's view: {@code a} and {@code b} are the centre (latitude, longitude on the globe; plot x, y on the plane). |
| 114 | 3 | `public static Event clock(long ms, int speedIndex, boolean running)` _(in InputLog.Event)_ |  |
| 118 | 3 | `public static Event month(long ms, int month)` _(in InputLog.Event)_ |  |
| 123 | 1 | `private static double f(double v)` _(in InputLog.Event)_ | A value as the file keeps it: a float, widened. |
| 127 | 3 | `public static int keyCode(String keyName)` | The code a key is logged under: its toolkit name's hash. |

### the file (lines 131-242)

| line | len | member | says |
|---:|---:|---|---|
| 134 | 31 | `private static void write(DataOutputStream out, Event e) throws IOException` | One event's bytes, after its kind and its step in time. |
| 167 | 38 | `private static Event read(DataInputStream in, Kind kind, long ms) throws IOException` | ...and read back, the step in time already added to {@code ms}. |
| 207 | 5 | `private static void writeText(DataOutputStream out, String s) throws IOException` | A string as its UTF-8 bytes with the length as a varint: DataOutputStream.writeUTF stops at 64 KB, and a typed text can be anything. |
| 213 | 7 | `private static String readText(DataInputStream in) throws IOException` |  |
| 221 | 7 | `private static void writeVarLong(DataOutputStream out, long v) throws IOException` |  |
| 229 | 9 | `private static long readVarLong(DataInputStream in) throws IOException` |  |
| 239 | 1 | `private static long zigzag(long v)` |  |
| 241 | 1 | `private static long unzigzag(long v)` |  |

### reading (lines 243-303)

| line | len | member | says |
|---:|---:|---|---|
| 249 | 21 | `public static List<Event> read(Path file)` | Everything readable in a log: a file cut off anywhere - a crash, a copy that stopped - gives back the events before the cut, and a record cut through is left out. |
| 272 | 8 | `public static long startOf(Path file)` | The start of a sitting as the file records it, in epoch milliseconds, or -1 when the file has no readable header. |
| 282 | 11 | `public static List<Path> sittings(Path folder)` | The sittings' logs in a recording's folder, oldest first (by the moment in the name). |
| 295 | 8 | `private static long startedAt(Path file)` | The moment in a log's file name, or -1 when the name is not one. |

### writing (lines 304-438)

| line | len | member | says |
|---:|---:|---|---|
| 307 | 10 | `public static Writer open(Path folder, String recordingId, long epochMs)` | Opens a sitting's log in {@code folder} (made if it is missing); a log that cannot be opened is said once and returns a writer that keeps nothing, because the game never waits on it. |
| 322 | 116 | **type** `public static final class Writer implements AutoCloseable` | One sitting's log being written. |
| 338 | 7 | `private Writer(Path file)` _(in InputLog.Writer)_ | A writer that keeps nothing: the log would not open. |
| 346 | 14 | `private Writer(Path file, String recordingId, long epochMs) throws IOException` _(in InputLog.Writer)_ |  |
| 362 | 1 | `public Path path()` _(in InputLog.Writer)_ | The file this sitting writes. |
| 365 | 1 | `public long written()` _(in InputLog.Writer)_ | How many events the thread has written so far (a harness waits on it). |
| 368 | 10 | `public void add(Event e)` _(in InputLog.Writer)_ | Queues an event and returns at once. |
| 379 | 27 | `private void run()` _(in InputLog.Writer)_ |  |
| 407 | 10 | `private void drain() throws IOException` _(in InputLog.Writer)_ |  |
| 420 | 17 | `public void close()` _(in InputLog.Writer)_ | Drains what is queued, finishes the gzip and closes the file; waits for the thread at most {@link #CLOSE_WAIT_MS}. |

