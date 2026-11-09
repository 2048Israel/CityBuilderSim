# BuildersShort.java - 145 lines · 10 methods · 7 constants · model

`ham/citybuildersim/BuildersShort.java` - generated 2026-11-09 by CodeMap; line numbers are as of that run.

> The builders short (FC1): what an order pays a point of work as the builders run out of points for the sites - a
> premium that rises with the months of work queued at the pace the builders leave the sites - and when a foreign
> contractor takes the orders instead. Pure arithmetic: BuildingManager reads it for every price (orderPriceMultiplier(),
> nonMaterialPricePerPoint()), Game for the contractor's month (foreignCrewsOffer()), and the screens and the harness
> call these functions and never copy them.
> 
> WHY. Jerus's 2026-11-01 report on his own city: "in a sufficiently big city, on a specific sequence of events, a
> city could end up, where there are no crews to build, they are all repairing. and it gets stuck, no way to fix it".
> The month's repairs take the builders' points before the sites (Game.getBuildingOutput()), and when they take them
> all nothing advances, the builders' own depot waits in the same queue for points the repairs never leave
> (Construction.plan(): "the repairs take every crew it has"), and the price of a point did not read the queue, so the
> orders kept coming at the old price. Now the price reads it - every order is dearer for the work already queued - and
> when the sites are left with none, a foreign contractor builds the builders' own depots first and the city climbs out.
> 
> AN ORDER'S OWN BUILDINGS (BA1). With no cap on orders (BusinessInvestment.MAX_CONCURRENT_ORDERS and MAX_ORDER_MONTHS
> went), an order of six hundred homes would take years of the queue at the price before it, every home at the price of
> the first. Now each of its buildings is priced on the queue the ones before it leave, as each order of a run already is
> (Game.buildRunInvoice()): an order of n pays the mean of the premium over the n queues it stands in
> (meanPremium(), multiplier()). One building is priced exactly as FC1 priced it.

**Uses:** [BusinessInvestment](BusinessInvestment.md) (1)

**Used by (6):** [BuildScreen](BuildScreen.md), [BuildingManager](BuildingManager.md), [CityNeeds](CityNeeds.md), [ConstructionScreen](ConstructionScreen.md), [ConstructionTrapCheck](ConstructionTrapCheck.md), [Game](Game.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 33 | `BuildersShort.KNEE_MONTHS` | `BusinessInvestment.BACKLOG_MONTHS_BEFORE_EXPANDING` | Months of work queued at the sites' pace at or under which a point carries no premium: the backlog the builders themselves expand at (BusinessInvestment.BACKLOG_MONTHS_BEFORE_EXPANDING); three city-months in four in t... |
| 36 | `BuildersShort.HALF_MONTHS` | `24` | Months past the knee at which the premium is half its ceiling. |
| 39 | `BuildersShort.CEILING` | `0.8` | The most the premium adds to the price of a point's work, as a share of it: what an order pays when the builders have no points left for sites. |
| 42 | `BuildersShort.FOREIGN_FLOOR` | `0.05` | The share of the builders' month (every post offered) under which what the repairs leave the sites counts as none, and the foreign contractor takes the orders. |
| 45 | `BuildersShort.FOREIGN_PRICE` | `2.0` | The contractor's price for a point, a multiple of the city's price before any premium: above the ceiling's 1 + CEILING, so no city prefers it. |
| 48 | `BuildersShort.FOREIGN_CAPACITY_SHARE` | `0.25` | The contractor's month as a share of the builders' month, every post offered; never less than the points to build the builders' largest depot, so a depot can be built in a month. |
| 90 | `BuildersShort.EXACT_SUM_MOST` | `64` | The largest order whose premium is the exact mean over its buildings (meanPremium()); a larger one reads the curve's integral, the same figure to the precision meanPremium() states. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 24 | 122 | **type** `public final class BuildersShort` | The builders short (FC1): what an order pays a point of work as the builders run out of points for the sites - a premium that rises with the months of work queued at the pace the builders leave the sites - and when a ... |
| 26 | 1 | `private BuildersShort()` |  |
| 54 | 7 | `public static double monthsQueued(double queuePoints, double sitesAtEveryPost)` | The months of work queued at the pace the sites get: queue over sites. |
| 67 | 7 | `public static double premium(double months)` | What a point's work carries over its price at that many months queued, as a share of it: none at or under KNEE_MONTHS, then CEILING * x^2 / (1 + x^2) for x the months past the knee over HALF_MONTHS - no slope at the k... |
| 80 | 3 | `public static boolean foreignTakesOrders(double sitesAtEveryPost, double outputAtEveryPost)` | Whether the foreign contractor takes the orders: the builders' points for sites, every post offered, are under FOREIGN_FLOOR of their whole month, every post offered. |
| 85 | 3 | `public static double multiplier(double months, boolean foreign)` | What an order's non-material price is multiplied by: the contractor's price while it takes orders, else one and the premium at that many months queued. |
| 107 | 17 | `public static double meanPremium(double fromMonths, double stepMonths, long n)` | The mean premium over an order of {@code n} buildings, each priced on the queue the ones before it leave (BA1): the building i (from 0) stands in {@code fromMonths + i * stepMonths} months queued, where {@code stepMon... |
| 126 | 5 | `private static double antiderivative(double months)` | The area under premium() from the knee to that many months queued; nothing at or under the knee. |
| 132 | 3 | `private static double clamp(double premium)` |  |
| 137 | 3 | `public static double multiplier(double fromMonths, double stepMonths, long n, boolean foreign)` | ...an order of {@code n} buildings' multiplier (BA1): the contractor's price while it takes orders, else one and meanPremium(); multiplier(fromMonths, foreign) exactly for one. |
| 142 | 3 | `public static double foreignCapacity(double outputAtEveryPost, double largestDepotPoints)` | The contractor's month, in points: FOREIGN_CAPACITY_SHARE of the builders' month, every post offered, and never less than the builders' largest depot, so one can be built in a month. |

