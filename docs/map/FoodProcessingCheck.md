# FoodProcessingCheck.java - 511 lines · 7 methods · 3 constants · harnesses

`ham/citybuildersim/FoodProcessingCheck.java` - generated 2026-11-06 by CodeMap; line numbers are as of that run.

> The third of the shelf that arrives already made.
> 
> WHAT THIS HAS TO PROVE. Not that the eleventh sector makes money - whether it
> does depends on the city, which is the point of it, and the run that shows
> one is in claude/. What is checkable is the four things that cost this batch
> its four calibration passes, each of which was a real fault that a green
> suite did not catch:
> 
>   1. ALL THREE PLANTS CAN BE BUILT. The generic planner sizes a sector by one
>      good and skips every template that does not make it, so two of the three
>      were invisible: not refused, not scored badly, never considered. The
>      test is that the sector's own planner reaches each of them.
> 
>   2. A PLANT'S OUTPUT IS PRICED AT WHAT IT WILL FETCH ONCE IT IS RUNNING,
>      not at the ceiling a good with no domestic maker stands at. The first
>      Meat Works was financed against $8.90 processed meat and opened into
>      $7.70.
> 
>   3. THE SALES TAX IS IN THE ESTIMATE. It is a VAT with no credit behind an
>      import, so a plant on imported meat pays it on the whole ticket - $5k a
>      month against $1k of operating income, five times the profit, invisible
>      to the generic estimate.
> 
>   4. THE PLANTS EARN A WAGE. Revenue per worker is the one ratio that decides
>      whether a maker survives a city growing up, because the wage is the cost
>      that follows the city and the price is not. The first calibration had
>      these three at $6.0k-$10.7k a head against the bakeries' $13.5k-$17.8k,
>      and every one of them died.
> 
>   5. A PLANT IS COUNTED IN THE MONEY THE RETIREMENT MEASURE IS IN. The
>      sector's spare capacity is a sum of dollars, and a plant was counted in
>      kilograms, so the rule sold nothing at the founding's price level and
>      something at today's (RT1).
> 
> Every fixture CAUSES its condition: the meat price is set, not waited for.

**Uses:** [Good](Good.md) (26), [BuildingsTemplate](BuildingsTemplate.md) (13), [LongPlaytest](LongPlaytest.md) (11), [BusinessInvestment](BusinessInvestment.md) (8), [Formats](Formats.md) (7), [JobType](JobType.md) (6), [Game](Game.md) (4), [Equity](Equity.md) (3), [GameFiles](GameFiles.md) (2), [FoodProcessing](FoodProcessing.md) (2), [PriceLevel](PriceLevel.md) (2), [PayTier](PayTier.md) (1), [TempDirs](TempDirs.md) (1), [BuildingManager](BuildingManager.md) (1), [Markets](Markets.md) (1), [TaxPolicy](TaxPolicy.md) (1), [GoodsMarket](GoodsMarket.md) (1)

## Sections

| line | section |
|---:|---|
| 100 | · 1. the five goods it exists for |
| 140 | · 2. three plants, and all three reachable |
| 168 | · 3. what a plant earns per person who runs it |
| 218 | · 4. the estimate: the price it leaves behind, and the tax |
| 346 | · 5. the meat price is the bet, and it is real |
| 431 | · 6. it survives a save, by name |
| 454 | · 7. a plant is counted in the money the measure counts in (RT1) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 68 | `FoodProcessingCheck.MEAT_WORKS` | `"Meat Works"` |  |
| 69 | `FoodProcessingCheck.SNACKS` | `"Snack & Oils Plant"` |  |
| 70 | `FoodProcessingCheck.BOTTLING` | `"Bottling Plant"` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 48 | `static int fails` |  |
| 49 | `static PrintStream out` |  |
| 50 | `static PrintStream quiet` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 46 | 466 | **type** `public class FoodProcessingCheck` | The third of the shelf that arrives already made. |
| 52 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 57 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 62 | 5 | `static void quietly(Runnable r)` |  |
| 72 | 5 | `static double jobsOf(BuildingsTemplate t)` |  |
| 78 | 5 | `static double payrollOf(BuildingsTemplate t)` |  |
| 85 | 9 | `static double midBandRevenue(BuildingsTemplate t)` | A template's nameplate valued at the middle of each good's world band, in thousands. |
| 95 | 416 | `public static void main(String[] args) throws Exception` |  |

