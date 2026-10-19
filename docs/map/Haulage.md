# Haulage.java - 777 lines · 48 methods · 9 constants · sectors

`ham/citybuildersim/sectors/Haulage.java` - generated 2026-10-19 by CodeMap; line numbers are as of that run.

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
> The share is pushed to nobody, and it is not struck: every sector reads it
> when it is asked for its operating rate (Sector.getHaulageRatio()), off the
> trucks Freight holds then and the need the last haul set, as a sector's van
> ratio reads its vans. Struck at the haul it read the trucks after a month's
> wear and before the month's purchases, so a fleet bought to its need still
> ran at 99% all month (a 600-month city, FR2's notes).

**Uses:** [Good](Good.md) (41), [Sector](Sector.md) (11), [BuildingsTemplate](BuildingsTemplate.md) (10), [Traffic](Traffic.md) (8), [Freight](Freight.md) (7), [Sectors](Sectors.md) (4), [InfrastructureManager](InfrastructureManager.md) (3), [BuildingManager](BuildingManager.md) (3), [JobType](JobType.md) (3), [Markets](Markets.md) (2), [GoodsMarket](GoodsMarket.md) (1)

**Used by (10):** [BondCheck](BondCheck.md), [Freight](Freight.md), [HaulageCheck](HaulageCheck.md), [HealthCheck](HealthCheck.md), [LongPlaytest](LongPlaytest.md), [MaterialsCheck](MaterialsCheck.md), [ProcessingSector](ProcessingSector.md), [RailCheck](RailCheck.md), [RefineryView](RefineryView.md), [Sector](Sector.md)

## Sections

| line | section |
|---:|---|
| 178 | · the legs |
| 199 | · the price of a leg |
| 337 | · the month |
| 497 | · the fleet |
| 615 | · the price |
| 643 | · what it is doing |
| 688 | · save, reset, reform |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 61 | `Haulage.TONNES_A_LOAD` | `25` | Tonnes in a full load: an EU 40 t articulated truck's payload (spec R9). |
| 64 | `Haulage.TONNES_A_TRUCK_MONTH` | `3000` | Tonnes a truck carries in a month on one shift: 6-7 loads a ten-hour shift over 22 days, rounded down to 3,000 (spec 3.2; FR2.md 2 has the sources). |
| 67 | `Haulage.DIESEL_LITRES_PER_100_KM` | `33` | What a laden tractor-trailer burns, litres of diesel a hundred kilometres (spec R3, R23): a van's is twelve (Sector.DIESEL_LITRES_PER_100_KM). |
| 70 | `Haulage.DIESEL_LITRES_A_TONNE` | `Sector.KM_A_LOAD / TONNES_A_LOAD * DIESEL_LITRES_PER_100_KM / 100` | Litres of diesel a tonne carried burns: a city leg's fifty kilometres a load (Sector.KM_A_LOAD) over a load's 25 t at 33 L a 100 km, .66. |
| 73 | `Haulage.STREET_WEAR` | `1.10` | How much faster a truck past the depots' room wears: a tenth, D6's +10% upkeep built as wear because a truck parked on the street is a truck nobody maintains. |
| 82 | `Haulage.DEPOT_ORDER_MARGIN` | `.5` | How far past the depots' room the fleet goes before another depot is ordered: half a depot's room, so a new yard opens half full. |
| 89 | `Haulage.FLEET_RECOVERY_MONTHS` | `24` | Months over which the trucks' cost past their wear is recovered (FR2's ★10, the review): the time a city takes to grow into a new works, so a month's purchases are not one month's bill. |
| 92 | `Haulage.DRIVERS_A_TRUCK` | `1` | Drivers a truck the depots keep needs: one, on one shift (spec 3.2; US fleets run 0.93 a truck, the spec's R2). |
| 95 | `Haulage.BILLED_AS` | `"Haulage"` | What a bill is called on the shipper's input line. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 97 | `private final Freight freight` |  |
| 100 | `private double quote` | The price of a tonne's city leg in force, in the city's money (D$ a tonne): a money figure, which a reform scales. |
| 103 | `private boolean quoteKnown` | Whether quote has been struck from a month's carriage; until then getQuote() is estimate(), the price at today's prices with no history. |
| 115 | `private double steadyQuote` | THE STEADY PRICE A PLANNER WEIGHS A WORKS AT (FR2, the review): the same rule with the trucks worn in place of the trucks bought, and no scarcity - what a tonne's city leg costs a city that is not growing its fleet th... |
| 116 | `private boolean steadyKnown` |  |
| 131 | `private double fleetOwed` | THE FLEET ACCOUNT (★10, the review): what the trucks bought have cost past the trucks worn, not yet recovered - money, the city's. |
| 134 | `private double capacityInForce` | The tonnes a month the trucks could carry when the month was billed (trucks x TONNES_A_TRUCK_MONTH): the most that bill charged for, and what the price is struck over. |
| 147 | `private double share` | The share of the need the trucks can carry, in force for the month (getShare()): struck at the haul, once, as the need is - over the fleet held before the month's wear, which the month's order replaces, or the fleet s... |
| 150 | `private double need` | The city-leg tonnes the share is read against: the plant standing at the last haul, at nameplate (nameplateNeed()). |
| 153 | `private double needOnSite` | ...and what the plant on site will ask when it stands, struck with it (nameplateNeedOnSite()): what the fleet is bought toward besides. |
| 162 | `private boolean trucksKnown` | Whether the fleet is a figure Haulage knows. |
| 168 | `private double diplomaOffered` | The share of Freight's DIPLOMA posts it offers this month, the drivers' tier (strikeDrivers()): 1 with every depot full. |
| 171 | `private double tonnes, carried, bill, diesel, dieselImported, dieselLitres, dieselLitresImported, worn, all...` | The month billed, for the screens and the checks. |
| 224 | `LegPrice NONE` | No quote and no legs: every price 0 (a works with no city to weigh it in). |
| 297 | `private final double[] exportShares` | Each good's share of the city's units shipped abroad, and of its units bought landed from abroad, struck at the last haul (strikeTrade()). |
| 300 | `private final boolean[] traded` | ...and whether each was struck from trade, or is the no-trade default. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 58 | 720 | **type** `public final class Haulage` | Freight's Haulage division: the trucks that carry the city leg of every bulk tonne (FR2, 2026-10-18; docs/studies/spec-freight-and-infrastructure.md 2, 3.2, 3.3). |
| 173 | 4 | `Haulage(Freight freight)` |  |

### the legs (lines 178-198)

| line | len | member | says |
|---:|---:|---|---|
| 192 | 6 | `public static double legTonnes(Sector s, Good g, double railShare)` | The city-leg truck tonnes of a sector's month in progress, of one good. |

### the price of a leg (lines 199-336)

| line | len | member | says |
|---:|---:|---|---|
| 216 | 13 | **type** `public interface LegPrice` | What the city leg of one tonne costs a works the planner weighs (FR2, spec 3.5): the price a works is weighed with in place of its lorries. |
| 218 | 1 | `double in(Good g)` _(in Haulage.LegPrice)_ | D$ the city leg of one tonne of `g` received costs. |
| 221 | 1 | `double out(Good g)` _(in Haulage.LegPrice)_ | D$ the city leg of one tonne of `g` made costs on average. |
| 237 | 7 | `public LegPrice legPrice()` | Haulage's price as a works is weighed with it, live from the city: the steady quote (getSteadyQuote(): the rule on the trucks worn, the estimate before the first month's carriage), the railway's share in force for eac... |
| 246 | 4 | `private double inWeight(Good g)` | The city leg of a received tonne of `g`, in truck tonnes: 1 less the siding's share of its imported, railed part; 0 for anything but BULK. |
| 252 | 4 | `private double outWeight(Good g)` | ...and of a made tonne, on average: the share the city exported, less the siding's share of its railed part. |
| 264 | 18 | `public double nameplateNeed(Sectors sectors)` | The city-leg tonnes a month the city's standing plant asks for at nameplate (FR2; closeMonth()): every sector's but Freight's BULK inputs and outputs at capacity, less what a works hands itself (captive goods, Sector.... |
| 303 | 21 | `void strikeTrade(Sectors sectors)` | Strikes the trade's shares from the sectors' month (above): called at the haul, before the need. |
| 326 | 1 | `private double exportShare(Good g)` | Made here and shipped abroad, struck at the last haul: what share of a made tonne's leg its maker pays. |
| 329 | 1 | `private double importShare(Good g)` | ...and landed from abroad: what share of a received tonne's leg the railway's siding may relieve. |
| 332 | 1 | `private static double unit(double x)` | x clamped to [0, 1]; 0 if it is not a number. |
| 335 | 1 | `private static double clean(double x)` | x not below zero; 0 if it is not a number. |

