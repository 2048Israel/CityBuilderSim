# GdpCheck.java - 608 lines · 4 methods · 0 constants · harnesses

`ham/citybuildersim/GdpCheck.java` - generated 2026-10-07 by CodeMap; line numbers are as of that run.

> Verifies the national accounts: the identity, growth rates, and the government's books - and, since 0.7.58, that every good a sector holds is stock in them (a fleet bought abroad among them).

**Uses:** [NationalAccounts](NationalAccounts.md) (47), [Good](Good.md) (9), [Game](Game.md) (8), [GameFiles](GameFiles.md) (6), [Rail](Rail.md) (3), [Founding](Founding.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (2), [Sector](Sector.md) (2), [EconomyManager](EconomyManager.md) (2), [Debt](Debt.md) (1), [Retail](Retail.md) (1), [Sectors](Sectors.md) (1), [BuildingManager](BuildingManager.md) (1), [RailCheck](RailCheck.md) (1), [GoodsMarket](GoodsMarket.md) (1)

## Sections

| line | section |
|---:|---|
| 30 | · 1. the identity |
| 47 | · 2. stock is a CHANGE |
| 59 | · 3. THE BUG THIS REPLACES |
| 74 | · 3c. the two ways it went negative anyway |
| 287 | · 3b. exports, the first the city has ever had |
| 315 | · 4. annual and per capita |
| 333 | · 5. growth |
| 358 | · 6. government books |
| 390 | · 4. THE INTEREST LINE IS REAL |

## Fields (state)

| line | field | says |
|---:|---|---|
| 6 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 4 | 605 | **type** `public class GdpCheck` | Verifies the national accounts: the identity, growth rates, and the government's books - and, since 0.7.58, that every good a sector holds is stock in them (a fleet bought abroad among them). |
| 8 | 6 | `static void check(String label, double actual, double expected)` |  |
| 15 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 21 | 4 | `static int heldAt(Good g)` | Where a good stands in NationalAccounts.HELD. |
| 26 | 582 | `public static void main(String[] args)` |  |

