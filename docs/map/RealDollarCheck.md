# RealDollarCheck.java - 408 lines · 17 methods · 4 constants · harnesses

`ham/citybuildersim/RealDollarCheck.java` - generated 2026-10-31 by CodeMap; line numbers are as of that run.

> The two US dollars (batch WN4, D29; spec-nations 10 item 29, spec-exchange-rates 3.9): the in-game US$ inflating on the US's path, and the real US$ of
> 2015 the city's real GDP and a rate are shown in, for comparison only.
> 
> WHY. The dollar's prices were a seeded walk around a mean the founding screen's five chips set, pulled back to a flat trend: a US that inflated by one
> rule and a dollar that inflated by another. Where the city has the nations' moneys the world's price level is now the US's own, month by month, and
> the three comparison figures are made of it; a harness holds the level to the nations' path computed another way, the walk to the walk it always was,
> and each figure to the arithmetic it is defined by - a fixture that causes the condition, not the model's own number read back.
> 
> What this has to prove:
>   1. THE DOLLAR IS THE US'S (the full file): on Random and on Historical, 37 months of a city, the world's price level equals the US's price level
>      over its January 2000 level, computed from Nations' Januaries, to 1e-12 every month; the headline rate is this year's pace; the ring's realised
>      inflation is the level over the level a year ago; on Historical the Januaries equal the file's US CPI over 2000's; a pinned world holds; a
>      saved and loaded city follows the path again.
>   2. NO FILE, THE WALK (the literals): a city on the literals has the walk, its level the bare WorldEconomy's over the same months, to the bit.
>   3. D$ PER 2015 US$ is the rate times the level, and the rate at founding.
>   4. REAL GDP IN 2015 US$ is the GDP over the index over the founding's rate.
>   5. THE REAL EFFECTIVE RATE: 100 at founding; the city's prices up 10% and nothing else moving reads 110; a partner's money 10% dearer against the
>      US$ with half the weight reads 100 x 1.1^-.5; the US alone with no trade; the weights are shares.
>   6. THE SHOCK (WorldEconomy.shockLevel(), the ensemble's import shock): on the path it is a factor the next month keeps, saved, and permanent; on the
>      walk it is the level times the size, which the pull then takes back, and what the old save-and-restore door did.

**Uses:** [RealDollar](RealDollar.md) (49), [WorldEconomy](WorldEconomy.md) (23), [Game](Game.md) (18), [Nations](Nations.md) (15), [ForeignAccounts](ForeignAccounts.md) (11), [Founding](Founding.md) (9), [ForeignCheck](ForeignCheck.md) (7), [WorldData](WorldData.md) (6), [GameFiles](GameFiles.md) (5), [PartnerTrade](PartnerTrade.md) (4), [WorldMoneys](WorldMoneys.md) (2), [Good](Good.md) (2), [NationalAccounts](NationalAccounts.md) (1)

## Sections

| line | section |
|---:|---|
| 113 | · 1 |
| 204 | · 2 |
| 228 | · 3 |
| 247 | · 4 |
| 264 | · 5 |
| 329 | · 6 |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 35 | `RealDollarCheck.out` | `System.out` |  |
| 36 | `RealDollarCheck.quiet` | `new PrintStream(OutputStream.nullOutputStream())` |  |
| 39 | `RealDollarCheck.SEED` | `20_261_031L` | The path seed the cities are founded on. |
| 332 | `RealDollarCheck.GROWING` | `new WorldEconomy.UsPath() { @ Override public double level(int t) { return Ma...` | A synthetic US: 0.2% a month, 2.4% a year, so a shock is read against a path that moves. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 34 | `static int fails` |  |
| 53 | `int n, bad` |  |
| 54 | `String first` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 32 | 377 | **type** `public class RealDollarCheck` | The two US dollars (batch WN4, D29; spec-nations 10 item 29, spec-exchange-rates 3.9): the in-game US$ inflating on the US's path, and the real US$ of 2015 the city's real GDP and a rate are shown in, for comparison o... |
| 41 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 47 | 3 | `static void close(String label, double got, double want, double tol)` | A figure the arithmetic gives, judged to a tolerance. |
| 52 | 12 | **type** `static final class Tally` | Figures compared one by one, reported as one line: every one has to agree, and at least one has to have been read. |
| 56 | 7 | `void see(boolean ok, Supplier<String> what)` _(in RealDollarCheck.Tally)_ |  |
| 65 | 6 | `static void tally(String label, Tally t)` |  |
| 72 | 5 | `static void quietly(Runnable work)` |  |
| 78 | 1 | `static void play(Game city)` |  |
| 81 | 16 | `static Game town(String label, Founding founding)` | A trading town on a founding, as WorldMoneysCheck builds it, on a scratch folder; the world's file as named. |
| 98 | 1 | `static boolean same(double a, double b)` |  |
| 100 | 12 | `public static void main(String[] args)` |  |

### 1 (lines 113-203)

| line | len | member | says |
|---:|---:|---|---|
| 116 | 7 | `static double usLevel(Nations nations, int usa, int t)` | The US's price level in month t over its January 2000 level, computed here from Nations' Januaries, log-linear between them. |
| 125 | 4 | `static double usPace(Nations nations, int usa, int t)` | The US's inflation in the year of month t: January over January, less one. |
| 130 | 73 | `static void theDollarIsTheUs()` |  |

### 2 (lines 204-227)

| line | len | member | says |
|---:|---:|---|---|
| 206 | 21 | `static void noFileTheWalk()` |  |

### 3 (lines 228-246)

| line | len | member | says |
|---:|---:|---|---|
| 230 | 16 | `static void dollarsPer2015Usd()` |  |

### 4 (lines 247-263)

| line | len | member | says |
|---:|---:|---|---|
| 249 | 14 | `static void realGdp()` |  |

### 5 (lines 264-328)

| line | len | member | says |
|---:|---:|---|---|
| 266 | 62 | `static void realEffectiveRate()` |  |

### 6 (lines 329-408)

| line | len | member | says |
|---:|---:|---|---|
| 337 | 71 | `static void theShock()` |  |

