# PortCheck.java - 1,881 lines · 43 methods · 13 constants · harnesses

`ham/citybuildersim/PortCheck.java` - generated 2026-11-09 by CodeMap; line numbers are as of that run.

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
> ... (23 more lines in the source)

**Uses:** [Ports](Ports.md) (350), [BoatSchedule](BoatSchedule.md) (196), [CityShore](CityShore.md) (98), [Good](Good.md) (96), [Game](Game.md) (35), [MapFrame](MapFrame.md) (34), [BuildingsTemplate](BuildingsTemplate.md) (33), [Traffic](Traffic.md) (32), [ShipShapes](ShipShapes.md) (19), [BuildingManager](BuildingManager.md) (18), [World](World.md) (13), [PriceLevel](PriceLevel.md) (10), [Founding](Founding.md) (8), [JobType](JobType.md) (8), [Refining](Refining.md) (7), [SpreadPlanner](SpreadPlanner.md) (7), [BuildingVisual](BuildingVisual.md) (6), [GameFiles](GameFiles.md) (5), [TaxPolicy](TaxPolicy.md) (5), [GoodsMarket](GoodsMarket.md) (5), [MapCheck](MapCheck.md) (5), [Freight](Freight.md) (4), [BuildingType](BuildingType.md) (4), [InfrastructureManager](InfrastructureManager.md) (4), [BuildCard](BuildCard.md) (4), [BuildingCatalog](BuildingCatalog.md) (3), [HeavyIndustry](HeavyIndustry.md) (3), [Timing](Timing.md) (2), [MoneyAudit](MoneyAudit.md) (2), [LandManager](LandManager.md) (2)... and 7 more

**Used by (3):** [BuildAdviceCheck](BuildAdviceCheck.md), [BuildCardCheck](BuildCardCheck.md), [LandlordPortCheck](LandlordPortCheck.md)

## Sections

| line | section |
|---:|---|
| 147 | · the research's rule (runs/research-freight.md 5), its rows as the research writes them |
| 254 | 1. THE RULE |
| 278 | 2. THE CARGO, THE SHIPS AND THE TERMINALS |
| 390 | 2b. THE BULK BERTH (FR3) |
| 461 | 2c. A TERMINAL OF SEVERAL BERTHS, ORDERED BY THE COUNT (SL1) |
| 633 | 3. THE FACTOR |
| 667 | 4. THE SHARES |
| 742 | 5. NO PORT, THE SAME BAND |
| 790 | 6. A PORT ON A TOWN |
| 915 | 7. CRUDE NEEDS ROOM |
| 1032 | 8. THE BOATS |
| 1166 | 9. THE SAVE |
| 1221 | 10. THE HARBOUR'S QUAY |
| 1438 | · 10b. THE BOATS ALONGSIDE OVERLAP NOTHING (HB1 slice E, 2026-11-08) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 145 | `PortCheck.FILES` | `new java.util.IdentityHashMap<>()` | Each fixture town's save folder. |
| 150 | `PortCheck.KM` | `5000` | The distance the shares are struck at, km: the research's headline. |
| 153 | `PortCheck.LORRY_F` | `2.0, LORRY_C =.080, ACCESS_KM = 50` | A lorry: F US$2.0 a tonne, c US$0.080 a tonne-km; and the leg to a quay at each end, 50 km. |
| 156 | `PortCheck.LIQUID_ROWS` | `{ { 4 + 1,.0012 }, { 4 + 2,.0022 }, { 4 + 5,.0035 }, { 4 + 6,.0035 } }` | The sea rows, {F, c}: liquid VLCC, Suezmax, Aframax-LR, MR... |
| 158 | `PortCheck.DRY_ROWS` | `{ { 6 + 1.2,.00075 }, { 6 + 5,.0022 } }` | ...dry Capesize, Panamax... |
| 160 | `PortCheck.BOX_ROWS` | `{ { 24,.0055 } }` | ...deep-sea boxes... |
| 162 | `PortCheck.GENERAL_ROWS` | `{ { 40,.0060 } }` | ...and general cargo. |
| 464 | `PortCheck.MAIN_GENERAL_BERTH_TONNES_A_YEAR` | `450_000` | Tonnes a year of a General Cargo Terminal on main at 0.8.66, before SL1: the berth the default playtest stood full 35 of at m3996 (544,000 people). |
| 466 | `PortCheck.MAIN_GENERAL_BERTHS_AT_M3996` | `35` | ...and how many it stood, all full, at m3996. |
| 472 | `PortCheck.SPLIT_ROUNDING` | `2` | How far a terminal's cash, points or materials may sit from its ratio's rounding: 2. |
| 1224 | `PortCheck.BOX_W` | `6, BOX_H = 5` | The fixture terminal's box, in plots: 6 across and 5 deep (the fixture's own, not the model's). |
| 1441 | `PortCheck.BULK_ALONG` | `5, BULK_DEEP = 4` | A Bulk Berth's box of the fixture, in plots: 5 along the shore and 4 deep (the fixture's own; the berth's land is 215,000 m2). |
| 1444 | `PortCheck.BERTH_SCALES` | `{ 4, 8 }` | The scales the berths are laid at, px a plot: 4, just past DETAIL_FROM (where boats were drawn 1.6 times true before slice E), and 8 (the renders' other). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 99 | `static int fails` |  |
| 100 | `static PrintStream out` |  |
| 101 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 97 | 1785 | **type** `public class PortCheck` | The city's ports and their boats (0.7.86, batch O9; docs/studies/spec-oil.md 2.9, 2.10 and 4's PortCheck; runs/research-freight.md 5). |
| 103 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 108 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 114 | 4 | `static void timed(String label, boolean ok, String detail)` | A timing assertion's line (HB1 slice E, 10b's cost), as report() prints one, its verdict Timing's: soft under -Dcbs.timing=soft, hard at the gate. |
| 119 | 5 | `static void quietly(Runnable r)` |  |
| 125 | 6 | `static<T> T quietlyGet(java.util.function.Supplier<T> s)` |  |
| 132 | 5 | `static BuildingsTemplate template(Game g, String name)` |  |
| 139 | 4 | `static double auditMiss(Game g)` | The month's audit residual as a share of the house's money tolerance at what moved (MoneyAudit.tolerance()): at most 1 closes. |

