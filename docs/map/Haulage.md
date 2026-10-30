# Haulage.java - 1,252 lines · 72 methods · 14 constants · sectors

`ham/citybuildersim/sectors/Haulage.java` - generated 2026-10-30 by CodeMap; line numbers are as of that run.

> Freight's Haulage division: the trucks that carry the city leg of every bulk
> tonne (FR2, 2026-10-18; docs/studies/spec-freight-and-infrastructure.md 2,
> 3.2, 3.3).
> 
> WHY. Until FR2 every sector moved its own bulk in its own vans, 120 t a
> van-month, both sides of every door, so an imported tonne paid its city leg
> twice (once inside the band, once in the buyer's vans), a tonne between two
> works paid nobody, and a siding saved nothing; I4 found a Pelletising Plant
> keeping 2,538 vans, 64% of its cost. Now a tonne moves in legs and each leg
> is paid once: the buyer pays the city leg of what it receives, the exporter
> of what it ships, at Haulage's quote; a railed tonne's city leg is a quarter
> truck, the rest by siding. The vans keep the GOODS stream and nothing else
> (Sector.tonnesMoved()).
> 
> NOT A SECTOR, A DIVISION OF ONE. It has no books of its own: it holds its own
> figures, its own price rule and its own fleet's arithmetic, and books through
> Freight (spec 3.1: one balance sheet, one share register, one borrower at the
> bank). The trucks are Freight's TRUCKS pantry, the drivers and mechanics are
> the posts of the Truck Depots it stands on (BuildingsTemplate.getTruckRoom()),
> and its revenue is Freight's other revenue, named "Haulage billed". The
> railway's price rule is the model, on these books instead of those: whatever
> a month costs to run, plus a return on what has been sunk, over the tonnes
> carried (see Freight.TARGET_RETURN, which is the one it reads).
> 
> THE MONTH, which Freight.haul() runs (a bill, then the rest):
> 
>   1. billMonth(): each sector's city-leg tonnes of the month just ended, at
>      the shares that were in force while they moved, at the quote that was in
>      force, for the share Haulage said it would carry and never more than the
>      trucks it had could (a firm short of haulage collected the rest itself).
>   2. closeMonth(): the trucks' diesel on what they carried; their wear; the
>      coming month's need (the city legs of the plant standing, at
>      nameplate: nameplateNeed()); and the reprice.
> 
> The share is struck at the haul (closeMonth()), once, and every sector reads
> it when it is asked for its operating rate (Sector.getHaulageRatio()), as a
> sector's van ratio reads its vans. Read live it ran a fleet bought to its
> need at 99% all month, and over the fleet after its wear it read a fleet
> kept to its need short every month (a 600-month city, FR2's notes); so it
> is struck over the fleet held before the month's wear, which the month's
> order then replaces.
> 
> THE DEAL (FR8). The city's share (getShare()) is one figure, the fleet over
> the whole need, and until FR8 it was every sector's: a giant works landing
> in one industry put its legs into the need and every bulk sector, mines,
> mills and farms alike, ran short together for the months its trucks took to
> arrive (FR2's notes, star 5; Jerus, 2026-10-20: "pool per industry"). Now,
> when the trucks do not suffice, the fleet's room is DEALT per sector at the
> haul (deal()): each sector keeps what it was carried at the last haul (its
> standing demand), the smallest standing demands served first; the growth
> over that, the newest demand, is served last and pro rata from what room is
> left. A giant works' arrival shortens its own sector, not the town's. Each
> sector's share and its standing demand are saved with the rest.

**Uses:** [Good](Good.md) (51), [Sector](Sector.md) (16), [Freight](Freight.md) (13), [Traffic](Traffic.md) (10), [BuildingsTemplate](BuildingsTemplate.md) (10), [InfrastructureManager](InfrastructureManager.md) (8), [Sectors](Sectors.md) (5), [BuildingManager](BuildingManager.md) (3), [JobType](JobType.md) (3), [Markets](Markets.md) (2), [GoodsMarket](GoodsMarket.md) (1)

**Used by (25):** [BondCheck](BondCheck.md), [BuildCard](BuildCard.md), [BuildCardCheck](BuildCardCheck.md), [BuildScreen](BuildScreen.md), [Freight](Freight.md), [FreightView](FreightView.md), [FreightViewCheck](FreightViewCheck.md), [HaulageCheck](HaulageCheck.md), [HealthCheck](HealthCheck.md), [HomeRailCheck](HomeRailCheck.md), [InfrastructureScreen](InfrastructureScreen.md), [LandlordPortCheck](LandlordPortCheck.md), [LongPlaytest](LongPlaytest.md), [MapCheck](MapCheck.md), [MaterialsCheck](MaterialsCheck.md), [ProcessingSector](ProcessingSector.md), [RailCheck](RailCheck.md), [RefineryView](RefineryView.md), [Sector](Sector.md), [SectorScreen](SectorScreen.md), [TradeBand](TradeBand.md), [TradeBandCheck](TradeBandCheck.md), [TruckCheck](TruckCheck.md), [TruckRouteCheck](TruckRouteCheck.md), [TruckSchedule](TruckSchedule.md)

## Sections

| line | section |
|---:|---|
| 220 | · the legs |
| 250 | · the price of a leg |
| 456 | · the month |
| 725 | · the fleet |
| 846 | · the price |
| 874 | · what it is doing |
| 971 | · the month's moves |
| 1104 | · save, reset, reform |

## Enum constants

| line | constant | says |
|---:|---|---|
| 995 | `Haulage.Way.LANDED` |  |
| 995 | `Haulage.Way.SHIPPED` |  |
| 995 | `Haulage.Way.HOME` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 77 | `Haulage.TONNES_A_LOAD` | `25` | Tonnes in a full load: an EU 40 t articulated truck's payload (spec R9). |
| 80 | `Haulage.TONNES_A_TRUCK_MONTH` | `3000` | Tonnes a truck carries in a month on one shift: 6-7 loads a ten-hour shift over 22 days, rounded down to 3,000 (spec 3.2; FR2.md 2 has the sources). |
| 83 | `Haulage.DIESEL_LITRES_PER_100_KM` | `33` | What a laden tractor-trailer burns, litres of diesel a hundred kilometres (spec R3, R23): a van's is twelve (Sector.DIESEL_LITRES_PER_100_KM). |
| 86 | `Haulage.DIESEL_LITRES_A_TONNE` | `Sector.KM_A_LOAD / TONNES_A_LOAD * DIESEL_LITRES_PER_100_KM / 100` | Litres of diesel a tonne carried burns: a city leg's fifty kilometres a load (Sector.KM_A_LOAD) over a load's 25 t at 33 L a 100 km, .66. |
| 89 | `Haulage.STREET_WEAR` | `1.10` | How much faster a truck past the depots' room wears: a tenth, D6's +10% upkeep built as wear because a truck parked on the street is a truck nobody maintains. |
| 98 | `Haulage.DEPOT_ORDER_MARGIN` | `.5` | How far past the depots' room the fleet goes before another depot is ordered: half a depot's room, so a new yard opens half full. |
| 105 | `Haulage.FLEET_RECOVERY_MONTHS` | `24` | Months over which the trucks' cost past their wear is recovered (FR2's ★10, the review): the time a city takes to grow into a new works, so a month's purchases are not one month's bill. |
| 108 | `Haulage.MIN_TRUCKS_A_MONTH` | `1` | The fewest trucks a month's order is capped at (bid()): one - a haulier can always buy a truck, however small its fleet. |
| 111 | `Haulage.DRIVERS_A_TRUCK` | `1` | Drivers a truck the depots keep needs: one, on one shift (spec 3.2; US fleets run 0.93 a truck, the spec's R2). |
| 114 | `Haulage.BILLED_AS` | `"Haulage"` | What a bill is called on the shipper's input line. |
| 1027 | `Haulage.MOVE_FIELDS` | `{ "tonnes", "rail", "sea", "lorry", "truck", "siding", "yard" }` | The seven figures of a move, in the order a save keeps them. |
| 1030 | `Haulage.MOVE_PREFIX` | `"haul.move."` | The save keys' prefix (Freight's extras); the figure names avoid the scaler's intensive words (ScaleCheck), so a scaled copy's tonnes are scaled. |
| 1033 | `Haulage.SHARE_PREFIX` | `"haul.share."` | The save keys' prefix for a sector's dealt share (FR8, deal()), the sector's key after it; "share" in the name keeps the scaler's hands off a share (ScaleCheck). |
| 1036 | `Haulage.STANDING_PREFIX` | `"haul.standing."` | ...and for the tonnes it was dealt, its next standing demand: tonnes, so scaled with the city, and so no intensive word in the name (ScaleCheck). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 116 | `private final Freight freight` |  |
| 119 | `private double quote` | The price of a tonne's city leg in force, in the city's money (D$ a tonne): a money figure, which a reform scales. |
| 122 | `private boolean quoteKnown` | Whether quote has been struck from a month's carriage; until then getQuote() is estimate(), the price at today's prices with no history. |
| 134 | `private double steadyQuote` | THE STEADY PRICE A PLANNER WEIGHS A WORKS AT (FR2, the review): the same rule with the trucks worn in place of the trucks bought, and no scarcity - what a tonne's city leg costs a city that is not growing its fleet th... |
| 135 | `private boolean steadyKnown` |  |
| 150 | `private double fleetOwed` | THE FLEET ACCOUNT (★10, the review): what the trucks bought have cost past the trucks worn, not yet recovered - money, the city's. |
| 153 | `private double capacityInForce` | The tonnes a month the trucks could carry when the month was billed (trucks x TONNES_A_TRUCK_MONTH): the most that bill charged for, and what the price is struck over. |
| 166 | `private double share` | The share of the need the trucks can carry, in force for the month (getShare()): struck at the haul, once, as the need is - over the fleet held before the month's wear, which the month's order replaces, or the fleet s... |
| 169 | `private double need` | The city-leg tonnes the share is read against: the plant standing at the last haul, at nameplate (nameplateNeed()). |
| 172 | `private double needOnSite` | ...and what the plant on site will ask when it stands, struck with it (nameplateNeedOnSite()): what the fleet is bought toward besides. |
| 182 | `private final Map<String, Double> sectorShare` | THE DEAL (FR8; deal()): each sector's own share of its need, struck with the city's at the haul, by sector key, in the sectors' order: every sector but Freight, one whose plant asked nothing at the haul a literal 1. |
| 189 | `private final Map<String, Double> standing` | ...and the tonnes a month of legs each was dealt at the haul (its share of its need): next haul's standing demand, in the need's own units - nameplate legs, not the month's. |
| 192 | `private double newestShare` | The share of the growth over the standing demand that the room left served at the last haul (deal()), 1 with the trucks sufficing; what a sector with no entry reads, which only a save from before FR8 leaves. |
| 195 | `private double standingShare` | The share of the standing demand the fleet covered at the last haul (deal()): below 1 only when it could not cover what the sectors were carried the month before. |
| 204 | `private boolean trucksKnown` | Whether the fleet is a figure Haulage knows. |
| 210 | `private double diplomaOffered` | The share of Freight's DIPLOMA posts it offers this month, the drivers' tier (strikeDrivers()): 1 with every depot full. |
| 213 | `private double tonnes, carried, bill, diesel, dieselImported, dieselLitres, dieselLitresImported, worn, all...` | The month billed, for the screens and the checks. |
| 277 | `LegPrice NONE` | No quote and no legs: every price 0 (a works with no city to weigh it in). |
| 416 | `private final double[] exportShares` | Each good's share of the city's units shipped abroad, and of its units bought landed from abroad, struck at the last haul (strikeTrade()). |
| 419 | `private final boolean[] traded` | ...and whether each was struck from trade, or is the no-trade default. |
| 997 | `private final String direction, label` |  |
| 1039 | `private final Map<String, Map<String, double[]>> moves` | The month billed's moves: sector key, then "GOOD.WAY", then the seven figures. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 74 | 1179 | **type** `public final class Haulage` | Freight's Haulage division: the trucks that carry the city leg of every bulk tonne (FR2, 2026-10-18; docs/studies/spec-freight-and-infrastructure.md 2, 3.2, 3.3). |
| 215 | 4 | `Haulage(Freight freight)` |  |

### the legs (lines 220-249)

| line | len | member | says |
|---:|---:|---|---|
| 237 | 7 | `public static double legTonnes(Sector s, Good g, double railShare, double seaShare, double homeShare, double quayShare)` | The city-leg truck tonnes of a sector's month in progress, of one good. |
| 246 | 3 | `public static double legTonnes(Sector s, Good g, double railShare)` | The legs with the railway at the boundary alone (FR2): no home share and no port yard, to the bit what it was before FR6. |

### the price of a leg (lines 250-455)

| line | len | member | says |
|---:|---:|---|---|
| 269 | 13 | **type** `public interface LegPrice` | What the city leg of one tonne costs a works the planner weighs (FR2, spec 3.5): the price a works is weighed with in place of its lorries. |
| 271 | 1 | `double in(Good g)` _(in Haulage.LegPrice)_ | D$ the city leg of one tonne of `g` received costs. |
| 274 | 1 | `double out(Good g)` _(in Haulage.LegPrice)_ | D$ the city leg of one tonne of `g` made costs on average. |
| 295 | 8 | `public LegPrice legPrice()` | Haulage's price as a works is weighed with it, live from the city: the steady quote (getSteadyQuote(): the rule on the trucks worn, the estimate before the first month's carriage), the railway's share in force for eac... |
| 308 | 8 | `private double inWeight(Good g)` | The city leg of a received tonne of `g`, in truck tonnes: 1 less the siding's share of its imported, railed part, less the port yard's part of what crossed a berth and the siding's part of what the railway carries of ... |
| 318 | 5 | `private double outWeight(Good g)` | ...and of a made tonne, on average: the share the city exported, less the siding's share of its railed part and the port yard's (FR6). |
| 325 | 6 | `private double railWeightIn(Good g)` | The railed part of a received tonne's leg (FR6), in tonnes the railway charges for: the siding's part of the home tonnes and the yard's part of the quay's; 0 for anything but BULK. |
| 333 | 5 | `private double railWeightOut(Good g)` | ...and of a made tonne, on average: the share the city exported, of the yard's part of the quay (FR6). |
| 346 | 3 | `public double nameplateNeed(Sectors sectors)` | The city-leg tonnes a month the city's standing plant asks for at nameplate (FR2; closeMonth()): every sector's but Freight's BULK inputs and outputs at capacity, less what a works hands itself (captive goods, Sector.... |
| 356 | 4 | `public double nameplateNeed(Sector s)` | ...and one sector's share of it (FR8: the deal weighs each sector's legs): its BULK inputs and outputs at nameplate, as nameplateNeed() weighs them; 0 for Freight, which carries and is carried by nobody, and for no se... |
| 367 | 10 | `private double nameplateNeed(Sectors sectors, Map<String, Double> bySector)` | The city's nameplate need, and, in `bySector` (when given), each sector's own legs in `sectors.all()` order. |
| 379 | 22 | `private double legsAtNameplate(Sector s, double[] total)` | One sector's legs at nameplate, adding each good's term to `total[0]` as it is met when a total is given. |
| 422 | 21 | `void strikeTrade(Sectors sectors)` | Strikes the trade's shares from the sectors' month (above): called at the haul, before the need. |
| 445 | 1 | `private double exportShare(Good g)` | Made here and shipped abroad, struck at the last haul: what share of a made tonne's leg its maker pays. |
| 448 | 1 | `private double importShare(Good g)` | ...and landed from abroad: what share of a received tonne's leg the railway's siding may relieve. |
| 451 | 1 | `private static double unit(double x)` | x clamped to [0, 1]; 0 if it is not a number. |
| 454 | 1 | `private static double clean(double x)` | x not below zero; 0 if it is not a number. |

