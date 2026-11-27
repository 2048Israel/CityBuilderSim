# DebtStatistics.java - 252 lines · 21 methods · 2 constants · model

`ham/citybuildersim/DebtStatistics.java` - generated 2026-11-27 by CodeMap; line numbers are as of that run.

> Household debt as the statistics offices print it, read from the cells (HC2).
> WHY: the average across everyone hides both debt-free families and struggling holders.
> Amounts per adult split a household's debts evenly between its grown-ups.
> Total holders assume independent kinds; their total is the SCF-shaped approximation.

**Uses:** [Household](Household.md) (6), [CellSpread](CellSpread.md) (6), [HouseholdBalance](HouseholdBalance.md) (4), [PayTier](PayTier.md) (4), [FamilyStructure](FamilyStructure.md) (2), [Bank](Bank.md) (2), [WorkingHousehold](WorkingHousehold.md) (1), [AdultStage](AdultStage.md) (1)

**Used by (7):** [CardCheck](CardCheck.md), [DebtStatisticsCheck](DebtStatisticsCheck.md), [HistorySave](HistorySave.md), [HouseholdBalance](HouseholdBalance.md), [HouseholdDebtPanel](HouseholdDebtPanel.md), [MoneyWordsCheck](MoneyWordsCheck.md), [ReadPathCheck](ReadPathCheck.md)

## Enum constants

| line | constant | says |
|---:|---|---|
| 23 | `DebtStatistics.Kind.TOTAL` |  |
| 23 | `DebtStatistics.Kind.CARD` |  |
| 23 | `DebtStatistics.Kind.STUDENT` |  |
| 23 | `DebtStatistics.Kind.CAR` |  |
| 23 | `DebtStatistics.Kind.MORTGAGE` |  |
| 24 | `DebtStatistics.Stage.YOUNG` |  |
| 24 | `DebtStatistics.Stage.ESTABLISHED` |  |
| 24 | `DebtStatistics.Stage.SENIOR` |  |
| 24 | `DebtStatistics.Stage.ELDER` |  |
| 24 | `DebtStatistics.Stage.OTHER` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 26 | `DebtStatistics.GRID_POINTS` | `32` | Coarse log-CDF grid, shared by the quantile search. |
| 28 | `DebtStatistics.HIGH_SERVICE` | `.40` | Payment burden boundary used by the SCF. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 72 | `private final List<K> keys` |  |
| 73 | `private final List<V> values` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 21 | 232 | **type** `public final class DebtStatistics` | Household debt as the statistics offices print it, read from the cells (HC2). |
| 22 | 1 | `private DebtStatistics()` |  |
| 23 | 1 | **type** `public enum Kind` |  |
| 24 | 1 | **type** `public enum Stage` |  |
| 30 | 2 | **type** `public record Measure(double shareHolding, double meanAmongHolders, double medianAmongHolders, double lateS...` |  |
| 32 | 14 | **type** `public record Snapshot(double households, double adults, Map<Kind, Measure> perHousehold, Map<Kind, Measure...` |  |
| 37 | 3 | `{ ... }` _(in DebtStatistics.Snapshot)_ |  |
| 40 | 5 | `Snapshot scaled(double scale)` _(in DebtStatistics.Snapshot)_ |  |
| 47 | 8 | **type** `public record Survey(int month, Map<String, Snapshot> groups)` | Yearly surveys have their own year axis; they must not be folded as monthly series. |
| 48 | 1 | `{ ... }` _(in DebtStatistics.Survey)_ |  |
| 49 | 5 | `public Survey scaled(double scale)` _(in DebtStatistics.Survey)_ |  |
| 57 | 3 | `private static<K, V> Map<K, V> orderedCopy(Map<K, V> values)` | Immutable survey maps retain their recorded order across the native JSON round trip. |
| 71 | 34 | **type** `static final class Ordered<K, V> extends AbstractMap<K, V>` | A map nobody can change that keeps the order it was given: a survey's groups and a snapshot's measures, in the order they were recorded. |
| 75 | 7 | `Ordered(Map<K, V> from)` _(in DebtStatistics.Ordered)_ |  |
| 83 | 1 | `public V get(Object key)` _(in DebtStatistics.Ordered)_ |  |
| 84 | 1 | `public boolean containsKey(Object key)` _(in DebtStatistics.Ordered)_ |  |
| 85 | 1 | `public int size()` _(in DebtStatistics.Ordered)_ |  |
| 87 | 17 | `public Set<Map.Entry<K, V>> entrySet()` _(in DebtStatistics.Ordered)_ |  |
| 106 | 6 | `private static Map<Kind, Measure> scaledMeasures(Map<Kind, Measure> measures, double scale)` |  |
| 112 | 1 | **type** `record Component(double weight, double share, double mean, double sigma)` |  |
| 114 | 6 | `public static Stage stage(Household c)` |  |
| 122 | 8 | `static double amount(Household c, Kind kind)` | Cars remain financed on the card; owner mortgages arrive with HO1, so those books are zero. |
| 131 | 8 | `static double holding(Household c, Kind kind)` |  |
| 140 | 4 | `static double sigma(Kind kind)` |  |
| 145 | 8 | `static double cdf(List<Component> components, double x)` |  |
| 155 | 20 | `static double median(List<Component> components)` | Locate on the log grid, then refine only that interval; no household is sampled. |
| 176 | 3 | `public static Snapshot of(HouseholdBalance balance, PayTier tier, Stage stage)` |  |
| 180 | 52 | `private static Snapshot of(HouseholdBalance balance, PayTier tier, Stage stage, boolean untiered)` |  |
| 234 | 3 | `public static Snapshot untiered(HouseholdBalance balance, Stage stage)` | Students and other households without a pay tier, on the same stage axis. |
| 238 | 12 | `public static Survey survey(HouseholdBalance balance, int month)` |  |
| 251 | 1 | `private static double ratio(double a, double b)` |  |

