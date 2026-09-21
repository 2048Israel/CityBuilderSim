# YearBook.java - 823 lines · 39 methods · 4 constants · model

`ham/citybuildersim/YearBook.java` - generated 2026-09-21 by CodeMap; line numbers are as of that run.

> The run, one line a year - for READING rather than for drawing.
> 
> WHY THIS EXISTS
> 
> Jerus, 2026-09-14: "i want another txt or something which is different, it
> basically summarizes not by month, but by year, all the stuff, so that you or
> another ai, can be sent that, and doesnt die due to tokens... so that they can
> analyze the economic situation of the run."
> 
> The history is every month the city has ever lived across ninety-odd series.
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

**Uses:** [HistorySave](HistorySave.md) (12), [CityCalendar](CityCalendar.md) (3), [GameVersion](GameVersion.md) (2)

**Used by (3):** [Game](Game.md), [HistoryScreen](HistoryScreen.md), [YearBookCheck](YearBookCheck.md)

## Sections

| line | section |
|---:|---|
| 64 | THE RULES |
| 249 | THE DERIVED COLUMNS |
| 268 | THE DERIVED SERIES THAT TWO SCREENS BOTH WANT |
| 423 | THE FOLD |
| 461 | THE FILE |
| 482 | · · the rows, as index ranges into the month axis |
| 491 | · · fold everything, then drop what never moved |
| 506 | · · the preamble |
| 533 | · · the columns |
| 553 | · · the table |
| 566 | · · the extremes |
| 596 | WHAT HAPPENED |
| 729 | · small helpers |

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
| 209 | `YearBook.RULES` | `rules()` |  |
| 210 | `YearBook.PREFIXES` | `prefixRules()` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 259 | `final String name` |  |
| 260 | `final Kind kind` |  |
| 261 | `final double[] monthly` |  |
| 262 | `final String note` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 47 | 777 | **type** `public final class YearBook` | The run, one line a year - for READING rather than for drawing. |
| 50 | 8 | **type** `public enum Kind` | How a column's months become one number. |
| 62 | 1 | `private YearBook()` |  |

### THE RULES (lines 64-248)

| line | len | member | says |
|---:|---:|---|---|
| 82 | 1 | **type** `private record Rule(Kind kind, String note)` | What a series IS, and one line saying what it means and in what unit. |
| 84 | 103 | `private static Map<String, Rule> rules()` |  |
| 188 | 1 | `private static void flow(Map<String, Rule> m, String k, String note)` |  |
| 189 | 1 | `private static void level(Map<String, Rule> m, String k, String note)` |  |
| 190 | 1 | `private static void rate(Map<String, Rule> m, String k, String note)` |  |
| 200 | 8 | `private static Map<String, Rule> prefixRules()` | The runtime families, matched on the part before the colon. |
| 220 | 4 | `public static Kind kindOf(String series)` | The rule for a series, or null if nobody has declared one. |
| 226 | 4 | `public static String noteOf(String series)` | What a series means, in one line, or null if nobody has said. |
| 231 | 8 | `private static Rule ruleFor(String series)` |  |
| 241 | 7 | `public static List<String> unruled(HistorySave history)` | Every series in this history that nobody has declared a rule for. |

### THE DERIVED COLUMNS (lines 249-267)

| line | len | member | says |
|---:|---:|---|---|
| 258 | 9 | **type** `private static final class Column` |  |
| 263 | 3 | `Column(String name, Kind kind, double[] monthly, String note)` _(in YearBook.Column)_ |  |

### THE DERIVED SERIES THAT TWO SCREENS BOTH WANT (lines 268-422)

| line | len | member | says |
|---:|---:|---|---|
| 300 | 12 | `public static double[] labourForce(HistorySave h)` | Who is actually available to work: the workforce less the people who are not looking. |
| 323 | 12 | `public static double[] filledPosts(HistorySave h)` | Posts with somebody in them - the labour force less the pool. |
| 337 | 11 | `public static double[] unemployment(HistorySave h)` | THE definition of the city's unemployment rate off a history. |
| 358 | 9 | `public static double[] averageWage(HistorySave h)` | THE definition of the average wage off a history: the wage bill over the posts that are FILLED. |
| 368 | 54 | `private static List<Column> columns(HistorySave h)` |  |

### THE FOLD (lines 423-460)

| line | len | member | says |
|---:|---:|---|---|
| 428 | 22 | `private static double fold(Column c, int from, int to)` | A row's value for a column, by that column's own rule. |
| 451 | 9 | `private static double worst(Column c, int from, int to, boolean high)` |  |

### THE FILE (lines 461-595)

| line | len | member | says |
|---:|---:|---|---|
| 465 | 1 | `public static String years(HistorySave h)` |  |
| 466 | 1 | `public static String decades(HistorySave h)` |  |
| 468 | 121 | `private static String write(HistorySave history, int span)` |  |
| 590 | 1 | `private static int bucket(int month, int span)` |  |
| 592 | 3 | `private static String mark(Kind k)` |  |

### WHAT HAPPENED (lines 596-728)

| line | len | member | says |
|---:|---:|---|---|
| 605 | 77 | `private static String episodes(HistorySave h, List<Column> cols)` |  |
| 684 | 1 | **type** `private interface Test` | A condition, how many months met it, where they were, and its worst reading. |
| 684 | 1 | `boolean holds(double v)` _(in YearBook.Test)_ |  |
| 686 | 28 | `private static void spell(StringBuilder out, String label, double[] series, List<Integer> axis, Test test, String what, boolean...` |  |
| 716 | 12 | `private static String spans(List<int[]> runs, List<Integer> axis)` | Consecutive months collapsed into ranges, and a long list cut off honestly. |

### small helpers (lines 729-823)

| line | len | member | says |
|---:|---:|---|---|
| 731 | 3 | `private static String at(List<Integer> axis, int i)` |  |
| 735 | 5 | `private static boolean hasAny(double[] v)` |  |
| 741 | 4 | `private static double firstReal(double[] v)` |  |
| 746 | 4 | `private static double lastReal(double[] v)` |  |
| 751 | 5 | `private static double sumOf(double[] v)` |  |
| 757 | 5 | `private static double meanOf(double[] v)` |  |
| 763 | 8 | `private static int argBest(double[] v, boolean high)` |  |
| 772 | 8 | `private static int argFurthestFrom(double[] v, double anchor)` |  |
| 781 | 3 | `private static String pad(String s, int width)` |  |
| 785 | 3 | `private static String pct(double fraction)` |  |
| 798 | 17 | `static String compact(double v)` | Three significant figures, and never a thousands separator. |
| 816 | 7 | `private static String trim(String s)` |  |

