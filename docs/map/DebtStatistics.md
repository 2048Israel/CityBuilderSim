# DebtStatistics.java - 205 lines · 16 methods · 2 constants · model

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
| 19 | `DebtStatistics.Kind.TOTAL` |  |
| 19 | `DebtStatistics.Kind.CARD` |  |
| 19 | `DebtStatistics.Kind.STUDENT` |  |
| 19 | `DebtStatistics.Kind.CAR` |  |
| 19 | `DebtStatistics.Kind.MORTGAGE` |  |
| 20 | `DebtStatistics.Stage.YOUNG` |  |
| 20 | `DebtStatistics.Stage.ESTABLISHED` |  |
| 20 | `DebtStatistics.Stage.SENIOR` |  |
| 20 | `DebtStatistics.Stage.ELDER` |  |
| 20 | `DebtStatistics.Stage.OTHER` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 22 | `DebtStatistics.GRID_POINTS` | `32` | Coarse log-CDF grid, shared by the quantile search. |
| 24 | `DebtStatistics.HIGH_SERVICE` | `.40` | Payment burden boundary used by the SCF. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 17 | 189 | **type** `public final class DebtStatistics` | Household debt as the statistics offices print it, read from the cells (HC2). |
| 18 | 1 | `private DebtStatistics()` |  |
| 19 | 1 | **type** `public enum Kind` |  |
| 20 | 1 | **type** `public enum Stage` |  |
| 26 | 2 | **type** `public record Measure(double shareHolding, double meanAmongHolders, double medianAmongHolders, double lateS...` |  |
| 28 | 14 | **type** `public record Snapshot(double households, double adults, Map<Kind, Measure> perHousehold, Map<Kind, Measure...` |  |
| 33 | 3 | `{ ... }` _(in DebtStatistics.Snapshot)_ |  |
| 36 | 5 | `Snapshot scaled(double scale)` _(in DebtStatistics.Snapshot)_ |  |
| 43 | 8 | **type** `public record Survey(int month, Map<String, Snapshot> groups)` | Yearly surveys have their own year axis; they must not be folded as monthly series. |
| 44 | 1 | `{ ... }` _(in DebtStatistics.Survey)_ |  |
| 45 | 5 | `public Survey scaled(double scale)` _(in DebtStatistics.Survey)_ |  |
| 53 | 5 | `private static<K, V> Map<K, V> orderedCopy(Map<K, V> values)` | Immutable survey maps retain their recorded order across the native JSON round trip. |
| 59 | 6 | `private static Map<Kind, Measure> scaledMeasures(Map<Kind, Measure> measures, double scale)` |  |
| 65 | 1 | **type** `record Component(double weight, double share, double mean, double sigma)` |  |
| 67 | 6 | `public static Stage stage(Household c)` |  |
| 75 | 8 | `static double amount(Household c, Kind kind)` | Cars remain financed on the card; owner mortgages arrive with HO1, so those books are zero. |
| 84 | 8 | `static double holding(Household c, Kind kind)` |  |
| 93 | 4 | `static double sigma(Kind kind)` |  |
| 98 | 8 | `static double cdf(List<Component> components, double x)` |  |
| 108 | 20 | `static double median(List<Component> components)` | Locate on the log grid, then refine only that interval; no household is sampled. |
| 129 | 3 | `public static Snapshot of(HouseholdBalance balance, PayTier tier, Stage stage)` |  |
| 133 | 52 | `private static Snapshot of(HouseholdBalance balance, PayTier tier, Stage stage, boolean untiered)` |  |
| 187 | 3 | `public static Snapshot untiered(HouseholdBalance balance, Stage stage)` | Students and other households without a pay tier, on the same stage axis. |
| 191 | 12 | `public static Survey survey(HouseholdBalance balance, int month)` |  |
| 204 | 1 | `private static double ratio(double a, double b)` |  |

