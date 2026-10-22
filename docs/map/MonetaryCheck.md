# MonetaryCheck.java - 917 lines · 10 methods · 5 constants · harnesses

`ham/citybuildersim/MonetaryCheck.java` - generated 2026-10-21 by CodeMap; line numbers are as of that run.

> Money: what a basket costs, what the world charges, and what the rate does.
> 
> WHAT IS ACTUALLY BEING ASKED
> 
>   1. Does the price index measure what households BUY, on a basket fixed at a
>      base period? A CPI that re-weights as spending shifts shows no inflation
>      for a family that switched to cheaper food while eating worse. Since
>      0.7.43: five components on the trailing year's spending, luxury capped,
>      struck again every REBASE_MONTHS and chained, so neither the level nor
>      the year's rate jumps at a link - and since 0.7.45 each component's own
>      level runs through it too.
> 
>   2. Do prices RATION? A shop that can meet a fifth of demand and charges
>      cost-plus is not a shop, it is a queue - and a model with no demand-pull
>      channel gives a policy rate nothing to cool. Since 0.7.43 the shelf aims
>      at the price that clears, between its floor and CLEARING_CAP over it,
>      and moves CLEAR_SPEED of the way there a month.
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

**Uses:** [PriceIndex](PriceIndex.md) (62), [DebtManager](DebtManager.md) (24), [Game](Game.md) (18), [ForeignAccounts](ForeignAccounts.md) (17), [Retail](Retail.md) (13), [LongPlaytest](LongPlaytest.md) (12), [WorldEconomy](WorldEconomy.md) (6), [GameFiles](GameFiles.md) (5), [BuildingsTemplate](BuildingsTemplate.md) (2), [TempDirs](TempDirs.md) (1), [Founding](Founding.md) (1), [Bank](Bank.md) (1)

**Used by (3):** [CurveCheck](CurveCheck.md), [ExpectationsCheck](ExpectationsCheck.md), [HouseholdCheck](HouseholdCheck.md)

## Sections

| line | section |
|---:|---|
| 70 | · 1. the basket |
| 94 | · · the high and low water marks |
| 146 | · · five components, the year's weights, chained (0.7.43) |
| 299 | · 2. prices ration |
| 340 | · 3. the world is a real place |
| 373 | · 4. and the rate does something |
| 501 | · 5. in a city, and across a reload |
| 605 | 6. INFLATION FALLS WITH THE RATE (asserted since 0.7.3; measured |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 691 | `MonetaryCheck.HELD_RATES` | `{.03,.10,.20,.40 }` | The policy rates the one founding is held at, from month 25: the three of the baseline, and since 0.7.2 a fourth at 40% - past the old stop of the dial (25% until 0.7.2), the uncapped case. |
| 694 | `MonetaryCheck.MEASURED_MONTHS` | `60` | How long each run is: five years, the last three of them at the held rate. |
| 697 | `MonetaryCheck.HELD_FROM` | `LongPlaytest.HELD_AFTER + 1` | The month the dial is held from: the one after LongPlaytest.HELD_AFTER, the months the held runs start after. |
| 710 | `MonetaryCheck.MEASUREMENT_NOISE` | `.0010` | How far apart two runs of the one founding may read, in inflation a year, when only the dial's timing moves: 0.10 points. |
| 713 | `MonetaryCheck.TRANSMISSION_FLOOR` | `.01` | How much lower inflation must run at a dial of 40% than at 3%, a year: one point - the channel has to be worth a point across the range or it is not a channel. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 46 | `static int fails` |  |
| 47 | `static PrintStream out` |  |
| 48 | `static PrintStream quiet` |  |
| 797 | `static double fewestCrews` | The fewest of its posts the builders offered in any held month of the runs since the last reset (0.7.17). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 44 | 874 | **type** `public class MonetaryCheck` | Money: what a basket costs, what the world charges, and what the rate does. |
| 50 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 55 | 9 | `static void close(String label, double actual, double expected, double tol)` |  |
| 65 | 539 | `public static void main(String[] args) throws Exception` |  |

### 6. INFLATION FALLS WITH THE RATE (asserted since 0.7.3; measured (lines 605-917)

| line | len | member | says |
|---:|---:|---|---|
| 716 | 3 | `static void step(Game g, double heldRate)` | One month, as the playtest steps it: a broke city steps rather than skips. |
| 721 | 6 | `static void step(Game g, double heldRate, int heldFrom)` | ...holding the dial from a month of the caller's: the noise run holds it a month late. |
| 729 | 66 | `static Game founding(Path root, String label)` | The playtest's founding (seed 0), to the month before the dial is held. |
| 800 | 8 | `static double[] fingerprint(Game g)` | What the founding looks like the month before the dial moves - equal across the runs, or they are not one founding. |
| 814 | 17 | `static double heldRun(Path root, String label, double rate, int heldFrom, double[] print, Game[] city)` | One run of the founding at a held rate: the dial held from heldFrom to MEASURED_MONTHS, and inflation over the held months as an annual rate. |
| 832 | 78 | `static void inflationFallsWithTheRate(Path root)` |  |
| 911 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |

