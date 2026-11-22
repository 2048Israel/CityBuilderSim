# CardCheck.java - 385 lines · 13 methods · 0 constants · harnesses

`ham/citybuildersim/CardCheck.java` - generated 2026-11-22 by CodeMap; line numbers are as of that run.

> Cards inside a household cell: shapes, shocks, minimums, arrears, and the bank (HC1).
> WHY: a cell average used to discharge all its neighbours and conceal thin buffers.
> Every edge is caused with the household's own inputs; money is checked as an identity.

**Uses:** [Bank](Bank.md) (21), [PayTier](PayTier.md) (19), [FamilyStructure](FamilyStructure.md) (17), [WorkingHousehold](WorkingHousehold.md) (15), [HouseholdBalance](HouseholdBalance.md) (15), [Household](Household.md) (15), [CellSpread](CellSpread.md) (12), [Game](Game.md) (7), [HouseholdFlowCheck](HouseholdFlowCheck.md) (6), [DebtStatistics](DebtStatistics.md) (5), [MoneyAudit](MoneyAudit.md) (4), [Good](Good.md) (3), [SaveFileCheck](SaveFileCheck.md) (3), [BusinessServices](BusinessServices.md) (2), [GameVersion](GameVersion.md) (2), [Sector](Sector.md) (2), [BusinessDebtManager](BusinessDebtManager.md) (1), [DebtManager](DebtManager.md) (1), [Equity](Equity.md) (1), [LongPlaytest](LongPlaytest.md) (1), [GameFiles](GameFiles.md) (1), [PopulationCohorts](PopulationCohorts.md) (1), [HouseholdMemoryCheck](HouseholdMemoryCheck.md) (1), [AdultStage](AdultStage.md) (1), [AgeBand](AgeBand.md) (1)

**Used by (1):** [SaveFileCheck](SaveFileCheck.md)

## Fields (state)

| line | field | says |
|---:|---|---|
| 13 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 12 | 374 | **type** `public final class CardCheck` | Cards inside a household cell: shapes, shocks, minimums, arrears, and the bank (HC1). |
| 14 | 3 | `static void check(String label, boolean pass)` |  |
| 17 | 3 | `static void close(String label, double got, double want, double tolerance)` |  |
| 20 | 5 | `static WorkingHousehold cell(double income, double savings, double debt, double holders)` |  |
| 25 | 26 | `static void spread()` |  |
| 51 | 47 | `static void month()` |  |
| 98 | 41 | `static void chargeOff()` |  |
| 139 | 26 | `static void save()` |  |
| 165 | 15 | `static void neighbours()` |  |
| 180 | 17 | `static void census()` |  |
| 197 | 84 | `static void city()` |  |
| 281 | 8 | `public static void main(String[] args)` |  |
| 290 | 61 | `static void reviewFlows()` |  |
| 353 | 32 | `static boolean continues(Game live, Game loaded)` | Advance both real cities; a matching reload must also make the same next month. |

