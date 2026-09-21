# EquityCheck.java - 348 lines · 5 methods · 0 constants · harnesses

`ham/citybuildersim/EquityCheck.java` - generated 2026-09-21 by CodeMap; line numbers are as of that run.

> Verifies the share register: who buys, at what price, what they are paid,
> and that a month with owners in it still adds up.
> 
> Every claim is CAUSED. A register is handed a record and asked what it
> would do; households are given savings and offered shares; a company is
> given a month's income and its owners are paid. The one live city at the
> end is there to show the mechanism runs inside the audited month, not to
> hope that somebody happens to buy something.

**Uses:** [Equity](Equity.md) (56), [HouseholdBalance](HouseholdBalance.md) (18), [DebtManager](DebtManager.md) (13), [PayTier](PayTier.md) (9), [FamilyStructure](FamilyStructure.md) (7), [Bank](Bank.md) (3), [Household](Household.md) (2), [Sectors](Sectors.md) (2), [Game](Game.md) (2), [GameFiles](GameFiles.md) (1)

## Sections

| line | section |
|---:|---|
| 71 | · 1. the households first, then the world |
| 114 | · 2. the world's test |
| 137 | · 3. the regimes |
| 158 | · 4. how much it raises |
| 188 | · 5. the price |
| 206 | · 6. the founders |
| 218 | · 7. the dividend |
| 261 | · 8. the save |
| 277 | · 9. a live city |

## Fields (state)

| line | field | says |
|---:|---|---|
| 20 | `static int fails` |  |
| 21 | `static PrintStream out` |  |
| 22 | `static PrintStream quiet` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 18 | 331 | **type** `public class EquityCheck` | Verifies the share register: who buys, at what price, what they are paid, and that a month with owners in it still adds up. |
| 24 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 29 | 9 | `static void close(String label, double actual, double expected, double tol)` |  |
| 40 | 5 | `static Equity withRecord(int company, double...monthlyIncome)` | A register with twelve months of this income on one company's record. |
| 47 | 15 | `static HouseholdBalance savers(double savedEach, double takeHome)` | Households: a hundred unskilled couples with this much saved each, struck once so they have a take-home. |
| 63 | 285 | `public static void main(String[] args) throws Exception` |  |

