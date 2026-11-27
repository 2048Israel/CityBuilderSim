# MonetaryCheck.java - 1,456 lines · 29 methods · 14 constants · harnesses

`ham/citybuildersim/MonetaryCheck.java` - generated 2026-11-27 by CodeMap; line numbers are as of that run.

> Money: what a basket costs, what the world charges, and what the rate does.
> 
> WHAT IS ACTUALLY BEING ASKED
> 
>   1. Does the price index measure what households BUY, on a basket fixed at a
>      base period? A CPI that re-weights as spending shifts shows no inflation
>      for a family that switched to cheaper food while eating worse.
>      Nine components use actual trailing reference spending,
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
>   6. Does the rate lower inflation overall, while higher financing costs
>      can delay housing starts and raise the same class's rents later?
>      Preserve the original founding's 3%-to-40% effect of at least one
>      point over months 49-84. Measure the policy slope and the timing of
>      the paired estimate across the fixed world-path sample, rather than make
>      a local supply inversion a universal adjacent-rate law. Across that
>      sample, require at least a point of non-shelter disinflation on the
>      same physical basket, and the saving return reaching each live plan.
>      Jerus,
>      2026-11-23: "sometimes its as simple as check reality"; sources and
>      the revised law's authority are in docs/queue/RES3-notes.md.
>      (FS10, MQ1: the D$ held by ForeignAccounts.pinRate() from month 25,
>      so a month's delay in the hand cannot move the currency the rows
>      ride on; read over three whole calendar years, months 49 to 84, the
>      world market's yearly solves in the same months of every run; the
>      builders' crews and the month's delay, in each year and over the
>      three, asserted hard, not waited for.)

**Uses:** [PriceIndex](PriceIndex.md) (89), [Game](Game.md) (24), [DebtManager](DebtManager.md) (24), [ForeignAccounts](ForeignAccounts.md) (18), [Retail](Retail.md) (15), [LongPlaytest](LongPlaytest.md) (12), [CityCalendar](CityCalendar.md) (9), [Founding](Founding.md) (8), [GameFiles](GameFiles.md) (7), [WorldEconomy](WorldEconomy.md) (6), [PriceLevel](PriceLevel.md) (5), [BuildingsTemplate](BuildingsTemplate.md) (3), [HousingInventory](HousingInventory.md) (2), [Investor](Investor.md) (1), [HousingForms](HousingForms.md) (1), [BuildingManager](BuildingManager.md) (1), [TempDirs](TempDirs.md) (1), [CpiCoverageFinding](CpiCoverageFinding.md) (1), [HousingMarket](HousingMarket.md) (1), [HousingChoice](HousingChoice.md) (1), [Sectors](Sectors.md) (1), [WorldMarket](WorldMarket.md) (1), [Bank](Bank.md) (1)

**Used by (3):** [CurveCheck](CurveCheck.md), [ExpectationsCheck](ExpectationsCheck.md), [HouseholdCheck](HouseholdCheck.md)

## Sections

