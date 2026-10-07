# Rail.java - 891 lines · 37 methods · 10 constants · sectors

`ham/citybuildersim/sectors/Rail.java` - generated 2026-10-07 by CodeMap; line numbers are as of that run.

> The railway. THE TWELFTH SECTOR (2026-09-16, Jerus's call).
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
> Retail's shelf rule in shape, as it was until 0.7.43 - a floor that pays
> for the business and a scarcity term that lifts it (the shelf aims at the
> price that clears since, Retail.repriceShelf()) - but the floor is a
> REGULATED NETWORK'S and not a shop's, and that difference was measured
> rather than guessed. See TARGET_RETURN for the run that made the case.
> 
> ... (39 more lines in the source)

**Uses:** [Traffic](Traffic.md) (21), [Good](Good.md) (16), [BusinessInvestment](BusinessInvestment.md) (12), [BuildingsTemplate](BuildingsTemplate.md) (6), [Formats](Formats.md) (5), [Sector](Sector.md) (4), [Game](Game.md) (3), [GoodsMarket](GoodsMarket.md) (2), [BuildingType](BuildingType.md) (1), [Sectors](Sectors.md) (1), [Markets](Markets.md) (1)

**Used by (9):** [BuildCardCheck](BuildCardCheck.md), [BuildScreen](BuildScreen.md), [Game](Game.md), [GdpCheck](GdpCheck.md), [InfrastructureScreen](InfrastructureScreen.md), [OilCheck](OilCheck.md), [RailCheck](RailCheck.md), [SaveFileCheck](SaveFileCheck.md), [Sectors](Sectors.md)

## Sections

| line | section |
|---:|---|
| 326 | THE MONTH |
| 522 | · the fleet |
| 556 | · what it is doing, for everyone else |
| 629 | PLANNING - the freight nobody is carrying |
| 750 | · the books |
| 769 | · the screen |
| 817 | · save/load |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 134 | `Rail.RAIL_FLOOR` | `.30` | The cheapest the railway will ever quote, as a share of the lorry rate. |
| 137 | `Rail.OPENING_QUOTE` | `.60` | What a city with no railway assumes the first line could charge. |
| 158 | `Rail.MIN_LINE_UTILISATION` | `.60` | How much of a line's nameplate has to be freight nobody is carrying before it is worth laying. |
| 177 | `Rail.TONNES_PER_SET` | `2500` | Tonnes a month one wagon set can haul. |
| 180 | `Rail.SET_LIFE_MONTHS` | `240` | How long a wagon set lasts before it is scrap. |
| 211 | `Rail.TARGET_RETURN` | `.012` | What it tries to earn a month ON THE TRACK IT HAS SUNK, before scarcity. |
| 214 | `Rail.MAX_SCARCITY_MULTIPLE` | `1.6` | How far a network that cannot keep up can push the quote above cost-plus. |
| 217 | `Rail.REPRICE_SPEED` | `.25` | How fast the quote walks to where it should be. |
| 245 | `Rail.WORLD_FUEL_PER_TONNE` | `.03` | What a tonne of haulage burns, IN THE WORLD'S MONEY. |
| 253 | `Rail.FUEL_LITRES_PER_TONNE` | `WORLD_FUEL_PER_TONNE / Good.FUEL.worldImportPrice()` | Litres of fuel a tonne hauled burns (0.7.62): WORLD_FUEL_PER_TONNE over a litre's import price (Good.FUEL), eighteen - so a city with no refinery pays the railway's fuel bill it always paid, at the world's price level. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 256 | `private double quote` | The share of the lorry rate it is charging, today. |
| 263 | `private final double[] carried` | What it is actually carrying, by stream - the share in force for the month now running, and therefore the share the band was set from and the share the invoice will be raised at when that month is struck. |
| 266 | `private double rTonnes, rHauled, rTruckBill, rHaulage, rFuel` | the month just billed, for the screens |
| 269 | `private double rFuelImported` | The part of the month's fuel the world sold it (0.7.62). |
| 270 | `private double rCapacity, rTightness, rFx` |  |
| 278 | `private double rPaidAbroad` | The part of the month's lorry bill that went abroad with the cargo (0.7.29): what the lorries were paid for the tonnes the railway did not carry, at the shares the month was billed at. |
| 290 | `private boolean allowedKnown` | Whether the two above are this railway's own figures (0.7.29). |
| 306 | `private boolean fleetKnown` | Whether this railway's fleet is a figure it actually knows. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 121 | 771 | **type** `public final class Rail extends Sector` | The railway. |
| 308 | 17 | `public Rail()` |  |

### THE MONTH (lines 326-521)

| line | len | member | says |
|---:|---:|---|---|
| 353 | 134 | `public void haul(Sectors sectors)` | Bills last month's freight, re-prices, and moves the band for the month about to run. |
| 493 | 5 | `private double monthlyCost(double fuelBill)` | What a month of railway costs to RUN. |
| 510 | 11 | `private double rateBase()` | The capital the return is measured against: the track and the ground under it, at what the balance sheet says they are worth. |

### the fleet (lines 522-555)

| line | len | member | says |
|---:|---:|---|---|
| 525 | 3 | `public double trackTonnes()` | Tonnes a month of track standing, whether or not there is anything to run on it. |
| 530 | 1 | `public double fleet()` | Wagon sets owned. |
| 533 | 1 | `public double setsNeeded()` | ...and how many the track standing would need. |
| 536 | 1 | `public double replacementSets()` | What the fleet wears out a month, in sets: the replacements it asks for for ever (0.7.29, the railway page's line; haul()'s wear). |
| 551 | 4 | `public double bid(Good g)` | What it asks the market for: the gap between the fleet it has and the fleet its track needs. |

### what it is doing, for everyone else (lines 556-628)

| line | len | member | says |
|---:|---:|---|---|
| 558 | 1 | `public double getQuote()` |  |
| 559 | 1 | `public double getCapacityTonnes()` |  |
| 560 | 1 | `public double getTradeTonnes()` |  |
| 561 | 1 | `public double getHauledTonnes()` |  |
| 562 | 1 | `public double getTightness()` |  |
| 563 | 1 | `public double getTruckBill()` |  |
| 564 | 1 | `public double getHaulageBilled()` |  |
| 565 | 1 | `public double getFuelBill()` |  |
| 568 | 1 | `public double getFuelImported()` | ...and the part of it bought from the world (0.7.62): all of it with no refinery in the city. |
| 577 | 1 | `public double getAllowedRevenue()` | What a month has to bring in: everything it costs to run, plus the return on the track. |
| 589 | 1 | `public double getPaidAbroad()` | THE MONTH'S LORRY BILL, THREE WAYS (0.7.29). |
| 592 | 1 | `public double getKept()` | ...and what the shippers kept: the lorry bill less what was billed at home and what was paid abroad, never below zero (NaN while the split is not known). |
| 595 | 1 | `public double getScarcity()` | The multiple a network that cannot reach the city's freight can charge on top. |
| 598 | 1 | `public double[] getCarried()` | The share of each stream the railway is carrying, for the road relief. |
| 614 | 11 | `public void reapplyBand()` | Puts the band back where the saved month left it. |
| 627 | 1 | `public double lorryRatePerTonne()` | What a tonne of the city's own freight costs by lorry, this month. |

### PLANNING - the freight nobody is carrying (lines 629-749)

| line | len | member | says |
|---:|---:|---|---|
| 649 | 10 | `public double estimatedMonthlyProfit(BuildingsTemplate t, BusinessInvestment plans)` | What one more line would earn: the freight it could pick up, at today's quote, less what it costs to run and to stand. |
| 667 | 69 | `public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game)` | Manufacturing's shape: the best template by profit over cost, floored on staffing. |
| 743 | 3 | `public double[] retirementDemandAndCapacity(Game game)` | Tonnes against nameplate: a city whose trade collapses should sell the track, and this is the one sector in the game whose demand series is neither a good nor a headcount. |
| 748 | 1 | `public double unitsOf(BuildingsTemplate t)` |  |

### the books (lines 750-768)

| line | len | member | says |
|---:|---:|---|---|
| 758 | 7 | `protected Map<String, Double> nameOtherRevenue()` | ITS WHOLE REVENUE HAS A NAME, because none of it is the sale of a good. |
| 767 | 1 | `public String inputLabel()` |  |

### the screen (lines 769-816)

| line | len | member | says |
|---:|---:|---|---|
| 772 | 1 | `public boolean hasPlantBlock()` |  |
| 775 | 41 | `public List<Sector.Line> ownLines(Game game)` |  |

### save/load (lines 817-891)

| line | len | member | says |
|---:|---:|---|---|
| 820 | 19 | `protected void saveExtras(Map<String, Double> extras)` |  |
| 841 | 22 | `protected void restoreExtras(Map<String, Double> extras)` |  |
| 865 | 8 | `protected void resetExtras()` |  |
| 883 | 8 | `protected void redenominateExtras(double scale)` | THE QUOTE IS NOT MONEY AND DOES NOT MOVE, which is the whole reason it was written as a share of the lorry rate. |

