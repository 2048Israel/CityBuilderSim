# ReplayCheck.java - 1,451 lines · 29 methods · 2 constants · harnesses

`ham/citybuildersim/ReplayCheck.java` - generated 2026-11-09 by CodeMap; line numbers are as of that run.

> Replay of a recorded city (batch TR1, 2026-10-31): the recorder, the throwaway copy, the input log, and what recording costs;
> and Follow (batch TR2, 2026-11-09): the timeline of the input logs and the player that plays it, with TR1's four review fixes.
> Timing harness (Timing.HARNESSES): section 6 judges the recorder's FX-thread cost through Timing.
> 
> What it has to prove (the numbers are the brief's section 7, docs/queue/TR1.md):
>   1. a recording writes one month each, and a rewind rewrites from the next: a grown city, "Record this city" on, played 24
>      months, has 24 month files, the meta and the history, and nothing under saves/ but the autosave's and the slot the check
>      saved to; loaded back at month 12 (the save carries the switch and the id) and played 3 months with the property tax moved (R0: the income tax is shut until 1917, so the open dial),
>      the months past 15 are gone, 13 to 15 are rewritten (13's save carries the new rate) and 1 to 12 are byte for byte what
>      they were; a city founded after that records nothing; and a writer held behind by the backlog never holds the clock;
>   2. replaying month N gives the figures the city had at N: population, cash, GDP, the price level and the map's building
>      count, at the first, a middle and the last month; a quick step swaps the map to N's count and nothing else; the
>      recording's history cut at a month is the history the city had then, series by series, and a cut is right across the
>      yearly fold and the finished years of trade;
>   3. nothing sticks: a rate changed on the copy is the recorded one after a step away and back, the copy records nothing and
>      lives in a folder of its own, and the recording's files and the player's folder are byte for byte unchanged;
>   4. the input log round-trips - every kind, event for event, the moves closer than MOVE_EVERY_MS dropped, the sittings oldest
>      first, a file cut mid-record reads back a prefix, a sitting still open reads up to its last flush - and a re-enacted Save,
>      Load and Quit on the copy writes nothing outside the copy's own folder;
>   5. the camera's arithmetic (CameraPath): a glide's ends are exact on the globe and the plane, its middle scale the ends'
>      geometric mean, its ease symmetric, monotone and still at both ends, the centre's share the formula's (2/3 at the middle
>      for a ratio of 4, the eased share when the scales match, halfway on the equator), its month linear; the fly out ends at
>      the widest scale on the city and takes as long as the doublings say; the smooth hand's five notches sum to ZOOM_STEP^5
>      and ease out, a held key's velocity rises and falls without a jump, a hitch is a pause, a drift moves until still();
>      5b. the graphs behind the city (ReplayGraphs): the recording's months in order, scaled to its highest, a year of GDP;
>      5c. a quick step leaves most of the map's pictures as they were: a month on, fewer than half the tiles in view have another
>      stamp, those that have the same stamp paint the same pixels in both maps, and the new map's tiles read the ground carried over;
>      5d. a replay's camera move loads first and then runs (CM1): in the real window, off screen, the normal game draws its map with
>      no camera, no loaded move and nothing pinned; a glide from the street to the whole map reaches Ready with every plan view
>      loaded, holds its pins while it waits and runs on a click; a glide across months does the same and runs on a key; while
>      they run no frame asks the worker, draws unpainted ground or lets a picture go, over at least L0 and FAR for the first, and
>      afterwards nothing is pinned or held; and Esc while a glide loads leaves the camera idle with nothing held (ReplayMoveProbe.check;
>      skipped where JavaFX cannot start, as in the cloud);
>   6. recording costs the month only its fill and changes nothing in it: two same cities played 12 months, one recording, agree
>      to the last digit; the recorder's FX-thread cost per month is within SaveWriter.CLOCK_MS (printed, with the size of a month's
>      file and of the history, and of a month at deflate level 6 and 1);
>   7. TR1's review (TR2): a log writer whose write failed takes no more events and lets go of its file (it can be deleted); a save
>      whose recording id is not the shape Recording.newId writes ("../escape", a drive path) gives no folder, recording off, and
>      records nothing outside recordings/; a first write after a load that fails leaves no month of the old timeline past it, and
>      one that cannot clear the old months writes nothing and ends the recording on the next month; a month that autosaves is
>      filled once, and the recording's month is the autosave's save but for its name;
>   8. the timeline (InputTimeline, FollowPlayer): two sittings with a rewind give a segment each (and a third where the recording
>      holds the older one's later months), startOf finds a month's segment and marker, a skipped month the marker past it, and
>      none a month no log reaches; a sitting that loads an earlier save is cut into runs; stateAt gives the screen, the mouse, the
>      window, the map and the buttons; a gap of more than IDLE_GAP_MS plays in IDLE_KEEP_MS with idle skipping on and in full with
>      it off; crossing a segment switches sittings; stopping and rejoining at now() lands where it stopped;
>   9. Follow into a headless copy: a real recording and a sitting logged with the writer, played by FollowPlayer into a Target over
>      a ReplayCity that loads each month whole as the log turns it: the copy is at each logged month in order, the screens arrive
>      in logged order at their logged times divided by the speed (to the frame), joining mid-month loads the month and catches
>      its input up flagged, and ends on the screen playing from the month's start reaches; nothing in the recording is written.

**Uses:** [InputLog](InputLog.md) (149), [CameraPath](CameraPath.md) (103), [Recording](Recording.md) (57), [InputTimeline](InputTimeline.md) (46), [FollowPlayer](FollowPlayer.md) (26), [GameFiles](GameFiles.md) (22), [Game](Game.md) (14), [ReplayCity](ReplayCity.md) (14), [HistorySave](HistorySave.md) (12), [DataSave](DataSave.md) (11), [ReplayGraphs](ReplayGraphs.md) (11), [CityMap](CityMap.md) (7), [MapTiles](MapTiles.md) (7), [MapFrame](MapFrame.md) (7), [ReplayMoveProbe](ReplayMoveProbe.md) (5), [World](World.md) (4), [TilePainter](TilePainter.md) (4), [PartnerTrade](PartnerTrade.md) (4), [TempDirs](TempDirs.md) (4), [SaveWriter](SaveWriter.md) (4), [SaveSlotCheck](SaveSlotCheck.md) (3), [Timing](Timing.md) (2), [GameVersion](GameVersion.md) (2), [CityCalendar](CityCalendar.md) (1), [TaxPolicy](TaxPolicy.md) (1), [SaveHeader](SaveHeader.md) (1)

## Sections

| line | section |
|---:|---|
| 189 | · 1. a recording writes one month each, and a rewind rewrites from the next |
| 399 | · 3. nothing sticks |
| 428 | · 4. the input log round-trips; a re-enacted Save, Load, Quit writes nothing |
| 553 | · 5. the camera's arithmetic |
| 775 | · 5d. a replay's camera move loads first, then runs with nothing unpainted and nothing asked (CM1) |
| 796 | · 6. recording costs the month only its fill, and changes nothing in it |
| 919 | 7. TR1's review: a failed log write, an id from a save, a failed first write, a month that autosaves |
| 1119 | 8. the timeline |
| 1350 | 9. Follow into a headless copy |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 76 | `ReplayCheck.GROWN` | `48` | The month the section 1 to 4 city is grown to before it is recorded. |
| 79 | `ReplayCheck.GROWN_SIX` | `36` | The month the section 6 cities are grown to. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 73 | `static int fails` |  |
| 1123 | `final ReplayCity copy` |  |
| 1124 | `final List<String> calls` |  |
| 1125 | `final List<InputLog.Event> events` |  |
| 1126 | `final List<Boolean> catching` |  |
| 1127 | `final List<Integer> months` |  |
| 1128 | `final List<String> screens` |  |
| 1129 | `final List<Double> screenTimes` |  |
| 1130 | `double elapsed` |  |
| 1131 | `String screen` |  |
| 1132 | `boolean loaded` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 71 | 1381 | **type** `public class ReplayCheck` | Replay of a recorded city (batch TR1, 2026-10-31): the recorder, the throwaway copy, the input log, and what recording costs; and Follow (batch TR2, 2026-11-09): the timeline of the input logs and the player that play... |
| 82 | 4 | `static void helper(String label, boolean ok)` | An assertion's line: Timing.verdict says OK, FAIL, or SOFT with the figure and its budget. |
| 88 | 10 | `static double[] districtsMiddle(CityMap map)` | The middle of a map's districts in plots {x, y}: where a view of the city looks. |
| 100 | 13 | `static long[] stampsOf(MapTiles tiles, CityMap map, List<long[]> view)` | The stamps of the tiles in a view, their district plans drawn first as the view's worker draws them (the jobs run and kept here). |
| 115 | 3 | `static boolean near(double a, double b, double tol)` | Whether two figures agree to {@code tol}, absolutely under 1 and relatively over it. |
| 120 | 1 | **type** `record Figures(long population, double gdp, double cash, double price, long buildings)` | What the city shows at a month's end: the figures the replay has to give back. |
| 122 | 6 | `static Figures figuresOf(Game g)` |  |
| 130 | 9 | `static String parted(Figures got, Figures want)` | Whether two figures agree, and which differ when they do not (the words for the line). |
| 141 | 12 | `static Map<String, String> hashes(Path dir) throws Exception` | Every file under a folder: its path from the folder, its size and its SHA-256. |
| 155 | 8 | `static String firstDifference(Map<String, String> before, Map<String, String> after)` | The first difference between two maps of hashes, in words, or null when they are the same. |
| 165 | 9 | `static void clockMonth(Game game)` | Plays one month on the clock's path with the month's printed report silenced: the city prints its reports to the log. |
| 175 | 5 | `static Map<Integer, byte[]> monthBytes(Recording rec) throws IOException` |  |
| 181 | 702 | `public static void main(String[] args) throws Exception` |  |
| 891 | 19 | `static void movedChecks(ReplayMoveProbe.Moved m, double glideSeconds, String ranBy, boolean acrossLevels)` | Section 5d's assertions for one glide, loaded and run in the window (ReplayMoveProbe.check()): it reached Ready with every plan view loaded, held its pins while it waited, ran on the click or the key; no frame of its ... |
| 912 | 6 | `static boolean closedIgnores(InputLog.Writer closed) throws Exception` | Whether a closed log ignores an event added to it: nothing more is read from its file. |

### 7. TR1's review: a failed log write, an id from a save, a failed first write, a month that autosaves (lines 919-1118)

| line | len | member | says |
|---:|---:|---|---|
| 922 | 3 | **type** `interface Body` | A body that may throw: what {@link #printed} runs. |
| 923 | 1 | `void run() throws Exception` _(in ReplayCheck.Body)_ |  |
| 927 | 11 | `static String printed(Body body) throws Exception` | What a body prints to the log, as a string (and not to the log). |
| 940 | 5 | `static GameFiles.Result printedResult(java.util.function.Supplier<GameFiles.Result> write) throws Exception` | What a task's write returns, with what it prints kept out of the log. |
| 947 | 8 | `static boolean awaitTrue(java.util.function.BooleanSupplier condition, long ms) throws InterruptedException` | Waits up to {@code ms} for a condition, polling; whether it came true. |
| 956 | 5 | `static int occurrences(String text, String part)` |  |
| 963 | 127 | `static void reviewChecks(Game a, Game b, GameFiles filesA, GameFiles filesB) throws Exception` | {@code a} and {@code b}: the section 6 cities, both recording. |
| 1096 | 22 | `static boolean tripFirstWrite(Game city) throws Exception` | The city's own first write after a load: a month written, a month past the next one made undeletable (a directory with a file in it), recording switched off and on as a load does; the first month then cannot clear the... |

### 8. the timeline (lines 1119-1349)

| line | len | member | says |
|---:|---:|---|---|
| 1122 | 33 | **type** `static final class Tape implements FollowPlayer.Target` | What a Follow target was handed, in order; with a copy it loads each month whole, as Follow does in the window (sections 8 and 9). |
| 1134 | 1 | `Tape(ReplayCity copy)` _(in ReplayCheck.Tape)_ |  |
| 1136 | 5 | `public void loadMonth(int month)` _(in ReplayCheck.Tape)_ |  |
| 1142 | 10 | `public void event(InputLog.Event e, boolean catchingUp)` _(in ReplayCheck.Tape)_ |  |
| 1153 | 1 | `long count(InputLog.Kind kind)` _(in ReplayCheck.Tape)_ |  |
| 1157 | 6 | `static void playOut(FollowPlayer player, Tape tape, double frameMs)` | Plays the timeline out in frames of {@code frameMs}; the tape's clock is the frames' end. |
| 1164 | 3 | `static int[] months(int from, int to)` |  |
| 1168 | 173 | `static void timelineChecks()` |  |
| 1342 | 7 | `static boolean playsNothing(InputTimeline empty)` |  |

### 9. Follow into a headless copy (lines 1350-1451)

| line | len | member | says |
|---:|---:|---|---|
| 1352 | 99 | `static void followChecks(Recording rec, GameFiles files) throws Exception` |  |

