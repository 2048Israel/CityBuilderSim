# GdpCheck.java - 959 lines · 5 methods · 0 constants · harnesses

`ham/citybuildersim/GdpCheck.java` - generated 2026-10-27 by CodeMap; line numbers are as of that run.

> Verifies the national accounts: the identity, growth rates, and the government's books - and, since 0.7.58, that every good a sector holds is stock in them (a fleet bought abroad among them).

**Uses:** [Good](Good.md) (82), [NationalAccounts](NationalAccounts.md) (78), [Game](Game.md) (16), [GameFiles](GameFiles.md) (10), [Sector](Sector.md) (9), [BuildingManager](BuildingManager.md) (5), [Founding](Founding.md) (5), [BuildingsTemplate](BuildingsTemplate.md) (5), [Sectors](Sectors.md) (4), [Freight](Freight.md) (3), [EconomyManager](EconomyManager.md) (3), [Retail](Retail.md) (2), [CopperRefining](CopperRefining.md) (2), [Debt](Debt.md) (1), [WoodProducts](WoodProducts.md) (1), [HeavyIndustry](HeavyIndustry.md) (1), [RailCheck](RailCheck.md) (1), [GoodsMarket](GoodsMarket.md) (1)

## Sections

| line | section |
|---:|---|
| 32 | · 1. the identity |
| 49 | · 2. stock is a CHANGE |
| 61 | · 3. THE BUG THIS REPLACES |
| 76 | · 3c. the two ways it went negative anyway |
| 379 | · 3b. exports, the first the city has ever had |
| 407 | · 4. annual and per capita |
| 425 | · 5. growth |
| 450 | · 6. government books |
| 482 | · 4. THE INTEREST LINE IS REAL |

## Fields (state)

| line | field | says |
|---:|---|---|
| 8 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 6 | 954 | **type** `public class GdpCheck` | Verifies the national accounts: the identity, growth rates, and the government's books - and, since 0.7.58, that every good a sector holds is stock in them (a fleet bought abroad among them). |
| 10 | 6 | `static void check(String label, double actual, double expected)` |  |
| 17 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 23 | 4 | `static int heldAt(Good g)` | Where a good stands in NationalAccounts.HELD. |
| 28 | 791 | `public static void main(String[] args)` |  |
| 842 | 117 | `static void sheds(java.io.PrintStream quiet, java.io.PrintStream out)` | ...AND IN A CITY (0.7.136, batch FX1): a town's bakeries and materials plants retired with their sheds full. |

