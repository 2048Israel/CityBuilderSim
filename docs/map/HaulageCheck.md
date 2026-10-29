# HaulageCheck.java - 1,621 lines · 41 methods · 4 constants · harnesses

`ham/citybuildersim/HaulageCheck.java` - generated 2026-10-29 by CodeMap; line numbers are as of that run.

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
>   3. EACH LEG ONCE. In a played city with a mine selling ore at home to a
>      pelletising plant that lands its coal and ships its pellets, every
>      sector's "Haulage" line is its legs' tonnes at its own share in force and
>      the quote, a tonne sold at home is its buyer's and nobody else's, a goods
>      tonne has no truck leg, and Freight's revenue is the shippers' lines to
>      the cent. And a reload bills the next month to the bit, with the same
>      shares behind its need and the same fleet after it.
>   4. THE SIDING. A railway carrying a share r of the imports takes
>      RAIL_ROAD_RELIEF x r of their truck tonnes off the city leg; a share
>      carried by sea alone relieves nothing (since FR6 a station beside a
>      sea terminal is a port yard that does: HomeRailCheck 4).
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
>      its first works, standing after the haul, waits for the next deal; a
>      fleet that cannot cover what the sectors were carried the month before
>      cuts the largest standing demand and leaves the smaller whole; and each
>      sector's share and standing demand are saved, to the bit.
> 
> It tests the model through its public getters and constants and changes
> ... (5 more lines in the source)

**Uses:** [Good](Good.md) (147), [Sector](Sector.md) (77), [Game](Game.md) (49), [Haulage](Haulage.md) (42), [Freight](Freight.md) (32), [ProcessingSector](ProcessingSector.md) (24), [JobType](JobType.md) (22), [Sectors](Sectors.md) (18), [GameFiles](GameFiles.md) (16), [Traffic](Traffic.md) (13), [BuildingsTemplate](BuildingsTemplate.md) (7), [BuildingManager](BuildingManager.md) (5), [Mining](Mining.md) (5), [BusinessInvestment](BusinessInvestment.md) (5), [SpreadPlanner](SpreadPlanner.md) (4), [Founding](Founding.md) (3), [HeavyIndustry](HeavyIndustry.md) (2), [InfrastructureManager](InfrastructureManager.md) (1), [Resource](Resource.md) (1), [Ports](Ports.md) (1), [BuildingType](BuildingType.md) (1), [Markets](Markets.md) (1)

