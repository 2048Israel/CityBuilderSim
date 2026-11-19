# LuxuryRetailCheck.java - 249 lines · 10 methods · 2 constants · harnesses

`ham/citybuildersim/LuxuryRetailCheck.java` - generated 2026-11-19 by CodeMap; line numbers are as of that run.

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
>   2. A TOWN'S BOUTIQUES BOOK IT. A town given BOUTIQUES_ASKED Boutiques and a queue at the planner's margin that
>      fills them, staffed and stocked, sells every month, and a year of its books puts payroll over value added
>      (LS1's share: the margin less power, water and upkeep) inside the band. Twelve months summed, as LS1's probe
>      sums them: a share is a ratio of two flows, and one month of one sector is noise.
> 
>   3. ...AND A BOUTIQUE STILL PAYS. The investor builds against estimatedMonthlyProfit(); with a queue past the
>      counters standing of a whole Boutique's coverage (so the estimate reads the queue, not its half-coverage
>      fallback), a shop whose payroll is BEA's share of its margin still has the rest, so one more Boutique is worth
>      building and no city's queue stands at a door nobody will open.
> 
> Every fixture CAUSES its condition: the town is given its shops, and Luxury Retail's planner is held so the count is
> the fixture's, not a planner's.

**Uses:** [LuxuryRetail](LuxuryRetail.md) (11), [BuildingsTemplate](BuildingsTemplate.md) (7), [LongPlaytest](LongPlaytest.md) (6), [JobType](JobType.md) (4), [Game](Game.md) (3), [BusinessInvestment](BusinessInvestment.md) (3), [Sectors](Sectors.md) (3), [GameFiles](GameFiles.md) (2), [HouseholdBalance](HouseholdBalance.md) (2), [PayTier](PayTier.md) (1), [TempDirs](TempDirs.md) (1), [Good](Good.md) (1), [PriceLevel](PriceLevel.md) (1), [SectorBooks](SectorBooks.md) (1)

## Sections

| line | section |
|---:|---|
| 115 | · 1. the catalogue strikes the share |
| 136 | · 2. a town's Boutiques book it |
| 220 | · 3. a Boutique still pays |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 75 | `LuxuryRetailCheck.TOWN_HOUSES` | `200` | The fixture town's houses: enough that its workers outnumber its posts, so its wages are the ladder's and not a short town's. |
| 78 | `LuxuryRetailCheck.BOUTIQUES_ASKED` | `2` | The Boutiques the fixture town stands, and the queue its households are given the fortune to ask for: the count is the fixture's, not the town's own demand. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 39 | `static int fails` |  |
| 40 | `static PrintStream out` |  |
| 41 | `static PrintStream quiet` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 37 | 213 | **type** `public class LuxuryRetailCheck` | What a luxury shop's post sells: its payroll over its margin at BEA's mark (LX1, 2026-11-17). |
| 43 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 48 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 53 | 5 | `static void quietly(Runnable r)` |  |
| 59 | 6 | `static BuildingsTemplate template(Game g, String name)` |  |
| 67 | 1 | `static double low()` | The band's floor: BEA's share over LABOUR_SHARE_FACTOR. |
| 70 | 1 | `static double high()` | ...and its top: BEA's share times LABOUR_SHARE_FACTOR. |
| 72 | 1 | `static boolean inBand(double share)` |  |
| 85 | 5 | `static double plannersMargin()` | The margin the planner builds to: it orders counters while the queue at its price stands more than TARGET_HEADROOM over them (LuxuryRetail.plan()), and a queue of (1 + headroom) counters strikes the position (1 + head... |
| 92 | 5 | `static double ladderPayroll(BuildingsTemplate t)` | A template's payroll at the wage ladder, every post filled. |
| 98 | 151 | `public static void main(String[] args) throws Exception` |  |

