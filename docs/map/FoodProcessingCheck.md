# FoodProcessingCheck.java - 427 lines · 7 methods · 3 constants · harnesses

`ham/citybuildersim/FoodProcessingCheck.java` - generated 2026-09-22 by CodeMap; line numbers are as of that run.

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
> Every fixture CAUSES its condition: the meat price is set, not waited for.

**Uses:** [Good](Good.md) (25), [BuildingsTemplate](BuildingsTemplate.md) (12), [LongPlaytest](LongPlaytest.md) (11), [Formats](Formats.md) (7), [JobType](JobType.md) (6), [Game](Game.md) (4), [BusinessInvestment](BusinessInvestment.md) (3), [Equity](Equity.md) (3), [GameFiles](GameFiles.md) (2), [FoodProcessing](FoodProcessing.md) (2), [PayTier](PayTier.md) (1), [BuildingManager](BuildingManager.md) (1), [Markets](Markets.md) (1), [TaxPolicy](TaxPolicy.md) (1), [GoodsMarket](GoodsMarket.md) (1)

## Sections

| line | section |
|---:|---|
| 96 | · 1. the five goods it exists for |
| 132 | · 2. three plants, and all three reachable |
| 160 | · 3. what a plant earns per person who runs it |
| 210 | · 4. the estimate: the price it leaves behind, and the tax |
| 333 | · 5. the meat price is the bet, and it is real |
| 394 | · 6. it survives a save, by name |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 64 | `FoodProcessingCheck.MEAT_WORKS` | `"Meat Works"` |  |
| 65 | `FoodProcessingCheck.SNACKS` | `"Snack & Oils Plant"` |  |
| 66 | `FoodProcessingCheck.BOTTLING` | `"Bottling Plant"` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 44 | `static int fails` |  |
| 45 | `static PrintStream out` |  |
| 46 | `static PrintStream quiet` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 42 | 386 | **type** `public class FoodProcessingCheck` | The third of the shelf that arrives already made. |
| 48 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 53 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 58 | 5 | `static void quietly(Runnable r)` |  |
| 68 | 5 | `static double jobsOf(BuildingsTemplate t)` |  |
| 74 | 5 | `static double payrollOf(BuildingsTemplate t)` |  |
| 81 | 9 | `static double midBandRevenue(BuildingsTemplate t)` | A template's nameplate valued at the middle of each good's world band, in thousands. |
| 91 | 336 | `public static void main(String[] args) throws Exception` |  |

