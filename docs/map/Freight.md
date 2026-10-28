# Freight.java - 2,036 lines · 95 methods · 13 constants · sectors

`ham/citybuildersim/sectors/Freight.java` - generated 2026-10-28 by CodeMap; line numbers are as of that run.

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

**Uses:** [Traffic](Traffic.md) (35), [Ports](Ports.md) (31), [Good](Good.md) (29), [BuildingsTemplate](BuildingsTemplate.md) (23), [BusinessInvestment](BusinessInvestment.md) (17), [Formats](Formats.md) (8), [Sector](Sector.md) (7), [Haulage](Haulage.md) (6), [PortDivision](PortDivision.md) (6), [JobType](JobType.md) (5), [Game](Game.md) (5), [Sectors](Sectors.md) (4), [InfrastructureManager](InfrastructureManager.md) (4), [Markets](Markets.md) (3), [BuildingManager](BuildingManager.md) (3), [GoodsMarket](GoodsMarket.md) (2), [SectorStatements](SectorStatements.md) (2), [BuildingType](BuildingType.md) (1)

**Used by (38):** [BuildCardCheck](BuildCardCheck.md), [BuildScreen](BuildScreen.md), [BuildingVisual](BuildingVisual.md), [EconomyManager](EconomyManager.md), [FreightView](FreightView.md), [FreightViewCheck](FreightViewCheck.md), [FuelSplit](FuelSplit.md), [Game](Game.md), [GdpCheck](GdpCheck.md), [Haulage](Haulage.md), [HaulageCheck](HaulageCheck.md), [HealthCheck](HealthCheck.md), [HomeRailCheck](HomeRailCheck.md), [InfrastructureScreen](InfrastructureScreen.md), [LandlordPortCheck](LandlordPortCheck.md), [LongPlaytest](LongPlaytest.md), [MapCheck](MapCheck.md), [OilCheck](OilCheck.md), [PolicyPreview](PolicyPreview.md), [PortCheck](PortCheck.md), [PortDivision](PortDivision.md), [Ports](Ports.md), [RailCheck](RailCheck.md), [RailDepotCheck](RailDepotCheck.md), [RefineryCheck](RefineryCheck.md), [RefineryView](RefineryView.md), [RefineryViewCheck](RefineryViewCheck.md), [SaveFileCheck](SaveFileCheck.md), [Sector](Sector.md), [SectorScreen](SectorScreen.md), [Sectors](Sectors.md), [SpreadPlanner](SpreadPlanner.md), [TradeBand](TradeBand.md), [TradeBandCheck](TradeBandCheck.md), [TrainSchedule](TrainSchedule.md), [TruckCheck](TruckCheck.md), [TruckRouteCheck](TruckRouteCheck.md), [TruckSchedule](TruckSchedule.md)

## Sections

