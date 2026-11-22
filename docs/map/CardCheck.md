# CardCheck.java - 280 lines · 11 methods · 0 constants · harnesses

`ham/citybuildersim/CardCheck.java` - generated 2026-11-22 by CodeMap; line numbers are as of that run.

> Cards inside a household cell: shapes, shocks, minimums, arrears, and the bank (HC1).
> WHY: a cell average used to discharge all its neighbours and conceal thin buffers.
> Every edge is caused with the household's own inputs; money is checked as an identity.

**Uses:** [Bank](Bank.md) (21), [WorkingHousehold](WorkingHousehold.md) (14), [HouseholdBalance](HouseholdBalance.md) (13), [CellSpread](CellSpread.md) (12), [PayTier](PayTier.md) (11), [FamilyStructure](FamilyStructure.md) (8), [Household](Household.md) (8), [Game](Game.md) (5), [DebtStatistics](DebtStatistics.md) (5), [MoneyAudit](MoneyAudit.md) (4), [BusinessServices](BusinessServices.md) (2), [GameVersion](GameVersion.md) (2), [Sector](Sector.md) (2), [BusinessDebtManager](BusinessDebtManager.md) (1), [DebtManager](DebtManager.md) (1), [Good](Good.md) (1), [Equity](Equity.md) (1), [LongPlaytest](LongPlaytest.md) (1), [GameFiles](GameFiles.md) (1)

## Fields (state)

| line | field | says |
|---:|---|---|
| 13 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 12 | 269 | **type** `public final class CardCheck` | Cards inside a household cell: shapes, shocks, minimums, arrears, and the bank (HC1). |
| 14 | 3 | `static void check(String label, boolean pass)` |  |
| 17 | 3 | `static void close(String label, double got, double want, double tolerance)` |  |
| 20 | 5 | `static WorkingHousehold cell(double income, double savings, double debt, double holders)` |  |
| 25 | 26 | `static void spread()` |  |
| 51 | 47 | `static void month()` |  |
| 98 | 41 | `static void chargeOff()` |  |
| 139 | 21 | `static void save()` |  |
| 160 | 15 | `static void neighbours()` |  |
| 175 | 17 | `static void census()` |  |
| 192 | 83 | `static void city()` |  |
| 275 | 5 | `public static void main(String[] args)` |  |

