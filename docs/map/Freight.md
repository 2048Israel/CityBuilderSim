# Freight.java - 2,166 lines · 107 methods · 13 constants · sectors

`ham/citybuildersim/sectors/Freight.java` - generated 2026-11-13 by CodeMap; line numbers are as of that run.

> The Freight sector: the city's carrier, in the railway's slot (FR2, 2026-10-18;
> docs/studies/spec-freight-and-infrastructure.md 3.1, D1). Its Rail division is the
> railway, THE TWELFTH SECTOR (2026-09-16, Jerus's call), whose design follows.
> Its Haulage division (Haulage, from FR2) is the trucks that carry the city leg
> of every bulk tonne; it keeps its own figures and price rule and books through
> this sector, and the two divisions share these buildings' costs by whose they
> are (haulageShareOf()). Its Ports division (PortDivision, from FR3) is the
> crews of the city's berths, who bill the handling and pay the city its dues.
> 
> Every price in this game has always had freight inside it. A tonne of steel
> lands at $1,284 and leaves at $847, and three quarters of that $437 wedge is
> the cost of moving it - see Good.baseFreight(), which split the two apart on
> 2026-09-16 without moving a single price. Until today that freight was a
> constant and it was paid to nobody: the money simply left with the cargo.
> 
> THIS IS THE BUSINESS THAT WANTS TO BE PAID IT. Jerus: "the freight cant reach
> zero cause freight needs profit, its its own sector, you build roads
> obviously but rail is its own sector, it wants and will do everything
> possible to stay profitable and maximize profits. freight money goes to that
> sector."
> 
> =======================================================================
> HOW THE MONEY ACTUALLY MOVES, which is the part worth reading twice
> =======================================================================
> 
> There were two ways to write this and only one of them keeps the audit
> honest.
> 
>   THE ONE NOT TAKEN: leave the whole freight bill inside the import and
>   export band, narrow the band by what rail saves, and pay the railway out
>   of it. That means a payment which the books record as going abroad is in
>   fact going to a company in the city - so either the money audit sees
>   money vanish across the boundary and come back from nowhere, or the trade
>   settlement grows a special case for one sector. Both are the kind of
>   plumbing that is wrong six months later and nobody remembers why.
> 
>   THE ONE TAKEN: the band carries ONLY what the lorries still move, and the
>   railway BILLS THE SHIPPER, at home, like any other supplier of a service.
>   GoodsMarket.setFreightFactor() is handed the lorries' remaining share, so
>   a good entirely on rail leaves the band with no freight in it at all; and
>   Sector.billForService() puts the railway's invoice on the shipper's input
>   line, where freight belongs in anybody's accounts. Same net cost to the
>   shipper, money that never leaves the city, and the VAT falls out for free
>   because the ledger already credits a purchase at the supplier's own rate.
> 
> What the shipper pays per unit, either way:
> 
>      (1 - share) x baseFreight      still to the lorries, inside the band
>    +  share x quote x baseFreight   to the railway, on its invoice
>    =  baseFreight x (1 - share x (1 - quote))
> 
> so a railway carrying everything at 60% of the lorry rate takes 40% off the
> city's freight bill and keeps the other 60% at home. In a played city that
> is not a rounding difference: the 4,000-month playtest ships 1,166,821
> tonnes a month and pays $372m of freight on it, against a GDP of $592m. A
> city that hauls its own freight is a materially different city.
> 
> =======================================================================
> WHAT IT CHARGES
> =======================================================================
> ... (58 more lines in the source)

**Uses:** [Traffic](Traffic.md) (35), [Good](Good.md) (33), [Ports](Ports.md) (31), [BuildingsTemplate](BuildingsTemplate.md) (29), [BusinessInvestment](BusinessInvestment.md) (15), [JobType](JobType.md) (12), [Formats](Formats.md) (8), [Sector](Sector.md) (7), [Haulage](Haulage.md) (6), [PortDivision](PortDivision.md) (6), [Sectors](Sectors.md) (5), [Markets](Markets.md) (5), [Game](Game.md) (5), [InfrastructureManager](InfrastructureManager.md) (4), [BuildingManager](BuildingManager.md) (3), [GoodsMarket](GoodsMarket.md) (2), [SectorStatements](SectorStatements.md) (2), [PriceLevel](PriceLevel.md) (1), [BuildingType](BuildingType.md) (1)

**Used by (44):** [BuildCardCheck](BuildCardCheck.md), [BuildScreen](BuildScreen.md), [BuildingVisual](BuildingVisual.md), [EconomyManager](EconomyManager.md), [EquipmentCheck](EquipmentCheck.md), [FittingCheck](FittingCheck.md), [Forms1900Check](Forms1900Check.md), [FreightView](FreightView.md), [FreightViewCheck](FreightViewCheck.md), [FuelSplit](FuelSplit.md), [Game](Game.md), [GdpCheck](GdpCheck.md), [Haulage](Haulage.md), [HaulageCheck](HaulageCheck.md), [HealthCheck](HealthCheck.md), [HomeRailCheck](HomeRailCheck.md), [InfrastructureScreen](InfrastructureScreen.md), [LandlordPortCheck](LandlordPortCheck.md), [LongPlaytest](LongPlaytest.md), [MapCheck](MapCheck.md), [OilCheck](OilCheck.md), [PolicyPreview](PolicyPreview.md), [PortCheck](PortCheck.md), [PortDivision](PortDivision.md), [Ports](Ports.md), [RailCheck](RailCheck.md), [RailDepotCheck](RailDepotCheck.md), [RefineryCheck](RefineryCheck.md), [RefineryView](RefineryView.md), [RefineryViewCheck](RefineryViewCheck.md), [SaveFileCheck](SaveFileCheck.md), [Sector](Sector.md), [SectorScreen](SectorScreen.md), [Sectors](Sectors.md), [SpreadPlanner](SpreadPlanner.md), [TradeBand](TradeBand.md), [TradeBandCheck](TradeBandCheck.md), [TrainSchedule](TrainSchedule.md), [TruckCheck](TruckCheck.md), [TruckRouteCheck](TruckRouteCheck.md), [TruckSchedule](TruckSchedule.md), [WorldData](WorldData.md), [WorldDataCheck](WorldDataCheck.md), [WorldMarket](WorldMarket.md)

## Sections

| line | section |
|---:|---|
| 475 | THE MONTH |
| 893 | · the fleet |
| 939 | · the stations (FR7) |
| 1140 | · the Haulage division's seam (FR2) |
| 1323 | · the Ports division's seam (FR3) |
| 1414 | · what it is doing, for everyone else |
| 1570 | PLANNING - the freight nobody is carrying |
| 1890 | · the books |
| 1925 | · the screen |
| 2021 | · save/load |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 157 | `Freight.RAIL_FLOOR` | `.30` | The cheapest the railway will ever quote, as a share of the lorry rate. |
| 160 | `Freight.OPENING_QUOTE` | `.60` | What a city with no railway assumes the first line could charge. |
| 181 | `Freight.MIN_LINE_UTILISATION` | `.60` | How much of a line's nameplate has to be freight nobody is carrying before it is worth laying. |
| 200 | `Freight.TONNES_PER_SET` | `2500` | Tonnes a month one wagon set can haul. |
| 203 | `Freight.SET_LIFE_MONTHS` | `240` | How long a wagon set lasts before it is scrap. |
| 234 | `Freight.TARGET_RETURN` | `.012` | What it tries to earn a month ON THE TRACK IT HAS SUNK, before scarcity. |
| 237 | `Freight.MAX_SCARCITY_MULTIPLE` | `1.6` | How far a network that cannot keep up can push the quote above cost-plus. |
| 240 | `Freight.REPRICE_SPEED` | `.25` | How fast the quote walks to where it should be. |
| 276 | `Freight.WORLD_FUEL_PER_TONNE` | `.03 * PriceLevel.FACTOR` | What a tonne of haulage burns, IN THE WORLD'S MONEY. |
| 288 | `Freight.FUEL_LITRES_PER_TONNE` | `18` | Litres of fuel a tonne hauled burns: eighteen. |
| 293 | `Freight.PORT_YARD_SHARE` | `.45` | The most of the BULK tonnes that crossed a berth the port yard takes to rail (FR6, D7): Montreal's rail share, on the port's own railway on the quay (spec R70). |
| 299 | `Freight.CITY_LITRES_A_TONNE` | `Haulage.DIESEL_LITRES_A_TONNE / 3` | Litres of diesel the railway burns on a city tonne (FR6): a third of a truck's city leg (Haulage.DIESEL_LITRES_A_TONNE), as rail costs about a third of a truck a tonne-kilometre (spec R50, R51); FUEL_LITRES_PER_TONNE ... |
| 1059 | `Freight.BULK_WORKS_MIN_TONNES` | `TONNES_PER_SET * MIN_LINE_UTILISATION` | The bulk a building has to make and use between them, tonnes a month at nameplate, to be a bulk works (FR7): MIN_LINE_UTILISATION of a wagon set's month, as a line is not laid below that share of its nameplate. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 302 | `private double quote` | The share of the lorry rate it is charging, today. |
| 309 | `private final double[] carried` | What it is actually carrying, by stream - the share in force for the month now running, and therefore the share the band was set from and the share the invoice will be raised at when that month is struck. |
| 323 | `private double homeShare, quayShare` | THE CITY'S OWN BULK ON THE RAILWAY (FR6, D4, D7; docs/queue/FR6.md 2): the share of the offer of the bulk bought at home (homeShare) and of the port yard's quay bulk (quayShare) the railway carries, in force for the m... |
| 326 | `private double rTonnes, rHauled, rTruckBill, rHaulage, rFuel` | the month just billed, for the screens |
| 337 | `private double rHomeOffered, rHomeRailed, rHomeBill, rQuayRailed, rCityLitres` | The city's own bulk in the month billed (FR6), for the screens: the city tonnes the railway was offered at the haul (the siding's part of the home tonnes, and the yard's part of the quay's when a yard stands), the hom... |
| 350 | `private double rBulkOffered` | The BULK stream's part of rTonnes (FR7): the bulk the railway was offered, the berths' first share taken off, as rTonnes is. |
| 359 | `private double rCrossed` | Every tonne of freight that crossed the boundary in the month (0.7.86): rTonnes is what the railway was offered, the berths that go before it having taken theirs (ham.citybuildersim.Ports) - the figure its tightness, ... |
| 362 | `private double rOfferedBill` | ...and what a lorry would have charged for the tonnes the railway was offered (0.7.86): the lorry bill less the berths' first share of it; the whole bill with no terminal. |
| 372 | `private final double[] railIn` | The month billed's railed tonnes by cargo kind, landed and shipped (0.8.2, batch FR1; spec-freight 3.3 and 4.4): each owner's tonnes of a good across the boundary at the railway's share in force, under the good's kind... |
| 382 | `private double truckIn, truckOut, truckHome` | The month billed's city-leg truck tonnes by load, for the map's trucks (0.8.8, batch FR5; spec-freight 4.7): the bulk tonnes landed, shipped and bought at home, each at the railway's share in force as Haulage.legTonne... |
| 385 | `private double rFuelImported` | The part of the month's fuel the world sold it (0.7.62). |
| 388 | `private double rFuelLitres, rFuelLitresImported` | ...and the month's diesel in litres, and the litres of it the world sold (0.7.95, batch O11: the refinery's pictogram's railway). |
| 389 | `private double rCapacity, rTightness, rFx` |  |
| 392 | `private double rRunning` | ...and the part of rAllowed that is the railway's running cost (FR2): what it earned is its bill less this, now that the sector's income is the trucks' too. |
| 400 | `private double rPaidAbroad` | The part of the month's lorry bill that went abroad with the cargo (0.7.29): what the lorries were paid for the tonnes the railway did not carry, at the shares the month was billed at. |
| 412 | `private boolean allowedKnown` | Whether the two above are this railway's own figures (0.7.29). |
| 428 | `private boolean fleetKnown` | Whether this railway's fleet is a figure it actually knows. |
| 431 | `private final Haulage haulage` | The Haulage division (FR2): the trucks that carry the city leg. |
| 434 | `private final PortDivision portDivision` | The Ports division (FR3): the crews of the city's berths, who bill the handling and pay the city its dues. |
| 446 | `private final double[] ownWages` | THE PAYROLL, SPLIT THREE WAYS (FR3, the landlord port, docs/queue/FR3.md 3): the posts of the buildings this sector owns (the track, the depots) and the crews it works for the city (the sea terminals', BuildingsTempla... |
| 447 | `private final double[] crewWages` |  |
| 449 | `private double carterWages` | The wage bill at full staffing of the carters' posts (Haulage's horse form, R0c), a part of the NO_DIPLOMA tier's ownWages: struck with them in updateWages(), 0 without the form. |
| 451 | `private final double[] wageRates` | The wage of a post of each tier as last struck, and the crews' posts of each kind of cargo's berths: the Ports division's per-kind figures (portsCrewByKind()). |
| 452 | `private final long[][] crewPostsByKind` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 144 | 2023 | **type** `public final class Freight extends Sector` | The Freight sector: the city's carrier, in the railway's slot (FR2, 2026-10-18; docs/studies/spec-freight-and-infrastructure.md 3.1, D1). |
| 454 | 20 | `public Freight()` |  |

