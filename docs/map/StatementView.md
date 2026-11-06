# StatementView.java - 638 lines · 39 methods · 10 constants · interface

`ham/citybuildersim/ui/StatementView.java` - generated 2026-11-06 by CodeMap; line numbers are as of that run.

> A formal statement on the page (0.7.74): SectorStatements' rows set as an
> accountant sets them - a title block, the columns (note, this month, last
> month, change, common size), negatives in parentheses, one rule over a
> subtotal and two under a bottom line, a note number that opens its note in
> place - and the Summary | Statement switch the sector pages and the bank's
> two share, and the IN SHORT card every summary carries.
> 
> WHY. Jerus asked for "both a summarized and a detailed actual statement"
> (the project's spec-sector-statements.md). The pages' statement column was
> Statement's two-column book, plain words with minus signs; that stays, as
> what a Summary draws where it draws a statement at all, and this is the
> Statement view's: one shape for every sector and the bank, so a statement
> on one page lines up with every other. The figures are the model's
> thousands as they are (D2: in millions past seven digits), never
> abbreviated - a statement whose column reads 13.6M over 940k does not
> line up.

**Uses:** [Palette](Palette.md) (109), [Kept](Kept.md) (59), [SectorStatements](SectorStatements.md) (36), [Statement](Statement.md) (2), [SectorScreen](SectorScreen.md) (1)

**Used by (3):** [BankScreen](BankScreen.md), [KeptBench](KeptBench.md), [SectorScreen](SectorScreen.md)

## Sections

| line | section |
|---:|---|
| 71 | · the switch |
| 111 | · figures |
| 138 | · the title block |
| 161 | · the toolbar |
| 203 | · the table |
| 392 | · a statement in columns |
| 575 | · IN SHORT |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 42 | `StatementView.SUMMARY` | `"Summary", STATEMENT = "Statement"` | The switch's two words: the picture, or the statement (D1: one choice for every page, kept for the session). |
| 45 | `StatementView.TABLE` | `760` | A formal statement's width: its five columns and a label of some fifty characters at the body size. |
| 48 | `StatementView.NOTE_COLUMN` | `28` | The note column. |
| 51 | `StatementView.FIGURE_COLUMN` | `100` | This month's column and last month's: "(9,999,999)" at the body size, and room. |
| 54 | `StatementView.CHANGE_COLUMN` | `96` | The change column. |
| 57 | `StatementView.SHARE_COLUMN` | `64` | The common-size column: "(100.0%)". |
| 60 | `StatementView.INDENT` | `12` | How far a line sits in under its head. |
| 63 | `StatementView.SHARE_MOST` | `10` | A share of the base past this many times it reads "n/m", not meaningful: an outside line against a month with almost no revenue. |
| 66 | `StatementView.GAP` | `Palette.GAP` | The gap between columns. |
| 578 | `StatementView.SHORT_LABEL` | `150, SHORT_FIGURE = 78` | IN SHORT's columns: a label, then this month, last month and the change. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 37 | 602 | **type** `final class StatementView` | A formal statement on the page (0.7.74): SectorStatements' rows set as an accountant sets them - a title block, the columns (note, this month, last month, change, common size), negatives in parentheses, one rule over ... |
| 39 | 1 | `private StatementView()` |  |
| 69 | 1 | **type** `record Columns(boolean then, boolean change, boolean share, String shareWord)` | What the columns show: last month's (off for a statement of the month's movement alone), and the change and the common-size share, each of which the toolbar turns off; `shareWord` heads the share's ("of revenue"), nul... |

### the switch (lines 71-110)

| line | len | member | says |
|---:|---:|---|---|
| 77 | 10 | `static HBox viewSwitch(boolean statement, Consumer<Boolean> pick)` | The switch, at the right of a page strip (board SectorFrame): two segments, the one shown lit. |
| 88 | 13 | `private static Button segment(String word, boolean on, boolean left, Runnable go)` |  |
| 103 | 7 | `static HBox stripWithSwitch(Node strip, Boolean statement, Consumer<Boolean> pick)` | A page strip with the switch at its right end; `statement` null leaves the switch off (Operations, a bank page without one). |

### figures (lines 111-137)

| line | len | member | says |
|---:|---:|---|---|
| 114 | 8 | `static String figure(double thousands, boolean millions)` | A figure as the statement prints it: thousands (or millions, D2) grouped, negatives in parentheses (D3), nothing a dash, not known an em dash. |
| 124 | 8 | `static String share(double part, double base)` | A part of a base as the common-size column prints it: a tenth of a per cent, in parentheses below nothing; blank on no base, "n/m" past SHARE_MOST. |
| 134 | 3 | `static String units(boolean millions)` | The units line: "in $ thousands", or millions past seven digits (D2). |

### the title block (lines 138-160)

| line | len | member | says |
|---:|---:|---|---|
| 141 | 3 | `static VBox titleBlock(String company, String title, String period, boolean millions)` | The title block: the company, the statement's name, the period and the units. |
| 146 | 14 | `static VBox titleBlock(String company, String title, String period)` | ...for a report whose figures are not all money in one unit (the investor report): the period alone. |

### the toolbar (lines 161-202)

