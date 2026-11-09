# ReplayCheck.java - 1,571 lines · 31 methods · 3 constants · harnesses

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
>      its input up flagged, and ends on the screen playing from the month's start reaches; nothing in the recording is written;
>  10. Follow in the real window: the rules (FollowRules) drop what a copy must not do again - anything logged on a game menu (Save,
>      Load, New city, Settings and Quit live there) or while the copy is on one, anything under a dialog, a click on a node that
>      opens a menu or moves the clock, Space, Left, Right and F11, Esc and P where they would open the menu - and keep the
>      rest; then a sitting logged with the writer over a copy of the recording is played into the window by frames: the copy
>      is at each logged month as its marker passes, each click on a rail tab takes it to that tab's screen before the SCREEN
>      marker comes and the screens arrive in order, the click on the Menu button is dropped and Follow stays on through every
>      press, the ghost sits at the logged pointer, a stop and a join in mid-month reaches the screen and month that a run
>      from the month's start reaches, a viewer's press ends Follow and his move does only past the grace and 8 px, and leaving
>      the replay leaves the copy and the player's folder as they were (FollowBench.check and followCheck; the window part is
>      skipped where JavaFX cannot start).

**Uses:** [InputLog](InputLog.md) (185), [CameraPath](CameraPath.md) (103), [Recording](Recording.md) (58), [FollowRules](FollowRules.md) (48), [InputTimeline](InputTimeline.md) (46), [FollowPlayer](FollowPlayer.md) (26), [GameFiles](GameFiles.md) (24), [Game](Game.md) (15), [ReplayCity](ReplayCity.md) (14), [HistorySave](HistorySave.md) (12), [DataSave](DataSave.md) (11), [ReplayGraphs](ReplayGraphs.md) (11), [CityMap](CityMap.md) (7), [MapTiles](MapTiles.md) (7), [MapFrame](MapFrame.md) (7), [FollowBench](FollowBench.md) (6), [ReplayMoveProbe](ReplayMoveProbe.md) (5), [World](World.md) (4), [TilePainter](TilePainter.md) (4), [PartnerTrade](PartnerTrade.md) (4), [TempDirs](TempDirs.md) (4), [SaveWriter](SaveWriter.md) (4), [Timing](Timing.md) (3), [SaveSlotCheck](SaveSlotCheck.md) (3), [GameVersion](GameVersion.md) (2), [CityCalendar](CityCalendar.md) (1), [TaxPolicy](TaxPolicy.md) (1), [SaveHeader](SaveHeader.md) (1), [LongPlaytest](LongPlaytest.md) (1)

**Used by (1):** [FollowBench](FollowBench.md)

## Sections

| line | section |
|---:|---|
| 201 | · 1. a recording writes one month each, and a rewind rewrites from the next |
| 411 | · 3. nothing sticks |
| 440 | · 4. the input log round-trips; a re-enacted Save, Load, Quit writes nothing |
| 565 | · 5. the camera's arithmetic |
| 787 | · 5d. a replay's camera move loads first, then runs with nothing unpainted and nothing asked (CM1) |
| 808 | · 6. recording costs the month only its fill, and changes nothing in it |
| 935 | 7. TR1's review: a failed log write, an id from a save, a failed first write, a month that autosaves |
| 1135 | 8. the timeline |
| 1366 | 9. Follow into a headless copy |
| 1468 | 10. Follow in the window |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 88 | `ReplayCheck.GROWN` | `48` | The month the section 1 to 4 city is grown to before it is recorded. |
| 91 | `ReplayCheck.GROWN_SIX` | `36` | The month the section 6 cities are grown to. |
| 1471 | `ReplayCheck.GAME_MENUS` | `{ "showMainMenu", "showSavingMenu", "showSaveSlotConfirm", "showLoadMenu", "l...` | The screens UserInterface.isGameMenu takes for a game menu: the main menu (Save, Load, New city, Settings, Credits and Quit are on it or behind it), the pages they open, and the replay's list. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 85 | `static int fails` |  |
| 1139 | `final ReplayCity copy` |  |
| 1140 | `final List<String> calls` |  |
| 1141 | `final List<InputLog.Event> events` |  |
| 1142 | `final List<Boolean> catching` |  |
| 1143 | `final List<Integer> months` |  |
| 1144 | `final List<String> screens` |  |
| 1145 | `final List<Double> screenTimes` |  |
| 1146 | `double elapsed` |  |
| 1147 | `String screen` |  |
| 1148 | `boolean loaded` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 83 | 1489 | **type** `public class ReplayCheck` | Replay of a recorded city (batch TR1, 2026-10-31): the recorder, the throwaway copy, the input log, and what recording costs; and Follow (batch TR2, 2026-11-09): the timeline of the input logs and the player that play... |
| 94 | 4 | `static void helper(String label, boolean ok)` | An assertion's line: Timing.verdict says OK, FAIL, or SOFT with the figure and its budget. |
| 100 | 10 | `static double[] districtsMiddle(CityMap map)` | The middle of a map's districts in plots {x, y}: where a view of the city looks. |
| 112 | 13 | `static long[] stampsOf(MapTiles tiles, CityMap map, List<long[]> view)` | The stamps of the tiles in a view, their district plans drawn first as the view's worker draws them (the jobs run and kept here). |
| 127 | 3 | `static boolean near(double a, double b, double tol)` | Whether two figures agree to {@code tol}, absolutely under 1 and relatively over it. |
| 132 | 1 | **type** `record Figures(long population, double gdp, double cash, double price, long buildings)` | What the city shows at a month's end: the figures the replay has to give back. |
| 134 | 6 | `static Figures figuresOf(Game g)` |  |
| 142 | 9 | `static String parted(Figures got, Figures want)` | Whether two figures agree, and which differ when they do not (the words for the line). |
| 153 | 12 | `public static Map<String, String> hashes(Path dir) throws Exception` | Every file under a folder: its path from the folder, its size and its SHA-256 (public since TR2: FollowBench asks it in the window). |
| 167 | 8 | `public static String firstDifference(Map<String, String> before, Map<String, String> after)` | The first difference between two maps of hashes, in words, or null when they are the same. |
| 177 | 9 | `static void clockMonth(Game game)` | Plays one month on the clock's path with the month's printed report silenced: the city prints its reports to the log. |
| 187 | 5 | `static Map<Integer, byte[]> monthBytes(Recording rec) throws IOException` |  |
| 193 | 706 | `public static void main(String[] args) throws Exception` |  |
| 907 | 19 | `static void movedChecks(ReplayMoveProbe.Moved m, double glideSeconds, String ranBy, boolean acrossLevels)` | Section 5d's assertions for one glide, loaded and run in the window (ReplayMoveProbe.check()): it reached Ready with every plan view loaded, held its pins while it waited, ran on the click or the key; no frame of its ... |
| 928 | 6 | `static boolean closedIgnores(InputLog.Writer closed) throws Exception` | Whether a closed log ignores an event added to it: nothing more is read from its file. |

