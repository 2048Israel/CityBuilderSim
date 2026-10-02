# SectorScreen.java - 3,221 lines · 109 methods · 22 constants · interface

`ham/citybuildersim/ui/SectorScreen.java` - generated 2026-10-02 by CodeMap; line numbers are as of that run.

> The sector economy: every business in the city as a card, and each one's
> five pages - what goes in and what comes out, the income statement with
> last month beside it, the balance sheet and its owners, cash and credit,
> and what its investors decided and why.
> 
> Split out of UserInterface on 2026-09-18 (the shell still reads which
> sector and page are open, openSector and sectorPage, for the rail and the
> scroll memory; other screens open a sector's books through
> openSectorBooks()). REDRAWN IN BUILD'S STYLE in 0.7.30 (the project's
> spec-sectors-0730.md): the list was fifteen 560 px rows of a figure and a
> blurb, and each page a statement column of label-and-figure lines under
> grey paragraphs. The pictures lead now - fifteen cards under four figures,
> each with a running-at bar and its investors' word; a business as inputs →
> the plant (its rate as a ring, the six throttles as a cascade) → outputs; a
> waterfall over the income statement; the balance sheet as two bars and an
> owners card; the month's cash as a bridge; and the investors' decision as
> one line with each building's first gate. The statements stay, whole, and
> still open into their parts; every paragraph is behind an (i).