| line | len | member | says |
|---:|---:|---|---|
| 168 | 12 | `static HBox toolbar(Columns c, Runnable change, Runnable share, Runnable openAll, Runnable closeAll)` | The toolbar over a statement: the change column and the common-size column, each on or off, and every note opened or closed at once. |
| 181 | 11 | `private static Button toggle(String word, boolean on, Runnable go)` |  |
| 193 | 9 | `private static Button action(String word, Runnable go)` |  |

### the table (lines 203-391)

| line | len | member | says |
|---:|---:|---|---|
| 206 | 1 | `static String noteKey(String table, int note)` | A note's key in the screen's open set. |
| 209 | 8 | `static void openAll(SectorStatements.Table t, String table, Set<String> open, IntFunction<Node> notes)` | Every note a statement has, opened (the toolbar's "open every note"). |
| 219 | 3 | `static void closeAll(String table, Set<String> open)` | ...and closed. |
| 236 | 6 | `static VBox table(SectorStatements.Table t, String table, String nowWord, String thenWord, Columns c, double base, double baseT...` | The statement itself: its column heads, then each row it shows. |
| 243 | 29 | `private static VBox tableOf(SectorStatements.Table t, String table, String nowWord, String thenWord, Columns c, double base, do...` |  |
| 281 | 5 | `static boolean nil(SectorStatements.Row r, SectorStatements.Table t, Columns c, boolean millions)` | Whether a line prints as nothing in every column it has: under half the statement's unit both months (a few hundred dollars, in thousands), or not known. |
| 287 | 1 | `private static boolean blank(String figure)` |  |
| 290 | 14 | `static HBox heads(String nowWord, String thenWord, Columns c)` | The column heads. |
| 305 | 1 | `private static String headStyle()` |  |
| 307 | 9 | `private static Label cell(String text, double width, String style, Pos at)` |  |
| 318 | 32 | `static Node row(SectorStatements.Row r, boolean known, boolean millions, Columns c, double base, double baseThen, boolean hasNo...` | One row: its note number, its label, and its figures, ruled as its kind is. |
| 352 | 39 | `private static HBox lead(SectorStatements.Row r, boolean hasNote, boolean opened, Runnable toggle, String info)` | A row's note number - a door when it opens something - and its label with its (i), set in as its kind is: what row() and columnsRow() put their figures after. |

### a statement in columns (lines 392-574)

| line | len | member | says |
|---:|---:|---|---|
| 401 | 5 | `static VBox columnsTable(SectorStatements.Table t, String table, String[] heads, Set<String> open, IntFunction<Node> notes, Fun...` | A statement in columns, this month (0.7.75): each row's parts and its figure, under `heads` - the statement of changes in equity's share capital, what it kept and revalued, and the total (R3). |
| 407 | 36 | `private static VBox columnsTableOf(SectorStatements.Table t, String table, String[] heads, Set<String> open, IntFunction<Node> ...` |  |
| 445 | 7 | `static boolean nilColumns(SectorStatements.Row r, boolean millions)` | Whether a line in columns prints as nothing in every one of them (nil()'s rule); a residual never is. |
| 454 | 22 | `static Node columnsRow(SectorStatements.Row r, boolean millions, boolean hasNote, boolean opened, Runnable toggle, String info)` | One row in columns: its parts, then its figure; ruled as its kind is, and a bottom line's negatives in the verdict's colour. |
| 478 | 23 | `private static Node ruled(String text, double width, String style, String rule)` | A figure in its column, with a rule over it (a subtotal), or over it and two under it (a bottom line). |
| 502 | 9 | `private static Region line()` |  |
| 517 | 11 | `static VBox card(VBox title, HBox toolbar, VBox table)` | A formal statement as a card: its title block, the toolbar, the table. |
| 530 | 8 | `static javafx.scene.layout.FlowPane beside(Node statement, Node ratios)` | A statement card beside its ratios, wrapping the ratios under it on a window too narrow for both. |
| 540 | 29 | `static VBox noteRows(List<SectorStatements.Row> rows, boolean millions, String sentence)` | A note's rows, this month: a small list under the line it opens (D12: this month only). |
| 571 | 3 | `static VBox noteSentence(String sentence)` | A sentence as a note: what a line is, when it has no parts to list. |

### IN SHORT (lines 575-638)

| line | len | member | says |
|---:|---:|---|---|
| 585 | 4 | `static VBox inShort(String[] labels, double[] now, double[] then, boolean known, String info)` | IN SHORT (the summaries' card): five lines, this month, last month and the change, in $ millions - the statement's own subtotals (D14), so the summary and the statement say the same thing in two sizes. |
| 590 | 26 | `private static VBox inShortOf(String[] labels, double[] now, double[] then, boolean known, String info)` |  |
| 618 | 6 | `static String millions(double thousands)` | Thousands as millions to a tenth, a true minus below nothing (the summaries keep their signs, D3). |
| 626 | 5 | `static String signedMillions(double thousands)` | ...with a sign either way, for a change. |
| 633 | 5 | `static String sentence(String caps)` | "Gross profit" from "GROSS PROFIT": a statement's line as a sentence's words, for the summaries. |

