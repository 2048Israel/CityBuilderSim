# Statement.java - 482 lines · 23 methods · 5 constants · interface

`ham/citybuildersim/ui/Statement.java` - generated 2026-10-04 by CodeMap; line numbers are as of that run.

> The rows a statement is built from: a head, a line, a note, a total, a
> disclosure that opens, and the two-column book the sector pages and the
> bank's income statement use - its lines able to stay open through the
> clock's redraw since 0.7.9 (opens()).
> 
> Static for the same reason as Money: they hold no state and every screen
> uses them, so they belong to no screen. Called unqualified through an
> import static, exactly as they were when they were methods of the window.

**Uses:** [Palette](Palette.md) (79)

**Used by (21):** [BankCheck](BankCheck.md), [BondCheck](BondCheck.md), [BooksCheck](BooksCheck.md), [EconomyManager](EconomyManager.md), [HouseholdAccounts](HouseholdAccounts.md), [HouseholdCheck](HouseholdCheck.md), [LandScreen](LandScreen.md), [LongPlaytest](LongPlaytest.md), [MiningCheck](MiningCheck.md), [MonthOrder](MonthOrder.md), [NewGameCheck](NewGameCheck.md), [PeopleScreen](PeopleScreen.md), [RailCheck](RailCheck.md), [Sector](Sector.md), [SectorBooks](SectorBooks.md), [SectorBooksCheck](SectorBooksCheck.md), [SectorFlow](SectorFlow.md), [SectorScreen](SectorScreen.md), [SectorState](SectorState.md), [Sectors](Sectors.md), [TradeScreen](TradeScreen.md)

## Sections

| line | section |
|---:|---|
| 26 | A STATEMENT WITH TWO COLUMNS. |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 34 | `Statement.STATEMENT` | `560` | How wide a statement is. |
| 150 | `Statement.BOOK_NOW` | `116` |  |
| 152 | `Statement.BOOK_THEN` | `104` |  |
| 443 | `Statement.CLOSED` | `"\u25b8"` |  |
| 445 | `Statement.OPENED` | `"\u25be"` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 24 | 459 | **type** `public final class Statement` | The rows a statement is built from: a head, a line, a note, a total, a disclosure that opens, and the two-column book the sector pages and the bank's income statement use - its lines able to stay open through the cloc... |

### A STATEMENT WITH TWO COLUMNS. (lines 26-482)

| line | len | member | says |
|---:|---:|---|---|
| 37 | 1 | `public static VBox statementHead(String title)` | A statement's heading, with a rule under it. |
| 40 | 18 | `public static VBox statementHead(String title, double width)` |  |
| 59 | 3 | `public static HBox statementLine(String label, String value)` |  |
| 68 | 19 | `public static HBox statementLine(String label, String value, String tone)` | label stays grey whatever happens, because the label is never the news. |
| 89 | 8 | `public static Label statementNote(String text)` | The sentence under a line - what it means, or where it comes from. |
| 104 | 11 | `public static HBox statementNote(String shown, String whole)` | ...in two layers (0.7.22, the text cut): `shown` on the page, short, and the whole note one click away behind its (i) - nothing deleted, it moves (Pieces, TEXT IN THREE LAYERS). |
| 117 | 32 | `public static VBox statementTotal(String label, String value, String tone)` | The line a statement adds up to: a rule, then the figure in full. |
| 155 | 3 | `public static HBox bookHead(String left)` | The column headings, once, at the top of a statement. |
| 160 | 26 | `public static HBox bookHead(String left, String now, String then)` | ...with the two columns named - the bank's balance sheet sets this month beside "a year ago" (0.7.13). |
| 198 | 28 | `public static HBox bookLine(String label, double now, double then, boolean known, String tone)` | One line of a set of books: what it is, this month, and last month. |
| 228 | 4 | `public static VBox bookLine(String label, double now, double then, boolean known, String tone, VBox detail, String word)` | A statement line that opens into its parts. |
| 237 | 26 | `public static VBox bookLine(String label, double now, double then, boolean known, String tone, VBox detail, String word, java.u...` | ...and remembers whether it was open: see opens(). |
| 274 | 17 | `static void opens(javafx.scene.Node row, Label mark, String word, VBox detail, String key, java.util.Set<String> open)` | A row that opens its detail - DRAWN AS THE SCREEN LAST LEFT IT (0.7.9). |
| 297 | 40 | `public static VBox bookTotal(String label, double now, double then, boolean known, String tone)` | The line a section adds up to: a rule, then this month's figure at full weight and last month's quieter beside it. |
| 348 | 3 | `public static VBox statementDisclosure(String label, String value, VBox detail)` | A statement line that opens something underneath it. |
| 353 | 3 | `public static VBox statementDisclosure(String label, String value, VBox detail, String hint)` | As above, naming what opening it shows. |
| 358 | 24 | `public static VBox statementDisclosure(String label, String value, VBox detail, String hint, java.util.Set<String> open)` | ...and remembering whether it was open: see opens(). |
| 384 | 7 | `public static Label bookLine(String label, double value, boolean bold, String colour)` | A statement line: label left, figure right, in one fixed-width column. |
| 392 | 6 | `public static Label bookRule()` |  |
| 400 | 6 | `public static Label bookNote(String text)` | A short grey line under a figure, for the one sentence it needs. |
| 419 | 5 | `public static VBox disclosure(String line, String hint, VBox detail)` | A line you can click to open the working behind it. |
| 426 | 16 | `public static VBox disclosure(String line, String hint, VBox detail, String style)` | As above, keeping a row's own styling - used by the tier table. |
| 447 | 35 | `public static VBox reportSection(String heading, String...rows)` |  |

