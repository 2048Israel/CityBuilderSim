# PowerViewCheck.java - 377 lines · 14 methods · 1 constants · harnesses

`ham/citybuildersim/PowerViewCheck.java` - generated 2026-11-22 by CodeMap; line numbers are as of that run.

> Power observations against native dispatch, including shortage, curtailment, fuel order, phase timing and saves.
> WHY. A screen can look plausible while showing next month's fleet or calling row receipts profit. The fixtures cause
> each condition through the real utility, and compare projections with its retained records rather than a copied market.

**Uses:** [PowerView](PowerView.md) (27), [UtilitiesHandler](UtilitiesHandler.md) (24), [PowerPriceCheck](PowerPriceCheck.md) (22), [PowerDispatch](PowerDispatch.md) (17), [HistorySave](HistorySave.md) (10), [BuildingsTemplate](BuildingsTemplate.md) (9), [Game](Game.md) (8), [YearBook](YearBook.md) (8), [BuildAdvice](BuildAdvice.md) (6), [Good](Good.md) (4), [BuildingCatalog](BuildingCatalog.md) (2), [MoneyAudit](MoneyAudit.md) (2), [ReadPathCheck](ReadPathCheck.md) (2), [CityCalendar](CityCalendar.md) (2), [BuildingType](BuildingType.md) (1)

**Used by (1):** [SaveFileCheck](SaveFileCheck.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 16 | `PowerViewCheck.JSON` | `new Gson()` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 15 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 14 | 364 | **type** `public final class PowerViewCheck` | Power observations against native dispatch, including shortage, curtailment, fuel order, phase timing and saves. |
| 17 | 4 | `static void check(String label, boolean ok)` |  |
| 21 | 1 | `static boolean near(double a, double b)` |  |
| 22 | 3 | `static BuildingsTemplate row(String name)` |  |
| 25 | 9 | `static UtilitiesHandler fixture(Map<BuildingsTemplate, Long> plants, double draw)` |  |
| 34 | 1 | `static PowerView.View view(UtilitiesHandler u)` |  |
| 36 | 5 | `static String savedModel(Game g)` |  |
| 42 | 32 | `static void matches(UtilitiesHandler u)` |  |
| 75 | 28 | `static void dispatchCases()` |  |
| 104 | 19 | `static void meritMoves()` |  |
| 124 | 25 | `static boolean saveChecks()` |  |
| 150 | 68 | `static boolean pendingSaveChecks()` |  |
| 219 | 67 | `static void timingPurityAndFold()` |  |
| 288 | 80 | `static void rowLeaveRejoinHistory()` | Fixed native months cause departure and rejoin; synthetic tail aging only puts that actual history through its yearly fold. |
| 369 | 8 | `public static void main(String[] args)` |  |

