# ReloadCheck.java - 549 lines · 34 methods · 9 constants · harnesses

`ham/citybuildersim/ReloadCheck.java` - generated 2026-10-28 by CodeMap; line numbers are as of that run.

> Plays a city, saves it, loads the save into a second game, runs both a month
> and compares what each would save: a reloaded city has to play its next
> month as the city that kept running. Not part of the game.
> 
> WHY THIS EXISTS. 0.7.46 (A1) cut the series that differ a month after a
> reload from 62 of 226 to 12 of 229, and the residual was left (todo-archive,
> star 2). Nothing compared a reloaded city with its running twin past the
> moment of the load: LongPlaytest's roundTrip() compares at the load only,
> and SaveFileCheck 14 plays fixture cities on, not a played city's whole
> save. FW6 measured it with a scratch tool - the probe city saved at a month,
> loaded into a second Game, both run a month, both saved, the two saves'
> JSON compared leaf by leaf. Every history series matched for 24 months; the
> save did not: each sector book's stockRevalued and stockCounted (the month
> after a load counts no revaluation), Construction's drawnImported and
> drawnLocal, and the lastInvestment notes (the first investment pass after a
> load weighs a different candidate, because the markets' month tally is not
> saved). This is that tool as a check, so the next change that lets a city
> play differently after a load is a red line and not a month nobody compared.
> 
> WHAT IT PROVES, on the probe city (Danzik on the flag's preset, played by
> LongPlaytest.probePlay) saved at about months 30, 120, 360 and 1200, each time
> loaded into a second Game, with both run a month and then another:
> 
>   1. EVERY HISTORY SERIES of the reloaded city is the running city's, a
>      month on and two months on.
>   2. ...AND EVERY FIGURE IN ITS SAVE, to the cent, a month on and two on.
>   3. THE CAUSES, ONE BY ONE, so a red section 2 says why: (a) every
>      sector's stock revaluation is counted in the reload's first month and
>      equals the live city's; (b) Construction's materials drawn at home and
>      imported; (c) every market's month tally right after the load, and the
>      investment notes a month on; (d) each sector book's shares right after
>      the load; (e) the price index's rent and food weights right after the
>      load; (f) a time skip reports what the running city's skip reports.
>   4. THE REAL EARTH: sections 1 and 2 at one checkpoint (about month 120)
>      on LongPlaytest.probeCity(files, "rotterdam"), where earth-2.bin is
>      named; a line saying it did not run, never a FAIL, where it is not.
> 
> Every fixture is asserted: the history holds more than a hundred series and
> the save more than ten thousand numbers (so the comparison is not empty),
> and each cause is shown at work in the live city before its parity is.
> 
> WHY FOUR CHECKPOINTS AND NOT THREE: a cause shows only in the states that
> exercise it. The investment notes, for one, came out the same after a reload
> at months 35 and 365 of this city and different at 123 and 1205 (Heavy
> Industry weighing an Electric Arc Furnace on the live city's tally and a
> Blast Furnace on an empty one), so a harness with the first two alone would
> have passed a bug it was written to find. The months are the probe's own
> (probePlay's skips are fixed by its stop count), so they do not move unless
> LongPlaytest does; a fixture line says each cause was seen at work.

**Uses:** [Game](Game.md) (13), [GameFiles](GameFiles.md) (6), [EarthData](EarthData.md) (6), [LongPlaytest](LongPlaytest.md) (4), [SectorBooks](SectorBooks.md) (3), [GoodsMarket](GoodsMarket.md) (2), [Sector](Sector.md) (2), [SectorStatements](SectorStatements.md) (2), [TimeSkipReport](TimeSkipReport.md) (2), [EarthSite](EarthSite.md) (2), [Construction](Construction.md) (1)

**Used by (2):** [BridgeCheck](BridgeCheck.md), [LandmassCheck](LandmassCheck.md)

## Sections

| line | section |
|---:|---|
| 107 | · the findings |
| 175 | · the numbers |
| 186 | · the two files |
| 244 | · one checkpoint |
| 389 | · the report |
| 490 | · the Earth |
| 512 | · the run |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 79 | `ReloadCheck.CHECKPOINTS` | `{ 30, 120, 360, 1200 }` | The months the live city is saved at; LongPlaytest.probePlay overshoots by a few, so a point reads g.getMonth(). |
| 82 | `ReloadCheck.EARTH_CHECKPOINT` | `120` | The month the Earth city is saved at (section 4). |
| 85 | `ReloadCheck.EARTH_SPOT` | `"rotterdam"` | Where section 4's city is founded: the real Earth round Rotterdam, as LongPlaytest --earth rotterdam plays it. |
| 88 | `ReloadCheck.SKIP_MONTHS` | `2` | A time skip of section 3f: two months, so it is a skip (the game autosaves before one), and two are run on each city. |
| 91 | `ReloadCheck.CENT` | `0.005` | Two numbers are the same when they differ by no more than a cent... |
| 94 | `ReloadCheck.RELATIVE` | `1e-9` | ...and by no more than a billionth of the larger (or of 1, for the tiny). |
| 100 | `ReloadCheck.EXEMPT` | `Set.of("slotName", "savedAt", "mapStamp")` | The keys no two saves of one city share, whatever it did: the slot's name, the time it was written and the content hash of the map sidecar (the map is MapCheck's, and batch CC1-M's). |
| 103 | `ReloadCheck.SHOWN` | `12` | How many differing paths a failing line lists, each with both values; the rest is counted. |
| 105 | `ReloadCheck.QUIET` | `new PrintStream(OutputStream.nullOutputStream())` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 76 | `static int fails` |  |
| 115 | `int differing` |  |
| 116 | `final List<String> first` |  |
| 117 | `final Map<String, Integer> kinds` |  |
| 140 | `int numbers, series` |  |
| 145 | `int compared` |  |
| 146 | `final Map<String, Integer> saw` |  |
| 163 | `final String city` |  |
| 164 | `final int month` |  |
| 166 | `String problem` | Why the checkpoint could not be compared (a save or a load that failed), or null. |
| 167 | `Compared history1, history2, save1, save2` |  |
| 168 | `final Cause revalued` |  |
| 170 | `int skips, skipsWithOutbreak` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 74 | 476 | **type** `public class ReloadCheck` | Plays a city, saves it, loads the save into a second game, runs both a month and compares what each would save: a reloaded city has to play its next month as the city that kept running. |

### the findings (lines 107-174)

| line | len | member | says |
|---:|---:|---|---|
| 114 | 23 | **type** `static class Findings` | What a comparison found: how many things differ, in how many kinds of place, and the first SHOWN of them, each with both values. |
| 119 | 6 | `void differ(String what, String live, String reloaded)` _(in ReloadCheck.Findings)_ |  |
| 127 | 9 | `String kindsLine()` _(in ReloadCheck.Findings)_ | The kinds, the first few with how many of each: what a failing line says before the list. |
| 139 | 3 | **type** `static final class Compared extends Findings` | Two saves' files compared leaf by leaf: how many numbers and how many series (arrays of numbers) were compared. |
| 144 | 16 | **type** `static final class Cause extends Findings` | One cause (section 3): what was compared, and what the live city showed the cause at work in (the fixture). |
| 148 | 1 | `void saw(String what, int n)` _(in ReloadCheck.Cause)_ |  |
| 150 | 4 | `void compare(String what, double live, double reloaded)` _(in ReloadCheck.Cause)_ |  |
| 155 | 4 | `void compare(String what, String live, String reloaded)` _(in ReloadCheck.Cause)_ |  |
| 162 | 12 | **type** `static final class Point` | What one checkpoint found: the city saved at {@code month}, reloaded, and both played on. |
| 172 | 1 | `Point(String city, int month)` _(in ReloadCheck.Point)_ |  |

### the numbers (lines 175-185)

| line | len | member | says |
|---:|---:|---|---|
| 177 | 6 | `static boolean same(double a, double b)` |  |
| 184 | 1 | `static String num(double v)` |  |

### the two files (lines 186-243)

| line | len | member | says |
|---:|---:|---|---|
| 188 | 7 | `static JsonElement read(Path file)` |  |
| 197 | 6 | `static boolean isNumeric(JsonElement e)` | A number, or the strings Gson writes for the ones JSON has no word for. |
| 204 | 1 | `static double valueOf(JsonElement e)` |  |
| 206 | 5 | `static String show(JsonElement e)` |  |
| 213 | 7 | `static Compared compare(Path live, Path reloaded)` | The live city's file against the reloaded city's, key by key and leaf by leaf; null (a file not there) is itself a difference. |
| 221 | 22 | `static void walk(String path, JsonElement a, JsonElement b, Compared c)` |  |

### one checkpoint (lines 244-388)

| line | len | member | says |
|---:|---:|---|---|
| 252 | 28 | `static Point reload(GameFiles files, Game g, String city)` | The city {@code g} saved where it stands and loaded into a second game, observed right after the load (the markets' tally, the shares, the price index's weights), then both run a month (the sector books, Construction,... |
| 282 | 6 | `private static Compared[] saveAndCompare(GameFiles files, Game g, Game back, String when)` | Both cities saved (the live one to slot 2, the reloaded to slot 3) and their files compared: {the history's, the save's}. |
| 295 | 23 | `private static void observeTheLoad(Point p, Game g, Game back)` | Right after the load, before any month: (c) every market's tally of the month just closed - the goods imported, exported, filled at home, offered and bid for; the trades list and the month's draws have no getter that ... |
| 319 | 14 | `private static void compareShares(Cause c, String what, SectorBooks.Shares live, SectorBooks.Shares re)` |  |
| 338 | 25 | `private static void observeAMonthOn(Point p, Game g, Game back)` | A month on: (a) each sector book's stock revaluation and whether it was counted, (b) Construction's materials drawn at home and imported in the month last struck, and (c) the investment notes the investors left. |
| 370 | 16 | `private static void skips(Point p, Game g, Game back)` | (f) Two skips run on each city from the same month, and the second report's health figures compared. |
| 387 | 1 | `private static int outbreakMonths(TimeSkipReport report)` |  |

### the report (lines 389-489)

| line | len | member | says |
|---:|---:|---|---|
| 392 | 5 | `static void check(String label, boolean ok, String detail, List<String> more)` | One labelled assertion, printed either way; a failing one lists, under its line, what differs. |
| 398 | 1 | `static void check(String label, boolean ok, String detail)` |  |
| 401 | 6 | `static void nothingDiffers(String label, Findings f, String what)` | An assertion that nothing differs: the first difference on its line, the others (to SHOWN) under it, the rest counted. |
| 408 | 5 | `static String seen(Map<String, Integer> saw)` |  |
| 414 | 5 | `static Map<String, Integer> sawTogether(List<Point> points, Function<Point, Cause> cause)` |  |
| 420 | 3 | `static boolean allAbove0(Map<String, Integer> saw)` |  |
| 424 | 1 | `static String when(Point p, int monthsOn)` |  |
| 427 | 16 | `static void history(List<Point> points)` | Sections 1 and 4's history lines: the fixture line first, then each checkpoint a month on and two months on. |
| 445 | 12 | `static void whole(List<Point> points)` | Sections 2 and 4's whole-save lines. |
| 458 | 20 | `static void section3(List<Point> points)` |  |
| 480 | 9 | `static void cause(List<Point> points, Function<Point, Cause> which, String id, String when, String label)` | One cause: the fixture line (the live city showed it at work, in every way named), then its parity at each checkpoint. |

### the Earth (lines 490-511)

| line | len | member | says |
|---:|---:|---|---|
| 493 | 18 | `static boolean earthHere()` | Whether section 4 can run: earth-2.bin named (-Dcbs.earth, or at data/earth-2.bin) and the game reading it; says so in one line where not. |

### the run (lines 512-549)

| line | len | member | says |
|---:|---:|---|---|
| 514 | 35 | `public static void main(String[] args)` |  |