**Uses:** [Palette](Palette.md) (404), [Sector](Sector.md) (102), [Icons](Icons.md) (40), [BuildCard](BuildCard.md) (37), [SectorFlow](SectorFlow.md) (32), [BuildScreen](BuildScreen.md) (31), [SectorBooks](SectorBooks.md) (29), [BusinessDebtManager](BusinessDebtManager.md) (19), [BusinessInvestment](BusinessInvestment.md) (17), [CityCalendar](CityCalendar.md) (11), [Formats](Formats.md) (11), [Good](Good.md) (9), [Equity](Equity.md) (7), [OrderBook](OrderBook.md) (7), [Bank](Bank.md) (6), [Mortgage](Mortgage.md) (6), [HistorySave](HistorySave.md) (4), [BuildAdvice](BuildAdvice.md) (4), [JobType](JobType.md) (3), [Exchange](Exchange.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (3), [UserInterface](UserInterface.md) (2), [Game](Game.md) (2), [Statement](Statement.md) (2), [BankScreen](BankScreen.md) (2), [SalesTaxLedger](SalesTaxLedger.md) (1), [BondMarket](BondMarket.md) (1), [BuildingManager](BuildingManager.md) (1), [RealEstate](RealEstate.md) (1)

**Used by (4):** [FundScreen](FundScreen.md), [HistoryScreen](HistoryScreen.md), [TradeScreen](TradeScreen.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 48 | THE SECTOR ECONOMY. |
| 186 | · THE LIST (0.7.30) |
| 630 | · THE INVESTORS' WORD, IN A KIND (0.7.30) |
| 821 | ONE BUSINESS, FIVE PAGES |
| 961 | · the screen's own pieces |
| 1146 | A STATEMENT LINE THAT OPENS |
| 1227 | · THE INCOME STATEMENT |
| 1234 | · what the two big lines open into |
| 1505 | INCOME (0.7.30): A WATERFALL OVER THE STATEMENT |
| 1659 | THE BALANCE SHEET (0.7.30): TWO BARS AND ITS OWNERS |
| 2024 | CASH AND CREDIT (0.7.30): A BRIDGE, AND WHAT ITS BORROWING COSTS |
| 2470 | THE INVESTORS (0.7.30): WHY ISN'T IT BUILDING, AND WHAT WOULD LET IT? |
| 2502 | · · what it decided |
| 2509 | · · its buildings |
| 2518 | · · and what stops it |
| 2547 | · · the rules |
| 2553 | · · what you control |
| 2808 | OPERATIONS (0.7.30): INPUTS → THE PLANT → OUTPUTS |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 86 | `SectorScreen.SECTOR_HOME` | `"Operations"` | The page a business opens on, and falls back to for a page it does not know. |
| 93 | `SectorScreen.SECTOR_PAGES` | `{ "Operations", "Income", "Balance sheet", "Cash & debt", "Investors" }` | The five pages, in the strip's order: the names other screens open a business's books by. |
| 97 | `SectorScreen.PAGE_ICONS` | `{ Icons.INDUSTRY, Icons.COIN, Icons.FINANCES, Icons.BANK, Icons.SECTOR }` | Each page's icon on its chip (0.7.30): the plant, the coin, the ledger, the bank, the investors. |
| 109 | `SectorScreen.FRAME_CHROME` | `276` | How much of the stage a business's fixed frame takes above its page's scroller - the head, the five figures, the investors' line, the pages, and their gaps - until the frame is laid out and its own height is read (0.7... |
| 112 | `SectorScreen.LIST_CHROME` | `190` | ...and the list's: its head and its four figures (0.7.30). |
| 115 | `SectorScreen.STAGE_REST` | `36` | What the menu spends around the frame and the page: its padding over and under them and the gap between, and the four pixels of slack its height is held to (UserInterface's rootMenu) - so the page is what is left unde... |
| 118 | `SectorScreen.COUNT_MILLIS` | `400` | How long a figure takes to count from last month's to this month's (0.7.30). |
| 121 | `SectorScreen.SWEEP_MILLIS` | `700` | ...and the plant's ring to sweep up from nothing (0.7.30). |
| 191 | `SectorScreen.LIST_INFO` | `"What the city's businesses kept this month, after tax - each card one busine...` | What the list's (i) holds: the old caption, and what a card does. |
| 198 | `SectorScreen.NOTHING_YET_LIST` | `"The sector books are written when a month closes.This city has not " + "clos...` | The empty books' sentence (the old list's alert). |
| 202 | `SectorScreen.NOTHING_YET_SECTOR` | `"This city has not closed a month since it was loaded, so there is no " + "st...` | ...and a business's (the old page's alert). |
| 206 | `SectorScreen.NO_WORD_YET_LIST` | `"Nothing recorded since the city was loaded or founded: a month on, each " + ...` | The list's one line while no business has a word for the month (0.7.34), in place of each card's own. |
| 378 | `SectorScreen.RUNNING_INFO` | `"How much of what its plants could make each business made this month: its " ...` | RUNNING AT's (i). |
| 491 | `SectorScreen.SPARK_WIDTH` | `120` | The width the sparkline takes on a card (0.7.30; 90 on the old list). |
| 494 | `SectorScreen.SPARK_HEIGHT` | `28` | ...and its height (0.7.30; 22 on the old list). |
| 497 | `SectorScreen.SPARK_MONTHS` | `24` | How many months of net income the sparkline draws (0.7.4): two years. |
| 1520 | `SectorScreen.OPERATING_INFO` | `"What the business made from trading, before it pays for the ground it " + "s...` | Operating income's (i): the old page's sentence under it. |
| 1524 | `SectorScreen.REFUND_INFO` | `"The sales tax line is a REFUND this month: the credit on what this sector " ...` | The sales tax refund's (i). |
| 2488 | `SectorScreen.CONTROL_INFO` | `"None of this is yours to set.These are private companies deciding for " + "t...` | WHAT YOU CONTROL's (i): the old page's last paragraph. |
| 2493 | `SectorScreen.WAITING_INFO` | `"This business wants to build and there is nowhere to put it.It is the one " ...` | The waiting-on-ground alert's (i). |
| 2832 | `SectorScreen.FLOW_INFO` | `"Each row's money is the month the books closed on - the Income page's - and ...` | The flow's money and units: which month each is (SectorFlow's two months). |
| 2898 | `SectorScreen.FOLD_PAST` | `6` | The goods a column folds into one row past this many (the shops' and the kitchens' thirteen foods). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 44 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 84 | `Sector openSector` | Which sector's books are open, or null for the list. |
| 87 | `String sectorPage` |  |
| 90 | `boolean sectorsExpanded` | Whether every card on the list is open to its second row (0.7.4) - kept while the game runs, like the page, and never saved. |
| 100 | `private final java.util.Set<String> detailsOpen` | Which "details" folds are open - kept while the game runs, not saved (0.7.30). |
| 103 | `private final java.util.Map<String, java.util.Set<String>> linesOpen` | Which statement lines are open, by sector and then by label (0.7.30: a waterfall's bar opens its line). |
| 106 | `private final java.util.Map<String, Integer> drawnAt` | The month each view was last drawn at: its figures count up and its ring sweeps once a month, not on every redraw. |
| 184 | `private javafx.scene.control.ScrollPane body` | The page's scroller. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 41 | 3181 | **type** `final class SectorScreen` | The sector economy: every business in the city as a card, and each one's five pages - what goes in and what comes out, the income statement with last month beside it, the balance sheet and its owners, cash and credit,... |
| 46 | 1 | `SectorScreen(UserInterface ui)` |  |

### THE SECTOR ECONOMY. (lines 48-185)

| line | len | member | says |
|---:|---:|---|---|
| 124 | 6 | `void openSectorBooks(Sector sector, String page)` | Open one business's books from somewhere else in the game. |
| 132 | 5 | `void open(String page)` | One of the open business's pages, at its top. |
| 139 | 5 | `void backToList()` | Back to the list, at its top. |
| 146 | 8 | `void showSectorMenu()` | The tab lands here: what every business in the city earned. |
| 156 | 4 | `private boolean freshMonth(String view)` | Whether this draw of a view is the first since its month changed - the count-ups and the sweep run then and on no other redraw. |
| 162 | 20 | `private void frameOver(VBox frame, VBox page, double chrome)` | The fixed frame over a scrolling page, at the page's width: the shape every redrawn tab has (Infrastructure's, 0.7.29). |

