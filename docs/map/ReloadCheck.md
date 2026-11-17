# ReloadCheck.java - 721 lines · 41 methods · 14 constants · harnesses

`ham/citybuildersim/ReloadCheck.java` - generated 2026-11-17 by CodeMap; line numbers are as of that run.

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
>   2. ...AND EVERY FIGURE IN ITS SAVE, to the cent, a month on and two on
>      (the map sidecar's stamp among them since CC1-M).
>   3. THE CAUSES, ONE BY ONE, so a red section 2 says why: (a) every
>      sector's stock revaluation is counted in the reload's first month and
>      equals the live city's; (b) Construction's materials drawn at home and
>      imported; (c) every market's month tally right after the load, and the
>      investment notes a month on; (d) each sector book's shares right after
>      the load; (e) the price index's rent and food weights right after the
>      load; (f) a time skip reports what the running city's skip reports;
>      (g) the sudden stop's words and the hot money's peaks; (h) Quarrying's
>      carry; (i) each market's draws since the month closed, and the seller
>      rows they put back a month on; (j) a seller-priced market's trades of a
>      month are off its list the month after.
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

**Uses:** [Game](Game.md) (17), [Trade](Trade.md) (13), [Good](Good.md) (12), [GameFiles](GameFiles.md) (7), [EarthData](EarthData.md) (6), [Sector](Sector.md) (5), [GoodsMarket](GoodsMarket.md) (4), [LongPlaytest](LongPlaytest.md) (4), [SectorBooks](SectorBooks.md) (3), [SectorStatements](SectorStatements.md) (2), [TimeSkipReport](TimeSkipReport.md) (2), [EarthSite](EarthSite.md) (2), [CapitalFlows](CapitalFlows.md) (1), [Construction](Construction.md) (1)

**Used by (2):** [BridgeCheck](BridgeCheck.md), [LandmassCheck](LandmassCheck.md)

## Sections

| line | section |
|---:|---|
| 131 | · the findings |
| 205 | · the numbers |
| 216 | · the two files |
| 274 | · the caused point |
| 301 | · one checkpoint |
| 529 | · the report |
| 654 | · the Earth |
| 676 | · the run |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 86 | `ReloadCheck.CHECKPOINTS` | `{ 30, 120, 360, 1200 }` | The months the live city is saved at; LongPlaytest.probePlay overshoots by a few, so a point reads g.getMonth(). |
| 89 | `ReloadCheck.EARTH_CHECKPOINT` | `120` | The month the Earth city is saved at (section 4). |
| 92 | `ReloadCheck.EARTH_SPOT` | `"rotterdam"` | Where section 4's city is founded: the real Earth round Rotterdam, as LongPlaytest --earth rotterdam plays it. |
| 95 | `ReloadCheck.CAUSED_STOP` | `"the reload check's own sudden stop"` | The words of the sudden stop the harness starts on the live city before its caused point (FB3, FW6-C): no checkpoint of the probe city is in a stop, and the words ride in a String no array carries. |
| 98 | `ReloadCheck.HAND_ORDER_SHARE` | `.5` | The share of what the makers of one good hold that the harness's hand order between the presses takes (FB3, FW6-C): part of it, so the order is all filled at home and none imported. |
| 101 | `ReloadCheck.CARRY_SAW` | `"cities that carried limestone into aggregate"` | What the carry's fixture counts (FB3, FW6-C): also how main() asks whether any checkpoint carried limestone before it places one. |
| 104 | `ReloadCheck.CAUSED_CARRY` | `100` | The tonnes of limestone the harness places as a carry when no checkpoint's city had one (FB3, FW6-C). |
| 107 | `ReloadCheck.NEVER_FILLED` | `Set.of(Good.BUILDING_WORK)` | The seller-priced goods no sector sells through its market (FB3, FW6-C): building work is the crews' own points, so its list is empty at every checkpoint and no fixture can ask that it sold. |
| 110 | `ReloadCheck.SKIP_MONTHS` | `2` | A time skip of section 3f: two months, so it is a skip (the game autosaves before one), and two are run on each city. |
| 113 | `ReloadCheck.CENT` | `0.005` | Two numbers are the same when they differ by no more than a cent... |
| 116 | `ReloadCheck.RELATIVE` | `1e-9` | ...and by no more than a billionth of the larger (or of 1, for the tiny). |
| 124 | `ReloadCheck.EXEMPT` | `Set.of("slotName", "savedAt")` | The keys no two saves of one city share, whatever it did: the slot's name and the time it was written. |
| 127 | `ReloadCheck.SHOWN` | `12` | How many differing paths a failing line lists, each with both values; the rest is counted. |
| 129 | `ReloadCheck.QUIET` | `new PrintStream(OutputStream.nullOutputStream())` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 83 | `static int fails` |  |
| 139 | `int differing` |  |
| 140 | `final List<String> first` |  |
| 141 | `final Map<String, Integer> kinds` |  |
| 164 | `int numbers, series` |  |
| 169 | `int compared` |  |
| 170 | `final Map<String, Integer> saw` |  |
| 187 | `final String city` |  |
| 188 | `final int month` |  |
| 190 | `String problem` | Why the checkpoint could not be compared (a save or a load that failed), or null. |
| 191 | `Compared history1, history2, save1, save2` |  |
| 192 | `final Cause revalued` |  |
| 195 | `int skips, skipsWithOutbreak, skipsSick` |  |
| 197 | `HandOrder order` | The hand order the harness made on the live city before this point (FB3, FW6-C), or null: a point with one is the caused point. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 81 | 641 | **type** `public class ReloadCheck` | Plays a city, saves it, loads the save into a second game, runs both a month and compares what each would save: a reloaded city has to play its next month as the city that kept running. |

### the findings (lines 131-204)

| line | len | member | says |
|---:|---:|---|---|
| 138 | 23 | **type** `static class Findings` | What a comparison found: how many things differ, in how many kinds of place, and the first SHOWN of them, each with both values. |
| 143 | 6 | `void differ(String what, String live, String reloaded)` _(in ReloadCheck.Findings)_ |  |
| 151 | 9 | `String kindsLine()` _(in ReloadCheck.Findings)_ | The kinds, the first few with how many of each: what a failing line says before the list. |
| 163 | 3 | **type** `static final class Compared extends Findings` | Two saves' files compared leaf by leaf: how many numbers and how many series (arrays of numbers) were compared. |
| 168 | 16 | **type** `static final class Cause extends Findings` | One cause (section 3): what was compared, and what the live city showed the cause at work in (the fixture). |
| 172 | 1 | `void saw(String what, int n)` _(in ReloadCheck.Cause)_ |  |
| 174 | 4 | `void compare(String what, double live, double reloaded)` _(in ReloadCheck.Cause)_ |  |
| 179 | 4 | `void compare(String what, String live, String reloaded)` _(in ReloadCheck.Cause)_ |  |
| 186 | 15 | **type** `static final class Point` | What one checkpoint found: the city saved at {@code month}, reloaded, and both played on. |
| 199 | 1 | `Point(String city, int month)` _(in ReloadCheck.Point)_ |  |
| 203 | 1 | **type** `record HandOrder(Good good, List<Trade> rows)` | A hand order made between the presses (FB3, FW6-C): the good and the draws it logged, one a seller, with the units taken from its stock. |

### the numbers (lines 205-215)

| line | len | member | says |
|---:|---:|---|---|
| 207 | 6 | `static boolean same(double a, double b)` |  |
| 214 | 1 | `static String num(double v)` |  |

### the two files (lines 216-273)

| line | len | member | says |
|---:|---:|---|---|
| 218 | 7 | `static JsonElement read(Path file)` |  |
| 227 | 6 | `static boolean isNumeric(JsonElement e)` | A number, or the strings Gson writes for the ones JSON has no word for. |
| 234 | 1 | `static double valueOf(JsonElement e)` |  |
| 236 | 5 | `static String show(JsonElement e)` |  |
| 243 | 7 | `static Compared compare(Path live, Path reloaded)` | The live city's file against the reloaded city's, key by key and leaf by leaf; null (a file not there) is itself a difference. |
| 251 | 22 | `static void walk(String path, JsonElement a, JsonElement b, Compared c)` |  |

### the caused point (lines 274-300)

| line | len | member | says |
|---:|---:|---|---|
| 283 | 17 | `static HandOrder causeTheConditions(Game g, boolean placeACarry)` | FB3 (FW6-C): three restores that no checkpoint's save causes the case of, caused on the live city as it stands (between the presses, a month just closed): a sudden stop begun (the words are a String no array carries);... |

### one checkpoint (lines 301-528)

| line | len | member | says |
|---:|---:|---|---|
| 309 | 1 | `static Point reload(GameFiles files, Game g, String city)` | The city {@code g} saved where it stands and loaded into a second game, observed right after the load (the markets' tally, the shares, the price index's weights), then both run a month (the sector books, Construction,... |
| 312 | 31 | `static Point reload(GameFiles files, Game g, String city, HandOrder order)` | ...with the hand order the harness made on the live city before it, if any (FB3, FW6-C), so a month on can read the seller rows it put back. |
| 345 | 6 | `private static Compared[] saveAndCompare(GameFiles files, Game g, Game back, String when)` | Both cities saved (the live one to slot 2, the reloaded to slot 3) and their files compared: {the history's, the save's}. |
| 360 | 40 | `private static void observeTheLoad(Point p, Game g, Game back)` | Right after the load, before any month: (c) every market's tally of the month just closed - the goods imported, exported, filled at home, offered and bid for, and the units drawn since (FB3, FW6-C); the trades list ha... |
| 402 | 12 | `private static void compareDraws(Cause c, String good, List<Trade> live, List<Trade> re)` | (i) One market's draws since the month closed, the live city's against the reloaded city's: how many, and each one's seller, buyer, units and price. |
| 415 | 14 | `private static void compareShares(Cause c, String what, SectorBooks.Shares live, SectorBooks.Shares re)` |  |
| 434 | 36 | `private static void observeAMonthOn(Point p, Game g, Game back)` | A month on: (a) each sector book's stock revaluation and whether it was counted, (b) Construction's materials drawn at home and imported in the month last struck, and (c) the investment notes the investors left. |
| 471 | 4 | `private static double soldAtHome(Sector s, Good good)` |  |
| 480 | 11 | `private static Map<Good, Set<Trade>> sellerPricedTrades(Point p, Game g)` | (j) Every seller-priced market's trades as the live city has them a month on (FB3, FW6-C), kept by object: Trade is a record, so a set that asked equals() would call this month's sale the last one's. |
| 493 | 8 | `private static void tradesOfTheFirstMonthGone(Point p, Game g, Map<Good, Set<Trade>> firstMonth)` | ...and a month later none of those trades is on its market's list: a seller-priced good's month begins as a traded good's does (Markets.startSellerPricedMonth()). |
| 508 | 18 | `private static void skips(Point p, Game g, Game back)` | (f) Two skips run on each city from the same month, and the second report's health figures compared. |
| 527 | 1 | `private static int outbreakMonths(TimeSkipReport report)` |  |

### the report (lines 529-653)

| line | len | member | says |
|---:|---:|---|---|
| 532 | 5 | `static void check(String label, boolean ok, String detail, List<String> more)` | One labelled assertion, printed either way; a failing one lists, under its line, what differs. |
| 538 | 1 | `static void check(String label, boolean ok, String detail)` |  |
| 541 | 6 | `static void nothingDiffers(String label, Findings f, String what)` | An assertion that nothing differs: the first difference on its line, the others (to SHOWN) under it, the rest counted. |
| 548 | 5 | `static String seen(Map<String, Integer> saw)` |  |
| 554 | 5 | `static Map<String, Integer> sawTogether(List<Point> points, Function<Point, Cause> cause)` |  |
| 560 | 3 | `static boolean allAbove0(Map<String, Integer> saw)` |  |
| 564 | 1 | `static String when(Point p, int monthsOn)` |  |
| 567 | 16 | `static void history(List<Point> points)` | Sections 1 and 4's history lines: the fixture line first, then each checkpoint a month on and two months on. |
| 585 | 12 | `static void whole(List<Point> points)` | Sections 2 and 4's whole-save lines. |
| 598 | 30 | `static void section3(List<Point> points)` |  |
| 630 | 12 | `static void causedCause(List<Point> points, Function<Point, Cause> which, String id, String when, String label)` | A cause whose fixture the caused point supplies (FB3, FW6-C): the line says what the checkpoints alone showed. |
| 644 | 9 | `static void cause(List<Point> points, Function<Point, Cause> which, String id, String when, String label)` | One cause: the fixture line (the live city showed it at work, in every way named), then its parity at each checkpoint. |

### the Earth (lines 654-675)

| line | len | member | says |
|---:|---:|---|---|
| 657 | 18 | `static boolean earthHere()` | Whether section 4 can run: earth-2.bin named (-Dcbs.earth, or at data/earth-2.bin) and the game reading it; says so in one line where not. |

### the run (lines 676-721)

| line | len | member | says |
|---:|---:|---|---|
| 678 | 43 | `public static void main(String[] args)` |  |

