# EquityCheck.java - 379 lines · 5 methods · 0 constants · harnesses

`ham/citybuildersim/EquityCheck.java` - generated 2026-10-21 by CodeMap; line numbers are as of that run.

> Verifies the share register: who buys, at what price, what they are paid,
> and that a month with owners in it still adds up.
> 
> Every claim is CAUSED. A register is handed a record and asked what it
> would do; households are given savings and offered shares; a company is
> given a month's income and its owners are paid. The one live city at the
> end is there to show the mechanism runs inside the audited month, not to
> hope that somebody happens to buy something.

**Uses:** [Equity](Equity.md) (59), [HouseholdBalance](HouseholdBalance.md) (18), [DebtManager](DebtManager.md) (15), [PayTier](PayTier.md) (9), [FamilyStructure](FamilyStructure.md) (7), [Bank](Bank.md) (3), [Household](Household.md) (2), [Sectors](Sectors.md) (2), [Game](Game.md) (2), [TempDirs](TempDirs.md) (1), [GameFiles](GameFiles.md) (1)

## Sections

| line | section |
|---:|---|
| 70 | · 1. the households first, then the world |
| 113 | · 2. the world's test |
| 136 | · 3. the regimes |
| 157 | · 4. how much it raises |
| 187 | · 5. the price |
| 223 | · 6. the founders |
| 235 | · 7. the dividend |
| 278 | · 8. the save |
| 294 | · 9. a live city |

## Fields (state)

| line | field | says |
|---:|---|---|
| 19 | `static int fails` |  |
| 20 | `static PrintStream out` |  |
| 21 | `static PrintStream quiet` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 17 | 363 | **type** `public class EquityCheck` | Verifies the share register: who buys, at what price, what they are paid, and that a month with owners in it still adds up. |
| 23 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 28 | 9 | `static void close(String label, double actual, double expected, double tol)` |  |
| 39 | 5 | `static Equity withRecord(int company, double...monthlyIncome)` | A register with twelve months of this income on one company's record. |
| 46 | 15 | `static HouseholdBalance savers(double savedEach, double takeHome)` | Households: a hundred unskilled couples with this much saved each, struck once so they have a take-home. |
| 62 | 317 | `public static void main(String[] args) throws Exception` |  |