### THE LIST (0.7.30) (lines 186-629)

| line | len | member | says |
|---:|---:|---|---|
| 209 | 34 | `void listScreen()` |  |
| 250 | 6 | `static boolean noWordYet(Game game, List<Sector> order)` | Whether no business has a word for the month (0.7.34): every sector's Game.getLastInvestment() empty, as it is from a load or a founding until a month runs. |
| 258 | 7 | `List<Sector> marketOrder()` | The sectors in Build's market order: by the group their own buildings are under (BuildCard.groupRank()), then as Sectors lists them. |
| 273 | 23 | `HBox listVitals(SectorBooks books, List<Sector> order, boolean animate)` | The list's four figures: what they all kept, with its move on last month; how many lost, the two that lost most named; who works there, against the posts; and the range they run at, over every business with a plant an... |
| 304 | 3 | **type** `record ListFigures(double kept, double before, boolean compared, String moved, String moveTone, String kept...` | The list's four figures as words, off the books and the sectors' plants - a pure read, for the strip and the probes: what they kept and their move, the sectors losing and the words naming them, the workers and posts, ... |
| 308 | 68 | `static ListFigures listFigures(SectorBooks books, List<Sector> order)` |  |
| 394 | 70 | `VBox sectorCard(Sector sector, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then, List<? extends Number> netIncomeSerie...` | One business on the list (0.7.30): its icon, its name with the Build group under it and its blurb on hover, what it kept and its move; its last two years, its workers, and how much of its plant runs with what cuts it ... |
| 466 | 7 | `static String runningWords(SectorFlow.Plant plant)` | A card's plant in words: "running at 39% · roads 64%" - the throttle that cuts it most, when one is under 100% - or the homes let, or "no plant standing". |
| 475 | 6 | `String groupWords(Sector sector)` | "Industry · food mills": the Build category and the group a sector's own buildings are under, once when they are one. |
| 483 | 6 | `static Label caption(String text, String tone)` | A line of caption that wraps, in a colour. |
| 515 | 69 | `javafx.scene.Node sparkline(List<? extends Number> series)` | A sector's net income as a line (0.7.4): the last SPARK_MONTHS of the history's netIncome:<sector>, the zero line faint under it. |
| 590 | 18 | `GridPane sectorCardMore(Sector sector, SectorBooks.SectorMonth now)` | The card's five figures more, with the list opened (0.7.4; three and two since 0.7.30): revenue, margin and cash; what it owes and its posts filled - each off SectorMonth or the sector, nothing recomputed. |
| 610 | 11 | `static VBox moreCell(String word, String figure, String tone)` | One labelled figure on a card's second row: the word over the figure, 120 wide. |
| 623 | 6 | `static String sectorBlurb(Sector sector)` | What the business actually does - the sector's own first sentence, whole (cut at 72 until 0.7.20). |

