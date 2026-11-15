# SaveSlotCheck.java - 1,978 lines · 53 methods · 4 constants · harnesses

`ham/citybuildersim/SaveSlotCheck.java` - generated 2026-11-15 by CodeMap; line numbers are as of that run.

> Verifies the slot system: ten saves plus an autosave, the version stamp, and
> the labels the menu is drawn from.
> 
> The thing this is really guarding is INDEPENDENCE. A save menu that shows ten
> slots and quietly writes them all to the same file, or draws slot 3's label
> from slot 7's city, is worse than a single save - it invites a player to
> spread a hundred hours across ten slots that were never really there.
> 
> AND THE AUTOSAVE HOLDS A WHOLE MONTH (0.7.52). Until 0.7.51 the twelfth
> month's autosave was written near the top of Game.nextMonth(), after the
> calendar had turned and before the month ran: the file said month N, its
> history ended at N - 1, and a city loaded from it never ran N. Each of the
> four autosaves - the twelfth month's, the one before a skip, the skip's end (UP6)
> and the one on quit - is loaded here and played on: its month is its history's last, and
> the history has no gap and no month twice.
> 
> AND THE AUTOSAVE IS WRITTEN OFF THE CLOCK (UP1, 2026-10-22). The autosave
> is filled on the FX thread and written on SaveWriter's, from a snapshot, and
> section 7c holds each half of that on one grown city: 7c.1 that the two
> snapshots taken of it share no object a month could change (and 7c.1b the
> same of the states the city cannot reach by itself); 7c.2 that the month
> which autosaves, on a history of MONTHLY_KEPT months, holds the clock at
> most SaveWriter.CLOCK_MS - the one timing assertion in this file, through
> Timing; 7c.3 that the files a snapshot writes behind three months are the
> files the old way writes on this thread, field for field, and load at the
> month asked; 7c.4 that two autosaves asked for back to back write once or
> twice, never at once, and the file is whole after each; 7c.5 that a load, a
> numbered save, a skip and Quit each wait for an autosave still held in the
> writer; 7c.6 that a write which fails is reported on the calling thread,
> leaves the earlier autosave whole, and is followed by one that writes; and
> 7c.7 that the floor of real time (SaveWriter.FLOOR_SECONDS) holds the
> autosaves to one in thirty seconds however fast the months come, on a clock
> the check moves and on the real one. Section 7d (LB3) holds a file open while
> a write swaps over it, as the Rankings board's read and an antivirus scan do on
> Windows: the write waits for the handle (GameFiles.SWAP_RETRIES) and lands, one
> held past every retry fails whole and says so, and the next lands.
> 
> AND THE SKIP'S END, THE CLOSE AND THE PLAYER'S SAVE (UP6, 2026-11-06). Section 7e holds
> what UP6 added, on a young city whose save clock stands still: 7e.1 that a skip inside
> the floor ends with its own last month on disk, named for the skip, and the running
> clock keeps its floor after it; 7e.2 that the window's close autosaves like Quit and
> waits for the writer; 7e.3 that the player's Save returns with the writer held and its
> files are whole when its result comes; 7e.3b that a second Save while the first is in
> the writer waits for it, so both are written; 7e.3c that the writer touches no slot but
> the one the player saved to, and refuses the autosave's slot and one past the last; and
> 7e.4 that a history which could not be written is told beside the save's result - to the
> player (the sync way too) and, for an autosave, in the inbox - with the .bak kept.
> Section 7f (UP6b) causes a skip to throw inside a month before closing, delays an
> autosave's report across a month, and writes newer history beside a failed save;
> it also holds a numbered background result until the calling thread reports it.

