# StepsCheck.java - 212 lines · 7 methods · 0 constants · harnesses

`ham/citybuildersim/StepsCheck.java` - generated 2026-11-21 by CodeMap; line numbers are as of that run.

> Magnitude controls follow the figure and bond lots follow the month's money.
> 
> WHAT THIS HAS TO PROVE (SZ1):
> 1. Empty, small, large and billion-sized figures follow one leading-digit
>    rule, with no duplicate step and no step below the caller's least.
> 2. Every instrument's dial is priced at 1900, reference and hyperinflated
>    levels, with positive round lots and the minimum-issue cap caused.
> 3. The city's live lot API reads its price index, currency unit and minimum.
> 4. Real presses on a large building card and a large fund transfer move
>    large figures, and the card's next labels follow the new magnitude.
> Restoring the old fixed lots or fixed UI steps must fail this harness.

**Uses:** [BondMarket](BondMarket.md) (16), [PriceIndex](PriceIndex.md) (7), [KeptBench](KeptBench.md) (7), [PriceLevel](PriceLevel.md) (7), [MagnitudeSteps](MagnitudeSteps.md) (6), [Game](Game.md) (5), [Rollover](Rollover.md) (3), [Auction](Auction.md) (3), [GameFiles](GameFiles.md) (2), [DebtQuote](DebtQuote.md) (1), [KeptCheck](KeptCheck.md) (1), [BuildAdvice](BuildAdvice.md) (1)

## Fields (state)

| line | field | says |
|---:|---|---|
| 26 | `static int fails` |  |
| 27 | `static PrintStream out` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 25 | 188 | **type** `public final class StepsCheck` | Magnitude controls follow the figure and bond lots follow the month's money. |
| 29 | 4 | `static void check(String label, boolean ok)` |  |
| 34 | 4 | `static void close(String label, double actual, double expected)` |  |
| 39 | 3 | `static void steps(String label, double value, double least, double...expected)` |  |
| 43 | 5 | `static boolean round(double lot)` |  |
| 49 | 5 | `static<T> T fx(java.util.concurrent.Callable<T> call) throws Exception` |  |
| 56 | 9 | `static void uniformPrices(Game game, double level, int month)` | Every component changes equally, so the model's actual basket has this price ratio regardless of its weights. |
| 66 | 146 | `public static void main(String[] args) throws Exception` |  |