### THE INVESTORS' WORD, IN A KIND (0.7.30) (lines 630-820)

| line | len | member | says |
|---:|---:|---|---|
| 642 | 15 | `static String kindIcon(BuildCard.WordKind k)` |  |
| 659 | 8 | `static String kindColour(BuildCard.WordKind k)` | The kind's colour: its edge and its icon. |
| 669 | 16 | `static String kindWord(BuildCard.WordKind k)` | ...and its word, before the sector's. |
| 687 | 6 | `String sectorWords(BuildCard.SectorInvestors si)` | The words after the kind: what is on site of theirs, the build card's "nothing recorded" before a month has run, or the word with "Holding: " gone. |
| 695 | 5 | `static String shortWord(String words)` | A word's short form for a card: a "Built" or "Sold" word up to its " - " (the frame's line and the tooltip have the rest). |
| 702 | 10 | `static String builtTag(String word)` | "+2 Industrial Bakery": what a "Built" word built, for the month's flash; null for any other word. |
| 717 | 29 | `Node investorsMini(BuildCard.SectorInvestors si, boolean blank)` | The word on a card: the kind's icon at 12, its word, the sector's - wrapping, never cut; a click opens the Investors page. |
| 748 | 4 | `static String textColour(BuildCard.WordKind k)` | A kind's word in a colour that reads as text: the grey kinds' edge is too faint for words. |
| 761 | 34 | `VBox investorsLine(BuildCard.SectorInvestors si, boolean animate, double iconBox)` | The investors' line (0.7.30): under every page's strip, 36 high on the raised ground with a 3 px edge in the kind's colour - its icon in a tinted square, the kind in a word, the sector's word whole, and "Investors ›" ... |
| 797 | 10 | `static HBox kindRow(BuildCard.WordKind k, String words, double box)` | One kind's row: its icon square, its word and the words after it, wrapping. |
| 809 | 11 | `static void flash(Region r)` | A region's one flash in the building pink: a "Built" month landing (0.7.30). |

### ONE BUSINESS, FIVE PAGES (lines 821-960)

| line | len | member | says |
|---:|---:|---|---|
| 833 | 33 | `void drawSectorScreen()` |  |
| 868 | 35 | `HBox sectorHead(Sector sector)` | The head: "Sectors ›" - the way back - the business's icon and name, its blurb behind an (i), and a door to its buildings on Build. |
| 911 | 32 | `HBox sectorVitals(Sector sector, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then, SectorFlow.Plant plant, boolean ani...` | The five figures: KEPT with its move, MARGIN, CASH, OWES - "owes nothing" when it owes nothing, the rate and leverage when it owes something (B8) - and RUNNING AT with the throttle that cuts it most, or the homes let ... |
| 945 | 5 | `static String owesWords(boolean blocked, int blockedMonths, SectorBooks.SectorMonth now)` | OWES's note: the ban, "owes nothing" when it owes nothing (B8: it quoted a rate on no debt), or its rate and leverage. |
| 952 | 8 | `static String[] runningCell(SectorFlow.Plant plant)` | The frame's fifth figure, {label, figure, note}: what it runs at and what cuts it most; the landlords' homes let; or no plant. |

### the screen's own pieces (lines 961-1145)

