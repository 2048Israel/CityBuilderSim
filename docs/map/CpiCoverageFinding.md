# CpiCoverageFinding.java - 353 lines · 14 methods · 2 constants · model

`ham/citybuildersim/CpiCoverageFinding.java` - generated 2026-11-28 by CodeMap; line numbers are as of that run.

> Nine-component CPI coverage, comparable prices, and native saved observations.
> WHY: closing fleets cannot recover purchases, and a pump/queue unit-value average
> can move with unchanged prices. Exercise both actual transactions and index math.

**Uses:** [PriceIndex](PriceIndex.md) (73), [CpiReferenceFinding](CpiReferenceFinding.md) (14), [Game](Game.md) (11), [Good](Good.md) (9), [HistorySave](HistorySave.md) (8), [Motoring](Motoring.md) (7), [Household](Household.md) (3), [GameFiles](GameFiles.md) (2), [JobType](JobType.md) (2), [HouseholdBalance](HouseholdBalance.md) (1), [Founding](Founding.md) (1)

**Used by (1):** [MonetaryCheck](MonetaryCheck.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 16 | `CpiCoverageFinding.FEES` | `{ 2, 1,.5,.1 }, FEE_SPEND = { 5, 3, 2, 0 }` |  |
| 17 | `CpiCoverageFinding.SAVED_AT` | `Pattern.compile("(\"savedAt\"\\s*:\\s*)[0-9]+")` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 18 | `private static int nativeRead` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 15 | 339 | **type** `public final class CpiCoverageFinding` | Nine-component CPI coverage, comparable prices, and native saved observations. |
| 20 | 1 | `private static void check(String label, boolean ok)` |  |
| 21 | 1 | `private static void close(String label, double actual, double expected)` |  |
| 22 | 1 | `private static void quietly(Runnable action)` |  |
| 24 | 5 | `private static PriceIndex based(double[] prices, double[] spend)` |  |
| 30 | 81 | `private static void priceMath()` |  |
| 112 | 2 | **type** `private record Guards(Motoring.NewTrade cars, Motoring.PetrolPrices petrol, double fuel, double litres, dou...` |  |
| 115 | 7 | `private static Guards guards(Game g)` |  |
| 124 | 6 | `private static List<Double> currentInputs(Game g)` | The changed current acquisition inputs, whose existing ledgers/observations must also survive a load. |
| 131 | 22 | `private static void readsAndHistory(Game g)` |  |
| 155 | 35 | `private static void normalFuelPeriods(Game g)` | Two ordinary complete months distinguish the struck and current acquisition books. |
| 192 | 12 | `private static String nativeText(GameFiles files, boolean history) throws Exception` | Preserve all serialized text except the one wall-clock metadata timestamp. |
| 206 | 35 | `private static void cachedInputs(Game g, String label, String role) throws Exception` | Bounded read-only evidence for money inputs not represented by the native save's cached totals. |
| 242 | 39 | `private static void nativeTwins(Game g, GameFiles files, String label) throws Exception` |  |
| 282 | 65 | `static int run() throws Exception` |  |
| 348 | 5 | `public static void main(String[] args) throws Exception` |  |

