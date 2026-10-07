# GameLog.java - 348 lines · 20 methods · 6 constants · model

`ham/citybuildersim/GameLog.java` - generated 2026-10-06 by CodeMap; line numbers are as of that run.

> Everything the game prints, written somewhere a player can find it.
> 
> WHY
> 
> This codebase talks. It prints monthly reports, "Buildings loaded from...",
> "Autosave failed", the construction-format warning, the reason a load was
> refused. All of it goes to System.out - and in a packaged build there is no
> console attached, so every one of those lines is written to nowhere.
> 
> That is the difference between a bug report and a shrug. A player says "it
> broke"; without this there is nothing to read, and the most useful thing the
> game already knew about its own failure was thrown away at the moment it
> happened.
> 
> HOW
> 
> System.out and System.err are replaced with streams that write to BOTH the
> original destination and a file. Nothing else in the game changes - every
> existing println is now a log line, which is the point: a logging system that
> needs a thousand call sites edited does not get adopted.
> 
> TWO FILES
> 
> On start, the previous log is moved aside rather than appended to. So there
> are always exactly two: the run happening now, and the one before it. That
> second file is the important one - the usual sequence is that something goes
> wrong, the player restarts, and only then thinks to ask for the log.
> 
> PAST THE CAP, FAILURES STILL GET THROUGH (0.7.50)
> 
> Jerus's 0.7.49 game froze after its log had reached the cap, throwing on
> every frame, and the file showed none of it: the trigger and the freeze
> were both past the end. Ordinary output still stops at the cap; a failure
> - the crash handler's, or any other failure() - is still written, each one
> once by its first lines, until FAILURE_BYTES more have gone in.
> 
> NEVER THROWS
> 
> If the log cannot be opened - read-only folder, disk full, no permission -
> the game keeps its ordinary streams and carries on. A game that refuses to
> start because it could not open its own log file would be a worse bug than
> any it was meant to help diagnose.

**Uses:** [GameFiles](GameFiles.md) (1), [GameVersion](GameVersion.md) (1)

**Used by (9):** [CityBuilderSim](CityBuilderSim.md), [EconomyManager](EconomyManager.md), [Game](Game.md), [GameFiles](GameFiles.md), [GamePrefs](GamePrefs.md), [PolicyScreen](PolicyScreen.md), [RobustnessCheck](RobustnessCheck.md), [TimeChart](TimeChart.md), [UserInterface](UserInterface.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 62 | `GameLog.LOG_FILE` | `"log.txt"` |  |
| 63 | `GameLog.PREVIOUS_LOG_FILE` | `"log-previous.txt"` |  |
| 76 | `GameLog.MAX_BYTES` | `2_000_000` | The cap on ordinary output: once either stream has written more than this to the file, the file says so and from then takes failures alone, FAILURE_BYTES more of them (0.7.50; until then, nothing more). |
| 82 | `GameLog.FAILURE_BYTES` | `200_000` | How much more the file takes past the cap, in failures alone (0.7.50): a tenth of the cap. |
| 89 | `GameLog.FIRST_LINES` | `4` | How many of a failure's lines make it the same failure as one already written past the cap: what it says (without the time), the exception and its message, and the first two frames. |
| 91 | `GameLog.STAMP` | `DateTimeFormatter.ofPattern("HH:mm:ss")` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 94 | `private static PrintStream originalOut` |  |
| 95 | `private static PrintStream originalErr` |  |
| 96 | `private static Path logPath` |  |
| 97 | `private static boolean started` |  |
| 100 | `private static volatile Sink sink` | The file both streams write to, and what it takes past the cap; null until start(). |
| 230 | `private OutputStream file` |  |
| 231 | `private final long failureRoom` |  |
| 232 | `private boolean capped` |  |
| 233 | `private long failureBytes` |  |
| 234 | `private final Set<String> seen` |  |
| 316 | `private final OutputStream console` |  |
| 317 | `private final Sink file` |  |
| 318 | `private final long cap` |  |
| 319 | `private long written` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 60 | 289 | **type** `public final class GameLog` | Everything the game prints, written somewhere a player can find it. |
| 102 | 1 | `private GameLog()` |  |
| 105 | 3 | `public static Path file()` | Where the log is, or null if logging never started. |
| 109 | 3 | `public static boolean isRunning()` |  |
| 116 | 43 | `public static synchronized void start(GameFiles files)` | Begins logging into the save folder. |
| 161 | 3 | `public static void note(String message)` | A line that is worth finding later, stamped with the time. |
| 169 | 6 | `public static void failure(String context, Throwable cause)` | A failure, with its stack trace, in one place in the file - and past the cap (0.7.50), still in the file, once for its first lines. |
| 177 | 20 | `static String entry(String time, String context, Throwable cause)` | The lines failure() writes, as one string: the same lines, in the same order, it always printed one by one. |
| 203 | 9 | `static String firstLines(String context, Throwable cause)` | What makes two failures the same one past the cap: FIRST_LINES of it - what it says without the time, the exception and its message, and the first frames. |
| 213 | 9 | `private static void rotate(Path current, Path previous)` |  |
| 228 | 79 | **type** `static final class Sink` | The log file, shared by the two streams (0.7.50): everything until the cap, then failures alone - each once by its first lines, until `failureRoom` more bytes have gone in, when it says so and closes. |
| 236 | 4 | `Sink(OutputStream file, long failureRoom)` _(in GameLog.Sink)_ |  |
| 242 | 11 | `synchronized boolean write(byte[] b, int off, int len)` _(in GameLog.Sink)_ | Ordinary output: written until the cap, and false once nothing ordinary is. |
| 255 | 15 | `synchronized void reachCap(long cap)` _(in GameLog.Sink)_ | The cap reached: said once in the file, which takes failures alone from here. |
| 271 | 3 | `synchronized boolean capped()` _(in GameLog.Sink)_ |  |
| 279 | 21 | `synchronized boolean failure(String firstLines, String entry)` _(in GameLog.Sink)_ | A failure past the cap: written whole unless one with the same first lines already was, while there is room. |
| 301 | 5 | `synchronized void flush()` _(in GameLog.Sink)_ |  |
| 314 | 34 | **type** `static final class TeeStream extends OutputStream` | Writes to two places at once. |
| 321 | 5 | `TeeStream(OutputStream console, Sink file, long cap)` _(in GameLog.TeeStream)_ |  |
| 327 | 4 | `public void write(int b) throws IOException` _(in GameLog.TeeStream)_ |  |
| 332 | 4 | `public void write(byte[] b, int off, int len) throws IOException` _(in GameLog.TeeStream)_ |  |
| 337 | 5 | `private void writeToFile(byte[] b, int off, int len)` _(in GameLog.TeeStream)_ |  |
| 343 | 4 | `public void flush() throws IOException` _(in GameLog.TeeStream)_ |  |

