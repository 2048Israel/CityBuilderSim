# GoldPrice.java - 332 lines · 22 methods · 18 constants · model

`ham/citybuildersim/GoldPrice.java` - generated 2026-11-10 by CodeMap; line numbers are as of that run.

> Gold's price in US dollars a fine troy ounce, a month at a time (batch GD1; spec-gold-money 4.1): the statute's par to March 1933, a ten-month path
> to the new par, that par to July 1971, then on Historical the USGS's annual price to December 2025, and an AR(1) in gold's real price where the
> table ends (on Random from August 1971). A pure model: it reads the nations' US price level, the seed and the month, saves nothing, and nothing
> in the economy reads it yet (GD2 hands it to the vault).
> 
> WHY THIS EXISTS. The game had no gold anywhere. GD2 to GD4 need its price: the vault's ninth reserve, the gold standard the city can hold its
> money to, the mines that make it. A price a month is wanted for any year the game can reach, 1900 to 2400, on both paths. It is a pure function of
> the path, the seed and the month, like WorldMoneys' draws (every draw is new Random(Nations.scramble(seed, who, t, what))), so nothing is saved
> and a reload continues the path; the walk is remembered only so that a month is not walked twice, and asking 2400 first gives what asking in
> order gives.
> 
> THE SOURCES. The Gold Standard Act, 14 March 1900, 31 Stat. 45 (public domain): the dollar of 25.8 grains nine-tenths fine, so 23.22 grains of
> fine gold. The Gold Reserve Act, 30 January 1934, 48 Stat. 337, and Proclamation 2072, 31 January 1934, 48 Stat. 1730 (public domain): the
> dollar at 35 to the ounce. The table gold-usd-annual.csv: the USGS's Data Series 140 (1971-2022) and Mineral Commodity Summaries 2026 (2023-2025),
> public domain / CC0. The innovation's sigma is measured from the World Bank's monthly gold (the Pink Sheet) 1973-2024, 624 changes, and ships as a
> number only: the series is the LBMA's and is not shipped.
> 
> Months are Nations' and WorldMoneys' index: t = 0 is January of Nations.FIRST_YEAR (1900).

**Uses:** [Nations](Nations.md) (18), [PriceLevel](PriceLevel.md) (2), [WorldMoneys](WorldMoneys.md) (1)

**Used by (7):** [Game](Game.md), [GoldPriceCheck](GoldPriceCheck.md), [GoldStandardCheck](GoldStandardCheck.md), [Inbox](Inbox.md), [PegAnchorCheck](PegAnchorCheck.md), [PolicyScreen](PolicyScreen.md), [VaultMoneysCheck](VaultMoneysCheck.md)

## Sections

