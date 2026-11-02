# LandTable.java - 362 lines · 13 methods · 5 constants · tools

`ham/citybuildersim/tools/LandTable.java` - generated 2026-11-02 by CodeMap; line numbers are as of that run.

> Makes src/main/resources/world/land-2024.json, the table LandFactors reads: every nation's land a hectare, in US dollars of 2024, from the
> published farmland prices of tools/world/land.tsv (Eurostat's, the USDA's, Statistics Canada's) and, for the nations that publish none, a fit.
> 
> WHY. Jerus: "land costs different prices purely by who you are buying it from" (spec-nations 6.3). Three statistical offices publish a price of
> farmland, which is 25 of the world file's 196 nations; the other 171 need one too, and a guess for each by hand would be 171 guesses. A fit on what is
> published (ln of the price against ln of the nation's output a head and ln of its people a square kilometre, ordinary least squares over the 25) gives
> every nation a figure that moves the right way with how rich and how crowded it is, and is marked 1, the game's estimate, against the 3 of a
> published one. A fitted nation is never dearer than the dearest nation that publishes (CEILING): the city-states, Singapore, Monaco, Macau and Hong
> Kong, would otherwise run to 30 to 200 times Canada's price, from an elasticity read off 25 nations none of which is a city.
> 
> Reads tools/world/land.tsv, the world file (WorldData.locate(), or the first argument) for each nation's output a head in 2024 (gdp_usd over people)
> and src/main/resources/world/world-2025.json for its people a square kilometre. Run it from the repository root; same input, same bytes.

**Uses:** [LandFactors](LandFactors.md) (8), [WorldData](WorldData.md) (8)

**Used by (1):** [LandNationsCheck](LandNationsCheck.md)

## Sections

| line | section |
|---:|---|
| 156 | READING |
| 208 | THE FIT |
| 266 | WRITING |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 41 | `LandTable.TSV` | `"tools/world/land.tsv"` | The published prices, one row each, and their rates and sources. |
| 44 | `LandTable.RANKINGS` | `"src/main/resources/world/world-2025.json"` | The rankings' file, whose places carry each nation's density (people a square kilometre). |
| 47 | `LandTable.OUT` | `"src/main/resources" + LandFactors.RESOURCE` | The table it writes. |
| 50 | `LandTable.ACRES_PER_HECTARE` | `2.47105381` | Acres in a hectare: a published price an acre times it is the price a hectare. |
| 53 | `LandTable.PUBLISHED` | `WorldData.MARK_PUBLISHED, ESTIMATE = WorldData.MARK_ESTIMATE` | The mark of a published figure, and of the game's estimate (WorldData.MARK_PUBLISHED, MARK_ESTIMATE). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 60 | `final int number` |  |
| 61 | `final String iso3, name` |  |
| 62 | `double usdHa` |  |
| 63 | `int mark` |  |
| 64 | `String source` |  |
| 65 | `Row row` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 36 | 327 | **type** `public final class LandTable` | Makes src/main/resources/world/land-2024.json, the table LandFactors reads: every nation's land a hectare, in US dollars of 2024, from the published farmland prices of tools/world/land.tsv (Eurostat's, the USDA's, Sta... |
| 38 | 1 | `private LandTable()` |  |
| 56 | 1 | **type** `record Row(String iso3, double figure, String money, String per, int year, String source)` | One published row of the tsv. |
| 59 | 14 | **type** `static final class Line` | One nation's line of the table, before it is written. |
| 67 | 5 | `Line(int number, String iso3, String name)` _(in LandTable.Line)_ |  |
| 74 | 8 | `public static void main(String[] args) throws IOException` |  |
| 83 | 68 | `static void run(WorldData file, Path tsv, Path rankings, Path out) throws IOException` |  |
| 152 | 3 | `private static boolean usable(Line line)` |  |

### READING (lines 156-207)

| line | len | member | says |
|---:|---:|---|---|
| 160 | 33 | `private static void readTsv(Path tsv, List<Row> rows, Map<String, Double> usdPerUnit, Map<String, String> rateRows, Map<String,...` |  |
| 195 | 12 | `private static Map<String, Double> readDensity(Path rankings) throws IOException` | The rankings' people a square kilometre by iso3 (a place's id): the first of its "density" figure. |

### THE FIT (lines 208-265)

| line | len | member | says |
|---:|---:|---|---|
| 213 | 52 | **type** `record Fit(double a, double output, double density, double r2, double sd, int n)` | Ordinary least squares of ln usd_ha on [1, ln output a head, ln density]: the three coefficients, R squared, the residual standard error, how many nations. |
| 215 | 26 | `static Fit of(List<Line> basis)` _(in LandTable.Fit)_ |  |
| 243 | 21 | `private static double[] solve(double[][] m)` _(in LandTable.Fit)_ | Gaussian elimination with partial pivoting on a 3 x 4 augmented matrix. |

### WRITING (lines 266-362)

| line | len | member | says |
|---:|---:|---|---|
| 270 | 1 | `private static double round2(double v)` |  |
| 272 | 1 | `private static double round3(double v)` |  |
| 275 | 68 | `private static void write(Path out, List<Line> lines, Fit fit, double ceiling, Map<String, Double> usdPerUnit, Map<String, Stri...` | The table's text: the meta apart and each nation on a line of its own, so a diff of a changed figure is one line. |
| 344 | 18 | `private static void summary(List<Line> lines, Fit fit, double ceiling, double reference, int none, Path out)` |  |

