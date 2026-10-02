# YearBook.java - 1,469 lines · 74 methods · 18 constants · model

`ham/citybuildersim/YearBook.java` - generated 2026-10-02 by CodeMap; line numbers are as of that run.

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

**Uses:** [HistorySave](HistorySave.md) (27), [DecisionLog](DecisionLog.md) (9), [Currency](Currency.md) (5), [CityCalendar](CityCalendar.md) (5), [GameVersion](GameVersion.md) (2), [Formats](Formats.md) (2)

**Used by (12):** [BankScreen](BankScreen.md), [ChartCheck](ChartCheck.md), [ChartModel](ChartModel.md), [Game](Game.md), [GovernmentScreen](GovernmentScreen.md), [HistoryCheck](HistoryCheck.md), [HistoryScreen](HistoryScreen.md), [ReadPathCheck](ReadPathCheck.md), [TimeChart](TimeChart.md), [TradeScreen](TradeScreen.md), [UserInterface](UserInterface.md), [YearBookCheck](YearBookCheck.md)

## Sections

| line | section |
|---:|---|
| 64 | THE RULES |
| 282 | THE DERIVED COLUMNS |
| 301 | THE DERIVED SERIES THAT TWO SCREENS BOTH WANT |
| 554 | THE FOLD |
| 592 | THE TABLES, ONCE, FOR THE TEXT AND THE CSV (0.7.16) |
| 694 | THE FILE |
| 724 | · · the rows, as index ranges into the month axis |
| 733 | · · fold everything, then drop what never moved |
| 748 | · · the two tables, every cell as the text prints it |
| 782 | · · the preamble |
| 810 | · · the columns |
| 830 | · · the table |
| 834 | · · the extremes |
| 851 | WHAT HAPPENED |
| 1004 | THE NAMED EPISODES (0.7.5) |
| 1228 | · WHAT IS RUNNING NOW (0.7.37). City History's strip names the trouble |
| 1375 | · small helpers |

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
| 242 | `YearBook.RULES` | `rules()` |  |
| 243 | `YearBook.PREFIXES` | `prefixRules()` |  |
| 480 | `YearBook.GDP_PARTS` | `{ "consumption", "investment", "government", "netExports" }` | GDP's four parts, as HistorySave names them (0.7.6): C, I, G and NX, in the order they stack. |
| 1029 | `YearBook.EPISODE_MIN_MONTHS` | `3` | A run shorter than this many months is noise, and is not named - or shaded on the chart. |
| 1032 | `YearBook.EPISODE_JOIN_MONTHS` | `6` | Two runs with fewer months of relief than this between them are one episode. |
| 1035 | `YearBook.DEPRESSION_MONTHS` | `24` | A recession this many months long, or longer, is called a depression. |
| 1045 | `YearBook.FINANCIAL_EQUITY` | `0` | The bank's equity under this, in thousands, is a financial crisis: the bank has failed. |
| 1048 | `YearBook.RECESSION_GROWTH` | `0` | Real output over a rolling year against the year before, as a fraction, under this is a recession: under zero, a fall. |
| 1051 | `YearBook.CURRENCY_MOVE` | `2` | The exchange rate past this multiple of itself a year before is a currency crisis: the currency halved. |
| 1054 | `YearBook.INFLATION_EPISODE` | `.25` | Prices rising faster than this a year is an inflation. |
| 1057 | `YearBook.DEFLATION_EPISODE` | `-.10` | Prices falling faster than this a year (a negative rate) is a deflation. |
| 1060 | `YearBook.EPIDEMIC_SICK` | `.10` | More of the workforce off sick than this is an epidemic. |
| 1063 | `YearBook.TREASURY_CASH` | `0` | The treasury's cash under this, in thousands, is a treasury crisis: it is overdrawn. |
| 1066 | `YearBook.SLUMP_UNEMPLOYMENT` | `.20` | More of the labour force out of work than this is a slump. |
| 1240 | `YearBook.CHRONIC_MONTHS` | `120` | An episode still running after this many months is chronic: City History lists it after the others that are running, and leads with it only when it runs alone (0.7.37). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 292 | `final String name` |  |
| 293 | `final Kind kind` |  |
| 294 | `final double[] monthly` |  |
| 295 | `final String note` |  |
| 676 | `private final String text` |  |
| 677 | `private final Table table` |  |
| 678 | `private final Table within` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 47 | 1423 | **type** `public final class YearBook` | The run, one line a year - for READING rather than for drawing. |
| 50 | 8 | **type** `public enum Kind` | How a column's months become one number. |
| 62 | 1 | `private YearBook()` |  |

