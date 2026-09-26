# Statement.java - 464 lines · 22 methods · 5 constants · interface

`ham/citybuildersim/ui/Statement.java` - generated 2026-09-26 by CodeMap; line numbers are as of that run.

> The rows a statement is built from: a head, a line, a note, a total, a
> disclosure that opens, and the two-column book the sector pages and the
> bank's income statement use - its lines able to stay open through the
> clock's redraw since 0.7.9 (opens()).
> 
> Static for the same reason as Money: they hold no state and every screen
> uses them, so they belong to no screen. Called unqualified through an
> import static, exactly as they were when they were methods of the window.

**Uses:** [Palette](Palette.md) (66)

**Used by (16):** [BankCheck](BankCheck.md), [BooksCheck](BooksCheck.md), [EconomyManager](EconomyManager.md), [HouseholdAccounts](HouseholdAccounts.md), [HouseholdCheck](HouseholdCheck.md), [LongPlaytest](LongPlaytest.md), [MiningCheck](MiningCheck.md), [MonthOrder](MonthOrder.md), [NewGameCheck](NewGameCheck.md), [PeopleScreen](PeopleScreen.md), [RailCheck](RailCheck.md), [Sector](Sector.md), [SectorBooks](SectorBooks.md), [SectorBooksCheck](SectorBooksCheck.md), [SectorScreen](SectorScreen.md), [SectorState](SectorState.md)

## Sections

| line | section |
|---:|---|
| 26 | A STATEMENT WITH TWO COLUMNS. |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 34 | `Statement.STATEMENT` | `560` | How wide a statement is. |
| 132 | `Statement.BOOK_NOW` | `116` |  |
| 134 | `Statement.BOOK_THEN` | `104` |  |
| 425 | `Statement.CLOSED` | `"\u25b8"` |  |
| 427 | `Statement.OPENED` | `"\u25be"` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 24 | 441 | **type** `public final class Statement` | The rows a statement is built from: a head, a line, a note, a total, a disclosure that opens, and the two-column book the sector pages and the bank's income statement use - its lines able to stay open through the cloc... |

### A STATEMENT WITH TWO COLUMNS. (lines 26-464)

| line | len | member | says |
|---:|---:|---|---|
| 37 | 1 | `public static VBox statementHead(String title)` | A statement's heading, with a rule under it. |
| 40 | 18 | `public static VBox statementHead(String title, double width)` |  |
| 59 | 3 | `public static HBox statementLine(String label, String value)` |  |
| 68 | 19 | `public static HBox statementLine(String label, String value, String tone)` | label stays grey whatever happens, because the label is never the news. |
| 89 | 8 | `public static Label statementNote(String text)` | The sentence under a line - what it means, or where it comes from. |
| 99 | 32 | `public static VBox statementTotal(String label, String value, String tone)` | The line a statement adds up to: a rule, then the figure in full. |
| 137 | 3 | `public static HBox bookHead(String left)` | The column headings, once, at the top of a statement. |
| 142 | 26 | `public static HBox bookHead(String left, String now, String then)` | ...with the two columns named - the bank's balance sheet sets this month beside "a year ago" (0.7.13). |
| 180 | 28 | `public static HBox bookLine(String label, double now, double then, boolean known, String tone)` | One line of a set of books: what it is, this month, and last month. |
| 210 | 4 | `public static VBox bookLine(String label, double now, double then, boolean known, String tone, VBox detail, String word)` | A statement line that opens into its parts. |
| 219 | 26 | `public static VBox bookLine(String label, double now, double then, boolean known, String tone, VBox detail, String word, java.u...` | ...and remembers whether it was open: see opens(). |
| 256 | 17 | `static void opens(javafx.scene.Node row, Label mark, String word, VBox detail, String key, java.util.Set<String> open)` | A row that opens its detail - DRAWN AS THE SCREEN LAST LEFT IT (0.7.9). |
| 279 | 40 | `public static VBox bookTotal(String label, double now, double then, boolean known, String tone)` | The line a section adds up to: a rule, then this month's figure at full weight and last month's quieter beside it. |
| 330 | 3 | `public static VBox statementDisclosure(String label, String value, VBox detail)` | A statement line that opens something underneath it. |
| 335 | 3 | `public static VBox statementDisclosure(String label, String value, VBox detail, String hint)` | As above, naming what opening it shows. |
| 340 | 24 | `public static VBox statementDisclosure(String label, String value, VBox detail, String hint, java.util.Set<String> open)` | ...and remembering whether it was open: see opens(). |
| 366 | 7 | `public static Label bookLine(String label, double value, boolean bold, String colour)` | A statement line: label left, figure right, in one fixed-width column. |
| 374 | 6 | `public static Label bookRule()` |  |
| 382 | 6 | `public static Label bookNote(String text)` | A short grey line under a figure, for the one sentence it needs. |
| 401 | 5 | `public static VBox disclosure(String line, String hint, VBox detail)` | A line you can click to open the working behind it. |
| 408 | 16 | `public static VBox disclosure(String line, String hint, VBox detail, String style)` | As above, keeping a row's own styling - used by the tier table. |
| 429 | 35 | `public static VBox reportSection(String heading, String...rows)` |  |

