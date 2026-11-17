# LuxuryRetailCheck.java - 230 lines · 10 methods · 2 constants · harnesses

`ham/citybuildersim/LuxuryRetailCheck.java` - generated 2026-11-17 by CodeMap; line numbers are as of that run.

> What a luxury shop's post sells: its payroll over its margin at BEA's mark (LX1, 2026-11-17).
> 
> WHY. LS1 (docs/studies/spec-labour-share.md) read Luxury Retail's payroll at 2.2% of its value added where BEA's
> other retail (4A0) pays 41.7%, and found the gap in the sales per post: a Boutique covered 320 pieces of a $22,000
> good a month with seven posts, so each post took 47 mean monthly wages of margin a month where BEA's take 2.4. On
> main after CU2 it read 0.5-1.7% (LX1's notes). No harness read LuxuryRetail before this one, so nothing said what a
> shop's post sells; LX1 struck the two shops' coverage and posts to BEA's share (LuxuryRetail, WHAT A POST SELLS).
> 
> WHAT THIS HAS TO PROVE:
> 
>   1. THE CATALOGUE STRIKES THE SHARE. Each luxury template's payroll at the wage ladder, over the margin a full
>      shop earns at the margin the planner builds to (the position a queue TARGET_HEADROOM over the counters
>      strikes), on what a piece lands at, is LuxuryRetail.LABOUR_SHARE within LABOUR_SHARE_FACTOR.
> 
>   2. A TOWN'S BOUTIQUES BOOK IT. A town given the Boutiques its own queue asks for, at the margin the planner builds
>      to, staffed and stocked, sells, and a year of its books puts payroll over value added (LS1's share: the
>      margin less power, water and upkeep) inside the band. Twelve months summed, as LS1's probe sums them: a share
>      is a ratio of two flows, and one month of one sector is noise.
> 
>   3. ...AND A BOUTIQUE STILL PAYS. The investor builds against estimatedMonthlyProfit(); a shop whose payroll is
>      BEA's share of its margin still has the rest, so the town's Boutique is worth building and no city's queue
>      stands at a door nobody will open.
> 
> Every fixture CAUSES its condition: the town is given its shops, and Luxury Retail's planner is held so the count is
> the fixture's, not a planner's.

**Uses:** [LuxuryRetail](LuxuryRetail.md) (11), [BuildingsTemplate](BuildingsTemplate.md) (7), [LongPlaytest](LongPlaytest.md) (6), [JobType](JobType.md) (4), [Game](Game.md) (3), [BusinessInvestment](BusinessInvestment.md) (3), [Sectors](Sectors.md) (3), [GameFiles](GameFiles.md) (2), [PayTier](PayTier.md) (1), [TempDirs](TempDirs.md) (1), [Good](Good.md) (1), [PriceLevel](PriceLevel.md) (1), [HouseholdBalance](HouseholdBalance.md) (1), [SectorBooks](SectorBooks.md) (1)

## Sections

| line | section |
|---:|---|
| 114 | · 1. the catalogue strikes the share |
| 135 | · 2. a town's Boutiques book it |
| 219 | · 3. a Boutique still pays |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 74 | `LuxuryRetailCheck.TOWN_HOUSES` | `200` | The fixture town's houses: enough that its workers outnumber its posts, so its wages are the ladder's and not a short town's. |
| 77 | `LuxuryRetailCheck.BOUTIQUES_ASKED` | `2` | The Boutiques' worth of queue the fixture's households are given the fortune to ask for. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 38 | `static int fails` |  |
| 39 | `static PrintStream out` |  |
| 40 | `static PrintStream quiet` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 36 | 195 | **type** `public class LuxuryRetailCheck` | What a luxury shop's post sells: its payroll over its margin at BEA's mark (LX1, 2026-11-17). |
| 42 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 47 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 52 | 5 | `static void quietly(Runnable r)` |  |
| 58 | 6 | `static BuildingsTemplate template(Game g, String name)` |  |
| 66 | 1 | `static double low()` | The band's floor: BEA's share over LABOUR_SHARE_FACTOR. |
| 69 | 1 | `static double high()` | ...and its top: BEA's share times LABOUR_SHARE_FACTOR. |
| 71 | 1 | `static boolean inBand(double share)` |  |
| 84 | 5 | `static double plannersMargin()` | The margin the planner builds to: it orders counters while the queue at its price stands more than TARGET_HEADROOM over them (LuxuryRetail.plan()), and a queue of (1 + headroom) counters strikes the position (1 + head... |
| 91 | 5 | `static double ladderPayroll(BuildingsTemplate t)` | A template's payroll at the wage ladder, every post filled. |
| 97 | 133 | `public static void main(String[] args) throws Exception` |  |

