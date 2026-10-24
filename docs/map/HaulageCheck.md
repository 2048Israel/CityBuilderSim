# HaulageCheck.java - 1,243 lines · 34 methods · 4 constants · harnesses

`ham/citybuildersim/HaulageCheck.java` - generated 2026-10-24 by CodeMap; line numbers are as of that run.

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
> numbers them (its 11, a real run, is LongPlaytest's):
> 
>   1. THE SLOT. The twelfth key, Sectors.KEYS[11] (the brief's "position 12"),
>      is "Freight", the registry has as many sectors as keys, and the twelfth
>      is a Freight.
>   2. TRUCKS. The world sells them and never buys them, one truck weighs 40 t
>      gross less a load, and the Heavy Truck Plant alone makes them.
>   3. EACH LEG ONCE. In a played city with a mine selling ore at home to a
>      pelletising plant that lands its coal and ships its pellets, every
>      sector's "Haulage" line is its legs' tonnes at the share in force and the
>      quote, a tonne sold at home is its buyer's and nobody else's, a goods
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
> 
> It tests the model through its public getters and constants and changes
> nothing in it; where the code and the brief disagree it says FAIL and the
> line says where. The sections run in the order 1, 2, 3 (with the reload), 5, 6, 7, 8, 4, 9, 10, because 5 and 6 read section 3's city
> (6 changes it with hauls run by hand) and every later section plays a city of its own.

**Uses:** [Good](Good.md) (147), [Sector](Sector.md) (45), [Haulage](Haulage.md) (35), [Game](Game.md) (34), [Freight](Freight.md) (30), [ProcessingSector](ProcessingSector.md) (24), [JobType](JobType.md) (22), [Sectors](Sectors.md) (18), [GameFiles](GameFiles.md) (13), [Traffic](Traffic.md) (13), [BuildingsTemplate](BuildingsTemplate.md) (7), [BuildingManager](BuildingManager.md) (5), [Mining](Mining.md) (5), [BusinessInvestment](BusinessInvestment.md) (5), [SpreadPlanner](SpreadPlanner.md) (4), [HeavyIndustry](HeavyIndustry.md) (2), [InfrastructureManager](InfrastructureManager.md) (1), [Founding](Founding.md) (1), [Resource](Resource.md) (1), [Ports](Ports.md) (1), [BuildingType](BuildingType.md) (1), [Markets](Markets.md) (1)

**Used by (7):** [FreightViewCheck](FreightViewCheck.md), [HomeRailCheck](HomeRailCheck.md), [LandlordPortCheck](LandlordPortCheck.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [RailDepotCheck](RailDepotCheck.md), [TradeBandCheck](TradeBandCheck.md), [TruckCheck](TruckCheck.md)

## Sections

| line | section |
|---:|---|
| 110 | THE FIXTURES |
| 369 | · 3. EACH LEG ONCE - and A RELOAD, on the same city |
| 463 | · · the reload |
| 546 | 1. THE SLOT |
| 579 | 2. TRUCKS |
| 626 | 5. VANS FOR GOODS |
| 654 | 6. THE FREIGHT FACTOR |
| 748 | 7. THE FLEET and 8. THE PRICE |
| 860 | · 8. THE PRICE |
| 943 | 4. THE SIDING |
| 1013 | 9. THE DEPOT |
| 1130 | 10. THE WEIGHING |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 97 | `HaulageCheck.GROSS_TONNES` | `40` | A truck's gross weight, FR2.md section 2 (an EU 40 t articulated truck): the model states its tare, 40 t less a load. |
| 100 | `HaulageCheck.RELIEF` | `InfrastructureManager.RAIL_ROAD_RELIEF` | The relief a siding gives a railed tonne's city leg (InfrastructureManager). |
| 102 | `HaulageCheck.T` | `Haulage.TONNES_A_TRUCK_MONTH` |  |
| 108 | `HaulageCheck.SLOT` | `11` | Freight's place in Sectors.KEYS: FR2.md calls it "position 12" and "KEYS[12]", counting from one - the twelfth sector, Rail's old place, index 11 (retail is the first). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 69 | `static int fails` |  |
| 181 | `final Map<Sector, Double> legs` |  |
| 182 | `double tonnes, capacity, quote, share, f` |  |
| 218 | `static String worstWhere` | Where the last compare() found its worst miss: the sector, what it was billed, what the brief says. |
| 221 | `static double topOfMonth` | What the builders' repair imports added to their line over the rows the prediction saw (positive), over every compare() so far. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 67 | 1177 | **type** `public class HaulageCheck` | Freight's trucks, held to FR2's brief: the city leg of every bulk tonne is billed once, by Haulage, at a quote the trucks' own books set, and the vans carry goods and nothing else. |
| 71 | 5 | `static void quietly(Runnable r)` |  |
| 77 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 82 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 88 | 3 | `static boolean same(double a, double b)` | Bitwise, not near. |
| 92 | 3 | `static boolean near(double a, double b, double tol)` |  |

### THE FIXTURES (lines 110-545)

| line | len | member | says |
|---:|---:|---|---|
| 128 | 1 | `static Game base(GameFiles files)` | A city with a bulk chain at home and trade both ways: three iron mines, and a pelletising plant that buys their ore (and lands the rest, which the mines cannot make), lands its coal and ships its pellets. |
| 131 | 29 | `static Game base(GameFiles files, boolean plant)` | ...with or without the plant: mines and no works are a city whose ore all goes abroad. |
| 162 | 1 | `static Game played(GameFiles files, int months)` | ...played for `months`, which is long enough for the trucks to be seeded (at the first haul, at the plant's nameplate need) and the books settled. |
| 164 | 5 | `static Game played(GameFiles files, int months, boolean plant)` |  |
| 170 | 1 | `static Game played(String label, int months)` |  |
| 173 | 5 | `static void lay(Game game, String name, int count)` | Track or depots, handed to the city rather than waited for, on their ground (RailCheck.lay()). |
| 180 | 6 | **type** `static final class Pred` | The month about to be billed, worked out from the model's own getters before the haul runs. |
| 184 | 1 | `double bill(Sector s)` _(in HaulageCheck.Pred)_ |  |
| 193 | 21 | `static Pred predict(Game game)` | What the next haul bills, from the rows the month just run left: each sector's legs (Haulage.legTonnes at the railway's share of each good in force), at the share Haulage says it carries and never more than the trucks... |
| 215 | 1 | `static double line(Sector s)` |  |
| 231 | 38 | `static double[] compare(Game game, Pred p)` | Every sector's "Haulage" line against the prediction. |
| 270 | 5 | `static double sum(Game game)` |  |
| 277 | 9 | `static double goodsTonnes(Sector s)` | The GOODS tonnes a sector's standing plant moves, in and out, worked out from its capacities (no captive good in these cities). |
| 288 | 9 | `static double bulkTonnes(Sector s)` | The BULK tonnes a sector's plant moves at nameplate. |
| 299 | 1 | `static void haul(Game game)` | One haul, run by hand: the month's rows billed at the state in force, as Game.chargeFreight() runs it. |
| 302 | 7 | `static void setState(Haulage h, double need, double share)` | Haulage's need and share put where a fixture wants them, through its own save and restore (the share is struck at a haul, so a fixture cannot reach it by moving the pantry). |
| 311 | 6 | `static void offTheBooks(Haulage h)` | Trucks a fixture hands over (setPantry) were bought by nobody: Haulage's fleet account (★10) kept at nought for them, or their wear comes back to the shippers as a refund. |
| 324 | 11 | `static double[] tradeShares(Game game, Good g)` | A good's shares of the city's trade, from the sectors' own month: the units shipped abroad over those and the units bought at home, and the units landed over those and the units bought at home - or, with nothing trade... |
| 341 | 16 | `static double nameplate(Game game, boolean railed)` | The city-leg tonnes the plant standing asks for at nameplate, from the brief's weights (the FR2 review): every sector's but Freight's BULK inputs at capacity x tpu x (1 - RELIEF x rail x import share), plus its BULK o... |
| 359 | 4 | `static void strike(Game game, double fleet)` | The trucks set to `fleet` and a haul run by hand: the share is struck then, over the fleet held at its start (Haulage.getShare()). |
| 364 | 181 | `public static void main(String[] args)` |  |

### 1. THE SLOT (lines 546-578)

| line | len | member | says |
|---:|---:|---|---|
| 550 | 28 | `static void slot()` |  |

### 2. TRUCKS (lines 579-625)

| line | len | member | says |
|---:|---:|---|---|
| 583 | 42 | `static void trucks()` |  |

### 5. VANS FOR GOODS (lines 626-653)

| line | len | member | says |
|---:|---:|---|---|
| 630 | 23 | `static void vans(Game a)` |  |

### 6. THE FREIGHT FACTOR (lines 654-747)

| line | len | member | says |
|---:|---:|---|---|
| 658 | 89 | `static void factor(Game a)` |  |

### 7. THE FLEET and 8. THE PRICE (lines 748-942)

| line | len | member | says |
|---:|---:|---|---|
| 752 | 161 | `static void fleetAndPrice()` |  |
| 915 | 27 | `static void seeding()` | The first fleet is given once, at the nameplate need, free; a city that is known to have run out is not given another. |

### 4. THE SIDING (lines 943-1012)

| line | len | member | says |
|---:|---:|---|---|
| 947 | 65 | `static void siding()` |  |

### 9. THE DEPOT (lines 1013-1129)

| line | len | member | says |
|---:|---:|---|---|
| 1017 | 112 | `static void depot()` |  |

### 10. THE WEIGHING (lines 1130-1243)

| line | len | member | says |
|---:|---:|---|---|
| 1134 | 109 | `static void weighing()` |  |

