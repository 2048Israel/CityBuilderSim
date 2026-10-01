# SectorScreen.java - 1,727 lines · 28 methods · 7 constants · interface

`ham/citybuildersim/ui/SectorScreen.java` - generated 2026-10-01 by CodeMap; line numbers are as of that run.

> The sector economy: the businesses as a list, and each one's five pages -
> operations, the income statement with last month beside it, the balance
> sheet, cash and credit, and what its investors decided - every line of the
> statement opening into where the figure came from.
> 
> Split out of UserInterface on 2026-09-18: the three banners THE SECTOR
> ECONOMY, ONE BUSINESS, FIVE PAGES and A STATEMENT LINE THAT OPENS exactly as
> they were, the shell's members reached through ui. The shell still reads
> which sector and page are open (openSector, sectorPage) for the rail and
> the scroll memory, and other screens open a sector's books through
> openSectorBooks().

**Uses:** [Palette](Palette.md) (173), [Sector](Sector.md) (29), [SectorBooks](SectorBooks.md) (18), [BusinessInvestment](BusinessInvestment.md) (15), [CityCalendar](CityCalendar.md) (9), [BusinessDebtManager](BusinessDebtManager.md) (8), [Equity](Equity.md) (6), [OrderBook](OrderBook.md) (6), [Mortgage](Mortgage.md) (6), [HistorySave](HistorySave.md) (4), [Good](Good.md) (4), [JobType](JobType.md) (3), [Exchange](Exchange.md) (3), [Bank](Bank.md) (3), [UserInterface](UserInterface.md) (2), [Statement](Statement.md) (2), [BankScreen](BankScreen.md) (2), [SalesTaxLedger](SalesTaxLedger.md) (1), [BondMarket](BondMarket.md) (1), [Game](Game.md) (1)

