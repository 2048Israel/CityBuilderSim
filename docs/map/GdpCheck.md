# GdpCheck.java - 1,092 lines · 7 methods · 0 constants · harnesses

`ham/citybuildersim/GdpCheck.java` - generated 2026-11-14 by CodeMap; line numbers are as of that run.

> Verifies the national accounts: the identity, growth rates, and the government's books - and, since 0.7.58, that every good a sector holds is stock in them (a fleet bought abroad among them).

**Uses:** [Good](Good.md) (99), [NationalAccounts](NationalAccounts.md) (80), [Game](Game.md) (16), [Sector](Sector.md) (12), [GameFiles](GameFiles.md) (10), [BuildingManager](BuildingManager.md) (9), [EconomyManager](EconomyManager.md) (8), [BuildingsTemplate](BuildingsTemplate.md) (7), [Founding](Founding.md) (6), [GoodsMarket](GoodsMarket.md) (6), [Sectors](Sectors.md) (4), [CopperRefining](CopperRefining.md) (4), [Trade](Trade.md) (4), [Freight](Freight.md) (3), [Manufacturing](Manufacturing.md) (3), [Retail](Retail.md) (2), [Debt](Debt.md) (1), [WoodProducts](WoodProducts.md) (1), [HeavyIndustry](HeavyIndustry.md) (1), [RailCheck](RailCheck.md) (1), [CopperMining](CopperMining.md) (1), [Markets](Markets.md) (1)

## Sections

| line | section |
|---:|---|
| 35 | · 1. the identity |
| 52 | · 2. stock is a CHANGE |
| 64 | · 3. THE BUG THIS REPLACES |
| 79 | · 3c. the two ways it went negative anyway |
| 382 | · 3b. exports, the first the city has ever had |
| 410 | · 4. annual and per capita |
| 428 | · 5. growth |
| 453 | · 6. government books |
| 485 | · 4. THE INTEREST LINE IS REAL |

## Fields (state)

| line | field | says |
|---:|---|---|
| 8 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 6 | 1087 | **type** `public class GdpCheck` | Verifies the national accounts: the identity, growth rates, and the government's books - and, since 0.7.58, that every good a sector holds is stock in them (a fleet bought abroad among them). |
| 10 | 6 | `static void check(String label, double actual, double expected)` |  |
| 17 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 23 | 4 | `static int heldAt(Good g)` | Where a good stands in NationalAccounts.HELD. |
| 28 | 795 | `public static void main(String[] args)` |  |
| 825 | 53 | `static void forecastPrices()` | GDP1: a new input band must reach every works' next run decision. |
| 886 | 62 | `static void delayedExports()` | GDP1: an imported feed made into stock before the accounts, exported after them. |
| 971 | 121 | `static void sheds(java.io.PrintStream quiet, java.io.PrintStream out)` | ...AND IN A CITY (0.7.136, batch FX1): a town's bakeries and materials plants retired with their sheds full. |

