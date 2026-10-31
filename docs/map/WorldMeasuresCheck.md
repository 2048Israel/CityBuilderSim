# WorldMeasuresCheck.java - 662 lines · 25 methods · 47 constants · harnesses

`ham/citybuildersim/WorldMeasuresCheck.java` - generated 2026-10-31 by CodeMap; line numbers are as of that run.

> The Rankings' real-world file held to what it promises (batch LB1; the
> project's spec-leaderboards.md 6-8): WorldMeasures' reading of
> world-2025.json, and the rules tools/world/FORMAT-2025.txt states of the
> file, which the pipeline could break without the reader noticing.
> 
> WHY. The board's Real world 2025 ranks the player's cities beside 196
> nations and 62 city regions on 70 measures, and a wrong figure there is a
> rank nobody can trust: a missing figure read as 0 puts a nation last for a
> number nobody published, a mark or a source that does not resolve is a
> drawer that cannot say where a figure came from, and a ratio of US prices
> that is not the CPI-U's turns every real dollar into the wrong 2015 one.
> The rules are one method, problems(), each problem a short sentence naming
> the place and key; the real file has none, and for each kind a copy of the
> real JSON changed in one place has exactly that one.
> 
> THE DATA. world-2025.json in the resources, always. The full world-1.bin
> (-Dcbs.world=<path>, WorldData.locate()) is not in the repository; where it
> is named, section 2 also holds the file to every one of its nations, and a
> line says when it is not.
> 
> What this has to prove:
>   1. THE FILE: it loads; its name, year and window; under 1 MB; 70 measures
>      with unique keys in ten groups; 13 for city regions, 4 only the player's
>      cities rank, 10 on Your world; problems() of the real file is empty.
>   2. EVERY NATION OF world-1.bin IS THERE ONCE: 196 nations in ISO3 order,
>      no region with a nation's id, the fixture's twelve, and on the full file
>      every nation it has, with its name, and none it lacks.
>   3. A MISSING FIGURE IS ABSENT, NEVER 0: Taiwan carries no WDI figure but
>      world-1's; no place has a figure of a measure nothing publishes or only
>      the player's cities rank; a region has only city-region measures; every
>      value is finite, and a measure that cannot be 0 is over it.
>   4. EVERY MARK AND SOURCE RESOLVES, AND EVERY SOURCE NAMES ITS LICENCE: marks
>      are 1, 2 or 3 and listed; sources and rules are in the meta and each
>      used; a mark 1 has a rule and the others none; the IMF's World Economic
>      Outlook is not a source; the meta's counts are a recount.
>   5. A CITY REGION'S SHARES OF ITS NATION ARE UNDER 1: its nation, boundary
>      and both shares resolve, and it is smaller than its nation.
>   6. THE 2015-OVER-2025 RATIO IS THE CPI-U'S TO THE DIGIT: the BLS's annual
>      averages for 2015 and 2025, every year of the window between them
>      rising, and the ratio their division.
>   7. THE MONEY FIGURES AGREE WITH EACH OTHER: the US is 1 on price level and
>      rate; PPP over market is 1 over the price level; GDP per person is GDP
>      over people; a band is the digits of the people; a region is smaller than
>      its nation; a region on its people's share has no GDP per person.
> 
> Every fixture causes its condition: a doctored copy is the real JSON parsed,
> changed in one place and written back, so that what it breaks is the one rule
> its section names, and the real file is checked by the same method.

**Uses:** [WorldMeasures](WorldMeasures.md) (53), [WorldData](WorldData.md) (12), [WorldDataCheck](WorldDataCheck.md) (2)

## Sections