### THE MONTH (lines 475-892)

| line | len | member | says |
|---:|---:|---|---|
| 502 | 1 | `public void haul(Sectors sectors)` | Bills last month's freight, re-prices, and moves the band for the month about to run. |
| 512 | 324 | `public void haul(Sectors sectors, ham.citybuildersim.Ports ports)` | ...WITH THE CITY'S PORTS (0.7.86, batch O9; ham.citybuildersim.Ports): the berths' shares in force bill the railway's part of each good and send the ships' part abroad in step 1, take their kinds' tonnes before the ra... |
| 853 | 8 | `private double monthlyCost(double fuelBill)` | What a month of railway costs to RUN. |
| 873 | 19 | `private double rateBase()` | The capital the return is measured against: the track and the ground under it, at what the balance sheet says they are worth. |

### the fleet (lines 893-938)

| line | len | member | says |
|---:|---:|---|---|
| 896 | 3 | `public double trackTonnes()` | Tonnes a month of track standing, whether or not there is anything to run on it. |
| 906 | 3 | `public double trackOnSite()` | ...and the track on site (BA1, 2026-11-02): what shortfalls() takes off the tonnes the lorries still carry, with the stations' and the sidings' below. |
| 911 | 1 | `public double fleet()` | Wagon sets owned. |
| 914 | 1 | `public double setsNeeded()` | ...and how many the track standing would need. |
| 917 | 1 | `public double replacementSets()` | What the fleet wears out a month, in sets: the replacements it asks for for ever (0.7.29, the railway page's line; haul()'s wear). |
| 932 | 6 | `public double bid(Good g)` | What it asks the market for: the gap between the fleet it has and the fleet its track needs. |

