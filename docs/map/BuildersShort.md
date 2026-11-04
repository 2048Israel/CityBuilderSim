# BuildersShort.java - 87 lines · 6 methods · 6 constants · model

`ham/citybuildersim/BuildersShort.java` - generated 2026-11-04 by CodeMap; line numbers are as of that run.

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

**Uses:** [BusinessInvestment](BusinessInvestment.md) (1)

**Used by (6):** [BuildScreen](BuildScreen.md), [BuildingManager](BuildingManager.md), [CityNeeds](CityNeeds.md), [ConstructionScreen](ConstructionScreen.md), [ConstructionTrapCheck](ConstructionTrapCheck.md), [Game](Game.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 27 | `BuildersShort.KNEE_MONTHS` | `BusinessInvestment.BACKLOG_MONTHS_BEFORE_EXPANDING` | Months of work queued at the sites' pace at or under which a point carries no premium: the backlog the builders themselves expand at (BusinessInvestment.BACKLOG_MONTHS_BEFORE_EXPANDING); three city-months in four in t... |
| 30 | `BuildersShort.HALF_MONTHS` | `24` | Months past the knee at which the premium is half its ceiling. |
| 33 | `BuildersShort.CEILING` | `0.8` | The most the premium adds to the price of a point's work, as a share of it: what an order pays when the builders have no points left for sites. |
| 36 | `BuildersShort.FOREIGN_FLOOR` | `0.05` | The share of the builders' month (every post offered) under which what the repairs leave the sites counts as none, and the foreign contractor takes the orders. |
| 39 | `BuildersShort.FOREIGN_PRICE` | `2.0` | The contractor's price for a point, a multiple of the city's price before any premium: above the ceiling's 1 + CEILING, so no city prefers it. |
| 42 | `BuildersShort.FOREIGN_CAPACITY_SHARE` | `0.25` | The contractor's month as a share of the builders' month, every post offered; never less than the points to build the builders' largest depot, so a depot can be built in a month. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 18 | 70 | **type** `public final class BuildersShort` | The builders short (FC1): what an order pays a point of work as the builders run out of points for the sites - a premium that rises with the months of work queued at the pace the builders leave the sites - and when a ... |
| 20 | 1 | `private BuildersShort()` |  |
| 48 | 7 | `public static double monthsQueued(double queuePoints, double sitesAtEveryPost)` | The months of work queued at the pace the sites get: queue over sites. |
| 61 | 7 | `public static double premium(double months)` | What a point's work carries over its price at that many months queued, as a share of it: none at or under KNEE_MONTHS, then CEILING * x^2 / (1 + x^2) for x the months past the knee over HALF_MONTHS - no slope at the k... |
| 74 | 3 | `public static boolean foreignTakesOrders(double sitesAtEveryPost, double outputAtEveryPost)` | Whether the foreign contractor takes the orders: the builders' points for sites, every post offered, are under FOREIGN_FLOOR of their whole month, every post offered. |
| 79 | 3 | `public static double multiplier(double months, boolean foreign)` | What an order's non-material price is multiplied by: the contractor's price while it takes orders, else one and the premium at that many months queued. |
| 84 | 3 | `public static double foreignCapacity(double outputAtEveryPost, double largestDepotPoints)` | The contractor's month, in points: FOREIGN_CAPACITY_SHARE of the builders' month, every post offered, and never less than the builders' largest depot, so one can be built in a month. |

