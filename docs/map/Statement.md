# Statement.java - 517 lines · 25 methods · 5 constants · interface

`ham/citybuildersim/ui/Statement.java` - generated 2026-11-13 by CodeMap; line numbers are as of that run.

> The rows a statement is built from: a head, a line, a note, a total, a
> disclosure that opens, and the two-column book the sector pages and the
> bank's income statement use - its lines able to stay open through the
> clock's redraw since 0.7.9 (opens()).
> 
> Static for the same reason as Money: they hold no state and every screen
> uses them, so they belong to no screen. Called unqualified through an
> import static, exactly as they were when they were methods of the window.

**Uses:** [Palette](Palette.md) (79), [Kept](Kept.md) (54)

**Used by (30):** [AutoBuilder](AutoBuilder.md), [BankCheck](BankCheck.md), [BankScreen](BankScreen.md), [BondCheck](BondCheck.md), [BooksCheck](BooksCheck.md), [CapitalFlowCheck](CapitalFlowCheck.md), [CopperRefiningCheck](CopperRefiningCheck.md), [EconomyManager](EconomyManager.md), [Forms1900Check](Forms1900Check.md), [HouseholdAccounts](HouseholdAccounts.md), [HouseholdCheck](HouseholdCheck.md), [LandScreen](LandScreen.md), [LongPlaytest](LongPlaytest.md), [MaterialsCheck](MaterialsCheck.md), [MiningCheck](MiningCheck.md), [MonthOrder](MonthOrder.md), [NewGameCheck](NewGameCheck.md), [PeopleScreen](PeopleScreen.md), [RailCheck](RailCheck.md), [Sector](Sector.md), [SectorBooks](SectorBooks.md), [SectorBooksCheck](SectorBooksCheck.md), [SectorFlow](SectorFlow.md), [SectorScreen](SectorScreen.md), [SectorState](SectorState.md), [Sectors](Sectors.md), [StatementView](StatementView.md), [TradeScreen](TradeScreen.md), [WellCheck](WellCheck.md), [WoodProductsCheck](WoodProductsCheck.md)

## Sections

| line | section |
|---:|---|
| 26 | A STATEMENT WITH TWO COLUMNS. |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 34 | `Statement.STATEMENT` | `560` | How wide a statement is. |
| 159 | `Statement.BOOK_NOW` | `116` |  |
| 161 | `Statement.BOOK_THEN` | `104` |  |
| 473 | `Statement.CLOSED` | `"\u25b8"` |  |
| 475 | `Statement.OPENED` | `"\u25be"` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 24 | 494 | **type** `public final class Statement` | The rows a statement is built from: a head, a line, a note, a total, a disclosure that opens, and the two-column book the sector pages and the bank's income statement use - its lines able to stay open through the cloc... |

### A STATEMENT WITH TWO COLUMNS. (lines 26-517)

| line | len | member | says |
|---:|---:|---|---|
| 37 | 1 | `public static VBox statementHead(String title)` | A statement's heading, with a rule under it. |
| 40 | 21 | `public static VBox statementHead(String title, double width)` |  |
| 62 | 3 | `public static HBox statementLine(String label, String value)` |  |
| 71 | 21 | `public static HBox statementLine(String label, String value, String tone)` | label stays grey whatever happens, because the label is never the news. |
| 94 | 8 | `public static Label statementNote(String text)` | The sentence under a line - what it means, or where it comes from. |
| 109 | 13 | `public static HBox statementNote(String shown, String whole)` | ...in two layers (0.7.22, the text cut): `shown` on the page, short, and the whole note one click away behind its (i) - nothing deleted, it moves (Pieces, TEXT IN THREE LAYERS). |
| 124 | 34 | `public static VBox statementTotal(String label, String value, String tone)` | The line a statement adds up to: a rule, then the figure in full. |
| 164 | 3 | `public static HBox bookHead(String left)` | The column headings, once, at the top of a statement. |
| 169 | 29 | `public static HBox bookHead(String left, String now, String then)` | ...with the two columns named - the bank's balance sheet sets this month beside "a year ago" (0.7.13). |
| 210 | 3 | `public static HBox bookLine(String label, double now, double then, boolean known, String tone)` | One line of a set of books: what it is, this month, and last month. |
| 215 | 30 | `private static HBox bookLine(String label, double now, double then, boolean known, String tone, String style)` | ...in a style of its own (KP2b: the hand, for a line that opens), given where the line is made and not set again after it. |
| 247 | 4 | `public static VBox bookLine(String label, double now, double then, boolean known, String tone, VBox detail, String word)` | A statement line that opens into its parts. |
| 256 | 30 | `public static VBox bookLine(String label, double now, double then, boolean known, String tone, VBox detail, String word, java.u...` | ...and remembers whether it was open: see opens(). |
| 297 | 19 | `static void opens(javafx.scene.Node row, Label mark, String word, VBox detail, String key, java.util.Set<String> open)` | A row that opens its detail - DRAWN AS THE SCREEN LAST LEFT IT (0.7.9). |
| 322 | 42 | `public static VBox bookTotal(String label, double now, double then, boolean known, String tone)` | The line a section adds up to: a rule, then this month's figure at full weight and last month's quieter beside it. |
| 375 | 3 | `public static VBox statementDisclosure(String label, String value, VBox detail)` | A statement line that opens something underneath it. |
| 380 | 3 | `public static VBox statementDisclosure(String label, String value, VBox detail, String hint)` | As above, naming what opening it shows. |
| 385 | 27 | `public static VBox statementDisclosure(String label, String value, VBox detail, String hint, java.util.Set<String> open)` | ...and remembering whether it was open: see opens(). |
| 414 | 7 | `public static Label bookLine(String label, double value, boolean bold, String colour)` | A statement line: label left, figure right, in one fixed-width column. |
| 422 | 6 | `public static Label bookRule()` |  |
| 430 | 6 | `public static Label bookNote(String text)` | A short grey line under a figure, for the one sentence it needs. |
| 449 | 5 | `public static VBox disclosure(String line, String hint, VBox detail)` | A line you can click to open the working behind it. |
| 456 | 16 | `public static VBox disclosure(String line, String hint, VBox detail, String style)` | As above, keeping a row's own styling - used by the tier table. |
| 477 | 4 | `public static VBox reportSection(String heading, String...rows)` |  |
| 482 | 35 | `private static VBox reportSectionOf(String heading, String[] rows)` |  |