| line | len | member | says |
|---:|---:|---|---|
| 964 | 1 | `static String m(double thousands)` | Money in the model's thousands, as the statements write it: "$13.6M". |
| 967 | 1 | `static String marginWords(double share)` | A margin to a tenth of a per cent, with no sign on one that rounds to nothing (B11). |
| 970 | 10 | `static VBox kpi(String label, String value, String note, String tone, String info, String where, Runnable go)` | A limit cell with an (i) after its label when `info` is not null. |
| 982 | 1 | `static Label figureOf(VBox cell)` | A limit cell's figure, for its count-up. |
| 985 | 5 | `static void noteTone(VBox cell, String tone)` | A limit cell's note in a colour of its own: a move on last month, up or down. |
| 992 | 11 | `static void countUp(Label label, double from, double to, java.util.function.DoubleFunction<String> words)` | A figure counted from last month's to this month's over COUNT_MILLIS, ending on this month's exactly (0.7.30). |
| 1005 | 12 | `static Region sweep(Region ring)` | A ring (Pieces.ring()) whose arc sweeps up from nothing over SWEEP_MILLIS - there is no last month's rate to sweep from (0.7.30). |
| 1019 | 16 | `static HBox alertLine(String svg, String colour, String text, String info, Node right)` | One line that something is wrong or waiting: its icon in a colour, a few words, and the whole of it behind an (i). |
| 1037 | 8 | `static VBox card(Node...children)` | A card on the raised ground: a picture's frame. |
| 1047 | 14 | `static HBox head(String text, String info, Node right)` | A picture's caption in capitals, with an (i) after it when `info` is not null, and something at its right. |
| 1063 | 6 | `static Label figure(String text, double size, String tone)` | A figure, never cut, at a size, in a colour. |
| 1071 | 9 | `static Node withInfo(Node line, String info)` | An (i) after a statement line's label: the paragraph that sat under it (0.7.30). |
| 1082 | 3 | `java.util.Set<String> linesOpen(Sector sector)` | The statement lines open on one business's pages. |
| 1087 | 13 | `static HBox ratioRow(String name, String value, String colour, String info)` | A ratio as a chip in a row: its name, the chip, and its (i). |
| 1102 | 9 | `static VBox ratioColumn(String title, Node...rows)` | A ratio column, 300 wide, beside a statement. |
| 1113 | 8 | `static HBox statementAndRatios(VBox statement, VBox ratios)` | A statement column beside its ratios: the statement at its old width, the ratios to its right. |
| 1123 | 15 | `static HBox keyPart(String colour, String name, String figure)` | One part of a bar's key: its swatch, its name and its figure. |
| 1140 | 5 | `static javafx.scene.layout.FlowPane key(List<HBox> parts)` | A bar's key, wrapping. |

### A STATEMENT LINE THAT OPENS (lines 1146-1226)

| line | len | member | says |
|---:|---:|---|---|
| 1179 | 30 | `HBox bookDetailRow(String label, String value, boolean indented, String tone)` | One line inside an opened statement line. |
| 1210 | 3 | `HBox bookDetailRow(String label, double amount, boolean indented)` |  |
| 1215 | 8 | `Label bookDetailNote(String text)` | A sentence at the bottom of an opened line, when the split has something to say. |

### THE INCOME STATEMENT (lines 1227-1233)

| line | len | member | says |
|---:|---:|---|---|
| 1230 | 3 | `static String inputLabel(Sector sector)` | What this sector's direct cost is actually called - the sector says. |

### what the two big lines open into (lines 1234-1504)

| line | len | member | says |
|---:|---:|---|---|
| 1247 | 64 | `VBox revenueDetail(Sector sector)` | Revenue, by good, each split into what the city took and what was shipped. |
| 1323 | 73 | `VBox inputsDetail(Sector sector)` | The cost of sales, by good, each split into what the city grew or made and what was landed. |
| 1398 | 49 | `VBox wagesDetail(Sector sector)` | Wages by pay tier - which kind of worker this business is actually paying. |
| 1459 | 45 | `VBox salesTaxDetail(Sector sector)` | The sales tax, as the ledger actually strikes it: charged on what was sold here, credited for what suppliers already remitted, and the difference remitted. |

### INCOME (0.7.30): A WATERFALL OVER THE STATEMENT (lines 1505-1658)

| line | len | member | says |
|---:|---:|---|---|
| 1528 | 18 | `void incomePage(VBox page, Sector sector, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then, boolean animate)` |  |
| 1548 | 26 | `List<Step> incomeSteps(SectorBooks.SectorMonth now, String inputs, java.util.Set<String> open)` | The waterfall's steps: revenue, each cost down to what it kept, the three totals the model strikes; a bar with a statement line that opens opens it. |
| 1576 | 76 | `void incomeStatement(VBox page, Sector sector, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then, boolean known, java.u...` | The income statement, whole, opening as it always has, and its ratios beside it. |
| 1654 | 4 | `void openLine(java.util.Set<String> open, String label)` | A waterfall's bar opening its statement line: the line put in the open set, and the page drawn again. |

### THE BALANCE SHEET (0.7.30): TWO BARS AND ITS OWNERS (lines 1659-2023)

