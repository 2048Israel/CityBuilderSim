# HaulageCheck.java - 1,671 lines · 44 methods · 4 constants · harnesses

`ham/citybuildersim/HaulageCheck.java` - generated 2026-11-22 by CodeMap; line numbers are as of that run.

> Freight's trucks, held to FR2's brief: the city leg of every bulk tonne is
> billed once, by Haulage, at a quote the trucks' own books set, and the vans
> carry goods and nothing else.
> 
> WHY THIS HARNESS EXISTS. Until FR2 every sector moved its own bulk in its own
> vans, both sides of every door: an imported tonne paid its city leg twice, a
> tonne between two works paid nobody and a siding saved nothing. FR2 (docs/queue/
> FR2.md) moved the city leg to Freight's Haulage division. It was written by
> one agent from the brief and is held here by another, from the brief's rules
> and the model's own getters and constants: no figure below is typed in that a
> getter or a constant could give. Ten claims, numbered as FR2.md section 6
> numbers them (its 11, a real run, is LongPlaytest's), and an eleventh that
> is FR8's (docs/queue/FR8.md), not FR2's:
> 
>   1. THE SLOT. The twelfth key, Sectors.KEYS[11] (the brief's "position 12"),
>      is "Freight", the registry has as many sectors as keys, and the twelfth
>      is a Freight.
>   2. TRUCKS. The world sells them and never buys them, one truck weighs 40 t
>      gross less a load, and the Heavy Truck Plant alone makes them.
>   3. EACH LEG ONCE. (FS7: the months short of trucks and the months with
>      enough are caused, the fleet set to half the need at one month and to
>      twice it at another, and read the month after each; the legs the plant's
>      trade makes are still the play's.) In a played city with a mine selling ore at home to a
>      pelletising plant that lands its coal and ships its pellets, every
>      sector's "Haulage" line is its legs' tonnes at its own share in force and
>      the quote, a tonne sold at home is its buyer's and nobody else's, a goods
>      tonne has no truck leg, and Freight's revenue is the shippers' lines to
>      the cent. And a reload bills the next month to the bit, with the same
>      shares behind its need and the same fleet after it.
>   4. THE SIDING. A railway carrying a share r of the imports takes
>      RAIL_ROAD_RELIEF x r of their truck tonnes off the city leg; a share
>      carried by sea alone relieves nothing (since FR6 a station beside a
>      sea terminal is a port yard that does: HomeRailCheck 4). (FS7: the
>      share is caused, 0 < r < 1: the spurs are floor(bulk offered / P) - 1,
>      P a spur's sidings, so their sidings stand short of the bulk and are
>      at least one; the sea city's "goes by sea, not rail" is still the play's.)
>   5. VANS FOR GOODS. A works whose tonnes are all bulk keeps no vans; the
>      vans of a sector with goods are its goods' tonnes over TONNES_PER_VAN.
>   6. THE FREIGHT FACTOR. A bulk sector runs at MIN_VAN_RATE + (1 - MIN_VAN_RATE)
>      x the trucks' share, a literal 1 when the trucks suffice, and the
>      fifth factor is the lesser of that and its vans'.
>   7. THE FLEET. The need (the plant standing, at nameplate, struck at each
>      haul), the bid, the wear (a tenth faster past the depots' room), the
>      seeding once and never again, and the bill never past what the trucks
>      could carry.
>   8. THE PRICE. The quote walks REPRICE_SPEED toward the rule's target, over
>      what the trucks COULD carry, at the scarcity the tightness sets.
>   9. THE DEPOT. Ordered once the fleet passes the room by DEPOT_ORDER_MARGIN of
>      a depot, not before; and the drivers follow the trucks: a depot keeping
>      fewer trucks than its room offers fewer driver posts, and the city counts
>      and pays the same number.
>  10. THE WEIGHING. A works is weighed with its legs at the quote, the vans
>      only for its goods, the railed part of an import relieved by the siding.
>  11. THE DEAL (FR8). When the trucks cannot carry the whole need the fleet's
>      room is dealt per sector, the newest demand last: a giant works landing
>      in a town whose fleet is full shortens its own sector and no other's
>      (the city's share is still the fleet over the need, which is where the
>      one-share rule would have put every bulk sector); a fleet with room
>      leaves every sector whole; the growth is served pro rata from the room
>      left; a sector that asked nothing at the haul is dealt a whole 1, and
> ... (11 more lines in the source)

**Uses:** [Good](Good.md) (148), [Sector](Sector.md) (77), [Game](Game.md) (51), [Haulage](Haulage.md) (42), [Freight](Freight.md) (32), [ProcessingSector](ProcessingSector.md) (24), [JobType](JobType.md) (22), [GameFiles](GameFiles.md) (20), [Sectors](Sectors.md) (19), [Traffic](Traffic.md) (13), [BuildingsTemplate](BuildingsTemplate.md) (8), [BuildingManager](BuildingManager.md) (5), [Mining](Mining.md) (5), [BusinessInvestment](BusinessInvestment.md) (5), [SpreadPlanner](SpreadPlanner.md) (4), [Founding](Founding.md) (3), [HeavyIndustry](HeavyIndustry.md) (2), [InfrastructureManager](InfrastructureManager.md) (1), [Resource](Resource.md) (1), [Ports](Ports.md) (1), [BuildingType](BuildingType.md) (1), [Markets](Markets.md) (1)

**Used by (9):** [EquipmentCheck](EquipmentCheck.md), [Forms1900Check](Forms1900Check.md), [FreightViewCheck](FreightViewCheck.md), [HomeRailCheck](HomeRailCheck.md), [IslandRailCheck](IslandRailCheck.md), [LandlordPortCheck](LandlordPortCheck.md), [RailDepotCheck](RailDepotCheck.md), [TradeBandCheck](TradeBandCheck.md), [TruckRouteCheck](TruckRouteCheck.md)

## Sections

| line | section |
|---:|---|
| 132 | THE FIXTURES |
| 442 | · 3. EACH LEG ONCE - and A RELOAD, on the same city |
| 548 | · · the reload |
| 639 | 1. THE SLOT |
| 672 | 2. TRUCKS |
| 719 | 5. VANS FOR GOODS |
| 747 | 6. THE FREIGHT FACTOR |
| 849 | 7. THE FLEET and 8. THE PRICE |
| 961 | · 8. THE PRICE |
| 1044 | 4. THE SIDING |
| 1125 | 9. THE DEPOT |
| 1242 | 10. THE WEIGHING |
| 1357 | 11. THE DEAL (FR8) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 119 | `HaulageCheck.GROSS_TONNES` | `40` | A truck's gross weight, FR2.md section 2 (an EU 40 t articulated truck): the model states its tare, 40 t less a load. |
| 122 | `HaulageCheck.RELIEF` | `InfrastructureManager.RAIL_ROAD_RELIEF` | The relief a siding gives a railed tonne's city leg (InfrastructureManager). |
| 124 | `HaulageCheck.T` | `Haulage.TONNES_A_TRUCK_MONTH` |  |
| 130 | `HaulageCheck.SLOT` | `11` | Freight's place in Sectors.KEYS: FR2.md calls it "position 12" and "KEYS[12]", counting from one - the twelfth sector, Rail's old place, index 11 (retail is the first). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 91 | `static int fails` |  |
| 219 | `final Map<Sector, Double> legs` |  |
| 220 | `double tonnes, capacity, quote, share, fit` |  |
| 266 | `static String worstWhere` | Where the last compare() found its worst miss: the sector, what it was billed, what the brief says. |
| 269 | `static double topOfMonth` | What the builders' repair imports added to their line over the rows the prediction saw (positive), over every compare() so far. |
| 1367 | `final GameFiles files` |  |
| 1368 | `final Game game` |  |
| 1369 | `final Freight fr` |  |
| 1370 | `final Haulage h` |  |
| 1371 | `final Map<String, Double> standing` |  |
| 1372 | `double standingTotal` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 89 | 1583 | **type** `public class HaulageCheck` | Freight's trucks, held to FR2's brief: the city leg of every bulk tonne is billed once, by Haulage, at a quote the trucks' own books set, and the vans carry goods and nothing else. |
| 93 | 5 | `static void quietly(Runnable r)` |  |
| 99 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 104 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 110 | 3 | `static boolean same(double a, double b)` | Bitwise, not near. |
| 114 | 3 | `static boolean near(double a, double b, double tol)` |  |

### THE FIXTURES (lines 132-638)

| line | len | member | says |
|---:|---:|---|---|
| 150 | 1 | `static Game base(GameFiles files)` | A city with a bulk chain at home and trade both ways: three iron mines, and a pelletising plant that buys their ore (and lands the rest, which the mines cannot make), lands its coal and ships its pellets. |
| 153 | 1 | `static Game base(GameFiles files, boolean plant)` | ...with or without the plant: mines and no works are a city whose ore all goes abroad. |
| 162 | 30 | `static Game base(GameFiles files, boolean plant, boolean literals)` | ...and, with `literals`, playing on the goods' literals (Game.playOnLiterals(): the flag is the harness's and not the save's, so a city so made is not one to reload). |
| 194 | 1 | `static Game played(GameFiles files, int months)` | ...played for `months`, which is long enough for the trucks to be seeded (at the first haul, at the plant's nameplate need) and the books settled. |
| 196 | 1 | `static Game played(GameFiles files, int months, boolean plant)` |  |
| 198 | 5 | `static Game played(GameFiles files, int months, boolean plant, boolean literals)` |  |
| 204 | 1 | `static Game played(String label, int months)` |  |
| 207 | 5 | `static void lay(Game game, String name, int count)` | Track or depots, handed to the city rather than waited for, on their ground (RailCheck.lay()). |
| 218 | 9 | **type** `static final class Pred` | The month about to be billed, worked out from the model's own getters before the haul runs. |
| 223 | 1 | `double f(Sector s)` _(in HaulageCheck.Pred)_ | The share of its legs the sector is billed at: its own share in force, scaled to the trucks' capacity. |
| 225 | 1 | `double bill(Sector s)` _(in HaulageCheck.Pred)_ |  |
| 235 | 27 | `static Pred predict(Game game)` | What the next haul bills, from the rows the month just run left: each sector's legs (Haulage.legTonnes at the railway's share of each good in force), at the share its own industry was dealt (FR8) and, the sectors' car... |
| 263 | 1 | `static double line(Sector s)` |  |
| 280 | 38 | `static double[] compare(Game game, Pred p)` | Every sector's "Haulage" line against the prediction. |
| 323 | 13 | `static String dealtUnlike(Game live, Game back)` | What differs between two cities' deals (FR8), to the bit: each sector's share and standing demand, the newest share, the standing share. |
| 337 | 5 | `static double sum(Game game)` |  |
| 344 | 9 | `static double goodsTonnes(Sector s)` | The GOODS tonnes a sector's standing plant moves, in and out, worked out from its capacities (no captive good in these cities). |
| 355 | 9 | `static double bulkTonnes(Sector s)` | The BULK tonnes a sector's plant moves at nameplate. |
| 366 | 1 | `static void haul(Game game)` | One haul, run by hand: the month's rows billed at the state in force, as Game.chargeFreight() runs it. |
| 373 | 9 | `static void setState(Haulage h, double need, double share)` | Haulage's need and share put where a fixture wants them, through its own save and restore (the share is struck at a haul, so a fixture cannot reach it by moving the pantry). |
| 384 | 6 | `static void offTheBooks(Haulage h)` | Trucks a fixture hands over (setPantry) were bought by nobody: Haulage's fleet account (★10) kept at nought for them, or their wear comes back to the shippers as a refund. |
| 397 | 11 | `static double[] tradeShares(Game game, Good g)` | A good's shares of the city's trade, from the sectors' own month: the units shipped abroad over those and the units bought at home, and the units landed over those and the units bought at home - or, with nothing trade... |
| 414 | 16 | `static double nameplate(Game game, boolean railed)` | The city-leg tonnes the plant standing asks for at nameplate, from the brief's weights (the FR2 review): every sector's but Freight's BULK inputs at capacity x tpu x (1 - RELIEF x rail x import share), plus its BULK o... |
| 432 | 4 | `static void strike(Game game, double fleet)` | The trucks set to `fleet` and a haul run by hand: the share is struck then, over the fleet held at its start (Haulage.getShare()). |
| 437 | 201 | `public static void main(String[] args)` |  |

### 1. THE SLOT (lines 639-671)

| line | len | member | says |
|---:|---:|---|---|
| 643 | 28 | `static void slot()` |  |

### 2. TRUCKS (lines 672-718)

| line | len | member | says |
|---:|---:|---|---|
| 676 | 42 | `static void trucks()` |  |

### 5. VANS FOR GOODS (lines 719-746)

| line | len | member | says |
|---:|---:|---|---|
| 723 | 23 | `static void vans(Game a)` |  |

### 6. THE FREIGHT FACTOR (lines 747-848)

| line | len | member | says |
|---:|---:|---|---|
| 751 | 97 | `static void factor(Game a)` |  |

### 7. THE FLEET and 8. THE PRICE (lines 849-1043)

| line | len | member | says |
|---:|---:|---|---|
| 853 | 161 | `static void fleetAndPrice()` |  |
| 1016 | 27 | `static void seeding()` | The first fleet is given once, at the nameplate need, free; a city that is known to have run out is not given another. |

### 4. THE SIDING (lines 1044-1124)

| line | len | member | says |
|---:|---:|---|---|
| 1048 | 76 | `static void siding()` |  |

### 9. THE DEPOT (lines 1125-1241)

| line | len | member | says |
|---:|---:|---|---|
| 1129 | 112 | `static void depot()` |  |

### 10. THE WEIGHING (lines 1242-1356)

| line | len | member | says |
|---:|---:|---|---|
| 1246 | 110 | `static void weighing()` |  |

### 11. THE DEAL (FR8) (lines 1357-1671)

| line | len | member | says |
|---:|---:|---|---|
| 1366 | 43 | **type** `static final class Town` | A played city whose trucks suffice, every sector with legs dealt a literal 1 and its standing demand noted: the town whose fleet is full, before anything lands. |
| 1374 | 1 | `Town(String label)` _(in HaulageCheck.Town)_ |  |
| 1382 | 1 | `Town(String label, boolean bakery)` _(in HaulageCheck.Town)_ | ...with `bakery` an Industrial Bakery handed to the city before its first month (WN3, the world market): the sector with legs the deal's save section counts a fourth of. |
| 1389 | 19 | `Town(String label, boolean bakery, String held)` _(in HaulageCheck.Town)_ | ...with `held` a sector held out of the planner's hands through the sixteen months (CU2), so it stands no works at the haul. |
| 1411 | 8 | `static boolean land(Game g, String building, int count)` | `count` more of a building, handed to the city finished and paid for from a fixture's cash (as base() builds its plant); whether it stood. |
| 1421 | 5 | `static double trucksFor(double tonnes)` | The trucks that carry `tonnes` a month, and not an ulp short of it. |
| 1427 | 244 | `static void deal()` |  |

