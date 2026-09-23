# SectorScreen.java - 1,461 lines · 28 methods · 6 constants · interface

`ham/citybuildersim/ui/SectorScreen.java` - generated 2026-09-23 by CodeMap; line numbers are as of that run.

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

**Uses:** [Palette](Palette.md) (167), [Sector](Sector.md) (29), [SectorBooks](SectorBooks.md) (18), [BusinessInvestment](BusinessInvestment.md) (13), [Equity](Equity.md) (5), [HistorySave](HistorySave.md) (4), [Good](Good.md) (4), [BusinessDebtManager](BusinessDebtManager.md) (3), [JobType](JobType.md) (3), [CityCalendar](CityCalendar.md) (3), [Exchange](Exchange.md) (3), [UserInterface](UserInterface.md) (2), [Statement](Statement.md) (2), [SalesTaxLedger](SalesTaxLedger.md) (1)

**Used by (1):** [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 38 | THE SECTOR ECONOMY. |
| 365 | ONE BUSINESS, FIVE PAGES |
| 448 | A STATEMENT LINE THAT OPENS |
| 567 | · THE INCOME STATEMENT |
| 574 | · what the two big lines open into |
| 898 | · · and the ratios |
| 930 | · THE BALANCE SHEET |
| 990 | · · the ratios |
| 1142 | · CASH AND CREDIT |
| 1240 | · · credit |
| 1286 | · THE INVESTORS |
| 1306 | · · what it decided |
| 1339 | · · the conditions |
| 1375 | · · and what stops it |
| 1428 | · WHAT IT DOES |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 65 | `SectorScreen.SECTOR_HOME` | `"Operations"` |  |
| 71 | `SectorScreen.SECTOR_PAGES` | `{ "Operations", "Income", "Balance sheet", "Cash & debt", "Investors" }` |  |
| 245 | `SectorScreen.CARD_LEFT` | `280` | The width the card's name and blurb are held to, so the sparkline and the figures always have their room (0.7.4). |
| 248 | `SectorScreen.SPARK_WIDTH` | `90` | The sparkline's width on a sector's card (0.7.4): a word's size, between the blurb and the figures. |
| 251 | `SectorScreen.SPARK_HEIGHT` | `22` | ...and its height, a line of caption and a half. |
| 254 | `SectorScreen.SPARK_MONTHS` | `24` | How many months of net income the sparkline draws (0.7.4): two years. |

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
| 31 | 1431 | **type** `final class SectorScreen` | The sector economy: the businesses as a list, and each one's five pages - operations, the income statement with last month beside it, the balance sheet, cash and credit, and what its investors decided - every line of ... |
| 36 | 1 | `SectorScreen(UserInterface ui)` |  |

### THE SECTOR ECONOMY. (lines 38-364)

| line | len | member | says |
|---:|---:|---|---|
| 75 | 6 | `void openSectorBooks(Sector sector, String page)` | Open one business's books from somewhere else in the game. |
| 83 | 80 | `void showSectorMenu()` | The tab lands here: what every business in the city earned. |
| 181 | 62 | `VBox sectorCard(Sector sector, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then, List<? extends Number> netIncomeSeries)` | One business on the list: what it kept, which way it moved, and a click in. |
| 263 | 49 | `javafx.scene.Node sparkline(List<? extends Number> series)` | A sector's net income as a line (0.7.4): the last SPARK_MONTHS of the history's netIncome:<sector>, the zero line faint under it, the line GOOD when the latest month made money and BAD when it did not. |
| 319 | 17 | `HBox sectorCardMore(Sector sector, SectorBooks.SectorMonth now)` | The card's second row, with the list opened (0.7.4): five figures at the caption's size - revenue, margin, cash, what it owes, and its workers against the posts it offers - each off SectorMonth or the sector, nothing ... |
| 338 | 11 | `static VBox moreCell(String word, String figure, String tone)` | One labelled figure on a card's second row: the word over the figure, a fifth of the card wide. |
| 351 | 7 | `static String sectorBlurb(Sector sector)` | What the business actually does, in a few words - the sector's own first sentence. |
| 360 | 4 | `static String numberWord(int n)` | "six", "seven" - for the total line, which names the count. |

### ONE BUSINESS, FIVE PAGES (lines 365-447)

| line | len | member | says |
|---:|---:|---|---|
| 369 | 48 | `void drawSectorScreen()` |  |
| 418 | 29 | `HBox sectorVitals(Sector sector, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then)` |  |

### A STATEMENT LINE THAT OPENS (lines 448-566)

| line | len | member | says |
|---:|---:|---|---|
| 480 | 30 | `HBox bookDetailRow(String label, String value, boolean indented, String tone)` | One line inside an opened statement line. |
| 511 | 3 | `HBox bookDetailRow(String label, double amount, boolean indented)` |  |
| 516 | 8 | `Label bookDetailNote(String text)` | A sentence at the bottom of an opened line, when the split has something to say. |
| 526 | 40 | `VBox bookTotal(String label, double now, double then, boolean known, String tone)` | The line a section adds up to: a rule, then the figure at full weight. |

### THE INCOME STATEMENT (lines 567-573)

| line | len | member | says |
|---:|---:|---|---|
| 570 | 3 | `static String inputLabel(Sector sector)` | What this sector's direct cost is actually called - the sector says. |

### what the two big lines open into (lines 574-929)

| line | len | member | says |
|---:|---:|---|---|
| 587 | 64 | `VBox revenueDetail(Sector sector)` | Revenue, by good, each split into what the city took and what was shipped. |
| 663 | 73 | `VBox inputsDetail(Sector sector)` | The cost of sales, by good, each split into what the city grew or made and what was landed. |
| 738 | 37 | `VBox wagesDetail(Sector sector)` | Wages by pay tier - which kind of worker this business is actually paying. |
| 787 | 45 | `VBox salesTaxDetail(Sector sector)` | The sales tax, as the ledger actually strikes it: charged on what was sold here, credited for what suppliers already remitted, and the difference remitted. |
| 833 | 96 | `void incomePage(VBox column, Sector sector, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then)` |  |

### THE BALANCE SHEET (lines 930-1141)

| line | len | member | says |
|---:|---:|---|---|
| 932 | 84 | `void balancePage(VBox column, Sector sector, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then)` |  |
| 1026 | 86 | `void ownersBlock(VBox column, int company, double bookEquity, double netIncome)` | Who owns a company, what a share is worth, and what it pays. |
| 1117 | 24 | `void sharePriceChart(VBox column, int company)` | One company's share price over the city's life: the quote against what the register says a share is worth, both per founding share. |

### CASH AND CREDIT (lines 1142-1285)

| line | len | member | says |
|---:|---:|---|---|
| 1144 | 141 | `void cashAndDebtPage(VBox column, Sector sector, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then)` |  |

### THE INVESTORS (lines 1286-1427)

| line | len | member | says |
|---:|---:|---|---|
| 1299 | 128 | `void investorPage(VBox column, Sector sector, SectorBooks.SectorMonth now)` |  |

### WHAT IT DOES (lines 1428-1461)

| line | len | member | says |
|---:|---:|---|---|
| 1438 | 10 | `void operationsPage(VBox column, Sector sector)` |  |
| 1450 | 11 | `static String toneColour(Sector.Line.Tone tone)` | The palette colour a sector's line asked for, or none. |

