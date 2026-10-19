# Freight.java - 1,505 lines · 72 methods · 10 constants · sectors

`ham/citybuildersim/sectors/Freight.java` - generated 2026-10-19 by CodeMap; line numbers are as of that run.

> The Freight sector: the city's carrier, in the railway's slot (FR2, 2026-10-18;
> docs/studies/spec-freight-and-infrastructure.md 3.1, D1). Its Rail division is the
> railway, THE TWELFTH SECTOR (2026-09-16, Jerus's call), whose design follows.
> Its Haulage division (Haulage, from FR2) is the trucks that carry the city leg
> of every bulk tonne; it keeps its own figures and price rule and books through
> this sector, and the two divisions share these buildings' costs by whose they
> are (haulageShareOf()).
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
> 
> ... (45 more lines in the source)

**Uses:** [Traffic](Traffic.md) (33), [Ports](Ports.md) (29), [Good](Good.md) (26), [BuildingsTemplate](BuildingsTemplate.md) (18), [BusinessInvestment](BusinessInvestment.md) (15), [Sector](Sector.md) (7), [PortDivision](PortDivision.md) (6), [Haulage](Haulage.md) (5), [JobType](JobType.md) (5), [Game](Game.md) (5), [Formats](Formats.md) (5), [Sectors](Sectors.md) (3), [Markets](Markets.md) (3), [BuildingManager](BuildingManager.md) (3), [GoodsMarket](GoodsMarket.md) (2), [SectorStatements](SectorStatements.md) (2), [BuildingType](BuildingType.md) (1)

**Used by (25):** [BuildCardCheck](BuildCardCheck.md), [BuildScreen](BuildScreen.md), [EconomyManager](EconomyManager.md), [FuelSplit](FuelSplit.md), [Game](Game.md), [GdpCheck](GdpCheck.md), [Haulage](Haulage.md), [HaulageCheck](HaulageCheck.md), [HealthCheck](HealthCheck.md), [InfrastructureScreen](InfrastructureScreen.md), [LandlordPortCheck](LandlordPortCheck.md), [MapCheck](MapCheck.md), [OilCheck](OilCheck.md), [PortCheck](PortCheck.md), [PortDivision](PortDivision.md), [Ports](Ports.md), [RailCheck](RailCheck.md), [RefineryCheck](RefineryCheck.md), [RefineryView](RefineryView.md), [RefineryViewCheck](RefineryViewCheck.md), [SaveFileCheck](SaveFileCheck.md), [Sector](Sector.md), [Sectors](Sectors.md), [SpreadPlanner](SpreadPlanner.md), [TrainSchedule](TrainSchedule.md)

## Sections

| line | section |
|---:|---|
| 400 | THE MONTH |
| 733 | · the fleet |
| 769 | · the Haulage division's seam (FR2) |
| 901 | · the Ports division's seam (FR3) |
| 987 | · what it is doing, for everyone else |
| 1097 | PLANNING - the freight nobody is carrying |
| 1263 | · the books |
| 1298 | · the screen |
| 1391 | · save/load |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 142 | `Freight.RAIL_FLOOR` | `.30` | The cheapest the railway will ever quote, as a share of the lorry rate. |
| 145 | `Freight.OPENING_QUOTE` | `.60` | What a city with no railway assumes the first line could charge. |
| 166 | `Freight.MIN_LINE_UTILISATION` | `.60` | How much of a line's nameplate has to be freight nobody is carrying before it is worth laying. |
| 185 | `Freight.TONNES_PER_SET` | `2500` | Tonnes a month one wagon set can haul. |
| 188 | `Freight.SET_LIFE_MONTHS` | `240` | How long a wagon set lasts before it is scrap. |
| 219 | `Freight.TARGET_RETURN` | `.012` | What it tries to earn a month ON THE TRACK IT HAS SUNK, before scarcity. |
| 222 | `Freight.MAX_SCARCITY_MULTIPLE` | `1.6` | How far a network that cannot keep up can push the quote above cost-plus. |
| 225 | `Freight.REPRICE_SPEED` | `.25` | How fast the quote walks to where it should be. |
| 261 | `Freight.WORLD_FUEL_PER_TONNE` | `.03` | What a tonne of haulage burns, IN THE WORLD'S MONEY. |
| 273 | `Freight.FUEL_LITRES_PER_TONNE` | `18` | Litres of fuel a tonne hauled burns: eighteen. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 276 | `private double quote` | The share of the lorry rate it is charging, today. |
| 283 | `private final double[] carried` | What it is actually carrying, by stream - the share in force for the month now running, and therefore the share the band was set from and the share the invoice will be raised at when that month is struck. |
| 286 | `private double rTonnes, rHauled, rTruckBill, rHaulage, rFuel` | the month just billed, for the screens |
| 295 | `private double rCrossed` | Every tonne of freight that crossed the boundary in the month (0.7.86): rTonnes is what the railway was offered, the berths that go before it having taken theirs (ham.citybuildersim.Ports) - the figure its tightness, ... |
| 298 | `private double rOfferedBill` | ...and what a lorry would have charged for the tonnes the railway was offered (0.7.86): the lorry bill less the berths' first share of it; the whole bill with no terminal. |
| 308 | `private final double[] railIn` | The month billed's railed tonnes by cargo kind, landed and shipped (0.8.2, batch FR1; spec-freight 3.3 and 4.4): each owner's tonnes of a good across the boundary at the railway's share in force, under the good's kind... |
| 312 | `private double rFuelImported` | The part of the month's fuel the world sold it (0.7.62). |
| 315 | `private double rFuelLitres, rFuelLitresImported` | ...and the month's diesel in litres, and the litres of it the world sold (0.7.95, batch O11: the refinery's pictogram's railway). |
| 316 | `private double rCapacity, rTightness, rFx` |  |
| 319 | `private double rRunning` | ...and the part of rAllowed that is the railway's running cost (FR2): what it earned is its bill less this, now that the sector's income is the trucks' too. |
| 327 | `private double rPaidAbroad` | The part of the month's lorry bill that went abroad with the cargo (0.7.29): what the lorries were paid for the tonnes the railway did not carry, at the shares the month was billed at. |
| 339 | `private boolean allowedKnown` | Whether the two above are this railway's own figures (0.7.29). |
| 355 | `private boolean fleetKnown` | Whether this railway's fleet is a figure it actually knows. |
| 358 | `private final Haulage haulage` | The Haulage division (FR2): the trucks that carry the city leg. |
| 361 | `private final PortDivision portDivision` | The Ports division (FR3): the crews of the city's berths, who bill the handling and pay the city its dues. |
| 373 | `private final double[] ownWages` | THE PAYROLL, SPLIT THREE WAYS (FR3, the landlord port, docs/queue/FR3.md 3): the posts of the buildings this sector owns (the track, the depots) and the crews it works for the city (the sea terminals', BuildingsTempla... |
| 374 | `private final double[] crewWages` |  |
| 376 | `private final double[] wageRates` | The wage of a post of each tier as last struck, and the crews' posts of each kind of cargo's berths: the Ports division's per-kind figures (portsCrewByKind()). |
| 377 | `private final long[][] crewPostsByKind` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 129 | 1377 | **type** `public final class Freight extends Sector` | The Freight sector: the city's carrier, in the railway's slot (FR2, 2026-10-18; docs/studies/spec-freight-and-infrastructure.md 3.1, D1). |
| 379 | 20 | `public Freight()` |  |

### THE MONTH (lines 400-732)

| line | len | member | says |
|---:|---:|---|---|
| 427 | 1 | `public void haul(Sectors sectors)` | Bills last month's freight, re-prices, and moves the band for the month about to run. |
| 437 | 239 | `public void haul(Sectors sectors, ham.citybuildersim.Ports ports)` | ...WITH THE CITY'S PORTS (0.7.86, batch O9; ham.citybuildersim.Ports): the berths' shares in force bill the railway's part of each good and send the ships' part abroad in step 1, take their kinds' tonnes before the ra... |
| 693 | 8 | `private double monthlyCost(double fuelBill)` | What a month of railway costs to RUN. |
| 713 | 19 | `private double rateBase()` | The capital the return is measured against: the track and the ground under it, at what the balance sheet says they are worth. |

### the fleet (lines 733-768)

| line | len | member | says |
|---:|---:|---|---|
| 736 | 3 | `public double trackTonnes()` | Tonnes a month of track standing, whether or not there is anything to run on it. |
| 741 | 1 | `public double fleet()` | Wagon sets owned. |
| 744 | 1 | `public double setsNeeded()` | ...and how many the track standing would need. |
| 747 | 1 | `public double replacementSets()` | What the fleet wears out a month, in sets: the replacements it asks for for ever (0.7.29, the railway page's line; haul()'s wear). |
| 762 | 6 | `public double bid(Good g)` | What it asks the market for: the gap between the fleet it has and the fleet its track needs. |

### the Haulage division's seam (FR2) (lines 769-900)

| line | len | member | says |
|---:|---:|---|---|
| 780 | 1 | `public Haulage haulage()` | The Haulage division: the trucks that carry the city leg (FR2). |
| 794 | 25 | `public void updateWages(double[] wagePerType, long[] posts)` | The wage bill on the posts it offers: its depots' drivers for the trucks they keep (Haulage.strikeDrivers()), every other post whole - the same whole numbers the city counts (BuildingManager.getPostsOfferedBySector()). |
| 827 | 5 | `public double ownPayroll()` | The payroll of the posts on the buildings it owns - the track's and the depots' - at the fill rates (FR3): what getPayroll() was before the berths' crews came to this sector, struck on the same operands in the same or... |
| 834 | 1 | `public double railPayroll()` | The Rail division's payroll line (FR3): its share of ownPayroll(), what is left of the sector's payroll after the Ports division's crews and Haulage's depots; what the railway's rule reads (monthlyCost()). |
| 837 | 1 | `public double haulagePayroll()` | ...and Haulage's (FR3): its share of ownPayroll(), the depots' posts less the drivers laid off; what its rule reads (Haulage.closeMonth()). |
| 840 | 5 | `public double portsPayroll()` | ...and the payroll of the crews it works for the city, the sea terminals' dockers, at the fill rates (FR3): what the city's transit bill paid for them before; 0 with no terminal. |
| 847 | 6 | `public double portsPayrollShare()` | The berths' crews' share of the payroll it pays (FR3): portsPayroll() over getPayroll(); 0 with no terminal or no payroll. |
| 860 | 11 | `public double[] portsCrewByKind()` | The month's crew wages by kind of cargo, in Ports.Cargo order (FR3): each kind's terminals' posts at the wage of each tier and its fill - what the city paid for them before the crews moved - for the Ports division to ... |
| 873 | 1 | `double trucksHeld()` | Trucks owned: the TRUCKS pantry. |
| 876 | 1 | `void seedTrucks(double trucks)` | The first fleet, free, the month there is a city leg to carry (Haulage.closeMonth(); Sector.vansKnown's reason). |
| 879 | 4 | `double trucksBought()` | What the month's trucks cost it, bought at home and abroad: the statement in progress's TRUCKS, before the strike banks it (Haulage's running cost). |
| 885 | 1 | `void wearTrucks(double trucks)` | A month of the trucks' wear, out of the pantry. |
| 888 | 4 | `double truckPrice()` | What a truck is worth on this sector's books: the market's price as getInventoryValue() reads it. |
| 894 | 3 | `Markets.Draw drawDiesel(double litres, Sectors sectors)` | The trucks' diesel, drawn as a buyer as the railway's is: the refiners' shelf first, the world for the rest. |
| 899 | 1 | `void bookHaulage(double amount)` | Haulage's bill, as this sector's other revenue; named "Haulage billed" (nameOtherRevenue()). |

### the Ports division's seam (FR3) (lines 901-986)

| line | len | member | says |
|---:|---:|---|---|
| 904 | 1 | `public PortDivision portDivision()` | The Ports division: the crews of the city's berths, who bill the handling and pay the city its dues (FR3). |
| 907 | 1 | `Game gameOf()` | The game this sector plays in, for the division's rate and dial; null in a bare fixture. |
| 910 | 1 | `ham.citybuildersim.BuildingManager buildingsOf()` | The city's buildings, for the division's berths and their book value. |
| 913 | 1 | `void bookPortHandling(double amount)` | The Ports division's handling bill, as this sector's other revenue; named "Port handling billed" (nameOtherRevenue()). |
| 916 | 1 | `void payPortDues(double amount)` | ...and the dues it pays the city, an operating cost named "Port dues": its cash falls by them at the strike and the treasury takes them in the same month (Sector.payTheCity()). |
| 919 | 6 | `double diplomaWage()` | What a post on the DIPLOMA tier costs a month, the drivers' tier; nothing with no game to ask. |
| 939 | 6 | `double haulageShareOf(ToDoubleFunction<BuildingsTemplate> f)` | The Truck Depots' share of one of this sector's cost lines, by a figure of their buildings: the finished depots' sum of it over every finished building's, or 0 when there is none to share (no buildings, or none that h... |
| 958 | 9 | `public double haulagePayrollShare()` | ...and its payroll's share by the posts OFFERED (FR2, ★2): a depot's drivers for the places no truck fills are laid off and paid by nobody (Haulage.strikeDrivers()), so they are in neither side of the ratio. |
| 979 | 7 | `double haulageShareOfSites(ToDoubleFunction<BuildingsTemplate> f)` | ...and the same share for the BALANCE SHEET's lines, the buildings' value and the ground under them, which count the buildings on site as well as the finished (EconomyManager.pushBalanceSheetInputs(): "Buildings finis... |

### what it is doing, for everyone else (lines 987-1096)

| line | len | member | says |
|---:|---:|---|---|
| 989 | 1 | `public double getQuote()` |  |
| 990 | 1 | `public double getCapacityTonnes()` |  |
| 992 | 1 | `public double getTradeTonnes()` | Every tonne that crossed the boundary in the month, by sea as well (0.7.86). |
| 995 | 1 | `public double getOfferedTonnes()` | ...and what of it the railway was offered: all of it, less what the berths that go before it took (0.7.86). |
| 996 | 1 | `public double getHauledTonnes()` |  |
| 999 | 1 | `public double railedIn(ham.citybuildersim.Ports.Cargo k)` | Tonnes of a kind the railway landed, the month billed (0.8.2, batch FR1): the map's trains' to count, read by nothing in the model. |
| 1001 | 1 | `public double railedOut(ham.citybuildersim.Ports.Cargo k)` | ...and shipped. |
| 1002 | 1 | `public double getTightness()` |  |
| 1003 | 1 | `public double getTruckBill()` |  |
| 1004 | 1 | `public double getHaulageBilled()` |  |
| 1005 | 1 | `public double getFuelBill()` |  |
| 1008 | 1 | `public double getFuelImported()` | ...and the part of it bought from the world (0.7.62): all of it with no refinery in the city. |
| 1011 | 1 | `public double getFuelLitres()` | The litres of diesel the last haul drew (0.7.95): its tonnes at FUEL_LITRES_PER_TONNE; none after a load until the next. |
| 1014 | 1 | `public double getFuelLitresImported()` | ...and the litres of it the world sold, the refiners' tanks having none left (Markets.draw()). |
| 1023 | 1 | `public double getAllowedRevenue()` | What a month has to bring in: everything it costs to run, plus the return on the track. |
| 1026 | 1 | `public double getRunningCost()` | The railway's running cost in the month billed (FR2): the Rail division's share of the sector's lines and its fuel - what its bill is measured against now that the sector's net income holds the trucks' as well. |
| 1039 | 1 | `public double getPaidAbroad()` | THE MONTH'S LORRY BILL, THREE WAYS (0.7.29). |
| 1042 | 1 | `public double getKept()` | ...and what the shippers kept: the lorry bill less what was billed at home and what was paid abroad, never below zero (NaN while the split is not known). |
| 1045 | 1 | `public double getScarcity()` | The multiple a network that cannot reach the city's freight can charge on top. |
| 1048 | 1 | `public double[] getCarried()` | The share of each stream the railway is carrying, for the road relief. |
| 1056 | 6 | `public double railShareInForce(Good g)` | The railway's share of one good's crossing tonnes in force now (FR2): its stream's carried share, less what the city's berths take first (Ports.railShareOf()); 0 for a good with no freight stream. |
| 1077 | 1 | `public void reapplyBand()` | Puts the band back where the saved month left it. |
| 1080 | 13 | `public void reapplyBand(ham.citybuildersim.Ports ports)` | ...with the city's ports' shares in force (0.7.86): step 5's lines, as haul() leaves them. |
| 1095 | 1 | `public double lorryRatePerTonne()` | What a tonne of the city's own freight costs by lorry, this month - of the freight the railway was offered, the berths that go before it having taken theirs (0.7.86). |

### PLANNING - the freight nobody is carrying (lines 1097-1262)

| line | len | member | says |
|---:|---:|---|---|
| 1117 | 22 | `public double estimatedMonthlyProfit(BuildingsTemplate t, BusinessInvestment plans)` | What one more line would earn: the freight it could pick up, at today's quote, less what it costs to run and to stand. |
| 1147 | 92 | `public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game)` | Manufacturing's shape: the best template by profit over cost, floored on staffing. |
| 1246 | 3 | `public double[] retirementDemandAndCapacity(Game game)` | Tonnes against nameplate: a city whose trade collapses should sell the track, and this is the one sector in the game whose demand series is neither a good nor a headcount. |
| 1251 | 1 | `public double unitsOf(BuildingsTemplate t)` |  |
| 1259 | 3 | `public boolean mayRetire(BuildingsTemplate t)` | A Truck Depot is never retired by the railway's measure (FR2): the retirement rule weighs track against the tonnes offered it, and a depot has no track to weigh. |

### the books (lines 1263-1297)

| line | len | member | says |
|---:|---:|---|---|
| 1271 | 19 | `protected Map<String, Double> nameOtherRevenue()` | ITS WHOLE REVENUE HAS A NAME, because none of it is the sale of a good. |
| 1293 | 1 | `public ham.citybuildersim.SectorStatements.Format statementFormat()` | Its formal statements' format (0.7.74, spec-sector-statements 4.6): a carrier, read by its revenue a worker. |
| 1296 | 1 | `public String inputLabel()` |  |

### the screen (lines 1298-1390)

| line | len | member | says |
|---:|---:|---|---|
| 1301 | 1 | `public boolean hasPlantBlock()` |  |
| 1304 | 86 | `public List<Sector.Line> ownLines(Game game)` |  |

### save/load (lines 1391-1505)

| line | len | member | says |
|---:|---:|---|---|
| 1394 | 31 | `protected void saveExtras(Map<String, Double> extras)` |  |
| 1427 | 33 | `protected void restoreExtras(Map<String, Double> extras)` |  |
| 1462 | 18 | `protected void resetExtras()` |  |
| 1490 | 15 | `protected void redenominateExtras(double scale)` | THE QUOTE IS NOT MONEY AND DOES NOT MOVE, which is the whole reason it was written as a share of the lorry rate. |

