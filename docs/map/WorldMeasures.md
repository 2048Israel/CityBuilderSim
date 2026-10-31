# WorldMeasures.java - 388 lines · 46 methods · 1 constants · model

`ham/citybuildersim/WorldMeasures.java` - generated 2026-10-31 by CodeMap; line numbers are as of that run.

> The Rankings' real-world figures: world-2025.json's 196 nations and city
> regions, the 70 measures each is ranked on, every place's latest figure of
> each with its year, its mark (published, a published projection, the game's
> estimate) and its source, and the sources' licences, the estimates' rules
> and the CPI-U that turns a US dollar of any year into a 2015 one.
> 
> WHY. The board's Real world 2025 (spec-leaderboards 6-8, batch LB1) ranks
> the player's cities beside every real place on every measure, and each
> figure it shows has to say where it comes from, in what year, and whether
> the game made it. The game never fetches anything, so tools/world/ bakes the
> figures into the resources once, and this reads them: it trusts the file and
> never invents - a figure the file does not carry is absent (null, NaN),
> never 0, because the leaderboard that read a 0 would rank a place last for
> a number nobody published. What the file must keep to is WorldMeasuresCheck's,
> not this reader's; the format is tools/world/FORMAT-2025.txt.

**Used by (1):** [WorldMeasuresCheck](WorldMeasuresCheck.md)

## Sections

| line | section |
|---:|---|
| 298 | THE FILE'S META |
| 368 | THE PLACES |

## Enum constants

