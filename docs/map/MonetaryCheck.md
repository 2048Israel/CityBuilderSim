# MonetaryCheck.java - 1,152 lines · 16 methods · 10 constants · harnesses

`ham/citybuildersim/MonetaryCheck.java` - generated 2026-11-23 by CodeMap; line numbers are as of that run.

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
>   5. Does all of it hold in a real city, and survive a reload? (FS10: a city
>      played 132 months for the identity; its three premises - the basket
>      based, the world's prices moved, the currency off its bounds - set on
>      it by PriceIndex.restore(), WorldEconomy.shockLevel() and pinRate().)
> 
>   6. And does INFLATION FALL WITH THE RATE? One founding held at 3%, 10%,
>      20% and 40% from month 25 to 84: each higher dial's inflation no
>      higher than the lower one's, within the noise a month's delay in the
>      hand makes, and the 3% row at least a point above the 40% row. A
>      measurement from 0.6.11 to 0.7.2, the baseline 7.0 had to turn; an
>      assertion since 0.7.3, with the households' saving answering the real
>      deposit rate - and the columns that say which channel carried it.
>      (FS10, MQ1: the D$ held by ForeignAccounts.pinRate() from month 25,
>      so a month's delay in the hand cannot move the currency the rows
>      ride on; read over three whole calendar years, months 49 to 84, the
>      world market's yearly solves in the same months of every run; the
>      builders' crews and the month's delay, in each year and over the
>      three, asserted hard, not waited for.)

**Uses:** [PriceIndex](PriceIndex.md) (73), [DebtManager](DebtManager.md) (24), [Game](Game.md) (22), [ForeignAccounts](ForeignAccounts.md) (18), [Retail](Retail.md) (13), [LongPlaytest](LongPlaytest.md) (12), [CityCalendar](CityCalendar.md) (9), [WorldEconomy](WorldEconomy.md) (6), [PriceLevel](PriceLevel.md) (5), [GameFiles](GameFiles.md) (5), [BuildingsTemplate](BuildingsTemplate.md) (2), [BuildingManager](BuildingManager.md) (1), [TempDirs](TempDirs.md) (1), [Founding](Founding.md) (1), [HousingMarket](HousingMarket.md) (1), [HousingChoice](HousingChoice.md) (1), [Sectors](Sectors.md) (1), [WorldMarket](WorldMarket.md) (1), [Bank](Bank.md) (1)

**Used by (3):** [CurveCheck](CurveCheck.md), [ExpectationsCheck](ExpectationsCheck.md), [HouseholdCheck](HouseholdCheck.md)

## Sections

