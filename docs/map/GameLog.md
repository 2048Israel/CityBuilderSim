# GameLog.java - 220 lines · 12 methods · 4 constants · model

`ham/citybuildersim/GameLog.java` - generated 2026-09-23 by CodeMap; line numbers are as of that run.

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
> NEVER THROWS
> 
> If the log cannot be opened - read-only folder, disk full, no permission -
> the game keeps its ordinary streams and carries on. A game that refuses to
> start because it could not open its own log file would be a worse bug than
> any it was meant to help diagnose.

**Uses:** [GameFiles](GameFiles.md) (1), [GameVersion](GameVersion.md) (1)

**Used by (8):** [CityBuilderSim](CityBuilderSim.md), [EconomyManager](EconomyManager.md), [Game](Game.md), [GameFiles](GameFiles.md), [GamePrefs](GamePrefs.md), [PolicyScreen](PolicyScreen.md), [RobustnessCheck](RobustnessCheck.md), [UserInterface](UserInterface.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 52 | `GameLog.LOG_FILE` | `"log.txt"` |  |
| 53 | `GameLog.PREVIOUS_LOG_FILE` | `"log-previous.txt"` |  |
| 62 | `GameLog.MAX_BYTES` | `2_000_000` | Above this, the file is rotated mid-run. |
| 64 | `GameLog.STAMP` | `DateTimeFormatter.ofPattern("HH:mm:ss")` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 67 | `private static PrintStream originalOut` |  |
| 68 | `private static PrintStream originalErr` |  |
| 69 | `private static Path logPath` |  |
| 70 | `private static boolean started` |  |
| 173 | `private final OutputStream console` |  |
| 174 | `private OutputStream file` |  |
| 175 | `private long written` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 50 | 171 | **type** `public final class GameLog` | Everything the game prints, written somewhere a player can find it. |
| 72 | 1 | `private GameLog()` |  |
| 75 | 3 | `public static Path file()` | Where the log is, or null if logging never started. |
| 79 | 3 | `public static boolean isRunning()` |  |
| 86 | 41 | `public static synchronized void start(GameFiles files)` | Begins logging into the save folder. |
| 129 | 3 | `public static void note(String message)` | A line that is worth finding later, stamped with the time. |
| 134 | 20 | `public static void failure(String context, Throwable cause)` | A failure, with its stack trace, in one place in the file. |
| 155 | 9 | `private static void rotate(Path current, Path previous)` |  |
| 171 | 49 | **type** `private static final class TeeStream extends OutputStream` | Writes to two places at once. |
| 177 | 4 | `TeeStream(OutputStream console, OutputStream file)` _(in GameLog.TeeStream)_ |  |
| 182 | 4 | `public void write(int b) throws IOException` _(in GameLog.TeeStream)_ |  |
| 187 | 4 | `public void write(byte[] b, int off, int len) throws IOException` _(in GameLog.TeeStream)_ |  |
| 192 | 20 | `private void writeToFile(byte[] b, int off, int len)` _(in GameLog.TeeStream)_ |  |
| 213 | 6 | `public void flush() throws IOException` _(in GameLog.TeeStream)_ |  |

