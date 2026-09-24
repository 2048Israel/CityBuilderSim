# YearBook.java - 1,121 lines · 51 methods · 8 constants · model

`ham/citybuildersim/YearBook.java` - generated 2026-09-24 by CodeMap; line numbers are as of that run.

> The run, one line a year - for READING rather than for drawing.
> 
> WHY THIS EXISTS
> 
> Jerus, 2026-09-14: "i want another txt or something which is different, it
> basically summarizes not by month, but by year, all the stuff, so that you or
> another ai, can be sent that, and doesnt die due to tokens... so that they can
> analyze the economic situation of the run."
> 
> The history is every month the city has ever lived across a hundred-odd series.
> A 333-year run is forty thousand numbers a series and nobody - person or
> model - can hold it. Twelve months folded into one row divides it by twelve
> and loses almost nothing an economy is judged on, because an economy is
> judged on years.
> 
> WHY EVERY COLUMN CARRIES ITS OWN RULE
> 
> A year is not one operation. Summing a population gives twelve times the city;
> averaging GDP gives a month and calls it a year; summing an interest rate is
> meaningless in any unit. So each series declares what it IS - a flow, a level
> or a rate - and the fold follows from that. The rules are a table in this
> file rather than a guess from the name, and YearBookCheck asserts the table
> covers every series HistorySave keeps. A series added without a rule still
> appears, marked, rather than silently vanishing from the analysis.
> 
> WHY THE EXTREMES ARE HERE TOO
> 
> A year's average hides the month the bank failed and the epidemic that ran in
> March. That is the same lesson the skip report was built on: an episode
> cannot be reconstructed from endpoints. So every rate column also reports its
> worst and best single month inside the row, and the file ends with a plain
> list of what actually happened and when.
> 
> NOTHING HERE READS THE CITY. It is a pure function of a HistorySave, which is
> what lets a harness build a history by hand and assert the arithmetic, and
> what lets the export run on a loaded slot as happily as on the live game.

**Uses:** [HistorySave](HistorySave.md) (23), [CityCalendar](CityCalendar.md) (5), [GameVersion](GameVersion.md) (2)

**Used by (4):** [Game](Game.md), [HistoryCheck](HistoryCheck.md), [HistoryScreen](HistoryScreen.md), [YearBookCheck](YearBookCheck.md)

## Sections

| line | section |
|---:|---|
| 64 | THE RULES |
| 270 | THE DERIVED COLUMNS |
| 289 | THE DERIVED SERIES THAT TWO SCREENS BOTH WANT |
| 542 | THE FOLD |
| 580 | THE FILE |
| 601 | · · the rows, as index ranges into the month axis |
| 610 | · · fold everything, then drop what never moved |
| 625 | · · the preamble |
| 652 | · · the columns |
| 672 | · · the table |
| 685 | · · the extremes |
| 715 | WHAT HAPPENED |
| 868 | THE NAMED EPISODES (0.7.5) |
| 1027 | · small helpers |

## Enum constants

| line | constant | says |
|---:|---|---|
| 52 | `YearBook.Kind.FLOW` | A monthly flow - the row's months ADDED. |
| 54 | `YearBook.Kind.LEVEL` | A stock - the value in the row's LAST month. |
| 56 | `YearBook.Kind.RATE` | A rate, a price or an index - the row's months AVERAGED. |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 59 | `YearBook.MONTHS_A_YEAR` | `12` |  |
| 60 | `YearBook.MONTHS_A_DECADE` | `120` |  |
| 230 | `YearBook.RULES` | `rules()` |  |
| 231 | `YearBook.PREFIXES` | `prefixRules()` |  |
| 468 | `YearBook.GDP_PARTS` | `{ "consumption", "investment", "government", "netExports" }` | GDP's four parts, as HistorySave names them (0.7.6): C, I, G and NX, in the order they stack. |
| 893 | `YearBook.EPISODE_MIN_MONTHS` | `3` | A run shorter than this many months is noise, and is not named - or shaded on the chart. |
| 896 | `YearBook.EPISODE_JOIN_MONTHS` | `6` | Two runs with fewer months of relief than this between them are one episode. |
| 899 | `YearBook.DEPRESSION_MONTHS` | `24` | A recession this many months long, or longer, is called a depression. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 280 | `final String name` |  |
| 281 | `final Kind kind` |  |
| 282 | `final double[] monthly` |  |
| 283 | `final String note` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 47 | 1075 | **type** `public final class YearBook` | The run, one line a year - for READING rather than for drawing. |
| 50 | 8 | **type** `public enum Kind` | How a column's months become one number. |
| 62 | 1 | `private YearBook()` |  |