| line | section |
|---:|---|
| 33 | THE DIALS - each with its source: (est.) is the game's estimate |
| 91 | THE OFFICIAL PATH - the statute's price, the same on both paths |
| 114 | THE TABLE - the USGS's annual price, read once |
| 216 | THE RULE - ln of the real price an AR(1) toward the last twenty years' mean, its drift zero (est.) |
| 304 | WHAT A CALLER READS |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 38 | `GoldPrice.PAR_1900` | `480.0 / 23.22` | US$ an ounce from 1900: the Gold Standard Act's dollar of 25.8 grains nine-tenths fine is 23.22 grains fine, so 480 grains an ounce over 23.22 (20.6718). |
| 41 | `GoldPrice.PAR_1934` | `35.0` | US$ an ounce under Proclamation 2072, from 31 January 1934 to 15 August 1971. |
| 44 | `GoldPrice.FLOAT_START` | `(1933 - Nations.FIRST_YEAR) * 12 + 3` | The first month off the old par: April 1933, when the dollar floated (from October the RFC bought gold at prices raised by steps). |
| 47 | `GoldPrice.FLOAT_STEPS` | `10` | The months of the geometric path from the old par to the new, April 1933 to January 1934 (est.): month FLOAT_START + k - 1 is PAR_1900 x (PAR_1934 / PAR_1900)^(k / FLOAT_STEPS), k = 1..FLOAT_STEPS. |
| 50 | `GoldPrice.LAST_PAR_MONTH` | `(1971 - Nations.FIRST_YEAR) * 12 + 6` | The last month at PAR_1934: July 1971 (the gold window closed 15 August). |
| 53 | `GoldPrice.TABLE_FIRST_YEAR` | `1971` | The first year of the table. |
| 56 | `GoldPrice.TABLE_LAST_YEAR` | `Nations.HISTORY_LAST_YEAR` | The last year of the table, the last of the nations' history (2025's price is the USGS's estimate from January to November). |
| 59 | `GoldPrice.GRAMS_PER_TROY_OUNCE` | `31.1034768` | Grams in a troy ounce, exact by the 1959 international agreement (a metric ton is 1e6 g over it). |
| 62 | `GoldPrice.SIGMA` | `.04749` | The monthly innovation of ln gold's real price, one sd: 4.749%, measured from the World Bank's monthly gold 1973-2024 (624 changes). |
| 65 | `GoldPrice.HALF_LIFE_MONTHS` | `120` | The half-life of gold's real price toward its mean in months: ten years (est.: it wanders for decades, and a pull is needed only so a 2400 city has a finite price). |
| 68 | `GoldPrice.PHI` | `StrictMath.pow(.5, 1.0 / HALF_LIFE_MONTHS)` | The monthly persistence of the AR(1): 0.5 to the 1 over the half-life. |
| 71 | `GoldPrice.MEAN_YEARS` | `20` | The years the target is the mean real price of, counted back from the table's last (2006 to 2025). |
| 74 | `GoldPrice.RANDOM_RULE_START` | `LAST_PAR_MONTH + 1` | The first month the rule rules on Random: August 1971, the month after the last at PAR_1934. |
| 77 | `GoldPrice.HISTORICAL_RULE_START` | `WorldMoneys.HISTORICAL_RULE_START` | The first month the rule rules on Historical: January 2026, after the table (WorldMoneys' too). |
| 80 | `GoldPrice.RESOURCE` | `"/gold-usd-annual.csv"` | Where the table is, on the classpath. |
| 83 | `GoldPrice.HEADER` | `"year,value,unit,source"` | The table's header line. |
| 86 | `GoldPrice.GOLD` | `9_998` | The draws' who: gold sits beside WorldMoneys' world (9_999), below its moneys (10_000 on) and above the nations, regions and goods. |
| 89 | `GoldPrice.GOLD_DRAW` | `300` | The draws' what: no Nations draw (1 to 8) or WorldMoneys draw (100 to 104) is it. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 118 | `private static double[] table` |  |
| 237 | `private final Nations.Path path` | The rule: x(t) = ln(real price) - ln TARGET, and at the rule's first month s the state x(s - 1) is the official or the table's price at s - 1 (on Random July 1971's US$35, on Historical December 2025's US$3,300), then... |
| 238 | `private final long seed` |  |
| 239 | `private final IntToDoubleFunction usPriceLevel` |  |
| 240 | `private final boolean historical` |  |
| 241 | `private final int ruleStart` |  |
| 242 | `private final double lnTarget, lnUs0` |  |
| 244 | `private double[] x` | The walk so far: x[i] is the state of month ruleStart + i, the first {@code walked} of them known. |
| 245 | `private int walked` |  |
| 247 | `private double opening` | The state of the month before the rule's first: NaN until asked. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 31 | 302 | **type** `public final class GoldPrice` | Gold's price in US dollars a fine troy ounce, a month at a time (batch GD1; spec-gold-money 4.1): the statute's par to March 1933, a ten-month path to the new par, that par to July 1971, then on Historical the USGS's ... |

### THE DIALS - each with its source: (est.) is the game's estimate (lines 33-90)

### THE OFFICIAL PATH - the statute's price, the same on both paths (lines 91-113)

| line | len | member | says |
|---:|---:|---|---|
| 96 | 3 | `public static boolean isOfficial(int t)` | Whether the statute sets the month's price (January 1900 to July 1971): false before the calendar and after, where official() reads NaN. |
| 104 | 9 | `public static double official(int t)` | The statute's price in a month: PAR_1900 to March 1933, the geometric path to PAR_1934 over the next FLOAT_STEPS months, PAR_1934 to July 1971, NaN after (isOfficial says which). |

### THE TABLE - the USGS's annual price, read once (lines 114-215)

| line | len | member | says |
|---:|---:|---|---|
| 121 | 4 | `private static synchronized double[] table()` | The table, US$ an ounce a year from TABLE_FIRST_YEAR; read the first time it is asked, and refused, naming the line, if it is not whole. |
| 126 | 8 | `private static double[] load()` |  |
| 140 | 46 | `static double[] readTable(String text)` | A table's text read: comment lines (#) and blank ones skipped, then the header, then a line for each year TABLE_FIRST_YEAR to TABLE_LAST_YEAR (year, value, unit, source); US$ an ounce a year, index year - TABLE_FIRST_... |
| 188 | 5 | `public static double annual(int year)` | The table's price for a year, US$ an ounce: TABLE_FIRST_YEAR to TABLE_LAST_YEAR, refused outside them. |
| 195 | 1 | `private static int july(int year)` | July of a year as a month index: the month the year's average is placed at. |
| 198 | 1 | `private static double anchor(int year)` | The price a July is placed at: the table's year, but July 1971's is PAR_1934 (the free market's 1971 stays in the table as the record). |
| 205 | 10 | `static double tablePath(int t)` | Historical's price in a month from July 1971 (LAST_PAR_MONTH) to December 2025: the year's average placed at July, the months log-linear between Julys, ln p = ln a(y) + (t - July(y)) / 12 (ln a(y + 1) - ln a(y)); July... |

### THE RULE - ln of the real price an AR(1) toward the last twenty years' mean, its drift zero (est.) (lines 216-303)

| line | len | member | says |
|---:|---:|---|---|
| 224 | 5 | `public static double targetRealPrice()` | The target: the mean real price of the table's last MEAN_YEARS years, in dollars of January 1900 by the data's own prices (the US CPI), the mean of annual(y) x usCpi(1900) / usCpi(y). |
| 250 | 3 | `public GoldPrice(Nations nations)` | The gold price of a Nations: its path, its seed and its US line (index(Nations.FRONTIER)). |
| 255 | 9 | `GoldPrice(Nations.Path path, long seed, IntToDoubleFunction usPriceLevel)` | The gold price of a path and a seed on a US price level a year, in any base: the harness's fixture with no world file. |
| 265 | 5 | `private static IntToDoubleFunction usLine(Nations nations)` |  |
| 272 | 5 | `private double lnUs(int t)` | ln of the path's US price level in month t, log-linear between Januaries as WorldMoneys' is: ln P(t) = ln P(y) + (t mod 12) / 12 (ln P(y + 1) - ln P(y)). |
| 278 | 5 | `private double level(int year)` |  |
| 284 | 1 | `private double gauss(int t)` |  |
| 287 | 16 | `private double state(int t)` | The state x of a month from the one before the rule's first on, walking only the months not yet walked. |

### WHAT A CALLER READS (lines 304-332)

| line | len | member | says |
|---:|---:|---|---|
| 312 | 6 | `public synchronized double usdPerOunce(int t)` | Gold's price in a month, US$ a fine troy ounce: the statute's to July 1971 on both paths; then on Random the rule, on Historical the table to December 2025 and the rule from January 2026. |
| 320 | 3 | `public synchronized double realPrice(int t)` | Gold's price in a month in dollars of January 1900: usdPerOunce over the path's US price level relative to the calendar's first January. |
| 325 | 1 | `public int ruleStart()` | The first month the rule rules on this path. |
| 328 | 1 | `public Nations.Path path()` | The path this price is on. |
| 331 | 1 | `public long seed()` | The seed of its draws. |