### the month (lines 456-724)

| line | len | member | says |
|---:|---:|---|---|
| 480 | 30 | `void billMonth(Map<Sector, Double> legs)` | Step 1 of the month: bills last month's city legs, sector by sector. |
| 515 | 117 | `void closeMonth(Sectors sectors)` | Steps 2 to 5: the diesel, the wear, the coming month's need, and the reprice. |
| 671 | 53 | `private void deal(Map<String, Double> needBy, double capacity)` | Deals the fleet's room per sector, struck with the city's share at the haul (closeMonth()): each sector's own share of its own need, the newest demand last (FR8). |

### the fleet (lines 725-845)

| line | len | member | says |
|---:|---:|---|---|
| 728 | 1 | `public double trucks()` | Trucks owned: Freight's TRUCKS pantry. |
| 731 | 1 | `public double trucksNeeded()` | ...and how many the coming month's need asks for. |
| 734 | 4 | `public double depotRoom()` | Trucks the depots standing keep: the room of Freight's finished buildings. |
| 740 | 1 | `public double onStreet()` | Trucks past the depots' room, parked on the street (D6): they run, unposted, at STREET_WEAR. |
| 748 | 7 | `public double bid()` | What Freight asks the market for: the gap between the fleet the need asks for and the fleet it has, at most an eighth of the need a month (Sector.FLEET_DELIVERY_MONTHS, the vans' rule, so a fleet is built up and not c... |
| 766 | 1 | `public double trucksAsked()` | The fleet the month's order is toward: the trucks the plant standing needs and the plant on site will (FR2, the review). |
| 769 | 9 | `public double nameplateNeedOnSite(Sectors sectors)` | The legs at nameplate the city's works on site will ask when they stand: nameplateNeed()'s weights, over Freight's every other sector's buildings on site. |
| 780 | 14 | `private double templateLegs(BuildingsTemplate b)` | One building's legs a month at nameplate, as nameplateNeed() weighs a sector's: its BULK inputs and outputs, VANS and TRUCKS aside. |
| 803 | 15 | `public BuildingsTemplate depotToOrder()` | The Truck Depot to order, or null: the first of Freight's templates with room for trucks, when the fleet is known, none is on site, and the fleet has passed the depots' room by DEPOT_ORDER_MARGIN of a depot. |
| 835 | 7 | `public void strikeDrivers()` | Strikes the month's drivers: Freight's DIPLOMA posts less a driver for each place in the depots no truck fills. |
| 844 | 1 | `public double offeredShare(JobType type)` | The share of Freight's posts of a job type on offer this month: the drivers' tier's as struck, every other's whole. |

### the price (lines 846-873)

| line | len | member | says |
|---:|---:|---|---|
| 857 | 16 | `public double estimate()` | What a tonne's city leg costs at today's prices with no history behind it: the quote before the first month's carriage has struck one. |

### what it is doing (lines 874-970)

| line | len | member | says |
|---:|---:|---|---|
| 877 | 1 | `public double getQuote()` | The quote in force; the estimate while no month has struck one. |
| 880 | 1 | `public double getSteadyQuote()` | The steady price a planner weighs a works' legs at (legPrice()): the rule on the trucks worn, no scarcity; the estimate until struck. |
| 881 | 1 | `public boolean isQuoteKnown()` |  |
| 891 | 1 | `public double getShare()` | The share of the city-leg tonnes the trucks can carry: the trucks held at the haul, at TONNES_A_TRUCK_MONTH each, over the need - a literal 1 when they suffice, and 1 with no need (a city that has never needed a truck... |
| 900 | 1 | `public double getShare(Sector s)` | The share of its own need that the sector's industry was dealt at the last haul (FR8; deal()): the one its operating rate and its bill read, a literal 1 for one that asked nothing. |
| 903 | 5 | `public double shareOf(String key)` | The same by sector key: the share dealt it at the last haul, the newest demand's if it was dealt none (a save from before FR8), the city's for no key. |
| 910 | 1 | `public double getNewestShare()` | The share of the growth over the standing demand that the room left at the last haul served; 1 when the trucks sufficed. |
| 913 | 1 | `public double getStandingShare()` | The share of the standing demand, what the sectors were carried the month before, that the fleet covered at the last haul; 1 when all of it. |
| 916 | 4 | `public double getStanding(String key)` | The tonnes a month of legs the sector was dealt at the last haul, its next standing demand; 0 for a sector with none. |
| 922 | 1 | `public Map<String, Double> sectorShares()` | Every sector's share dealt at the last haul, by key in the sectors' order: a view, not a copy. |
| 925 | 1 | `public double getNeed()` | The city-leg tonnes the share is read against, and the tonnes a month the trucks could carry when the month was billed. |
| 928 | 1 | `public double getNeedOnSite()` | ...and the legs at nameplate of the works on site, struck with it: what trucksAsked() adds. |
| 929 | 1 | `public double getCapacityInForce()` |  |
| 932 | 1 | `public double getTonnes()` | the month billed |
| 933 | 1 | `public double getCarried()` |  |
| 934 | 1 | `public double getBill()` |  |
| 935 | 1 | `public double getDiesel()` |  |
| 936 | 1 | `public double getDieselImported()` |  |
| 939 | 1 | `public double getDieselLitres()` | The litres of diesel the last haul drew: none after a load until the next, as the railway's. |
| 942 | 1 | `public double getDieselLitresImported()` | ...and the litres of it the world sold (the refiners' tanks having none left): the refinery's pictogram's (RefineryView). |
| 945 | 1 | `public double getWorn()` | The trucks worn out in the month billed. |
| 948 | 1 | `public double getAllowed()` | What the month had to bring in (the rule's running cost plus the return on the rate base) and the scarcity it was priced at the end of. |
| 949 | 1 | `public double getTightness()` |  |
| 960 | 5 | `public double getTarget()` | The rule's target for a tonne's city leg, as the last reprice walked the quote toward it (FR4, for the Freight screen's quote strip): what the month had to bring in (getAllowed()) over what the trucks could carry (get... |
| 967 | 3 | `private double rawTarget()` | The rule's target a tonne before its guards: what the month had to bring in over the trucks' capacity, at the scarcity (closeMonth(), getTarget()). |

### the month's moves (lines 971-1103)

| line | len | member | says |
|---:|---:|---|---|
| 994 | 19 | **type** `public enum Way` | Which way a sector's tonnes of a good went: landed from the world, shipped to it, or bought from a works of the city (no edge crossed). |
| 999 | 4 | `Way(String direction, String label)` _(in Haulage.Way)_ |  |
| 1005 | 1 | `public String direction()` _(in Haulage.Way)_ | "in" or "out": which side of the works the tonnes were on. |
| 1008 | 1 | `public String label()` _(in Haulage.Way)_ | How the page says it: "bought abroad", "sold abroad", "bought in the city". |
| 1011 | 1 | `public boolean crossed()` _(in Haulage.Way)_ | Whether the tonnes crossed the city boundary (they have a long haul). |
| 1023 | 2 | **type** `public record Move(Way way, Good good, double tonnes, double rail, double sea, double lorry, double truck, ...` | One good a sector moved in one way in the month billed, in tonnes. |
| 1042 | 3 | `void beginMoves()` | Starts the month's tally (Freight.haul(), where truckIn and its kin are cleared). |
| 1055 | 28 | `void tallyMoves(String sector, Good g, boolean bulk, double landed, double shipped, double bought, double railShare, double sea...` | Tallies one sector's one good of the month being billed, from the very numbers Freight.haul() weighs it with (legTonnes()'s own expression, per direction): `landed`, `shipped` and `bought` are units, `rail` the railwa... |
| 1084 | 5 | `private void addMove(String sector, Good g, Way way, double...figures)` |  |
| 1091 | 12 | `public List<Move> movesOf(String sector)` | A sector's moves of the month billed, by good (the Good order) and then way (landed, shipped, home); empty for a sector that moved no freight, and for one this haulage does not know. |

### save, reset, reform (lines 1104-1252)

| line | len | member | says |
|---:|---:|---|---|
| 1107 | 41 | `public void save(Map<String, Double> extras)` | Haulage's state into Freight's extras (keys haul.*): the dieselLitres are not saved, as the railway's are not. |
| 1150 | 42 | `public void restore(Map<String, Double> extras)` | ...and back. |
| 1194 | 28 | `private void restoreMoves(Map<String, Double> extras)` | The month's moves back from their keys, read from the right (figure, WAY, GOOD; what is left is the sector, whose name may hold a dot); a key this build cannot place is left out. |
| 1224 | 17 | `public void reset()` | Back to a city that has never needed a truck. |
| 1243 | 9 | `public void redenominate(double scale)` | Its money in the new unit (Denomination): the quote, the bill, the diesel and what the month was allowed. |