**Uses:** [GameFiles](GameFiles.md) (122), [Game](Game.md) (62), [LongPlaytest](LongPlaytest.md) (30), [SaveWriter](SaveWriter.md) (24), [SaveHeader](SaveHeader.md) (8), [GameVersion](GameVersion.md) (8), [Notice](Notice.md) (7), [Bridges](Bridges.md) (6), [HistorySave](HistorySave.md) (6), [TempDirs](TempDirs.md) (5), [DataSave](DataSave.md) (4), [TreasuryFund](TreasuryFund.md) (4), [AutoBuilder](AutoBuilder.md) (4), [Landmasses](Landmasses.md) (4), [Bank](Bank.md) (3), [BuildLog](BuildLog.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (2), [ConstructionControl](ConstructionControl.md) (2), [BondMarket](BondMarket.md) (2), [Exchange](Exchange.md) (2), [Timing](Timing.md) (2), [SaveCopy](SaveCopy.md) (1), [BridgeWorks](BridgeWorks.md) (1)

**Used by (1):** [ReplayCheck](ReplayCheck.md)

## Sections

| line | section |
|---:|---|
| 111 | · 1. eleven distinct files |
| 135 | · 2. empty means empty |
| 147 | · 3. three cities that do not touch |
| 188 | · 4. names |
| 208 | · 5. the version stamp |
| 235 | · 6. each slot round-trips into its own city |
| 253 | · 7. the autosave |
| 309 | · 7b. the autosave holds a whole month (0.7.52) |
| 347 | · 7c. the autosave off the clock (UP1, 2026-10-22) |
| 350 | · 7d. a file held open while a write swaps over it (LB3) |
| 353 | · 7e. the skip's end, the close and the player's Save (UP6, 2026-11-06) |
| 357 | · 8. histories are per slot |
| 375 | 7c. THE AUTOSAVE OFF THE CLOCK (UP1, 2026-10-22) |
| 476 | 7e. THE SKIP'S END, THE CLOSE AND THE PLAYER'S SAVE (UP6, 2026-11-06) |
| 1200 | · 7c.2 to 7c.7: the writer, on a city whose history is as long as a kept one |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 397 | `SaveSlotCheck.GROWN_MONTHS` | `180` | How many months 7c grows its city: long enough that the lists and books a city reaches by itself hold something - the inbox is empty at month 124 of this fixture and has a notice by 183, the bonds' books have orders f... |
| 400 | `SaveSlotCheck.RELEASE_ON_REFUSAL` | `2` | The refused try of the swap at which 7d lets go of the file it holds: the second of GameFiles.SWAP_RETRIES, well inside them; the swap's own count, no clock (SS2). |
| 490 | `SaveSlotCheck.LANDS_WITHIN_SECONDS` | `10` | How long a Save's result is waited for before the check calls it lost. |
| 1230 | `SaveSlotCheck.SILENT` | `new PrintStream(OutputStream.nullOutputStream())` | The stream the model's per-month printing goes to while 7c steps months: each month prints some forty lines, and nobody reads them here. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 81 | `static int fails` |  |
| 1117 | `final List<String> unfilled` |  |
| 1118 | `private int next` |  |
| 1119 | `private int depth` |  |
| 1774 | `final IdentityHashMap<Object, String> mutable` | Every mutable object reached, by identity, with the path it was first reached by. |
| 1777 | `final List<String> opaque` | Each object of a class the walk cannot see into: its path and its class. |
| 1780 | `private final IdentityHashMap<Object, Boolean> walked` | The containers already walked (mutable or not): a cycle, or a shared one, is walked once. |
| 1783 | `private final IdentityHashMap<Object, Boolean> settled` | What isImmutable() has settled for a model object: true while it is being settled, which is how a cycle ends. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 79 | 1900 | **type** `public class SaveSlotCheck` | Verifies the slot system: ten saves plus an autosave, the version stamp, and the labels the menu is drawn from. |
| 83 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 88 | 10 | `static void assertEquals(String label, Object actual, Object expected)` |  |
| 99 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 106 | 268 | `public static void main(String[] args) throws Exception` |  |

### 7c. THE AUTOSAVE OFF THE CLOCK (UP1, 2026-10-22) (lines 375-475)

| line | len | member | says |
|---:|---:|---|---|
| 412 | 63 | `static void heldOpen() throws Exception` | 7d (LB3's gate review, 2026-10-31): a write's swap over a file another handle has open. |

### 7e. THE SKIP'S END, THE CLOSE AND THE PLAYER'S SAVE (UP6, 2026-11-06) (lines 476-1199)

| line | len | member | says |
|---:|---:|---|---|
| 493 | 11 | `static GameFiles.Result landed(String label, java.util.concurrent.CompletableFuture<GameFiles.Result> future)` | A Save's result, or null after a FAIL line when none came within LANDS_WITHIN_SECONDS. |
| 506 | 7 | `static boolean returnsAtOnce(Runnable call) throws InterruptedException` | Runs {@code call} on a thread of its own and says whether it came back within five seconds: a Save that waits for a held writer never does. |
| 515 | 4 | `static void makeObstacle(Path tmp) throws IOException` | A folder with a file in it where the write streams its .tmp: no write can open it (a read-only folder would not stop one on Windows), as 7c.6. |
| 521 | 11 | `static Map<String, String> hashed(Path folder) throws Exception` | Every regular file in a folder by name, with its bytes' SHA-256. |
| 534 | 8 | `static boolean refuses(Runnable call)` | True when {@code call} throws an IllegalArgumentException. |
| 544 | 6 | `static int lastMonthOf(Path historyFile)` | The last month a history file's axis runs to, or -1 when it is not whole JSON. |
| 552 | 5 | `static GameFiles.Result quietlyResult(java.util.function.Supplier<GameFiles.Result> call)` | The result of {@code call} with the model's printing swallowed. |
| 558 | 207 | `static void skipEndCloseAndSave() throws Exception` |  |
| 767 | 136 | `static void autosaveReviewFollowUps() throws Exception` | UP6b: cause a broken skip, a late report and a failed save beside newer history, on scratch files. |
| 905 | 4 | `static int lastMonthOfHistory(Game city)` | The last completed month in the live history, before any attempted save can obscure it. |
| 918 | 96 | `static void offTheClock() throws Exception` | 7c: grows the city and runs the subsections on it. |
| 1023 | 72 | `static void copiesOnFilledStates()` | 7c.1b: the setters that copy, on states the grown city cannot reach (a bank rescue's preferred, the fund's resolutions and orders, a site on overtime): each is given an object with every field filled, and what the sav... |
| 1097 | 9 | `static void copiesOf(String label, Object source, Object held)` | `held` writes the JSON `source` does and shares no mutable object with it; `source` has something in it to share. |
| 1115 | 66 | **type** `static final class Filler` | An instance of a model class with every Gson-visible field set to a value that is not its default: a number, a string, the last constant of an enum, two elements in each array, list and map, a nested model object fill... |
| 1121 | 23 | `<T> T make(Class<T> type)` _(in SaveSlotCheck.Filler)_ |  |
| 1145 | 35 | `private Object value(java.lang.reflect.Type type)` _(in SaveSlotCheck.Filler)_ |  |
| 1182 | 6 | `static void assertNoneShared(String label, Walk first, Walk second)` |  |
| 1189 | 10 | `static void assertNoneUnseen(String label, Walk first, Walk second)` |  |

### 7c.2 to 7c.7: the writer, on a city whose history is as long as a kept one (lines 1200-1978)

| line | len | member | says |
|---:|---:|---|---|
| 1203 | 3 | `static long floorNanos()` | The floor in nanoseconds, as Game.nextMonth() reads it. |
| 1208 | 4 | `static void assertTimed(String label, boolean ok)` | A timing assertion's line: Timing.verdict says OK, FAIL, or SOFT with the figure and its budget. |
| 1214 | 4 | `static String autosaveName(GameFiles files)` | The slot name the autosave file carries, or null when there is no readable autosave. |
| 1220 | 8 | `static JsonObject parsed(Path file)` | A file parsed as one JSON object, or null when it is missing or is not whole JSON. |
| 1233 | 9 | `static void quietly(Runnable work)` | Runs {@code work} with System.out swallowed, and restores it. |
| 1244 | 3 | `static void step(Game city)` | One month on the clock, as the window's clock steps it (toggleNextMonth(), no wait), the model's printing swallowed. |
| 1249 | 5 | `static double median(double[] figures)` | The median of a set of figures. |
| 1264 | 13 | `static void padHistory(HistorySave history)` | Grows a history to MONTHLY_KEPT months through its own recording path (HistorySave.appendMonth(), which ends in the fold recordMonth() ends in), each series given its own last recorded value, or 0 for an empty one. |
| 1283 | 10 | `static void wholeOnDisk(String which, Game city, GameFiles files)` | The autosave on disk, read back: its save and its history both whole JSON, the save at the city's month, the history as long as the city's (no month has run since it was asked for). |
| 1295 | 14 | `static Thread releaseLater(long ms, AtomicLong releasedAt)` | Lets the held writer go after {@code ms}, on a thread of its own; {@code releasedAt} reads 0 until it has (nanoTime, set just before the release). |
| 1320 | 60 | `static void clockCost(Game city, GameFiles files, long[] now) throws Exception` | 7c.2: the clock. |
| 1391 | 64 | `static void writtenBehind(Game city, GameFiles files) throws Exception` | 7c.3: what the writer writes is what the old way writes. |
| 1463 | 26 | `static void oneAtATimeAlwaysWhole(Game city, GameFiles files)` | 7c.4: two autosaves asked for back to back. |
| 1499 | 72 | `static void theWaits(Game city, GameFiles files) throws Exception` | 7c.5: a load, a numbered save, a skip and Quit each wait for an autosave still in the writer. |
| 1579 | 42 | `static void aWriteThatFails(Game city, GameFiles files) throws Exception` | 7c.6: a write that fails. |
| 1631 | 65 | `static void theFloor(Game city, GameFiles files, long[] now) throws Exception` | 7c.7: the floor of real time. |
| 1704 | 45 | `static Game grownCity(GameFiles files, int months)` | The playtest's city, founded and played to `months`, on `files` (a scratch folder): the founding, the first builds, the schools and the advice, as LongPlaytest plays them, with its map made so the save carries one. |
| 1771 | 170 | **type** `static final class Walk` | What Gson would write of an object, as sets of identities: every object in it that someone could still change (a list, a map, an array that is not empty, a model object with a non-final field), each with the path it w... |
| 1785 | 5 | `static Walk of(Object root, String name)` _(in SaveSlotCheck.Walk)_ |  |
| 1792 | 12 | `static List<String> shared(Walk first, Walk second)` _(in SaveSlotCheck.Walk)_ | The paths, in the first walk, of every mutable object in both: "path (and where the second reached it): class". |
| 1805 | 7 | `private static boolean isLeaf(Object o)` _(in SaveSlotCheck.Walk)_ |  |
| 1813 | 3 | `private static boolean isModel(Class<?> c)` _(in SaveSlotCheck.Walk)_ |  |
| 1818 | 7 | `private static boolean isMutableJdk(Class<?> c)` _(in SaveSlotCheck.Walk)_ | The JDK's collections that Gson writes and anyone can still change. |
| 1827 | 7 | `private static boolean isImmutableJdk(Class<?> c)` _(in SaveSlotCheck.Walk)_ | List.of and its kin, and Collections' empty and singleton collections: nobody can change them. |
| 1836 | 12 | `private static List<Field> fieldsOf(Class<?> c)` _(in SaveSlotCheck.Walk)_ | Gson's fields of a model object: declared, up the chain, not static, transient or synthetic. |
| 1849 | 7 | `private static Object get(Field f, Object o)` _(in SaveSlotCheck.Walk)_ |  |
| 1858 | 23 | `private boolean isImmutable(Object o)` _(in SaveSlotCheck.Walk)_ | True when nobody can change this value: the same test as the walk's, without recording anything. |
| 1883 | 5 | `private boolean record(Object o, String path)` _(in SaveSlotCheck.Walk)_ | Records `o` under `path`; false when it was reached before (its first path stands, and it is not walked again). |
| 1889 | 51 | `private void visit(Object o, String path)` _(in SaveSlotCheck.Walk)_ |  |
| 1943 | 3 | `static void wholeMonth(String which, Game city, GameFiles files)` | The autosave as `city` stands now: wholeMonth(which, city, files, city.getMonth()). |
| 1952 | 18 | `static void wholeMonth(String which, Game city, GameFiles files, int month)` | Loads the autosave and plays two single months on (single, so neither writes it again): it carries `month`, its history ends at that month, and after the two its history runs one month at a time to the city's. |
| 1971 | 7 | `static void cleanUp(Path root)` |  |