### 7. TR1's review: a failed log write, an id from a save, a failed first write, a month that autosaves (lines 935-1134)

| line | len | member | says |
|---:|---:|---|---|
| 938 | 3 | **type** `interface Body` | A body that may throw: what {@link #printed} runs. |
| 939 | 1 | `void run() throws Exception` _(in ReplayCheck.Body)_ |  |
| 943 | 11 | `static String printed(Body body) throws Exception` | What a body prints to the log, as a string (and not to the log). |
| 956 | 5 | `static GameFiles.Result printedResult(java.util.function.Supplier<GameFiles.Result> write) throws Exception` | What a task's write returns, with what it prints kept out of the log. |
| 963 | 8 | `static boolean awaitTrue(java.util.function.BooleanSupplier condition, long ms) throws InterruptedException` | Waits up to {@code ms} for a condition, polling; whether it came true. |
| 972 | 5 | `static int occurrences(String text, String part)` |  |
| 979 | 127 | `static void reviewChecks(Game a, Game b, GameFiles filesA, GameFiles filesB) throws Exception` | {@code a} and {@code b}: the section 6 cities, both recording. |
| 1112 | 22 | `static boolean tripFirstWrite(Game city) throws Exception` | The city's own first write after a load: a month written, a month past the next one made undeletable (a directory with a file in it), recording switched off and on as a load does; the first month then cannot clear the... |

### 8. the timeline (lines 1135-1365)

| line | len | member | says |
|---:|---:|---|---|
| 1138 | 33 | **type** `static final class Tape implements FollowPlayer.Target` | What a Follow target was handed, in order; with a copy it loads each month whole, as Follow does in the window (sections 8 and 9). |
| 1150 | 1 | `Tape(ReplayCity copy)` _(in ReplayCheck.Tape)_ |  |
| 1152 | 5 | `public void loadMonth(int month)` _(in ReplayCheck.Tape)_ |  |
| 1158 | 10 | `public void event(InputLog.Event e, boolean catchingUp)` _(in ReplayCheck.Tape)_ |  |
| 1169 | 1 | `long count(InputLog.Kind kind)` _(in ReplayCheck.Tape)_ |  |
| 1173 | 6 | `static void playOut(FollowPlayer player, Tape tape, double frameMs)` | Plays the timeline out in frames of {@code frameMs}; the tape's clock is the frames' end. |
| 1180 | 3 | `static int[] months(int from, int to)` |  |
| 1184 | 173 | `static void timelineChecks()` |  |
| 1358 | 7 | `static boolean playsNothing(InputTimeline empty)` |  |

### 9. Follow into a headless copy (lines 1366-1467)

| line | len | member | says |
|---:|---:|---|---|
| 1368 | 99 | `static void followChecks(Recording rec, GameFiles files) throws Exception` |  |

### 10. Follow in the window (lines 1468-1571)

| line | len | member | says |
|---:|---:|---|---|
| 1474 | 77 | `static void followRulesChecks()` |  |
| 1556 | 15 | `static void followWindowChecks(Recording rec) throws Exception` | Section 10 in the real window, off screen: FollowBench's findings on the Reenactor and on a sitting followed in a copy of {@code rec}, each printed as a line. |

