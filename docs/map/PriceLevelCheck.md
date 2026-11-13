# PriceLevelCheck.java - 1,343 lines · 53 methods · 19 constants · harnesses

`ham/citybuildersim/PriceLevelCheck.java` - generated 2026-11-13 by CodeMap; line numbers are as of that run.

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
>  10. THE SCREENS' CUT-OFFS (EP2), at the factor and at today's prices, over the default founding's two children, the built city's and the town's: the partners given
>      a lane (TradeLanes.of over the feed, at minLane()), the holdings folded and shown (FundView.FOLD_UNDER) and every live nominal line
>      ui.Money.something() shows are the same every month, a lane is drawn somewhere, and the lines the old half-thousand would have hidden
>      are there at the factor and none at today's prices. Section 8 is left out: its twin parts on series of its own (main's record).
> 
> The one oracle for "today's figure" is the child at today's prices: a constant that is read there is the figure as the class writes it, so
> nothing in this file copies a literal from the model.

**Uses:** [PriceLevel](PriceLevel.md) (51), [LongPlaytest](LongPlaytest.md) (35), [Game](Game.md) (21), [Good](Good.md) (16), [WorldData](WorldData.md) (15), [PayTier](PayTier.md) (14), [BuildingsTemplate](BuildingsTemplate.md) (10), [RealDollar](RealDollar.md) (8), [LabourMarket](LabourMarket.md) (7), [NationsMap](NationsMap.md) (7), [Nations](Nations.md) (6), [Consumption](Consumption.md) (6), [GameFiles](GameFiles.md) (6), [EducationType](EducationType.md) (5), [TradeLanes](TradeLanes.md) (5), [BuildingManager](BuildingManager.md) (4), [Education](Education.md) (4), [UtilitiesHandler](UtilitiesHandler.md) (4), [BuildingCatalog](BuildingCatalog.md) (3), [HistorySave](HistorySave.md) (3), [FundView](FundView.md) (3), [CityCalendar](CityCalendar.md) (2), [WorldMoneys](WorldMoneys.md) (2), [TaxPolicy](TaxPolicy.md) (2), [Money](Money.md) (2), [Rankings](Rankings.md) (2), [Mining](Mining.md) (2), [MiningCheck](MiningCheck.md) (2), [TreasuryFund](TreasuryFund.md) (2), [RunWorks](RunWorks.md) (2)... and 13 more

**Used by (12):** [BondCheck](BondCheck.md), [CementConcreteCheck](CementConcreteCheck.md), [CoalMiningCheck](CoalMiningCheck.md), [CopperMiningCheck](CopperMiningCheck.md), [CopperRefiningCheck](CopperRefiningCheck.md), [CreditCheck](CreditCheck.md), [EducationCheck](EducationCheck.md), [ForestryCheck](ForestryCheck.md), [MaterialsCheck](MaterialsCheck.md), [QuarryingCheck](QuarryingCheck.md), [TreasuryCheck](TreasuryCheck.md), [WoodProductsCheck](WoodProductsCheck.md)

## Sections

