# YearBook.java - 1,675 lines · 85 methods · 18 constants · model

`ham/citybuildersim/YearBook.java` - generated 2026-11-10 by CodeMap; line numbers are as of that run.

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
> 
> PAST FIVE HUNDRED YEARS THE HISTORY IS A YEAR TO AN ENTRY (0.7.55;
> HistorySave.foldOldYears()), folded by THE RULES below, so a folded year
> is one entry that is already this book's row for it. Everything here that
> counted entries as months counts HistorySave.monthsIn() instead - a row's
> n, a rate's mean, a spell's months, an episode's length - and everything
> that looked twelve entries back looks twelve MONTHS back
> (HistorySave.back()); a flow reads a month at a time (aligned()), so it is
> weighted by its months where it is added. A folded year has no single
> months, so the WITHIN block leaves its cells blank.

**Uses:** [HistorySave](HistorySave.md) (49), [DecisionLog](DecisionLog.md) (9), [Currency](Currency.md) (5), [CityCalendar](CityCalendar.md) (5), [PriceIndex](PriceIndex.md) (2), [ResearchCatalog](ResearchCatalog.md) (2), [Expectations](Expectations.md) (2), [WorldMoneys](WorldMoneys.md) (2), [GameVersion](GameVersion.md) (2), [Formats](Formats.md) (2), [Bank](Bank.md) (1), [Nations](Nations.md) (1), [Health](Health.md) (1)

**Used by (23):** [BankScreen](BankScreen.md), [ChartCheck](ChartCheck.md), [ChartModel](ChartModel.md), [CityMeasures](CityMeasures.md), [ClockCheck](ClockCheck.md), [Game](Game.md), [GovernmentScreen](GovernmentScreen.md), [HistoryCheck](HistoryCheck.md), [HistorySave](HistorySave.md), [HistoryScreen](HistoryScreen.md), [InterestAbroadCheck](InterestAbroadCheck.md), [KeptBench](KeptBench.md), [OilView](OilView.md), [PolicyScreen](PolicyScreen.md), [RankingsCheck](RankingsCheck.md), [ReadPathCheck](ReadPathCheck.md), [ResearchViewCheck](ResearchViewCheck.md), [ScaleCheck](ScaleCheck.md), [TimeChart](TimeChart.md), [TradeScreen](TradeScreen.md), [UserInterface](UserInterface.md), [WorldMoneysCheck](WorldMoneysCheck.md), [YearBookCheck](YearBookCheck.md)

## Sections

| line | section |
|---:|---|
| 74 | THE RULES |
| 343 | THE DERIVED COLUMNS |
| 362 | THE DERIVED SERIES THAT TWO SCREENS BOTH WANT |
| 675 | THE FOLD |
| 732 | THE TABLES, ONCE, FOR THE TEXT AND THE CSV (0.7.16) |
| 834 | THE FILE |
| 864 | · · the rows, as index ranges into the month axis |
| 873 | · · fold everything, then drop what never moved |
| 888 | · · the two tables, every cell as the text prints it |
| 922 | · · the preamble |
| 957 | · · the columns |
| 977 | · · the table |
| 981 | · · the extremes |
| 998 | WHAT HAPPENED |
| 1152 | THE NAMED EPISODES (0.7.5) |
| 1379 | · WHAT IS RUNNING NOW (0.7.37). City History's strip names the trouble |
| 1566 | · small helpers |

## Enum constants

| line | constant | says |
|---:|---|---|
| 62 | `YearBook.Kind.FLOW` | A monthly flow - the row's months ADDED. |
| 64 | `YearBook.Kind.LEVEL` | A stock - the value in the row's LAST month. |
| 66 | `YearBook.Kind.RATE` | A rate, a price or an index - the row's months AVERAGED. |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 69 | `YearBook.MONTHS_A_YEAR` | `12` |  |
| 70 | `YearBook.MONTHS_A_DECADE` | `120` |  |
| 101 | `YearBook.MONEY` | `"{the city's money}"` | Where a note names the city's money. |
| 303 | `YearBook.RULES` | `rules()` |  |
| 304 | `YearBook.PREFIXES` | `prefixRules()` |  |
| 589 | `YearBook.GDP_PARTS` | `{ "consumption", "investment", "government", "netExports" }` | GDP's four parts, as HistorySave names them (0.7.6): C, I, G and NX, in the order they stack. |
| 1177 | `YearBook.EPISODE_MIN_MONTHS` | `3` | A run shorter than this many months is noise, and is not named - or shaded on the chart. |
| 1180 | `YearBook.EPISODE_JOIN_MONTHS` | `6` | Two runs with fewer months of relief than this between them are one episode. |
| 1183 | `YearBook.DEPRESSION_MONTHS` | `24` | A recession this many months long, or longer, is called a depression. |
| 1195 | `YearBook.FINANCIAL_EQUITY` | `0` | The bank's equity under this, in thousands, is a financial crisis: the bank has failed. |
| 1198 | `YearBook.RECESSION_GROWTH` | `0` | Real output over a rolling year against the year before, as a fraction, under this is a recession: under zero, a fall. |
| 1201 | `YearBook.CURRENCY_MOVE` | `2` | The exchange rate past this multiple of itself a year before is a currency crisis: the currency halved. |
| 1204 | `YearBook.INFLATION_EPISODE` | `.25` | Prices rising faster than this a year is an inflation. |
| 1207 | `YearBook.DEFLATION_EPISODE` | `-.10` | Prices falling faster than this a year (a negative rate) is a deflation. |
| 1210 | `YearBook.EPIDEMIC_OUTBREAK` | `Health.OUTBREAK_FLOOR` | An epidemic is named while an outbreak runs - more of the workforce off sick than its care explains - not while a city short of care is as sick as it always is (A8). |
| 1213 | `YearBook.TREASURY_CASH` | `0` | The treasury's cash under this, in thousands, is a treasury crisis: it is overdrawn. |
| 1216 | `YearBook.SLUMP_UNEMPLOYMENT` | `.20` | More of the labour force out of work than this is a slump. |
| 1393 | `YearBook.CHRONIC_MONTHS` | `120` | An episode still running after this many months is chronic: City History lists it after the others that are running, and leads with it only when it runs alone (0.7.37). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 353 | `final String name` |  |
| 354 | `final Kind kind` |  |
| 355 | `final double[] monthly` |  |
| 356 | `final String note` |  |
| 816 | `private final String text` |  |
| 817 | `private final Table table` |  |
| 818 | `private final Table within` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 57 | 1619 | **type** `public final class YearBook` | The run, one line a year - for READING rather than for drawing. |
| 60 | 8 | **type** `public enum Kind` | How a column's months become one number. |
| 72 | 1 | `private YearBook()` |  |

### THE RULES (lines 74-342)

| line | len | member | says |
|---:|---:|---|---|
| 93 | 1 | **type** `private record Rule(Kind kind, String note)` | What a series IS, and one line saying what it means and in what unit. |
| 103 | 176 | `private static Map<String, Rule> rules()` |  |
| 280 | 1 | `private static void flow(Map<String, Rule> m, String k, String note)` |  |
| 281 | 1 | `private static void level(Map<String, Rule> m, String k, String note)` |  |
| 282 | 1 | `private static void rate(Map<String, Rule> m, String k, String note)` |  |
| 292 | 10 | `private static Map<String, Rule> prefixRules()` | The runtime families, matched on the part before the colon. |
| 314 | 4 | `public static Kind kindOf(String series)` | The rule for a series, or null if nobody has declared one. |
| 320 | 4 | `public static String noteOf(String series)` | What a series means, in one line, or null if nobody has said. |
| 325 | 8 | `private static Rule ruleFor(String series)` |  |
| 335 | 7 | `public static List<String> unruled(HistorySave history)` | Every series in this history that nobody has declared a rule for. |

### THE DERIVED COLUMNS (lines 343-361)

| line | len | member | says |
|---:|---:|---|---|
| 352 | 9 | **type** `private static final class Column` |  |
| 357 | 3 | `Column(String name, Kind kind, double[] monthly, String note)` _(in YearBook.Column)_ |  |

### THE DERIVED SERIES THAT TWO SCREENS BOTH WANT (lines 362-674)

| line | len | member | says |
|---:|---:|---|---|
| 394 | 3 | `public static double[] labourForce(HistorySave h)` | Who is actually available to work: the workforce less the people who are not looking. |
| 399 | 9 | `static double[] labourForce(double[] workforce, double[] students, double[] prisoners)` | labourForce() off the three series, whole or a tail of them alike (UP2): one definition. |
| 419 | 3 | `public static double[] filledPosts(HistorySave h)` | Posts with somebody in them - the labour force less the pool. |
| 424 | 9 | `static double[] filledPosts(double[] force, double[] jobs, double[] pool)` | filledPosts() off the labour force, the posts and the pool, whole or a stretch of them alike (LB3, CityMeasures at a month): one definition. |
| 435 | 3 | `public static double[] unemployment(HistorySave h)` | THE definition of the city's unemployment rate off a history. |
| 444 | 4 | `public static double[] unemploymentTail(HistorySave h, int n)` | unemployment()'s last {@code n} entries and no more of it (UP2), value for value: every input is read a month at a time, so the tail of the rate is the rate off the tails of its inputs. |
| 450 | 8 | `static double[] unemployment(double[] force, double[] jobs, double[] pool)` | unemployment() off the labour force, the posts and the pool, whole or a tail of them alike (UP2): one definition. |
| 468 | 3 | `public static double[] averageWage(HistorySave h)` | THE definition of the average wage off a history: the wage bill over the posts that are FILLED. |
| 473 | 7 | `static double[] averageWage(double[] bill, double[] filled)` | averageWage() off the wage bill and the posts filled, whole or a stretch of them alike (LB3, CityMeasures at a month): one definition. |
| 497 | 3 | `public static double[] realGdp(HistorySave h)` | Output in FOUNDING money, a month at a time: nominal GDP over the price index. |
| 516 | 3 | `public static double[] realGdpYear(HistorySave h)` | ...and a rolling YEAR of it, which is what the Reports page draws and what a recession is read off. |
| 529 | 3 | `private static double[] rollingYear(double[] monthly, HistorySave h)` | Twelve months summed, ending at each month; NaN before the twelfth, and wherever the window holds a NaN. |
| 540 | 19 | `private static double[] rollingYear(double[] monthly, int base, int outFirst, HistorySave h)` | rollingYear() for the axis entries {@code outFirst} on, off a series that begins at entry {@code base} (UP2): {@code monthly[x]} is axis entry base + x, and the result's [x] is entry outFirst + x. |
| 572 | 3 | `public static double[] real(HistorySave h, String key)` | Any money series in FOUNDING money, a month at a time: divided by the price index the way realGdp() is, so GDP's four parts and GDP itself are the same money and the layers under the line add up to it. |
| 577 | 7 | `private static double[] real(double[] nominal, double[] index)` | real() off the two series, whole or a tail of them alike (UP2): one definition. |
| 598 | 3 | `public static double[] realYear(HistorySave h, String key)` | ...and a rolling YEAR of one of them in founding money (0.7.6), summed as realGdpYear() is - so a year of consumption, investment, government and net exports adds up to the year of real GDP it is part of. |
| 609 | 10 | `public static double[] inflation(HistorySave h)` | Inflation YEAR ON YEAR - the price index against its own reading twelve months earlier, the window PriceIndex uses. |
| 627 | 3 | `public static double componentInflation(HistorySave h, int component)` | One component's inflation over the last year (0.7.45; PriceIndex COMPONENTS order): its chained level now against twelve months before, off the history's line for it - NaN until a year of the line is recorded, which o... |
| 631 | 43 | `private static List<Column> columns(HistorySave h)` |  |

### THE FOLD (lines 675-731)

| line | len | member | says |
|---:|---:|---|---|
| 688 | 25 | `private static double fold(Column c, int from, int to, HistorySave h)` | A row's value for a column, by that column's own rule. |
| 715 | 5 | `private static int monthsIn(HistorySave h, int from, int to)` | The months axis entries from..to hold (0.7.55): a row's n. |
| 722 | 9 | `private static double worst(Column c, int from, int to, boolean high, HistorySave h)` | A row's worst or best SINGLE month: a folded year has none, so it is passed over (0.7.55). |

### THE TABLES, ONCE, FOR THE TEXT AND THE CSV (0.7.16) (lines 732-833)

| line | len | member | says |
|---:|---:|---|---|
| 760 | 25 | **type** `public record Table(List<String> header, List<List<String>> rows)` | One of the book's tables: its column names and its rows, each cell the string the text prints - an empty string where the text is blank. |
| 762 | 6 | `{ ... }` _(in YearBook.Table)_ |  |
| 770 | 6 | `String tabbed()` _(in YearBook.Table)_ | As the text prints it: a tab between cells and a newline after every line, the header first. |
| 778 | 6 | `public String csv()` _(in YearBook.Table)_ | As RFC 4180 writes it: a comma between fields and CRLF after every record, the header first. |
| 786 | 7 | `private static void csvRecord(StringBuilder out, List<String> fields)` |  |
| 799 | 5 | `static String csvField(String s)` | One CSV field, quoted only when it has to be: a comma, a double quote or a line break inside it, and a quote inside is doubled. |
| 815 | 18 | **type** `public static final class Book` | A book, built once: the text the reader gets and the two tables in it. |
| 820 | 5 | `private Book(String text, Table table, Table within)` _(in YearBook.Book)_ |  |
| 827 | 1 | `public String text()` _(in YearBook.Book)_ | The whole text file, exactly what years() and decades() return. |
| 829 | 1 | `public Table table()` _(in YearBook.Book)_ | The YEARS (or DECADES) table: the row, mo, n and every column shown. |
| 831 | 1 | `public Table within()` _(in YearBook.Book)_ | The WITHIN table: the row, then each [~] column's worst and best month, .lo and .hi. |

### THE FILE (lines 834-997)

| line | len | member | says |
|---:|---:|---|---|
| 839 | 1 | `public static String years(HistorySave h, Currency money)` | The book a year to the row, in the city's own money's name (0.7.10). |
| 841 | 1 | `public static String decades(HistorySave h, Currency money)` | ...and a decade to the row. |
| 844 | 1 | `public static Book yearBook(HistorySave h, Currency money)` | The year book whole - its text and its tables - for an export that writes both (0.7.16). |
| 846 | 1 | `public static Book decadeBook(HistorySave h, Currency money)` | ...and the decade book. |
| 848 | 143 | `private static Book write(HistorySave history, int span, Currency money)` |  |
| 992 | 1 | `private static int bucket(int month, int span)` |  |
| 994 | 3 | `private static String mark(Kind k)` |  |

### WHAT HAPPENED (lines 998-1151)

| line | len | member | says |
|---:|---:|---|---|
| 1007 | 86 | `private static String whatHappened(HistorySave h, List<Column> cols)` |  |
| 1095 | 1 | **type** `private interface Test` | A condition, how many months met it, where they were, and its worst reading. |
| 1095 | 1 | `boolean holds(double v)` _(in YearBook.Test)_ |  |
| 1098 | 14 | `private static List<int[]> runsOf(double[] series, Test test)` | Index ranges {first, last} of every unbroken run of months where the test held. |
| 1113 | 24 | `private static void spell(StringBuilder out, String label, double[] series, HistorySave h, Test test, String what, boolean wors...` |  |
| 1139 | 12 | `private static String spans(List<int[]> runs, List<Integer> axis)` | Consecutive months collapsed into ranges, and a long list cut off honestly. |

### THE NAMED EPISODES (0.7.5) (lines 1152-1378)

| line | len | member | says |
|---:|---:|---|---|
| 1233 | 4 | **type** `public record Episode(String kind, String name, int fromMonth, int toMonth, double worst, int worstMonth)` | One named stretch of the city's life. |
| 1235 | 1 | `public int months()` _(in YearBook.Episode)_ | How many months it ran, relief inside it included. |
| 1246 | 40 | `public static List<Episode> episodes(HistorySave h)` | Every named episode in a history, oldest first. |
| 1295 | 9 | `public static List<int[]> recessions(HistorySave h)` | The months to shade on a chart as recession, as {firstMonth, lastMonth} on the history's axis: every run where the rolling year of real output was below the year before it, of at least EPISODE_MIN_MONTHS - NOT joined,... |
| 1318 | 6 | **type** `public record Band(int fromMonth, int toMonth, double depth, int depthMonth, Episode episode)` | One recession band as the chart labels it (0.7.23): its months - one of recessions(), exactly - how deep real output fell inside it and in which month, and the recession or depression it belongs to, whose name it carr... |
| 1320 | 1 | `public int months()` _(in YearBook.Band)_ | How many months the band covers. |
| 1322 | 1 | `public String name()` _(in YearBook.Band)_ | The name it carries on the chart: its episode's. |
| 1326 | 23 | `public static List<Band> recessionBands(HistorySave h)` | Every recession band, oldest first, with its depth and its episode: recessions(), told about (0.7.23). |
| 1356 | 16 | `public static String trigger(String kind)` | The rule that names an episode of this kind, in words (0.7.23) - what the chart says a band or a span is, from the table's own thresholds. |
| 1374 | 4 | `public static String triggerFooter()` | The two rules every kind shares, in words: how long it must last, and what joins two into one. |

### WHAT IS RUNNING NOW (0.7.37). City History's strip names the trouble (lines 1379-1565)

| line | len | member | says |
|---:|---:|---|---|
| 1402 | 6 | `public static boolean isSevere(String kind)` | Whether an episode of this kind is a crisis rather than a watch (0.7.37): the bank failed, the currency halved, the treasury overdrawn, or a recession long enough to be a depression. |
| 1410 | 6 | `public static int monthsIn(List<Episode> episodes, String kind)` | How many months the episodes of one kind ran, all told - City History's hard times by kind (0.7.37). |
| 1418 | 3 | `public static boolean isChronic(Episode e)` | Whether an episode has run CHRONIC_MONTHS or more, relief inside it included (0.7.37). |
| 1429 | 5 | `public static List<Episode> running(HistorySave h)` | The episodes still running - those whose last month is the history's last, which the history has not closed - in the order City History names them (0.7.37): a chronic one after every other; then a crisis before a watc... |
| 1436 | 13 | `public static List<Episode> running(List<Episode> episodes, int lastMonth)` | ...from a list already read, for a screen that has episodes() in hand: those ending at `lastMonth`, in that order. |
| 1451 | 6 | `public static boolean worstIsHigh(String kind)` | Whether an episode of this kind is at its worst when its figure is HIGHEST - a peak - rather than lowest - a depth. |
| 1463 | 14 | `public static String worstWords(String kind, double worst)` | An episode's worst reading, in words, from the episode's own record (0.7.23): "real output 3.1% below the year before", "inflation at 31% a year". |
| 1483 | 10 | `public static double[] realGrowth(HistorySave h)` | The rolling year of real output against the year before it, as a fraction; NaN until two years are recorded. |
| 1500 | 25 | `public static double[] realGrowthTail(HistorySave h, int n)` | realGrowth()'s last {@code n} entries and no more of it (UP2), value for value: the header's GDP tile reads only the last (n = 1), and this reads the months that figure leans on - the twelve behind the entry and the e... |
| 1527 | 10 | `private static double[] currencyMove(HistorySave h)` | The exchange rate as a multiple of itself a year before - above 2 is a currency that halved. |
| 1539 | 26 | `private static void named(List<Episode> found, HistorySave h, String kind, double[] series, Test test, boolean worstIsHigh, Str...` | One row of the table: the runs of a condition, dropped, joined and named. |

### small helpers (lines 1566-1675)

| line | len | member | says |
|---:|---:|---|---|
| 1568 | 3 | `private static String at(List<Integer> axis, int i)` |  |
| 1572 | 5 | `private static boolean hasAny(double[] v)` |  |
| 1578 | 4 | `private static double firstReal(double[] v)` |  |
| 1583 | 4 | `private static double lastReal(double[] v)` |  |
| 1589 | 10 | `private static double sumOf(double[] v, HistorySave h)` | A flow's months added, a folded year's twelve at its monthly average (0.7.55). |
| 1601 | 10 | `private static double meanOf(double[] v, HistorySave h)` | A rate's months averaged, a folded year counting its twelve (0.7.55). |
| 1613 | 1 | `private static int last1(int[] run)` | The last entry of a joined run, or -2 with none - so the relief before the first run is never read. |
| 1615 | 8 | `private static int argBest(double[] v, boolean high)` |  |
| 1624 | 8 | `private static int argFurthestFrom(double[] v, double anchor)` |  |
| 1633 | 3 | `private static String pad(String s, int width)` |  |
| 1637 | 3 | `private static String pct(double fraction)` |  |
| 1650 | 17 | `static String compact(double v)` | Three significant figures, and never a thousands separator. |
| 1668 | 7 | `private static String trim(String s)` |  |

