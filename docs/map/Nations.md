# Nations.java - 772 lines · 44 methods · 42 constants · model

`ham/citybuildersim/Nations.java` - generated 2026-10-31 by CodeMap; line numbers are as of that run.

> The nations beyond the city, each on the world's path: its year - output,
> people, prices and its money's rate to the US dollar - from the path
> (Historical or Random), the seed and the world file (WN1; spec-nations 7).
> 
> WHY THIS EXISTS. The world outside was a price list fixed at founding, and
> the only thing out there that moved, the dollar's inflation, was drawn from
> the month alone, so every city met the same world (spec-nations 1.4).
> Jerus: "perhaps a seed for the path? one being historical and the other
> being random". Now the world has a path, picked at founding (D9).
> 
> A NATION'S YEAR IS A PURE FUNCTION OF (path, seed, year) (spec 7.3): every
> draw is new Random(scramble(seed, nation, year, what)), so nothing about the
> nations is saved but the path and the seed, and asking 2150 before 2030
> gives what asking in order gives. The path is worked year by year for all
> the nations at once - the frontier, the world's factor and the moneys'
> anchors are shared - eagerly to 2100 (the UN's last year) and on demand past
> it.
> 
> HISTORICAL replays the file's real years from January 2000, each series to
> its last published year, and then the game's own rule with seeded shocks of
> HISTORICAL_SHOCK_SHARE the size and no crises. RANDOM starts from the file's
> January 2000 and draws its own path. The rule is the same in both: growth a
> head in logs (so output is never negative) is a trend that falls as a nation
> nears the frontier, a world factor, its region's, its own persistent shock
> and now and then a crisis; its prices are drawn around its income group's
> inflation; its money follows relative parity plus a shock, or its peg.
> Past 2100 a Historical nation's growth eases to LONG_RUN_GROWTH (the
> leaderboard's D8; the nations study's D14) and people are held.
> 
> The map draws it (WN2) and the world market trades on it (WN3). It copies
> what it needs from the file and keeps no reference to it.

**Uses:** [WorldData](WorldData.md) (14), [World](World.md) (4), [CityCalendar](CityCalendar.md) (2)

**Used by (20):** [DataSave](DataSave.md), [Founding](Founding.md), [FoundingScreen](FoundingScreen.md), [Game](Game.md), [GlobeCheck](GlobeCheck.md), [HistorySave](HistorySave.md), [LongPlaytest](LongPlaytest.md), [MapView](MapView.md), [NationsCheck](NationsCheck.md), [NationsMap](NationsMap.md), [PartnerTrade](PartnerTrade.md), [RealDollarCheck](RealDollarCheck.md), [SaveFileCheck](SaveFileCheck.md), [VaultMoneysCheck](VaultMoneysCheck.md), [WorldEconomy](WorldEconomy.md), [WorldMarket](WorldMarket.md), [WorldMarketCheck](WorldMarketCheck.md), [WorldMoneys](WorldMoneys.md), [WorldMoneysCheck](WorldMoneysCheck.md), [YearBook](YearBook.md)

## Sections

| line | section |
|---:|---|
| 65 | THE DIALS - each with its source: [R..] is the spec's list, (est.) the game's estimate |
| 184 | THE RECORDS |
| 211 | THE NATIONS - copied from the file when this is made |
| 410 | WHAT A CALLER ASKS |
| 528 | THE FILE'S FIGURES - gaps filled, the years before the first, the stand-ins |
| 598 | THE DRAWS |
| 614 | THE YEAR - people, output, prices, moneys, goods, in that order |

## Enum constants