| line | section |
|---:|---|
| 472 | THE MONTH |
| 890 | · the fleet |
| 926 | · the stations (FR7) |
| 1109 | · the Haulage division's seam (FR2) |
| 1241 | · the Ports division's seam (FR3) |
| 1327 | · what it is doing, for everyone else |
| 1483 | PLANNING - the freight nobody is carrying |
| 1762 | · the books |
| 1797 | · the screen |
| 1893 | · save/load |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 156 | `Freight.RAIL_FLOOR` | `.30` | The cheapest the railway will ever quote, as a share of the lorry rate. |
| 159 | `Freight.OPENING_QUOTE` | `.60` | What a city with no railway assumes the first line could charge. |
| 180 | `Freight.MIN_LINE_UTILISATION` | `.60` | How much of a line's nameplate has to be freight nobody is carrying before it is worth laying. |
| 199 | `Freight.TONNES_PER_SET` | `2500` | Tonnes a month one wagon set can haul. |
| 202 | `Freight.SET_LIFE_MONTHS` | `240` | How long a wagon set lasts before it is scrap. |
| 233 | `Freight.TARGET_RETURN` | `.012` | What it tries to earn a month ON THE TRACK IT HAS SUNK, before scarcity. |
| 236 | `Freight.MAX_SCARCITY_MULTIPLE` | `1.6` | How far a network that cannot keep up can push the quote above cost-plus. |
| 239 | `Freight.REPRICE_SPEED` | `.25` | How fast the quote walks to where it should be. |
| 275 | `Freight.WORLD_FUEL_PER_TONNE` | `.03` | What a tonne of haulage burns, IN THE WORLD'S MONEY. |
| 287 | `Freight.FUEL_LITRES_PER_TONNE` | `18` | Litres of fuel a tonne hauled burns: eighteen. |
| 292 | `Freight.PORT_YARD_SHARE` | `.45` | The most of the BULK tonnes that crossed a berth the port yard takes to rail (FR6, D7): Montreal's rail share, on the port's own railway on the quay (spec R70). |
| 298 | `Freight.CITY_LITRES_A_TONNE` | `Haulage.DIESEL_LITRES_A_TONNE / 3` | Litres of diesel the railway burns on a city tonne (FR6): a third of a truck's city leg (Haulage.DIESEL_LITRES_A_TONNE), as rail costs about a third of a truck a tonne-kilometre (spec R50, R51); FUEL_LITRES_PER_TONNE ... |
| 1036 | `Freight.BULK_WORKS_MIN_TONNES` | `TONNES_PER_SET * MIN_LINE_UTILISATION` | The bulk a building has to make and use between them, tonnes a month at nameplate, to be a bulk works (FR7): MIN_LINE_UTILISATION of a wagon set's month, as a line is not laid below that share of its nameplate. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 301 | `private double quote` | The share of the lorry rate it is charging, today. |
| 308 | `private final double[] carried` | What it is actually carrying, by stream - the share in force for the month now running, and therefore the share the band was set from and the share the invoice will be raised at when that month is struck. |
| 322 | `private double homeShare, quayShare` | THE CITY'S OWN BULK ON THE RAILWAY (FR6, D4, D7; docs/queue/FR6.md 2): the share of the offer of the bulk bought at home (homeShare) and of the port yard's quay bulk (quayShare) the railway carries, in force for the m... |
| 325 | `private double rTonnes, rHauled, rTruckBill, rHaulage, rFuel` | the month just billed, for the screens |
| 336 | `private double rHomeOffered, rHomeRailed, rHomeBill, rQuayRailed, rCityLitres` | The city's own bulk in the month billed (FR6), for the screens: the city tonnes the railway was offered at the haul (the siding's part of the home tonnes, and the yard's part of the quay's when a yard stands), the hom... |
| 349 | `private double rBulkOffered` | The BULK stream's part of rTonnes (FR7): the bulk the railway was offered, the berths' first share taken off, as rTonnes is. |
| 358 | `private double rCrossed` | Every tonne of freight that crossed the boundary in the month (0.7.86): rTonnes is what the railway was offered, the berths that go before it having taken theirs (ham.citybuildersim.Ports) - the figure its tightness, ... |
| 361 | `private double rOfferedBill` | ...and what a lorry would have charged for the tonnes the railway was offered (0.7.86): the lorry bill less the berths' first share of it; the whole bill with no terminal. |
| 371 | `private final double[] railIn` | The month billed's railed tonnes by cargo kind, landed and shipped (0.8.2, batch FR1; spec-freight 3.3 and 4.4): each owner's tonnes of a good across the boundary at the railway's share in force, under the good's kind... |
| 381 | `private double truckIn, truckOut, truckHome` | The month billed's city-leg truck tonnes by load, for the map's trucks (0.8.8, batch FR5; spec-freight 4.7): the bulk tonnes landed, shipped and bought at home, each at the railway's share in force as Haulage.legTonne... |
| 384 | `private double rFuelImported` | The part of the month's fuel the world sold it (0.7.62). |
| 387 | `private double rFuelLitres, rFuelLitresImported` | ...and the month's diesel in litres, and the litres of it the world sold (0.7.95, batch O11: the refinery's pictogram's railway). |
| 388 | `private double rCapacity, rTightness, rFx` |  |
| 391 | `private double rRunning` | ...and the part of rAllowed that is the railway's running cost (FR2): what it earned is its bill less this, now that the sector's income is the trucks' too. |
| 399 | `private double rPaidAbroad` | The part of the month's lorry bill that went abroad with the cargo (0.7.29): what the lorries were paid for the tonnes the railway did not carry, at the shares the month was billed at. |
| 411 | `private boolean allowedKnown` | Whether the two above are this railway's own figures (0.7.29). |
| 427 | `private boolean fleetKnown` | Whether this railway's fleet is a figure it actually knows. |
| 430 | `private final Haulage haulage` | The Haulage division (FR2): the trucks that carry the city leg. |
| 433 | `private final PortDivision portDivision` | The Ports division (FR3): the crews of the city's berths, who bill the handling and pay the city its dues. |
| 445 | `private final double[] ownWages` | THE PAYROLL, SPLIT THREE WAYS (FR3, the landlord port, docs/queue/FR3.md 3): the posts of the buildings this sector owns (the track, the depots) and the crews it works for the city (the sea terminals', BuildingsTempla... |
| 446 | `private final double[] crewWages` |  |
| 448 | `private final double[] wageRates` | The wage of a post of each tier as last struck, and the crews' posts of each kind of cargo's berths: the Ports division's per-kind figures (portsCrewByKind()). |
| 449 | `private final long[][] crewPostsByKind` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 143 | 1894 | **type** `public final class Freight extends Sector` | The Freight sector: the city's carrier, in the railway's slot (FR2, 2026-10-18; docs/studies/spec-freight-and-infrastructure.md 3.1, D1). |
| 451 | 20 | `public Freight()` |  |

### THE MONTH (lines 472-889)

| line | len | member | says |
|---:|---:|---|---|
| 499 | 1 | `public void haul(Sectors sectors)` | Bills last month's freight, re-prices, and moves the band for the month about to run. |
| 509 | 324 | `public void haul(Sectors sectors, ham.citybuildersim.Ports ports)` | ...WITH THE CITY'S PORTS (0.7.86, batch O9; ham.citybuildersim.Ports): the berths' shares in force bill the railway's part of each good and send the ships' part abroad in step 1, take their kinds' tonnes before the ra... |
| 850 | 8 | `private double monthlyCost(double fuelBill)` | What a month of railway costs to RUN. |
| 870 | 19 | `private double rateBase()` | The capital the return is measured against: the track and the ground under it, at what the balance sheet says they are worth. |

### the fleet (lines 890-925)

| line | len | member | says |
|---:|---:|---|---|
| 893 | 3 | `public double trackTonnes()` | Tonnes a month of track standing, whether or not there is anything to run on it. |
| 898 | 1 | `public double fleet()` | Wagon sets owned. |
| 901 | 1 | `public double setsNeeded()` | ...and how many the track standing would need. |
| 904 | 1 | `public double replacementSets()` | What the fleet wears out a month, in sets: the replacements it asks for for ever (0.7.29, the railway page's line; haul()'s wear). |
| 919 | 6 | `public double bid(Good g)` | What it asks the market for: the gap between the fleet it has and the fleet its track needs. |

### the stations (FR7) (lines 926-1108)

| line | len | member | says |
|---:|---:|---|---|
| 956 | 3 | `public double stationTonnes()` | Tonnes a month the stations standing sort (FR7): the Rail Depots' and the Rail Terminals'. |
| 961 | 3 | `public double sidingTonnes()` | Tonnes a month of siding standing (FR7): the Rail Spurs' track, each into one works. |
| 970 | 3 | `public static double capacityOf(double track, double sets, double stations, double sidings)` | What the railway can carry in a month (FR7): the track or the wagon sets, whichever is less, and no more than the stations and the sidings handle together. |
| 985 | 8 | `public static double[] deal(double capacity, double stations, double sidings, double bulkWant, double goodsWant)` | The tonnes the railway takes of a month's offer, {bulk, goods}, as haul() deals them (FR7). |
| 1004 | 7 | `public static double dealHome(double capacity, double stations, double sidings, double dealtBulk, double dealtGoods, double offer)` | The city tonnes the railway takes of the offer of the city's own bulk, after the boundary's (FR6, spec 3.3: "dealt after the boundary's"): at most the offer, what the track or the sets (`capacity`, the figure deal() i... |
| 1018 | 6 | `public boolean hasPortYard()` | Whether the port yard stands (FR6, D7): a station (a Rail Depot or a Rail Terminal) and a sea terminal both standing. |
| 1039 | 11 | `public static double bulkTonnesOf(BuildingsTemplate t)` | The tonnes a month of BULK goods a template makes and uses at nameplate (FR7); zero for a building that touches none. |
| 1059 | 4 | `public static boolean isBulkWorks(BuildingsTemplate t)` | Whether a template is a bulk works (FR7): a building, in any sector but Freight's own, that makes and uses BULK goods (Good.traffic()) in tonnes enough to fill a wagon set a month (BULK_WORKS_MIN_TONNES) - a mine, a q... |
| 1065 | 3 | `public int bulkWorks()` | How many bulk works stand in the city (FR7; isBulkWorks()): the most spurs worth laying, a siding to each. |
| 1082 | 1 | **type** `public record Shortfalls(double track, double station, double siding)` | The three shortfalls plan() orders against, tonnes a month (FR7), from the month's figures and what stands now. |
| 1085 | 5 | `public Shortfalls shortfalls()` | The month's shortfalls against what stands now (Shortfalls). |
| 1092 | 1 | `public double getBulkOffered()` | The bulk the railway was offered in the month billed (FR7): rTonnes less the goods; none after a load until the next haul. |
| 1101 | 7 | `public void setMonthForTest(double bulkOffered, double goodsOffered, double lorryPerTonne)` | Hands the planner a month it did not run (FR7's harness): the bulk and the goods offered the railway, and what a lorry charges for a tonne of them. |

### the Haulage division's seam (FR2) (lines 1109-1240)

| line | len | member | says |
|---:|---:|---|---|
| 1120 | 1 | `public Haulage haulage()` | The Haulage division: the trucks that carry the city leg (FR2). |
| 1134 | 25 | `public void updateWages(double[] wagePerType, long[] posts)` | The wage bill on the posts it offers: its depots' drivers for the trucks they keep (Haulage.strikeDrivers()), every other post whole - the same whole numbers the city counts (BuildingManager.getPostsOfferedBySector()). |
| 1167 | 5 | `public double ownPayroll()` | The payroll of the posts on the buildings it owns - the track's and the depots' - at the fill rates (FR3): what getPayroll() was before the berths' crews came to this sector, struck on the same operands in the same or... |
| 1174 | 1 | `public double railPayroll()` | The Rail division's payroll line (FR3): its share of ownPayroll(), what is left of the sector's payroll after the Ports division's crews and Haulage's depots; what the railway's rule reads (monthlyCost()). |
| 1177 | 1 | `public double haulagePayroll()` | ...and Haulage's (FR3): its share of ownPayroll(), the depots' posts less the drivers laid off; what its rule reads (Haulage.closeMonth()). |
| 1180 | 5 | `public double portsPayroll()` | ...and the payroll of the crews it works for the city, the sea terminals' dockers, at the fill rates (FR3): what the city's transit bill paid for them before; 0 with no terminal. |
| 1187 | 6 | `public double portsPayrollShare()` | The berths' crews' share of the payroll it pays (FR3): portsPayroll() over getPayroll(); 0 with no terminal or no payroll. |
| 1200 | 11 | `public double[] portsCrewByKind()` | The month's crew wages by kind of cargo, in Ports.Cargo order (FR3): each kind's terminals' posts at the wage of each tier and its fill - what the city paid for them before the crews moved - for the Ports division to ... |
| 1213 | 1 | `double trucksHeld()` | Trucks owned: the TRUCKS pantry. |
| 1216 | 1 | `void seedTrucks(double trucks)` | The first fleet, free, the month there is a city leg to carry (Haulage.closeMonth(); Sector.vansKnown's reason). |
| 1219 | 4 | `double trucksBought()` | What the month's trucks cost it, bought at home and abroad: the statement in progress's TRUCKS, before the strike banks it (Haulage's running cost). |
| 1225 | 1 | `void wearTrucks(double trucks)` | A month of the trucks' wear, out of the pantry. |
| 1228 | 4 | `double truckPrice()` | What a truck is worth on this sector's books: the market's price as getInventoryValue() reads it. |
| 1234 | 3 | `Markets.Draw drawDiesel(double litres, Sectors sectors)` | The trucks' diesel, drawn as a buyer as the railway's is: the refiners' shelf first, the world for the rest. |
| 1239 | 1 | `void bookHaulage(double amount)` | Haulage's bill, as this sector's other revenue; named "Haulage billed" (nameOtherRevenue()). |

### the Ports division's seam (FR3) (lines 1241-1326)

| line | len | member | says |
|---:|---:|---|---|
| 1244 | 1 | `public PortDivision portDivision()` | The Ports division: the crews of the city's berths, who bill the handling and pay the city its dues (FR3). |
| 1247 | 1 | `Game gameOf()` | The game this sector plays in, for the division's rate and dial; null in a bare fixture. |
| 1250 | 1 | `ham.citybuildersim.BuildingManager buildingsOf()` | The city's buildings, for the division's berths and their book value. |
| 1253 | 1 | `void bookPortHandling(double amount)` | The Ports division's handling bill, as this sector's other revenue; named "Port handling billed" (nameOtherRevenue()). |
| 1256 | 1 | `void payPortDues(double amount)` | ...and the dues it pays the city, an operating cost named "Port dues": its cash falls by them at the strike and the treasury takes them in the same month (Sector.payTheCity()). |
| 1259 | 6 | `double diplomaWage()` | What a post on the DIPLOMA tier costs a month, the drivers' tier; nothing with no game to ask. |
| 1279 | 6 | `double haulageShareOf(ToDoubleFunction<BuildingsTemplate> f)` | The Truck Depots' share of one of this sector's cost lines, by a figure of their buildings: the finished depots' sum of it over every finished building's, or 0 when there is none to share (no buildings, or none that h... |
| 1298 | 9 | `public double haulagePayrollShare()` | ...and its payroll's share by the posts OFFERED (FR2, ★2): a depot's drivers for the places no truck fills are laid off and paid by nobody (Haulage.strikeDrivers()), so they are in neither side of the ratio. |
| 1319 | 7 | `double haulageShareOfSites(ToDoubleFunction<BuildingsTemplate> f)` | ...and the same share for the BALANCE SHEET's lines, the buildings' value and the ground under them, which count the buildings on site as well as the finished (EconomyManager.pushBalanceSheetInputs(): "Buildings finis... |

### what it is doing, for everyone else (lines 1327-1482)

| line | len | member | says |
|---:|---:|---|---|
| 1329 | 1 | `public double getQuote()` |  |
| 1330 | 1 | `public double getCapacityTonnes()` |  |
| 1332 | 1 | `public double getTradeTonnes()` | Every tonne that crossed the boundary in the month, by sea as well (0.7.86). |
| 1335 | 1 | `public double getOfferedTonnes()` | ...and what of it the railway was offered: all of it, less what the berths that go before it took (0.7.86). |
| 1336 | 1 | `public double getHauledTonnes()` |  |
| 1339 | 1 | `public double railedIn(ham.citybuildersim.Ports.Cargo k)` | Tonnes of a kind the railway landed, the month billed (0.8.2, batch FR1): the map's trains' to count, read by nothing in the model. |
| 1341 | 1 | `public double railedOut(ham.citybuildersim.Ports.Cargo k)` | ...and shipped. |
| 1344 | 1 | `public double truckedIn()` | Tonnes the city's trucks carried from the gates to their buyers, the month billed (0.8.8, batch FR5): imports less the siding's relief, the map's trucks' to count, read by nothing in the model. |
| 1346 | 1 | `public double truckedOut()` | ...and from their makers to the gates, less the same relief. |
| 1348 | 1 | `public double truckedHome()` | ...and between two firms of the city, bought at home: they cross no edge. |
| 1349 | 1 | `public double getTightness()` |  |
| 1350 | 1 | `public double getTruckBill()` |  |
| 1351 | 1 | `public double getHaulageBilled()` |  |
| 1352 | 1 | `public double getFuelBill()` |  |
| 1355 | 1 | `public double getFuelImported()` | ...and the part of it bought from the world (0.7.62): all of it with no refinery in the city. |
| 1358 | 1 | `public double getHomeShare()` | The share of the city's own bulk the railway carries, in force now (FR6): of the siding's part of what was bought at home. |
| 1361 | 1 | `public double getQuayShare()` | ...and of the quay bulk's yard part (FR6, D7); zero with no port yard. |
| 1364 | 1 | `public double getHomeOffered()` | The city tonnes the railway was offered in the month billed (FR6): the home tonnes' siding part, and the quay's yard part when a yard stood. |
| 1367 | 1 | `public double getHomeRailed()` | The home tonnes the railway carried in the month billed (FR6), at the share in force while they moved. |
| 1370 | 1 | `public double getQuayRailed()` | ...and the quay's, the port yard's (FR6, D7). |
| 1373 | 1 | `public double getHomeBill()` | What the railway billed on the home and the quay tonnes in the month billed (FR6): the city leg's rate, the railway's quote on Haulage's. |
| 1376 | 1 | `public double getCityLitres()` | The litres of diesel the month billed's city tonnes burned (FR6): CITY_LITRES_A_TONNE each; part of getFuelLitres(), none after a load until the next haul. |
| 1379 | 1 | `public double getFuelLitres()` | The litres of diesel the last haul drew (0.7.95): its tonnes at FUEL_LITRES_PER_TONNE, and the city tonnes at CITY_LITRES_A_TONNE (FR6); none after a load until the next. |
| 1382 | 1 | `public double getFuelLitresImported()` | ...and the litres of it the world sold, the refiners' tanks having none left (Markets.draw()). |
| 1391 | 1 | `public double getAllowedRevenue()` | What a month has to bring in: everything it costs to run, plus the return on the track. |
| 1394 | 1 | `public double getRunningCost()` | The railway's running cost in the month billed (FR2): the Rail division's share of the sector's lines and its fuel - what its bill is measured against now that the sector's net income holds the trucks' as well. |
| 1412 | 1 | `public double getPaidAbroad()` | THE MONTH'S LORRY BILL, THREE WAYS (0.7.29). |
| 1415 | 1 | `public double getKept()` | ...and what the shippers kept: the lorry bill less what was billed at home and what was paid abroad, never below zero (NaN while the split is not known). |
| 1418 | 1 | `public double getScarcity()` | The multiple a network that cannot reach the city's freight can charge on top. |
| 1421 | 1 | `public double[] getCarried()` | The share of each stream the railway is carrying, for the road relief. |
| 1429 | 6 | `public double railShareInForce(Good g)` | The railway's share of one good's crossing tonnes in force now (FR2): its stream's carried share, less what the city's berths take first (Ports.railShareOf()); 0 for a good with no freight stream. |
| 1442 | 6 | `public double seaShareInForce(Good g)` | The berths' share of one good's crossing tonnes in force now (FR6): its stream's carried share through Ports.seaShareOf(); 0 for a good with no freight stream and with no ports. |
| 1463 | 1 | `public void reapplyBand()` | Puts the band back where the saved month left it. |
| 1466 | 13 | `public void reapplyBand(ham.citybuildersim.Ports ports)` | ...with the city's ports' shares in force (0.7.86): step 5's lines, as haul() leaves them. |
| 1481 | 1 | `public double lorryRatePerTonne()` | What a tonne of the city's own freight costs by lorry, this month - of the freight the railway was offered, the berths that go before it having taken theirs (0.7.86). |

### PLANNING - the freight nobody is carrying (lines 1483-1761)

| line | len | member | says |
|---:|---:|---|---|
| 1503 | 34 | `public double estimatedMonthlyProfit(BuildingsTemplate t, BusinessInvestment plans)` | What one more line would earn: the freight it could pick up, at today's quote, less what it costs to run and to stand. |
| 1547 | 180 | `public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game)` | Manufacturing's shape: the best template by profit over cost, floored on staffing. |
| 1734 | 3 | `public double[] retirementDemandAndCapacity(Game game)` | Tonnes against nameplate: a city whose trade collapses should sell the track, and this is the one sector in the game whose demand series is neither a good nor a headcount. |
| 1739 | 1 | `public double unitsOf(BuildingsTemplate t)` |  |
| 1752 | 9 | `public boolean mayRetire(BuildingsTemplate t)` | A Truck Depot is never retired by the railway's measure (FR2): the retirement rule weighs track against the tonnes offered it, and a depot has no track to weigh. |

### the books (lines 1762-1796)

| line | len | member | says |
|---:|---:|---|---|
| 1770 | 19 | `protected Map<String, Double> nameOtherRevenue()` | ITS WHOLE REVENUE HAS A NAME, because none of it is the sale of a good. |
| 1792 | 1 | `public ham.citybuildersim.SectorStatements.Format statementFormat()` | Its formal statements' format (0.7.74, spec-sector-statements 4.6): a carrier, read by its revenue a worker. |
| 1795 | 1 | `public String inputLabel()` |  |

### the screen (lines 1797-1892)

| line | len | member | says |
|---:|---:|---|---|
| 1800 | 1 | `public boolean hasPlantBlock()` |  |
| 1803 | 89 | `public List<Sector.Line> ownLines(Game game)` |  |

### save/load (lines 1893-2036)

| line | len | member | says |
|---:|---:|---|---|
| 1896 | 43 | `protected void saveExtras(Map<String, Double> extras)` |  |
| 1941 | 45 | `protected void restoreExtras(Map<String, Double> extras)` |  |
| 1988 | 22 | `protected void resetExtras()` |  |
| 2020 | 16 | `protected void redenominateExtras(double scale)` | THE QUOTE IS NOT MONEY AND DOES NOT MOVE, which is the whole reason it was written as a share of the lorry rate. |

