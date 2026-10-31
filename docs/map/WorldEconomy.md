# WorldEconomy.java - 521 lines · 27 methods · 11 constants · model

`ham/citybuildersim/WorldEconomy.java` - generated 2026-10-31 by CodeMap; line numbers are as of that run.

> The rest of the world, which has its own inflation and did not use to: the in-game US dollar's price level, which is the US's own path where the
> city has the nations' moneys (WN4, D29; THE US DOLLAR ON THE US'S PATH below) and a seeded walk around a mean where it has no world file.
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

**Uses:** [Nations](Nations.md) (3), [WorldMoneys](WorldMoneys.md) (2), [World](World.md) (1)

**Used by (14):** [CentralBankCheck](CentralBankCheck.md), [Founding](Founding.md), [FoundingScreen](FoundingScreen.md), [FundCheck](FundCheck.md), [Game](Game.md), [LongPlaytest](LongPlaytest.md), [MonetaryCheck](MonetaryCheck.md), [NewGameCheck](NewGameCheck.md), [PolicyScreen](PolicyScreen.md), [RealDollarCheck](RealDollarCheck.md), [SaveFileCheck](SaveFileCheck.md), [SavedCities](SavedCities.md), [SkipReportCheck](SkipReportCheck.md), [TradeScreen](TradeScreen.md)

## Sections

| line | section |
|---:|---|
| 41 | THE WORLD'S PRICE LEVEL IS STATIONARY, AND THE MEAN SAYS WHERE |
| 122 | WHY THE DRAW IS BIASED LOW, AND WHY THE BAND WAS A FICTION |
| 374 | THE US DOLLAR ON THE US'S PATH (WN4, D29; Jerus, 2026-10-21) |
| 461 | · carrying |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 73 | `WorldEconomy.DEFAULT_MEAN_INFLATION` | `.01` | What the world's inflation averages. |
| 76 | `WorldEconomy.INFLATION_SPREAD` | `.03` | How far either side of the mean a draw can land. |
| 79 | `WorldEconomy.MIN_MEAN_INFLATION` | `.0` | The most and least a city may be founded with. |
| 80 | `WorldEconomy.MAX_MEAN_INFLATION` | `.08` |  |
| 120 | `WorldEconomy.PERSISTENCE` | `.90` | How much of last month's rate survives into this one. |
| 175 | `WorldEconomy.LOW_BIAS` | `2.0` | How far the uniform draw is bent toward the bottom of the band. |
| 177 | `WorldEconomy.SEED` | `0x5F3A91C7L` |  |
| 236 | `WorldEconomy.TREND_INFLATION` | `.0` | The world's long-run trend, against which the wandering rate is a cycle. |
| 239 | `WorldEconomy.TREND_PULL` | `.006` | How hard the level is pulled back to trend. |
| 351 | `WorldEconomy.LEVEL_RING` | `13` |  |
| 507 | `WorldEconomy.LEGACY_MEAN_INFLATION` | `.0333` | The mean every city founded before 2026-09-13 grew up in. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 88 | `private double meanInflation` | The worlds the founding screen offered as five chips (FOUNDING_CHOICES) and the closed form under them (settledLevelAt) went in WN4: the US dollar is the US's money and inflates on its path, so no screen offers the me... |
| 187 | `private long seed` | The seed of the world's own draws: the founding's path seed (Founding.getPathSeed(); WN3, 2026-10-28, docs/studies/spec-nations.md 11, item 1). |
| 194 | `private double annualInflation` |  |
| 195 | `private double priceLevel` |  |
| 241 | `private int monthsRun` |  |
| 258 | `private boolean pinned` |  |
| 353 | `private final double[] levels` | The level at the end of each of the last thirteen months; head is the newest. |
| 354 | `private int head` |  |
| 420 | `private UsPath path` |  |
| 421 | `private double levelFactor` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 39 | 483 | **type** `public class WorldEconomy` | The rest of the world, which has its own inflation and did not use to: the in-game US dollar's price level, which is the US's own path where the city has the nations' moneys (WN4, D29; THE US DOLLAR ON THE US'S PATH b... |

