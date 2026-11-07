# PriceLevelCheck.java - 1,093 lines · 42 methods · 19 constants · harnesses

`ham/citybuildersim/PriceLevelCheck.java` - generated 2026-11-07 by CodeMap; line numbers are as of that run.

> A city founded at 1900's prices is today's real economy with every dollar multiplied by one factor, and this is how we know (EP1, Jerus
> 2026-10-30: "1900 price level"; docs/studies/spec-1900-start.md 2).
> 
> WHY. The calendar starts in 1900 with today's technology, and "1900 price level" means every nominal anchor - the world's prices, the wage
> rungs, the building costs, the opening money, every dollar dial - is today's figure times PriceLevel.FACTOR (the US CPI of 1900 over 2025,
> 0.02588) and no ratio, rate, count or physical quantity moves. The anchors are in over a hundred places and a missed one is not an error: it
> is a relative price that is silently off by 38.6 times, so food costs a labourer's day or a house costs a week's pay, and nobody sees it
> until a screen reads strange. Nobody can enumerate this codebase's money by reading it, so the test is not a list of fields: it is TWO CITIES,
> as DenominationCheck's is.
> 
>   A is founded at the factor, as the game is.
>   B is founded at today's prices (-Dcbs.priceLevel=1, PriceLevel's one door).
> 
> If the factor is only a change of price level then A is B with a different label on the axis: every real quantity identical to the person
> and the tonne, every nominal one identical after dividing by the factor, month by month. A missed anchor changes a relative price, a changed
> relative price changes what gets built and who moves in, and the two part company; the harness says which series, and when.
> 
> WHAT THIS PROVES, in sections.
>   1. The series: 126 years, the first year the calendar's, the factor the series' ratio.
>   2. The world file's US dollar, from founding to 2025, times the factor, is 1 (the dollar the world inflates on and the factor that set the
>      opening prices are one dollar).
>   3. Each funnel carries the factor: the six rungs, a good's three prices, the food prices of consumption.json, a building's cost, upkeep and
>      consumables read from the data file, and the one copy of the materials' price.
>   4. The rankings' bridge is the series' ratio, 2015 over the founding year.
>   5. The named literals each carry it, and the cross-file ratios between them and the funnels (the unskilled wage over a food, tuition over a
>      wage, materials over a cash cost, the two land bases, the works' dollars over the cash a point) equal the same ratios of today's figures.
>   6. THE TWIN, which is the finding: the default founding run for TWIN_MONTHS months in a child JVM at the factor and in one at today's prices,
>      one after the other, each printing a month-by-month digest; the parent loads no city.
>   7. The twin again on a built city, BUILT_MONTHS months.
>   8. The twin once more on the PLAYED city - the one MapCheck and the playtest play: a founding on MiningCheck's world, forty houses, three
>      stores and two farms put up, then the playtest's advisor looking every six months, ten houses retired at PLAYED_DEMOLITION and an iron mine
>      bought at PLAYED_IRON, PLAYED_MONTHS months. It found what the first two could not, because it is the one that BUYS GROUND and
>      BORROWS: a proceeds figure rounded to a fixed hundredth of a unit (Game.cents: a thirty-eight times coarser rounding at the factor), and
>      three offers tied at one rate of ground a dollar that the nearer should win, an ulp apart at the factor, so the farther won
>      (LandMarket.RANK_TIE).
>   9. The twin on ScaleCheck's town (the playtest's founding with forty houses, stores and farms, saved and loaded as ScaleCheck loads it),
>      TOWN_MONTHS months. It found what the three before could not, because it is the one that SELLS ABROAD for a year and reads the year back:
>      the history rounded a table's money to a fixed hundredth of a thousand (PartnerTrade.HISTORY_ROUND), a thirty-eight times coarser
>      rounding at the factor, and Game.cityExportPriceLevel() weights the world's prices by those rounded exports, so the second January's
>      terms of trade, and the D$, were another's by month 24.
> 
> The one oracle for "today's figure" is the child at today's prices: a constant that is read there is the figure as the class writes it, so
> nothing in this file copies a literal from the model.