### the research's rule (runs/research-freight.md 5), its rows as the research writes them (lines 147-253)

| line | len | member | says |
|---:|---:|---|---|
| 165 | 5 | `static double ruleShare(double[][] rows)` | Sea against a lorry, door to door, at KM: each row's, averaged over the kind's rows. |
| 176 | 19 | `static Game town(String label, int houses, double spare)` | A town that pays its people: houses, shops, bakeries to work in, power, water, roads and builders, standing, on ground a quarter more than they take and `spare` more (OilCheck's town). |
| 197 | 29 | `static boolean buyCoast(Game g)` | Buys toward the sea nearest the site, the offer nearest it each time, until one standing runs out to it, and buys that (WaterCheck's way). |
| 227 | 26 | `public static void main(String[] args) throws Exception` |  |

### 1. THE RULE (lines 254-277)

| line | len | member | says |
|---:|---:|---|---|
| 256 | 21 | `static void theRule()` |  |

### 2. THE CARGO, THE SHIPS AND THE TERMINALS (lines 278-389)

| line | len | member | says |
|---:|---:|---|---|
| 280 | 109 | `static void cargoShipsAndTerminals()` |  |

### 2b. THE BULK BERTH (FR3) (lines 390-460)

| line | len | member | says |
|---:|---:|---|---|
| 392 | 68 | `static void theBulkBerth()` |  |

### 2c. A TERMINAL OF SEVERAL BERTHS, ORDERED BY THE COUNT (SL1) (lines 461-632)

| line | len | member | says |
|---:|---:|---|---|
| 474 | 158 | `static void terminalsOfSeveralBerths()` |  |

### 3. THE FACTOR (lines 633-666)

| line | len | member | says |
|---:|---:|---|---|
| 635 | 31 | `static void theFactor()` |  |

### 4. THE SHARES (lines 667-741)

| line | len | member | says |
|---:|---:|---|---|
| 669 | 5 | `static double[] tonnes(Object...pairs)` |  |
| 675 | 66 | `static void theShares()` |  |

### 5. NO PORT, THE SAME BAND (lines 742-789)

| line | len | member | says |
|---:|---:|---|---|
| 744 | 45 | `static void noPortTheSameBand()` |  |

### 6. A PORT ON A TOWN (lines 790-914)

| line | len | member | says |
|---:|---:|---|---|
| 792 | 122 | `static Game aPortOnATown()` |  |

### 7. CRUDE NEEDS ROOM (lines 915-1031)

| line | len | member | says |
|---:|---:|---|---|
| 917 | 114 | `static void crudeNeedsRoom()` |  |

### 8. THE BOATS (lines 1032-1165)

| line | len | member | says |
|---:|---:|---|---|
| 1034 | 113 | `static void theBoats()` |  |
| 1149 | 16 | `static Ports monthAtSea()` | A month's trade by sea on every kind: Ports handed a month with each kind's berths taking its share. |

### 9. THE SAVE (lines 1166-1220)

| line | len | member | says |
|---:|---:|---|---|
| 1168 | 52 | `static void acrossASave(Game g) throws Exception` |  |

### 10. THE HARBOUR'S QUAY (lines 1221-1437)

| line | len | member | says |
|---:|---:|---|---|
| 1227 | 3 | `static CityShore.Work harbourWork(long x0, long y0, int side)` | A harbour terminal of the fixture: its box at (x0, y0), the sea on `side`, the harbour's quay. |
| 1232 | 8 | `static CityShore pairOnShore(int side, int gap)` | Two harbour terminals facing `side` at (2000, 2000), the second `gap` plots past the first along the shore, on the same line. |
| 1242 | 9 | `static CityShore.Wall pairsWall(int side, int gap)` | The wall the pair should make, worked from the geometry alone: the strip before both sea sides and the gap between, HARBOUR_QUAY_PLOTS deep. |
| 1253 | 4 | `static boolean walled(List<CityShore.Wall> walls, long x, long y)` | Whether some wall of `walls` holds world plot (x, y). |
| 1259 | 4 | `static boolean walledAll(List<CityShore.Wall> walls, long x0, long y0, long w, long h, boolean all)` | Whether every plot of the rectangle (x0, y0, w, h) is in a wall (all = true), or none of them is (all = false). |
| 1265 | 6 | `static boolean wallInABox(CityShore shore)` | Whether any plot of any wall of `shore` is in a work's box. |
| 1272 | 165 | `static void theHarbourQuay()` |  |

### 10b. THE BOATS ALONGSIDE OVERLAP NOTHING (HB1 slice E, 2026-11-08) (lines 1438-1881)

| line | len | member | says |
|---:|---:|---|---|
| 1447 | 4 | `static CityShore.Work bulkWork(long x0, long y0, int side)` | A Bulk Berth of the fixture with its box at (x0, y0), the sea on `side`, the harbour's quay. |
| 1453 | 6 | `static CityShore berthRow(int count, int side, int pitch)` | `count` Bulk Berths facing `side`, `pitch` plots from one box to the next along the shore, from (3000, 3000). |
| 1461 | 11 | `static List<BoatSchedule.Route> routesOf(CityShore shore, Ports.Cargo...cargos)` | The routes of a shore's berthed works in the order laid, one each, the berth's cargo cycling through `cargos`: a straight lane 60 plots out to sea from the work's berth point, the berth carrying the work's side. |
| 1474 | 3 | `static BoatSchedule.Boat dockedOn(List<BoatSchedule.Route> routes, int r, Ports.Ship ship, double arrives)` | A call of `ship` on route r arriving at `arrives`, and its boat at the quay the instant it does, as the schedule places it. |
| 1479 | 3 | `static double[][] box(double x, double y, double w, double h)` | A rectangle's outline {xs, ys}. |
| 1487 | 37 | `static double thickness(double[][] a, double[][] b)` | How thick the part two convex outlines share is: the narrowest width, across the edges of the part itself, of what the second clips of the first (Sutherland-Hodgman) - 0 when they share nothing or only a line. |
| 1530 | 18 | `static int[] audit(CityShore shore, List<BoatSchedule.Boat> given, List<BoatSchedule.Boat> drawn, double scale)` | What a layout of docked boats comes to, counted from the outlines (thickness()) and not the model's test: {how many lie in a place of their own, alongside or stern-to (are not the boat given), how many pairs of drawn ... |
| 1550 | 5 | `static int[] same(int side, int n)` | `n` copies of a side, for kinds(): a row of boats facing one way. |
| 1557 | 11 | `static int[] kinds(List<BoatSchedule.Boat> given, List<BoatSchedule.Boat> drawn, int[] sides, double scale)` | How a layout's docked boats lie, by comparing each with the places BoatSchedule gives it on its side (sides[i], the side of the berth of boat i): {alongside, stern-to, as placed}. |
| 1570 | 3 | `static void add(int[] to, int[] k)` | The sum of a kinds() count into another. |
| 1574 | 229 | `static void berthsShareTheQuay()` |  |
| 1810 | 71 | `static void windowFixture()` | A boat's place does not depend on the window: BoatSchedule.drawn() weighs the boats of a frame against every boat docked at that moment (docked()), on a bay of berths in a row, liquid and dry bulk by turns (MR tankers... |

