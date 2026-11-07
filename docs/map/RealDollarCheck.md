# RealDollarCheck.java - 607 lines · 23 methods · 4 constants · harnesses

`ham/citybuildersim/RealDollarCheck.java` - generated 2026-11-07 by CodeMap; line numbers are as of that run.

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
>      US$ with half the weight reads 100 x 1.1^-.5; the US alone with no trade; the weights are shares. And (WN5b, which names the partners on the Trade
>      screen): a town with no trade weighs no partner to name; with a finished year's trade the partners it weighs are named in nation order with
>      their shares (Game.realEffectivePartners()), the rate is made of exactly those partners to the bit, and a money that never changed is weighed at
>      its rate in the first January, else the file's first-year figure (how many nations take the fallback is counted, not assumed).
>   6. THE SHOCK (WorldEconomy.shockLevel(), the ensemble's import shock): on the path it is a factor the next month keeps, saved, and permanent; on the
>      walk it is the level times the size, which the pull then takes back, and what the old save-and-restore door did.
>   7. A CHANGE OF MONEY (WN6; pending row WN5-R): a nation that changed money since the first January (found in the file, in the Random town and in a
>      Historical one carried past a euro) is chained - RealDollar.moneyChain is [rate then / rate now] x [the US's prices then / now] x [its prices now /
>      then] for the year it changed - so that its real rate is held across the year; the town's real effective rate with trade on it alone is the chained
>      reading and not the old one; a nation that never changed reads as it did, to the bit.

**Uses:** [RealDollar](RealDollar.md) (80), [Nations](Nations.md) (40), [Game](Game.md) (24), [WorldEconomy](WorldEconomy.md) (23), [ForeignAccounts](ForeignAccounts.md) (15), [Founding](Founding.md) (11), [WorldData](WorldData.md) (9), [ForeignCheck](ForeignCheck.md) (7), [PartnerTrade](PartnerTrade.md) (7), [GameFiles](GameFiles.md) (5), [WorldMoneys](WorldMoneys.md) (4), [Good](Good.md) (4), [NationalAccounts](NationalAccounts.md) (1)

**Used by (1):** [PartnersCheck](PartnersCheck.md)

## Sections

| line | section |
|---:|---|
| 122 | · 1 |
| 213 | · 2 |
| 237 | · 3 |
| 256 | · 4 |
| 273 | · 5 |
| 392 | · 6 |
| 472 | · 7 |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 43 | `RealDollarCheck.out` | `System.out` |  |
| 44 | `RealDollarCheck.quiet` | `new PrintStream(OutputStream.nullOutputStream())` |  |
| 47 | `RealDollarCheck.SEED` | `20_261_031L` | The path seed the cities are founded on. |
| 395 | `RealDollarCheck.GROWING` | `new WorldEconomy.UsPath() { @ Override public double level(int t) { return Ma...` | A synthetic US: 0.2% a month, 2.4% a year, so a shock is read against a path that moves. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 42 | `static int fails` |  |
| 61 | `int n, bad` |  |
| 62 | `String first` |  |
| 475 | `static Game effectiveTown` | The Random town section 5 played thirty months on, kept for section 7 (null until section 5 has a file to play on). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 40 | 568 | **type** `public class RealDollarCheck` | The two US dollars (batch WN4, D29; spec-nations 10 item 29, spec-exchange-rates 3.9): the in-game US$ inflating on the US's path, and the real US$ of 2015 the city's real GDP and a rate are shown in, for comparison o... |
| 49 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 55 | 3 | `static void close(String label, double got, double want, double tol)` | A figure the arithmetic gives, judged to a tolerance. |
| 60 | 12 | **type** `static final class Tally` | Figures compared one by one, reported as one line: every one has to agree, and at least one has to have been read. |
| 64 | 7 | `void see(boolean ok, Supplier<String> what)` _(in RealDollarCheck.Tally)_ |  |
| 73 | 6 | `static void tally(String label, Tally t)` |  |
| 80 | 5 | `static void quietly(Runnable work)` |  |
| 86 | 1 | `static void play(Game city)` |  |
| 89 | 16 | `static Game town(String label, Founding founding)` | A trading town on a founding, as WorldMoneysCheck builds it, on a scratch folder; the world's file as named. |
| 106 | 1 | `static boolean same(double a, double b)` |  |
| 108 | 13 | `public static void main(String[] args)` |  |

### 1 (lines 122-212)

| line | len | member | says |
|---:|---:|---|---|
| 125 | 7 | `static double usLevel(Nations nations, int usa, int t)` | The US's price level in month t over its first January's level, computed here from Nations' Januaries, log-linear between them. |
| 134 | 4 | `static double usPace(Nations nations, int usa, int t)` | The US's inflation in the year of month t: January over January, less one. |
| 139 | 73 | `static void theDollarIsTheUs()` |  |

### 2 (lines 213-236)

| line | len | member | says |
|---:|---:|---|---|
| 215 | 21 | `static void noFileTheWalk()` |  |

### 3 (lines 237-255)

| line | len | member | says |
|---:|---:|---|---|
| 239 | 16 | `static void dollarsPer2015Usd()` |  |

### 4 (lines 256-272)

| line | len | member | says |
|---:|---:|---|---|
| 258 | 14 | `static void realGdp()` |  |

### 5 (lines 273-391)

| line | len | member | says |
|---:|---:|---|---|
| 275 | 116 | `static void realEffectiveRate()` |  |

### 6 (lines 392-471)

| line | len | member | says |
|---:|---:|---|---|
| 400 | 71 | `static void theShock()` |  |

### 7 (lines 472-607)

| line | len | member | says |
|---:|---:|---|---|
| 482 | 16 | `static int[] firstChange(Nations nations)` | The first nation, in the file's order, whose money changed since the first January (its Year.money differs from the year before's), and the first year it did, preferring one whose rate really moved (by more than 1%: E... |
| 500 | 9 | `static double chainBy(Nations nations, int usa, int n, int year)` | The chain as the brief defines it, written out: the product over each year of a change in (FIRST_YEAR, year] of [rate(Y-1)/rate(Y)] x [P_US(Y-1)/P_US(Y)] x [P_n(Y)/P_n(Y-1)]. |
| 511 | 4 | `static void tradeOnlyWith(Game town, int nation)` | Hands the town a finished year's trade with one nation alone, so that the real effective rate weighs it and no other (the door section 5 uses). |
| 516 | 12 | `static void changeOfMoney()` |  |
| 529 | 35 | `static void oneChange(String label, Game town)` |  |
| 569 | 38 | `static void chainAndGetter(String label, Game town, int n, Nations nations, WorldMoneys moneys, int usa, boolean changed)` | Weights the town's trade on one nation alone, brings the moneys on to the year after a change if they are not there, and holds the getter: a nation that changed reads as the definition says (against the way it was rea... |