**Used by (8):** [FreightViewCheck](FreightViewCheck.md), [HomeRailCheck](HomeRailCheck.md), [LandlordPortCheck](LandlordPortCheck.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [RailDepotCheck](RailDepotCheck.md), [TradeBandCheck](TradeBandCheck.md), [TruckCheck](TruckCheck.md), [TruckRouteCheck](TruckRouteCheck.md)

## Sections

| line | section |
|---:|---|
| 126 | THE FIXTURES |
| 424 | · 3. EACH LEG ONCE - and A RELOAD, on the same city |
| 518 | · · the reload |
| 609 | 1. THE SLOT |
| 642 | 2. TRUCKS |
| 689 | 5. VANS FOR GOODS |
| 717 | 6. THE FREIGHT FACTOR |
| 819 | 7. THE FLEET and 8. THE PRICE |
| 931 | · 8. THE PRICE |
| 1014 | 4. THE SIDING |
| 1084 | 9. THE DEPOT |
| 1201 | 10. THE WEIGHING |
| 1315 | 11. THE DEAL (FR8) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 113 | `HaulageCheck.GROSS_TONNES` | `40` | A truck's gross weight, FR2.md section 2 (an EU 40 t articulated truck): the model states its tare, 40 t less a load. |
| 116 | `HaulageCheck.RELIEF` | `InfrastructureManager.RAIL_ROAD_RELIEF` | The relief a siding gives a railed tonne's city leg (InfrastructureManager). |
| 118 | `HaulageCheck.T` | `Haulage.TONNES_A_TRUCK_MONTH` |  |
| 124 | `HaulageCheck.SLOT` | `11` | Freight's place in Sectors.KEYS: FR2.md calls it "position 12" and "KEYS[12]", counting from one - the twelfth sector, Rail's old place, index 11 (retail is the first). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 85 | `static int fails` |  |
| 201 | `final Map<Sector, Double> legs` |  |
| 202 | `double tonnes, capacity, quote, share, fit` |  |
| 248 | `static String worstWhere` | Where the last compare() found its worst miss: the sector, what it was billed, what the brief says. |
| 251 | `static double topOfMonth` | What the builders' repair imports added to their line over the rows the prediction saw (positive), over every compare() so far. |
| 1325 | `final GameFiles files` |  |
| 1326 | `final Game game` |  |
| 1327 | `final Freight fr` |  |
| 1328 | `final Haulage h` |  |
| 1329 | `final Map<String, Double> standing` |  |
| 1330 | `double standingTotal` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 83 | 1539 | **type** `public class HaulageCheck` | Freight's trucks, held to FR2's brief: the city leg of every bulk tonne is billed once, by Haulage, at a quote the trucks' own books set, and the vans carry goods and nothing else. |
| 87 | 5 | `static void quietly(Runnable r)` |  |
| 93 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 98 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 104 | 3 | `static boolean same(double a, double b)` | Bitwise, not near. |
| 108 | 3 | `static boolean near(double a, double b, double tol)` |  |

### THE FIXTURES (lines 126-608)

| line | len | member | says |
|---:|---:|---|---|
| 144 | 1 | `static Game base(GameFiles files)` | A city with a bulk chain at home and trade both ways: three iron mines, and a pelletising plant that buys their ore (and lands the rest, which the mines cannot make), lands its coal and ships its pellets. |
| 147 | 29 | `static Game base(GameFiles files, boolean plant)` | ...with or without the plant: mines and no works are a city whose ore all goes abroad. |
| 178 | 1 | `static Game played(GameFiles files, int months)` | ...played for `months`, which is long enough for the trucks to be seeded (at the first haul, at the plant's nameplate need) and the books settled. |
| 180 | 5 | `static Game played(GameFiles files, int months, boolean plant)` |  |
| 186 | 1 | `static Game played(String label, int months)` |  |
| 189 | 5 | `static void lay(Game game, String name, int count)` | Track or depots, handed to the city rather than waited for, on their ground (RailCheck.lay()). |
| 200 | 9 | **type** `static final class Pred` | The month about to be billed, worked out from the model's own getters before the haul runs. |
| 205 | 1 | `double f(Sector s)` _(in HaulageCheck.Pred)_ | The share of its legs the sector is billed at: its own share in force, scaled to the trucks' capacity. |
| 207 | 1 | `double bill(Sector s)` _(in HaulageCheck.Pred)_ |  |
| 217 | 27 | `static Pred predict(Game game)` | What the next haul bills, from the rows the month just run left: each sector's legs (Haulage.legTonnes at the railway's share of each good in force), at the share its own industry was dealt (FR8) and, the sectors' car... |
| 245 | 1 | `static double line(Sector s)` |  |
| 262 | 38 | `static double[] compare(Game game, Pred p)` | Every sector's "Haulage" line against the prediction. |
| 305 | 13 | `static String dealtUnlike(Game live, Game back)` | What differs between two cities' deals (FR8), to the bit: each sector's share and standing demand, the newest share, the standing share. |
| 319 | 5 | `static double sum(Game game)` |  |
| 326 | 9 | `static double goodsTonnes(Sector s)` | The GOODS tonnes a sector's standing plant moves, in and out, worked out from its capacities (no captive good in these cities). |
| 337 | 9 | `static double bulkTonnes(Sector s)` | The BULK tonnes a sector's plant moves at nameplate. |
| 348 | 1 | `static void haul(Game game)` | One haul, run by hand: the month's rows billed at the state in force, as Game.chargeFreight() runs it. |
| 355 | 9 | `static void setState(Haulage h, double need, double share)` | Haulage's need and share put where a fixture wants them, through its own save and restore (the share is struck at a haul, so a fixture cannot reach it by moving the pantry). |
| 366 | 6 | `static void offTheBooks(Haulage h)` | Trucks a fixture hands over (setPantry) were bought by nobody: Haulage's fleet account (★10) kept at nought for them, or their wear comes back to the shippers as a refund. |
| 379 | 11 | `static double[] tradeShares(Game game, Good g)` | A good's shares of the city's trade, from the sectors' own month: the units shipped abroad over those and the units bought at home, and the units landed over those and the units bought at home - or, with nothing trade... |
| 396 | 16 | `static double nameplate(Game game, boolean railed)` | The city-leg tonnes the plant standing asks for at nameplate, from the brief's weights (the FR2 review): every sector's but Freight's BULK inputs at capacity x tpu x (1 - RELIEF x rail x import share), plus its BULK o... |
| 414 | 4 | `static void strike(Game game, double fleet)` | The trucks set to `fleet` and a haul run by hand: the share is struck then, over the fleet held at its start (Haulage.getShare()). |
| 419 | 189 | `public static void main(String[] args)` |  |

### 1. THE SLOT (lines 609-641)

| line | len | member | says |
|---:|---:|---|---|
| 613 | 28 | `static void slot()` |  |

### 2. TRUCKS (lines 642-688)

| line | len | member | says |
|---:|---:|---|---|
| 646 | 42 | `static void trucks()` |  |

### 5. VANS FOR GOODS (lines 689-716)

| line | len | member | says |
|---:|---:|---|---|
| 693 | 23 | `static void vans(Game a)` |  |

### 6. THE FREIGHT FACTOR (lines 717-818)

| line | len | member | says |
|---:|---:|---|---|
| 721 | 97 | `static void factor(Game a)` |  |

### 7. THE FLEET and 8. THE PRICE (lines 819-1013)

| line | len | member | says |
|---:|---:|---|---|
| 823 | 161 | `static void fleetAndPrice()` |  |
| 986 | 27 | `static void seeding()` | The first fleet is given once, at the nameplate need, free; a city that is known to have run out is not given another. |

### 4. THE SIDING (lines 1014-1083)

| line | len | member | says |
|---:|---:|---|---|
| 1018 | 65 | `static void siding()` |  |

### 9. THE DEPOT (lines 1084-1200)

| line | len | member | says |
|---:|---:|---|---|
| 1088 | 112 | `static void depot()` |  |

### 10. THE WEIGHING (lines 1201-1314)

| line | len | member | says |
|---:|---:|---|---|
| 1205 | 109 | `static void weighing()` |  |

### 11. THE DEAL (FR8) (lines 1315-1621)

| line | len | member | says |
|---:|---:|---|---|
| 1324 | 35 | **type** `static final class Town` | A played city whose trucks suffice, every sector with legs dealt a literal 1 and its standing demand noted: the town whose fleet is full, before anything lands. |
| 1332 | 1 | `Town(String label)` _(in HaulageCheck.Town)_ |  |
| 1340 | 18 | `Town(String label, boolean bakery)` _(in HaulageCheck.Town)_ | ...with `bakery` an Industrial Bakery handed to the city before its first month (WN3, the world market): the sector with legs the deal's save section counts a fourth of. |
| 1361 | 8 | `static boolean land(Game g, String building, int count)` | `count` more of a building, handed to the city finished and paid for from a fixture's cash (as base() builds its plant); whether it stood. |
| 1371 | 5 | `static double trucksFor(double tonnes)` | The trucks that carry `tonnes` a month, and not an ulp short of it. |
| 1377 | 244 | `static void deal()` |  |