**Used by (1):** [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 38 | THE SECTOR ECONOMY. |
| 407 | ONE BUSINESS, FIVE PAGES |
| 488 | A STATEMENT LINE THAT OPENS |
| 568 | · THE INCOME STATEMENT |
| 575 | · what the two big lines open into |
| 911 | · · and the ratios |
| 943 | · THE BALANCE SHEET |
| 1003 | · · the ratios |
| 1192 | · CASH AND CREDIT |
| 1327 | · · credit |
| 1496 | · THE INVESTORS |
| 1516 | · · what it decided |
| 1549 | · · the conditions |
| 1628 | · · and what stops it |
| 1681 | · WHAT IT DOES |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 65 | `SectorScreen.SECTOR_HOME` | `"Operations"` |  |
| 71 | `SectorScreen.SECTOR_PAGES` | `{ "Operations", "Income", "Balance sheet", "Cash & debt", "Investors" }` |  |
| 256 | `SectorScreen.CARD_LEFT` | `280` | The width the card's name and blurb are held to, so the sparkline and the figures always have their room (0.7.4). |
| 259 | `SectorScreen.BLURB_LINES` | `2` | How many lines of its first sentence a sector's card shows; the tooltip has the rest (0.7.20). |
| 262 | `SectorScreen.SPARK_WIDTH` | `90` | The sparkline's width on a sector's card (0.7.4): a word's size, between the blurb and the figures. |
| 265 | `SectorScreen.SPARK_HEIGHT` | `22` | ...and its height, a line of caption and a half. |
| 268 | `SectorScreen.SPARK_MONTHS` | `24` | How many months of net income the sparkline draws (0.7.4): two years. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 34 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 64 | `Sector openSector` | Which sector's books are open, or null for the list. |
| 66 | `String sectorPage` |  |
| 69 | `boolean sectorsExpanded` | Whether every card on the list is open to its second row (0.7.4) - kept while the game runs, like the page, and never saved. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 31 | 1697 | **type** `final class SectorScreen` | The sector economy: the businesses as a list, and each one's five pages - operations, the income statement with last month beside it, the balance sheet, cash and credit, and what its investors decided - every line of ... |
| 36 | 1 | `SectorScreen(UserInterface ui)` |  |

### THE SECTOR ECONOMY. (lines 38-406)

| line | len | member | says |
|---:|---:|---|---|
| 75 | 6 | `void openSectorBooks(Sector sector, String page)` | Open one business's books from somewhere else in the game. |
| 83 | 78 | `void showSectorMenu()` | The tab lands here: what every business in the city earned. |
| 179 | 75 | `VBox sectorCard(Sector sector, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then, List<? extends Number> netIncomeSeries)` | One business on the list: what it kept, which way it moved, and a click in. |
| 286 | 69 | `javafx.scene.Node sparkline(List<? extends Number> series)` | A sector's net income as a line (0.7.4): the last SPARK_MONTHS of the history's netIncome:<sector>, the zero line faint under it. |
| 362 | 17 | `HBox sectorCardMore(Sector sector, SectorBooks.SectorMonth now)` | The card's second row, with the list opened (0.7.4): five figures at the caption's size - revenue, margin, cash, what it owes, and its workers against the posts it offers - each off SectorMonth or the sector, nothing ... |
| 381 | 11 | `static VBox moreCell(String word, String figure, String tone)` | One labelled figure on a card's second row: the word over the figure, a fifth of the card wide. |
| 394 | 6 | `static String sectorBlurb(Sector sector)` | What the business actually does - the sector's own first sentence, whole (cut at 72 until 0.7.20). |
| 402 | 4 | `static String numberWord(int n)` | "six", "seven" - for the total line, which names the count. |

### ONE BUSINESS, FIVE PAGES (lines 407-487)

| line | len | member | says |
|---:|---:|---|---|
| 411 | 46 | `void drawSectorScreen()` |  |
| 458 | 29 | `HBox sectorVitals(Sector sector, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then)` |  |

### A STATEMENT LINE THAT OPENS (lines 488-567)

| line | len | member | says |
|---:|---:|---|---|
| 520 | 30 | `HBox bookDetailRow(String label, String value, boolean indented, String tone)` | One line inside an opened statement line. |
| 551 | 3 | `HBox bookDetailRow(String label, double amount, boolean indented)` |  |
| 556 | 8 | `Label bookDetailNote(String text)` | A sentence at the bottom of an opened line, when the split has something to say. |

### THE INCOME STATEMENT (lines 568-574)

| line | len | member | says |
|---:|---:|---|---|
| 571 | 3 | `static String inputLabel(Sector sector)` | What this sector's direct cost is actually called - the sector says. |

### what the two big lines open into (lines 575-942)

| line | len | member | says |
|---:|---:|---|---|
| 588 | 64 | `VBox revenueDetail(Sector sector)` | Revenue, by good, each split into what the city took and what was shipped. |
| 664 | 73 | `VBox inputsDetail(Sector sector)` | The cost of sales, by good, each split into what the city grew or made and what was landed. |
| 739 | 49 | `VBox wagesDetail(Sector sector)` | Wages by pay tier - which kind of worker this business is actually paying. |
| 800 | 45 | `VBox salesTaxDetail(Sector sector)` | The sales tax, as the ledger actually strikes it: charged on what was sold here, credited for what suppliers already remitted, and the difference remitted. |
| 846 | 96 | `void incomePage(VBox column, Sector sector, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then)` |  |

### THE BALANCE SHEET (lines 943-1191)

| line | len | member | says |
|---:|---:|---|---|
| 945 | 84 | `void balancePage(VBox column, Sector sector, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then)` |  |
| 1039 | 120 | `void ownersBlock(VBox column, int company, double bookEquity, double netIncome)` | Who owns a company, what a share is worth, and what it pays. |
| 1165 | 26 | `void sharePriceChart(VBox column, int company)` | One company's share price over the city's life: the last trade (the desk's quote until 0.7.12 round 2) against what the register says a share is worth, both per founding share. |

### CASH AND CREDIT (lines 1192-1495)

| line | len | member | says |
|---:|---:|---|---|
| 1194 | 301 | `void cashAndDebtPage(VBox column, Sector sector, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then)` |  |

### THE INVESTORS (lines 1496-1680)

| line | len | member | says |
|---:|---:|---|---|
| 1509 | 171 | `void investorPage(VBox column, Sector sector, SectorBooks.SectorMonth now)` |  |

### WHAT IT DOES (lines 1681-1727)

| line | len | member | says |
|---:|---:|---|---|
| 1691 | 12 | `void operationsPage(VBox column, Sector sector)` |  |
| 1709 | 5 | `static HBox noteInLayers(String shown, String whole)` | A sector's note in two layers (0.7.21; Sector.Line.note(shown, whole)): the short line where statementNote() would put the paragraph, and the paragraph behind its (i). |
| 1716 | 11 | `static String toneColour(Sector.Line.Tone tone)` | The palette colour a sector's line asked for, or none. |