### THE RULES (lines 64-269)

| line | len | member | says |
|---:|---:|---|---|
| 83 | 1 | **type** `private record Rule(Kind kind, String note)` | What a series IS, and one line saying what it means and in what unit. |
| 85 | 121 | `private static Map<String, Rule> rules()` |  |
| 207 | 1 | `private static void flow(Map<String, Rule> m, String k, String note)` |  |
| 208 | 1 | `private static void level(Map<String, Rule> m, String k, String note)` |  |
| 209 | 1 | `private static void rate(Map<String, Rule> m, String k, String note)` |  |
| 219 | 10 | `private static Map<String, Rule> prefixRules()` | The runtime families, matched on the part before the colon. |
| 241 | 4 | `public static Kind kindOf(String series)` | The rule for a series, or null if nobody has declared one. |
| 247 | 4 | `public static String noteOf(String series)` | What a series means, in one line, or null if nobody has said. |
| 252 | 8 | `private static Rule ruleFor(String series)` |  |
| 262 | 7 | `public static List<String> unruled(HistorySave history)` | Every series in this history that nobody has declared a rule for. |

### THE DERIVED COLUMNS (lines 270-288)

| line | len | member | says |
|---:|---:|---|---|
| 279 | 9 | **type** `private static final class Column` |  |
| 284 | 3 | `Column(String name, Kind kind, double[] monthly, String note)` _(in YearBook.Column)_ |  |

### THE DERIVED SERIES THAT TWO SCREENS BOTH WANT (lines 289-541)

| line | len | member | says |
|---:|---:|---|---|
| 321 | 12 | `public static double[] labourForce(HistorySave h)` | Who is actually available to work: the workforce less the people who are not looking. |
| 344 | 12 | `public static double[] filledPosts(HistorySave h)` | Posts with somebody in them - the labour force less the pool. |
| 358 | 11 | `public static double[] unemployment(HistorySave h)` | THE definition of the city's unemployment rate off a history. |
| 379 | 9 | `public static double[] averageWage(HistorySave h)` | THE definition of the average wage off a history: the wage bill over the posts that are FILLED. |
| 404 | 3 | `public static double[] realGdp(HistorySave h)` | Output in FOUNDING money, a month at a time: nominal GDP over the price index. |
| 423 | 3 | `public static double[] realGdpYear(HistorySave h)` | ...and a rolling YEAR of it, which is what the Reports page draws and what a recession is read off. |
| 428 | 14 | `private static double[] rollingYear(double[] monthly)` | Twelve months summed, ending at each month; NaN before the twelfth, and wherever the window holds a NaN. |
| 455 | 8 | `public static double[] real(HistorySave h, String key)` | Any money series in FOUNDING money, a month at a time: divided by the price index the way realGdp() is, so GDP's four parts and GDP itself are the same money and the layers under the line add up to it. |
| 477 | 3 | `public static double[] realYear(HistorySave h, String key)` | ...and a rolling YEAR of one of them in founding money (0.7.6), summed as realGdpYear() is - so a year of consumption, investment, government and net exports adds up to the year of real GDP it is part of. |
| 488 | 9 | `public static double[] inflation(HistorySave h)` | Inflation YEAR ON YEAR - the price index against its own reading twelve months earlier, the window PriceIndex uses. |
| 498 | 43 | `private static List<Column> columns(HistorySave h)` |  |

