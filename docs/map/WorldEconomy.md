# WorldEconomy.java - 440 lines · 20 methods · 12 constants · model

`ham/citybuildersim/WorldEconomy.java` - generated 2026-10-29 by CodeMap; line numbers are as of that run.

> The rest of the world, which has its own inflation and did not use to.
> 
> WHY THIS EXISTS. Every foreign price in the game was a constant - food at
> $0.20, building materials at $2.00, ore at $0.20 - converted at the exchange
> rate and otherwise frozen since the founding of the city. That is a world in
> which nothing outside ever changes, and it had two consequences that were
> both quietly load-bearing:
> 
>   - the only way an import could get dearer was for the CITY's currency to
>     fall, so every price shock the player felt was their own fault. There was
>     no such thing as bad luck from outside.
>   - purchasing power parity had to be pinned at 1.00, because the world's
>     basket never moved. An anchor at a constant is an anchor to the founding
>     year for ever, which is not what PPP means and gets worse the longer a
>     city runs.
> 
> Jerus: "have the USD itself have a random weighted avg moving inflation of
> random 1-8%".
> 
> A WEIGHTED MOVING AVERAGE OF RANDOM DRAWS, which is what that describes and
> also what inflation actually looks like. A fresh uniform draw every month
> would be white noise - 1% in March, 8% in April, meaningless in a twelve-month
> rate. Real inflation is persistent: this year's is mostly last year's. So each
> month draws, and the rate walks a small fraction of the way toward the draw,
> which produces long slow swings between the bounds rather than a jitter.
> 
> DETERMINISTIC, seeded off the month like Health's outbreak roll, because
> ForeignCheck asserts that two runs of the same city come out identical and a
> world with real randomness in it would end that.

**Uses:** [Nations](Nations.md) (3), [World](World.md) (1)

**Used by (13):** [CentralBankCheck](CentralBankCheck.md), [Founding](Founding.md), [FoundingScreen](FoundingScreen.md), [FundCheck](FundCheck.md), [Game](Game.md), [LongPlaytest](LongPlaytest.md), [MonetaryCheck](MonetaryCheck.md), [NewGameCheck](NewGameCheck.md), [PolicyScreen](PolicyScreen.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [SkipReportCheck](SkipReportCheck.md), [TradeScreen](TradeScreen.md)

## Sections

| line | section |
|---:|---|
| 40 | THE WORLD'S PRICE LEVEL IS STATIONARY, AND THE MEAN SAYS WHERE |
| 140 | WHY THE DRAW IS BIASED LOW, AND WHY THE BAND WAS A FICTION |
| 387 | · carrying |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 72 | `WorldEconomy.DEFAULT_MEAN_INFLATION` | `.01` | What the world's inflation averages. |
| 75 | `WorldEconomy.INFLATION_SPREAD` | `.03` | How far either side of the mean a draw can land. |
| 78 | `WorldEconomy.MIN_MEAN_INFLATION` | `.0` | The most and least a city may be founded with. |
| 79 | `WorldEconomy.MAX_MEAN_INFLATION` | `.08` |  |
| 87 | `WorldEconomy.FOUNDING_CHOICES` | `{ 0, DEFAULT_MEAN_INFLATION,.02,.0333,.05 }` | The worlds the founding screen offers, as its five chips: none, the default, twice it, the mean every city grew up in before 2026-09-13 (LEGACY_MEAN_INFLATION), and five. |
| 138 | `WorldEconomy.PERSISTENCE` | `.90` | How much of last month's rate survives into this one. |
| 193 | `WorldEconomy.LOW_BIAS` | `2.0` | How far the uniform draw is bent toward the bottom of the band. |
| 195 | `WorldEconomy.SEED` | `0x5F3A91C7L` |  |
| 254 | `WorldEconomy.TREND_INFLATION` | `.0` | The world's long-run trend, against which the wandering rate is a cycle. |
| 257 | `WorldEconomy.TREND_PULL` | `.006` | How hard the level is pulled back to trend. |
| 364 | `WorldEconomy.LEVEL_RING` | `13` |  |
| 429 | `WorldEconomy.LEGACY_MEAN_INFLATION` | `.0333` | The mean every city founded before 2026-09-13 grew up in. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 106 | `private double meanInflation` |  |
| 205 | `private long seed` | The seed of the world's own draws: the founding's path seed (Founding.getPathSeed(); WN3, 2026-10-28, docs/studies/spec-nations.md 11, item 1). |
| 212 | `private double annualInflation` |  |
| 213 | `private double priceLevel` |  |
| 259 | `private int monthsRun` |  |
| 276 | `private boolean pinned` |  |
| 366 | `private final double[] levels` | The level at the end of each of the last thirteen months; head is the newest. |
| 367 | `private int head` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 38 | 403 | **type** `public class WorldEconomy` | The rest of the world, which has its own inflation and did not use to. |

### THE WORLD'S PRICE LEVEL IS STATIONARY, AND THE MEAN SAYS WHERE (lines 40-139)

| line | len | member | says |
|---:|---:|---|---|
| 100 | 5 | `public static double settledLevelAt(double mean)` | Where the world's price level settles at a given mean. |
| 119 | 4 | `public void setMeanInflation(double mean)` | Sets the world a city is founded into. |
| 124 | 1 | `public double getMeanInflation()` |  |
| 127 | 1 | `public double minInflation()` | The band this world's inflation wanders in: the mean, either way. |
| 128 | 1 | `public double maxInflation()` |  |

### WHY THE DRAW IS BIASED LOW, AND WHY THE BAND WAS A FICTION (lines 140-386)

| line | len | member | says |
|---:|---:|---|---|
| 208 | 1 | `public void setSeed(long seed)` | Sets the seed the draws are made from (Game: buildWorld() and the load, from the founding's path seed). |
| 210 | 1 | `public long getSeed()` |  |
| 216 | 6 | `private static long scramble(long n)` | Scrambles a month into something that does not correlate with its neighbours. |
| 272 | 1 | `public void pin()` | Holds the world's prices still, for a fixture measuring something else. |
| 274 | 1 | `public boolean isPinned()` |  |
| 278 | 26 | `public void advanceMonth(int month)` |  |
| 306 | 3 | `public double trendLevel()` | Where the trend says the world's prices should be by now. |
| 311 | 1 | `public double getPriceLevel()` | What the world's basket costs now against what it cost at founding. |
| 326 | 1 | `public double getInflation()` | The HEADLINE rate: what the band is doing this month. |
| 358 | 5 | `public double realisedInflation()` | What the world's prices ACTUALLY did over the last twelve months, from the level itself. |
| 372 | 1 | `{ ... }` | A fresh world has a year of history at its opening rate, so the first twelve months read the headline rather than zero. |
| 374 | 4 | `private void recordLevel()` |  |
| 380 | 6 | `private void backcastLevels()` | Fills the ring as if the headline rate had held for a year. |

### carrying (lines 387-440)

| line | len | member | says |
|---:|---:|---|---|
| 389 | 14 | `public double[] toSaveArray()` |  |
| 404 | 23 | `public void restore(double[] saved)` |  |
| 431 | 9 | `public void reset()` |  |

