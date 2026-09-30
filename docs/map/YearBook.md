# YearBook.java - 1,253 lines · 61 methods · 9 constants · model

`ham/citybuildersim/YearBook.java` - generated 2026-09-30 by CodeMap; line numbers are as of that run.

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

**Uses:** [HistorySave](HistorySave.md) (25), [Currency](Currency.md) (5), [CityCalendar](CityCalendar.md) (5), [GameVersion](GameVersion.md) (2)

**Used by (4):** [Game](Game.md), [HistoryCheck](HistoryCheck.md), [HistoryScreen](HistoryScreen.md), [YearBookCheck](YearBookCheck.md)

## Sections

| line | section |
|---:|---|
| 64 | THE RULES |
| 278 | THE DERIVED COLUMNS |
| 297 | THE DERIVED SERIES THAT TWO SCREENS BOTH WANT |
| 550 | THE FOLD |
| 588 | THE TABLES, ONCE, FOR THE TEXT AND THE CSV (0.7.16) |
| 690 | THE FILE |
| 720 | · · the rows, as index ranges into the month axis |
| 729 | · · fold everything, then drop what never moved |
| 744 | · · the two tables, every cell as the text prints it |
| 778 | · · the preamble |
| 806 | · · the columns |
| 826 | · · the table |
| 830 | · · the extremes |
| 847 | WHAT HAPPENED |
| 1000 | THE NAMED EPISODES (0.7.5) |
| 1159 | · small helpers |

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
| 91 | `YearBook.MONEY` | `"{the city's money}"` | Where a note names the city's money. |
| 238 | `YearBook.RULES` | `rules()` |  |
| 239 | `YearBook.PREFIXES` | `prefixRules()` |  |
| 476 | `YearBook.GDP_PARTS` | `{ "consumption", "investment", "government", "netExports" }` | GDP's four parts, as HistorySave names them (0.7.6): C, I, G and NX, in the order they stack. |
| 1025 | `YearBook.EPISODE_MIN_MONTHS` | `3` | A run shorter than this many months is noise, and is not named - or shaded on the chart. |
| 1028 | `YearBook.EPISODE_JOIN_MONTHS` | `6` | Two runs with fewer months of relief than this between them are one episode. |
| 1031 | `YearBook.DEPRESSION_MONTHS` | `24` | A recession this many months long, or longer, is called a depression. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 288 | `final String name` |  |
| 289 | `final Kind kind` |  |
| 290 | `final double[] monthly` |  |
| 291 | `final String note` |  |
| 672 | `private final String text` |  |
| 673 | `private final Table table` |  |
| 674 | `private final Table within` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 47 | 1207 | **type** `public final class YearBook` | The run, one line a year - for READING rather than for drawing. |
| 50 | 8 | **type** `public enum Kind` | How a column's months become one number. |
| 62 | 1 | `private YearBook()` |  |

### THE RULES (lines 64-277)

| line | len | member | says |
|---:|---:|---|---|
| 83 | 1 | **type** `private record Rule(Kind kind, String note)` | What a series IS, and one line saying what it means and in what unit. |
| 93 | 121 | `private static Map<String, Rule> rules()` |  |
| 215 | 1 | `private static void flow(Map<String, Rule> m, String k, String note)` |  |
| 216 | 1 | `private static void level(Map<String, Rule> m, String k, String note)` |  |
| 217 | 1 | `private static void rate(Map<String, Rule> m, String k, String note)` |  |
| 227 | 10 | `private static Map<String, Rule> prefixRules()` | The runtime families, matched on the part before the colon. |
| 249 | 4 | `public static Kind kindOf(String series)` | The rule for a series, or null if nobody has declared one. |
| 255 | 4 | `public static String noteOf(String series)` | What a series means, in one line, or null if nobody has said. |
| 260 | 8 | `private static Rule ruleFor(String series)` |  |
| 270 | 7 | `public static List<String> unruled(HistorySave history)` | Every series in this history that nobody has declared a rule for. |

### THE DERIVED COLUMNS (lines 278-296)

