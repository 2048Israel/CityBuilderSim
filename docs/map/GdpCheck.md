# GdpCheck.java - 746 lines · 4 methods · 0 constants · harnesses

`ham/citybuildersim/GdpCheck.java` - generated 2026-10-12 by CodeMap; line numbers are as of that run.

> Verifies the national accounts: the identity, growth rates, and the government's books - and, since 0.7.58, that every good a sector holds is stock in them (a fleet bought abroad among them).

**Uses:** [NationalAccounts](NationalAccounts.md) (63), [Good](Good.md) (39), [Game](Game.md) (12), [GameFiles](GameFiles.md) (8), [Founding](Founding.md) (4), [Sector](Sector.md) (4), [Rail](Rail.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (3), [EconomyManager](EconomyManager.md) (3), [Sectors](Sectors.md) (2), [BuildingManager](BuildingManager.md) (2), [CopperRefining](CopperRefining.md) (2), [Debt](Debt.md) (1), [Retail](Retail.md) (1), [HeavyIndustry](HeavyIndustry.md) (1), [RailCheck](RailCheck.md) (1), [GoodsMarket](GoodsMarket.md) (1)

## Sections

| line | section |
|---:|---|
| 32 | · 1. the identity |
| 49 | · 2. stock is a CHANGE |
| 61 | · 3. THE BUG THIS REPLACES |
| 76 | · 3c. the two ways it went negative anyway |
| 325 | · 3b. exports, the first the city has ever had |
| 353 | · 4. annual and per capita |
| 371 | · 5. growth |
| 396 | · 6. government books |
| 428 | · 4. THE INTEREST LINE IS REAL |

## Fields (state)

| line | field | says |
|---:|---|---|
| 8 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 6 | 741 | **type** `public class GdpCheck` | Verifies the national accounts: the identity, growth rates, and the government's books - and, since 0.7.58, that every good a sector holds is stock in them (a fleet bought abroad among them). |
| 10 | 6 | `static void check(String label, double actual, double expected)` |  |
| 17 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 23 | 4 | `static int heldAt(Good g)` | Where a good stands in NationalAccounts.HELD. |
| 28 | 718 | `public static void main(String[] args)` |  |