| line | section |
|---:|---|
| 191 | · 1. the basket |
| 215 | · · the high and low water marks |
| 267 | · · nine components, the year's weights, chained (0.7.43) |
| 423 | · 2. prices ration |
| 464 | · 3. the world is a real place |
| 497 | · 4. and the rate does something |
| 625 | · 5. in a city, and across a reload |
| 752 | 6. INFLATION FALLS WITH THE RATE (asserted since 0.7.3; measured |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 60 | `MonetaryCheck.POLICY_SEEDS` | `java.util.stream.LongStream.range(16, 32).toArray()` | Independent validation paths; timing limits were calibrated on the default town and paths 0-15. |
| 875 | `MonetaryCheck.HELD_RATES` | `{.03,.10,.20,.40 }` | The policy rates the one founding is held at, from month 25: the three of the baseline, and since 0.7.2 a fourth at 40% - past the old stop of the dial (25% until 0.7.2), the uncapped case. |
| 878 | `MonetaryCheck.MEASURED_MONTHS` | `84` | The month each run ends at, and its window with it: the December of the seventh year, the month before the next January's solve of the world's prices (asserted against the calendar). |
| 881 | `MonetaryCheck.WINDOW_YEARS` | `3` | The whole calendar years the rows are read over: three, the 36 months the section read before FS10, each from its January's solve to its December. |
| 889 | `MonetaryCheck.HELD_FROM` | `LongPlaytest.HELD_AFTER + 1` | The month the dial is held from: the one after LongPlaytest.HELD_AFTER, the months the held runs start after. |
| 904 | `MonetaryCheck.ORDERING_RESOLUTION` | `.0065` | The original 0.65-point ordering resolution. |
| 907 | `MonetaryCheck.TIMING_LIMIT` | `.0075` | Frozen before validation: twice 0.356534 points on default/path 0-15, rounded up to a quarter point. |
| 909 | `MonetaryCheck.ANNUAL_TIMING_LIMIT` | `.02` | Frozen before validation: twice the 0.961207-point pooled annual response, rounded up to a quarter point. |
| 912 | `MonetaryCheck.TRANSMISSION_FLOOR` | `.01` | Minimum annual effect: a point on the original town's CPI; a flat demand channel fails (non-shelter prices: the ★ law, inflationFallsWithTheRate()). |
| 915 | `MonetaryCheck.NON_SHELTER_AGREEING` | `14` | Of the sixteen declared towns, the fewest whose common-basket non-shelter effect is positive (Call B, 2026-11-25); each of the rest names its shortage. |
| 1422 | `MonetaryCheck.WORLD_LEVEL_SET` | `1.5` | The world's price level section 5's fixture sets (WorldEconomy.shockLevel()): half again the founding's 1.0, far past the one point its premise asks. |
| 1425 | `MonetaryCheck.PINNED_RATE` | `1.0` | The rate section 5's fixture pins the currency at (ForeignAccounts.pinRate()): one D$ to the US$, a long way from either bound. |
| 1428 | `MonetaryCheck.FIXTURE_SHELF_RISE` | `.01` | What the fixture's shelf rises a month, a fraction: a point, so the index it strikes is not 1.000 and its year of readings reads a rate. |
| 1431 | `MonetaryCheck.FIXTURE_RENT_RISE` | `.005` | ...and what its rent rises a month: half of that, so the two components part. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 61 | `private static long policySeed` |  |
| 66 | `private final java.util.List<HousingStart> starts` |  |
| 67 | `private double laterRent` |  |
| 68 | `private int laterSamples` |  |
| 69 | `private double laterPaidRent` |  |
| 70 | `private int laterStockMonths, laterTradeMonths` |  |
| 162 | `static int fails` |  |
| 163 | `static PrintStream out` |  |
| 164 | `static PrintStream quiet` |  |
| 1039 | `static double fewestCrews` | The fewest of its posts the builders offered in any held month of the runs since the last reset (0.7.17). |
| 1052 | `static boolean solvesOnTheCalendar` | Whether, in every month of every run's window so far (heldRun()), the world market had solved in that month's own calendar year: its yearly repricings fall on each January of the window, the same months in every run, ... |
| 1055 | `static boolean basedBeforeWindow` | Whether the basket was based before the window opened in every run so far (heldRun()): an index not yet based reads 0% over it. |
| 1058 | `static double heldAt` | The rate every run's D$ is pinned at (heldRun()): the founding's in the month the dial is first held, NaN until the first run sets it. |
| 1061 | `static boolean currencyHeld` | Whether every run's D$ stood at heldAt, pinned, in every month from the hold to the window's close (heldRun()). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 57 | 1400 | **type** `public class MonetaryCheck` | Money: what a basket costs, what the world charges, and what the rate does. |
| 62 | 1 | **type** `private record HousingStart(int month, long doors)` |  |
| 63 | 1 | **type** `private record PaidSupply(double rentPerDoor, int stockMonths, int tradeMonths)` |  |
| 65 | 42 | **type** `private static final class HousingTown extends Game` | Harness-only observation at acceptance; no model flow or saved order state is reconstructed. |
| 71 | 1 | `HousingTown(GameFiles files, Founding founding)` _(in MonetaryCheck.HousingTown)_ |  |
| 72 | 6 | `public boolean buildFor(Investor payer, BuildingsTemplate template, long quantity)` _(in MonetaryCheck.HousingTown)_ |  |
| 78 | 4 | `long earlyStarts()` _(in MonetaryCheck.HousingTown)_ |  |
| 82 | 19 | `void observeSupply()` _(in MonetaryCheck.HousingTown)_ |  |
| 101 | 1 | `double laterRent()` _(in MonetaryCheck.HousingTown)_ |  |
| 102 | 4 | `PaidSupply paidSupply()` _(in MonetaryCheck.HousingTown)_ |  |
| 108 | 25 | **type** `private record BasketPrices(double[] quantities, double[] prices, int linked, double index)` | Flatten the services sub-basket too: changing fee shares must not masquerade as a price response. |
| 109 | 17 | `static BasketPrices read(PriceIndex p)` _(in MonetaryCheck.BasketPrices)_ |  |
| 126 | 6 | `double cost(double[] q, boolean withoutRent)` _(in MonetaryCheck.BasketPrices)_ |  |
| 138 | 14 | **type** `private record PriceWindow(BasketPrices from, BasketPrices to, double spendFactor, double expectedInflation...` | One run's window: its two baskets, its mean spending plan, the window's mean expected inflation (Expectations.getExpectedInflation(), what the shelf's floor and the fees are struck at), the shelf's floor at either end... |
| 140 | 3 | `double nonShelterInflation(double[] quantities)` _(in MonetaryCheck.PriceWindow)_ |  |
| 144 | 7 | `double nonShelterAtFloor(double[] quantities)` _(in MonetaryCheck.PriceWindow)_ | ...with the groceries priced at the shelf's floor at either end: the same basket with the shortage mark-up taken out. |
| 152 | 4 | **type** `private record PolicyComparison(long seed, double[] inflation, double[][] years, double[] late, double[][] ...` |  |
| 158 | 3 | `static boolean shortOfGroceries(ham.citybuildersim.sectors.Retail shops)` | A grocery shortage this month: the shelf's target at the shortage mark-up's cap, or the sale clearing past it (Retail.clearsPastTheCap()). |
| 166 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 171 | 9 | `static void close(String label, double actual, double expected, double tol)` |  |
| 181 | 570 | `public static void main(String[] args) throws Exception` |  |

### 6. INFLATION FALLS WITH THE RATE (asserted since 0.7.3; measured (lines 752-1456)

| line | len | member | says |
|---:|---:|---|---|
| 884 | 3 | `static int windowMonths()` | The months a window is, from its first January to its last December: the years before the last whole, and the eleven of the last after its January's own solve, from the model's calendar (CityCalendar.monthOfYear(MEASU... |
| 918 | 3 | `static void step(Game g, double heldRate)` | One month, as the playtest steps it: a broke city steps rather than skips. |
| 923 | 7 | `static void step(Game g, double heldRate, int heldFrom)` | ...holding the dial from a month of the caller's: the paired timing run holds it a month late. |
| 932 | 3 | `static Game founding(Path root, String label)` | The playtest's default founding, to the month before the dial is held. |
| 943 | 3 | `static Game founding(Path root, String label, boolean onLiterals)` | ...on the goods' literals where asked (Game.playOnLiterals()); no section here asks since WN3-R. |
| 951 | 3 | `static Game founding(Path root, String label, boolean onLiterals, String...held)` | ...with the sectors named held out of the investor's building (BusinessInvestment.holdSector()), as the harnesses that read a steady bank hold the one whose plant on a bank loan the bank cannot carry (WN3 held Cement ... |
| 959 | 78 | `static Game founding(Path root, String label, boolean onLiterals, boolean causesHeldOff, String...held)` | ...with the causes of 1900 held off from the first month where asked (R0b call 7): only heldRun() asks, the three harnesses that borrow this founding (CurveCheck, ExpectationsCheck, HouseholdCheck) keep the deaths of ... |
| 1042 | 8 | `static double[] fingerprint(Game g)` | What the founding looks like the month before the dial moves - equal across the runs, or they are not one founding. |
| 1064 | 10 | `static void reportHeldRent(Game g, String label)` | Read the rent counter-channel without changing any paid lease, quote, policy input or assertion. |
| 1083 | 3 | `static double heldRun(Path root, String label, double rate, int heldFrom, double[] print, Game[] city, double[] years)` | One run of the founding at a held rate: the dial held from heldFrom, the D$ pinned (ForeignAccounts.pinRate()) at the founding's rate in its own last month, HELD_FROM, on time and late alike, and inflation over the wi... |
| 1087 | 79 | `private static double heldRun(Path root, String label, double rate, int heldFrom, double[] print, Game[] city, double[] years, ...` |  |
| 1167 | 130 | `static void inflationFallsWithTheRate(Path root)` |  |
| 1298 | 122 | `private static PolicyComparison policyComparison(Path root, long seed)` |  |
| 1439 | 10 | `static PriceIndex basedBasket(int shoppingMonths)` | FS10: a price index put through the model's own door, PriceIndex.takeMonth(): a shelf and a rent, each month with spending on both in it (the households' 40 on food and 60 on rent, the weights the basket is struck on)... |
| 1450 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |

