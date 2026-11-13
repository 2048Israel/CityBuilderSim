# MonetaryCheck.java - 1,005 lines · 14 methods · 8 constants · harnesses

`ham/citybuildersim/MonetaryCheck.java` - generated 2026-11-13 by CodeMap; line numbers are as of that run.

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

**Uses:** [PriceIndex](PriceIndex.md) (64), [DebtManager](DebtManager.md) (24), [Game](Game.md) (22), [ForeignAccounts](ForeignAccounts.md) (17), [Retail](Retail.md) (13), [LongPlaytest](LongPlaytest.md) (12), [WorldEconomy](WorldEconomy.md) (6), [PriceLevel](PriceLevel.md) (5), [GameFiles](GameFiles.md) (5), [Observation](Observation.md) (2), [BuildingsTemplate](BuildingsTemplate.md) (2), [BuildingManager](BuildingManager.md) (1), [TempDirs](TempDirs.md) (1), [Founding](Founding.md) (1), [Sectors](Sectors.md) (1), [Bank](Bank.md) (1)

**Used by (3):** [CurveCheck](CurveCheck.md), [ExpectationsCheck](ExpectationsCheck.md), [HouseholdCheck](HouseholdCheck.md)

## Sections

| line | section |
|---:|---|
| 75 | · 1. the basket |
| 99 | · · the high and low water marks |
| 151 | · · five components, the year's weights, chained (0.7.43) |
| 304 | · 2. prices ration |
| 345 | · 3. the world is a real place |
| 378 | · 4. and the rate does something |
| 506 | · 5. in a city, and across a reload |
| 616 | 6. INFLATION FALLS WITH THE RATE (asserted since 0.7.3; measured |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 702 | `MonetaryCheck.HELD_RATES` | `{.03,.10,.20,.40 }` | The policy rates the one founding is held at, from month 25: the three of the baseline, and since 0.7.2 a fourth at 40% - past the old stop of the dial (25% until 0.7.2), the uncapped case. |
| 705 | `MonetaryCheck.MEASURED_MONTHS` | `60` | How long each run is: five years, the last three of them at the held rate. |
| 708 | `MonetaryCheck.HELD_FROM` | `LongPlaytest.HELD_AFTER + 1` | The month the dial is held from: the one after LongPlaytest.HELD_AFTER, the months the held runs start after. |
| 721 | `MonetaryCheck.MEASUREMENT_NOISE` | `.0010` | How far apart two runs of the one founding may read, in inflation a year, when only the dial's timing moves: 0.10 points. |
| 724 | `MonetaryCheck.TRANSMISSION_FLOOR` | `.01` | How much lower inflation must run at a dial of 40% than at 3%, a year: one point - the channel has to be worth a point across the range or it is not a channel. |
| 844 | `MonetaryCheck.SETTLED_STEP` | `.01` | How far the groceries component of the price index may move in a month and still be settled, a fraction: a point. |
| 847 | `MonetaryCheck.SETTLED_MONTHS` | `12` | How many months running the shelf must have moved no more than SETTLED_STEP for the town to count as settled: a year (six months settled it at month 53, the rows 12.17 / 9.27 / 8.20 / 7.85% in order, but the month-lat... |
| 850 | `MonetaryCheck.MAX_SETTLE_MONTHS` | `60` | The most months the founding is played on to settle, so a town that never does is read as it stands. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 46 | `static int fails` |  |
| 47 | `static PrintStream out` |  |
| 48 | `static PrintStream quiet` |  |
| 853 | `static int settledAfter` | The months the last heldRun() played its founding on to settle (settle()): the log's figure. |
| 876 | `static double fewestCrews` | The fewest of its posts the builders offered in any held month of the runs since the last reset (0.7.17). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 44 | 962 | **type** `public class MonetaryCheck` | Money: what a basket costs, what the world charges, and what the rate does. |
| 50 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 55 | 9 | `static void close(String label, double actual, double expected, double tol)` |  |
| 65 | 550 | `public static void main(String[] args) throws Exception` |  |

### 6. INFLATION FALLS WITH THE RATE (asserted since 0.7.3; measured (lines 616-1005)

| line | len | member | says |
|---:|---:|---|---|
| 727 | 3 | `static void step(Game g, double heldRate)` | One month, as the playtest steps it: a broke city steps rather than skips. |
| 732 | 6 | `static void step(Game g, double heldRate, int heldFrom)` | ...holding the dial from a month of the caller's: the noise run holds it a month late. |
| 740 | 3 | `static Game founding(Path root, String label)` | The playtest's founding (seed 0), to the month before the dial is held. |
| 751 | 3 | `static Game founding(Path root, String label, boolean onLiterals)` | ...on the goods' literals where asked (Game.playOnLiterals()); no section here asks since WN3-R. |
| 759 | 3 | `static Game founding(Path root, String label, boolean onLiterals, String...held)` | ...with the sectors named held out of the investor's building (BusinessInvestment.holdSector()), as the harnesses that read a steady bank hold the one whose plant on a bank loan the bank cannot carry (WN3 held Cement ... |
| 767 | 75 | `static Game founding(Path root, String label, boolean onLiterals, boolean causesHeldOff, String...held)` | ...with the causes of 1900 held off from the first month where asked (R0b call 7): only heldRun() asks, the three harnesses that borrow this founding (CurveCheck, ExpectationsCheck, HouseholdCheck) keep the deaths of ... |
| 862 | 12 | `static int settle(Game g)` | GW1: plays the founding on at its own dial, a month at a time, until the basket is based and the groceries shelf has moved by no more than SETTLED_STEP a month for SETTLED_MONTHS running, at most MAX_SETTLE_MONTHS; re... |
| 879 | 8 | `static double[] fingerprint(Game g)` | What the founding looks like the month before the dial moves - equal across the runs, or they are not one founding. |
| 893 | 22 | `static double heldRun(Path root, String label, double rate, int heldFrom, double[] print, Game[] city)` | One run of the founding at a held rate: the dial held from heldFrom to MEASURED_MONTHS, and inflation over the held months as an annual rate. |
| 916 | 82 | `static void inflationFallsWithTheRate(Path root)` |  |
| 999 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |

