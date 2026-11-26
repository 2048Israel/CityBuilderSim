# PortCheck.java - 2,416 lines · 73 methods · 17 constants · harnesses

`ham/citybuildersim/PortCheck.java` - generated 2026-11-25 by CodeMap; line numbers are as of that run.

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
>      good, to the bit, and nothing goes by sea. (FS7: the fixture is the
>      foundries and a spur stood, the railway's carried shares set on its
>      state and the month's tonnes handed to Ports - handMonth - not a played
>      town found with a railway.)
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
>      (FS7: the fixture is a refinery, a tanker berth, a Tank Farm and a
>      Crude Unit stood, the refiners' crude import set, the month's crude
>      handed to Ports with the refiners' room - handMonth - and the store
>      set a tonne over and at what they keep, an MR's cargo's two sides.)
> 
>   8. THE BOATS: whole calls, exact on average; inside their month; pure in
>      (call, t), loaded the right way each leg; a lane straight out away
>      from the site; and a frame touches only the routes on screen, and
> ... (45 more lines in the source)

**Uses:** [Ports](Ports.md) (387), [BoatSchedule](BoatSchedule.md) (196), [HarbourYard](HarbourYard.md) (142), [Good](Good.md) (109), [CityShore](CityShore.md) (109), [Traffic](Traffic.md) (39), [Game](Game.md) (36), [MapFrame](MapFrame.md) (34), [BuildingsTemplate](BuildingsTemplate.md) (33), [ShipShapes](ShipShapes.md) (19), [BuildingManager](BuildingManager.md) (18), [World](World.md) (13), [MapCheck](MapCheck.md) (11), [PriceLevel](PriceLevel.md) (10), [BuildingVisual](BuildingVisual.md) (9), [Founding](Founding.md) (8), [JobType](JobType.md) (8), [Refining](Refining.md) (7), [SpreadPlanner](SpreadPlanner.md) (7), [TileRaster](TileRaster.md) (7), [GameFiles](GameFiles.md) (5), [Freight](Freight.md) (5), [TaxPolicy](TaxPolicy.md) (5), [GoodsMarket](GoodsMarket.md) (5), [Timing](Timing.md) (4), [BuildingCatalog](BuildingCatalog.md) (4), [BuildingType](BuildingType.md) (4), [InfrastructureManager](InfrastructureManager.md) (4), [BuildCard](BuildCard.md) (4), [HeavyIndustry](HeavyIndustry.md) (3)... and 10 more

**Used by (6):** [BuildAdviceCheck](BuildAdviceCheck.md), [BuildCardCheck](BuildCardCheck.md), [LandlordPortCheck](LandlordPortCheck.md), [QuayCraneCheck](QuayCraneCheck.md), [QuayRailCheck](QuayRailCheck.md), [TradeBandCheck](TradeBandCheck.md)

## Sections

| line | section |
|---:|---|
| 170 | · the research's rule (runs/research-freight.md 5), its rows as the research writes them |
| 278 | 1. THE RULE |
| 302 | 2. THE CARGO, THE SHIPS AND THE TERMINALS |
| 414 | 2b. THE BULK BERTH (FR3) |
| 485 | 2c. A TERMINAL OF SEVERAL BERTHS, ORDERED BY THE COUNT (SL1) |
| 657 | 3. THE FACTOR |
| 691 | 4. THE SHARES |
| 766 | 5. NO PORT, THE SAME BAND |
| 852 | 6. A PORT ON A TOWN |
| 977 | 7. CRUDE NEEDS ROOM |
| 1118 | 8. THE BOATS |
| 1252 | 9. THE SAVE |
| 1307 | 10. THE HARBOUR'S QUAY |
| 1524 | · 10b. THE BOATS ALONGSIDE OVERLAP NOTHING (HB1 slice E, 2026-11-08) |
| 1968 | · 11. THE YARD'S PICTURE (HB2, 2026-11-09) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 168 | `PortCheck.FILES` | `new java.util.IdentityHashMap<>()` | Each fixture town's save folder. |
| 173 | `PortCheck.KM` | `5000` | The distance the shares are struck at, km: the research's headline. |
| 176 | `PortCheck.LORRY_F` | `2.0, LORRY_C =.080, ACCESS_KM = 50` | A lorry: F US$2.0 a tonne, c US$0.080 a tonne-km; and the leg to a quay at each end, 50 km. |
| 179 | `PortCheck.LIQUID_ROWS` | `{ { 4 + 1,.0012 }, { 4 + 2,.0022 }, { 4 + 5,.0035 }, { 4 + 6,.0035 } }` | The sea rows, {F, c}: liquid VLCC, Suezmax, Aframax-LR, MR... |
| 181 | `PortCheck.DRY_ROWS` | `{ { 6 + 1.2,.00075 }, { 6 + 5,.0022 } }` | ...dry Capesize, Panamax... |
| 183 | `PortCheck.BOX_ROWS` | `{ { 24,.0055 } }` | ...deep-sea boxes... |
| 185 | `PortCheck.GENERAL_ROWS` | `{ { 40,.0060 } }` | ...and general cargo. |
| 488 | `PortCheck.MAIN_GENERAL_BERTH_TONNES_A_YEAR` | `450_000` | Tonnes a year of a General Cargo Terminal on main at 0.8.66, before SL1: the berth the default playtest stood full 35 of at m3996 (544,000 people). |
| 490 | `PortCheck.MAIN_GENERAL_BERTHS_AT_M3996` | `35` | ...and how many it stood, all full, at m3996. |
| 496 | `PortCheck.SPLIT_ROUNDING` | `2` | How far a terminal's cash, points or materials may sit from its ratio's rounding: 2. |
| 1310 | `PortCheck.BOX_W` | `6, BOX_H = 5` | The fixture terminal's box, in plots: 6 across and 5 deep (the fixture's own, not the model's). |
| 1527 | `PortCheck.BULK_ALONG` | `5, BULK_DEEP = 4` | A Bulk Berth's box of the fixture, in plots: 5 along the shore and 4 deep (the fixture's own; the berth's land is 215,000 m2). |
| 1530 | `PortCheck.BERTH_SCALES` | `{ 4, 8 }` | The scales the berths are laid at, px a plot: 4, just past DETAIL_FROM (where boats were drawn 1.6 times true before slice E), and 8 (the renders' other). |
| 1971 | `PortCheck.YARD_BOXES` | `{ { BULK_ALONG, BULK_DEEP }, { 16, 15 } }` | The fixture's boxes, {along the shore, deep} in plots: a Bulk Berth's (BULK_ALONG by BULK_DEEP) and a large terminal's, deep enough for a loop. |
| 1974 | `PortCheck.YARD_BOX_NAMES` | `{ "small", "large" }` | What a box of YARD_BOXES is called in a line. |
| 1977 | `PortCheck.PORT_KINDS` | `{ HarbourYard.Kind.CONTAINER, HarbourYard.Kind.DRY_BULK, HarbourYard.Kind.LIQ...` | The kinds of a terminal, whose yards fill with stock. |
| 1980 | `PortCheck.RAIL_OPEN` | `HarbourYard.RAIL \| HarbourYard.OPEN_LOW \| HarbourYard.OPEN_HIGH` | The rail bits of a picture whose quay track runs on past both ends of its box. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 122 | `static int fails` |  |
| 123 | `static PrintStream out` |  |
| 124 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 120 | 2297 | **type** `public class PortCheck` | The city's ports and their boats (0.7.86, batch O9; docs/studies/spec-oil.md 2.9, 2.10 and 4's PortCheck; runs/research-freight.md 5). |
| 126 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 131 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 137 | 4 | `static void timed(String label, boolean ok, String detail)` | A timing assertion's line (HB1 slice E, 10b's cost), as report() prints one, its verdict Timing's: soft under -Dcbs.timing=soft, hard at the gate. |
| 142 | 5 | `static void quietly(Runnable r)` |  |
| 148 | 6 | `static<T> T quietlyGet(java.util.function.Supplier<T> s)` |  |
| 155 | 5 | `static BuildingsTemplate template(Game g, String name)` |  |
| 162 | 4 | `static double auditMiss(Game g)` | The month's audit residual as a share of the house's money tolerance at what moved (MoneyAudit.tolerance()): at most 1 closes. |

### the research's rule (runs/research-freight.md 5), its rows as the research writes them (lines 170-277)

| line | len | member | says |
|---:|---:|---|---|
| 188 | 5 | `static double ruleShare(double[][] rows)` | Sea against a lorry, door to door, at KM: each row's, averaged over the kind's rows. |
| 199 | 19 | `static Game town(String label, int houses, double spare)` | A town that pays its people: houses, shops, bakeries to work in, power, water, roads and builders, standing, on ground a quarter more than they take and `spare` more (OilCheck's town). |
| 220 | 29 | `static boolean buyCoast(Game g)` | Buys toward the sea nearest the site, the offer nearest it each time, until one standing runs out to it, and buys that (WaterCheck's way). |
| 250 | 27 | `public static void main(String[] args) throws Exception` |  |

### 1. THE RULE (lines 278-301)

| line | len | member | says |
|---:|---:|---|---|
| 280 | 21 | `static void theRule()` |  |

### 2. THE CARGO, THE SHIPS AND THE TERMINALS (lines 302-413)

| line | len | member | says |
|---:|---:|---|---|
| 304 | 109 | `static void cargoShipsAndTerminals()` |  |

### 2b. THE BULK BERTH (FR3) (lines 414-484)

| line | len | member | says |
|---:|---:|---|---|
| 416 | 68 | `static void theBulkBerth()` |  |

### 2c. A TERMINAL OF SEVERAL BERTHS, ORDERED BY THE COUNT (SL1) (lines 485-656)

| line | len | member | says |
|---:|---:|---|---|
| 498 | 158 | `static void terminalsOfSeveralBerths()` |  |

### 3. THE FACTOR (lines 657-690)

| line | len | member | says |
|---:|---:|---|---|
| 659 | 31 | `static void theFactor()` |  |

### 4. THE SHARES (lines 691-765)

| line | len | member | says |
|---:|---:|---|---|
| 693 | 5 | `static double[] tonnes(Object...pairs)` |  |
| 699 | 66 | `static void theShares()` |  |

### 5. NO PORT, THE SAME BAND (lines 766-851)

| line | len | member | says |
|---:|---:|---|---|
| 775 | 23 | `static void handMonth(Game g, double[] in, double[] leaving)` | FS7: one railway month's PORT PART, handed to the city instead of played: Freight.haul()'s steps 1, 3 and 5 as monthAtSea() and the pure sections hand them to a bare Ports - the month's tonnes across the boundary (`in... |
| 799 | 52 | `static void noPortTheSameBand()` |  |

### 6. A PORT ON A TOWN (lines 852-976)

| line | len | member | says |
|---:|---:|---|---|
| 854 | 122 | `static Game aPortOnATown()` |  |

### 7. CRUDE NEEDS ROOM (lines 977-1117)

| line | len | member | says |
|---:|---:|---|---|
| 979 | 138 | `static void crudeNeedsRoom()` |  |

### 8. THE BOATS (lines 1118-1251)

| line | len | member | says |
|---:|---:|---|---|
| 1120 | 113 | `static void theBoats()` |  |
| 1235 | 16 | `static Ports monthAtSea()` | A month's trade by sea on every kind: Ports handed a month with each kind's berths taking its share. |

### 9. THE SAVE (lines 1252-1306)

| line | len | member | says |
|---:|---:|---|---|
| 1254 | 52 | `static void acrossASave(Game g) throws Exception` |  |

### 10. THE HARBOUR'S QUAY (lines 1307-1523)

| line | len | member | says |
|---:|---:|---|---|
| 1313 | 3 | `static CityShore.Work harbourWork(long x0, long y0, int side)` | A harbour terminal of the fixture: its box at (x0, y0), the sea on `side`, the harbour's quay. |
| 1318 | 8 | `static CityShore pairOnShore(int side, int gap)` | Two harbour terminals facing `side` at (2000, 2000), the second `gap` plots past the first along the shore, on the same line. |
| 1328 | 9 | `static CityShore.Wall pairsWall(int side, int gap)` | The wall the pair should make, worked from the geometry alone: the strip before both sea sides and the gap between, HARBOUR_QUAY_PLOTS deep. |
| 1339 | 4 | `static boolean walled(List<CityShore.Wall> walls, long x, long y)` | Whether some wall of `walls` holds world plot (x, y). |
| 1345 | 4 | `static boolean walledAll(List<CityShore.Wall> walls, long x0, long y0, long w, long h, boolean all)` | Whether every plot of the rectangle (x0, y0, w, h) is in a wall (all = true), or none of them is (all = false). |
| 1351 | 6 | `static boolean wallInABox(CityShore shore)` | Whether any plot of any wall of `shore` is in a work's box. |
| 1358 | 165 | `static void theHarbourQuay()` |  |

### 10b. THE BOATS ALONGSIDE OVERLAP NOTHING (HB1 slice E, 2026-11-08) (lines 1524-1967)

| line | len | member | says |
|---:|---:|---|---|
| 1533 | 4 | `static CityShore.Work bulkWork(long x0, long y0, int side)` | A Bulk Berth of the fixture with its box at (x0, y0), the sea on `side`, the harbour's quay. |
| 1539 | 6 | `static CityShore berthRow(int count, int side, int pitch)` | `count` Bulk Berths facing `side`, `pitch` plots from one box to the next along the shore, from (3000, 3000). |
| 1547 | 11 | `static List<BoatSchedule.Route> routesOf(CityShore shore, Ports.Cargo...cargos)` | The routes of a shore's berthed works in the order laid, one each, the berth's cargo cycling through `cargos`: a straight lane 60 plots out to sea from the work's berth point, the berth carrying the work's side. |
| 1560 | 3 | `static BoatSchedule.Boat dockedOn(List<BoatSchedule.Route> routes, int r, Ports.Ship ship, double arrives)` | A call of `ship` on route r arriving at `arrives`, and its boat at the quay the instant it does, as the schedule places it. |
| 1565 | 3 | `static double[][] box(double x, double y, double w, double h)` | A rectangle's outline {xs, ys}. |
| 1573 | 37 | `static double thickness(double[][] a, double[][] b)` | How thick the part two convex outlines share is: the narrowest width, across the edges of the part itself, of what the second clips of the first (Sutherland-Hodgman) - 0 when they share nothing or only a line. |
| 1616 | 18 | `static int[] audit(CityShore shore, List<BoatSchedule.Boat> given, List<BoatSchedule.Boat> drawn, double scale)` | What a layout of docked boats comes to, counted from the outlines (thickness()) and not the model's test: {how many lie in a place of their own, alongside or stern-to (are not the boat given), how many pairs of drawn ... |
| 1636 | 5 | `static int[] same(int side, int n)` | `n` copies of a side, for kinds(): a row of boats facing one way. |
| 1643 | 11 | `static int[] kinds(List<BoatSchedule.Boat> given, List<BoatSchedule.Boat> drawn, int[] sides, double scale)` | How a layout's docked boats lie, by comparing each with the places BoatSchedule gives it on its side (sides[i], the side of the berth of boat i): {alongside, stern-to, as placed}. |
| 1656 | 3 | `static void add(int[] to, int[] k)` | The sum of a kinds() count into another. |
| 1660 | 229 | `static void berthsShareTheQuay()` |  |
| 1896 | 71 | `static void windowFixture()` | A boat's place does not depend on the window: BoatSchedule.drawn() weighs the boats of a frame against every boat docked at that moment (docked()), on a bay of berths in a row, liquid and dry bulk by turns (MR tankers... |

### 11. THE YARD'S PICTURE (HB2, 2026-11-09) (lines 1968-2416)

| line | len | member | says |
|---:|---:|---|---|
| 1983 | 5 | `static CityShore.Work yardWork(long x0, long y0, int box, int side, int type)` | A work of the fixture: box `box` of YARD_BOXES at (x0, y0), of type `type`, the harbour's quay, the sea on `side`. |
| 1993 | 24 | **type** `record YardGrid(int side, int w, int h)` | A picture's geometry for a sea side: its pixels `w` by `h`, read in the shore's own axes - `a` along the shore (x for a sea side of north or south, else y, so a small `a` is the LOW end) and `c` across it (the other). |
| 1994 | 1 | `static YardGrid of(CityShore.Work k)` _(in PortCheck.YardGrid)_ |  |
| 1996 | 1 | `int along()` _(in PortCheck.YardGrid)_ |  |
| 1998 | 1 | `int across()` _(in PortCheck.YardGrid)_ |  |
| 2000 | 1 | `int at(int[] pic, int a, int c)` _(in PortCheck.YardGrid)_ |  |
| 2003 | 1 | `int seaFrom(int rows)` _(in PortCheck.YardGrid)_ | The first c of the `rows` sea-most plot rows. |
| 2006 | 1 | `int land()` _(in PortCheck.YardGrid)_ | The c of the land edge line. |
| 2009 | 1 | `int cOfV(int v)` _(in PortCheck.YardGrid)_ | The c of the pixel row v out from the land side. |
| 2012 | 1 | `int aOf(int i)` _(in PortCheck.YardGrid)_ | The a of pixel i of a picture. |
| 2015 | 1 | `int cOf(int i)` _(in PortCheck.YardGrid)_ | The c of pixel i of a picture. |
| 2019 | 1 | `static boolean onTrack(int argb)` | Whether a pixel is a track's: its ballast, a sleeper or a steel rail. |
| 2022 | 8 | `static List<Integer> yardLine(YardGrid g, int[] pic, boolean acrossLine, int fixed, int from, int to, boolean onlySteel)` | The offsets from `from` of the pixels of a line of a picture that are steel (`onlySteel`) or any track's: the line across at c = `fixed` (`acrossLine`, p running along the shore), else along at a = `fixed`. |
| 2032 | 4 | `static boolean yardSame(YardGrid g, int[] x, int[] y, int a0, int a1, int c0, int c1)` | Whether two pictures agree on every pixel of the part a in [a0, a1), c in [c0, c1). |
| 2038 | 5 | `static boolean[] yardDiff(int[] a, int[] b)` | Which pixels of two pictures differ. |
| 2045 | 7 | `static long yardFar(int[] a, int[] base)` | How far a picture is from a base, in all: the channels' differences summed. |
| 2054 | 1 | `static String yardWhy(String was, boolean ok, String what)` | The first case of a loop that fails, kept: `was` if there is one, else `what` when `ok` is not. |
| 2057 | 1 | `static String yardCase(int box, int side, Object kind)` | A case's words: the box, the sea side, and the kind. |
| 2059 | 13 | `static void theYardsPicture()` |  |
| 2074 | 27 | `static void yardShape()` | The picture is the box's pixels, opaque, the same each call, and the work's own: another place or another type paints another. |
| 2103 | 44 | `static void yardFills()` | The yard fills: each step's stock holds the step before's, bare and with the tracks and a train; a tank farm shows no level and no rail; below nothing is bare, above LEVELS is full. |
| 2149 | 20 | `static void yardSteps()` | step() shows any cargo at all and never falls as busy rises. |
| 2171 | 53 | `static void yardQuayTrack()` | The quay track runs to a box's edge where it is open and stops short at a buffer where it is not; OPEN_x paints only in its end's plot; no RAIL, no track; no stock on it. |
| 2226 | 32 | `static void yardLead()` | The lead comes up the first or last plot column from the land edge to the quay track, and no stock stands in its column. |
| 2260 | 29 | `static void yardTrain()` | A train changes pixels only on the track's rows, and only with RAIL. |
| 2291 | 47 | `static void yardCells()` | gapCell() and leadCell() are the track and the lead at the offsets across the plot that the picture has them. |
| 2340 | 26 | `static void yardKinds()` | kindOf() reads a type by its berth's cargo, a tank farm by its shore, and nothing else. |
| 2368 | 4 | `static Good yardGood(Ports.Cargo c)` | The first good of a kind that is not crude, as MapCheck.tradeAtSea() takes it. |
| 2374 | 25 | `static void yardBusy()` | Ports.busy() is the month's sea tonnes both ways over the berths', 1 when over, 0 with no berth. |
| 2401 | 15 | `static void yardCost()` | Painting the large box's picture (a container yard, full, its track and train) costs no more than HarbourYard.PICTURE_MS. |

