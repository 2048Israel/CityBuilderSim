# Recording.java - 402 lines · 23 methods · 12 constants · model

`ham/citybuildersim/Recording.java` - generated 2026-11-12 by CodeMap; line numbers are as of that run.

> One recorded city on disk (TR1, 2026-10-31): the folder {@code recordings/<id>/} that "Record this city" keeps, and the one
> place that knows its files. A month is one file ({@code month-0000123.bin}: a head, the map's sidecar as the game made it,
> then the save as compact JSON, gzipped), the history one more ({@code history.json.gz}), the meta
> ({@code recording.json}) says whose it is, and each sitting's input is a log ({@code input-<epochMs>.bin.gz}, InputLog).
> Reading is by month and cheap: the map comes first in the file, so a quick step reads only the head of it. Writing is two
> {@link SaveWriter.Task}s built on the FX thread from snapshots and run on the save writer's thread, so a month pays only
> its fill. No JavaFX.
> 
> WHY IT EXISTS (TR1)
> 
> Jerus is filming the game's trailer, and a live take cannot be repeated: a peg breaks by chance
> ({@code ForeignAccounts.BREAK_BASE}, {@code BREAK_SLOPE}), a collapse comes when it comes, and a big city's month takes
> time. So the city is played once, reloading earlier saves until the story happens, and every month of the timeline last
> played is kept; the replay (ReplayCity) shows any month of it on a throwaway copy, as often as he likes. The history is not
> written every month (his is 6 to 27 MB): it goes at the autosave's pace and when recording stops, and the replay cuts it at
> the month on screen (HistorySave.cutAt).

**Uses:** [GameFiles](GameFiles.md) (11), [SaveWriter](SaveWriter.md) (4), [HistorySave](HistorySave.md) (3), [DataSave](DataSave.md) (2)

**Used by (10):** [FollowBench](FollowBench.md), [Game](Game.md), [InputLog](InputLog.md), [InputTimeline](InputTimeline.md), [ReplayCheck](ReplayCheck.md), [ReplayCity](ReplayCity.md), [ReplayMode](ReplayMode.md), [ReplayMoveProbe](ReplayMoveProbe.md), [SaveFileCheck](SaveFileCheck.md), [UiProbe](UiProbe.md)

## Sections