### THE RULES (lines 64-281)

| line | len | member | says |
|---:|---:|---|---|
| 83 | 1 | **type** `private record Rule(Kind kind, String note)` | What a series IS, and one line saying what it means and in what unit. |
| 93 | 125 | `private static Map<String, Rule> rules()` |  |
| 219 | 1 | `private static void flow(Map<String, Rule> m, String k, String note)` |  |
| 220 | 1 | `private static void level(Map<String, Rule> m, String k, String note)` |  |
| 221 | 1 | `private static void rate(Map<String, Rule> m, String k, String note)` |  |
| 231 | 10 | `private static Map<String, Rule> prefixRules()` | The runtime families, matched on the part before the colon. |
| 253 | 4 | `public static Kind kindOf(String series)` | The rule for a series, or null if nobody has declared one. |
| 259 | 4 | `public static String noteOf(String series)` | What a series means, in one line, or null if nobody has said. |
| 264 | 8 | `private static Rule ruleFor(String series)` |  |
| 274 | 7 | `public static List<String> unruled(HistorySave history)` | Every series in this history that nobody has declared a rule for. |

### THE DERIVED COLUMNS (lines 282-300)

| line | len | member | says |
|---:|---:|---|---|
| 291 | 9 | **type** `private static final class Column` |  |
| 296 | 3 | `Column(String name, Kind kind, double[] monthly, String note)` _(in YearBook.Column)_ |  |

### THE DERIVED SERIES THAT TWO SCREENS BOTH WANT (lines 301-553)

| line | len | member | says |
|---:|---:|---|---|
| 333 | 12 | `public static double[] labourForce(HistorySave h)` | Who is actually available to work: the workforce less the people who are not looking. |
| 356 | 12 | `public static double[] filledPosts(HistorySave h)` | Posts with somebody in them - the labour force less the pool. |
| 370 | 11 | `public static double[] unemployment(HistorySave h)` | THE definition of the city's unemployment rate off a history. |
| 391 | 9 | `public static double[] averageWage(HistorySave h)` | THE definition of the average wage off a history: the wage bill over the posts that are FILLED. |
| 416 | 3 | `public static double[] realGdp(HistorySave h)` | Output in FOUNDING money, a month at a time: nominal GDP over the price index. |
| 435 | 3 | `public static double[] realGdpYear(HistorySave h)` | ...and a rolling YEAR of it, which is what the Reports page draws and what a recession is read off. |
| 440 | 14 | `private static double[] rollingYear(double[] monthly)` | Twelve months summed, ending at each month; NaN before the twelfth, and wherever the window holds a NaN. |
| 467 | 8 | `public static double[] real(HistorySave h, String key)` | Any money series in FOUNDING money, a month at a time: divided by the price index the way realGdp() is, so GDP's four parts and GDP itself are the same money and the layers under the line add up to it. |
| 489 | 3 | `public static double[] realYear(HistorySave h, String key)` | ...and a rolling YEAR of one of them in founding money (0.7.6), summed as realGdpYear() is - so a year of consumption, investment, government and net exports adds up to the year of real GDP it is part of. |
| 500 | 9 | `public static double[] inflation(HistorySave h)` | Inflation YEAR ON YEAR - the price index against its own reading twelve months earlier, the window PriceIndex uses. |
| 510 | 43 | `private static List<Column> columns(HistorySave h)` |  |

### THE FOLD (lines 554-591)

| line | len | member | says |
|---:|---:|---|---|
| 559 | 22 | `private static double fold(Column c, int from, int to)` | A row's value for a column, by that column's own rule. |
| 582 | 9 | `private static double worst(Column c, int from, int to, boolean high)` |  |