| line | constant | says |
|---:|---|---|
| 51 | `Nations.Path.HISTORICAL` |  |
| 51 | `Nations.Path.RANDOM` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 60 | `Nations.DEFAULT_PATH` | `Path.RANDOM` | The path a founding takes when nobody picks one, on the Earth and on a generated world alike: Random (WN3; Jerus, 2026-10-29: "A new world defaults to the Random path instead of Historical"). |
| 63 | `Nations.DEFAULT_SEED` | `48_213L` | The path's seed when nobody rolls one: 48,213, the founding board's (spec-nations, board 4), so every harness and the playtest meet one world. |
| 70 | `Nations.FIRST_YEAR` | `CityCalendar.EPOCH_YEAR` | The year a path begins in, January of it: the city's calendar's (CityCalendar.EPOCH_YEAR). |
| 73 | `Nations.HISTORY_LAST_YEAR` | `2025` | The last year the file's history runs to (output, prices, rates): Historical replays it to here at most, and the game's own rule goes on [R11]. |
| 76 | `Nations.PEOPLE_LAST_YEAR` | `2100` | The last year of the UN's people, the medium variant [R13]; past it people are held and a Historical nation's growth eases. |
| 79 | `Nations.FRONTIER` | `"USA"` | The nation whose output a head is the frontier the others converge to: the United States [R11]. |
| 82 | `Nations.FRONTIER_GROWTH` | `.015` | The frontier's growth a head a year (est.: the US's grew about 1.3% a year 2000-2024 [R11], about 2% over the long run). |
| 85 | `Nations.CONVERGENCE` | `.02` | The share of the gap to the frontier (in logs) a nation closes a year: Barro and Sala-i-Martin's 2% [R29]. |
| 88 | `Nations.CATCH_UP_MAX` | `.02` | The most the catch-up adds to the trend (est.: 2% of the whole gap would give the poorest about 10% a year; the typical developing nation grew about two points faster than the US in 2000-2019 [R11]). |
| 91 | `Nations.SD_RICH` | `.018` | One standard deviation of a rich nation's yearly growth: the G7's, 2000-2019 [R11]. |
| 94 | `Nations.SD_EMERGING` | `.027` | One standard deviation of an emerging economy's yearly growth: fifteen large ones, 2000-2019 [R11]. |
| 97 | `Nations.EMERGING_GAP` | `1.7` | The gap to the frontier, in logs, at which a nation swings like an emerging economy (est.: the G7's output a head is about 5.5 times those fifteen's). |
| 100 | `Nations.SD_MAX` | `.045` | The most a year's whole swing is, one standard deviation, however poor the nation (est.). |
| 103 | `Nations.WORLD_SHARE_RICH` | `.27` | The share of a rich nation's output swings the world factor explains: 27% in the industrial nations, about 11% across 106 [R30]. |
| 106 | `Nations.WORLD_SD` | `SD_RICH * Math.sqrt(WORLD_SHARE_RICH)` | The world factor's size, one standard deviation: that share of a rich nation's variance, so a fixed size that is 12% of an emerging one's [R30]. |
| 109 | `Nations.REGION_SHARE` | `.10` | The share of an emerging economy's variance a region's factor is (est., after [R30]). |
| 112 | `Nations.REGION_SD` | `SD_EMERGING * Math.sqrt(REGION_SHARE)` | A region's factor's size, one standard deviation, drawn per region and year. |
| 115 | `Nations.OWN_PERSISTENCE` | `.3` | The share of this year's own shock that carries into the next: 0.3 (spec-nations 7.2). |
| 118 | `Nations.CRISIS_RATE` | `.017` | The chance a year a nation not in a crisis starts one: Barro's falls of 15% or more in output a head, 1.7% of country-years over a century [R32]. |
| 121 | `Nations.CRISIS_DEPTH_MIN` | `.15` | The shallowest a crisis is, the share of output a head lost over its years: Barro's threshold [R32]. |
| 124 | `Nations.CRISIS_DEPTH_MAX` | `.30` | The deepest a crisis is (est.: Barro and Ursua's mean fall about 22%). |
| 127 | `Nations.CRISIS_YEARS` | `3` | The years a crisis runs, the fall spread evenly over them (est.: "over a few years" [R32]). |
| 130 | `Nations.HISTORICAL_SHOCK_SHARE` | `.5` | The size of a Historical nation's shocks after its data, against Random's: world, region and own (est.: spec 7.1, "sized as Random's but smaller"); it draws no crisis. |
| 133 | `Nations.LONG_RUN_GROWTH` | `.01` | The growth a head a Historical nation eases to past 2100 (the leaderboard's D8; the nations study's D14: about what the world has sustained over the long run). |
| 136 | `Nations.EASE_YEARS` | `50` | The years past 2100 a Historical nation's growth takes to ease from its 2091-2100 average to LONG_RUN_GROWTH. |
| 139 | `Nations.LONG_RUN_WINDOW` | `10` | The years of growth a head, to 2100, the ease starts from (the leaderboard's D8). |
| 142 | `Nations.NO_DATA_SHARE` | `.1` | The share of the frontier's output a head a nation with no output figure at all starts from (est.). |
| 145 | `Nations.LOW_INCOME_MAX` | `890` | A nation below this output a head (US$ of 2015) is low income (est.: the World Bank's FY2026 US$1,135 of 2024 over the US GDP deflator since 2015). |
| 148 | `Nations.LOWER_MIDDLE_MAX` | `3_510` | A nation below this is lower middle income (est.: the World Bank's FY2026 US$4,495 of 2024, deflated to 2015). |
| 151 | `Nations.UPPER_MIDDLE_MAX` | `10_890` | A nation below this is upper middle income, above it high (est.: the World Bank's FY2026 US$13,935 of 2024, deflated to 2015). |
| 154 | `Nations.INFLATION_MEAN` | `{.02,.035,.055,.065 }` | A year's mean inflation by income group, high, upper middle, lower middle, low (est., after the WDI's medians 2000-2019 [R11]). |
| 157 | `Nations.INFLATION_SD` | `{.012,.025,.035,.045 }` | One standard deviation of a year's inflation innovation by income group, in the same order (est., after the WDI's medians 2000-2019 [R11]). |
| 160 | `Nations.INFLATION_PERSISTENCE` | `.6` | The share of this year's inflation, above or below its group's mean, that carries into the next (est., after [R11]). |
| 163 | `Nations.INFLATION_FLOOR` | `-.05` | The lowest a drawn year's inflation is, a shade of deflation (est.); a published year is the file's whatever it was. |
| 166 | `Nations.DOLLAR` | `"USD"` | The US dollar's code: its rate is 1 always, and a money's rate is against it. |
| 169 | `Nations.RATE_SD` | `.10` | A floating money's yearly change against the dollar beyond relative parity, one standard deviation (est.). |
| 172 | `Nations.PRICE_PERSISTENCE` | `.7` | The share of a good's price shock that carries into the next year: 0.7 (spec-nations 7.2). |
| 175 | `Nations.MIN_PRICE_SWING` | `.03` | The least a good's yearly price swing, one standard deviation of its log change, is taken to be (est.: manufactured goods move little). |
| 178 | `Nations.DEFAULT_REGION` | `"East Asia & Pacific"` | The region of a nation the file gives none (Taiwan, which WDI lacks): East Asia & Pacific's. |
| 181 | `Nations.WORLD` | `1, REGION = 2, OWN = 3, CRISIS = 4, CRISIS_DEPTH = 5, INFLATION = 6, RATE = 7...` | The kinds of draw, and where a draw that belongs to no nation sits among the nations' numbers. |
| 182 | `Nations.WORLD_NATION` | `0, REGION_BASE = 1000, GOOD_BASE = 2000` |  |
| 206 | `Nations.NO_DRAWS` | `new Draws(0, 0, 0, 0, false)` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 215 | `private final Path path` |  |
| 216 | `private final long seed` |  |
| 217 | `private final boolean historical` |  |
| 218 | `private final int n` |  |
| 219 | `private final String[] iso, names, regime, regionName, moneyLater` |  |
| 220 | `private final String[][] moneyDec` |  |
| 221 | `private final int[] regionOf` |  |
| 222 | `private final List<String> regions` |  |
| 223 | `private final int frontier` |  |
| 224 | `private final Series[] people, output, cpi, rate` |  |
| 225 | `private final int[] outLast, cpiLast, rateLast` |  |
| 226 | `private final Map<String, Integer> anchor` |  |
| 228 | `private final String[] pathMoneys` | WN4: every money the path can name, sorted by code, and for a code that is no later money its anchor (0-based): the nation of largest 2025 output that was ever on it. |
| 229 | `private final Map<String, Integer> historyAnchor` |  |
| 230 | `private final String[] goodKey` |  |
| 231 | `private final Series[] price` |  |
| 232 | `private final int[] priceLast` |  |
| 233 | `private final double[] swing` |  |
| 240 | `private final int[] shockOf` | Whose shock each good's price draws after the file's years, by good number: the first good that follows its Pink Sheet series (WN3). |
| 243 | `private final List<Year[]> years` | THE PATH SO FAR, a year at a time: the records, what was drawn, each money's rate and each good's price. |
| 244 | `private final List<Draws[]> drawn` |  |
| 245 | `private final List<Map<String, Double>> moneyLevels` |  |
| 246 | `private final List<double[]> goodPrices` |  |
| 247 | `private final List<int[]> goodMarks` |  |
| 248 | `private double[] perHead, priceLevel, inflationState, own, crisisAmount, longRun` |  |
| 249 | `private int[] crisisLeft` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 48 | 725 | **type** `public final class Nations` | The nations beyond the city, each on the world's path: its year - output, people, prices and its money's rate to the US dollar - from the path (Historical or Random), the seed and the world file (WN1; spec-nations 7). |
| 51 | 1 | **type** `public enum Path` | The world's path, picked at founding (D9): the real years from January 2000, or a path drawn from the seed. |

### THE DIALS - each with its source: [R..] is the spec's list, (est.) the game's estimate (lines 65-183)

### THE RECORDS (lines 184-210)

| line | len | member | says |
|---:|---:|---|---|
| 194 | 5 | **type** `public record Year(int year, double output, double people, double priceLevel, double inflation, double rate...` | A nation's year. |
| 197 | 1 | `public double outputPerHead()` _(in Nations.Year)_ | Output a head, US$ of 2015. |
| 201 | 1 | **type** `record Draws(double world, double region, double own, double crisis, boolean crisisStarts)` | The terms of a year's growth a head the path drew, in logs (0 where it draws none): the world's, the region's, the nation's own (the persistent state, not its innovation), the crisis's share of the year, and whether a... |
| 204 | 1 | **type** `interface Absent` | Says a figure of the file is not there, for a harness that must cause a missing figure: the series ("people", "gdp_2015usd", "cpi" or "rate"), the nation's iso3 and the year. |
| 204 | 1 | `boolean is(String series, String iso3, int year)` _(in Nations.Absent)_ |  |
| 209 | 1 | **type** `record Series(double[] v, int[] mark, int last)` | A series by year index from 2000 with no holes up to last: the file's figures, the gaps and the years before the first filled in. |

### THE NATIONS - copied from the file when this is made (lines 211-409)

| line | len | member | says |
|---:|---:|---|---|
| 252 | 3 | `public Nations(WorldData file, Path path, long seed)` | The nations on this path and seed, from the file. |
| 257 | 3 | `Nations(WorldData file, Path path, long seed, Map<String, String> regimes)` | As above, but regimes (iso3 to float, peg:CODE or uses:CODE) stand in for the file's: a harness's, the fixture having no peg to cause. |
| 261 | 148 | `Nations(WorldData file, Path path, long seed, Map<String, String> regimes, Absent absent)` |  |

### WHAT A CALLER ASKS (lines 410-527)

| line | len | member | says |
|---:|---:|---|---|
| 415 | 1 | `public Path path()` | The path this world is on. |
| 418 | 1 | `public long seed()` | The seed that draws it. |
| 421 | 1 | `public int count()` | How many nations there are (the file's N: 196 in the full file). |
| 424 | 1 | `public String iso3(int nation)` | A nation's ISO 3166 alpha-3 code; nations are numbered 1..count() as WorldData numbers them. |
| 427 | 1 | `public String name(int nation)` | A nation's name. |
| 430 | 4 | `public int index(String iso3)` | The number of the nation with that code, 1..count(); -1 when there is none. |
| 436 | 4 | `public synchronized Year year(int nation, int year)` | A nation's year, for any year from FIRST_YEAR on: a pure function of the path, the seed and the year. |
| 442 | 4 | `public synchronized double growth(int nation, int year)` | A nation's growth of output in a year, in logs; NaN in FIRST_YEAR, which has no year before it. |
| 448 | 1 | `public int goodCount()` | How many traded goods have a price path (the file's prices layer; 0 for a file without it). |
| 451 | 1 | `public String goodKey(int good)` | The key of good 1..goodCount() (the file's goods layer: the Good constant's name). |
| 454 | 4 | `public synchronized double goodPrice(int good, int year)` | A good's real price in a year, a ratio to 2025's (so 2025 is 1 on Historical): the file's to 2025 on Historical, the path's own AR(1) around 1 after it and on Random. |
| 460 | 4 | `public synchronized int goodPriceMark(int good, int year)` | The mark of that price: the file's where it is the file's, else 1. |
| 466 | 1 | `public static int yearOf(int gameMonth)` | The year of the city's calendar a game month is in (CityCalendar.yearOf), so a caller asks for the city's year. |
| 469 | 4 | `synchronized Draws draws(int nation, int year)` | What the year drew for a nation, for the harness. |
| 475 | 1 | `synchronized boolean inCrisis(int nation, int year)` | Whether a nation is in a crisis in a year (the year it starts included). |
| 478 | 1 | `String region(int nation)` | A nation's region, the World Bank's (the file's; East Asia & Pacific for the one it gives none). |
| 481 | 1 | `String regime(int nation)` | A nation's regime in the file (or the harness's): "float", "peg:CODE" or "uses:CODE". |
| 484 | 1 | `boolean usedAfterHistory(String code)` | Whether any nation is on this money after the history (Historical: in 2025, Random: from 2001): the later moneys, the ones a path goes on ruling. |
| 487 | 1 | `String[] pathMoneys()` | Every money the path can name, sorted by code (WN4): Random's are each nation's money of 2000 and its later one, Historical's every money of the file's history and the 2025 one. |
| 493 | 5 | `int anchorNation(String code)` | The nation whose price level is a money's, 1-based, 0 for a code the path cannot name: a later money's anchor (the largest 2025 output using it, not a uses: nation), else, for a money of the history only (the lira, th... |
| 500 | 1 | `double priceSwing(int good)` | A good's yearly price swing, one standard deviation of its file's log change 2001-2025, never under MIN_PRICE_SWING. |
| 503 | 3 | `static int incomeGroup(double outputPerHead)` | The income group of a nation by its output a head in US$ of 2015: 0 high, 1 upper middle, 2 lower middle, 3 low. |
| 507 | 4 | `private int check(int nation)` |  |
| 512 | 4 | `private int checkGood(int good)` |  |
| 518 | 5 | `private int reach(int year)` | The year's index, the path worked out to it. |
| 524 | 3 | `private void ensure(int year)` |  |

### THE FILE'S FIGURES - gaps filled, the years before the first, the stand-ins (lines 528-597)

| line | len | member | says |
|---:|---:|---|---|
| 533 | 4 | `private static double rawFigure(WorldData file, String series, int number, int year, Absent gone)` | The file's figure for a nation and year, NaN where it has none or the harness says it is absent. |
| 539 | 9 | `private static Series fromFile(WorldData file, String series, int number, int cap, Absent gone)` | A series of the file for a nation, indexed from 2000 and filled to its last figure at or before cap; null when it has none. |
| 554 | 29 | `static Series filled(double[] raw, int[] mark, int cap)` | A series from its figures, a NaN being none: the figures up to cap kept with their marks, the years before the first taking the first figure and a gap between two figures filled log-linearly between them, both marked ... |
| 585 | 3 | `private static Series standIn(double value)` | A series that is one figure, the game's estimate, in 2000: the rule works the rest. |
| 590 | 7 | `private static double swingOf(Series s)` | One standard deviation of a good's yearly log change over its file's years 2001 on, never under MIN_PRICE_SWING. |

### THE DRAWS (lines 598-613)

| line | len | member | says |
|---:|---:|---|---|
| 603 | 6 | `static long scramble(long seed, long nation, long year, long what)` | A draw's seed: the path's seed, the nation (a region's, a good's, 0 for the world's), the year and what is drawn, chained through SplitMix64. |
| 610 | 1 | `private double gauss(int what, long nation, int year)` |  |
| 612 | 1 | `private double unit(int what, long nation, int year)` |  |

### THE YEAR - people, output, prices, moneys, goods, in that order (lines 614-772)

| line | len | member | says |
|---:|---:|---|---|
| 618 | 102 | `private void step(int t)` |  |
| 722 | 4 | `private String moneyOf(int i, int t)` | The money a nation is in in a year: Historical's is the file's of December to 2025 and its 2025 one after; Random's is its money of 2000, or the one its regime uses from 2001. |
| 734 | 26 | `private double moneyLevel(String code, int t, int year, double share, Map<String, Double> cur, Set<String> busy, double[] infla...` | A money's rate to the dollar this year, NaN for one no nation uses after the history. |
| 762 | 5 | `private double levelAt(String code, int t)` | A money's rate in an earlier year of the path; NaN where it had none. |
| 769 | 3 | `private double held(String code, int t)` | A money's rate last year; NaN where it had none. |