**Uses:** [PriceLevel](PriceLevel.md) (50), [LongPlaytest](LongPlaytest.md) (35), [Game](Game.md) (19), [Good](Good.md) (16), [WorldData](WorldData.md) (13), [PayTier](PayTier.md) (13), [BuildingsTemplate](BuildingsTemplate.md) (10), [RealDollar](RealDollar.md) (8), [Nations](Nations.md) (6), [Consumption](Consumption.md) (6), [GameFiles](GameFiles.md) (6), [LabourMarket](LabourMarket.md) (5), [EducationType](EducationType.md) (5), [BuildingManager](BuildingManager.md) (4), [Education](Education.md) (4), [UtilitiesHandler](UtilitiesHandler.md) (4), [BuildingCatalog](BuildingCatalog.md) (3), [HistorySave](HistorySave.md) (3), [CityCalendar](CityCalendar.md) (2), [WorldMoneys](WorldMoneys.md) (2), [TaxPolicy](TaxPolicy.md) (2), [Rankings](Rankings.md) (2), [Mining](Mining.md) (2), [MiningCheck](MiningCheck.md) (2), [TreasuryFund](TreasuryFund.md) (2), [RunWorks](RunWorks.md) (2), [BridgeWorks](BridgeWorks.md) (2), [Sector](Sector.md) (2), [WorldMarket](WorldMarket.md) (1), [WorldDataCheck](WorldDataCheck.md) (1)... and 7 more

**Used by (12):** [BondCheck](BondCheck.md), [CementConcreteCheck](CementConcreteCheck.md), [CoalMiningCheck](CoalMiningCheck.md), [CopperMiningCheck](CopperMiningCheck.md), [CopperRefiningCheck](CopperRefiningCheck.md), [CreditCheck](CreditCheck.md), [EducationCheck](EducationCheck.md), [ForestryCheck](ForestryCheck.md), [MaterialsCheck](MaterialsCheck.md), [QuarryingCheck](QuarryingCheck.md), [TreasuryCheck](TreasuryCheck.md), [WoodProductsCheck](WoodProductsCheck.md)

## Sections