### THE TABLES, ONCE, FOR THE TEXT AND THE CSV (0.7.16) (lines 592-693)

| line | len | member | says |
|---:|---:|---|---|
| 620 | 25 | **type** `public record Table(List<String> header, List<List<String>> rows)` | One of the book's tables: its column names and its rows, each cell the string the text prints - an empty string where the text is blank. |
| 622 | 6 | `{ ... }` _(in YearBook.Table)_ |  |
| 630 | 6 | `String tabbed()` _(in YearBook.Table)_ | As the text prints it: a tab between cells and a newline after every line, the header first. |
| 638 | 6 | `public String csv()` _(in YearBook.Table)_ | As RFC 4180 writes it: a comma between fields and CRLF after every record, the header first. |
| 646 | 7 | `private static void csvRecord(StringBuilder out, List<String> fields)` |  |
| 659 | 5 | `static String csvField(String s)` | One CSV field, quoted only when it has to be: a comma, a double quote or a line break inside it, and a quote inside is doubled. |
| 675 | 18 | **type** `public static final class Book` | A book, built once: the text the reader gets and the two tables in it. |
| 680 | 5 | `private Book(String text, Table table, Table within)` _(in YearBook.Book)_ |  |
| 687 | 1 | `public String text()` _(in YearBook.Book)_ | The whole text file, exactly what years() and decades() return. |
| 689 | 1 | `public Table table()` _(in YearBook.Book)_ | The YEARS (or DECADES) table: the row, mo, n and every column shown. |
| 691 | 1 | `public Table within()` _(in YearBook.Book)_ | The WITHIN table: the row, then each [~] column's worst and best month, .lo and .hi. |

### THE FILE (lines 694-850)

| line | len | member | says |
|---:|---:|---|---|
| 699 | 1 | `public static String years(HistorySave h, Currency money)` | The book a year to the row, in the city's own money's name (0.7.10). |
| 701 | 1 | `public static String decades(HistorySave h, Currency money)` | ...and a decade to the row. |
| 704 | 1 | `public static Book yearBook(HistorySave h, Currency money)` | The year book whole - its text and its tables - for an export that writes both (0.7.16). |
| 706 | 1 | `public static Book decadeBook(HistorySave h, Currency money)` | ...and the decade book. |
| 708 | 136 | `private static Book write(HistorySave history, int span, Currency money)` |  |
| 845 | 1 | `private static int bucket(int month, int span)` |  |
| 847 | 3 | `private static String mark(Kind k)` |  |

### WHAT HAPPENED (lines 851-1003)

| line | len | member | says |
|---:|---:|---|---|
| 860 | 86 | `private static String whatHappened(HistorySave h, List<Column> cols)` |  |
| 948 | 1 | **type** `private interface Test` | A condition, how many months met it, where they were, and its worst reading. |
| 948 | 1 | `boolean holds(double v)` _(in YearBook.Test)_ |  |
| 951 | 14 | `private static List<int[]> runsOf(double[] series, Test test)` | Index ranges {first, last} of every unbroken run of months where the test held. |
| 966 | 23 | `private static void spell(StringBuilder out, String label, double[] series, List<Integer> axis, Test test, String what, boolean...` |  |
| 991 | 12 | `private static String spans(List<int[]> runs, List<Integer> axis)` | Consecutive months collapsed into ranges, and a long list cut off honestly. |

### THE NAMED EPISODES (0.7.5) (lines 1004-1227)

| line | len | member | says |
|---:|---:|---|---|
| 1083 | 4 | **type** `public record Episode(String kind, String name, int fromMonth, int toMonth, double worst, int worstMonth)` | One named stretch of the city's life. |
| 1085 | 1 | `public int months()` _(in YearBook.Episode)_ | How many months it ran, relief inside it included. |
| 1096 | 40 | `public static List<Episode> episodes(HistorySave h)` | Every named episode in a history, oldest first. |
| 1145 | 8 | `public static List<int[]> recessions(HistorySave h)` | The months to shade on a chart as recession, as {firstMonth, lastMonth} on the history's axis: every run where the rolling year of real output was below the year before it, of at least EPISODE_MIN_MONTHS - NOT joined,... |
| 1167 | 6 | **type** `public record Band(int fromMonth, int toMonth, double depth, int depthMonth, Episode episode)` | One recession band as the chart labels it (0.7.23): its months - one of recessions(), exactly - how deep real output fell inside it and in which month, and the recession or depression it belongs to, whose name it carr... |
| 1169 | 1 | `public int months()` _(in YearBook.Band)_ | How many months the band covers. |
| 1171 | 1 | `public String name()` _(in YearBook.Band)_ | The name it carries on the chart: its episode's. |
| 1175 | 23 | `public static List<Band> recessionBands(HistorySave h)` | Every recession band, oldest first, with its depth and its episode: recessions(), told about (0.7.23). |
| 1205 | 16 | `public static String trigger(String kind)` | The rule that names an episode of this kind, in words (0.7.23) - what the chart says a band or a span is, from the table's own thresholds. |
| 1223 | 4 | `public static String triggerFooter()` | The two rules every kind shares, in words: how long it must last, and what joins two into one. |

### WHAT IS RUNNING NOW (0.7.37). City History's strip names the trouble (lines 1228-1374)

| line | len | member | says |
|---:|---:|---|---|
| 1249 | 6 | `public static boolean isSevere(String kind)` | Whether an episode of this kind is a crisis rather than a watch (0.7.37): the bank failed, the currency halved, the treasury overdrawn, or a recession long enough to be a depression. |
| 1257 | 6 | `public static int monthsIn(List<Episode> episodes, String kind)` | How many months the episodes of one kind ran, all told - City History's hard times by kind (0.7.37). |
| 1265 | 3 | `public static boolean isChronic(Episode e)` | Whether an episode has run CHRONIC_MONTHS or more, relief inside it included (0.7.37). |
| 1276 | 5 | `public static List<Episode> running(HistorySave h)` | The episodes still running - those whose last month is the history's last, which the history has not closed - in the order City History names them (0.7.37): a chronic one after every other; then a crisis before a watc... |
| 1283 | 13 | `public static List<Episode> running(List<Episode> episodes, int lastMonth)` | ...from a list already read, for a screen that has episodes() in hand: those ending at `lastMonth`, in that order. |
| 1298 | 6 | `public static boolean worstIsHigh(String kind)` | Whether an episode of this kind is at its worst when its figure is HIGHEST - a peak - rather than lowest - a depth. |
| 1310 | 14 | `public static String worstWords(String kind, double worst)` | An episode's worst reading, in words, from the episode's own record (0.7.23): "real output 3.1% below the year before", "inflation at 31% a year". |
| 1330 | 9 | `public static double[] realGrowth(HistorySave h)` | The rolling year of real output against the year before it, as a fraction; NaN until two years are recorded. |
| 1341 | 9 | `private static double[] currencyMove(HistorySave h)` | The exchange rate as a multiple of itself a year before - above 2 is a currency that halved. |
| 1352 | 22 | `private static void named(List<Episode> found, List<Integer> axis, String kind, double[] series, Test test, boolean worstIsHigh...` | One row of the table: the runs of a condition, dropped, joined and named. |

### small helpers (lines 1375-1469)

| line | len | member | says |
|---:|---:|---|---|
| 1377 | 3 | `private static String at(List<Integer> axis, int i)` |  |
| 1381 | 5 | `private static boolean hasAny(double[] v)` |  |
| 1387 | 4 | `private static double firstReal(double[] v)` |  |
| 1392 | 4 | `private static double lastReal(double[] v)` |  |
| 1397 | 5 | `private static double sumOf(double[] v)` |  |
| 1403 | 5 | `private static double meanOf(double[] v)` |  |
| 1409 | 8 | `private static int argBest(double[] v, boolean high)` |  |
| 1418 | 8 | `private static int argFurthestFrom(double[] v, double anchor)` |  |
| 1427 | 3 | `private static String pad(String s, int width)` |  |
| 1431 | 3 | `private static String pct(double fraction)` |  |
| 1444 | 17 | `static String compact(double v)` | Three significant figures, and never a thousands separator. |
| 1462 | 7 | `private static String trim(String s)` |  |