| line | section |
|---:|---|
| 192 | (untitled) |
| 248 | 1. THE SERIES |
| 273 | 2. THE WORLD FILE |
| 321 | 3. THE FUNNELS |
| 450 | 4. THE RANKINGS' BRIDGE |
| 464 | 5. THE NAMED LITERALS AND THE RATIOS |
| 530 | 6. THE TWIN |
| 691 | 10. THE SCREENS' CUT-OFFS |
| 793 | (untitled) |
| 888 | THE CHILD: found the default city, run it, print what happened |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 77 | `PriceLevelCheck.TWIN_MONTHS` | `60` | The months each twin runs: five years, enough for the founding's wages, rents, builds, loans, trade and the first inflation to act, short enough for a minute or two a child. |
| 80 | `PriceLevelCheck.BUILT_MONTHS` | `120` | ...and the built city's: ten years, as DenominationCheck's reformed town is run (its fixture reads the loans, the schools and the first replacements). |
| 83 | `PriceLevelCheck.TOWN_MONTHS` | `120, PLAYED_MONTHS = 120` | Town and played-city maximum horizons; their nudge twins derive the comparable prefix (PO1). |
| 86 | `PriceLevelCheck.PLAYED_DEMOLITION` | `60` | The month the played city pulls down ten houses: month 60, as when the horizon was 120 months and the demolition its half. |
| 89 | `PriceLevelCheck.PLAYED_IRON` | `40` | The month the played city is given its iron mine: month 40, as when the horizon was 120 months and the mine its third. |
| 97 | `PriceLevelCheck.US_LEVEL_TOLERANCE` | `.003` | How far the world file's US price level from founding to 2025, times PriceLevel.FACTOR, may be from 1, as a share. |
| 100 | `PriceLevelCheck.STAND_IN_TOLERANCE` | `1e-9` | The stand-in century follows the series to this share: floating point of one logarithm, nothing the model rounds. |
| 111 | `PriceLevelCheck.TWIN_NOMINAL_TOLERANCE` | `1e-9` | How far a nominal series of the factor's city, divided by the factor, may be from the same series in the city at today's prices, as a share of the larger of the two series' largest size so far (a balance is a sum of f... |
| 114 | `PriceLevelCheck.TWIN_REAL_TOLERANCE` | `1e-9` | ...and a real one (a count, a ratio, a quantity in units), the same band: nothing real is rounded differently by a change of price level. |
| 120 | `PriceLevelCheck.TWIN_MONEY_FLOOR` | `1e-9` | A nominal series' value below this, in today's thousands, is zero for the comparison (a millionth of a dollar): the model's own numeric floors are at 1e-9 (OrderBook.DUST, Exchange.MIN_FAIR), and a balance that is rou... |
| 129 | `PriceLevelCheck.HISTORY_SIG_TOLERANCE` | `1.1e-5` | The history records money to a fixed grid (HistorySave.round2, round4, round6) or to six significant figures (roundSig), so a nominal history series of the factor's city, divided by the factor, is on its grid scaled b... |
| 136 | `PriceLevelCheck.TWIN_QUANTITY_FLOOR` | `1e-9` | A real quantity below this, in units, is zero for the comparison: a billionth of a unit, the size of the model's own numeric floors (OrderBook.DUST, Exchange.MIN_FAIR). |
| 143 | `PriceLevelCheck.TIE_FLIPS` | `2` | A real series the history records to a grid may be one step apart in at most this many months: a value that sits on the middle of two steps (0.00175 recorded to four places, the bank's deposit rate at month 0) rounds ... |
| 146 | `PriceLevelCheck.CONSTANT_TOLERANCE` | `1e-12` | The relative gap allowed between a constant that carries the factor and today's figure times it, and between a ratio and today's ratio: one rounding of one product. |
| 149 | `PriceLevelCheck.OLD_CUT` | `.5` | The literal half-thousand the screens held before EP2 as their "nothing" (ui.Money, FundView's fold), in thousands as written: the figures between it and the factor's own cut-off are the ones the old cut-off would hav... |
| 152 | `PriceLevelCheck.DOOR` | `"cbs.priceLevel"` | The child property that stands in for the factor (PriceLevel's one door). |
| 469 | `PriceLevelCheck.NOMINAL_CONSTANTS` | `{ "Game.FOUNDING_CASH", "Game.FOUNDING_RESERVE_USD", "Game.FIXED_ISSUE_COST",...` | The nominal amounts struck as constants, each a class and a name: today's figure times the factor, in both children. |
| 665 | `PriceLevelCheck.SIG_RECORDED` | `java.util.regex.Pattern.compile("h\\.(minimumWage\|landPrice\|foodPrice\|materia...` | The history series recorded to six significant figures (HistorySave.roundSig) rather than to a grid: the share prices and the household-scale prices. |
| 803 | `PriceLevelCheck.CHILD_MINUTES` | `10` | How long a child JVM may run before the check kills it and fails: the longest (the played city) takes a few minutes. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 154 | `static int fails` |  |
| 155 | `static PrintStream out` |  |
| 797 | `final char kind` |  |
| 798 | `final double[] v` |  |
| 807 | `double factor` |  |
| 808 | `boolean ok` |  |
| 809 | `final Map<String, Double> consts` |  |
| 810 | `final Map<String, Double> ratios` |  |
| 811 | `final Map<String, Series> series` |  |
| 813 | `final Map<String, double[]> counts` | What the screens' cut-offs did each month (EP2, the K lines: Counts); kept apart from the series so that theTwin never sees them. |
| 1116 | `final int months` |  |
| 1117 | `final Map<String, Character> kinds` |  |
| 1118 | `final Map<String, double[]> values` |  |
| 1121 | `String prefix` | A prefix for every series name, so a second city's one month rides in the first's digest. |
| 1244 | `final int months` |  |
| 1245 | `final Map<String, double[]> values` |  |
| 1246 | `NationsMap map` |  |
| 1247 | `boolean mapTried` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 74 | 1270 | **type** `public class PriceLevelCheck` | A city founded at 1900's prices is today's real economy with every dollar multiplied by one factor, and this is how we know (EP1, Jerus 2026-10-30: "1900 price level"; docs/studies/spec-1900-start.md 2). |
| 157 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 162 | 5 | `static void relative(String label, double actual, double expected, double band)` |  |
| 169 | 7 | `static double gap(double a, double b)` | The relative gap of two figures: \|a - b\| over the larger; 0 for two NaNs, two equal infinities or two zeros, infinite for one NaN. |
| 181 | 5 | `static double[] atFounding(double...today)` | Today's figures priced at the founding's level: each times PriceLevel.FACTOR (EP1). |
| 188 | 3 | `static boolean sameMoney(double a, double b)` | Two money figures built differently (a price and a price times units, against the study's product) are the same to a billionth: floating point only. |

### (untitled) (lines 192-247)

| line | len | member | says |
|---:|---:|---|---|
| 194 | 53 | `public static void main(String[] args) throws Exception` |  |

### 1. THE SERIES (lines 248-272)

| line | len | member | says |
|---:|---:|---|---|
| 252 | 20 | `static void theSeries()` |  |

### 2. THE WORLD FILE (lines 273-320)

| line | len | member | says |
|---:|---:|---|---|
| 277 | 21 | `static void theWorldFile() throws Exception` |  |
| 300 | 20 | `static void usLevels(String scope, WorldData data)` | A Historical city's US price level from its founding (month 0, January 1900) to 2025, against the series. |

### 3. THE FUNNELS (lines 321-449)

| line | len | member | says |
|---:|---:|---|---|
| 325 | 114 | `static void theFunnels() throws Exception` |  |
| 440 | 5 | `static JsonObject readResource(String name) throws Exception` |  |
| 446 | 3 | `static double num(JsonObject o, String key)` |  |

### 4. THE RANKINGS' BRIDGE (lines 450-463)

| line | len | member | says |
|---:|---:|---|---|
| 454 | 9 | `static void theBridge()` |  |

### 5. THE NAMED LITERALS AND THE RATIOS (lines 464-529)

| line | len | member | says |
|---:|---:|---|---|
| 486 | 26 | `static void theLiterals(Child plain, Child today)` |  |
| 513 | 16 | `static void theRatios(Child plain, Child today)` |  |

### 6. THE TWIN (lines 530-690)

| line | len | member | says |
|---:|---:|---|---|
| 535 | 16 | `static void observedTwin(Child plain, Child today, int months, String flag) throws Exception` | Compare every law up to the first same-level nudge that changes the city's path. |
| 553 | 23 | `static int stableMonths(Child base, Child nudged, int months)` | The month before a nudge changes a live series beyond the existing comparison tolerance. |
| 577 | 86 | `static void theTwin(Child plain, Child today, int monthsRun)` |  |
| 668 | 4 | `static boolean allZero(double[] v)` |  |
| 674 | 16 | `static double gridOf(double[] v)` | The grid a recorded series sits on, 1, .01, 1e-4 or 1e-6, the coarsest every value is a multiple of; 0 for none (six significant figures) or a series of zeros. |

### 10. THE SCREENS' CUT-OFFS (lines 691-792)

| line | len | member | says |
|---:|---:|---|---|
| 705 | 72 | `static void theCutOffs(Child plain, Child today, Child builtPlain, Child builtToday, Child townPlain, Child townToday)` | What the screens do at a dollar cut-off (EP2: ui.Money.NOTHING says nothing of a figure under it, FundView.FOLD_UNDER folds a holding under it into "and N more", TradeLanes.minLane() gives a partner under it no lane) ... |
| 782 | 10 | `static int unequal(double[] x, double[] y, List<String> first)` | How many months two rows part (a row of a different length parts in every month it does not share), and the first few as lines. |

### (untitled) (lines 793-887)

| line | len | member | says |
|---:|---:|---|---|
| 796 | 5 | **type** `static final class Series` | One series of the digest: a month-by-month list of values and whether it is money (N) or not (R). |
| 799 | 1 | `Series(char kind, double[] v)` _(in PriceLevelCheck.Series)_ |  |
| 806 | 81 | **type** `static final class Child` | What a child printed: its factor, the constants it read, the ratios, and the series. |
| 816 | 3 | `static Child run(boolean todaysPrices, boolean built) throws Exception` _(in PriceLevelCheck.Child)_ | A child JVM started as AllChecks starts a harness, with the one door open or not; one after the other, never at once. |
| 821 | 3 | `static Child run(boolean todaysPrices, String flag) throws Exception` _(in PriceLevelCheck.Child)_ | ...of the scenario the flag names: --twin, --twin-built, --twin-played. |
| 825 | 38 | `static Child run(boolean todaysPrices, String flag, boolean nudge) throws Exception` _(in PriceLevelCheck.Child)_ |  |
| 864 | 22 | `void read(String line)` _(in PriceLevelCheck.Child)_ |  |

### THE CHILD: found the default city, run it, print what happened (lines 888-1343)

| line | len | member | says |
|---:|---:|---|---|
| 893 | 220 | **type** `static final class Twin` | The city, run in a JVM of its own. |
| 895 | 45 | `static void run(String flag) throws Exception` _(in PriceLevelCheck.Twin)_ |  |
| 942 | 5 | `static Game foundedCity()` _(in PriceLevelCheck.Twin)_ | The default founding, as a player starts it. |
| 953 | 25 | `static Game builtCity()` _(in PriceLevelCheck.Twin)_ | DenominationCheck's town (its buses variant): the Wealthy treasury and a build list that puts a bit of everything in the city, so the anchors a default founding never reaches (tuition, fees, loans, a mine's ore, a fur... |
| 984 | 10 | `static Game townCity(PrintStream sink, GameFiles files)` _(in PriceLevelCheck.Twin)_ | ScaleCheck's founded town (its section 1): the playtest's founding, forty houses, three stores and two farms at the start, twenty houses after three months, two stores and a depot after seven, to the month ScaleCheck ... |
| 996 | 8 | `static void townMonth(Game g, int m)` _(in PriceLevelCheck.Twin)_ | One month of the town, ScaleCheck's script: month m is the m-th since the founding. |
| 1009 | 14 | `static Game playedCity(PrintStream sink)` _(in PriceLevelCheck.Twin)_ | MapCheck's played city (its playedCity()): MiningCheck's iron world, the playtest's autopilot, rollover and fund dial, forty houses, three stores and two farms. |
| 1025 | 17 | `static void playedMonth(Game g, int m)` _(in PriceLevelCheck.Twin)_ | One month of it, as MapCheck plays it: the advisor and the schools every sixth month, a demolition at the half, an iron mine a third in. |
| 1043 | 7 | `static double constant(String qualified) throws Exception` _(in PriceLevelCheck.Twin)_ |  |
| 1051 | 13 | `static void constants(PrintStream o) throws Exception` _(in PriceLevelCheck.Twin)_ |  |
| 1066 | 44 | `static void ratios(PrintStream o) throws Exception` _(in PriceLevelCheck.Twin)_ | Ratios between files: dimensionless, so today's and the factor's are one number (study 2.7, item 4). |
| 1111 | 1 | `static void q(PrintStream o, String name, double v)` _(in PriceLevelCheck.Twin)_ |  |
| 1115 | 122 | **type** `static final class Digest` | The month-by-month record: a name, whether it is money, and a value for every month. |
| 1123 | 1 | `Digest(int months)` _(in PriceLevelCheck.Digest)_ |  |
| 1125 | 6 | `void put(String name, char kind, int m, double x)` _(in PriceLevelCheck.Digest)_ |  |
| 1132 | 32 | `void sample(Game g, int m)` _(in PriceLevelCheck.Digest)_ |  |
| 1170 | 24 | `void history(Game g) throws Exception` _(in PriceLevelCheck.Digest)_ | The history the game keeps, every series of it, classified by the model's own definition of money: the series HistorySave.redenominate() divides are money, but for the four rates it divides by the unit (the D$ per US$... |
| 1195 | 1 | `static boolean hasNumber(List<?> l)` _(in PriceLevelCheck.Digest)_ |  |
| 1197 | 4 | `static boolean isListMap(Field f)` _(in PriceLevelCheck.Digest)_ |  |
| 1203 | 10 | `static boolean halved(List<?> before, List<?> after)` _(in PriceLevelCheck.Digest)_ | Whether redenominating by one half halved the series: at least one nonzero entry, and every nonzero one halved. |
| 1214 | 11 | `void addList(String name, char kind, List<?> l)` _(in PriceLevelCheck.Digest)_ |  |
| 1226 | 10 | `void print(PrintStream o)` _(in PriceLevelCheck.Digest)_ |  |
| 1243 | 100 | **type** `static final class Counts` | What the screens' cut-offs did to the city each month (EP2): counts the model's own code gives, a row of one per month for each, printed as K lines for section 10. |
| 1249 | 1 | `Counts(int months)` _(in PriceLevelCheck.Counts)_ |  |
| 1251 | 1 | `void put(String name, int m, double x)` _(in PriceLevelCheck.Counts)_ |  |
| 1254 | 14 | `NationsMap map()` _(in PriceLevelCheck.Counts)_ | The nations' map of the world file, built once the first time it is wanted (as MapView builds it); null where there is no file or it will not open. |
| 1269 | 22 | `void sample(Game g, Digest d, int m)` _(in PriceLevelCheck.Counts)_ |  |
| 1298 | 33 | `void lanes(Game g, int m)` _(in PriceLevelCheck.Counts)_ | The partners given a lane this month, built as ui/MapView.lanesNow() builds them: TradeLanes.of over the feed's exports and imports by nation and its minLane(), at the city's place (the Earth's key where the map draws... |
| 1332 | 10 | `void print(PrintStream o)` _(in PriceLevelCheck.Counts)_ |  |

