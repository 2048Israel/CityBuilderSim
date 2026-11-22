# GapOrderCheck.java - 355 lines · 13 methods · 3 constants · harnesses

`ham/citybuildersim/GapOrderCheck.java` - generated 2026-11-22 by CodeMap; line numbers are as of that run.

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
>   5. THE ORDER IS THE GAP: a city whose typical month outruns its sites and
>      whose queue is past the knee - its own plan orders several depots, the
>      count the target, the premium's share of the month, the plots and the
>      staffing give, where one a month ordered one; and it holds while they
>      are on site.
> 
>   6. THE TRAP STAYS AT ONE: BuildersFirstCheck's stalled city, its sites
>      short of a depot's points on the premium's share of a month, orders
>      one.
> 
>   7. A MAKER SHORT BY SEVERAL PLANTS ORDERS SEVERAL: Food Processing, the
>      city eating k plants' worth of drinks - the n-th plant's fill counts
>      the n - 1 before it, and the plan orders k.

**Uses:** [BusinessInvestment](BusinessInvestment.md) (17), [Construction](Construction.md) (13), [DeathCareFirstCheck](DeathCareFirstCheck.md) (12), [Game](Game.md) (12), [BuildersShort](BuildersShort.md) (8), [BuildingsTemplate](BuildingsTemplate.md) (6), [GameFiles](GameFiles.md) (5), [Good](Good.md) (5), [BuildersFirstCheck](BuildersFirstCheck.md) (3), [FoodProcessing](FoodProcessing.md) (3), [BuildingManager](BuildingManager.md) (2), [Sectors](Sectors.md) (1), [TempDirs](TempDirs.md) (1), [Founding](Founding.md) (1), [Sector](Sector.md) (1), [NewUnit](NewUnit.md) (1), [GoodsMarket](GoodsMarket.md) (1)

## Sections

| line | section |
|---:|---|
| 114 | 1. THE TARGET |
| 138 | THE FIXTURE |
| 171 | 2. THE MEDIAN |
| 200 | 3. THE RECORD |
| 229 | 4. THE SAVE |
| 248 | 5. THE GAP |
| 311 | 6. THE TRAP |
| 326 | 7. A MAKER |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 87 | `GapOrderCheck.BUILDERS` | `Sectors.CONSTRUCTION` |  |
| 88 | `GapOrderCheck.DEPOT` | `"Construction Depot"` |  |
| 89 | `GapOrderCheck.HOUSE` | `"House"` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 63 | `static int fails` |  |
| 64 | `static PrintStream out` |  |
| 65 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 61 | 295 | **type** `public class GapOrderCheck` | A planner orders to the gap, not one building a month (OG1, 2026-11-22). |
| 67 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 72 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 77 | 3 | `static boolean near(double a, double b)` |  |
| 81 | 5 | `static void quietly(Runnable r)` |  |
| 91 | 22 | `public static void main(String[] args) throws Exception` |  |

### 1. THE TARGET (lines 114-137)

| line | len | member | says |
|---:|---:|---|---|
| 116 | 21 | `static void target()` |  |

### THE FIXTURE (lines 138-170)

| line | len | member | says |
|---:|---:|---|---|
| 144 | 26 | `static Game city(Path root, String name)` | A town of 800 houses and eight depots, every sector held, a year played: the builders' month is a few thousand points, which the premium's share of can make several depots. |

### 2. THE MEDIAN (lines 171-199)

| line | len | member | says |
|---:|---:|---|---|
| 173 | 26 | `static void median(Game g)` |  |

### 3. THE RECORD (lines 200-228)

| line | len | member | says |
|---:|---:|---|---|
| 202 | 26 | `static void record(Game g)` |  |

### 4. THE SAVE (lines 229-247)

| line | len | member | says |
|---:|---:|---|---|
| 231 | 16 | `static void saved(Path root, Game g)` |  |

### 5. THE GAP (lines 248-310)

| line | len | member | says |
|---:|---:|---|---|
| 250 | 60 | `static void theGap(Game g)` |  |

### 6. THE TRAP (lines 311-325)

| line | len | member | says |
|---:|---:|---|---|
| 313 | 12 | `static void trap(Path root)` |  |

### 7. A MAKER (lines 326-355)

| line | len | member | says |
|---:|---:|---|---|
| 328 | 27 | `static void food(Game g)` |  |

