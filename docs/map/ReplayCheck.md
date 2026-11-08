# ReplayCheck.java - 895 lines · 13 methods · 2 constants · harnesses

`ham/citybuildersim/ReplayCheck.java` - generated 2026-11-08 by CodeMap; line numbers are as of that run.

> Replay of a recorded city (batch TR1, 2026-10-31): the recorder, the throwaway copy, the input log, and what recording costs.
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
>      file and of the history, and of a month at deflate level 6 and 1).

**Uses:** [CameraPath](CameraPath.md) (103), [InputLog](InputLog.md) (73), [Recording](Recording.md) (20), [HistorySave](HistorySave.md) (12), [ReplayGraphs](ReplayGraphs.md) (11), [GameFiles](GameFiles.md) (9), [CityMap](CityMap.md) (7), [MapTiles](MapTiles.md) (7), [ReplayCity](ReplayCity.md) (7), [DataSave](DataSave.md) (7), [MapFrame](MapFrame.md) (7), [Game](Game.md) (6), [ReplayMoveProbe](ReplayMoveProbe.md) (5), [World](World.md) (4), [TilePainter](TilePainter.md) (4), [PartnerTrade](PartnerTrade.md) (4), [SaveWriter](SaveWriter.md) (4), [SaveSlotCheck](SaveSlotCheck.md) (3), [Timing](Timing.md) (2), [GameVersion](GameVersion.md) (2), [CityCalendar](CityCalendar.md) (1), [TaxPolicy](TaxPolicy.md) (1), [TempDirs](TempDirs.md) (1)

## Sections

| line | section |
|---:|---|
| 174 | · 1. a recording writes one month each, and a rewind rewrites from the next |
| 384 | · 3. nothing sticks |
| 413 | · 4. the input log round-trips; a re-enacted Save, Load, Quit writes nothing |
| 538 | · 5. the camera's arithmetic |
| 760 | · 5d. a replay's camera move loads first, then runs with nothing unpainted and nothing asked (CM1) |
| 781 | · 6. recording costs the month only its fill, and changes nothing in it |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 61 | `ReplayCheck.GROWN` | `48` | The month the section 1 to 4 city is grown to before it is recorded. |
| 64 | `ReplayCheck.GROWN_SIX` | `36` | The month the section 6 cities are grown to. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 58 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 56 | 840 | **type** `public class ReplayCheck` | Replay of a recorded city (batch TR1, 2026-10-31): the recorder, the throwaway copy, the input log, and what recording costs. |
| 67 | 4 | `static void helper(String label, boolean ok)` | An assertion's line: Timing.verdict says OK, FAIL, or SOFT with the figure and its budget. |
| 73 | 10 | `static double[] districtsMiddle(CityMap map)` | The middle of a map's districts in plots {x, y}: where a view of the city looks. |
| 85 | 13 | `static long[] stampsOf(MapTiles tiles, CityMap map, List<long[]> view)` | The stamps of the tiles in a view, their district plans drawn first as the view's worker draws them (the jobs run and kept here). |
| 100 | 3 | `static boolean near(double a, double b, double tol)` | Whether two figures agree to {@code tol}, absolutely under 1 and relatively over it. |
| 105 | 1 | **type** `record Figures(long population, double gdp, double cash, double price, long buildings)` | What the city shows at a month's end: the figures the replay has to give back. |
| 107 | 6 | `static Figures figuresOf(Game g)` |  |
| 115 | 9 | `static String parted(Figures got, Figures want)` | Whether two figures agree, and which differ when they do not (the words for the line). |
| 126 | 12 | `static Map<String, String> hashes(Path dir) throws Exception` | Every file under a folder: its path from the folder, its size and its SHA-256. |
| 140 | 8 | `static String firstDifference(Map<String, String> before, Map<String, String> after)` | The first difference between two maps of hashes, in words, or null when they are the same. |
| 150 | 9 | `static void clockMonth(Game game)` | Plays one month on the clock's path with the month's printed report silenced: the city prints its reports to the log. |
| 160 | 5 | `static Map<Integer, byte[]> monthBytes(Recording rec) throws IOException` |  |
| 166 | 694 | `public static void main(String[] args) throws Exception` |  |
| 868 | 19 | `static void movedChecks(ReplayMoveProbe.Moved m, double glideSeconds, String ranBy, boolean acrossLevels)` | Section 5d's assertions for one glide, loaded and run in the window (ReplayMoveProbe.check()): it reached Ready with every plan view loaded, held its pins while it waited, ran on the click or the key; no frame of its ... |
| 889 | 6 | `static boolean closedIgnores(InputLog.Writer closed) throws Exception` | Whether a closed log ignores an event added to it: nothing more is read from its file. |

