# LandCheck.java - 1,176 lines · 11 methods · 0 constants · harnesses

`ham/citybuildersim/LandCheck.java` - generated 2026-09-26 by CodeMap; line numbers are as of that run.

> Verifies the land ledger: what the city owns, what it can allocate, what it
> charges, and that the three numbers never drift apart.
> 
> The one thing worth being paranoid about here is that allocated land can only
> ever go up by exactly what was built. A leak in either direction is invisible
> for a hundred months and then the city is either mysteriously full or
> mysteriously infinite.
> 
> Sections 1-11 are the ledger and the market. The land office has its own
> at the end: land priced in dollars and the two ways to pay for it (0.7.6,
> sections 12-13); and, since 0.7.13, the office in square kilometres (14),
> the funding page a city short of the price is offered, converting and
> from the vault, with the window abroad open and shut (15), and the next N
> plots bought at once ending exactly as N bought one by one (16).

**Uses:** [LandManager](LandManager.md) (74), [Game](Game.md) (44), [LandParcel](LandParcel.md) (29), [LandMarket](LandMarket.md) (14), [MoneyAudit](MoneyAudit.md) (4), [ForeignAccounts](ForeignAccounts.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (3), [GameFiles](GameFiles.md) (3), [DebtQuote](DebtQuote.md) (3), [BuildingManager](BuildingManager.md) (2), [TreasuryJournal](TreasuryJournal.md) (2)

## Sections

| line | section |
|---:|---|
| 43 | · 1. what the city starts with |
| 55 | · 2. allocating |
| 76 | · 3. filling up |
| 88 | · 4. releasing |
| 97 | · 5. the listing |
| 138 | · 5b. buying one |
| 168 | · 5c. a bigger city pays more |
| 188 | · 5d. supply and demand inside the city |
| 264 | · 5e. iron in the ground |
| 308 | · 5f. parcels are blocks, and they grow |
| 478 | · 6. not affording it |
| 495 | · 7. selling |
| 519 | · 8. the player's price |
| 539 | · 9. reset |
| 554 | · 10. every building fits on a starting city |
| 606 | · 11. the price is a density policy |
| 647 | 12. LAND IS PRICED IN DOLLARS (0.7.6) |
| 707 | 13. THE TWO WAYS TO PAY (0.7.6) |
| 898 | 14. THE OFFICE IN SQUARE KILOMETRES (0.7.13) |
| 928 | 15. SHORT OF THE PRICE: THE FUNDING PAGE (0.7.13) |
| 1057 | 16. THE NEXT N PLOTS, AT ONCE (0.7.13) |

## Fields (state)

| line | field | says |
|---:|---|---|
| 21 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 19 | 1158 | **type** `public class LandCheck` | Verifies the land ledger: what the city owns, what it can allocate, what it charges, and that the three numbers never drift apart. |
| 23 | 6 | `static void check(String label, double actual, double expected)` |  |
| 30 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 35 | 5 | `static void quietly(Runnable work)` |  |
| 41 | 605 | `public static void main(String[] args) throws Exception` |  |

### 12. LAND IS PRICED IN DOLLARS (0.7.6) (lines 647-706)

| line | len | member | says |
|---:|---:|---|---|
| 657 | 49 | `static void inDollars()` |  |

### 13. THE TWO WAYS TO PAY (0.7.6) (lines 707-897)

| line | len | member | says |
|---:|---:|---|---|
| 718 | 6 | `static Game dollarCity(String label)` |  |
| 725 | 5 | `static double[] moved(double[] before, double[] after)` |  |
| 731 | 166 | `static void bothWays() throws Exception` |  |

### 14. THE OFFICE IN SQUARE KILOMETRES (0.7.13) (lines 898-927)

| line | len | member | says |
|---:|---:|---|---|
| 906 | 21 | `static void inSquareKilometres()` |  |

### 15. SHORT OF THE PRICE: THE FUNDING PAGE (0.7.13) (lines 928-1056)

| line | len | member | says |
|---:|---:|---|---|
| 943 | 113 | `static void whenShort() throws Exception` |  |

### 16. THE NEXT N PLOTS, AT ONCE (0.7.13) (lines 1057-1176)

| line | len | member | says |
|---:|---:|---|---|
| 1072 | 104 | `static void severalAtOnce() throws Exception` |  |

