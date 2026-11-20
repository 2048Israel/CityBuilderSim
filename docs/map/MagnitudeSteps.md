# MagnitudeSteps.java - 39 lines · 4 methods · 0 constants · model

`ham/citybuildersim/MagnitudeSteps.java` - generated 2026-11-20 by CodeMap; line numbers are as of that run.

> The steps of a count, an area or an amount, following its leading digit.
> 
> WHY: fixed +10/+100 counts and million-dollar transfers took hundreds of
> presses in a large city (SZ1). One pure rule serves every magnitude control.

**Used by (6):** [BondMarket](BondMarket.md), [FinancesScreen](FinancesScreen.md), [FundScreen](FundScreen.md), [Pieces](Pieces.md), [StepsCheck](StepsCheck.md), [TradeScreen](TradeScreen.md)

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 9 | 31 | **type** `public final class MagnitudeSteps` | The steps of a count, an area or an amount, following its leading digit. |
| 10 | 1 | `private MagnitudeSteps()` |  |
| 13 | 9 | `public static double[] of(double value, double least)` | Leading unit, then its tenth, without duplicates or a step below least; an empty figure has just least. |
| 24 | 4 | `public static long count(long value, double step)` | A count moved by a step, clamped at zero and saturated before long arithmetic could wrap. |
| 30 | 9 | `public static double roundDown125(double value)` | The largest 1, 2 or 5 times a power of ten no greater than value. |

