# PortCheck.java - 2,347 lines · 72 methods · 17 constants · harnesses

`ham/citybuildersim/PortCheck.java` - generated 2026-11-14 by CodeMap; line numbers are as of that run.

> The city's ports and their boats (0.7.86, batch O9; docs/studies/spec-oil.md
> 2.9, 2.10 and 4's PortCheck; runs/research-freight.md 5).
> 
> WHAT THIS HAS TO PROVE:
> 
>   1. THE FREIGHT RULE: each kind's SEA_FREIGHT_SHARE is the research's rule
>      - F + c x d, sea against a lorry door to door, a 50 km lorry leg at
>      each end - at 5,000 km, from its own rows, to its two places; and every
>      kind's is under the railway's floor, so the berths go before it.
> 
>   2. THE CARGO, THE SHIPS AND THE TERMINALS: Good.cargo() as spec-oil 2.1
>      writes it; the ships' cargoes [P30][P40][P42][P46]; a tanker the
>      largest the free room takes, none under an MR; the four terminals'
>      berths, the city's, on the coast. And (FR3, docs/queue/FR3.md 2) the
>      Bulk Berth, id 142, and the Handysize and Supramax it takes: the
>      month's dry bulk calls in the classes of the largest dry bulk berth
>      standing, the Bulk Terminal's Panamax and Capesize as before. And
>      (SL1, 2026-10-28, section 2c) the four terminals as large modern real
>      ones, of several berths, their cost lines moved by their tonnes'
>      ratio; Ports.payingCount(), the terminals the uncovered tonnes pay
>      back, each counted after the ones before it, at its edges and the
>      dial, agreeing with berthPaysBack(); and main's 35 General Cargo
>      Terminals' trade (m3996) carried by the catalogue's 8.
> 
>   3. THE FACTOR equals its formula exactly, and with nothing at sea it is
>      the railway's own 1 - rail, the railway's share its own, to the bit.
> 
>   4. THE SHARES: a kind's berths are shared among its goods by their
>      tonnes; a kind under the quote goes before the railway and takes its
>      tonnes off what the railway is offered; one not under it takes only
>      what the railway leaves; crude with no tank room is held back, and
>      with room goes in the largest class it takes.
> 
>   5. NO PORT GIVES A BIT-IDENTICAL BAND: a town with a railway and no
>      terminal quotes 0.7.85's band, 1 - carried and carried x quote, every
>      good, to the bit, and nothing goes by sea.
> 
>   6. A PORT ON A TOWN: refused NO_COAST without owned sea, allowed with it;
>      its kind's goods then go by sea in its share, each good's band its
>      formula, narrower than the lorries'; the road told the ships' share;
>      its crews paid by Freight (FR3) and its upkeep with transit's; the calls tonnes over the ship's cargo;
>      the audit closing.
> 
>   7. CRUDE NEEDS ROOM: a refinery's crude with a tanker berth and no Tank
>      Farm is held back for room, and its page says so; with a farm it goes
>      by sea in the largest class the free room takes. AND THE NEXT CARGO'S
>      ROOM (0.7.105, Jerus's R2): a Crude Unit's month beside one farm left
>      14,185 t free, under an MR's cargo, so no tanker ever landed; the
>      refiners now keep an MR's cargo free, and its crude goes by sea.
> 
>   8. THE BOATS: whole calls, exact on average; inside their month; pure in
>      (call, t), loaded the right way each leg; a lane straight out away
>      from the site; and a frame touches only the routes on screen, and
>      finds every boat on them.
> 
>   9. THE SAVE: the shares in force and the month cross a save, the band and
>      the road with them, and a month on the city and its reload agree to
>      the bit; a save from before them loads with nothing at sea.
> 
>  10. THE HARBOUR'S QUAY (HB1, 2026-11-08; Jerus: "and that ships dock
> ... (38 more lines in the source)

**Uses:** [Ports](Ports.md) (371), [BoatSchedule](BoatSchedule.md) (196), [HarbourYard](HarbourYard.md) (142), [CityShore](CityShore.md) (109), [Good](Good.md) (100), [Game](Game.md) (35), [MapFrame](MapFrame.md) (34), [BuildingsTemplate](BuildingsTemplate.md) (33), [Traffic](Traffic.md) (32), [ShipShapes](ShipShapes.md) (19), [BuildingManager](BuildingManager.md) (18), [World](World.md) (13), [MapCheck](MapCheck.md) (11), [PriceLevel](PriceLevel.md) (10), [BuildingVisual](BuildingVisual.md) (9), [Founding](Founding.md) (8), [JobType](JobType.md) (8), [Refining](Refining.md) (7), [SpreadPlanner](SpreadPlanner.md) (7), [TileRaster](TileRaster.md) (7), [GameFiles](GameFiles.md) (5), [TaxPolicy](TaxPolicy.md) (5), [GoodsMarket](GoodsMarket.md) (5), [Timing](Timing.md) (4), [Freight](Freight.md) (4), [BuildingCatalog](BuildingCatalog.md) (4), [BuildingType](BuildingType.md) (4), [InfrastructureManager](InfrastructureManager.md) (4), [BuildCard](BuildCard.md) (4), [HeavyIndustry](HeavyIndustry.md) (3)... and 9 more

**Used by (5):** [BuildAdviceCheck](BuildAdviceCheck.md), [BuildCardCheck](BuildCardCheck.md), [LandlordPortCheck](LandlordPortCheck.md), [QuayCraneCheck](QuayCraneCheck.md), [QuayRailCheck](QuayRailCheck.md)

## Sections

| line | section |
|---:|---|
| 163 | · the research's rule (runs/research-freight.md 5), its rows as the research writes them |
| 271 | 1. THE RULE |
| 295 | 2. THE CARGO, THE SHIPS AND THE TERMINALS |
| 407 | 2b. THE BULK BERTH (FR3) |
| 478 | 2c. A TERMINAL OF SEVERAL BERTHS, ORDERED BY THE COUNT (SL1) |
| 650 | 3. THE FACTOR |
| 684 | 4. THE SHARES |
| 759 | 5. NO PORT, THE SAME BAND |
| 807 | 6. A PORT ON A TOWN |
| 932 | 7. CRUDE NEEDS ROOM |
| 1049 | 8. THE BOATS |
| 1183 | 9. THE SAVE |
| 1238 | 10. THE HARBOUR'S QUAY |
| 1455 | · 10b. THE BOATS ALONGSIDE OVERLAP NOTHING (HB1 slice E, 2026-11-08) |
| 1899 | · 11. THE YARD'S PICTURE (HB2, 2026-11-09) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 161 | `PortCheck.FILES` | `new java.util.IdentityHashMap<>()` | Each fixture town's save folder. |
| 166 | `PortCheck.KM` | `5000` | The distance the shares are struck at, km: the research's headline. |
| 169 | `PortCheck.LORRY_F` | `2.0, LORRY_C =.080, ACCESS_KM = 50` | A lorry: F US$2.0 a tonne, c US$0.080 a tonne-km; and the leg to a quay at each end, 50 km. |
| 172 | `PortCheck.LIQUID_ROWS` | `{ { 4 + 1,.0012 }, { 4 + 2,.0022 }, { 4 + 5,.0035 }, { 4 + 6,.0035 } }` | The sea rows, {F, c}: liquid VLCC, Suezmax, Aframax-LR, MR... |
| 174 | `PortCheck.DRY_ROWS` | `{ { 6 + 1.2,.00075 }, { 6 + 5,.0022 } }` | ...dry Capesize, Panamax... |
| 176 | `PortCheck.BOX_ROWS` | `{ { 24,.0055 } }` | ...deep-sea boxes... |
| 178 | `PortCheck.GENERAL_ROWS` | `{ { 40,.0060 } }` | ...and general cargo. |
| 481 | `PortCheck.MAIN_GENERAL_BERTH_TONNES_A_YEAR` | `450_000` | Tonnes a year of a General Cargo Terminal on main at 0.8.66, before SL1: the berth the default playtest stood full 35 of at m3996 (544,000 people). |
| 483 | `PortCheck.MAIN_GENERAL_BERTHS_AT_M3996` | `35` | ...and how many it stood, all full, at m3996. |
| 489 | `PortCheck.SPLIT_ROUNDING` | `2` | How far a terminal's cash, points or materials may sit from its ratio's rounding: 2. |
| 1241 | `PortCheck.BOX_W` | `6, BOX_H = 5` | The fixture terminal's box, in plots: 6 across and 5 deep (the fixture's own, not the model's). |
| 1458 | `PortCheck.BULK_ALONG` | `5, BULK_DEEP = 4` | A Bulk Berth's box of the fixture, in plots: 5 along the shore and 4 deep (the fixture's own; the berth's land is 215,000 m2). |
| 1461 | `PortCheck.BERTH_SCALES` | `{ 4, 8 }` | The scales the berths are laid at, px a plot: 4, just past DETAIL_FROM (where boats were drawn 1.6 times true before slice E), and 8 (the renders' other). |
| 1902 | `PortCheck.YARD_BOXES` | `{ { BULK_ALONG, BULK_DEEP }, { 16, 15 } }` | The fixture's boxes, {along the shore, deep} in plots: a Bulk Berth's (BULK_ALONG by BULK_DEEP) and a large terminal's, deep enough for a loop. |
| 1905 | `PortCheck.YARD_BOX_NAMES` | `{ "small", "large" }` | What a box of YARD_BOXES is called in a line. |
| 1908 | `PortCheck.PORT_KINDS` | `{ HarbourYard.Kind.CONTAINER, HarbourYard.Kind.DRY_BULK, HarbourYard.Kind.LIQ...` | The kinds of a terminal, whose yards fill with stock. |
| 1911 | `PortCheck.RAIL_OPEN` | `HarbourYard.RAIL \| HarbourYard.OPEN_LOW \| HarbourYard.OPEN_HIGH` | The rail bits of a picture whose quay track runs on past both ends of its box. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 115 | `static int fails` |  |
| 116 | `static PrintStream out` |  |
| 117 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 113 | 2235 | **type** `public class PortCheck` | The city's ports and their boats (0.7.86, batch O9; docs/studies/spec-oil.md 2.9, 2.10 and 4's PortCheck; runs/research-freight.md 5). |
| 119 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 124 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 130 | 4 | `static void timed(String label, boolean ok, String detail)` | A timing assertion's line (HB1 slice E, 10b's cost), as report() prints one, its verdict Timing's: soft under -Dcbs.timing=soft, hard at the gate. |
| 135 | 5 | `static void quietly(Runnable r)` |  |
| 141 | 6 | `static<T> T quietlyGet(java.util.function.Supplier<T> s)` |  |
| 148 | 5 | `static BuildingsTemplate template(Game g, String name)` |  |
| 155 | 4 | `static double auditMiss(Game g)` | The month's audit residual as a share of the house's money tolerance at what moved (MoneyAudit.tolerance()): at most 1 closes. |

