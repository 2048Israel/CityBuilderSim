# SaveSlotCheck.java - 1,395 lines · 42 methods · 2 constants · harnesses

`ham/citybuildersim/SaveSlotCheck.java` - generated 2026-10-25 by CodeMap; line numbers are as of that run.

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
> three autosaves - the twelfth month's, the one before a skip and the one on
> quit - is loaded here and played on: its month is its history's last, and
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
> the check moves and on the real one.

**Uses:** [GameFiles](GameFiles.md) (58), [Game](Game.md) (52), [LongPlaytest](LongPlaytest.md) (30), [SaveWriter](SaveWriter.md) (20), [GameVersion](GameVersion.md) (8), [SaveHeader](SaveHeader.md) (6), [HistorySave](HistorySave.md) (6), [TempDirs](TempDirs.md) (4), [DataSave](DataSave.md) (4), [Notice](Notice.md) (4), [TreasuryFund](TreasuryFund.md) (4), [AutoBuilder](AutoBuilder.md) (4), [Bank](Bank.md) (3), [BuildLog](BuildLog.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (2), [ConstructionControl](ConstructionControl.md) (2), [BondMarket](BondMarket.md) (2), [Exchange](Exchange.md) (2), [Timing](Timing.md) (2), [SaveCopy](SaveCopy.md) (1)

## Sections

| line | section |
|---:|---|
| 94 | · 1. eleven distinct files |
| 118 | · 2. empty means empty |
| 130 | · 3. three cities that do not touch |
| 171 | · 4. names |
| 191 | · 5. the version stamp |
| 218 | · 6. each slot round-trips into its own city |
| 236 | · 7. the autosave |
| 291 | · 7b. the autosave holds a whole month (0.7.52) |
| 305 | · 7c. the autosave off the clock (UP1, 2026-10-22) |
| 308 | · 8. histories are per slot |
| 326 | 7c. THE AUTOSAVE OFF THE CLOCK (UP1, 2026-10-22) |
| 620 | · 7c.2 to 7c.7: the writer, on a city whose history is as long as a kept one |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 348 | `SaveSlotCheck.GROWN_MONTHS` | `180` | How many months 7c grows its city: long enough that the lists and books a city reaches by itself hold something - the inbox is empty at month 124 of this fixture and has a notice by 183, the bonds' books have orders f... |
| 650 | `SaveSlotCheck.SILENT` | `new PrintStream(OutputStream.nullOutputStream())` | The stream the model's per-month printing goes to while 7c steps months: each month prints some forty lines, and nobody reads them here. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 64 | `static int fails` |  |
| 537 | `final List<String> unfilled` |  |
| 538 | `private int next` |  |
| 539 | `private int depth` |  |
| 1191 | `final IdentityHashMap<Object, String> mutable` | Every mutable object reached, by identity, with the path it was first reached by. |
| 1194 | `final List<String> opaque` | Each object of a class the walk cannot see into: its path and its class. |
| 1197 | `private final IdentityHashMap<Object, Boolean> walked` | The containers already walked (mutable or not): a cycle, or a shared one, is walked once. |
| 1200 | `private final IdentityHashMap<Object, Boolean> settled` | What isImmutable() has settled for a model object: true while it is being settled, which is how a cycle ends. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 62 | 1334 | **type** `public class SaveSlotCheck` | Verifies the slot system: ten saves plus an autosave, the version stamp, and the labels the menu is drawn from. |
| 66 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 71 | 10 | `static void assertEquals(String label, Object actual, Object expected)` |  |
| 82 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 89 | 236 | `public static void main(String[] args) throws Exception` |  |

### 7c. THE AUTOSAVE OFF THE CLOCK (UP1, 2026-10-22) (lines 326-619)

| line | len | member | says |
|---:|---:|---|---|
| 358 | 96 | `static void offTheClock() throws Exception` | 7c: grows the city and runs the subsections on it. |
| 463 | 52 | `static void copiesOnFilledStates()` | 7c.1b: the setters that copy, on states the grown city cannot reach (a bank rescue's preferred, the fund's resolutions and orders, a site on overtime): each is given an object with every field filled, and what the sav... |
| 517 | 9 | `static void copiesOf(String label, Object source, Object held)` | `held` writes the JSON `source` does and shares no mutable object with it; `source` has something in it to share. |
| 535 | 66 | **type** `static final class Filler` | An instance of a model class with every Gson-visible field set to a value that is not its default: a number, a string, the last constant of an enum, two elements in each array, list and map, a nested model object fill... |
| 541 | 23 | `<T> T make(Class<T> type)` _(in SaveSlotCheck.Filler)_ |  |
| 565 | 35 | `private Object value(java.lang.reflect.Type type)` _(in SaveSlotCheck.Filler)_ |  |
| 602 | 6 | `static void assertNoneShared(String label, Walk first, Walk second)` |  |
| 609 | 10 | `static void assertNoneUnseen(String label, Walk first, Walk second)` |  |

### 7c.2 to 7c.7: the writer, on a city whose history is as long as a kept one (lines 620-1395)

| line | len | member | says |
|---:|---:|---|---|
| 623 | 3 | `static long floorNanos()` | The floor in nanoseconds, as Game.nextMonth() reads it. |
| 628 | 4 | `static void assertTimed(String label, boolean ok)` | A timing assertion's line: Timing.verdict says OK, FAIL, or SOFT with the figure and its budget. |
| 634 | 4 | `static String autosaveName(GameFiles files)` | The slot name the autosave file carries, or null when there is no readable autosave. |
| 640 | 8 | `static JsonObject parsed(Path file)` | A file parsed as one JSON object, or null when it is missing or is not whole JSON. |
| 653 | 9 | `static void quietly(Runnable work)` | Runs {@code work} with System.out swallowed, and restores it. |
| 664 | 3 | `static void step(Game city)` | One month on the clock, as the window's clock steps it (toggleNextMonth(), no wait), the model's printing swallowed. |
| 669 | 5 | `static double median(double[] figures)` | The median of a set of figures. |
| 684 | 13 | `static void padHistory(HistorySave history)` | Grows a history to MONTHLY_KEPT months through its own recording path (HistorySave.appendMonth(), which ends in the fold recordMonth() ends in), each series given its own last recorded value, or 0 for an empty one. |
| 703 | 10 | `static void wholeOnDisk(String which, Game city, GameFiles files)` | The autosave on disk, read back: its save and its history both whole JSON, the save at the city's month, the history as long as the city's (no month has run since it was asked for). |
| 715 | 14 | `static Thread releaseLater(long ms, AtomicLong releasedAt)` | Lets the held writer go after {@code ms}, on a thread of its own; {@code releasedAt} reads 0 until it has (nanoTime, set just before the release). |
| 740 | 60 | `static void clockCost(Game city, GameFiles files, long[] now) throws Exception` | 7c.2: the clock. |
| 811 | 64 | `static void writtenBehind(Game city, GameFiles files) throws Exception` | 7c.3: what the writer writes is what the old way writes. |
| 883 | 26 | `static void oneAtATimeAlwaysWhole(Game city, GameFiles files)` | 7c.4: two autosaves asked for back to back. |
| 917 | 71 | `static void theWaits(Game city, GameFiles files) throws Exception` | 7c.5: a load, a numbered save, a skip and Quit each wait for an autosave still in the writer. |
| 996 | 42 | `static void aWriteThatFails(Game city, GameFiles files) throws Exception` | 7c.6: a write that fails. |
| 1048 | 65 | `static void theFloor(Game city, GameFiles files, long[] now) throws Exception` | 7c.7: the floor of real time. |
| 1121 | 45 | `static Game grownCity(GameFiles files, int months)` | The playtest's city, founded and played to `months`, on `files` (a scratch folder): the founding, the first builds, the schools and the advice, as LongPlaytest plays them, with its map made so the save carries one. |
| 1188 | 170 | **type** `static final class Walk` | What Gson would write of an object, as sets of identities: every object in it that someone could still change (a list, a map, an array that is not empty, a model object with a non-final field), each with the path it w... |
| 1202 | 5 | `static Walk of(Object root, String name)` _(in SaveSlotCheck.Walk)_ |  |
| 1209 | 12 | `static List<String> shared(Walk first, Walk second)` _(in SaveSlotCheck.Walk)_ | The paths, in the first walk, of every mutable object in both: "path (and where the second reached it): class". |
| 1222 | 7 | `private static boolean isLeaf(Object o)` _(in SaveSlotCheck.Walk)_ |  |
| 1230 | 3 | `private static boolean isModel(Class<?> c)` _(in SaveSlotCheck.Walk)_ |  |
| 1235 | 7 | `private static boolean isMutableJdk(Class<?> c)` _(in SaveSlotCheck.Walk)_ | The JDK's collections that Gson writes and anyone can still change. |
| 1244 | 7 | `private static boolean isImmutableJdk(Class<?> c)` _(in SaveSlotCheck.Walk)_ | List.of and its kin, and Collections' empty and singleton collections: nobody can change them. |
| 1253 | 12 | `private static List<Field> fieldsOf(Class<?> c)` _(in SaveSlotCheck.Walk)_ | Gson's fields of a model object: declared, up the chain, not static, transient or synthetic. |
| 1266 | 7 | `private static Object get(Field f, Object o)` _(in SaveSlotCheck.Walk)_ |  |
| 1275 | 23 | `private boolean isImmutable(Object o)` _(in SaveSlotCheck.Walk)_ | True when nobody can change this value: the same test as the walk's, without recording anything. |
| 1300 | 5 | `private boolean record(Object o, String path)` _(in SaveSlotCheck.Walk)_ | Records `o` under `path`; false when it was reached before (its first path stands, and it is not walked again). |
| 1306 | 51 | `private void visit(Object o, String path)` _(in SaveSlotCheck.Walk)_ |  |
| 1360 | 3 | `static void wholeMonth(String which, Game city, GameFiles files)` | The autosave as `city` stands now: wholeMonth(which, city, files, city.getMonth()). |
| 1369 | 18 | `static void wholeMonth(String which, Game city, GameFiles files, int month)` | Loads the autosave and plays two single months on (single, so neither writes it again): it carries `month`, its history ends at that month, and after the two its history runs one month at a time to the city's. |
| 1388 | 7 | `static void cleanUp(Path root)` |  |

