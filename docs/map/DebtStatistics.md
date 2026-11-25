# DebtStatistics.java - 196 lines · 15 methods · 2 constants · model

`ham/citybuildersim/DebtStatistics.java` - generated 2026-11-25 by CodeMap; line numbers are as of that run.

> Household debt as the statistics offices print it, read from the cells (HC2).
> WHY: the average across everyone hides both debt-free families and struggling holders.
> Amounts per adult split a household's debts evenly between its grown-ups.
> Total holders assume independent kinds; their total is the SCF-shaped approximation.

**Uses:** [Household](Household.md) (6), [CellSpread](CellSpread.md) (6), [HouseholdBalance](HouseholdBalance.md) (4), [PayTier](PayTier.md) (4), [FamilyStructure](FamilyStructure.md) (2), [Bank](Bank.md) (2), [WorkingHousehold](WorkingHousehold.md) (1), [AdultStage](AdultStage.md) (1)

**Used by (7):** [CardCheck](CardCheck.md), [DebtStatisticsCheck](DebtStatisticsCheck.md), [HistorySave](HistorySave.md), [HouseholdBalance](HouseholdBalance.md), [HouseholdDebtPanel](HouseholdDebtPanel.md), [MoneyWordsCheck](MoneyWordsCheck.md), [ReadPathCheck](ReadPathCheck.md)

## Enum constants

| line | constant | says |
|---:|---|---|
| 17 | `DebtStatistics.Kind.TOTAL` |  |
| 17 | `DebtStatistics.Kind.CARD` |  |
| 17 | `DebtStatistics.Kind.STUDENT` |  |
| 17 | `DebtStatistics.Kind.CAR` |  |
| 17 | `DebtStatistics.Kind.MORTGAGE` |  |
| 18 | `DebtStatistics.Stage.YOUNG` |  |
| 18 | `DebtStatistics.Stage.ESTABLISHED` |  |
| 18 | `DebtStatistics.Stage.SENIOR` |  |
| 18 | `DebtStatistics.Stage.ELDER` |  |
| 18 | `DebtStatistics.Stage.OTHER` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 20 | `DebtStatistics.GRID_POINTS` | `32` | Coarse log-CDF grid, shared by the quantile search. |
| 22 | `DebtStatistics.HIGH_SERVICE` | `.40` | Payment burden boundary used by the SCF. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 15 | 182 | **type** `public final class DebtStatistics` | Household debt as the statistics offices print it, read from the cells (HC2). |
| 16 | 1 | `private DebtStatistics()` |  |
| 17 | 1 | **type** `public enum Kind` |  |
| 18 | 1 | **type** `public enum Stage` |  |
| 24 | 2 | **type** `public record Measure(double shareHolding, double meanAmongHolders, double medianAmongHolders, double lateS...` |  |
| 26 | 14 | **type** `public record Snapshot(double households, double adults, Map<Kind, Measure> perHousehold, Map<Kind, Measure...` |  |
| 31 | 3 | `{ ... }` _(in DebtStatistics.Snapshot)_ |  |
| 34 | 5 | `Snapshot scaled(double scale)` _(in DebtStatistics.Snapshot)_ |  |
| 41 | 8 | **type** `public record Survey(int month, Map<String, Snapshot> groups)` | Yearly surveys have their own year axis; they must not be folded as monthly series. |
| 42 | 1 | `{ ... }` _(in DebtStatistics.Survey)_ |  |
| 43 | 5 | `public Survey scaled(double scale)` _(in DebtStatistics.Survey)_ |  |
| 50 | 6 | `private static Map<Kind, Measure> scaledMeasures(Map<Kind, Measure> measures, double scale)` |  |
| 56 | 1 | **type** `record Component(double weight, double share, double mean, double sigma)` |  |
| 58 | 6 | `public static Stage stage(Household c)` |  |
| 66 | 8 | `static double amount(Household c, Kind kind)` | Cars remain financed on the card; owner mortgages arrive with HO1, so those books are zero. |
| 75 | 8 | `static double holding(Household c, Kind kind)` |  |
| 84 | 4 | `static double sigma(Kind kind)` |  |
| 89 | 8 | `static double cdf(List<Component> components, double x)` |  |
| 99 | 20 | `static double median(List<Component> components)` | Locate on the log grid, then refine only that interval; no household is sampled. |
| 120 | 3 | `public static Snapshot of(HouseholdBalance balance, PayTier tier, Stage stage)` |  |
| 124 | 52 | `private static Snapshot of(HouseholdBalance balance, PayTier tier, Stage stage, boolean untiered)` |  |
| 178 | 3 | `public static Snapshot untiered(HouseholdBalance balance, Stage stage)` | Students and other households without a pay tier, on the same stage axis. |
| 182 | 12 | `public static Survey survey(HouseholdBalance balance, int month)` |  |
| 195 | 1 | `private static double ratio(double a, double b)` |  |