| line | len | member | says |
|---:|---:|---|---|
| 1671 | 106 | `void balancePage(VBox page, Sector sector, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then)` |  |
| 1779 | 16 | `static VBox sheetRow(String name, List<Segment> parts, double scale, String total, List<HBox> keyParts)` | One bar of the sheet: its name, the bar on the sheet's scale, its total, and its key under it. |
| 1815 | 87 | `VBox ownersCard(int company, double bookEquity, double netIncome, boolean wide)` | Who owns a company, what a share is worth, and what it pays - as a card (0.7.30, D11), on a sector's Balance sheet (`wide`: the figures beside the share price's chart) and on the Bank's Owners page (narrow, in its sta... |
| 1904 | 29 | `String ownersPolicy(int company)` | The owners card's (i): its regime and payout policy, and what it has raised and paid since founding. |
| 1935 | 14 | `static VBox miniFigure(String name, String value, String under, String tone, String info)` | One small figure in a strip: its name over it, a line under it, and an (i) when `info` is not null. |
| 1951 | 25 | `VBox orderBook(OrderBook shareBook)` | The order book's two sides, five levels each, as the old block's tables - in plain figures (B10). |
| 1982 | 6 | `void ownersBlock(VBox column, int company, double bookEquity, double netIncome)` | Who owns a company, for the Bank's Owners page: the owners card, in its statement column (0.7.30, D11; the block of statement lines this drew until then is the card's now). |
| 1995 | 28 | `Node sharePriceChart(int company)` | One company's share price over the city's life: the last trade (the desk's quote until 0.7.12 round 2) against what the register says a share is worth, both per founding share - the trend chart, with its note behind a... |

### CASH AND CREDIT (0.7.30): A BRIDGE, AND WHAT ITS BORROWING COSTS (lines 2024-2469)

| line | len | member | says |
|---:|---:|---|---|
| 2036 | 23 | `void cashAndDebtPage(VBox page, Sector sector, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then)` |  |
| 2061 | 24 | `static List<Step> cashSteps(SectorBooks.SectorMonth now)` | The bridge's steps: every flow of the month that moved a dollar, in the reconciliation's order - the two it missed (B2) last but the residual. |
| 2087 | 135 | `void cashPage(VBox page, Sector sector, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then, boolean known, BusinessDebtM...` | The rest of Cash & debt: the bridge's card, the can't-borrow line, the reconciliation and its pictures, and the credit grid. |
| 2224 | 8 | `static VBox endFigure(String name, String value, String tone)` | A figure at a bridge's end: its name over it. |
| 2234 | 25 | `String spreadInfo(String key)` | The spread paragraph, behind the rate bar's (i) (the old page's note under "It pays"). |
| 2266 | 82 | `VBox creditPictures(Sector sector, SectorBooks.SectorMonth now)` | Beside the reconciliation: its rate in parts - prime, its own risk, its record, the book's concentration (BusinessDebtManager's pricing); its leverage on a scale to the default point, with the bank's watch line and ow... |
| 2350 | 119 | `List<Sector.Line> creditLines(Sector sector, SectorBooks.SectorMonth now)` | Every credit line the page had, as lines for the fact grid - its paragraphs as the notes under them (the grid's (i)s). |

### THE INVESTORS (0.7.30): WHY ISN'T IT BUILDING, AND WHAT WOULD LET IT? (lines 2470-2807)

| line | len | member | says |
|---:|---:|---|---|
| 2496 | 67 | `void investorPage(VBox page, Sector sector, SectorBooks.SectorMonth now, BuildCard.SectorInvestors si, boolean animate)` |  |
| 2565 | 73 | `List<String[]> rules(Sector sector, SectorBooks.SectorMonth now)` | The rules it builds by, as {chip, its old note or null}: the landlords' mortgage tests or everybody else's interest test, the horizon and trend, the population, the headroom, the order rule, and the builders' exception. |
| 2640 | 19 | `static String losingInfo(int lossMonths, boolean nothingToSell)` | The LOSING tile's (i): where it stands against the rule, in the rule's own number (B9's "six" was a literal). |
| 2665 | 13 | `VBox decisionCard(BuildCard.SectorInvestors si, boolean animate)` | Its decision as a card, 56 high: the kind's icon in a square, the kind in a word, and the word whole; Retail's bank branches a second line; a "Built" month flashing once with what it built. |
| 2680 | 79 | `VBox buildingsList(BuildCard.SectorInvestors si)` | Each building it can put up: its icon and name, how many stand and are on site, its value added, the investors' estimate, and its first gate. |
| 2761 | 9 | `static String gateIcon(BuildCard.GateKind k)` | A gate's icon: the deposit's pick, the licence's cap, the staffing test's person, the land, the money. |
| 2772 | 9 | `static VBox tile(String name, Node figure, String line, String edge, String info)` | One of WHAT STOPS IT's tiles: its name, its figure, a line, a colour for its edge when it is the stop, and an (i). |
| 2783 | 8 | `static HBox rule(String text, String info)` | A rule as a chip, with its old note behind an (i). |
| 2793 | 5 | `static HBox controlDoor(String svg, String words, Runnable go)` | A door to a control the player holds: its icon, and where it goes. |
| 2800 | 7 | `void openPolicy(String area, String page)` | Policy, on one of its pages, at its top. |

### OPERATIONS (0.7.30): INPUTS → THE PLANT → OUTPUTS (lines 2808-3221)

| line | len | member | says |
|---:|---:|---|---|
| 2837 | 12 | `void operationsPage(VBox page, Sector sector, boolean animate)` |  |
| 2851 | 15 | `VBox everyLine(Sector sector)` | The old page, whole: Sector.operations() drawn as statement lines. |
| 2868 | 16 | `VBox flowCard(Sector sector, SectorFlow.Flow flow, boolean animate)` | The flow: what goes in, a chevron, the plant, a chevron, what comes out. |
| 2886 | 10 | `static Label chevron()` | A chevron between the flow's columns. |
| 2901 | 42 | `VBox inputsColumn(Sector sector, SectorFlow.Flow flow)` | WHAT GOES IN: each good bought - the city's part and the world's - and each service, by name. |
| 2945 | 9 | `static HBox rowHead(Node name, String info, String money)` | A row's head: its name (and its (i)), and its money at the right. |
| 2956 | 29 | `VBox inputRow(SectorFlow.In i, boolean counted)` | One good bought: its name, the city's part and the world's on a bar, the units and the share, the money; or a service, by name. |
| 2987 | 16 | `static String inputWords(SectorFlow.In i)` | A good bought, in words: "82,647 t · 7% from the city · on hand 3,478 vans". |
| 3005 | 4 | `static String units(double n, Good g)` | A count of a good in its unit, "under 1 van" for a fraction that would read "0 vans". |
| 3017 | 43 | `VBox plantCard(Sector sector, SectorFlow.Flow flow, boolean animate)` | THE PLANT: how many stand and are on site; a ring of the rate it runs at, sweeping up on a new month; the six throttles as a cascade, each its ratio on a bar and the running product after it, the thinnest amber; and i... |
| 3062 | 5 | `static HBox centred(Node n)` | A node centred in a row of its own. |
| 3069 | 22 | `static GridPane cascade(SectorFlow.Plant p)` | The throttle cascade: each of the six, its ratio on a bar, and the rate after it - the thinnest drawn amber. |
| 3093 | 54 | `VBox outputsColumn(SectorFlow.Flow flow)` | WHAT COMES OUT: each good made against what it could make - sold here, shipped, into stock, idled - its price and cost; and work billed. |
| 3149 | 29 | `static String outputWords(SectorFlow.Out o, BuildCard.Note note)` | A good made, in words: "made 78,615 t of 203,400 · 32,018 here · 46,598 shipped"; a seller-priced one against the Build note's capacity. |
| 3180 | 9 | `static String coverWords(BuildCard.Note note)` | What a seller-priced good's capacity is, in the Build note's words. |
| 3191 | 6 | `static String railWords(BuildCard.Note note, boolean counted)` | The railway's work against its track, off Build's note (RAIL {the city's trade in tonnes, the network's capacity}); null for anyone else. |
| 3203 | 5 | `static HBox noteInLayers(String shown, String whole)` | A sector's note in two layers (0.7.21; Sector.Line.note(shown, whole)): the short line where statementNote() would put the paragraph, and the paragraph behind its (i). |
| 3210 | 11 | `static String toneColour(Sector.Line.Tone tone)` | The palette colour a sector's line asked for, or none. |