| line | section |
|---:|---|
| 91 | · ids and listing |
| 160 | · reading |
| 277 | · writing |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 49 | `Recording.MAGIC` | `0x43524543` | The first four bytes of a month's file: "CREC". |
| 52 | `Recording.FORMAT` | `1` | The month file's layout: 1 is the head (magic, format, month, map length), the map's sidecar, then the save's gzipped JSON. |
| 55 | `Recording.MONTH_PREFIX` | `"month-"` | What a month's file name starts with; the month follows in seven digits, so the folder sorts the way the months run. |
| 58 | `Recording.MONTH_SUFFIX` | `".bin"` | What a month's file name ends with. |
| 61 | `Recording.HISTORY_FILE` | `"history.json.gz"` | The history's file in the folder: its compact JSON, gzipped. |
| 64 | `Recording.META_FILE` | `"recording.json"` | The meta's file in the folder: who the recording is of, as JSON. |
| 67 | `Recording.INPUT_PREFIX` | `"input-"` | What a sitting's input log starts with in the folder; the sitting's start in epoch milliseconds follows. |
| 70 | `Recording.INPUT_SUFFIX` | `".bin.gz"` | What a sitting's input log ends with. |
| 73 | `Recording.DEFLATE_LEVEL` | `java.util.zip.Deflater.DEFAULT_COMPRESSION` | How hard the save's JSON is deflated (java.util.zip): the default, 6 - ReplayCheck prints the size and time of 6 and of 1 on a grown city. |
| 76 | `Recording.ID_NAME_LENGTH` | `40` | The longest the name part of an id is, so a long city name does not make a long folder name. |
| 81 | `Recording.GSON` | `new Gson()` |  |
| 119 | `Recording.ID_SHAPE` | `java.util.regex.Pattern.compile("([a-z0-9]+(?:-[a-z0-9]+)*)-\\d{8}-\\d{6}")` | The shape {@link #newId} writes: words of letters and digits joined by hyphens, the day, the time. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 83 | `private final Path folder` |  |
| 84 | `private final Meta meta` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 46 | 357 | **type** `public final class Recording` | One recorded city on disk (TR1, 2026-10-31): the folder {@code recordings/<id>/} that "Record this city" keeps, and the one place that knows its files. |
| 79 | 1 | **type** `public record Meta(String id, String cityName, String version, int saveFormat, long startedAt, boolean earth)` | Who a recording is of: kept in recording.json when the first month is written. |
| 86 | 4 | `private Recording(Path folder, Meta meta)` |  |

### ids and listing (lines 91-159)

| line | len | member | says |
|---:|---:|---|---|
| 97 | 20 | `public static String newId(String cityName, long epochMs)` | A file-safe id for a new recording: the city's name in lower case without accents, its words joined by hyphens, then the moment as {@code yyyyMMdd-HHmmss} - "medellin-20261031-142355". |
| 125 | 5 | `public static boolean isId(String id)` | Whether {@code id} is exactly the shape {@link #newId} writes (TR2): a recording's id comes back from a save a player can edit, and it names a folder under {@code recordings/}, so nothing else - no separator, no dot, ... |
| 132 | 10 | `public static Recording at(Path folder)` | The recording in a folder; its meta() is null when the folder holds none that can be read. |
| 144 | 15 | `public static List<Recording> all(Path recordingsDirectory)` | Every folder in {@code recordingsDirectory} that holds a meta, newest started first; empty when the folder is missing. |

### reading (lines 160-276)

| line | len | member | says |
|---:|---:|---|---|
| 162 | 1 | `public Path folder()` |  |
| 165 | 1 | `public String id()` | The recording's id: its folder's name. |
| 168 | 1 | `public Meta meta()` | Who it is of, or null in a folder with no readable meta. |
| 171 | 3 | `public Path monthFile(int month)` | The file a month is in; it may not exist. |
| 176 | 14 | `public int[] months()` | The months on disk, ascending, from the folder's listing (a .bak or .tmp beside a month is not one). |
| 192 | 7 | `static int monthOf(String name)` | The month a file name stands for, or -1 when it is not exactly a month file's name. |
| 201 | 4 | `public int firstMonth()` | The first month on disk, or -1 with none. |
| 207 | 4 | `public int lastMonth()` | The last month on disk, or -1 with none. |
| 213 | 10 | `public long bytes()` | Every file in the folder, in bytes (the logs and the .bak beside a rewritten month too). |
| 228 | 12 | `public byte[] mapOf(int month)` | The map sidecar of a month, as the game made it (deflated), or null: no file for the month, the city had no map then, or the file will not read. |
| 242 | 14 | `public String saveJsonOf(int month)` | The month's save as compact JSON, or null when there is no file for it or it will not read. |
| 258 | 7 | `private static int readHead(DataInputStream in, int month) throws IOException` | Reads a month file's head: the map's length (-1: none), or -2 when it is not this recording's format or this month's. |
| 267 | 9 | `public HistorySave history()` | The history, parsed, or null when there is none yet or it will not read. |

### writing (lines 277-402)

| line | len | member | says |
|---:|---:|---|---|
| 288 | 36 | `public static SaveWriter.Task monthTask(GameFiles files, Path folder, Meta meta, int month, DataSave snapshot, byte[] map, bool...` | A month to write: built on the FX thread from a snapshot, run on the save writer's thread. |
| 326 | 14 | `static byte[] monthBytes(DataSave snapshot, byte[] map, int month, int level) throws IOException` | A month file's bytes: the head, the map, then the save's compact JSON gzipped at {@code level}. |
| 342 | 24 | `public static SaveWriter.Task historyTask(GameFiles files, Path folder, HistorySave snapshot)` | The history to write: built on the FX thread from a snapshot; written whole (the .tmp and the swap), a newer one replacing one still waiting. |
| 372 | 22 | `private static boolean dropMonthsFrom(Path folder, int month)` | Deletes the month files numbered {@code month} or above (and a .bak or .tmp of one): the timeline last played is the one kept, and the month about to be written is the new timeline's, so the old one's goes too. |
| 396 | 6 | **type** `private static final class Gz extends GZIPOutputStream` | A gzip stream at a chosen deflate level (GZIPOutputStream has no constructor for one). |
| 397 | 4 | `Gz(OutputStream out, int level) throws IOException` _(in Recording.Gz)_ |  |