| line | section |
|---:|---|
| 168 | THE RULES - one method, each problem a sentence naming the place and key |
| 277 | THE DOCTORED COPIES |
| 339 | THE SECTIONS |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 75 | `WorldMeasuresCheck.out` | `System.out` | Where the harness prints: the console. |
| 78 | `WorldMeasuresCheck.NATIONS` | `196` | The nations of world-1.bin (the 193 UN members, Taiwan, Hong Kong and Macau). |
| 81 | `WorldMeasuresCheck.MAX_BYTES` | `1_000_000` | The most the file may weigh: it is a resource inside the jar, and it is read whole. |
| 84 | `WorldMeasuresCheck.CPI_U_2015` | `237.017` | The CPI-U's annual average of 2015, all urban consumers, US city average, all items, not seasonally adjusted (BLS series CUUR0000SA0). |
| 87 | `WorldMeasuresCheck.CPI_U_2025` | `321.943` | The CPI-U's annual average of 2025 (BLS series CUUR0000SA0). |
| 90 | `WorldMeasuresCheck.CPI_SERIES` | `"CUUR0000SA0"` | The BLS series the CPI-U is of. |
| 93 | `WorldMeasuresCheck.GROUPS` | `List.of("economy 11", "work 6", "prices 8", "public 8", "banks 5", "people 11...` | The ten groups of measures, in the order of the file, each with how many it has. |
| 96 | `WorldMeasuresCheck.SCOPE_NC` | `13, SCOPE_Y = 4` | The measures whose scope is city regions too (NC), and the ones only the player's cities rank (Y). |
| 99 | `WorldMeasuresCheck.YOUR_WORLD` | `List.of("gdp", "gdp_pp", "growth_real", "growth_10y", "inflation", "price_lev...` | The ten measures of Your world (spec-leaderboards D15), in the file's order. |
| 102 | `WorldMeasuresCheck.POSITIVE` | `Set.of("gdp", "gdp.k", "gdp.ppp", "gdp_pp", "gdp_pp.k", "gdp_pp.ppp", "people...` | The measures (and the GDP variants) a place cannot have 0 of: a figure of 0 for one is a figure nobody published. |
| 107 | `WorldMeasuresCheck.AGREE_SHARE` | `.95, AGREE_TOLERANCE =.01` | The share of the money figures that must agree with their identity, and how near is agreeing: a few nations' published figures miss each identity (the WDI converts AGO BDI NPL SYR UGA ZWE at an alternative rate and gi... |
| 110 | `WorldMeasuresCheck.MIN_COUNTED` | `100` | The fewest nations an identity may be counted over before the share means anything. |
| 113 | `WorldMeasuresCheck.US_ONE_TOLERANCE` | `1e-9` | The nearest the United States' price level and rate against parity are to 1: the base of both is the United States. |
| 116 | `WorldMeasuresCheck.DOCTORED_NATION` | `"AFG"` | The nation the doctored copies change (Afghanistan, which has a published GDP and most of the others). |
| 119 | `WorldMeasuresCheck.DOCTORED_REGION` | `"TYO"` | The city region the doctored copies change (Tokyo, an OECD region with published shares). |
| 122 | `WorldMeasuresCheck.P_DUP_MEASURE` | `"a measure key that is in the file twice"` | The problems' sentences: problems() writes them and the sections look for them by these words. |
| 123 | `WorldMeasuresCheck.P_DUP_PLACE` | `"a place id that is in the file twice"` |  |
| 124 | `WorldMeasuresCheck.P_NOT_A_MEASURE` | `"is not a measure or a variant of the file"` |  |
| 125 | `WorldMeasuresCheck.P_NO_SOURCE` | `"is a measure nothing publishes(its source is none:)"` |  |
| 126 | `WorldMeasuresCheck.P_Y_ONLY` | `"is a measure only the player's cities rank(scope Y)"` |  |
| 127 | `WorldMeasuresCheck.P_REGION_SCOPE` | `"is not a city-region measure(scope NC)"` |  |
| 128 | `WorldMeasuresCheck.P_NOT_FINITE` | `"is not a finite number"` |  |
| 129 | `WorldMeasuresCheck.P_NOT_POSITIVE` | `"is not over 0"` |  |
| 130 | `WorldMeasuresCheck.P_WINDOW` | `"is outside the window"` |  |
| 131 | `WorldMeasuresCheck.P_MARK` | `"is not 1, 2 or 3"` |  |
| 132 | `WorldMeasuresCheck.P_MARK_UNLISTED` | `"is not in meta.marks"` |  |
| 133 | `WorldMeasuresCheck.P_SOURCE` | `"is not in meta.sources"` |  |
| 134 | `WorldMeasuresCheck.P_RULE_MISSING` | `"is an estimate(mark 1) with no rule"` |  |
| 135 | `WorldMeasuresCheck.P_RULE_UNKNOWN` | `"is not in meta.rules"` |  |
| 136 | `WorldMeasuresCheck.P_RULE_SPARE` | `"is not an estimate and carries a rule"` |  |
| 137 | `WorldMeasuresCheck.P_LICENCE` | `"licence is blank"` |  |
| 138 | `WorldMeasuresCheck.P_URL` | `"url is blank"` |  |
| 139 | `WorldMeasuresCheck.P_WEO` | `"mentions the IMF's World Economic Outlook"` |  |
| 140 | `WorldMeasuresCheck.P_SOURCE_UNUSED` | `"source is named by no figure"` |  |
| 141 | `WorldMeasuresCheck.P_RULE_UNUSED` | `"rule is made by no figure"` |  |
| 142 | `WorldMeasuresCheck.P_COUNT` | `"but the places hold"` |  |
| 143 | `WorldMeasuresCheck.P_REGION_NATION` | `"is not a nation place"` |  |
| 144 | `WorldMeasuresCheck.P_REGION_ID` | `"has a nation's id"` |  |
| 145 | `WorldMeasuresCheck.P_REGION_DEF` | `"is not in meta.definitions"` |  |
| 146 | `WorldMeasuresCheck.P_SHARE_MISSING` | `"is missing"` |  |
| 147 | `WorldMeasuresCheck.P_SHARE_RANGE` | `"is not over 0 and under 1"` |  |
| 148 | `WorldMeasuresCheck.P_REGION_PEOPLE` | `"is not under its nation's"` |  |
| 149 | `WorldMeasuresCheck.P_CPI_YEAR` | `"is absent"` |  |
| 150 | `WorldMeasuresCheck.P_CPI_RISE` | `"does not rise"` |  |
| 151 | `WorldMeasuresCheck.P_CPI_CONSTANT` | `"is not the BLS's"` |  |
| 152 | `WorldMeasuresCheck.P_CPI_SERIES` | `"series is not"` |  |
| 153 | `WorldMeasuresCheck.P_RATIO` | `"is not cpi_u 2015 over 2025"` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 73 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 71 | 592 | **type** `public class WorldMeasuresCheck` | The Rankings' real-world file held to what it promises (batch LB1; the project's spec-leaderboards.md 6-8): WorldMeasures' reading of world-2025.json, and the rules tools/world/FORMAT-2025.txt states of the file, whic... |
| 156 | 4 | `static void check(String label, boolean ok)` | Prints a label and OK or FAIL; the exit code is the count of FAILs. |
| 162 | 5 | `static void all(String label, int counted, List<String> bad)` | A check over many things, that says how many there were and the first few that were wrong. |

