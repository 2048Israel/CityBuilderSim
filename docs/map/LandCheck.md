# LandCheck.java - 1,282 lines · 13 methods · 0 constants · harnesses

`ham/citybuildersim/LandCheck.java` - generated 2026-10-02 by CodeMap; line numbers are as of that run.

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
> plots bought at once ending exactly as N bought one by one (16); and,
> since 0.7.26, the three figures the redrawn office takes from the model:
> the going rate, which it used to work out itself, the GROUND row's
> verdict and the receipt in the screens' money (17).

**Uses:** [LandManager](LandManager.md) (76), [Game](Game.md) (46), [LandParcel](LandParcel.md) (31), [LandMarket](LandMarket.md) (16), [CityNeeds](CityNeeds.md) (10), [MoneyAudit](MoneyAudit.md) (4), [ForeignAccounts](ForeignAccounts.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (3), [GameFiles](GameFiles.md) (3), [DebtQuote](DebtQuote.md) (3), [Formats](Formats.md) (3), [BuildingManager](BuildingManager.md) (2), [TreasuryJournal](TreasuryJournal.md) (2)

## Sections

| line | section |
|---:|---|
| 46 | · 1. what the city starts with |
| 58 | · 2. allocating |
| 79 | · 3. filling up |
| 91 | · 4. releasing |
| 100 | · 5. the listing |
| 141 | · 5b. buying one |
| 171 | · 5c. a bigger city pays more |
| 191 | · 5d. supply and demand inside the city |
| 267 | · 5e. iron in the ground |
| 311 | · 5f. parcels are blocks, and they grow |
| 481 | · 6. not affording it |
| 498 | · 7. selling |
| 522 | · 8. the player's price |
| 542 | · 9. reset |
| 557 | · 10. every building fits on a starting city |
| 609 | · 11. the price is a density policy |
| 651 | 12. LAND IS PRICED IN DOLLARS (0.7.6) |
| 711 | 13. THE TWO WAYS TO PAY (0.7.6) |
| 902 | 14. THE OFFICE IN SQUARE KILOMETRES (0.7.13) |
| 932 | 15. SHORT OF THE PRICE: THE FUNDING PAGE (0.7.13) |
| 1061 | 16. THE NEXT N PLOTS, AT ONCE (0.7.13) |
| 1181 | 17. THE OFFICE'S OWN FIGURES, IN THE MODEL (0.7.26) |

## Fields (state)

| line | field | says |
|---:|---|---|
| 24 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 22 | 1261 | **type** `public class LandCheck` | Verifies the land ledger: what the city owns, what it can allocate, what it charges, and that the three numbers never drift apart. |
| 26 | 6 | `static void check(String label, double actual, double expected)` |  |
| 33 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 38 | 5 | `static void quietly(Runnable work)` |  |
| 44 | 606 | `public static void main(String[] args) throws Exception` |  |

### 12. LAND IS PRICED IN DOLLARS (0.7.6) (lines 651-710)

| line | len | member | says |
|---:|---:|---|---|
| 661 | 49 | `static void inDollars()` |  |

### 13. THE TWO WAYS TO PAY (0.7.6) (lines 711-901)

| line | len | member | says |
|---:|---:|---|---|
| 722 | 6 | `static Game dollarCity(String label)` |  |
| 729 | 5 | `static double[] moved(double[] before, double[] after)` |  |
| 735 | 166 | `static void bothWays() throws Exception` |  |

### 14. THE OFFICE IN SQUARE KILOMETRES (0.7.13) (lines 902-931)

| line | len | member | says |
|---:|---:|---|---|
| 910 | 21 | `static void inSquareKilometres()` |  |

### 15. SHORT OF THE PRICE: THE FUNDING PAGE (0.7.13) (lines 932-1060)

| line | len | member | says |
|---:|---:|---|---|
| 947 | 113 | `static void whenShort() throws Exception` |  |

### 16. THE NEXT N PLOTS, AT ONCE (0.7.13) (lines 1061-1180)

| line | len | member | says |
|---:|---:|---|---|
| 1076 | 104 | `static void severalAtOnce() throws Exception` |  |

### 17. THE OFFICE'S OWN FIGURES, IN THE MODEL (0.7.26) (lines 1181-1282)

| line | len | member | says |
|---:|---:|---|---|
| 1196 | 73 | `static void theOfficesFigures()` |  |
| 1271 | 11 | `static double[] handMade(double marker, double[] perSqFt, double[] sizes, int n)` | A dollar listing of `n` plots by hand: each its size and its dollars a square foot, ids from 1, no ore. |

