# MonetaryCheck.java - 638 lines · 10 methods · 5 constants · harnesses

`ham/citybuildersim/MonetaryCheck.java` - generated 2026-09-24 by CodeMap; line numbers are as of that run.

> Money: what a basket costs, what the world charges, and what the rate does.
> 
> WHAT IS ACTUALLY BEING ASKED
> 
>   1. Does the price index measure what households BUY, on a basket fixed at a
>      base period? A CPI that re-weights as spending shifts shows no inflation
>      for a family that switched to cheaper food while eating worse.
> 
>   2. Do prices RATION? A shop that can meet a fifth of demand and charges
>      cost-plus is not a shop, it is a queue - and a model with no demand-pull
>      channel gives a policy rate nothing to cool.
> 
>   3. Is the world a real place? Its own inflation is the one price shock the
>      player cannot cause and cannot stop.
> 
>   4. And does the rate DO anything - to credit, to the currency, and to the
>      city that has to live with it? And since 0.7.4, does the rule aim
>      where the player's target says, and only move its intercept?
> 
>   5. Does all of it hold in a real city, and survive a reload?
> 
>   6. And does INFLATION FALL WITH THE RATE? One founding held at 3%, 10%,
>      20% and 40% from month 25 to 60: each higher dial's inflation no
>      higher than the lower one's, within the noise a month's delay in the
>      hand makes, and the 3% row at least a point above the 40% row. A
>      measurement from 0.6.11 to 0.7.2, the baseline 7.0 had to turn; an
>      assertion since 0.7.3, with the households' saving answering the real
>      deposit rate - and the columns that say which channel carried it.

**Uses:** [DebtManager](DebtManager.md) (18), [Game](Game.md) (18), [PriceIndex](PriceIndex.md) (12), [ForeignAccounts](ForeignAccounts.md) (11), [Retail](Retail.md) (8), [LongPlaytest](LongPlaytest.md) (7), [WorldEconomy](WorldEconomy.md) (6), [GameFiles](GameFiles.md) (5), [BuildingsTemplate](BuildingsTemplate.md) (2), [Founding](Founding.md) (1), [Bank](Bank.md) (1)

**Used by (1):** [HouseholdCheck](HouseholdCheck.md)

## Sections

| line | section |
|---:|---|
| 64 | · 1. the basket |
| 88 | · · the high and low water marks |
| 140 | · 2. prices ration |
| 160 | · 3. the world is a real place |
| 193 | · 4. and the rate does something |
| 287 | · 5. in a city, and across a reload |
| 391 | 6. INFLATION FALLS WITH THE RATE (asserted since 0.7.3; measured |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 471 | `MonetaryCheck.HELD_RATES` | `{.03,.10,.20,.40 }` | The policy rates the one founding is held at, from month 25: the three of the baseline, and since 0.7.2 a fourth at 40% - past the old stop of the dial (25% until 0.7.2), the uncapped case. |
| 474 | `MonetaryCheck.MEASURED_MONTHS` | `60` | How long each run is: five years, the last three of them at the held rate. |
| 477 | `MonetaryCheck.HELD_FROM` | `ForeignAccounts.SETTLING_MONTHS + 1` | The month the dial is held from: the first in which the currency may move. |
| 490 | `MonetaryCheck.MEASUREMENT_NOISE` | `.0010` | How far apart two runs of the one founding may read, in inflation a year, when only the dial's timing moves: 0.10 points. |
| 493 | `MonetaryCheck.TRANSMISSION_FLOOR` | `.01` | How much lower inflation must run at a dial of 40% than at 3%, a year: one point - the channel has to be worth a point across the range or it is not a channel. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 40 | `static int fails` |  |
| 41 | `static PrintStream out` |  |
| 42 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 38 | 601 | **type** `public class MonetaryCheck` | Money: what a basket costs, what the world charges, and what the rate does. |
| 44 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 49 | 9 | `static void close(String label, double actual, double expected, double tol)` |  |
| 59 | 331 | `public static void main(String[] args) throws Exception` |  |

### 6. INFLATION FALLS WITH THE RATE (asserted since 0.7.3; measured (lines 391-638)

| line | len | member | says |
|---:|---:|---|---|
| 496 | 3 | `static void step(Game g, double heldRate)` | One month, as the playtest steps it: a broke city steps rather than skips. |
| 501 | 6 | `static void step(Game g, double heldRate, int heldFrom)` | ...holding the dial from a month of the caller's: the noise run holds it a month late. |
| 509 | 16 | `static Game founding(Path root, String label)` | The playtest's founding (seed 0), to the month before the dial is held. |
| 527 | 8 | `static double[] fingerprint(Game g)` | What the founding looks like the month before the dial moves - equal across the runs, or they are not one founding. |
| 541 | 14 | `static double heldRun(Path root, String label, double rate, int heldFrom, double[] print, Game[] city)` | One run of the founding at a held rate: the dial held from heldFrom to MEASURED_MONTHS, and inflation over the held months as an annual rate. |
| 556 | 75 | `static void inflationFallsWithTheRate(Path root)` |  |
| 632 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |

