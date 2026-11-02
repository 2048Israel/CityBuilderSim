# RealDollarCheck.java - 551 lines · 23 methods · 4 constants · harnesses

`ham/citybuildersim/RealDollarCheck.java` - generated 2026-11-01 by CodeMap; line numbers are as of that run.

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
>      over its first January's level (the calendar's), computed from Nations' Januaries, to 1e-12 every month; the headline rate is this year's pace; the ring's realised
>      inflation is the level over the level a year ago; on Historical the Januaries equal the file's US CPI over 2000's; a pinned world holds; a
>      saved and loaded city follows the path again.
>   2. NO FILE, THE WALK (the literals): a city on the literals has the walk, its level the bare WorldEconomy's over the same months, to the bit.
>   3. D$ PER 2015 US$ is the rate times the level over the bridge (RealDollar.BRIDGE, EP1: the 2015 CPI over the founding year's, a founding US$ being
>      that many 2015 ones), and the rate over the bridge at founding.
>   4. REAL GDP IN 2015 US$ is the GDP over the index over the founding's rate, times the bridge.
>   5. THE REAL EFFECTIVE RATE: 100 at founding; the city's prices up 10% and nothing else moving reads 110; a partner's money 10% dearer against the
>      US$ with half the weight reads 100 x 1.1^-.5; the US alone with no trade; the weights are shares.
>   6. THE SHOCK (WorldEconomy.shockLevel(), the ensemble's import shock): on the path it is a factor the next month keeps, saved, and permanent; on the
>      walk it is the level times the size, which the pull then takes back, and what the old save-and-restore door did.
>   7. A CHANGE OF MONEY (WN6; pending row WN5-R): a nation that changed money since the first January (found in the file, in the Random town and in a
>      Historical one carried past a euro) is chained - RealDollar.moneyChain is [rate then / rate now] x [the US's prices then / now] x [its prices now /
>      then] for the year it changed - so that its real rate is held across the year; the town's real effective rate with trade on it alone is the chained
>      reading and not the old one; a nation that never changed reads as it did, to the bit.

**Uses:** [RealDollar](RealDollar.md) (71), [Nations](Nations.md) (36), [Game](Game.md) (23), [WorldEconomy](WorldEconomy.md) (23), [ForeignAccounts](ForeignAccounts.md) (13), [Founding](Founding.md) (10), [WorldData](WorldData.md) (9), [ForeignCheck](ForeignCheck.md) (7), [PartnerTrade](PartnerTrade.md) (6), [GameFiles](GameFiles.md) (5), [WorldMoneys](WorldMoneys.md) (4), [Good](Good.md) (4), [NationalAccounts](NationalAccounts.md) (1)

**Used by (1):** [PartnersCheck](PartnersCheck.md)

## Sections

| line | section |
|---:|---|
| 119 | · 1 |
| 210 | · 2 |
| 234 | · 3 |
| 253 | · 4 |
| 270 | · 5 |
| 336 | · 6 |
| 416 | · 7 |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 40 | `RealDollarCheck.out` | `System.out` |  |
| 41 | `RealDollarCheck.quiet` | `new PrintStream(OutputStream.nullOutputStream())` |  |
| 44 | `RealDollarCheck.SEED` | `20_261_031L` | The path seed the cities are founded on. |
| 339 | `RealDollarCheck.GROWING` | `new WorldEconomy.UsPath() { @ Override public double level(int t) { return Ma...` | A synthetic US: 0.2% a month, 2.4% a year, so a shock is read against a path that moves. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 39 | `static int fails` |  |
| 58 | `int n, bad` |  |
| 59 | `String first` |  |
| 419 | `static Game effectiveTown` | The Random town section 5 played thirty months on, kept for section 7 (null until section 5 has a file to play on). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 37 | 515 | **type** `public class RealDollarCheck` | The two US dollars (batch WN4, D29; spec-nations 10 item 29, spec-exchange-rates 3.9): the in-game US$ inflating on the US's path, and the real US$ of 2015 the city's real GDP and a rate are shown in, for comparison o... |
| 46 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 52 | 3 | `static void close(String label, double got, double want, double tol)` | A figure the arithmetic gives, judged to a tolerance. |
| 57 | 12 | **type** `static final class Tally` | Figures compared one by one, reported as one line: every one has to agree, and at least one has to have been read. |
| 61 | 7 | `void see(boolean ok, Supplier<String> what)` _(in RealDollarCheck.Tally)_ |  |
| 70 | 6 | `static void tally(String label, Tally t)` |  |
| 77 | 5 | `static void quietly(Runnable work)` |  |
| 83 | 1 | `static void play(Game city)` |  |
| 86 | 16 | `static Game town(String label, Founding founding)` | A trading town on a founding, as WorldMoneysCheck builds it, on a scratch folder; the world's file as named. |
| 103 | 1 | `static boolean same(double a, double b)` |  |
| 105 | 13 | `public static void main(String[] args)` |  |

### 1 (lines 119-209)

| line | len | member | says |
|---:|---:|---|---|
| 122 | 7 | `static double usLevel(Nations nations, int usa, int t)` | The US's price level in month t over its first January's level, computed here from Nations' Januaries, log-linear between them. |
| 131 | 4 | `static double usPace(Nations nations, int usa, int t)` | The US's inflation in the year of month t: January over January, less one. |
| 136 | 73 | `static void theDollarIsTheUs()` |  |

### 2 (lines 210-233)

| line | len | member | says |
|---:|---:|---|---|
| 212 | 21 | `static void noFileTheWalk()` |  |

### 3 (lines 234-252)

| line | len | member | says |
|---:|---:|---|---|
| 236 | 16 | `static void dollarsPer2015Usd()` |  |

### 4 (lines 253-269)

| line | len | member | says |
|---:|---:|---|---|
| 255 | 14 | `static void realGdp()` |  |

### 5 (lines 270-335)

| line | len | member | says |
|---:|---:|---|---|
| 272 | 63 | `static void realEffectiveRate()` |  |

### 6 (lines 336-415)

| line | len | member | says |
|---:|---:|---|---|
| 344 | 71 | `static void theShock()` |  |

### 7 (lines 416-551)

| line | len | member | says |
|---:|---:|---|---|
| 426 | 16 | `static int[] firstChange(Nations nations)` | The first nation, in the file's order, whose money changed since the first January (its Year.money differs from the year before's), and the first year it did, preferring one whose rate really moved (by more than 1%: E... |
| 444 | 9 | `static double chainBy(Nations nations, int usa, int n, int year)` | The chain as the brief defines it, written out: the product over each year of a change in (FIRST_YEAR, year] of [rate(Y-1)/rate(Y)] x [P_US(Y-1)/P_US(Y)] x [P_n(Y)/P_n(Y-1)]. |
| 455 | 4 | `static void tradeOnlyWith(Game town, int nation)` | Hands the town a finished year's trade with one nation alone, so that the real effective rate weighs it and no other (the door section 5 uses). |
| 460 | 12 | `static void changeOfMoney()` |  |
| 473 | 35 | `static void oneChange(String label, Game town)` |  |
| 513 | 38 | `static void chainAndGetter(String label, Game town, int n, Nations nations, WorldMoneys moneys, int usa, boolean changed)` | Weights the town's trade on one nation alone, brings the moneys on to the year after a change if they are not there, and holds the getter: a nation that changed reads as the definition says (against the way it was rea... |