| line | section |
|---:|---|
| 216 | (untitled) |
| 270 | 1. THE SERIES |
| 295 | 2. THE WORLD FILE |
| 343 | 3. THE FUNNELS |
| 469 | 4. THE RANKINGS' BRIDGE |
| 483 | 5. THE NAMED LITERALS AND THE RATIOS |
| 548 | 6. THE TWIN |
| 666 | (untitled) |
| 748 | THE CHILD: found the default city, run it, print what happened |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 73 | `PriceLevelCheck.TWIN_MONTHS` | `60` | The months each twin runs: five years, enough for the founding's wages, rents, builds, loans, trade and the first inflation to act, short enough for a minute or two a child. |
| 76 | `PriceLevelCheck.BUILT_MONTHS` | `120` | ...and the built city's: ten years, as DenominationCheck's reformed town is run (its fixture reads the loans, the schools and the first replacements). |
| 94 | `PriceLevelCheck.TOWN_MONTHS` | `33` | ...and the town's: ScaleCheck's founded city (its section 1), to month 33 (55 until FC1, 34 until BA1, below) - EP1's gate review found that this town parts from its twin at the factor where the three cities above did... |
| 110 | `PriceLevelCheck.PLAYED_MONTHS` | `103` | ...and the played city's: past its demolition (PLAYED_DEMOLITION), its iron mine (PLAYED_IRON) and the land the advisor keeps buying ahead of the city (month 96 was the first to part), to month 103 (105 until RT1, bel... |
| 113 | `PriceLevelCheck.PLAYED_DEMOLITION` | `60` | The month the played city pulls down ten houses: month 60, as when the horizon was 120 months and the demolition its half. |
| 116 | `PriceLevelCheck.PLAYED_IRON` | `40` | The month the played city is given its iron mine: month 40, as when the horizon was 120 months and the mine its third. |
| 124 | `PriceLevelCheck.US_LEVEL_TOLERANCE` | `.003` | How far the world file's US price level from founding to 2025, times PriceLevel.FACTOR, may be from 1, as a share. |
| 127 | `PriceLevelCheck.STAND_IN_TOLERANCE` | `1e-9` | The stand-in century follows the series to this share: floating point of one logarithm, nothing the model rounds. |
| 138 | `PriceLevelCheck.TWIN_NOMINAL_TOLERANCE` | `1e-9` | How far a nominal series of the factor's city, divided by the factor, may be from the same series in the city at today's prices, as a share of the larger of the two series' largest size so far (a balance is a sum of f... |
| 141 | `PriceLevelCheck.TWIN_REAL_TOLERANCE` | `1e-9` | ...and a real one (a count, a ratio, a quantity in units), the same band: nothing real is rounded differently by a change of price level. |
| 147 | `PriceLevelCheck.TWIN_MONEY_FLOOR` | `1e-9` | A nominal series' value below this, in today's thousands, is zero for the comparison (a millionth of a dollar): the model's own numeric floors are at 1e-9 (OrderBook.DUST, Exchange.MIN_FAIR), and a balance that is rou... |
| 156 | `PriceLevelCheck.HISTORY_SIG_TOLERANCE` | `1.1e-5` | The history records money to a fixed grid (HistorySave.round2, round4, round6) or to six significant figures (roundSig), so a nominal history series of the factor's city, divided by the factor, is on its grid scaled b... |
| 163 | `PriceLevelCheck.TWIN_QUANTITY_FLOOR` | `1e-9` | A real quantity below this, in units, is zero for the comparison: a billionth of a unit, the size of the model's own numeric floors (OrderBook.DUST, Exchange.MIN_FAIR). |
| 170 | `PriceLevelCheck.TIE_FLIPS` | `2` | A real series the history records to a grid may be one step apart in at most this many months: a value that sits on the middle of two steps (0.00175 recorded to four places, the bank's deposit rate at month 0) rounds ... |
| 173 | `PriceLevelCheck.CONSTANT_TOLERANCE` | `1e-12` | The relative gap allowed between a constant that carries the factor and today's figure times it, and between a ratio and today's ratio: one rounding of one product. |
| 176 | `PriceLevelCheck.DOOR` | `"cbs.priceLevel"` | The child property that stands in for the factor (PriceLevel's one door). |
| 488 | `PriceLevelCheck.NOMINAL_CONSTANTS` | `{ "Game.FOUNDING_CASH", "Game.FOUNDING_RESERVE_USD", "Game.FIXED_ISSUE_COST",...` | The nominal amounts struck as constants, each a class and a name: today's figure times the factor, in both children. |
| 640 | `PriceLevelCheck.SIG_RECORDED` | `java.util.regex.Pattern.compile("h\\.(minimumWage\|landPrice\|foodPrice\|materia...` | The history series recorded to six significant figures (HistorySave.roundSig) rather than to a grid: the share prices and the household-scale prices. |
| 676 | `PriceLevelCheck.CHILD_MINUTES` | `10` | How long a child JVM may run before the check kills it and fails: the longest (the played city) takes a few minutes. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 178 | `static int fails` |  |
| 179 | `static PrintStream out` |  |
| 670 | `final char kind` |  |
| 671 | `final double[] v` |  |
| 680 | `double factor` |  |
| 681 | `boolean ok` |  |
| 682 | `final Map<String, Double> consts` |  |
| 683 | `final Map<String, Double> ratios` |  |
| 684 | `final Map<String, Series> series` |  |
| 972 | `final int months` |  |
| 973 | `final Map<String, Character> kinds` |  |
| 974 | `final Map<String, double[]> values` |  |
| 977 | `String prefix` | A prefix for every series name, so a second city's one month rides in the first's digest. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 70 | 1024 | **type** `public class PriceLevelCheck` | A city founded at 1900's prices is today's real economy with every dollar multiplied by one factor, and this is how we know (EP1, Jerus 2026-10-30: "1900 price level"; docs/studies/spec-1900-start.md 2). |
| 181 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 186 | 5 | `static void relative(String label, double actual, double expected, double band)` |  |
| 193 | 7 | `static double gap(double a, double b)` | The relative gap of two figures: \|a - b\| over the larger; 0 for two NaNs, two equal infinities or two zeros, infinite for one NaN. |
| 205 | 5 | `static double[] atFounding(double...today)` | Today's figures priced at the founding's level: each times PriceLevel.FACTOR (EP1). |
| 212 | 3 | `static boolean sameMoney(double a, double b)` | Two money figures built differently (a price and a price times units, against the study's product) are the same to a billionth: floating point only. |

### (untitled) (lines 216-269)

| line | len | member | says |
|---:|---:|---|---|
| 218 | 51 | `public static void main(String[] args) throws Exception` |  |

### 1. THE SERIES (lines 270-294)

| line | len | member | says |
|---:|---:|---|---|
| 274 | 20 | `static void theSeries()` |  |

### 2. THE WORLD FILE (lines 295-342)

| line | len | member | says |
|---:|---:|---|---|
| 299 | 21 | `static void theWorldFile() throws Exception` |  |
| 322 | 20 | `static void usLevels(String scope, WorldData data)` | A Historical city's US price level from its founding (month 0, January 1900) to 2025, against the series. |

### 3. THE FUNNELS (lines 343-468)

| line | len | member | says |
|---:|---:|---|---|
| 347 | 111 | `static void theFunnels() throws Exception` |  |
| 459 | 5 | `static JsonObject readResource(String name) throws Exception` |  |
| 465 | 3 | `static double num(JsonObject o, String key)` |  |

### 4. THE RANKINGS' BRIDGE (lines 469-482)

| line | len | member | says |
|---:|---:|---|---|
| 473 | 9 | `static void theBridge()` |  |

### 5. THE NAMED LITERALS AND THE RATIOS (lines 483-547)

| line | len | member | says |
|---:|---:|---|---|
| 504 | 26 | `static void theLiterals(Child plain, Child today)` |  |
| 531 | 16 | `static void theRatios(Child plain, Child today)` |  |

### 6. THE TWIN (lines 548-665)

| line | len | member | says |
|---:|---:|---|---|
| 552 | 86 | `static void theTwin(Child plain, Child today, int monthsRun)` |  |
| 643 | 4 | `static boolean allZero(double[] v)` |  |
| 649 | 16 | `static double gridOf(double[] v)` | The grid a recorded series sits on, 1, .01, 1e-4 or 1e-6, the coarsest every value is a multiple of; 0 for none (six significant figures) or a series of zeros. |

### (untitled) (lines 666-747)

| line | len | member | says |
|---:|---:|---|---|
| 669 | 5 | **type** `static final class Series` | One series of the digest: a month-by-month list of values and whether it is money (N) or not (R). |
| 672 | 1 | `Series(char kind, double[] v)` _(in PriceLevelCheck.Series)_ |  |
| 679 | 68 | **type** `static final class Child` | What a child printed: its factor, the constants it read, the ratios, and the series. |
| 687 | 3 | `static Child run(boolean todaysPrices, boolean built) throws Exception` _(in PriceLevelCheck.Child)_ | A child JVM started as AllChecks starts a harness, with the one door open or not; one after the other, never at once. |
| 692 | 37 | `static Child run(boolean todaysPrices, String flag) throws Exception` _(in PriceLevelCheck.Child)_ | ...of the scenario the flag names: --twin, --twin-built, --twin-played. |
| 730 | 16 | `void read(String line)` _(in PriceLevelCheck.Child)_ |  |

### THE CHILD: found the default city, run it, print what happened (lines 748-1093)

| line | len | member | says |
|---:|---:|---|---|
| 753 | 216 | **type** `static final class Twin` | The city, run in a JVM of its own. |
| 755 | 41 | `static void run(String flag) throws Exception` _(in PriceLevelCheck.Twin)_ |  |
| 798 | 5 | `static Game foundedCity()` _(in PriceLevelCheck.Twin)_ | The default founding, as a player starts it. |
| 809 | 25 | `static Game builtCity()` _(in PriceLevelCheck.Twin)_ | DenominationCheck's town (its buses variant): the Wealthy treasury and a build list that puts a bit of everything in the city, so the anchors a default founding never reaches (tuition, fees, loans, a mine's ore, a fur... |
| 840 | 10 | `static Game townCity(PrintStream sink, GameFiles files)` _(in PriceLevelCheck.Twin)_ | ScaleCheck's founded town (its section 1): the playtest's founding, forty houses, three stores and two farms at the start, twenty houses after three months, two stores and a depot after seven, to the month ScaleCheck ... |
| 852 | 8 | `static void townMonth(Game g, int m)` _(in PriceLevelCheck.Twin)_ | One month of the town, ScaleCheck's script: month m is the m-th since the founding. |
| 865 | 14 | `static Game playedCity(PrintStream sink)` _(in PriceLevelCheck.Twin)_ | MapCheck's played city (its playedCity()): MiningCheck's iron world, the playtest's autopilot, rollover and fund dial, forty houses, three stores and two farms. |
| 881 | 17 | `static void playedMonth(Game g, int m)` _(in PriceLevelCheck.Twin)_ | One month of it, as MapCheck plays it: the advisor and the schools every sixth month, a demolition at the half, an iron mine a third in. |
| 899 | 7 | `static double constant(String qualified) throws Exception` _(in PriceLevelCheck.Twin)_ |  |
| 907 | 13 | `static void constants(PrintStream o) throws Exception` _(in PriceLevelCheck.Twin)_ |  |
| 922 | 44 | `static void ratios(PrintStream o) throws Exception` _(in PriceLevelCheck.Twin)_ | Ratios between files: dimensionless, so today's and the factor's are one number (study 2.7, item 4). |
| 967 | 1 | `static void q(PrintStream o, String name, double v)` _(in PriceLevelCheck.Twin)_ |  |
| 971 | 122 | **type** `static final class Digest` | The month-by-month record: a name, whether it is money, and a value for every month. |
| 979 | 1 | `Digest(int months)` _(in PriceLevelCheck.Digest)_ |  |
| 981 | 6 | `void put(String name, char kind, int m, double x)` _(in PriceLevelCheck.Digest)_ |  |
| 988 | 32 | `void sample(Game g, int m)` _(in PriceLevelCheck.Digest)_ |  |
| 1026 | 24 | `void history(Game g) throws Exception` _(in PriceLevelCheck.Digest)_ | The history the game keeps, every series of it, classified by the model's own definition of money: the series HistorySave.redenominate() divides are money, but for the four rates it divides by the unit (the D$ per US$... |
| 1051 | 1 | `static boolean hasNumber(List<?> l)` _(in PriceLevelCheck.Digest)_ |  |
| 1053 | 4 | `static boolean isListMap(Field f)` _(in PriceLevelCheck.Digest)_ |  |
| 1059 | 10 | `static boolean halved(List<?> before, List<?> after)` _(in PriceLevelCheck.Digest)_ | Whether redenominating by one half halved the series: at least one nonzero entry, and every nonzero one halved. |
| 1070 | 11 | `void addList(String name, char kind, List<?> l)` _(in PriceLevelCheck.Digest)_ |  |
| 1082 | 10 | `void print(PrintStream o)` _(in PriceLevelCheck.Digest)_ |  |

