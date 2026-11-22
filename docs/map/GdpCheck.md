# GdpCheck.java - 1,214 lines · 10 methods · 1 constants · harnesses

`ham/citybuildersim/GdpCheck.java` - generated 2026-11-22 by CodeMap; line numbers are as of that run.

> Verifies the national accounts: the identity, growth rates, and the government's books - and, since 0.7.58, that every good a sector holds is stock in them (a fleet bought abroad among them).
> 
> FS17: the three city fixtures that took a held good out of a played town (the railway's fleet, the copper refinery's yard, the bakeries'
> and plants' sheds) used to rest on the town a played 36 months left: that the good was worth more than the town makes in a month was a
> fact of that played town, so another batch moving it could turn them red. Each now reads the town's last month's GDP and CAUSES the
> ratio - the spurs laid, the refineries, bakeries and plants stood are as many as hold MONTHS_OF_OUTPUT months of it, counted from the
> model's own sizes (Freight.TONNES_PER_SET and the spur's rail capacity, the template's stock, the good's price) - and the premise is
> still asserted on the month that reads it.

**Uses:** [Good](Good.md) (102), [NationalAccounts](NationalAccounts.md) (81), [Game](Game.md) (19), [GameFiles](GameFiles.md) (16), [Sector](Sector.md) (14), [EconomyManager](EconomyManager.md) (10), [BuildingManager](BuildingManager.md) (9), [BuildingsTemplate](BuildingsTemplate.md) (8), [Founding](Founding.md) (7), [GoodsMarket](GoodsMarket.md) (6), [LongPlaytest](LongPlaytest.md) (6), [Freight](Freight.md) (4), [Sectors](Sectors.md) (4), [CopperRefining](CopperRefining.md) (4), [Trade](Trade.md) (4), [BusinessServices](BusinessServices.md) (4), [MoneyAudit](MoneyAudit.md) (4), [Manufacturing](Manufacturing.md) (3), [Retail](Retail.md) (2), [Debt](Debt.md) (1), [WoodProducts](WoodProducts.md) (1), [HeavyIndustry](HeavyIndustry.md) (1), [RailCheck](RailCheck.md) (1), [CopperMining](CopperMining.md) (1), [Markets](Markets.md) (1), [Statement](Statement.md) (1)

## Sections

| line | section |
|---:|---|
| 53 | · 1. the identity |
| 70 | · 2. stock is a CHANGE |
| 82 | · 3. THE BUG THIS REPLACES |
| 97 | · 3c. the two ways it went negative anyway |
| 402 | · 3b. exports, the first the city has ever had |
| 430 | · 4. annual and per capita |
| 448 | · 5. growth |
| 473 | · 6. government books |
| 505 | · 4. THE INTEREST LINE IS REAL |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 20 | `GdpCheck.MONTHS_OF_OUTPUT` | `3` | FS17: how many months of the town's last GDP a fixture's fleet, yard or shed is sized to be worth, so the month that loses it reads negative if the accounts book the loss as a fall. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 17 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 15 | 1200 | **type** `public class GdpCheck` | Verifies the national accounts: the identity, growth rates, and the government's books - and, since 0.7.58, that every good a sector holds is stock in them (a fleet bought abroad among them). |
| 22 | 6 | `static void check(String label, double actual, double expected)` |  |
| 29 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 35 | 4 | `static int asManyAsWorth(double gdp, double each)` | FS17: how many of a thing worth `each` hold MONTHS_OF_OUTPUT months of `gdp`, the last month's: at least one, and one when the thing has no worth to count by. |
| 41 | 4 | `static int heldAt(Good g)` | Where a good stands in NationalAccounts.HELD. |
| 46 | 830 | `public static void main(String[] args)` |  |
| 878 | 53 | `static void forecastPrices()` | GDP1: a new input band must reach every works' next run decision. |
| 939 | 62 | `static void delayedExports()` | GDP1: an imported feed made into stock before the accounts, exported after them. |
| 1024 | 121 | `static void sheds(java.io.PrintStream quiet, java.io.PrintStream out)` | ...AND IN A CITY (0.7.136, batch FX1): a town's bakeries and materials plants retired with their sheds full. |
| 1156 | 24 | `static void bankUpkeep()` | ...AND A BANK'S UPKEEP BOUGHT ABROAD (HL1, 2026-11-11, the review's finding): GDP is C + I + G + exports less imports, and the city's upkeep is bought of Business Services, which imports the share of it a sector of it... |
| 1182 | 32 | `private static void bankBooks(String label, boolean bare, Game c, double bill)` | One upkeep city's books, the month after the bank's operating cost `bill` was paid: the sectors' statements against the accounts'. |