| line | section |
|---:|---|
| 84 | · 1. the basket |
| 108 | · · the high and low water marks |
| 160 | · · five components, the year's weights, chained (0.7.43) |
| 313 | · 2. prices ration |
| 354 | · 3. the world is a real place |
| 387 | · 4. and the rate does something |
| 515 | · 5. in a city, and across a reload |
| 640 | 6. INFLATION FALLS WITH THE RATE (asserted since 0.7.3; measured |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 762 | `MonetaryCheck.HELD_RATES` | `{.03,.10,.20,.40 }` | The policy rates the one founding is held at, from month 25: the three of the baseline, and since 0.7.2 a fourth at 40% - past the old stop of the dial (25% until 0.7.2), the uncapped case. |
| 765 | `MonetaryCheck.MEASURED_MONTHS` | `84` | The month each run ends at, and its window with it: the December of the seventh year, the month before the next January's solve of the world's prices (asserted against the calendar). |
| 768 | `MonetaryCheck.WINDOW_YEARS` | `3` | The whole calendar years the rows are read over: three, the 36 months the section read before FS10, each from its January's solve to its December. |
| 776 | `MonetaryCheck.HELD_FROM` | `LongPlaytest.HELD_AFTER + 1` | The month the dial is held from: the one after LongPlaytest.HELD_AFTER, the months the held runs start after. |
| 789 | `MonetaryCheck.MEASUREMENT_NOISE` | `.0065` | How far apart two runs of the one founding may read when only the dial's timing moves: 0.65 points a year, about twice CB2's largest reading on RT2 main (0.9.113), holding each dial a month late over the same window. |
| 792 | `MonetaryCheck.TRANSMISSION_FLOOR` | `.01` | How much lower inflation must run at a dial of 40% than at 3%, a year: one point - the channel has to be worth a point across the range or it is not a channel. |
| 1118 | `MonetaryCheck.WORLD_LEVEL_SET` | `1.5` | The world's price level section 5's fixture sets (WorldEconomy.shockLevel()): half again the founding's 1.0, far past the one point its premise asks. |
| 1121 | `MonetaryCheck.PINNED_RATE` | `1.0` | The rate section 5's fixture pins the currency at (ForeignAccounts.pinRate()): one D$ to the US$, a long way from either bound. |
| 1124 | `MonetaryCheck.FIXTURE_SHELF_RISE` | `.01` | What the fixture's shelf rises a month, a fraction: a point, so the index it strikes is not 1.000 and its year of readings reads a rate. |
| 1127 | `MonetaryCheck.FIXTURE_RENT_RISE` | `.005` | ...and what its rent rises a month: half of that, so the two components part. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 55 | `static int fails` |  |
| 56 | `static PrintStream out` |  |
| 57 | `static PrintStream quiet` |  |
| 913 | `static double fewestCrews` | The fewest of its posts the builders offered in any held month of the runs since the last reset (0.7.17). |
| 926 | `static boolean solvesOnTheCalendar` | Whether, in every month of every run's window so far (heldRun()), the world market had solved in that month's own calendar year: its yearly repricings fall on each January of the window, the same months in every run, ... |
| 929 | `static boolean basedBeforeWindow` | Whether the basket was based before the window opened in every run so far (heldRun()): an index not yet based reads 0% over it. |
| 932 | `static double heldAt` | The rate every run's D$ is pinned at (heldRun()): the founding's in the month the dial is first held, NaN until the first run sets it. |
| 935 | `static boolean currencyHeld` | Whether every run's D$ stood at heldAt, pinned, in every month from the hold to the window's close (heldRun()). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 53 | 1100 | **type** `public class MonetaryCheck` | Money: what a basket costs, what the world charges, and what the rate does. |
| 59 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 64 | 9 | `static void close(String label, double actual, double expected, double tol)` |  |
| 74 | 565 | `public static void main(String[] args) throws Exception` |  |

### 6. INFLATION FALLS WITH THE RATE (asserted since 0.7.3; measured (lines 640-1152)

| line | len | member | says |
|---:|---:|---|---|
| 771 | 3 | `static int windowMonths()` | The months a window is, from its first January to its last December: the years before the last whole, and the eleven of the last after its January's own solve, from the model's calendar (CityCalendar.monthOfYear(MEASU... |
| 795 | 3 | `static void step(Game g, double heldRate)` | One month, as the playtest steps it: a broke city steps rather than skips. |
| 800 | 6 | `static void step(Game g, double heldRate, int heldFrom)` | ...holding the dial from a month of the caller's: the noise run holds it a month late. |
| 808 | 3 | `static Game founding(Path root, String label)` | The playtest's founding (seed 0), to the month before the dial is held. |
| 819 | 3 | `static Game founding(Path root, String label, boolean onLiterals)` | ...on the goods' literals where asked (Game.playOnLiterals()); no section here asks since WN3-R. |
| 827 | 3 | `static Game founding(Path root, String label, boolean onLiterals, String...held)` | ...with the sectors named held out of the investor's building (BusinessInvestment.holdSector()), as the harnesses that read a steady bank hold the one whose plant on a bank loan the bank cannot carry (WN3 held Cement ... |
| 835 | 76 | `static Game founding(Path root, String label, boolean onLiterals, boolean causesHeldOff, String...held)` | ...with the causes of 1900 held off from the first month where asked (R0b call 7): only heldRun() asks, the three harnesses that borrow this founding (CurveCheck, ExpectationsCheck, HouseholdCheck) keep the deaths of ... |
| 916 | 8 | `static double[] fingerprint(Game g)` | What the founding looks like the month before the dial moves - equal across the runs, or they are not one founding. |
| 938 | 10 | `static void reportHeldRent(Game g, String label)` | Read the rent counter-channel without changing any paid lease, quote, policy input or assertion. |
| 957 | 56 | `static double heldRun(Path root, String label, double rate, int heldFrom, double[] print, Game[] city, double[] years)` | One run of the founding at a held rate: the dial held from heldFrom, the D$ pinned (ForeignAccounts.pinRate()) at the founding's rate in its own last month, HELD_FROM, on time and late alike, and inflation over the wi... |
| 1014 | 102 | `static void inflationFallsWithTheRate(Path root)` |  |
| 1135 | 10 | `static PriceIndex basedBasket(int shoppingMonths)` | FS10: a price index put through the model's own door, PriceIndex.takeMonth(): a shelf and a rent, each month with spending on both in it (the households' 40 on food and 60 on rent, the weights the basket is struck on)... |
| 1146 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |

