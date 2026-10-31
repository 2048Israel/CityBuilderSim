# YearBook.java - 1,665 lines · 85 methods · 18 constants · model

`ham/citybuildersim/YearBook.java` - generated 2026-10-31 by CodeMap; line numbers are as of that run.

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

**Uses:** [HistorySave](HistorySave.md) (45), [DecisionLog](DecisionLog.md) (9), [Currency](Currency.md) (5), [CityCalendar](CityCalendar.md) (5), [PriceIndex](PriceIndex.md) (2), [Expectations](Expectations.md) (2), [GameVersion](GameVersion.md) (2), [Formats](Formats.md) (2), [Bank](Bank.md) (1), [WorldMoneys](WorldMoneys.md) (1), [Nations](Nations.md) (1), [Health](Health.md) (1)

**Used by (21):** [BankScreen](BankScreen.md), [ChartCheck](ChartCheck.md), [ChartModel](ChartModel.md), [CityMeasures](CityMeasures.md), [ClockCheck](ClockCheck.md), [Game](Game.md), [GovernmentScreen](GovernmentScreen.md), [HistoryCheck](HistoryCheck.md), [HistorySave](HistorySave.md), [HistoryScreen](HistoryScreen.md), [KeptBench](KeptBench.md), [OilView](OilView.md), [PolicyScreen](PolicyScreen.md), [RankingsCheck](RankingsCheck.md), [ReadPathCheck](ReadPathCheck.md), [ScaleCheck](ScaleCheck.md), [TimeChart](TimeChart.md), [TradeScreen](TradeScreen.md), [UserInterface](UserInterface.md), [WorldMoneysCheck](WorldMoneysCheck.md), [YearBookCheck](YearBookCheck.md)

## Sections

| line | section |
|---:|---|
| 74 | THE RULES |
| 333 | THE DERIVED COLUMNS |
| 352 | THE DERIVED SERIES THAT TWO SCREENS BOTH WANT |
| 665 | THE FOLD |
| 722 | THE TABLES, ONCE, FOR THE TEXT AND THE CSV (0.7.16) |
| 824 | THE FILE |
| 854 | · · the rows, as index ranges into the month axis |
| 863 | · · fold everything, then drop what never moved |
| 878 | · · the two tables, every cell as the text prints it |
| 912 | · · the preamble |
| 947 | · · the columns |
| 967 | · · the table |
| 971 | · · the extremes |
| 988 | WHAT HAPPENED |
| 1142 | THE NAMED EPISODES (0.7.5) |
| 1369 | · WHAT IS RUNNING NOW (0.7.37). City History's strip names the trouble |
| 1556 | · small helpers |

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
| 293 | `YearBook.RULES` | `rules()` |  |
| 294 | `YearBook.PREFIXES` | `prefixRules()` |  |
| 579 | `YearBook.GDP_PARTS` | `{ "consumption", "investment", "government", "netExports" }` | GDP's four parts, as HistorySave names them (0.7.6): C, I, G and NX, in the order they stack. |
| 1167 | `YearBook.EPISODE_MIN_MONTHS` | `3` | A run shorter than this many months is noise, and is not named - or shaded on the chart. |
| 1170 | `YearBook.EPISODE_JOIN_MONTHS` | `6` | Two runs with fewer months of relief than this between them are one episode. |
| 1173 | `YearBook.DEPRESSION_MONTHS` | `24` | A recession this many months long, or longer, is called a depression. |
| 1185 | `YearBook.FINANCIAL_EQUITY` | `0` | The bank's equity under this, in thousands, is a financial crisis: the bank has failed. |
| 1188 | `YearBook.RECESSION_GROWTH` | `0` | Real output over a rolling year against the year before, as a fraction, under this is a recession: under zero, a fall. |
| 1191 | `YearBook.CURRENCY_MOVE` | `2` | The exchange rate past this multiple of itself a year before is a currency crisis: the currency halved. |
| 1194 | `YearBook.INFLATION_EPISODE` | `.25` | Prices rising faster than this a year is an inflation. |
| 1197 | `YearBook.DEFLATION_EPISODE` | `-.10` | Prices falling faster than this a year (a negative rate) is a deflation. |
| 1200 | `YearBook.EPIDEMIC_OUTBREAK` | `Health.OUTBREAK_FLOOR` | An epidemic is named while an outbreak runs - more of the workforce off sick than its care explains - not while a city short of care is as sick as it always is (A8). |
| 1203 | `YearBook.TREASURY_CASH` | `0` | The treasury's cash under this, in thousands, is a treasury crisis: it is overdrawn. |
| 1206 | `YearBook.SLUMP_UNEMPLOYMENT` | `.20` | More of the labour force out of work than this is a slump. |
| 1383 | `YearBook.CHRONIC_MONTHS` | `120` | An episode still running after this many months is chronic: City History lists it after the others that are running, and leads with it only when it runs alone (0.7.37). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 343 | `final String name` |  |
| 344 | `final Kind kind` |  |
| 345 | `final double[] monthly` |  |
| 346 | `final String note` |  |
| 806 | `private final String text` |  |
| 807 | `private final Table table` |  |
| 808 | `private final Table within` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 57 | 1609 | **type** `public final class YearBook` | The run, one line a year - for READING rather than for drawing. |
| 60 | 8 | **type** `public enum Kind` | How a column's months become one number. |
| 72 | 1 | `private YearBook()` |  |

### THE RULES (lines 74-332)

| line | len | member | says |
|---:|---:|---|---|
| 93 | 1 | **type** `private record Rule(Kind kind, String note)` | What a series IS, and one line saying what it means and in what unit. |
| 103 | 166 | `private static Map<String, Rule> rules()` |  |
| 270 | 1 | `private static void flow(Map<String, Rule> m, String k, String note)` |  |
| 271 | 1 | `private static void level(Map<String, Rule> m, String k, String note)` |  |
| 272 | 1 | `private static void rate(Map<String, Rule> m, String k, String note)` |  |
| 282 | 10 | `private static Map<String, Rule> prefixRules()` | The runtime families, matched on the part before the colon. |
| 304 | 4 | `public static Kind kindOf(String series)` | The rule for a series, or null if nobody has declared one. |
| 310 | 4 | `public static String noteOf(String series)` | What a series means, in one line, or null if nobody has said. |
| 315 | 8 | `private static Rule ruleFor(String series)` |  |
| 325 | 7 | `public static List<String> unruled(HistorySave history)` | Every series in this history that nobody has declared a rule for. |

### THE DERIVED COLUMNS (lines 333-351)

| line | len | member | says |
|---:|---:|---|---|
| 342 | 9 | **type** `private static final class Column` |  |
| 347 | 3 | `Column(String name, Kind kind, double[] monthly, String note)` _(in YearBook.Column)_ |  |

### THE DERIVED SERIES THAT TWO SCREENS BOTH WANT (lines 352-664)

| line | len | member | says |
|---:|---:|---|---|
| 384 | 3 | `public static double[] labourForce(HistorySave h)` | Who is actually available to work: the workforce less the people who are not looking. |
| 389 | 9 | `static double[] labourForce(double[] workforce, double[] students, double[] prisoners)` | labourForce() off the three series, whole or a tail of them alike (UP2): one definition. |
| 409 | 3 | `public static double[] filledPosts(HistorySave h)` | Posts with somebody in them - the labour force less the pool. |
| 414 | 9 | `static double[] filledPosts(double[] force, double[] jobs, double[] pool)` | filledPosts() off the labour force, the posts and the pool, whole or a stretch of them alike (LB3, CityMeasures at a month): one definition. |
| 425 | 3 | `public static double[] unemployment(HistorySave h)` | THE definition of the city's unemployment rate off a history. |
| 434 | 4 | `public static double[] unemploymentTail(HistorySave h, int n)` | unemployment()'s last {@code n} entries and no more of it (UP2), value for value: every input is read a month at a time, so the tail of the rate is the rate off the tails of its inputs. |
| 440 | 8 | `static double[] unemployment(double[] force, double[] jobs, double[] pool)` | unemployment() off the labour force, the posts and the pool, whole or a tail of them alike (UP2): one definition. |
| 458 | 3 | `public static double[] averageWage(HistorySave h)` | THE definition of the average wage off a history: the wage bill over the posts that are FILLED. |
| 463 | 7 | `static double[] averageWage(double[] bill, double[] filled)` | averageWage() off the wage bill and the posts filled, whole or a stretch of them alike (LB3, CityMeasures at a month): one definition. |
| 487 | 3 | `public static double[] realGdp(HistorySave h)` | Output in FOUNDING money, a month at a time: nominal GDP over the price index. |
| 506 | 3 | `public static double[] realGdpYear(HistorySave h)` | ...and a rolling YEAR of it, which is what the Reports page draws and what a recession is read off. |
| 519 | 3 | `private static double[] rollingYear(double[] monthly, HistorySave h)` | Twelve months summed, ending at each month; NaN before the twelfth, and wherever the window holds a NaN. |
| 530 | 19 | `private static double[] rollingYear(double[] monthly, int base, int outFirst, HistorySave h)` | rollingYear() for the axis entries {@code outFirst} on, off a series that begins at entry {@code base} (UP2): {@code monthly[x]} is axis entry base + x, and the result's [x] is entry outFirst + x. |
| 562 | 3 | `public static double[] real(HistorySave h, String key)` | Any money series in FOUNDING money, a month at a time: divided by the price index the way realGdp() is, so GDP's four parts and GDP itself are the same money and the layers under the line add up to it. |
| 567 | 7 | `private static double[] real(double[] nominal, double[] index)` | real() off the two series, whole or a tail of them alike (UP2): one definition. |
| 588 | 3 | `public static double[] realYear(HistorySave h, String key)` | ...and a rolling YEAR of one of them in founding money (0.7.6), summed as realGdpYear() is - so a year of consumption, investment, government and net exports adds up to the year of real GDP it is part of. |
| 599 | 10 | `public static double[] inflation(HistorySave h)` | Inflation YEAR ON YEAR - the price index against its own reading twelve months earlier, the window PriceIndex uses. |
| 617 | 3 | `public static double componentInflation(HistorySave h, int component)` | One component's inflation over the last year (0.7.45; PriceIndex COMPONENTS order): its chained level now against twelve months before, off the history's line for it - NaN until a year of the line is recorded, which o... |
| 621 | 43 | `private static List<Column> columns(HistorySave h)` |  |

### THE FOLD (lines 665-721)

| line | len | member | says |
|---:|---:|---|---|
| 678 | 25 | `private static double fold(Column c, int from, int to, HistorySave h)` | A row's value for a column, by that column's own rule. |
| 705 | 5 | `private static int monthsIn(HistorySave h, int from, int to)` | The months axis entries from..to hold (0.7.55): a row's n. |
| 712 | 9 | `private static double worst(Column c, int from, int to, boolean high, HistorySave h)` | A row's worst or best SINGLE month: a folded year has none, so it is passed over (0.7.55). |

### THE TABLES, ONCE, FOR THE TEXT AND THE CSV (0.7.16) (lines 722-823)

| line | len | member | says |
|---:|---:|---|---|
| 750 | 25 | **type** `public record Table(List<String> header, List<List<String>> rows)` | One of the book's tables: its column names and its rows, each cell the string the text prints - an empty string where the text is blank. |
| 752 | 6 | `{ ... }` _(in YearBook.Table)_ |  |
| 760 | 6 | `String tabbed()` _(in YearBook.Table)_ | As the text prints it: a tab between cells and a newline after every line, the header first. |
| 768 | 6 | `public String csv()` _(in YearBook.Table)_ | As RFC 4180 writes it: a comma between fields and CRLF after every record, the header first. |
| 776 | 7 | `private static void csvRecord(StringBuilder out, List<String> fields)` |  |
| 789 | 5 | `static String csvField(String s)` | One CSV field, quoted only when it has to be: a comma, a double quote or a line break inside it, and a quote inside is doubled. |
| 805 | 18 | **type** `public static final class Book` | A book, built once: the text the reader gets and the two tables in it. |
| 810 | 5 | `private Book(String text, Table table, Table within)` _(in YearBook.Book)_ |  |
| 817 | 1 | `public String text()` _(in YearBook.Book)_ | The whole text file, exactly what years() and decades() return. |
| 819 | 1 | `public Table table()` _(in YearBook.Book)_ | The YEARS (or DECADES) table: the row, mo, n and every column shown. |
| 821 | 1 | `public Table within()` _(in YearBook.Book)_ | The WITHIN table: the row, then each [~] column's worst and best month, .lo and .hi. |

### THE FILE (lines 824-987)

| line | len | member | says |
|---:|---:|---|---|
| 829 | 1 | `public static String years(HistorySave h, Currency money)` | The book a year to the row, in the city's own money's name (0.7.10). |
| 831 | 1 | `public static String decades(HistorySave h, Currency money)` | ...and a decade to the row. |
| 834 | 1 | `public static Book yearBook(HistorySave h, Currency money)` | The year book whole - its text and its tables - for an export that writes both (0.7.16). |
| 836 | 1 | `public static Book decadeBook(HistorySave h, Currency money)` | ...and the decade book. |
| 838 | 143 | `private static Book write(HistorySave history, int span, Currency money)` |  |
| 982 | 1 | `private static int bucket(int month, int span)` |  |
| 984 | 3 | `private static String mark(Kind k)` |  |

### WHAT HAPPENED (lines 988-1141)

| line | len | member | says |
|---:|---:|---|---|
| 997 | 86 | `private static String whatHappened(HistorySave h, List<Column> cols)` |  |
| 1085 | 1 | **type** `private interface Test` | A condition, how many months met it, where they were, and its worst reading. |
| 1085 | 1 | `boolean holds(double v)` _(in YearBook.Test)_ |  |
| 1088 | 14 | `private static List<int[]> runsOf(double[] series, Test test)` | Index ranges {first, last} of every unbroken run of months where the test held. |
| 1103 | 24 | `private static void spell(StringBuilder out, String label, double[] series, HistorySave h, Test test, String what, boolean wors...` |  |
| 1129 | 12 | `private static String spans(List<int[]> runs, List<Integer> axis)` | Consecutive months collapsed into ranges, and a long list cut off honestly. |

### THE NAMED EPISODES (0.7.5) (lines 1142-1368)

| line | len | member | says |
|---:|---:|---|---|
| 1223 | 4 | **type** `public record Episode(String kind, String name, int fromMonth, int toMonth, double worst, int worstMonth)` | One named stretch of the city's life. |
| 1225 | 1 | `public int months()` _(in YearBook.Episode)_ | How many months it ran, relief inside it included. |
| 1236 | 40 | `public static List<Episode> episodes(HistorySave h)` | Every named episode in a history, oldest first. |
| 1285 | 9 | `public static List<int[]> recessions(HistorySave h)` | The months to shade on a chart as recession, as {firstMonth, lastMonth} on the history's axis: every run where the rolling year of real output was below the year before it, of at least EPISODE_MIN_MONTHS - NOT joined,... |
| 1308 | 6 | **type** `public record Band(int fromMonth, int toMonth, double depth, int depthMonth, Episode episode)` | One recession band as the chart labels it (0.7.23): its months - one of recessions(), exactly - how deep real output fell inside it and in which month, and the recession or depression it belongs to, whose name it carr... |
| 1310 | 1 | `public int months()` _(in YearBook.Band)_ | How many months the band covers. |
| 1312 | 1 | `public String name()` _(in YearBook.Band)_ | The name it carries on the chart: its episode's. |
| 1316 | 23 | `public static List<Band> recessionBands(HistorySave h)` | Every recession band, oldest first, with its depth and its episode: recessions(), told about (0.7.23). |
| 1346 | 16 | `public static String trigger(String kind)` | The rule that names an episode of this kind, in words (0.7.23) - what the chart says a band or a span is, from the table's own thresholds. |
| 1364 | 4 | `public static String triggerFooter()` | The two rules every kind shares, in words: how long it must last, and what joins two into one. |

### WHAT IS RUNNING NOW (0.7.37). City History's strip names the trouble (lines 1369-1555)

| line | len | member | says |
|---:|---:|---|---|
| 1392 | 6 | `public static boolean isSevere(String kind)` | Whether an episode of this kind is a crisis rather than a watch (0.7.37): the bank failed, the currency halved, the treasury overdrawn, or a recession long enough to be a depression. |
| 1400 | 6 | `public static int monthsIn(List<Episode> episodes, String kind)` | How many months the episodes of one kind ran, all told - City History's hard times by kind (0.7.37). |
| 1408 | 3 | `public static boolean isChronic(Episode e)` | Whether an episode has run CHRONIC_MONTHS or more, relief inside it included (0.7.37). |
| 1419 | 5 | `public static List<Episode> running(HistorySave h)` | The episodes still running - those whose last month is the history's last, which the history has not closed - in the order City History names them (0.7.37): a chronic one after every other; then a crisis before a watc... |
| 1426 | 13 | `public static List<Episode> running(List<Episode> episodes, int lastMonth)` | ...from a list already read, for a screen that has episodes() in hand: those ending at `lastMonth`, in that order. |
| 1441 | 6 | `public static boolean worstIsHigh(String kind)` | Whether an episode of this kind is at its worst when its figure is HIGHEST - a peak - rather than lowest - a depth. |
| 1453 | 14 | `public static String worstWords(String kind, double worst)` | An episode's worst reading, in words, from the episode's own record (0.7.23): "real output 3.1% below the year before", "inflation at 31% a year". |
| 1473 | 10 | `public static double[] realGrowth(HistorySave h)` | The rolling year of real output against the year before it, as a fraction; NaN until two years are recorded. |
| 1490 | 25 | `public static double[] realGrowthTail(HistorySave h, int n)` | realGrowth()'s last {@code n} entries and no more of it (UP2), value for value: the header's GDP tile reads only the last (n = 1), and this reads the months that figure leans on - the twelve behind the entry and the e... |
| 1517 | 10 | `private static double[] currencyMove(HistorySave h)` | The exchange rate as a multiple of itself a year before - above 2 is a currency that halved. |
| 1529 | 26 | `private static void named(List<Episode> found, HistorySave h, String kind, double[] series, Test test, boolean worstIsHigh, Str...` | One row of the table: the runs of a condition, dropped, joined and named. |

### small helpers (lines 1556-1665)

| line | len | member | says |
|---:|---:|---|---|
| 1558 | 3 | `private static String at(List<Integer> axis, int i)` |  |
| 1562 | 5 | `private static boolean hasAny(double[] v)` |  |
| 1568 | 4 | `private static double firstReal(double[] v)` |  |
| 1573 | 4 | `private static double lastReal(double[] v)` |  |
| 1579 | 10 | `private static double sumOf(double[] v, HistorySave h)` | A flow's months added, a folded year's twelve at its monthly average (0.7.55). |
| 1591 | 10 | `private static double meanOf(double[] v, HistorySave h)` | A rate's months averaged, a folded year counting its twelve (0.7.55). |
| 1603 | 1 | `private static int last1(int[] run)` | The last entry of a joined run, or -2 with none - so the relief before the first run is never read. |
| 1605 | 8 | `private static int argBest(double[] v, boolean high)` |  |
| 1614 | 8 | `private static int argFurthestFrom(double[] v, double anchor)` |  |
| 1623 | 3 | `private static String pad(String s, int width)` |  |
| 1627 | 3 | `private static String pct(double fraction)` |  |
| 1640 | 17 | `static String compact(double v)` | Three significant figures, and never a thousands separator. |
| 1658 | 7 | `private static String trim(String s)` |  |