| line | constant | says |
|---:|---|---|
| 44 | `WorldMeasures.Arrow.UP` |  |
| 44 | `WorldMeasures.Arrow.DOWN` |  |
| 44 | `WorldMeasures.Arrow.NONE` |  |
| 71 | `WorldMeasures.Kind.NATION` |  |
| 71 | `WorldMeasures.Kind.REGION` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 38 | `WorldMeasures.RESOURCE` | `"world/world-2025.json"` | Where the file lies, beside the classes. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 92 | `private final String name, made, cpiSeries, cpiSource` |  |
| 93 | `private final int year, windowFirst, windowLast, countNations, countRegions, countFigures, countEstimates` |  |
| 94 | `private final double usPrices2015Over2025` |  |
| 95 | `private final Map<Integer, Double> cpiU` |  |
| 96 | `private final List<Measure> measures` |  |
| 97 | `private final Map<String, Measure> measureByKey` |  |
| 98 | `private final Map<String, Variant> variants` |  |
| 99 | `private final Map<String, Source> sources` |  |
| 100 | `private final Map<String, String> rules, definitions` |  |
| 101 | `private final Map<Integer, Mark> marks` |  |
| 102 | `private final List<Place> places, nations, regions` |  |
| 103 | `private final Map<String, Place> placeById` |  |
| 105 | `private static WorldMeasures cached` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 35 | 354 | **type** `public final class WorldMeasures` | The Rankings' real-world figures: world-2025.json's 196 nations and city regions, the 70 measures each is ranked on, every place's latest figure of each with its year, its mark (published, a published projection, the ... |
| 41 | 1 | **type** `public record Figure(double value, int year, int mark, String source, String rule)` | A figure: its value in the measure's unit, the year it is of, its mark (3 published, 2 a published projection, 1 the game's estimate), the source it was read or made from, and the rule that made it (null unless the ma... |
| 44 | 1 | **type** `public enum Arrow` | Which way is better on a measure: more (UP), less (DOWN, ranked ascending), or neither (NONE: ranked largest first, with no verdict colour; spec-leaderboards 6). |
| 49 | 11 | **type** `public record Measure(String key, String group, String name, String unit, Arrow arrow, boolean money, Strin...` | A measure of the board: its key (the key of every place's figure), its group, name and unit, the arrow, whether it is in US$ and follows the Money toggle, its scope ("N" nations, "NC" nations and city regions, "Y" onl... |
| 52 | 1 | `public boolean forRegions()` _(in WorldMeasures.Measure)_ | Whether city regions carry it as well as nations. |
| 55 | 1 | `public boolean yoursOnly()` _(in WorldMeasures.Measure)_ | Whether only the player's cities are ranked on it: no real place has a figure. |
| 58 | 1 | `public boolean sourced()` _(in WorldMeasures.Measure)_ | Whether anything publishes it: a measure whose source is "none:..." has no figure for any real place. |
| 62 | 1 | **type** `public record Variant(String key, String of, String money, String source)` | A GDP measure in another money (key "gdp.k", of "gdp"): the money's name and the source spec. |
| 65 | 1 | **type** `public record Source(String id, String name, String url, String licence, String downloaded)` | A source a figure names: its id, name, url, licence and the day it was downloaded. |
| 68 | 1 | **type** `public record Mark(int mark, String symbol, String name)` | A mark: its number (3, 2 or 1), its symbol and its name. |
| 71 | 1 | **type** `public enum Kind` | A nation, or a city region. |
| 76 | 15 | **type** `public record Place(String id, Kind kind, String name, int band, String nation, String def, Figure sharePeo...` | A place. |
| 78 | 3 | `{ ... }` _(in WorldMeasures.Place)_ |  |
| 83 | 1 | `public Figure figure(String key)` _(in WorldMeasures.Place)_ | Its figure for a measure or variant key, null when the place has none. |
| 86 | 4 | `public double value(String key)` _(in WorldMeasures.Place)_ | Its value for a measure or variant key, NaN when the place has none. |
| 108 | 11 | `public static synchronized WorldMeasures load()` | The file in the resources, read once. |
| 121 | 5 | `public static WorldMeasures parse(String json)` | A file's text read as it stands, not cached: the harness's fixtures. |
| 127 | 77 | `private WorldMeasures(JsonObject root)` |  |
| 205 | 24 | `private static Place place(JsonObject p)` |  |
| 231 | 13 | `private static Figure figure(JsonElement e, String place, String key)` | A figure array of 4 or 5 elements; anything else names the place and key. |
| 245 | 9 | `private static Arrow arrow(JsonObject m, String key)` |  |
| 255 | 5 | `private static Map<String, String> strings(JsonObject o)` |  |
| 261 | 3 | `private static boolean has(JsonObject o, String key)` |  |
| 265 | 3 | `private static String text(JsonObject o, String key)` |  |
| 269 | 3 | `private static boolean flag(JsonObject o, String key)` |  |
| 273 | 4 | `private static String need(JsonObject o, String key, String where)` |  |
| 278 | 4 | `private static int needInt(JsonObject o, String key, String where)` |  |
| 283 | 4 | `private static double needDouble(JsonObject o, String key, String where)` |  |
| 288 | 4 | `private static JsonObject object(JsonObject o, String key, String where)` |  |
| 293 | 4 | `private static JsonArray array(JsonObject o, String key, String where)` |  |

### THE FILE'S META (lines 298-367)

| line | len | member | says |
|---:|---:|---|---|
| 303 | 1 | `public String name()` | The file's name ("world-2025"). |
| 306 | 1 | `public String made()` | The day the file was built (yyyy-mm-dd). |
| 309 | 1 | `public int year()` | The board's year: each figure is its place's latest in the window. |
| 312 | 1 | `public int windowFirst()` | The first year of the window: a figure older than this is left out. |
| 315 | 1 | `public int windowLast()` | The last year of the window. |
| 318 | 1 | `public double usPrices2015Over2025()` | The US's consumer prices of 2015 over 2025's (the CPI-U's annual averages divided, unrounded): a real 2025 dollar times this is a 2015 dollar. |
| 321 | 4 | `public double cpiU(int year)` | The CPI-U's annual average of a year the file carries (the BLS's, series CUUR0000SA0), NaN for a year it does not. |
| 327 | 1 | `public List<Integer> cpiYears()` | The years cpiU() has a value for, in order. |
| 330 | 1 | `public String cpiSeries()` | The BLS series the CPI-U is of (CUUR0000SA0), null when the file does not say. |
| 333 | 1 | `public String cpiSource()` | The id in sources() the CPI-U was read from, null when the file does not say. |
| 336 | 1 | `public List<Measure> measures()` | The 70 measures, in the file's order. |
| 339 | 1 | `public Measure measure(String key)` | A measure by key, null when there is none. |
| 342 | 1 | `public Map<String, Variant> variants()` | The GDP variants (gdp.k, gdp.ppp, gdp_pp.k, gdp_pp.ppp) by key. |
| 345 | 1 | `public Map<String, Source> sources()` | The sources figures name, by id, each with its licence. |
| 348 | 1 | `public Map<String, String> rules()` | How the game made each kind of estimate (mark 1), by rule id. |
| 351 | 1 | `public Map<String, String> definitions()` | The city boundaries a region's def names, by id. |
| 354 | 1 | `public Map<Integer, Mark> marks()` | The marks (3, 2, 1), by number. |
| 357 | 1 | `public int countNations()` | The nations the meta's counts say. |
| 360 | 1 | `public int countRegions()` | The city regions the meta's counts say. |
| 363 | 1 | `public int countFigures()` | The figures the meta's counts say (the places' figures, not their shares). |
| 366 | 1 | `public int countEstimates()` | The figures of them marked 1 the meta's counts say. |

### THE PLACES (lines 368-388)

| line | len | member | says |
|---:|---:|---|---|
| 373 | 1 | `public List<Place> places()` | Every place: the nations in ISO3 order, then the city regions. |
| 376 | 1 | `public List<Place> nations()` | The nations, in the file's order. |
| 379 | 1 | `public List<Place> regions()` | The city regions, in the file's order. |
| 382 | 1 | `public Place place(String id)` | A place by id, null when there is none. |
| 385 | 3 | `public String toString()` |  |

