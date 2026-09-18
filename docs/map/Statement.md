# Statement.java - 379 lines · 17 methods · 5 constants · interface

`ham/citybuildersim/ui/Statement.java` - generated 2026-09-18 by CodeMap; line numbers are as of that run.

> The rows a statement is built from: a head, a line, a note, a total, a
> disclosure that opens, and the two-column book the sector pages use.
> 
> Static for the same reason as Money: they hold no state and every screen
> uses them, so they belong to no screen. Called unqualified through an
> import static, exactly as they were when they were methods of the window.

**Uses:** [Palette](Palette.md) (55)

**Used by (15):** [BooksCheck](BooksCheck.md), [EconomyManager](EconomyManager.md), [HouseholdAccounts](HouseholdAccounts.md), [HouseholdCheck](HouseholdCheck.md), [LongPlaytest](LongPlaytest.md), [MiningCheck](MiningCheck.md), [MonthOrder](MonthOrder.md), [NewGameCheck](NewGameCheck.md), [PeopleScreen](PeopleScreen.md), [RailCheck](RailCheck.md), [Sector](Sector.md), [SectorBooks](SectorBooks.md), [SectorBooksCheck](SectorBooksCheck.md), [SectorScreen](SectorScreen.md), [SectorState](SectorState.md)

## Sections

| line | section |
|---:|---|
| 24 | A STATEMENT WITH TWO COLUMNS. |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 32 | `Statement.STATEMENT` | `560` | How wide a statement is. |
| 130 | `Statement.BOOK_NOW` | `116` |  |
| 132 | `Statement.BOOK_THEN` | `104` |  |
| 340 | `Statement.CLOSED` | `"\u25b8"` |  |
| 342 | `Statement.OPENED` | `"\u25be"` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 22 | 358 | **type** `public final class Statement` | The rows a statement is built from: a head, a line, a note, a total, a disclosure that opens, and the two-column book the sector pages use. |

### A STATEMENT WITH TWO COLUMNS. (lines 24-379)

| line | len | member | says |
|---:|---:|---|---|
| 35 | 1 | `public static VBox statementHead(String title)` | A statement's heading, with a rule under it. |
| 38 | 18 | `public static VBox statementHead(String title, double width)` |  |
| 57 | 3 | `public static HBox statementLine(String label, String value)` |  |
| 66 | 19 | `public static HBox statementLine(String label, String value, String tone)` | label stays grey whatever happens, because the label is never the news. |
| 87 | 8 | `public static Label statementNote(String text)` | The sentence under a line - what it means, or where it comes from. |
| 97 | 32 | `public static VBox statementTotal(String label, String value, String tone)` | The line a statement adds up to: a rule, then the figure in full. |
| 135 | 26 | `public static HBox bookHead(String left)` | The column headings, once, at the top of a statement. |
| 169 | 28 | `public static HBox bookLine(String label, double now, double then, boolean known, String tone)` | One line of a set of books: what it is, this month, and last month. |
| 199 | 33 | `public static VBox bookLine(String label, double now, double then, boolean known, String tone, VBox detail, String word)` | A statement line that opens into its parts. |
| 243 | 3 | `public static VBox statementDisclosure(String label, String value, VBox detail)` | A statement line that opens something underneath it. |
| 248 | 31 | `public static VBox statementDisclosure(String label, String value, VBox detail, String hint)` | As above, naming what opening it shows. |
| 281 | 7 | `public static Label bookLine(String label, double value, boolean bold, String colour)` | A statement line: label left, figure right, in one fixed-width column. |
| 289 | 6 | `public static Label bookRule()` |  |
| 297 | 6 | `public static Label bookNote(String text)` | A short grey line under a figure, for the one sentence it needs. |
| 316 | 5 | `public static VBox disclosure(String line, String hint, VBox detail)` | A line you can click to open the working behind it. |
| 323 | 16 | `public static VBox disclosure(String line, String hint, VBox detail, String style)` | As above, keeping a row's own styling - used by the tier table. |
| 344 | 35 | `public static VBox reportSection(String heading, String...rows)` |  |

