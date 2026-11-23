# PowerView.java - 95 lines · 4 methods · 0 constants · model

`ham/citybuildersim/PowerView.java` - generated 2026-11-23 by CodeMap; line numbers are as of that run.

> The power screen's pure projection of a retained dispatch and its struck tariff.
> WHY. Ending fleet, fuel prices and the next dispatch cannot explain an earlier month's clearing receipts.
> Every figure here comes from the recorded book; no dispatch, credit or pricing policy runs on the read path.
> Power is kW, energy kWh, money thousands of the city's currency, and prices thousands per kWh.

**Uses:** [UtilitiesHandler](UtilitiesHandler.md) (9), [PowerDispatch](PowerDispatch.md) (5), [Game](Game.md) (1), [BuildingsTemplate](BuildingsTemplate.md) (1)

**Used by (5):** [HistoryCheck](HistoryCheck.md), [HistorySave](HistorySave.md), [PowerViewCheck](PowerViewCheck.md), [ReadPathCheck](ReadPathCheck.md), [ServicesScreen](ServicesScreen.md)

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 15 | 81 | **type** `public final class PowerView` | The power screen's pure projection of a retained dispatch and its struck tariff. |
| 16 | 1 | `private PowerView()` |  |
| 18 | 1 | **type** `public record Half(String name, double hours, double askedKw, double unservedKw, double price)` |  |
| 19 | 1 | **type** `public record Row(String name, UtilitiesHandler.RowRecord record)` |  |
| 20 | 1 | **type** `public record Bill(double energy, double transmission, double distribution, double commerce, double industry)` |  |
| 21 | 1 | **type** `public record Payment(int dispatchMonth, int month, double localFuel, double importedFuel, double upkeep)` |  |
| 22 | 5 | **type** `public record View(boolean cleared, boolean recorded, int dispatchMonth, int struckMonth, List<Row> rows, L...` |  |
| 25 | 1 | `{ ... }` _(in PowerView.View)_ |  |
| 28 | 6 | `public static View of(Game game)` |  |
| 36 | 28 | `public static View of(UtilitiesHandler utility, IntFunction<String> names)` | The same public observation path, with catalogue names supplied separately for native dispatch fixtures. |
| 66 | 29 | `public static Map<String, Double> series(View v)` | Bounded series keyed by permanent catalogue id, never by building or mutable title. |

