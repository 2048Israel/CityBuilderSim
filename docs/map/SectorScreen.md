# SectorScreen.java - 1,299 lines · 25 methods · 2 constants · interface

`ham/citybuildersim/ui/SectorScreen.java` - generated 2026-09-21 by CodeMap; line numbers are as of that run.

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

**Uses:** [Palette](Palette.md) (136), [Sector](Sector.md) (28), [SectorBooks](SectorBooks.md) (17), [BusinessInvestment](BusinessInvestment.md) (13), [Equity](Equity.md) (5), [Good](Good.md) (4), [BusinessDebtManager](BusinessDebtManager.md) (3), [JobType](JobType.md) (3), [CityCalendar](CityCalendar.md) (3), [Exchange](Exchange.md) (3), [HistorySave](HistorySave.md) (3), [UserInterface](UserInterface.md) (2), [Statement](Statement.md) (2), [SalesTaxLedger](SalesTaxLedger.md) (1)

**Used by (1):** [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 38 | THE SECTOR ECONOMY. |
| 203 | ONE BUSINESS, FIVE PAGES |
| 286 | A STATEMENT LINE THAT OPENS |
| 405 | · THE INCOME STATEMENT |
| 412 | · what the two big lines open into |
| 736 | · · and the ratios |
| 768 | · THE BALANCE SHEET |
| 828 | · · the ratios |
| 980 | · CASH AND CREDIT |
| 1078 | · · credit |
| 1124 | · THE INVESTORS |
| 1144 | · · what it decided |
| 1177 | · · the conditions |
| 1213 | · · and what stops it |
| 1266 | · WHAT IT DOES |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 64 | `SectorScreen.SECTOR_HOME` | `"Operations"` |  |
| 67 | `SectorScreen.SECTOR_PAGES` | `{ "Operations", "Income", "Balance sheet", "Cash & debt", "Investors" }` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 34 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 63 | `Sector openSector` | Which sector's books are open, or null for the list. |
| 65 | `String sectorPage` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 31 | 1269 | **type** `final class SectorScreen` | The sector economy: the businesses as a list, and each one's five pages - operations, the income statement with last month beside it, the balance sheet, cash and credit, and what its investors decided - every line of ... |
| 36 | 1 | `SectorScreen(UserInterface ui)` |  |

### THE SECTOR ECONOMY. (lines 38-202)

| line | len | member | says |
|---:|---:|---|---|
| 71 | 6 | `void openSectorBooks(Sector sector, String page)` | Open one business's books from somewhere else in the game. |
| 79 | 52 | `void showSectorMenu()` | The tab lands here: what every business in the city earned. |
| 139 | 48 | `HBox sectorCard(Sector sector, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then)` | One business on the list: what it kept, which way it moved, and a click in. |
| 189 | 7 | `static String sectorBlurb(Sector sector)` | What the business actually does, in a few words - the sector's own first sentence. |
| 198 | 4 | `static String numberWord(int n)` | "six", "seven" - for the total line, which names the count. |

### ONE BUSINESS, FIVE PAGES (lines 203-285)

| line | len | member | says |
|---:|---:|---|---|
| 207 | 48 | `void drawSectorScreen()` |  |
| 256 | 29 | `HBox sectorVitals(Sector sector, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then)` |  |

### A STATEMENT LINE THAT OPENS (lines 286-404)

| line | len | member | says |
|---:|---:|---|---|
| 318 | 30 | `HBox bookDetailRow(String label, String value, boolean indented, String tone)` | One line inside an opened statement line. |
| 349 | 3 | `HBox bookDetailRow(String label, double amount, boolean indented)` |  |
| 354 | 8 | `Label bookDetailNote(String text)` | A sentence at the bottom of an opened line, when the split has something to say. |
| 364 | 40 | `VBox bookTotal(String label, double now, double then, boolean known, String tone)` | The line a section adds up to: a rule, then the figure at full weight. |

### THE INCOME STATEMENT (lines 405-411)

| line | len | member | says |
|---:|---:|---|---|
| 408 | 3 | `static String inputLabel(Sector sector)` | What this sector's direct cost is actually called - the sector says. |

### what the two big lines open into (lines 412-767)

| line | len | member | says |
|---:|---:|---|---|
| 425 | 64 | `VBox revenueDetail(Sector sector)` | Revenue, by good, each split into what the city took and what was shipped. |
| 501 | 73 | `VBox inputsDetail(Sector sector)` | The cost of sales, by good, each split into what the city grew or made and what was landed. |
| 576 | 37 | `VBox wagesDetail(Sector sector)` | Wages by pay tier - which kind of worker this business is actually paying. |
| 625 | 45 | `VBox salesTaxDetail(Sector sector)` | The sales tax, as the ledger actually strikes it: charged on what was sold here, credited for what suppliers already remitted, and the difference remitted. |
| 671 | 96 | `void incomePage(VBox column, Sector sector, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then)` |  |

### THE BALANCE SHEET (lines 768-979)

| line | len | member | says |
|---:|---:|---|---|
| 770 | 84 | `void balancePage(VBox column, Sector sector, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then)` |  |
| 864 | 86 | `void ownersBlock(VBox column, int company, double bookEquity, double netIncome)` | Who owns a company, what a share is worth, and what it pays. |
| 955 | 24 | `void sharePriceChart(VBox column, int company)` | One company's share price over the city's life: the quote against what the register says a share is worth, both per founding share. |

### CASH AND CREDIT (lines 980-1123)

| line | len | member | says |
|---:|---:|---|---|
| 982 | 141 | `void cashAndDebtPage(VBox column, Sector sector, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then)` |  |

### THE INVESTORS (lines 1124-1265)

| line | len | member | says |
|---:|---:|---|---|
| 1137 | 128 | `void investorPage(VBox column, Sector sector, SectorBooks.SectorMonth now)` |  |

### WHAT IT DOES (lines 1266-1299)

| line | len | member | says |
|---:|---:|---|---|
| 1276 | 10 | `void operationsPage(VBox column, Sector sector)` |  |
| 1288 | 11 | `static String toneColour(Sector.Line.Tone tone)` | The palette colour a sector's line asked for, or none. |

