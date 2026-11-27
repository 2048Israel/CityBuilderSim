# EquityCheck.java - 595 lines · 10 methods · 0 constants · harnesses

`ham/citybuildersim/EquityCheck.java` - generated 2026-11-27 by CodeMap; line numbers are as of that run.

> Verifies the share register: who buys, at what price, what they are paid,
> and that a month with owners in it still adds up.
> 
> Every claim is CAUSED. A register is handed a record and asked what it
> would do; households are given savings and offered shares; a company is
> given a month's income and its owners are paid. The one live city at the
> end is there to show the mechanism runs inside the audited month, not to
> hope that somebody happens to buy something.
> 
> The live city (FS14): the default founding in a TempDirs folder, played
> until Retail has a book (bounded, 36 months) and 240 months more. It
> asserts only what holds on any path - every share held by somebody, the
> audit every month, the register agreeing with the cells - and counts the
> offerings and the months a dividend was paid, which are its path's.

**Uses:** [Equity](Equity.md) (91), [DebtManager](DebtManager.md) (19), [HouseholdBalance](HouseholdBalance.md) (15), [Exchange](Exchange.md) (12), [PayTier](PayTier.md) (8), [FamilyStructure](FamilyStructure.md) (7), [Bank](Bank.md) (5), [Sectors](Sectors.md) (4), [Household](Household.md) (2), [Game](Game.md) (2), [TempDirs](TempDirs.md) (1), [GameFiles](GameFiles.md) (1)

## Sections

| line | section |
|---:|---|
| 68 | 10. mature value does not revive an offering quote (CBK3) |
| 189 | 11. the anchored floor cannot cause a consolidation loop (CBK3) |
| 270 | · 1. the households first, then the world |
| 314 | · 2. the world's test |
| 337 | · 3. the regimes |
| 358 | · 4. how much it raises |
| 388 | · 5. the price |
| 424 | · 6. the founders |
| 436 | · 7. the dividend |
| 479 | · 8. the save |
| 495 | · 9. a live city |

## Fields (state)

| line | field | says |
|---:|---|---|
| 25 | `static int fails` |  |
| 26 | `static PrintStream out` |  |
| 27 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 23 | 573 | **type** `public class EquityCheck` | Verifies the share register: who buys, at what price, what they are paid, and that a month with owners in it still adds up. |
| 29 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 34 | 9 | `static void close(String label, double actual, double expected, double tol)` |  |
| 45 | 5 | `static Equity withRecord(int company, double...monthlyIncome)` | A register with twelve months of this income on one company's record. |
| 52 | 7 | `static HouseholdBalance savers(double savedEach, double takeHome)` | Debt-free savers at the offering boundary, before a new card/shock month. |
| 61 | 6 | `static Equity staleOffering(int company, double quote)` | Two native offerings leave a large quote before the company has an earnings record. |

### 10. mature value does not revive an offering quote (CBK3) (lines 68-188)

| line | len | member | says |
|---:|---:|---|---|
| 69 | 97 | `static void maturePricesFollowTheirRecord()` |  |
| 168 | 12 | `static Exchange.Companies wageOnlyCompanies(double wage)` | No buyback money, but a paid wage makes the exchange's automatic split rule active. |
| 181 | 7 | `static boolean finiteListing(Equity register, Exchange market, int company)` |  |

### 11. the anchored floor cannot cause a consolidation loop (CBK3) (lines 189-595)

| line | len | member | says |
|---:|---:|---|---|
| 190 | 71 | `static void anchoredFloorIsNotAConsolidation()` |  |
| 262 | 333 | `public static void main(String[] args) throws Exception` |  |