| line | len | member | says |
|---:|---:|---|---|
| 287 | 9 | **type** `private static final class Column` |  |
| 292 | 3 | `Column(String name, Kind kind, double[] monthly, String note)` _(in YearBook.Column)_ |  |

### THE DERIVED SERIES THAT TWO SCREENS BOTH WANT (lines 297-549)

| line | len | member | says |
|---:|---:|---|---|
| 329 | 12 | `public static double[] labourForce(HistorySave h)` | Who is actually available to work: the workforce less the people who are not looking. |
| 352 | 12 | `public static double[] filledPosts(HistorySave h)` | Posts with somebody in them - the labour force less the pool. |
| 366 | 11 | `public static double[] unemployment(HistorySave h)` | THE definition of the city's unemployment rate off a history. |
| 387 | 9 | `public static double[] averageWage(HistorySave h)` | THE definition of the average wage off a history: the wage bill over the posts that are FILLED. |
| 412 | 3 | `public static double[] realGdp(HistorySave h)` | Output in FOUNDING money, a month at a time: nominal GDP over the price index. |
| 431 | 3 | `public static double[] realGdpYear(HistorySave h)` | ...and a rolling YEAR of it, which is what the Reports page draws and what a recession is read off. |
| 436 | 14 | `private static double[] rollingYear(double[] monthly)` | Twelve months summed, ending at each month; NaN before the twelfth, and wherever the window holds a NaN. |
| 463 | 8 | `public static double[] real(HistorySave h, String key)` | Any money series in FOUNDING money, a month at a time: divided by the price index the way realGdp() is, so GDP's four parts and GDP itself are the same money and the layers under the line add up to it. |
| 485 | 3 | `public static double[] realYear(HistorySave h, String key)` | ...and a rolling YEAR of one of them in founding money (0.7.6), summed as realGdpYear() is - so a year of consumption, investment, government and net exports adds up to the year of real GDP it is part of. |
| 496 | 9 | `public static double[] inflation(HistorySave h)` | Inflation YEAR ON YEAR - the price index against its own reading twelve months earlier, the window PriceIndex uses. |
| 506 | 43 | `private static List<Column> columns(HistorySave h)` |  |

### THE FOLD (lines 550-587)

| line | len | member | says |
|---:|---:|---|---|
| 555 | 22 | `private static double fold(Column c, int from, int to)` | A row's value for a column, by that column's own rule. |
| 578 | 9 | `private static double worst(Column c, int from, int to, boolean high)` |  |

### THE TABLES, ONCE, FOR THE TEXT AND THE CSV (0.7.16) (lines 588-689)

| line | len | member | says |
|---:|---:|---|---|
| 616 | 25 | **type** `public record Table(List<String> header, List<List<String>> rows)` | One of the book's tables: its column names and its rows, each cell the string the text prints - an empty string where the text is blank. |
| 618 | 6 | `{ ... }` _(in YearBook.Table)_ |  |
| 626 | 6 | `String tabbed()` _(in YearBook.Table)_ | As the text prints it: a tab between cells and a newline after every line, the header first. |
| 634 | 6 | `public String csv()` _(in YearBook.Table)_ | As RFC 4180 writes it: a comma between fields and CRLF after every record, the header first. |
| 642 | 7 | `private static void csvRecord(StringBuilder out, List<String> fields)` |  |
| 655 | 5 | `static String csvField(String s)` | One CSV field, quoted only when it has to be: a comma, a double quote or a line break inside it, and a quote inside is doubled. |
| 671 | 18 | **type** `public static final class Book` | A book, built once: the text the reader gets and the two tables in it. |
| 676 | 5 | `private Book(String text, Table table, Table within)` _(in YearBook.Book)_ |  |
| 683 | 1 | `public String text()` _(in YearBook.Book)_ | The whole text file, exactly what years() and decades() return. |
| 685 | 1 | `public Table table()` _(in YearBook.Book)_ | The YEARS (or DECADES) table: the row, mo, n and every column shown. |
| 687 | 1 | `public Table within()` _(in YearBook.Book)_ | The WITHIN table: the row, then each [~] column's worst and best month, .lo and .hi. |

