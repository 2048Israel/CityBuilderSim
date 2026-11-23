# GapOrderCheck.java - 403 lines · 14 methods · 4 constants · harnesses

`ham/citybuildersim/GapOrderCheck.java` - generated 2026-11-23 by CodeMap; line numbers are as of that run.

> A planner orders to the gap, not one building a month (OG1, 2026-11-22).
> 
> WHY. Jerus: "quite often construction has a huge backlog yet it doesnt
> expand its building capacity to tackle it", and "in big cities, even if
> building on depot every month its not enough, same with every other
> sector". On main's playtest the builders held queues of 20 to 31 months for
> 89 months running on one depot an order, and Food Processing imported up
> to eight plants' worth while it ordered one. A first cut sized the depots
> to the queue and ran the same playtest to 7,771 depots to main's 299 - the
> runaway 0.7.130 measured - so the builders' order is sized to the work
> the city sends them, and this harness holds both halves.
> 
> WHAT THIS HAS TO PROVE, every fixture causing its condition:
> 
>   1. THE BUILDERS' TARGET IS THE WORK COMING IN (Construction.
>      sitesMonthWanted()): the typical month at the knee or under it; past
>      it, the queue past the knee over QUEUE_WORKED_OFF_MONTHS more; and
>      never more than 1 + BuildersShort.CEILING of the typical month however
>      long the queue - the runaway's bound.
> 
>   2. THE TYPICAL MONTH IS THE MEDIAN (BusinessInvestment.
>      typicalMonthOfSiteWork()): one month's spike is worked off, not built
>      for; under MIN_MONTHS_OF_SITE_WORK there is none, and the order is one
>      depot, as before OG1.
> 
>   3. THE RECORD IS THE WORK ORDERED, THE BUILDERS' OWN LEFT OUT: in a
>      played city a month whose orders are houses records their points, and
>      a month whose orders are depots records none - an order of depots is
>      never read as work that calls for more.
> 
>   4. ...AND A SAVE KEEPS IT, so a loaded city sizes its next order as the
>      unbroken one does.
> 
>   5. THE ORDER IS THE GAP: a city that has ordered twice its sites' month
>      every month for a year, its queue past the knee - its own plan orders
>      several depots, where one a month ordered one: no more points than the
>      premium's share of the sites' month, the most that share, the staffing
>      and the plots allow, and never the sites past 1 + CEILING of the work
>      it orders a month; and it holds while they are on site.
> 
>   6. THE TRAP STAYS AT ONE: BuildersFirstCheck's stalled city, given a year
>      of orders that ask for depots it could staff, its sites short of a
>      depot's points on the premium's share of a month, orders one - the
>      premium's share the only cut, so the section fails without it.
> 
>   7. A MAKER SHORT BY SEVERAL PLANTS ORDERS SEVERAL: Food Processing, the
>      city eating k plants' worth of drinks - the n-th plant's fill counts
>      the n - 1 before it, and the plan orders k.

**Uses:** [BusinessInvestment](BusinessInvestment.md) (18), [DeathCareFirstCheck](DeathCareFirstCheck.md) (16), [Game](Game.md) (13), [Construction](Construction.md) (11), [BuildersShort](BuildersShort.md) (8), [BuildingsTemplate](BuildingsTemplate.md) (8), [Good](Good.md) (6), [GameFiles](GameFiles.md) (5), [FoodProcessing](FoodProcessing.md) (3), [BuildersFirstCheck](BuildersFirstCheck.md) (2), [Founding](Founding.md) (2), [BuildingManager](BuildingManager.md) (2), [Sectors](Sectors.md) (1), [TempDirs](TempDirs.md) (1), [Sector](Sector.md) (1), [NewUnit](NewUnit.md) (1), [GoodsMarket](GoodsMarket.md) (1)

## Sections

| line | section |
|---:|---|
| 117 | 1. THE TARGET |
| 141 | THE FIXTURE |
| 174 | 2. THE MEDIAN |
| 203 | 3. THE RECORD |
| 232 | 4. THE SAVE |
| 251 | 5. THE GAP |
| 328 | 6. THE TRAP |
| 365 | 7. A MAKER |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 89 | `GapOrderCheck.BUILDERS` | `Sectors.CONSTRUCTION` |  |
| 90 | `GapOrderCheck.DEPOT` | `"Construction Depot"` |  |
| 91 | `GapOrderCheck.HOUSE` | `"House"` |  |
| 92 | `GapOrderCheck.CLINIC` | `"Walk-in Clinic"` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 65 | `static int fails` |  |
| 66 | `static PrintStream out` |  |
| 67 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 63 | 341 | **type** `public class GapOrderCheck` | A planner orders to the gap, not one building a month (OG1, 2026-11-22). |
| 69 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 74 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 79 | 3 | `static boolean near(double a, double b)` |  |
| 83 | 5 | `static void quietly(Runnable r)` |  |
| 94 | 22 | `public static void main(String[] args) throws Exception` |  |

### 1. THE TARGET (lines 117-140)

| line | len | member | says |
|---:|---:|---|---|
| 119 | 21 | `static void target()` |  |

### THE FIXTURE (lines 141-173)

| line | len | member | says |
|---:|---:|---|---|
| 147 | 26 | `static Game city(Path root, String name)` | A town of 800 houses and eight depots, every sector held, a year played: the builders' month is a few thousand points, which the premium's share of can make several depots. |

### 2. THE MEDIAN (lines 174-202)

| line | len | member | says |
|---:|---:|---|---|
| 176 | 26 | `static void median(Game g)` |  |

### 3. THE RECORD (lines 203-231)

| line | len | member | says |
|---:|---:|---|---|
| 205 | 26 | `static void record(Game g)` |  |

### 4. THE SAVE (lines 232-250)

| line | len | member | says |
|---:|---:|---|---|
| 234 | 16 | `static void saved(Path root, Game g)` |  |

### 5. THE GAP (lines 251-327)

| line | len | member | says |
|---:|---:|---|---|
| 253 | 58 | `static void theGap(Game g)` |  |
| 316 | 11 | `static double aYearOfOrders(Game g, int clinics)` | A year of the city's own orders, the same each month: so many clinics placed by the city, then the month played, the builders held. |

### 6. THE TRAP (lines 328-364)

| line | len | member | says |
|---:|---:|---|---|
| 335 | 29 | `static void trap(Path root)` | BuildersFirstCheck.stalled()'s city, given first a year of orders it cannot keep up with, so the typical month is read and the order passes the record's guard: the work coming in asks for depots the city could staff, ... |

### 7. A MAKER (lines 365-403)

| line | len | member | says |
|---:|---:|---|---|
| 374 | 29 | `static void food(Game g)` | The one restored input in this harness. |

