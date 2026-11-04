# LandFactors.java - 232 lines · 18 methods · 5 constants · model

`ham/citybuildersim/LandFactors.java` - generated 2026-11-04 by CodeMap; line numbers are as of that run.

> Each nation's land factor: what a hectare of its land costs against a hectare of Canada's (1, D21), at the founding and as its output a head moves
> against Canada's. A pure model; the land office multiplies today's dollar formula by the seller's factor (WN5; LandSellers says who sells which plot).
> 
> WHY THIS EXISTS. Land was priced by one formula for the whole world and bought from nobody. Jerus: "land costs different prices purely by who you are
> buying it from", and "borders baked in; land bought from its nation" (spec-nations 6): a plot at Windsor on the Detroit bank is dearer than the one
> across the river. The figures are the published farmland prices of 25 nations and a fit for the other 171 (tools.LandTable makes the resource from
> tools/world/land.tsv, land-2024.json), in US dollars of 2024. A factor is a ratio, and a ratio to Canada needs no unit: Canada is 1.0 exactly, however
> the dollar or the money moves.
> 
> THE MOVE. From the founding (January of Nations.FIRST_YEAR, 1900 since EP1) the factor moves with the nation's real output a head on its path
> (Nations), against Canada's, at b, the fit's elasticity of land's price on output a head; log-linear between Januaries, as WorldMoneys moves a price
> level. With no path handed (a bare one, a harness's), or in month 0, it is the level.
> 
> WHY NOT ITS MONEY TOO. The study's line has the factor move "with its money" as well. WN5 first moved it with the nation's US dollar price level, its
> consumer prices over its money's rate, each money against its own rate at the founding as the real effective rate reads a partner (WN4). A price index
> runs on through a change of money and the new money's rate starts afresh, so every nation that redenominated fell to FACTOR_FLOOR: Zimbabwe's move to
> 2024 read 1.4e20, Venezuela's 430 against Canada's 47. The real rate's own swings mean-revert, and a nation's land is dear for the output a head it
> stands for; so the money is left out, and what the city pays follows the seller's money only through the rate it is paid at.
> 
> THE FOUNDING. The table is 2024's, so the level at the founding is the published ratio taken back along the same move on the file's own figures, the
> Historical path's: level = published ratio x (Canada's output a head from the founding to 2024 over the nation's)^b (fileMove()). A Historical city in
> January 2024 therefore pays the published ratio to the rounding of a power, and a nation that has grown richer than Canada since 2000 (the file holds
> the century before at 2000's real figures) was cheaper then. A Random path meets the same founding and moves on its own.

**Uses:** [Nations](Nations.md) (9), [WorldData](WorldData.md) (4), [WorldMoneys](WorldMoneys.md) (1)

**Used by (7):** [Game](Game.md), [LandMarket](LandMarket.md), [LandNationsCheck](LandNationsCheck.md), [LandSellers](LandSellers.md), [LandTable](LandTable.md), [PartnersCheck](PartnersCheck.md), [PartnersView](PartnersView.md)

## Sections

| line | section |
|---:|---|
| 74 | THE TABLE |
| 167 | THE FACTOR IN A MONTH |
| 208 | WHAT A CALLER READS |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 42 | `LandFactors.RESOURCE` | `"/world/land-2024.json"` | The land table on the classpath: tools.LandTable's output. |
| 45 | `LandFactors.REFERENCE` | `"CAN"` | The nation whose factor is exactly 1.0 (D21): Canada, whose land the game's formula was struck on. |
| 48 | `LandFactors.BASE_YEAR` | `2024` | The year of the table's figures. |
| 51 | `LandFactors.FACTOR_FLOOR` | `0.01` | The least a factor is, whatever a path does to its nation: a hundredth of Canada's price. |
| 54 | `LandFactors.FACTOR_CEILING` | `100` | The most a factor is: a hundred times Canada's price. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 56 | `private final int n` |  |
| 57 | `private final int reference` |  |
| 58 | `private final double elasticity` |  |
| 59 | `private final double[] usdHa, level` |  |
| 60 | `private final int[] mark` |  |
| 61 | `private final String[] source` |  |
| 62 | `private volatile Nations nations` |  |
| 84 | `private static Table table` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 39 | 194 | **type** `public final class LandFactors` | Each nation's land factor: what a hectare of its land costs against a hectare of Canada's (1, D21), at the founding and as its output a head moves against Canada's. |
| 64 | 9 | `private LandFactors(int n, int reference, double elasticity, double[] usdHa, double[] level, int[] mark, String[] source)` |  |

### THE TABLE (lines 74-166)

| line | len | member | says |
|---:|---:|---|---|
| 79 | 1 | **type** `private record Entry(double usdHa, int mark, String source)` | One nation's line of the resource. |
| 82 | 1 | **type** `private record Table(double elasticity, Map<String, Entry> byIso3)` | The resource read: the fit's elasticity and each nation's line by iso3. |
| 86 | 4 | `private static synchronized Table table()` |  |
| 91 | 22 | `private static Table read()` |  |
| 119 | 26 | `public static LandFactors load(WorldData file)` | The factors at the founding, for the nations of a world file: the table's published ratio to Canada's, times (Canada's move from the founding to January of BASE_YEAR over the nation's)^elasticity, each move read off t... |
| 151 | 8 | `static double fileMove(WorldData file, int nation)` | move() on the file's own figures, from the founding to BASE_YEAR: the output a head (gdp_2015usd over people, what Nations' output and people are on the Historical path) in BASE_YEAR over Nations.FIRST_YEAR's - what m... |
| 160 | 1 | `private static boolean usable(double v)` |  |
| 162 | 4 | `private static double clamp(double v)` |  |

### THE FACTOR IN A MONTH (lines 167-207)

| line | len | member | says |
|---:|---:|---|---|
| 172 | 3 | `public void setPaths(Nations nations)` | Hands the factors the nations' path (once the world market is built): its output a head is what a factor moves with. |
| 177 | 1 | `public boolean hasPaths()` | Whether a path has been handed (setPaths()). |
| 184 | 9 | `public double factorAt(int nation, int gameMonth)` | The nation's land factor in a game month: 1.0 exactly for the reference, 1.0 for a number outside 1..nationCount(), the level at the founding when no path is handed or in the founding's month, else the level times (th... |
| 198 | 9 | `private static double move(Nations paths, int nation, int t)` | What a nation's output a head has done since the founding, by month t (months since January of Nations.FIRST_YEAR): its year's over its first year's, log-linear between Januaries as WorldMoneys.priceLevel() reads a ye... |

### WHAT A CALLER READS (lines 208-232)

| line | len | member | says |
|---:|---:|---|---|
| 213 | 1 | `public double level(int nation)` | The nation's factor at the founding (month 0), 1.0 for a number outside 1..nationCount(). |
| 216 | 1 | `public double usdPerHectare(int nation)` | The nation's land a hectare, US dollars of BASE_YEAR (the table's: a published price, the fit's, or Canada's where it lacks the figures); the reference's for a number outside 1..nationCount(). |
| 219 | 1 | `public int mark(int nation)` | How sure the table is of the nation's price: WorldData.MARK_PUBLISHED (3) where a statistical office publishes it, MARK_ESTIMATE (1) for the fit or Canada's price, MARK_NONE (0) for a nation the table lacks. |
| 222 | 1 | `public String source(int nation)` | Where the price comes from: a source id of the table ("eurostat", "usda", "statcan"), "fit", or "none" (Canada's price used). |
| 225 | 1 | `public int reference()` | The number of the reference nation (Canada), 0 where the world file lacks it. |
| 228 | 1 | `public double elasticity()` | The exponent a factor moves with its nation's output a head against Canada's: the fit's elasticity of land's price on output a head. |
| 231 | 1 | `public int nationCount()` | How many nations there are (the world file's N). |