### THE FOLD (lines 542-579)

| line | len | member | says |
|---:|---:|---|---|
| 547 | 22 | `private static double fold(Column c, int from, int to)` | A row's value for a column, by that column's own rule. |
| 570 | 9 | `private static double worst(Column c, int from, int to, boolean high)` |  |

### THE FILE (lines 580-714)

| line | len | member | says |
|---:|---:|---|---|
| 584 | 1 | `public static String years(HistorySave h)` |  |
| 585 | 1 | `public static String decades(HistorySave h)` |  |
| 587 | 121 | `private static String write(HistorySave history, int span)` |  |
| 709 | 1 | `private static int bucket(int month, int span)` |  |
| 711 | 3 | `private static String mark(Kind k)` |  |

### WHAT HAPPENED (lines 715-867)

| line | len | member | says |
|---:|---:|---|---|
| 724 | 86 | `private static String whatHappened(HistorySave h, List<Column> cols)` |  |
| 812 | 1 | **type** `private interface Test` | A condition, how many months met it, where they were, and its worst reading. |
| 812 | 1 | `boolean holds(double v)` _(in YearBook.Test)_ |  |
| 815 | 14 | `private static List<int[]> runsOf(double[] series, Test test)` | Index ranges {first, last} of every unbroken run of months where the test held. |
| 830 | 23 | `private static void spell(StringBuilder out, String label, double[] series, List<Integer> axis, Test test, String what, boolean...` |  |
| 855 | 12 | `private static String spans(List<int[]> runs, List<Integer> axis)` | Consecutive months collapsed into ranges, and a long list cut off honestly. |

### THE NAMED EPISODES (0.7.5) (lines 868-1026)

| line | len | member | says |
|---:|---:|---|---|
| 914 | 1 | **type** `public record Episode(String kind, String name, int fromMonth, int toMonth, double worst)` | One named stretch of the city's life. |
| 924 | 40 | `public static List<Episode> episodes(HistorySave h)` | Every named episode in a history, oldest first. |
| 973 | 8 | `public static List<int[]> recessions(HistorySave h)` | The months to shade on a chart as recession, as {firstMonth, lastMonth} on the history's axis: every run where the rolling year of real output was below the year before it, of at least EPISODE_MIN_MONTHS - NOT joined,... |
| 983 | 9 | `private static double[] realGrowth(HistorySave h)` | The rolling year of real output against the year before it, as a fraction. |
| 994 | 9 | `private static double[] currencyMove(HistorySave h)` | The exchange rate as a multiple of itself a year before - above 2 is a currency that halved. |
| 1005 | 21 | `private static void named(List<Episode> found, List<Integer> axis, String kind, double[] series, Test test, boolean worstIsHigh...` | One row of the table: the runs of a condition, dropped, joined and named. |

### small helpers (lines 1027-1121)

| line | len | member | says |
|---:|---:|---|---|
| 1029 | 3 | `private static String at(List<Integer> axis, int i)` |  |
| 1033 | 5 | `private static boolean hasAny(double[] v)` |  |
| 1039 | 4 | `private static double firstReal(double[] v)` |  |
| 1044 | 4 | `private static double lastReal(double[] v)` |  |
| 1049 | 5 | `private static double sumOf(double[] v)` |  |
| 1055 | 5 | `private static double meanOf(double[] v)` |  |
| 1061 | 8 | `private static int argBest(double[] v, boolean high)` |  |
| 1070 | 8 | `private static int argFurthestFrom(double[] v, double anchor)` |  |
| 1079 | 3 | `private static String pad(String s, int width)` |  |
| 1083 | 3 | `private static String pct(double fraction)` |  |
| 1096 | 17 | `static String compact(double v)` | Three significant figures, and never a thousands separator. |
| 1114 | 7 | `private static String trim(String s)` |  |

