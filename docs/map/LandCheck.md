# LandCheck.java - 887 lines · 8 methods · 0 constants · harnesses

`ham/citybuildersim/LandCheck.java` - generated 2026-09-23 by CodeMap; line numbers are as of that run.

> Verifies the land ledger: what the city owns, what it can allocate, what it
> charges, and that the three numbers never drift apart.
> 
> The one thing worth being paranoid about here is that allocated land can only
> ever go up by exactly what was built. A leak in either direction is invisible
> for a hundred months and then the city is either mysteriously full or
> mysteriously infinite.

**Uses:** [LandManager](LandManager.md) (62), [LandParcel](LandParcel.md) (21), [LandMarket](LandMarket.md) (14), [Game](Game.md) (14), [MoneyAudit](MoneyAudit.md) (4), [ForeignAccounts](ForeignAccounts.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (3), [GameFiles](GameFiles.md) (3), [BuildingManager](BuildingManager.md) (2), [TreasuryJournal](TreasuryJournal.md) (2)

## Sections

| line | section |
|---:|---|
| 36 | · 1. what the city starts with |
| 48 | · 2. allocating |
| 69 | · 3. filling up |
| 81 | · 4. releasing |
| 90 | · 5. the listing |
| 131 | · 5b. buying one |
| 161 | · 5c. a bigger city pays more |
| 181 | · 5d. supply and demand inside the city |
| 257 | · 5e. iron in the ground |
| 301 | · 5f. parcels are blocks, and they grow |
| 471 | · 6. not affording it |
| 488 | · 7. selling |
| 512 | · 8. the player's price |
| 532 | · 9. reset |
| 547 | · 10. every building fits on a starting city |
| 599 | · 11. the price is a density policy |
| 637 | 12. LAND IS PRICED IN DOLLARS (0.7.6) |
| 697 | 13. THE TWO WAYS TO PAY (0.7.6) |

## Fields (state)

| line | field | says |
|---:|---|---|
| 14 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 12 | 876 | **type** `public class LandCheck` | Verifies the land ledger: what the city owns, what it can allocate, what it charges, and that the three numbers never drift apart. |
| 16 | 6 | `static void check(String label, double actual, double expected)` |  |
| 23 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 28 | 5 | `static void quietly(Runnable work)` |  |
| 34 | 602 | `public static void main(String[] args) throws Exception` |  |

### 12. LAND IS PRICED IN DOLLARS (0.7.6) (lines 637-696)

| line | len | member | says |
|---:|---:|---|---|
| 647 | 49 | `static void inDollars()` |  |

### 13. THE TWO WAYS TO PAY (0.7.6) (lines 697-887)

| line | len | member | says |
|---:|---:|---|---|
| 708 | 6 | `static Game dollarCity(String label)` |  |
| 715 | 5 | `static double[] moved(double[] before, double[] after)` |  |
| 721 | 166 | `static void bothWays() throws Exception` |  |