### the research's rule (runs/research-freight.md 5), its rows as the research writes them (lines 163-270)

| line | len | member | says |
|---:|---:|---|---|
| 181 | 5 | `static double ruleShare(double[][] rows)` | Sea against a lorry, door to door, at KM: each row's, averaged over the kind's rows. |
| 192 | 19 | `static Game town(String label, int houses, double spare)` | A town that pays its people: houses, shops, bakeries to work in, power, water, roads and builders, standing, on ground a quarter more than they take and `spare` more (OilCheck's town). |
| 213 | 29 | `static boolean buyCoast(Game g)` | Buys toward the sea nearest the site, the offer nearest it each time, until one standing runs out to it, and buys that (WaterCheck's way). |
| 243 | 27 | `public static void main(String[] args) throws Exception` |  |

### 1. THE RULE (lines 271-294)

| line | len | member | says |
|---:|---:|---|---|
| 273 | 21 | `static void theRule()` |  |

### 2. THE CARGO, THE SHIPS AND THE TERMINALS (lines 295-406)

| line | len | member | says |
|---:|---:|---|---|
| 297 | 109 | `static void cargoShipsAndTerminals()` |  |

### 2b. THE BULK BERTH (FR3) (lines 407-477)

| line | len | member | says |
|---:|---:|---|---|
| 409 | 68 | `static void theBulkBerth()` |  |

### 2c. A TERMINAL OF SEVERAL BERTHS, ORDERED BY THE COUNT (SL1) (lines 478-649)

| line | len | member | says |
|---:|---:|---|---|
| 491 | 158 | `static void terminalsOfSeveralBerths()` |  |

### 3. THE FACTOR (lines 650-683)

| line | len | member | says |
|---:|---:|---|---|
| 652 | 31 | `static void theFactor()` |  |

### 4. THE SHARES (lines 684-758)

| line | len | member | says |
|---:|---:|---|---|
| 686 | 5 | `static double[] tonnes(Object...pairs)` |  |
| 692 | 66 | `static void theShares()` |  |

### 5. NO PORT, THE SAME BAND (lines 759-806)

| line | len | member | says |
|---:|---:|---|---|
| 761 | 45 | `static void noPortTheSameBand()` |  |

### 6. A PORT ON A TOWN (lines 807-931)

| line | len | member | says |
|---:|---:|---|---|
| 809 | 122 | `static Game aPortOnATown()` |  |

### 7. CRUDE NEEDS ROOM (lines 932-1048)

| line | len | member | says |
|---:|---:|---|---|
| 934 | 114 | `static void crudeNeedsRoom()` |  |

### 8. THE BOATS (lines 1049-1182)

| line | len | member | says |
|---:|---:|---|---|
| 1051 | 113 | `static void theBoats()` |  |
| 1166 | 16 | `static Ports monthAtSea()` | A month's trade by sea on every kind: Ports handed a month with each kind's berths taking its share. |

### 9. THE SAVE (lines 1183-1237)

| line | len | member | says |
|---:|---:|---|---|
| 1185 | 52 | `static void acrossASave(Game g) throws Exception` |  |

### 10. THE HARBOUR'S QUAY (lines 1238-1454)

| line | len | member | says |
|---:|---:|---|---|
| 1244 | 3 | `static CityShore.Work harbourWork(long x0, long y0, int side)` | A harbour terminal of the fixture: its box at (x0, y0), the sea on `side`, the harbour's quay. |
| 1249 | 8 | `static CityShore pairOnShore(int side, int gap)` | Two harbour terminals facing `side` at (2000, 2000), the second `gap` plots past the first along the shore, on the same line. |
| 1259 | 9 | `static CityShore.Wall pairsWall(int side, int gap)` | The wall the pair should make, worked from the geometry alone: the strip before both sea sides and the gap between, HARBOUR_QUAY_PLOTS deep. |
| 1270 | 4 | `static boolean walled(List<CityShore.Wall> walls, long x, long y)` | Whether some wall of `walls` holds world plot (x, y). |
| 1276 | 4 | `static boolean walledAll(List<CityShore.Wall> walls, long x0, long y0, long w, long h, boolean all)` | Whether every plot of the rectangle (x0, y0, w, h) is in a wall (all = true), or none of them is (all = false). |
| 1282 | 6 | `static boolean wallInABox(CityShore shore)` | Whether any plot of any wall of `shore` is in a work's box. |
| 1289 | 165 | `static void theHarbourQuay()` |  |

### 10b. THE BOATS ALONGSIDE OVERLAP NOTHING (HB1 slice E, 2026-11-08) (lines 1455-1898)

| line | len | member | says |
|---:|---:|---|---|
| 1464 | 4 | `static CityShore.Work bulkWork(long x0, long y0, int side)` | A Bulk Berth of the fixture with its box at (x0, y0), the sea on `side`, the harbour's quay. |
| 1470 | 6 | `static CityShore berthRow(int count, int side, int pitch)` | `count` Bulk Berths facing `side`, `pitch` plots from one box to the next along the shore, from (3000, 3000). |
| 1478 | 11 | `static List<BoatSchedule.Route> routesOf(CityShore shore, Ports.Cargo...cargos)` | The routes of a shore's berthed works in the order laid, one each, the berth's cargo cycling through `cargos`: a straight lane 60 plots out to sea from the work's berth point, the berth carrying the work's side. |
| 1491 | 3 | `static BoatSchedule.Boat dockedOn(List<BoatSchedule.Route> routes, int r, Ports.Ship ship, double arrives)` | A call of `ship` on route r arriving at `arrives`, and its boat at the quay the instant it does, as the schedule places it. |
| 1496 | 3 | `static double[][] box(double x, double y, double w, double h)` | A rectangle's outline {xs, ys}. |
| 1504 | 37 | `static double thickness(double[][] a, double[][] b)` | How thick the part two convex outlines share is: the narrowest width, across the edges of the part itself, of what the second clips of the first (Sutherland-Hodgman) - 0 when they share nothing or only a line. |
| 1547 | 18 | `static int[] audit(CityShore shore, List<BoatSchedule.Boat> given, List<BoatSchedule.Boat> drawn, double scale)` | What a layout of docked boats comes to, counted from the outlines (thickness()) and not the model's test: {how many lie in a place of their own, alongside or stern-to (are not the boat given), how many pairs of drawn ... |
| 1567 | 5 | `static int[] same(int side, int n)` | `n` copies of a side, for kinds(): a row of boats facing one way. |
| 1574 | 11 | `static int[] kinds(List<BoatSchedule.Boat> given, List<BoatSchedule.Boat> drawn, int[] sides, double scale)` | How a layout's docked boats lie, by comparing each with the places BoatSchedule gives it on its side (sides[i], the side of the berth of boat i): {alongside, stern-to, as placed}. |
| 1587 | 3 | `static void add(int[] to, int[] k)` | The sum of a kinds() count into another. |
| 1591 | 229 | `static void berthsShareTheQuay()` |  |
| 1827 | 71 | `static void windowFixture()` | A boat's place does not depend on the window: BoatSchedule.drawn() weighs the boats of a frame against every boat docked at that moment (docked()), on a bay of berths in a row, liquid and dry bulk by turns (MR tankers... |

### 11. THE YARD'S PICTURE (HB2, 2026-11-09) (lines 1899-2347)

| line | len | member | says |
|---:|---:|---|---|
| 1914 | 5 | `static CityShore.Work yardWork(long x0, long y0, int box, int side, int type)` | A work of the fixture: box `box` of YARD_BOXES at (x0, y0), of type `type`, the harbour's quay, the sea on `side`. |
| 1924 | 24 | **type** `record YardGrid(int side, int w, int h)` | A picture's geometry for a sea side: its pixels `w` by `h`, read in the shore's own axes - `a` along the shore (x for a sea side of north or south, else y, so a small `a` is the LOW end) and `c` across it (the other). |
| 1925 | 1 | `static YardGrid of(CityShore.Work k)` _(in PortCheck.YardGrid)_ |  |
| 1927 | 1 | `int along()` _(in PortCheck.YardGrid)_ |  |
| 1929 | 1 | `int across()` _(in PortCheck.YardGrid)_ |  |
| 1931 | 1 | `int at(int[] pic, int a, int c)` _(in PortCheck.YardGrid)_ |  |
| 1934 | 1 | `int seaFrom(int rows)` _(in PortCheck.YardGrid)_ | The first c of the `rows` sea-most plot rows. |
| 1937 | 1 | `int land()` _(in PortCheck.YardGrid)_ | The c of the land edge line. |
| 1940 | 1 | `int cOfV(int v)` _(in PortCheck.YardGrid)_ | The c of the pixel row v out from the land side. |
| 1943 | 1 | `int aOf(int i)` _(in PortCheck.YardGrid)_ | The a of pixel i of a picture. |
| 1946 | 1 | `int cOf(int i)` _(in PortCheck.YardGrid)_ | The c of pixel i of a picture. |
| 1950 | 1 | `static boolean onTrack(int argb)` | Whether a pixel is a track's: its ballast, a sleeper or a steel rail. |
| 1953 | 8 | `static List<Integer> yardLine(YardGrid g, int[] pic, boolean acrossLine, int fixed, int from, int to, boolean onlySteel)` | The offsets from `from` of the pixels of a line of a picture that are steel (`onlySteel`) or any track's: the line across at c = `fixed` (`acrossLine`, p running along the shore), else along at a = `fixed`. |
| 1963 | 4 | `static boolean yardSame(YardGrid g, int[] x, int[] y, int a0, int a1, int c0, int c1)` | Whether two pictures agree on every pixel of the part a in [a0, a1), c in [c0, c1). |
| 1969 | 5 | `static boolean[] yardDiff(int[] a, int[] b)` | Which pixels of two pictures differ. |
| 1976 | 7 | `static long yardFar(int[] a, int[] base)` | How far a picture is from a base, in all: the channels' differences summed. |
| 1985 | 1 | `static String yardWhy(String was, boolean ok, String what)` | The first case of a loop that fails, kept: `was` if there is one, else `what` when `ok` is not. |
| 1988 | 1 | `static String yardCase(int box, int side, Object kind)` | A case's words: the box, the sea side, and the kind. |
| 1990 | 13 | `static void theYardsPicture()` |  |
| 2005 | 27 | `static void yardShape()` | The picture is the box's pixels, opaque, the same each call, and the work's own: another place or another type paints another. |
| 2034 | 44 | `static void yardFills()` | The yard fills: each step's stock holds the step before's, bare and with the tracks and a train; a tank farm shows no level and no rail; below nothing is bare, above LEVELS is full. |
| 2080 | 20 | `static void yardSteps()` | step() shows any cargo at all and never falls as busy rises. |
| 2102 | 53 | `static void yardQuayTrack()` | The quay track runs to a box's edge where it is open and stops short at a buffer where it is not; OPEN_x paints only in its end's plot; no RAIL, no track; no stock on it. |
| 2157 | 32 | `static void yardLead()` | The lead comes up the first or last plot column from the land edge to the quay track, and no stock stands in its column. |
| 2191 | 29 | `static void yardTrain()` | A train changes pixels only on the track's rows, and only with RAIL. |
| 2222 | 47 | `static void yardCells()` | gapCell() and leadCell() are the track and the lead at the offsets across the plot that the picture has them. |
| 2271 | 26 | `static void yardKinds()` | kindOf() reads a type by its berth's cargo, a tank farm by its shore, and nothing else. |
| 2299 | 4 | `static Good yardGood(Ports.Cargo c)` | The first good of a kind that is not crude, as MapCheck.tradeAtSea() takes it. |
| 2305 | 25 | `static void yardBusy()` | Ports.busy() is the month's sea tonnes both ways over the berths', 1 when over, 0 with no berth. |
| 2332 | 15 | `static void yardCost()` | Painting the large box's picture (a container yard, full, its track and train) costs no more than HarbourYard.PICTURE_MS. |

