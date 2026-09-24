# AgricultureCheck.java - 413 lines · 7 methods · 3 constants · harnesses

`ham/citybuildersim/AgricultureCheck.java` - generated 2026-09-24 by CodeMap; line numbers are as of that run.

> The ground under the loaf.
> 
> WHAT THIS HAS TO PROVE. Not that the city got bigger - it did not, and the
> measurement is in claude/farms.md where a number that will drift belongs.
> What is checkable is whether the tenth sector does the four things it was
> built to do:
> 
>   1. the mills BUY their raw material now, and the chain is four links deep
>   2. crops clear in a band with a real import ceiling, so a city with no
>      fields eats anyway and pays for the privilege
>   3. a farm is ground and almost nothing else - and the clock that runs on
>      that ground is what decides fields against glass
>   4. the farmland dial actually moves the bill, and the planner reads it
> 
> Every fixture CAUSES its condition. The clock test moves the land price
> rather than waiting three centuries for it; the relief test sets the dial
> rather than hoping a run finds it.

**Uses:** [Good](Good.md) (40), [BuildingsTemplate](BuildingsTemplate.md) (14), [Sectors](Sectors.md) (11), [Game](Game.md) (6), [Formats](Formats.md) (5), [JobType](JobType.md) (4), [BuildingManager](BuildingManager.md) (3), [Agriculture](Agriculture.md) (3), [TaxPolicy](TaxPolicy.md) (3), [GameFiles](GameFiles.md) (2), [GoodsMarket](GoodsMarket.md) (2), [EconomyManager](EconomyManager.md) (1), [Sector](Sector.md) (1), [BusinessInvestment](BusinessInvestment.md) (1), [Equity](Equity.md) (1), [PayTier](PayTier.md) (1)

## Sections

| line | section |
|---:|---|
| 75 | · 1. the good, and the chain it closes |
| 166 | · 2. the band, and the city with no fields |
| 182 | · 3. a farm is ground and almost nothing else |
| 276 | · 4. the dial, and what reads it |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 56 | `AgricultureCheck.MIXED` | `"Mixed Farm"` |  |
| 57 | `AgricultureCheck.GRAIN` | `"Grain Farm"` |  |
| 58 | `AgricultureCheck.GLASS` | `"Greenhouse Complex"` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 31 | `static int fails` |  |
| 32 | `static PrintStream out` |  |
| 33 | `static PrintStream quiet` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 29 | 385 | **type** `public class AgricultureCheck` | The ground under the loaf. |
| 35 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 40 | 9 | `static void check(String label, double actual, double expected, double tol)` |  |
| 50 | 5 | `static void quietly(Runnable r)` |  |
| 61 | 8 | `static double atTheFloor(BuildingsTemplate t)` | A template's whole harvest, valued at the world's export floor, in dollars. |
| 70 | 331 | `public static void main(String[] args) throws Exception` |  |
| 402 | 5 | `static double jobsOf(BuildingsTemplate t)` |  |
| 408 | 5 | `static double payrollOf(BuildingsTemplate t)` |  |