### the stations (FR7) (lines 939-1139)

| line | len | member | says |
|---:|---:|---|---|
| 969 | 3 | `public double stationTonnes()` | Tonnes a month the stations standing sort (FR7): the Rail Depots' and the Rail Terminals'. |
| 974 | 3 | `public double stationOnSite()` | ...and the stations on site (BA1, 2026-11-02; trackOnSite()). |
| 979 | 3 | `public double sidingTonnes()` | Tonnes a month of siding standing (FR7): the Rail Spurs' track, each into one works. |
| 984 | 3 | `public double sidingOnSite()` | ...and the sidings on site (BA1, 2026-11-02; trackOnSite()). |
| 993 | 3 | `public static double capacityOf(double track, double sets, double stations, double sidings)` | What the railway can carry in a month (FR7): the track or the wagon sets, whichever is less, and no more than the stations and the sidings handle together. |
| 1008 | 8 | `public static double[] deal(double capacity, double stations, double sidings, double bulkWant, double goodsWant)` | The tonnes the railway takes of a month's offer, {bulk, goods}, as haul() deals them (FR7). |
| 1027 | 7 | `public static double dealHome(double capacity, double stations, double sidings, double dealtBulk, double dealtGoods, double offer)` | The city tonnes the railway takes of the offer of the city's own bulk, after the boundary's (FR6, spec 3.3: "dealt after the boundary's"): at most the offer, what the track or the sets (`capacity`, the figure deal() i... |
| 1041 | 6 | `public boolean hasPortYard()` | Whether the port yard stands (FR6, D7): a station (a Rail Depot or a Rail Terminal) and a sea terminal both standing. |
| 1062 | 11 | `public static double bulkTonnesOf(BuildingsTemplate t)` | The tonnes a month of BULK goods a template makes and uses at nameplate (FR7); zero for a building that touches none. |
| 1082 | 4 | `public static boolean isBulkWorks(BuildingsTemplate t)` | Whether a template is a bulk works (FR7): a building, in any sector but Freight's own, that makes and uses BULK goods (Good.traffic()) in tonnes enough to fill a wagon set a month (BULK_WORKS_MIN_TONNES) - a mine, a q... |
| 1088 | 3 | `public int bulkWorks()` | How many bulk works stand in the city (FR7; isBulkWorks()): the most spurs worth laying, a siding to each. |
| 1105 | 1 | **type** `public record Shortfalls(double track, double station, double siding)` | The three shortfalls plan() orders against, tonnes a month (FR7), from the month's figures and what stands now. |
| 1115 | 6 | `public Shortfalls shortfalls()` | The month's shortfalls against what stands now and what is on site (Shortfalls; BA1, 2026-11-02: it was what stands only, while a sector could have one order on site). |
| 1123 | 1 | `public double getBulkOffered()` | The bulk the railway was offered in the month billed (FR7): rTonnes less the goods; none after a load until the next haul. |
| 1132 | 7 | `public void setMonthForTest(double bulkOffered, double goodsOffered, double lorryPerTonne)` | Hands the planner a month it did not run (FR7's harness): the bulk and the goods offered the railway, and what a lorry charges for a tonne of them. |

### the Haulage division's seam (FR2) (lines 1140-1322)

| line | len | member | says |
|---:|---:|---|---|
| 1151 | 1 | `public Haulage haulage()` | The Haulage division: the trucks that carry the city leg (FR2). |
| 1165 | 28 | `public void updateWages(double[] wagePerType, long[] posts)` | The wage bill on the posts it offers: its depots' drivers for the trucks they keep (Haulage.strikeDrivers()), every other post whole - the same whole numbers the city counts (BuildingManager.getPostsOfferedBySector()). |
| 1201 | 5 | `public double ownPayroll()` | The payroll of the posts on the buildings it owns - the track's and the depots' - at the fill rates (FR3): what getPayroll() was before the berths' crews came to this sector, struck on the same operands in the same or... |
| 1211 | 1 | `public double railPayroll()` | The Rail division's payroll line (FR3): its share of ownPayroll(), what is left of the sector's payroll after the Ports division's crews and Haulage's depots; what the railway's rule reads (monthlyCost()). |
| 1214 | 3 | `public double haulagePayroll()` | ...and Haulage's (FR3): its share of ownPayroll(), the depots' posts less the drivers laid off, and the carters' wages whole; what its rule reads (Haulage.closeMonth()). |
| 1219 | 1 | `public double carterPayroll()` | The carters' payroll (Haulage's horse form, R0c): their posts at the NO_DIPLOMA wage at the tier's fill rate, a part of ownPayroll(); 0 without the form. |
| 1223 | 9 | `public long[] postsPerTier()` | The posts it offers per tier: its buildings' (the track's, the depots', the berths' crews') and the carters Haulage's horse form hires, NO_DIPLOMA posts past the buildings' (R0c). |
| 1235 | 7 | `public long[] postsPastBuildings()` | ...and the carters again, as the posts past its buildings' the city's own count must add (BuildingManager.setPostsPast()), so the labour market is offered the posts the payroll is struck on. |
| 1244 | 5 | `public double portsPayroll()` | ...and the payroll of the crews it works for the city, the sea terminals' dockers, at the fill rates (FR3): what the city's transit bill paid for them before; 0 with no terminal. |
| 1251 | 6 | `public double portsPayrollShare()` | The berths' crews' share of the payroll it pays (FR3): portsPayroll() over getPayroll(); 0 with no terminal or no payroll. |
| 1264 | 11 | `public double[] portsCrewByKind()` | The month's crew wages by kind of cargo, in Ports.Cargo order (FR3): each kind's terminals' posts at the wage of each tier and its fill - what the city paid for them before the crews moved - for the Ports division to ... |
| 1277 | 1 | `double trucksHeld()` | Trucks owned: the TRUCKS pantry. |
| 1280 | 5 | `void seedTrucks(double trucks)` | The first fleet, free, the month there is a city leg to carry (Haulage.closeMonth(); Sector.vansKnown's reason). |
| 1291 | 3 | `public void deliverTrucks(double trucks)` | Trucks delivered to the fleet by a fitting (R1b; Game.fit() for a piece that ends CART_HORSES): ADDED to the trucks held, never replacing them. |
| 1296 | 4 | `double trucksBought()` | What the month's trucks cost it, bought at home and abroad: the statement in progress's TRUCKS, before the strike banks it (Haulage's running cost). |
| 1302 | 1 | `void wearTrucks(double trucks)` | A month of the trucks' wear, out of the pantry. |
| 1305 | 4 | `double truckPrice()` | What a truck is worth on this sector's books: the market's price as getInventoryValue() reads it. |
| 1311 | 3 | `Markets.Draw drawDiesel(double litres, Sectors sectors)` | The trucks' diesel, drawn as a buyer as the railway's is: the refiners' shelf first, the world for the rest. |
| 1316 | 3 | `Markets.Draw drawFeed(double tonnes, Sectors sectors)` | The horses' feed, drawn as a buyer as the diesel is (Haulage's horse form, R0c): the farms' shelf first, the world for the rest - crops. |
| 1321 | 1 | `void bookHaulage(double amount)` | Haulage's bill, as this sector's other revenue; named "Haulage billed" (nameOtherRevenue()). |

### the Ports division's seam (FR3) (lines 1323-1413)

| line | len | member | says |
|---:|---:|---|---|
| 1326 | 1 | `public PortDivision portDivision()` | The Ports division: the crews of the city's berths, who bill the handling and pay the city its dues (FR3). |
| 1329 | 1 | `Game gameOf()` | The game this sector plays in, for the division's rate and dial; null in a bare fixture. |
| 1332 | 1 | `ham.citybuildersim.BuildingManager buildingsOf()` | The city's buildings, for the division's berths and their book value. |
| 1335 | 1 | `void bookPortHandling(double amount)` | The Ports division's handling bill, as this sector's other revenue; named "Port handling billed" (nameOtherRevenue()). |
| 1338 | 1 | `void payPortDues(double amount)` | ...and the dues it pays the city, an operating cost named "Port dues": its cash falls by them at the strike and the treasury takes them in the same month (Sector.payTheCity()). |
| 1341 | 1 | `double diplomaWage()` | What a post on the DIPLOMA tier costs a month, the drivers' tier; nothing with no game to ask. |
| 1344 | 1 | `double noDiplomaWage()` | ...and on the NO_DIPLOMA tier, a carter's (Haulage's horse form, R0c). |
| 1346 | 6 | `private double wageOf(JobType tier)` |  |
| 1366 | 6 | `double haulageShareOf(ToDoubleFunction<BuildingsTemplate> f)` | The Truck Depots' share of one of this sector's cost lines, by a figure of their buildings: the finished depots' sum of it over every finished building's, or 0 when there is none to share (no buildings, or none that h... |
| 1385 | 9 | `public double haulagePayrollShare()` | ...and its payroll's share by the posts OFFERED (FR2, ★2): a depot's drivers for the places no truck fills are laid off and paid by nobody (Haulage.strikeDrivers()), so they are in neither side of the ratio. |
| 1406 | 7 | `double haulageShareOfSites(ToDoubleFunction<BuildingsTemplate> f)` | ...and the same share for the BALANCE SHEET's lines, the buildings' value and the ground under them, which count the buildings on site as well as the finished (EconomyManager.pushBalanceSheetInputs(): "Buildings finis... |

### what it is doing, for everyone else (lines 1414-1569)

| line | len | member | says |
|---:|---:|---|---|
| 1416 | 1 | `public double getQuote()` |  |
| 1417 | 1 | `public double getCapacityTonnes()` |  |
| 1419 | 1 | `public double getTradeTonnes()` | Every tonne that crossed the boundary in the month, by sea as well (0.7.86). |
| 1422 | 1 | `public double getOfferedTonnes()` | ...and what of it the railway was offered: all of it, less what the berths that go before it took (0.7.86). |
| 1423 | 1 | `public double getHauledTonnes()` |  |
| 1426 | 1 | `public double railedIn(ham.citybuildersim.Ports.Cargo k)` | Tonnes of a kind the railway landed, the month billed (0.8.2, batch FR1): the map's trains' to count, read by nothing in the model. |
| 1428 | 1 | `public double railedOut(ham.citybuildersim.Ports.Cargo k)` | ...and shipped. |
| 1431 | 1 | `public double truckedIn()` | Tonnes the city's trucks carried from the gates to their buyers, the month billed (0.8.8, batch FR5): imports less the siding's relief, the map's trucks' to count, read by nothing in the model. |
| 1433 | 1 | `public double truckedOut()` | ...and from their makers to the gates, less the same relief. |
| 1435 | 1 | `public double truckedHome()` | ...and between two firms of the city, bought at home: they cross no edge. |
| 1436 | 1 | `public double getTightness()` |  |
| 1437 | 1 | `public double getTruckBill()` |  |
| 1438 | 1 | `public double getHaulageBilled()` |  |
| 1439 | 1 | `public double getFuelBill()` |  |
| 1442 | 1 | `public double getFuelImported()` | ...and the part of it bought from the world (0.7.62): all of it with no refinery in the city. |
| 1445 | 1 | `public double getHomeShare()` | The share of the city's own bulk the railway carries, in force now (FR6): of the siding's part of what was bought at home. |
| 1448 | 1 | `public double getQuayShare()` | ...and of the quay bulk's yard part (FR6, D7); zero with no port yard. |
| 1451 | 1 | `public double getHomeOffered()` | The city tonnes the railway was offered in the month billed (FR6): the home tonnes' siding part, and the quay's yard part when a yard stood. |
| 1454 | 1 | `public double getHomeRailed()` | The home tonnes the railway carried in the month billed (FR6), at the share in force while they moved. |
| 1457 | 1 | `public double getQuayRailed()` | ...and the quay's, the port yard's (FR6, D7). |
| 1460 | 1 | `public double getHomeBill()` | What the railway billed on the home and the quay tonnes in the month billed (FR6): the city leg's rate, the railway's quote on Haulage's. |
| 1463 | 1 | `public double getCityLitres()` | The litres of diesel the month billed's city tonnes burned (FR6): CITY_LITRES_A_TONNE each; part of getFuelLitres(), none after a load until the next haul. |
| 1466 | 1 | `public double getFuelLitres()` | The litres of diesel the last haul drew (0.7.95): its tonnes at FUEL_LITRES_PER_TONNE, and the city tonnes at CITY_LITRES_A_TONNE (FR6); none after a load until the next. |
| 1469 | 1 | `public double getFuelLitresImported()` | ...and the litres of it the world sold, the refiners' tanks having none left (Markets.draw()). |
| 1478 | 1 | `public double getAllowedRevenue()` | What a month has to bring in: everything it costs to run, plus the return on the track. |
| 1481 | 1 | `public double getRunningCost()` | The railway's running cost in the month billed (FR2): the Rail division's share of the sector's lines and its fuel - what its bill is measured against now that the sector's net income holds the trucks' as well. |
| 1499 | 1 | `public double getPaidAbroad()` | THE MONTH'S LORRY BILL, THREE WAYS (0.7.29). |
| 1502 | 1 | `public double getKept()` | ...and what the shippers kept: the lorry bill less what was billed at home and what was paid abroad, never below zero (NaN while the split is not known). |
| 1505 | 1 | `public double getScarcity()` | The multiple a network that cannot reach the city's freight can charge on top. |
| 1508 | 1 | `public double[] getCarried()` | The share of each stream the railway is carrying, for the road relief. |
| 1516 | 6 | `public double railShareInForce(Good g)` | The railway's share of one good's crossing tonnes in force now (FR2): its stream's carried share, less what the city's berths take first (Ports.railShareOf()); 0 for a good with no freight stream. |
| 1529 | 6 | `public double seaShareInForce(Good g)` | The berths' share of one good's crossing tonnes in force now (FR6): its stream's carried share through Ports.seaShareOf(); 0 for a good with no freight stream and with no ports. |
| 1550 | 1 | `public void reapplyBand()` | Puts the band back where the saved month left it. |
| 1553 | 13 | `public void reapplyBand(ham.citybuildersim.Ports ports)` | ...with the city's ports' shares in force (0.7.86): step 5's lines, as haul() leaves them. |
| 1568 | 1 | `public double lorryRatePerTonne()` | What a tonne of the city's own freight costs by lorry, this month - of the freight the railway was offered, the berths that go before it having taken theirs (0.7.86). |

### PLANNING - the freight nobody is carrying (lines 1570-1889)

| line | len | member | says |
|---:|---:|---|---|
| 1590 | 34 | `public double estimatedMonthlyProfit(BuildingsTemplate t, BusinessInvestment plans)` | What one more line would earn: the freight it could pick up, at today's quote, less what it costs to run and to stand. |
| 1634 | 178 | `public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game)` | Manufacturing's shape: the best template by profit over cost, floored on staffing. |
| 1819 | 3 | `public double[] retirementDemandAndCapacity(Game game)` | Tonnes against nameplate: a city whose trade collapses should sell the track, and this is the one sector in the game whose demand series is neither a good nor a headcount. |
| 1824 | 1 | `public double unitsOf(BuildingsTemplate t)` |  |
| 1849 | 10 | `public boolean mayRetire(BuildingsTemplate t)` | A Truck Depot is never retired by the railway's measure (FR2): the retirement rule weighs track against the tonnes offered it, and a depot has no track to weigh. |
| 1865 | 8 | `public BuildingsTemplate firstStation()` | The smallest station of this sector's catalogue by the tonnes it sorts: the one plan() orders first, before any track; null with no catalogue or no station in it. |
| 1883 | 6 | `public boolean wouldOrderAgain(BuildingsTemplate station)` | Whether selling this station would leave the stations short by the first station's rung (FD1): the shortfall the stations leave, with this one's tonnes added back, is at least the tonnes plan() orders a first station ... |

### the books (lines 1890-1924)

| line | len | member | says |
|---:|---:|---|---|
| 1898 | 19 | `protected Map<String, Double> nameOtherRevenue()` | ITS WHOLE REVENUE HAS A NAME, because none of it is the sale of a good. |
| 1920 | 1 | `public ham.citybuildersim.SectorStatements.Format statementFormat()` | Its formal statements' format (0.7.74, spec-sector-statements 4.6): a carrier, read by its revenue a worker. |
| 1923 | 1 | `public String inputLabel()` |  |

### the screen (lines 1925-2020)

| line | len | member | says |
|---:|---:|---|---|
| 1928 | 1 | `public boolean hasPlantBlock()` |  |
| 1931 | 89 | `public List<Sector.Line> ownLines(Game game)` |  |

### save/load (lines 2021-2166)

| line | len | member | says |
|---:|---:|---|---|
| 2024 | 43 | `protected void saveExtras(Map<String, Double> extras)` |  |
| 2069 | 45 | `protected void restoreExtras(Map<String, Double> extras)` |  |
| 2116 | 23 | `protected void resetExtras()` |  |
| 2149 | 17 | `protected void redenominateExtras(double scale)` | THE QUOTE IS NOT MONEY AND DOES NOT MOVE, which is the whole reason it was written as a share of the lorry rate. |