### THE FILE (lines 690-846)

| line | len | member | says |
|---:|---:|---|---|
| 695 | 1 | `public static String years(HistorySave h, Currency money)` | The book a year to the row, in the city's own money's name (0.7.10). |
| 697 | 1 | `public static String decades(HistorySave h, Currency money)` | ...and a decade to the row. |
| 700 | 1 | `public static Book yearBook(HistorySave h, Currency money)` | The year book whole - its text and its tables - for an export that writes both (0.7.16). |
| 702 | 1 | `public static Book decadeBook(HistorySave h, Currency money)` | ...and the decade book. |
| 704 | 136 | `private static Book write(HistorySave history, int span, Currency money)` |  |
| 841 | 1 | `private static int bucket(int month, int span)` |  |
| 843 | 3 | `private static String mark(Kind k)` |  |

### WHAT HAPPENED (lines 847-999)

| line | len | member | says |
|---:|---:|---|---|
| 856 | 86 | `private static String whatHappened(HistorySave h, List<Column> cols)` |  |
| 944 | 1 | **type** `private interface Test` | A condition, how many months met it, where they were, and its worst reading. |
| 944 | 1 | `boolean holds(double v)` _(in YearBook.Test)_ |  |
| 947 | 14 | `private static List<int[]> runsOf(double[] series, Test test)` | Index ranges {first, last} of every unbroken run of months where the test held. |
| 962 | 23 | `private static void spell(StringBuilder out, String label, double[] series, List<Integer> axis, Test test, String what, boolean...` |  |
| 987 | 12 | `private static String spans(List<int[]> runs, List<Integer> axis)` | Consecutive months collapsed into ranges, and a long list cut off honestly. |

### THE NAMED EPISODES (0.7.5) (lines 1000-1158)

| line | len | member | says |
|---:|---:|---|---|
| 1046 | 1 | **type** `public record Episode(String kind, String name, int fromMonth, int toMonth, double worst)` | One named stretch of the city's life. |
| 1056 | 40 | `public static List<Episode> episodes(HistorySave h)` | Every named episode in a history, oldest first. |
| 1105 | 8 | `public static List<int[]> recessions(HistorySave h)` | The months to shade on a chart as recession, as {firstMonth, lastMonth} on the history's axis: every run where the rolling year of real output was below the year before it, of at least EPISODE_MIN_MONTHS - NOT joined,... |
| 1115 | 9 | `private static double[] realGrowth(HistorySave h)` | The rolling year of real output against the year before it, as a fraction. |
| 1126 | 9 | `private static double[] currencyMove(HistorySave h)` | The exchange rate as a multiple of itself a year before - above 2 is a currency that halved. |
| 1137 | 21 | `private static void named(List<Episode> found, List<Integer> axis, String kind, double[] series, Test test, boolean worstIsHigh...` | One row of the table: the runs of a condition, dropped, joined and named. |

### small helpers (lines 1159-1253)

| line | len | member | says |
|---:|---:|---|---|
| 1161 | 3 | `private static String at(List<Integer> axis, int i)` |  |
| 1165 | 5 | `private static boolean hasAny(double[] v)` |  |
| 1171 | 4 | `private static double firstReal(double[] v)` |  |
| 1176 | 4 | `private static double lastReal(double[] v)` |  |
| 1181 | 5 | `private static double sumOf(double[] v)` |  |
| 1187 | 5 | `private static double meanOf(double[] v)` |  |
| 1193 | 8 | `private static int argBest(double[] v, boolean high)` |  |
| 1202 | 8 | `private static int argFurthestFrom(double[] v, double anchor)` |  |
| 1211 | 3 | `private static String pad(String s, int width)` |  |
| 1215 | 3 | `private static String pct(double fraction)` |  |
| 1228 | 17 | `static String compact(double v)` | Three significant figures, and never a thousands separator. |
| 1246 | 7 | `private static String trim(String s)` |  |