### the month (lines 337-496)

| line | len | member | says |
|---:|---:|---|---|
| 357 | 21 | `void billMonth(Map<Sector, Double> legs)` | Step 1 of the month: bills last month's city legs, sector by sector. |
| 383 | 113 | `void closeMonth(Sectors sectors)` | Steps 2 to 5: the diesel, the wear, the coming month's need, and the reprice. |

### the fleet (lines 497-614)

| line | len | member | says |
|---:|---:|---|---|
| 500 | 1 | `public double trucks()` | Trucks owned: Freight's TRUCKS pantry. |
| 503 | 1 | `public double trucksNeeded()` | ...and how many the coming month's need asks for. |
| 506 | 4 | `public double depotRoom()` | Trucks the depots standing keep: the room of Freight's finished buildings. |
| 512 | 1 | `public double onStreet()` | Trucks past the depots' room, parked on the street (D6): they run, unposted, at STREET_WEAR. |
| 520 | 5 | `public double bid()` | What Freight asks the market for: the gap between the fleet the need asks for and the fleet it has, at most an eighth of the need a month (Sector.FLEET_DELIVERY_MONTHS, the vans' rule, so a fleet is built up and not c... |
| 536 | 1 | `public double trucksAsked()` | The fleet the month's order is toward: the trucks the plant standing needs and the plant on site will (FR2, the review). |
| 539 | 9 | `public double nameplateNeedOnSite(Sectors sectors)` | The legs at nameplate the city's works on site will ask when they stand: nameplateNeed()'s weights, over Freight's every other sector's buildings on site. |
| 550 | 14 | `private double templateLegs(BuildingsTemplate b)` | One building's legs a month at nameplate, as nameplateNeed() weighs a sector's: its BULK inputs and outputs, VANS and TRUCKS aside. |
| 573 | 15 | `public BuildingsTemplate depotToOrder()` | The Truck Depot to order, or null: the first of Freight's templates with room for trucks, when the fleet is known, none is on site, and the fleet has passed the depots' room by DEPOT_ORDER_MARGIN of a depot. |
| 605 | 6 | `public void strikeDrivers()` | Strikes the month's drivers: Freight's DIPLOMA posts less a driver for each place in the depots no truck fills. |
| 613 | 1 | `public double offeredShare(JobType type)` | The share of Freight's posts of a job type on offer this month: the drivers' tier's as struck, every other's whole. |

### the price (lines 615-642)

| line | len | member | says |
|---:|---:|---|---|
| 626 | 16 | `public double estimate()` | What a tonne's city leg costs at today's prices with no history behind it: the quote before the first month's carriage has struck one. |

### what it is doing (lines 643-687)

| line | len | member | says |
|---:|---:|---|---|
| 646 | 1 | `public double getQuote()` | The quote in force; the estimate while no month has struck one. |
| 649 | 1 | `public double getSteadyQuote()` | The steady price a planner weighs a works' legs at (legPrice()): the rule on the trucks worn, no scarcity; the estimate until struck. |
| 650 | 1 | `public boolean isQuoteKnown()` |  |
| 659 | 1 | `public double getShare()` | The share of the city-leg tonnes the trucks can carry: the trucks held now, at TONNES_A_TRUCK_MONTH each, over the need - a literal 1 when they suffice, and 1 with no need (a city that has never needed a truck runs at... |
| 662 | 1 | `public double getNeed()` | The city-leg tonnes the share is read against, and the tonnes a month the trucks could carry when the month was billed. |
| 665 | 1 | `public double getNeedOnSite()` | ...and the legs at nameplate of the works on site, struck with it: what trucksAsked() adds. |
| 666 | 1 | `public double getCapacityInForce()` |  |
| 669 | 1 | `public double getTonnes()` | the month billed |
| 670 | 1 | `public double getCarried()` |  |
| 671 | 1 | `public double getBill()` |  |
| 672 | 1 | `public double getDiesel()` |  |
| 673 | 1 | `public double getDieselImported()` |  |
| 676 | 1 | `public double getDieselLitres()` | The litres of diesel the last haul drew: none after a load until the next, as the railway's. |
| 679 | 1 | `public double getDieselLitresImported()` | ...and the litres of it the world sold (the refiners' tanks having none left): the refinery's pictogram's (RefineryView). |
| 682 | 1 | `public double getWorn()` | The trucks worn out in the month billed. |
| 685 | 1 | `public double getAllowed()` | What the month had to bring in (the rule's running cost plus the return on the rate base) and the scarcity it was priced at the end of. |
| 686 | 1 | `public double getTightness()` |  |

### save, reset, reform (lines 688-777)

| line | len | member | says |
|---:|---:|---|---|
| 691 | 28 | `public void save(Map<String, Double> extras)` | Haulage's state into Freight's extras (keys haul.*): the dieselLitres are not saved, as the railway's are not. |
| 721 | 30 | `public void restore(Map<String, Double> extras)` | ...and back. |
| 753 | 13 | `public void reset()` | Back to a city that has never needed a truck. |
| 768 | 9 | `public void redenominate(double scale)` | Its money in the new unit (Denomination): the quote, the bill, the diesel and what the month was allowed. |