### THE WORLD'S PRICE LEVEL IS STATIONARY, AND THE MEAN SAYS WHERE (lines 41-121)

| line | len | member | says |
|---:|---:|---|---|
| 101 | 4 | `public void setMeanInflation(double mean)` | Sets the world a city is founded into. |
| 106 | 1 | `public double getMeanInflation()` |  |
| 109 | 1 | `public double minInflation()` | The band this world's inflation wanders in: the mean, either way. |
| 110 | 1 | `public double maxInflation()` |  |

### WHY THE DRAW IS BIASED LOW, AND WHY THE BAND WAS A FICTION (lines 122-373)

| line | len | member | says |
|---:|---:|---|---|
| 190 | 1 | `public void setSeed(long seed)` | Sets the seed the draws are made from (Game: buildWorld() and the load, from the founding's path seed). |
| 192 | 1 | `public long getSeed()` |  |
| 198 | 6 | `private static long scramble(long n)` | Scrambles a month into something that does not correlate with its neighbours. |
| 254 | 1 | `public void pin()` | Holds the world's prices still, for a fixture measuring something else. |
| 256 | 1 | `public boolean isPinned()` |  |
| 260 | 31 | `public void advanceMonth(int month)` |  |
| 293 | 3 | `public double trendLevel()` | Where the trend says the world's prices should be by now. |
| 298 | 1 | `public double getPriceLevel()` | What the world's basket costs now against what it cost at founding. |
| 313 | 1 | `public double getInflation()` | The HEADLINE rate: what the band is doing this month. |
| 345 | 5 | `public double realisedInflation()` | What the world's prices ACTUALLY did over the last twelve months, from the level itself. |
| 359 | 1 | `{ ... }` | A fresh world has a year of history at its opening rate, so the first twelve months read the headline rather than zero. |
| 361 | 4 | `private void recordLevel()` |  |
| 367 | 6 | `private void backcastLevels()` | Fills the ring as if the headline rate had held for a year. |

### THE US DOLLAR ON THE US'S PATH (WN4, D29; Jerus, 2026-10-21) (lines 374-460)

| line | len | member | says |
|---:|---:|---|---|
| 412 | 7 | **type** `public interface UsPath` | The US's price path, as the dollar follows it: WorldMoneys' interpolation of the nations' price levels (Game hands it, followUsPath()). |
| 414 | 1 | `double level(int t)` _(in WorldEconomy.UsPath)_ | The US's price level in month t (months since January 2000) against its level in month 0. |
| 417 | 1 | `double pace(int t)` _(in WorldEconomy.UsPath)_ | The US's inflation in the calendar year of month t: P_US(y+1)/P_US(y) - 1. |
| 424 | 1 | `public void followUsPath(UsPath path)` | Follows the US's path from the next month (null: the walk again). |
| 427 | 6 | `public void followMoneys(WorldMoneys moneys)` | Follows the US's price path in these moneys (WorldMoneys.usPriceLevel, usPace); null the walk again. |
| 435 | 1 | `public boolean isOnUsPath()` | Whether the dollar's prices are the US's, on its path: false with no world file (the walk) and on the literals. |
| 438 | 1 | `public double getLevelFactor()` | What a shock has multiplied the path's level by: 1 until shockLevel() on a path. |
| 446 | 5 | `public void shockLevel(double size)` | An import-price shock: the world's price level times `size`, once. |
| 453 | 7 | `private void stepOnUsPath(int month)` | The month on the path: no draw, no pull; the level the US's times the factor, the headline rate this year's pace, the ring and the months run as ever. |

### carrying (lines 461-521)

| line | len | member | says |
|---:|---:|---|---|
| 463 | 16 | `public double[] toSaveArray()` |  |
| 480 | 25 | `public void restore(double[] saved)` |  |
| 509 | 12 | `public void reset()` |  |