### THE RULES - one method, each problem a sentence naming the place and key (lines 168-276)

| line | len | member | says |
|---:|---:|---|---|
| 173 | 80 | `static List<String> problems(WorldMeasures w)` | Everything wrong with a file, as short sentences naming the place and key; none when it keeps FORMAT-2025.txt. |
| 255 | 16 | `static void figureProblems(String at, WorldMeasures.Figure f, WorldMeasures w, List<String> p, Set<String> usedSources, Set<Str...` | What is wrong with one figure besides its value: its year, its mark, its source and its rule. |
| 273 | 3 | `static String digits17(double v)` | A double to 17 significant digits, which tells any two doubles apart. |

### THE DOCTORED COPIES (lines 277-338)

| line | len | member | says |
|---:|---:|---|---|
| 282 | 5 | `static WorldMeasures doctored(String json, Consumer<JsonObject> change)` | The real JSON parsed into an object, changed in one place by the change, and read again as a file. |
| 288 | 1 | `static JsonObject meta(JsonObject root)` |  |
| 290 | 6 | `static JsonObject placeOf(JsonObject root, String id)` |  |
| 297 | 5 | `static JsonArray figureOf(JsonObject root, String id, String key)` |  |
| 304 | 10 | `static void addFigure(JsonObject root, String id, String key, double value, int year, int mark, String source)` | A figure added to a place, with the meta's figure count one more so that the count is not what the copy breaks. |
| 316 | 7 | `static void caught(String label, WorldMeasures copy, String...words)` | Whether the copy has exactly one problem, and it says the words; prints what it has when it does not. |
| 325 | 6 | `static void caughtAll(String label, WorldMeasures copy, String word)` | Whether the copy has problems, and every one says the words (a change that breaks many figures alike). |
| 333 | 5 | `static void none(String label, List<String> real, String word)` | Whether the real file has no problem of a kind. |

### THE SECTIONS (lines 339-662)

| line | len | member | says |
|---:|---:|---|---|
| 343 | 26 | `public static void main(String[] args) throws Exception` |  |
| 371 | 35 | `static void theFile(WorldMeasures w, byte[] bytes, List<String> real)` | 1. |
| 408 | 26 | `static void theNations(WorldMeasures w) throws Exception` | 2. |
| 436 | 19 | `static void presence(WorldMeasures w, String of, WorldData data, boolean full)` | Every nation of a world-1 file is in the measures file once, with its name; for the full file, none is there that the file lacks. |
| 457 | 34 | `static void theMissing(WorldMeasures w, String json, List<String> real)` | 3. |
| 493 | 41 | `static void theMarksAndSources(WorldMeasures w, String json, List<String> real)` | 4. |
| 536 | 19 | `static void theShares(WorldMeasures w, String json, List<String> real)` | 5. |
| 557 | 27 | `static void theCpi(WorldMeasures w, String json, List<String> real)` | 6. |
| 586 | 59 | `static void theMoney(WorldMeasures w)` | 7. |
| 647 | 3 | `static boolean published(WorldMeasures.Figure f)` | Whether a figure is there and published (mark 3). |
| 652 | 3 | `static int bandOf(double people)` | The size band of a people figure: the digits of the number, to 6 significant digits, less one. |
| 657 | 5 | `static void agree(String label, int counted, List<String> outside)` | At least AGREE_SHARE of the counted agree with an identity: prints the count, the share and the ones outside. |

