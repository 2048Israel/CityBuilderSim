# SectorScreen.java - 5,048 lines · 194 methods · 55 constants · interface

`ham/citybuildersim/ui/SectorScreen.java` - generated 2026-10-10 by CodeMap; line numbers are as of that run.

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

**Uses:** [Palette](Palette.md) (666), [SectorStatements](SectorStatements.md) (135), [Sector](Sector.md) (119), [OilView](OilView.md) (101), [SectorBooks](SectorBooks.md) (70), [Icons](Icons.md) (66), [BuildCard](BuildCard.md) (55), [StatementView](StatementView.md) (52), [BuildScreen](BuildScreen.md) (51), [SectorFlow](SectorFlow.md) (41), [RefineryView](RefineryView.md) (40), [BusinessDebtManager](BusinessDebtManager.md) (19), [BusinessInvestment](BusinessInvestment.md) (19), [Equity](Equity.md) (13), [Retail](Retail.md) (11), [CityCalendar](CityCalendar.md) (11), [Formats](Formats.md) (11), [Good](Good.md) (9), [BuildAdvice](BuildAdvice.md) (8), [OrderBook](OrderBook.md) (7), [Debt](Debt.md) (7), [Restaurants](Restaurants.md) (7), [LuxuryRetail](LuxuryRetail.md) (7), [Bank](Bank.md) (6), [Mortgage](Mortgage.md) (6), [HistorySave](HistorySave.md) (4), [Game](Game.md) (4), [BuildingsTemplate](BuildingsTemplate.md) (4), [Sectors](Sectors.md) (4), [Levers](Levers.md) (4)... and 17 more

**Used by (6):** [BankScreen](BankScreen.md), [FundScreen](FundScreen.md), [HistoryScreen](HistoryScreen.md), [StatementView](StatementView.md), [TradeScreen](TradeScreen.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 51 | THE SECTOR ECONOMY. |
| 189 | · THE LIST (0.7.30) |
| 644 | · THE INVESTORS' WORD, IN A KIND (0.7.30) |
| 835 | ONE BUSINESS, FIVE PAGES |
| 1014 | · the screen's own pieces |
| 1189 | A STATEMENT LINE THAT OPENS |
| 1270 | · THE INCOME STATEMENT |
| 1277 | · what the two big lines open into |
| 1548 | INCOME (0.7.30; 0.7.74): A WATERFALL, IN SHORT, AND THE STATEMENT |
| 1777 | THE BALANCE SHEET (0.7.30; 0.7.74): TWO BARS, IN SHORT, ITS EQUITY'S |
| 2181 | CASH AND CREDIT (0.7.30; 0.7.74): A BRIDGE IN THREE STEPS, AND WHAT |
| 2603 | STATEMENT VIEWS (0.7.74): A SUMMARY AND A STATEMENT |
| 2702 | · Income |
| 2751 | · Balance sheet |
| 2843 | · Cash & debt |
| 3043 | · Investors |
| 3360 | THE INVESTORS (0.7.30): WHY ISN'T IT BUILDING, AND WHAT WOULD LET IT? |
| 3392 | · · what it decided |
| 3404 | · · its buildings |
| 3413 | · · and what stops it |
| 3442 | · · the rules |
| 3448 | · · what you control |
| 3708 | OPERATIONS (0.7.30): INPUTS → THE PLANT → OUTPUTS |
| 3758 | · OPERATIONS · THE REFINERY (0.7.95) |
| 4055 | · OPERATIONS · THE OIL INDUSTRY (0.7.96) |
| 4472 | · THE SHELF (0.7.45) |
| 4624 | · WHAT IT CHARGES (0.7.45) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 89 | `SectorScreen.SECTOR_HOME` | `"Operations"` | The page a business opens on, and falls back to for a page it does not know. |
| 96 | `SectorScreen.SECTOR_PAGES` | `{ "Operations", "Income", "Balance sheet", "Cash & debt", "Investors" }` | The five pages, in the strip's order: the names other screens open a business's books by. |
| 100 | `SectorScreen.PAGE_ICONS` | `{ Icons.INDUSTRY, Icons.COIN, Icons.FINANCES, Icons.BANK, Icons.SECTOR }` | Each page's icon on its chip (0.7.30): the plant, the coin, the ledger, the bank, the investors. |
| 112 | `SectorScreen.FRAME_CHROME` | `276` | How much of the stage a business's fixed frame takes above its page's scroller - the head, the five figures, the investors' line, the pages, and their gaps - until the frame is laid out and its own height is read (0.7... |
| 115 | `SectorScreen.LIST_CHROME` | `190` | ...and the list's: its head and its four figures (0.7.30). |
| 118 | `SectorScreen.STAGE_REST` | `36` | What the menu spends around the frame and the page: its padding over and under them and the gap between, and the four pixels of slack its height is held to (UserInterface's rootMenu) - so the page is what is left unde... |
| 121 | `SectorScreen.COUNT_MILLIS` | `400` | How long a figure takes to count from last month's to this month's (0.7.30). |
| 124 | `SectorScreen.SWEEP_MILLIS` | `700` | ...and the plant's ring to sweep up from nothing (0.7.30). |
| 194 | `SectorScreen.LIST_INFO` | `"What the city's businesses kept this month, after tax - each card one busine...` | What the list's (i) holds: the old caption, and what a card does. |
| 201 | `SectorScreen.NOTHING_YET_LIST` | `"The sector books are written when a month closes.This city has not " + "clos...` | The empty books' sentence (the old list's alert). |
| 205 | `SectorScreen.NOTHING_YET_SECTOR` | `"This city has not closed a month since it was loaded, so there is no " + "st...` | ...and a business's (the old page's alert). |
| 209 | `SectorScreen.NO_WORD_YET_LIST` | `"Nothing recorded since the city was loaded or founded: a month on, each " + ...` | The list's one line while no business has a word for the month (0.7.34), in place of each card's own. |
| 381 | `SectorScreen.RUNNING_INFO` | `"How much of what its plants could make each business made this month: its " ...` | RUNNING AT's (i). |
| 505 | `SectorScreen.SPARK_WIDTH` | `120` | The width the sparkline takes on a card (0.7.30; 90 on the old list). |
| 508 | `SectorScreen.SPARK_HEIGHT` | `28` | ...and its height (0.7.30; 22 on the old list). |
| 511 | `SectorScreen.SPARK_MONTHS` | `24` | How many months of net income the sparkline draws (0.7.4): two years. |
| 1565 | `SectorScreen.OPERATING_INFO` | `"What the business made from trading, after its sales tax and the ground it "...` | The operating line's (i): what it is, and the model's own operating income, which is before the ground and the sales tax (D4, D5). |
| 1569 | `SectorScreen.REFUND_INFO` | `"The sales tax line is a REFUND this month: the credit on what this sector " ...` | The sales tax refund's (i). |
| 1574 | `SectorScreen.FALL_INFO` | `"Revenue at the left, less the sales tax it remitted and what it bought, to i...` | The waterfall's (i). |
| 1860 | `SectorScreen.OWNERS_DOOR` | `"its owners, its share's price and what it pays: Investors"` | The Balance sheet's door to its owners' card (D7). |
| 2625 | `SectorScreen.INCOME` | `"income", SHEET = "sheet", EQUITY = "equity", CASH = "cash"` | Each statement's key in a business's open set: an open note on one is "income:note:5" in linesOpen(). |
| 2628 | `SectorScreen.OUTSIDE_INFO` | `"Money that reached its books outside its trading: the city's subsidy and the...` | Note 6, and the equity statement's outside line: what the outside lines are (F1). |
| 2636 | `SectorScreen.DERIVED_INFO` | `"Derived when the city was loaded: its save was made before share capital was...` | Share capital's line and note, when a save from before 0.7.75 was loaded (R3). |
| 2642 | `SectorScreen.CAPITAL_INFO` | `"What its owners put in: the book its founders' shares were issued against, a...` | Share capital's line and note (R3). |
| 2647 | `SectorScreen.STOCK_WORDS` | `"Stock is held at what it would fetch today, so a price collapse shrinks this...` | Note 7: the stock. |
| 2651 | `SectorScreen.ABROAD_WORDS` | `"What it has sent abroad for the world's rate, in the city's money at the rat...` | Note 9: what it holds abroad. |
| 2656 | `SectorScreen.DUE_WORDS` | `"What its loans, bonds and mortgages ask at the twelve settles ahead: a loan ...` | What falls due within a year (R2), behind its line's and its card's (i). |
| 2661 | `SectorScreen.NOT_SPLIT_WORDS` | `"When it falls due is counted at the next month's books: a load keeps the deb...` | The month after a load, R2's line. |
| 2792 | `SectorScreen.EQUITY_HEADS` | `{ "share capital", "kept, revalued", "total" }` | The equity statement's columns (R3). |
| 2795 | `SectorScreen.REVALUED_INFO` | `"Whatever else moved its equity, worked out as what is left: a building bough...` | The equity statement's remainder's (i). |
| 2860 | `SectorScreen.SCHEDULE_RATE` | `70, SCHEDULE_RUNS = 80` | The debt schedule's columns (R7): the kind (DEBT_KIND), five figures (DEBT_FIGURE), the rate and when the last of it falls due. |
| 2863 | `SectorScreen.SCHEDULE_INFO` | `"What it owed of each kind at last month's sheet, what it borrowed, repaid an...` | The schedule's (i). |
| 2973 | `SectorScreen.DEBT_KIND` | `130, DEBT_FIGURE = 100` | The debt card's columns: the kind, then owed, within a year, in one to five, after five, and the month's interest. |
| 3062 | `SectorScreen.EVERY_GATE_INFO` | `"Each building it can put up at every gate investors ask - ore, a licence, " ...` | EVERY BUILDING AT EVERY GATE's (i). |
| 3070 | `SectorScreen.GATE_NAME` | `200, GATE_MARK = 44, GATE_FIGURE = 96, GATE_PAYBACK = 84, GATE_PAYS = 210` | The grid's columns: the building, a gate's mark, a figure, the payback, how it would pay. |
| 3073 | `SectorScreen.GATE_HEADS` | `{ "ore", "licence", "staff", "land", "pays" }` | The gates' heads, in BuildCard.GateKind's order. |
| 3220 | `SectorScreen.SHARE_ROWS` | `{ { "Shares", "n" }, { "Earnings a share, the month", "$" }, { "Dividend a sh...` | The report's rows: {label, kind} - "n" a count, "$" money a share, "%" a share, "x" times, "M" money. |
| 3264 | `SectorScreen.SHARE_LABEL` | `220, SHARE_FIGURE = 120` | The report's columns. |
| 3378 | `SectorScreen.CONTROL_INFO` | `"None of this is yours to set.These are private companies deciding for " + "t...` | WHAT YOU CONTROL's (i): the old page's last paragraph. |
| 3383 | `SectorScreen.WAITING_INFO` | `"This business wants to build and there is nowhere to put it.It is the one " ...` | The waiting-on-ground alert's (i). |
| 3732 | `SectorScreen.FLOW_INFO` | `"Each row's money is the month the books closed on - the Income page's - and ...` | The flow's money and units: which month each is (SectorFlow's two months). |
| 3776 | `SectorScreen.REFINERY_HEAD` | `"THE REFINERY THIS MONTH"` | The card's heading, its line, and what its (i) says. |
| 3777 | `SectorScreen.REFINERY_SUB` | `"where the crude went · ribbons to scale"` |  |
| 3778 | `SectorScreen.REFINERY_INFO` | `"The month's crude comes in on the left, from the wells, the reserve or the w...` |  |
| 3976 | `SectorScreen.HATCHES` | `new java.util.HashMap<>()` |  |
| 4073 | `SectorScreen.OIL_WELLS_INFO` | `"The city's wells by kind.A land well lifts the ground pool - the oil under t...` | What the oil industry's (i)s say. |
| 4078 | `SectorScreen.OIL_UNITS_INFO` | `"Every kind of refinery unit.Its spread is what it makes of a litre of its fe...` |  |
| 4082 | `SectorScreen.OIL_PRODUCTS_INFO` | `"Every product of the refinery, and crude: its price here and the world's, th...` |  |
| 4085 | `SectorScreen.OIL_RESERVE_INFO` | `"The city's own crude, in its Strategic Reserves' tanks.Fill orders crude for...` |  |
| 4089 | `SectorScreen.RESERVE_CAVEAT` | `"bought at the next clearing at what crude then costs; what the room cannot t...` |  |
| 4090 | `SectorScreen.RESERVE_CAVEAT_INFO` | `"A fill is an order for the next clearing: the wells' crude pro rata with the...` |  |
| 4093 | `SectorScreen.RELEASE_CAVEAT` | `"offered to the refiners first; what they do not take ships at the export price"` |  |
| 4096 | `SectorScreen.RESERVE_LADDER` | `380` | The lever's ladder width on its dial card. |
| 4569 | `SectorScreen.SHELF_INFO` | `"A basket is one person's groceries for a month.The shelf moves a sixth of th...` | THE SHELF's (i). |
| 4725 | `SectorScreen.FOLD_PAST` | `6` | The goods a column folds into one row past this many (the shops' and the kitchens' thirteen foods). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 47 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 87 | `Sector openSector` | Which sector's books are open, or null for the list. |
| 90 | `String sectorPage` |  |
| 93 | `boolean sectorsExpanded` | Whether every card on the list is open to its second row (0.7.4) - kept while the game runs, like the page, and never saved. |
| 103 | `private final java.util.Set<String> detailsOpen` | Which "details" folds are open - kept while the game runs, not saved (0.7.30). |
| 106 | `private final java.util.Map<String, java.util.Set<String>> linesOpen` | Which statement lines are open, by sector and then by label (0.7.30: a waterfall's bar opens its line). |
| 109 | `private final java.util.Map<String, Integer> drawnAt` | The month each view was last drawn at: its figures count up and its ring sweeps once a month, not on every redraw. |
| 187 | `private javafx.scene.control.ScrollPane body` | The page's scroller. |
| 2619 | `boolean statementView` | Whether the pages draw the formal statement rather than the summary (D1): kept while the game runs, never saved; the Bank tab's two statements read it too. |
| 2622 | `boolean showChange` | The Statement view's change and common-size columns, each on or off from its toolbar, on every statement at once. |
| 3809 | `private final RefineryView.View view` |  |
| 3810 | `private final Sectors sectors` |  |
| 3811 | `private double drawnAt` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 44 | 5005 | **type** `final class SectorScreen` | The sector economy: every business in the city as a card, and each one's five pages - what goes in and what comes out, the income statement with last month beside it, the balance sheet and its owners, cash and credit,... |
| 49 | 1 | `SectorScreen(UserInterface ui)` |  |

### THE SECTOR ECONOMY. (lines 51-188)

| line | len | member | says |
|---:|---:|---|---|
| 127 | 6 | `void openSectorBooks(Sector sector, String page)` | Open one business's books from somewhere else in the game. |
| 135 | 5 | `void open(String page)` | One of the open business's pages, at its top. |
| 142 | 5 | `void backToList()` | Back to the list, at its top. |
| 149 | 8 | `void showSectorMenu()` | The tab lands here: what every business in the city earned. |
| 159 | 4 | `private boolean freshMonth(String view)` | Whether this draw of a view is the first since its month changed - the count-ups and the sweep run then and on no other redraw. |
| 165 | 20 | `private void frameOver(VBox frame, VBox page, double chrome)` | The fixed frame over a scrolling page, at the page's width: the shape every redrawn tab has (Infrastructure's, 0.7.29). |

### THE LIST (0.7.30) (lines 189-643)

| line | len | member | says |
|---:|---:|---|---|
| 212 | 34 | `void listScreen()` |  |
| 253 | 6 | `static boolean noWordYet(Game game, List<Sector> order)` | Whether no business has a word for the month (0.7.34): every sector's Game.getLastInvestment() empty, as it is from a load or a founding until a month runs. |
| 261 | 7 | `List<Sector> marketOrder()` | The sectors in Build's market order: by the group their own buildings are under (BuildCard.groupRank()), then as Sectors lists them. |
| 276 | 23 | `HBox listVitals(SectorBooks books, List<Sector> order, boolean animate)` | The list's four figures: what they all kept, with its move on last month; how many lost, the two that lost most named; who works there, against the posts; and the range they run at, over every business with a plant an... |
| 307 | 3 | **type** `record ListFigures(double kept, double before, boolean compared, String moved, String moveTone, String kept...` | The list's four figures as words, off the books and the sectors' plants - a pure read, for the strip and the probes: what they kept and their move, the sectors losing and the words naming them, the workers and posts, ... |
| 311 | 68 | `static ListFigures listFigures(SectorBooks books, List<Sector> order)` |  |
| 397 | 71 | `VBox sectorCard(Sector sector, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then, List<? extends Number> netIncomeSerie...` | One business on the list (0.7.30): its icon, its name with the Build group under it and its blurb on hover, what it kept and its move; its last two years, its workers, and how much of its plant runs with what cuts it ... |
| 470 | 8 | `static String runningWords(Sector sector, SectorFlow.Plant plant)` | ...the grocers' in theirs (0.7.45): "handed over 58% of what was asked · roads 70%". |
| 480 | 7 | `static String runningWords(SectorFlow.Plant plant)` | A card's plant in words: "running at 39% · roads 64%" - the throttle that cuts it most, when one is under 100% - or the homes let, or "no plant standing". |
| 489 | 6 | `String groupWords(Sector sector)` | "Industry · food mills": the Build category and the group a sector's own buildings are under, once when they are one. |
| 497 | 6 | `static Label caption(String text, String tone)` | A line of caption that wraps, in a colour. |
| 529 | 69 | `javafx.scene.Node sparkline(List<? extends Number> series)` | A sector's net income as a line (0.7.4): the last SPARK_MONTHS of the history's netIncome:<sector>, the zero line faint under it. |
| 604 | 18 | `GridPane sectorCardMore(Sector sector, SectorBooks.SectorMonth now)` | The card's five figures more, with the list opened (0.7.4; three and two since 0.7.30): revenue, margin and cash; what it owes and its posts filled - each off SectorMonth or the sector, nothing recomputed. |
| 624 | 11 | `static VBox moreCell(String word, String figure, String tone)` | One labelled figure on a card's second row: the word over the figure, 120 wide. |
| 637 | 6 | `static String sectorBlurb(Sector sector)` | What the business actually does - the sector's own first sentence, whole (cut at 72 until 0.7.20). |

### THE INVESTORS' WORD, IN A KIND (0.7.30) (lines 644-834)

| line | len | member | says |
|---:|---:|---|---|
| 656 | 15 | `static String kindIcon(BuildCard.WordKind k)` |  |
| 673 | 8 | `static String kindColour(BuildCard.WordKind k)` | The kind's colour: its edge and its icon. |
| 683 | 16 | `static String kindWord(BuildCard.WordKind k)` | ...and its word, before the sector's. |
| 701 | 6 | `String sectorWords(BuildCard.SectorInvestors si)` | The words after the kind: what is on site of theirs, the build card's "nothing recorded" before a month has run, or the word with "Holding: " gone. |
| 709 | 5 | `static String shortWord(String words)` | A word's short form for a card: a "Built" or "Sold" word up to its " - " (the frame's line and the tooltip have the rest). |
| 716 | 10 | `static String builtTag(String word)` | "+2 Industrial Bakery": what a "Built" word built, for the month's flash; null for any other word. |
| 731 | 29 | `Node investorsMini(BuildCard.SectorInvestors si, boolean blank)` | The word on a card: the kind's icon at 12, its word, the sector's - wrapping, never cut; a click opens the Investors page. |
| 762 | 4 | `static String textColour(BuildCard.WordKind k)` | A kind's word in a colour that reads as text: the grey kinds' edge is too faint for words. |
| 775 | 34 | `VBox investorsLine(BuildCard.SectorInvestors si, boolean animate, double iconBox)` | The investors' line (0.7.30): under every page's strip, 36 high on the raised ground with a 3 px edge in the kind's colour - its icon in a tinted square, the kind in a word, the sector's word whole, and "Investors ›" ... |
| 811 | 10 | `static HBox kindRow(BuildCard.WordKind k, String words, double box)` | One kind's row: its icon square, its word and the words after it, wrapping. |
| 823 | 11 | `static void flash(Region r)` | A region's one flash in the building pink: a "Built" month landing (0.7.30). |

### ONE BUSINESS, FIVE PAGES (lines 835-1013)

| line | len | member | says |
|---:|---:|---|---|
| 847 | 48 | `void drawSectorScreen()` |  |
| 897 | 35 | `HBox sectorHead(Sector sector)` | The head: "Sectors ›" - the way back - the business's icon and name, its blurb behind an (i), and a door to its buildings on Build. |
| 940 | 34 | `HBox sectorVitals(Sector sector, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then, SectorFlow.Plant plant, boolean ani...` | The five figures: KEPT with its move, MARGIN, CASH, OWES - "owes nothing" when it owes nothing, the rate and leverage when it owes something (B8) - and RUNNING AT with the throttle that cuts it most, or the homes let ... |
| 982 | 8 | `static String[] handedCell(Retail shops, SectorFlow.Plant plant)` | The grocers' fifth figure (0.7.45; the UI spec's D8), {label, figure, note}: the share of the baskets asked for at the price that the shops handed over (Retail.getHouseholdShare()) - for groceries the outcome, as LET ... |
| 992 | 4 | `static String handedTone(Retail shops)` | ...its verdict: what went unhanded, on GOING SHORT's own lines (the UI spec's D10) - one hunger rule, not a second. |
| 998 | 5 | `static String owesWords(boolean blocked, int blockedMonths, SectorBooks.SectorMonth now)` | OWES's note: the ban, "owes nothing" when it owes nothing (B8: it quoted a rate on no debt), or its rate and leverage. |
| 1005 | 8 | `static String[] runningCell(SectorFlow.Plant plant)` | The frame's fifth figure, {label, figure, note}: what it runs at and what cuts it most; the landlords' homes let; or no plant. |

### the screen's own pieces (lines 1014-1188)

| line | len | member | says |
|---:|---:|---|---|
| 1017 | 1 | `static String m(double thousands)` | Money in the model's thousands, as the statements write it: "$13.6M". |
| 1020 | 1 | `static String marginWords(double share)` | A margin to a tenth of a per cent, with no sign on one that rounds to nothing (B11). |
| 1023 | 10 | `static VBox kpi(String label, String value, String note, String tone, String info, String where, Runnable go)` | A limit cell with an (i) after its label when `info` is not null. |
| 1035 | 1 | `static Label figureOf(VBox cell)` | A limit cell's figure, for its count-up. |
| 1038 | 5 | `static void noteTone(VBox cell, String tone)` | A limit cell's note in a colour of its own: a move on last month, up or down. |
| 1045 | 11 | `static void countUp(Label label, double from, double to, java.util.function.DoubleFunction<String> words)` | A figure counted from last month's to this month's over COUNT_MILLIS, ending on this month's exactly (0.7.30). |
| 1058 | 12 | `static Region sweep(Region ring)` | A ring (Pieces.ring()) whose arc sweeps up from nothing over SWEEP_MILLIS - there is no last month's rate to sweep from (0.7.30). |
| 1072 | 16 | `static HBox alertLine(String svg, String colour, String text, String info, Node right)` | One line that something is wrong or waiting: its icon in a colour, a few words, and the whole of it behind an (i). |
| 1090 | 8 | `static VBox card(Node...children)` | A card on the raised ground: a picture's frame. |
| 1100 | 14 | `static HBox head(String text, String info, Node right)` | A picture's caption in capitals, with an (i) after it when `info` is not null, and something at its right. |
| 1116 | 6 | `static Label figure(String text, double size, String tone)` | A figure, never cut, at a size, in a colour. |
| 1124 | 9 | `static Node withInfo(Node line, String info)` | An (i) after a statement line's label: the paragraph that sat under it (0.7.30). |
| 1135 | 3 | `java.util.Set<String> linesOpen(Sector sector)` | The statement lines open on one business's pages. |
| 1140 | 13 | `static HBox ratioRow(String name, String value, String colour, String info)` | A ratio as a chip in a row: its name, the chip, and its (i). |
| 1155 | 9 | `static VBox ratioColumn(String title, Node...rows)` | A ratio column, 300 wide, beside a statement. |
| 1166 | 15 | `static HBox keyPart(String colour, String name, String figure)` | One part of a bar's key: its swatch, its name and its figure. |
| 1183 | 5 | `static javafx.scene.layout.FlowPane key(List<HBox> parts)` | A bar's key, wrapping. |

### A STATEMENT LINE THAT OPENS (lines 1189-1269)

| line | len | member | says |
|---:|---:|---|---|
| 1222 | 30 | `HBox bookDetailRow(String label, String value, boolean indented, String tone)` | One line inside an opened statement line. |
| 1253 | 3 | `HBox bookDetailRow(String label, double amount, boolean indented)` |  |
| 1258 | 8 | `Label bookDetailNote(String text)` | A sentence at the bottom of an opened line, when the split has something to say. |

### THE INCOME STATEMENT (lines 1270-1276)

| line | len | member | says |
|---:|---:|---|---|
| 1273 | 3 | `static String inputLabel(Sector sector)` | What this sector's direct cost is actually called - the sector says. |

### what the two big lines open into (lines 1277-1547)

| line | len | member | says |
|---:|---:|---|---|
| 1290 | 64 | `VBox revenueDetail(Sector sector)` | Revenue, by good, each split into what the city took and what was shipped. |
| 1366 | 73 | `VBox inputsDetail(Sector sector)` | The cost of sales, by good, each split into what the city grew or made and what was landed. |
| 1441 | 49 | `VBox wagesDetail(Sector sector)` | Wages by pay tier - which kind of worker this business is actually paying. |
| 1502 | 45 | `VBox salesTaxDetail(Sector sector)` | The sales tax, as the ledger actually strikes it: charged on what was sold here, credited for what suppliers already remitted, and the difference remitted. |

### INCOME (0.7.30; 0.7.74): A WATERFALL, IN SHORT, AND THE STATEMENT (lines 1548-1776)

| line | len | member | says |
|---:|---:|---|---|
| 1579 | 14 | `void incomePage(VBox page, Sector sector, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then, boolean animate)` |  |
| 1595 | 28 | `List<Step> incomeSteps(Sector sector, SectorStatements.Table t, SectorBooks.SectorMonth now)` | The waterfall's steps: revenue, each cost down to what it kept, the statement's subtotals between them (D14); a bar with a note opens it in the Statement view. |
| 1625 | 3 | `static Step fallTotal(String name, double amount)` | A waterfall's total: the business's violet, red under nothing. |
| 1630 | 10 | `static VBox incomeShort(Sector sector, SectorStatements.Table t)` | IN SHORT on Income: revenue, the middle line, operating profit, profit before tax and the profit - the statement's own lines (D14). |
| 1642 | 5 | `static double[] nows(SectorStatements.Table t, String[] ids)` | A table's figures this month, by id. |
| 1649 | 5 | `static double[] thens(SectorStatements.Table t, String[] ids)` | ...and last month. |
| 1656 | 3 | `static String pctWords(double share)` | A share as a ratio chip writes it: a tenth of a per cent, a dash where it means nothing. |
| 1661 | 3 | `static String timesWords(double x)` | Times over, a dash where it means nothing. |
| 1673 | 63 | `VBox incomeRatios(Sector sector, SectorStatements.Table t, SectorBooks.SectorMonth now)` | The Income page's ratios, both views: gross, operating and net margin, interest cover on the statement's operating profit (D10, F6), the effective tax rate, the wages' take and earnings a share - and its format's own:... |
| 1742 | 28 | `VBox everyDollar(SectorBooks.SectorMonth now)` | OF EVERY DOLLAR IT TOOK (the Income summary): each cost as cents of a dollar of its revenue, and what it kept, on one bar - a refund is not a cost and is left out, so the cents can come to more than a dollar. |
| 1772 | 4 | `void openNote(Sector sector, String table, int note)` | A summary's door into the Statement view with one note open: the waterfall's bars. |

### THE BALANCE SHEET (0.7.30; 0.7.74): TWO BARS, IN SHORT, ITS EQUITY'S (lines 1777-2180)

| line | len | member | says |
|---:|---:|---|---|
| 1793 | 65 | `void balancePage(VBox page, Sector sector, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then)` |  |
| 1869 | 39 | `VBox balanceRatios(Sector sector, SectorStatements.Table t, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then)` | The Balance sheet's ratios, both views: current and quick (what falls due within a year needs R2, so they read a dash the month after a load), debt to equity and to assets, the owners' share, return on equity a year o... |
| 1910 | 23 | `Node equityBridge(SectorBooks.SectorMonth now, SectorBooks.SectorMonth then)` | ITS EQUITY THIS MONTH (the Balance summary): the statement of changes in equity as a bridge, or null without last month's sheet. |
| 1935 | 16 | `static VBox sheetRow(String name, List<Segment> parts, double scale, String total, List<HBox> keyParts)` | One bar of the sheet: its name, the bar on the sheet's scale, its total, and its key under it. |
| 1972 | 87 | `VBox ownersCard(int company, double bookEquity, double netIncome, boolean wide)` | Who owns a company, what a share is worth, and what it pays - as a card (0.7.30, D11), on a sector's Investors page since 0.7.75 (D7; its Balance sheet until then, which keeps a door to it - `wide`: the figures beside... |
| 2061 | 29 | `String ownersPolicy(int company)` | The owners card's (i): its regime and payout policy, and what it has raised and paid since founding. |
| 2092 | 14 | `static VBox miniFigure(String name, String value, String under, String tone, String info)` | One small figure in a strip: its name over it, a line under it, and an (i) when `info` is not null. |
| 2108 | 25 | `VBox orderBook(OrderBook shareBook)` | The order book's two sides, five levels each, as the old block's tables - in plain figures (B10). |
| 2139 | 6 | `void ownersBlock(VBox column, int company, double bookEquity, double netIncome)` | Who owns a company, for the Bank's Owners page: the owners card, in its statement column (0.7.30, D11; the block of statement lines this drew until then is the card's now). |
| 2152 | 28 | `Node sharePriceChart(int company)` | One company's share price over the city's life: the last trade (the desk's quote until 0.7.12 round 2) against what the register says a share is worth, both per founding share - the trend chart, with its note behind a... |

### CASH AND CREDIT (0.7.30; 0.7.74): A BRIDGE IN THREE STEPS, AND WHAT (lines 2181-2602)

| line | len | member | says |
|---:|---:|---|---|
| 2198 | 60 | `void cashAndDebtPage(VBox page, Sector sector, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then)` |  |
| 2260 | 18 | `static List<Step> cashSteps(SectorStatements.Table cash)` | The bridge's steps: the cash flow statement's three sections, each with its three biggest items as its tooltip, and NOT ACCOUNTED FOR when there is any. |
| 2280 | 17 | `static String biggest(SectorStatements.Table t, String head, String total)` | A section's three biggest lines this month, in words: "Profit for the month +$1.2M\nIts suppliers' credit, net −$310k". |
| 2303 | 19 | `VBox carryCard(Sector sector, SectorStatements.Table inc, SectorStatements.Table cash, SectorBooks.SectorMonth now)` | CAN IT CARRY WHAT IT OWES (the Cash & debt summary): interest cover on the statement's operating profit (D10), its debt over a year of what its trading brings in, and whether it can borrow at all. |
| 2324 | 27 | `List<Node> whenDue(Sector sector)` | WHEN IT FALLS DUE (R2), under what it owes by kind: within a year, in one to five, after five - the city debt's ladder colours; or not counted yet. |
| 2353 | 8 | `static VBox endFigure(String name, String value, String tone)` | A figure at a bridge's end: its name over it. |
| 2363 | 25 | `String spreadInfo(String key)` | The spread paragraph, behind the rate bar's (i) (the old page's note under "It pays"). |
| 2395 | 86 | `VBox creditPictures(Sector sector, SectorBooks.SectorMonth now)` | Beside the reconciliation: its rate in parts - prime, its own risk, its record, the book's concentration (BusinessDebtManager's pricing); its leverage on a scale to the default point, with the bank's watch line and ow... |
| 2483 | 119 | `List<Sector.Line> creditLines(Sector sector, SectorBooks.SectorMonth now)` | Every credit line the page had, as lines for the fact grid - its paragraphs as the notes under them (the grid's (i)s). |

### STATEMENT VIEWS (0.7.74): A SUMMARY AND A STATEMENT (lines 2603-2701)

| line | len | member | says |
|---:|---:|---|---|
| 2665 | 5 | `void pickView(boolean statement)` | The switch picked: this page in the other view, at its top. |
| 2672 | 3 | `SectorStatements.Table incomeTable(Sector sector, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then)` | One business's income statement this month, its finance note from R1. |
| 2677 | 4 | `SectorStatements.Table sheetTable(Sector sector, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then)` | ...and its sheet, its debt by when it falls due from R2. |
| 2683 | 3 | `StatementView.Columns columns(String shareWord)` | A statement's columns as the toolbar left them; `shareWord` null for one with no common size. |
| 2688 | 7 | `HBox toolbar(SectorStatements.Table t, String table, StatementView.Columns c, java.util.Set<String> open, java.util.function.In...` | A statement's toolbar: its two columns, and its notes opened and closed together (none without notes). |
| 2697 | 1 | `static String period(SectorBooks.SectorMonth now)` | "For the month of January 2200". |
| 2700 | 1 | `static String asAt(SectorBooks.SectorMonth now)` | "As January 2200 closed". |

### Income (lines 2702-2750)

| line | len | member | says |
|---:|---:|---|---|
| 2705 | 26 | `void incomeStatementView(VBox page, Sector sector, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then)` | The statement of profit or loss, its notes, and its ratios beside it. |
| 2733 | 17 | `static String incomeInfo(String id, SectorBooks.SectorMonth now)` | An income statement row's (i), by id. |

### Balance sheet (lines 2751-2842)

| line | len | member | says |
|---:|---:|---|---|
| 2754 | 36 | `void balanceStatementView(VBox page, Sector sector, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then)` | The classified sheet with its ratios, then the statement of changes in equity. |
| 2800 | 22 | `static String equityInfo(String id, SectorBooks.SectorMonth now)` | An equity statement row's (i), by id: R6's four parts, the founders' shares and the rest. |
| 2824 | 18 | `static String sheetInfo(String id, SectorBooks.SectorMonth now)` | A sheet row's (i), by id. |

### Cash & debt (lines 2843-3042)

| line | len | member | says |
|---:|---:|---|---|
| 2846 | 12 | `void cashStatementView(VBox page, Sector sector, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then)` | The statement of cash flows, then what it owes by kind and by when it falls due. |
| 2877 | 46 | `VBox scheduleCard(Sector sector, boolean millions)` | ITS DEBT THIS MONTH, BY KIND (the Cash & debt statement; R7): the roll-forward from last month's sheet to this month's - at the start, borrowed, repaid, written off, at the end - with each kind's rate and when the las... |
| 2925 | 9 | `private static HBox scheduleRow(String kind, double[] v, double rate, double runsTo, boolean millions, boolean total)` | One kind's row of the schedule: its five figures, its rate and when the last of it falls due. |
| 2935 | 15 | `private static HBox scheduleRow(String[] words, String labelStyle, String figureStyle)` |  |
| 2952 | 19 | `static String cashInfo(String id, SectorBooks.SectorMonth now)` | A cash flow row's (i), by id. |
| 2982 | 33 | `VBox debtCard(Sector sector, boolean millions)` | ITS DEBT, BY KIND AND BY WHEN IT FALLS DUE (the Cash & debt statement): each kind it owes on (R2) with when it falls due and the month's interest on it (R1), its suppliers on a line at no interest, and the city debt's... |
| 3016 | 7 | `private static HBox debtRow(String kind, double[] v, boolean millions, boolean total)` |  |
| 3024 | 18 | `private static HBox debtRow(String[] words, String labelStyle, String figureStyle)` |  |

### Investors (lines 3043-3359)

| line | len | member | says |
|---:|---:|---|---|
| 3051 | 9 | `void investorStatementView(VBox page, Sector sector, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then, BuildCard.Secto...` | The investor report (spec 4.4): a share this month against last, who holds it and what it has raised and paid, how it is financed against the equity share it aims for, and since 0.7.75 every building at every gate (R4... |
| 3076 | 63 | `VBox everyGate(Sector sector)` | Every building it owns at every gate (R4): one row each, and what it means under it. |
| 3141 | 8 | `Node gateMark(BuildCard.GateMark g)` | One gate's mark: a tick, a cross with why behind a tooltip, or a dash where it is not one this building has. |
| 3151 | 6 | `static String paybackWords(double months)` | The months its estimate would take to earn what one costs, in months under two years and years past them; "never" when it would not pay. |
| 3159 | 11 | `static String paysWords(BuildCard.Appraisal a)` | How one would be paid for, as shares of its cost: "40% shares · 12% its own · 48% borrowed", the parts that are not nothing. |
| 3172 | 9 | `static String gateShort(BuildCard.GateKind k)` | A failed gate in two or three words, for the "so" line's second refusal. |
| 3189 | 19 | `String soWords(BuildCard.Appraisal a)` | What a building's gates mean (R4's "so"): it passes every one; ground is all that stops it, which the player can buy; it would not pay, which no gate opening would change; or its first refusal, and the others behind i... |
| 3210 | 8 | `static double[] shareFigures(SectorBooks.Shares s, SectorBooks.SectorMonth m)` | A share's figures, this month or last, as the report's rows: {shares, EPS, DPS, payout, book, price, fair, P/E, P/B, yield, market value}; NaN where it means nothing. |
| 3226 | 24 | `VBox shareReport(Sector sector, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then)` | A SHARE: the report's first block, this month against last; not counted the month after a load. |
| 3252 | 10 | `static String shareWords(double v, String kind)` | One of the report's figures in words, by its kind. |
| 3266 | 16 | `private static HBox shareRow(String label, String now, String then, String labelStyle, String figureStyle)` |  |
| 3284 | 25 | `VBox whoHoldsIt(int company)` | WHO HOLDS IT: the households, the world and the bank's desk, and what it has raised from them and paid them since founding. |
| 3311 | 17 | `VBox howFinanced(int company, SectorBooks.SectorMonth now)` | HOW IT IS FINANCED: what it owes and its owners' equity, each a share of what it owns, against the equity share it aims for. |
| 3335 | 24 | `VBox forShareholders(Sector sector, SectorBooks.SectorMonth now)` | FOR ITS SHAREHOLDERS (the Investors summary): the last trade and fair value, earnings and the dividend a share, the yield, price to earnings and to book, and who holds it - the reads Equity and Exchange already keep, ... |

### THE INVESTORS (0.7.30): WHY ISN'T IT BUILDING, AND WHAT WOULD LET IT? (lines 3360-3707)

| line | len | member | says |
|---:|---:|---|---|
| 3386 | 72 | `void investorPage(VBox page, Sector sector, SectorBooks.SectorMonth now, BuildCard.SectorInvestors si, boolean animate)` |  |
| 3460 | 78 | `List<String[]> rules(Sector sector, SectorBooks.SectorMonth now)` | The rules it builds by, as {chip, its old note or null}: the landlords' mortgage tests or everybody else's interest test, the horizon and trend, the population, the headroom, the order rule, and the builders' exception. |
| 3540 | 19 | `static String losingInfo(int lossMonths, boolean nothingToSell)` | The LOSING tile's (i): where it stands against the rule, in the rule's own number (B9's "six" was a literal). |
| 3565 | 13 | `VBox decisionCard(BuildCard.SectorInvestors si, boolean animate)` | Its decision as a card, 56 high: the kind's icon in a square, the kind in a word, and the word whole; Retail's bank branches a second line; a "Built" month flashing once with what it built. |
| 3580 | 79 | `VBox buildingsList(BuildCard.SectorInvestors si)` | Each building it can put up: its icon and name, how many stand and are on site, its value added, the investors' estimate, and its first gate. |
| 3661 | 9 | `static String gateIcon(BuildCard.GateKind k)` | A gate's icon: the deposit's pick, the licence's cap, the staffing test's person, the land, the money. |
| 3672 | 9 | `static VBox tile(String name, Node figure, String line, String edge, String info)` | One of WHAT STOPS IT's tiles: its name, its figure, a line, a colour for its edge when it is the stop, and an (i). |
| 3683 | 8 | `static HBox rule(String text, String info)` | A rule as a chip, with its old note behind an (i). |
| 3693 | 5 | `static HBox controlDoor(String svg, String words, Runnable go)` | A door to a control the player holds: its icon, and where it goes. |
| 3700 | 7 | `void openPolicy(String area, String page)` | Policy, on one of its pages, at its top. |

### OPERATIONS (0.7.30): INPUTS → THE PLANT → OUTPUTS (lines 3708-3757)

| line | len | member | says |
|---:|---:|---|---|
| 3737 | 20 | `void operationsPage(VBox page, Sector sector, boolean animate)` |  |

### OPERATIONS · THE REFINERY (0.7.95) (lines 3758-4054)

| line | len | member | says |
|---:|---:|---|---|
| 3787 | 19 | `VBox refineryCard(RefineryView.View v)` | Refining's picture and strip, or a line saying why there is nothing to draw. |
| 3808 | 20 | **type** `static final class RefineryPicture extends javafx.scene.layout.Pane` | The picture: RefineryView.picture() at the pane's width, painted. |
| 3813 | 8 | `RefineryPicture(RefineryView.View view, Sectors sectors)` _(in SectorScreen.RefineryPicture)_ |  |
| 3822 | 5 | `private void draw(double width)` _(in SectorScreen.RefineryPicture)_ |  |
| 3830 | 65 | `static List<Node> paint(RefineryView.Picture p, Sectors sectors)` | A picture's shapes as nodes, in its painting order: the ribbons, the boxes over them, the outlines, lines, icons and words. |
| 3897 | 34 | `static List<Node> text(RefineryView.Words w)` | A word and the run after it on its line, from its baseline, aligned on its x; over a halo of the ground if it asks. |
| 3933 | 9 | `static javafx.scene.text.Font font(RefineryView.Face f, double size)` | A picture's face at a size: Plex Sans at its three weights, Plex Mono at two. |
| 3944 | 19 | `static String iconOf(RefineryView.Mark m, Sectors sectors)` | What an icon of the picture's is drawn as. |
| 3965 | 10 | `static javafx.scene.paint.ImagePattern hatch(String colour, double opacity)` | The import's hatching in a colour (mockup 1's: a stripe at .85 every eight pixels on a ground at .14), at an opacity; one pattern a colour and opacity. |
| 3979 | 19 | `static VBox refineryStrip(RefineryView.View v)` | THIS MONTH, BY PRODUCT: its heading, and a cell a product in equal columns. |
| 4000 | 54 | `static VBox stripCell(RefineryView.Cell c)` | One product's cell: its swatch and name, its bar, and its five lines. |

### OPERATIONS · THE OIL INDUSTRY (0.7.96) (lines 4055-4471)

| line | len | member | says |
|---:|---:|---|---|
| 4099 | 17 | `List<Node> oilPanels(OilView.View v)` | Oil's panels, in the page's order. |
| 4118 | 8 | `static VBox oilBox(Node...rows)` | A box of the page (mockup 2's .box): the panel's ground, an edge, its rows. |
| 4128 | 20 | `static HBox oilHead(String title, String line, String info, Node right)` | A box's heading: its title, its line, an (i), and something at its right. |
| 4150 | 7 | `static Label oilWords(String text, double size, String colour)` | Words at a size in a colour, wrapping. |
| 4159 | 6 | `static Label oilFigure(String text, double size, String colour)` | A figure at a size in a colour, the regular weight, never cut. |
| 4167 | 8 | `static void oilTip(Node n, String tip)` | A tooltip on a node. |
| 4177 | 7 | `static Region oilSwatch(String colour)` | A swatch: a product's or a series' colour in a small square. |
| 4186 | 26 | `static GridPane oilFigures(OilView.View v)` | The four figures across the page (mockup 2's .figs). |
| 4214 | 44 | `VBox oilWells(OilView.View v)` | THE WELLS: the two cards by kind, the chart with its legend, the investors' words. |
| 4260 | 13 | `static VBox oilCard(String svg, String name, String count)` | A wells card (mockup 2's .wc): its icon, its name, its count at the right. |
| 4275 | 10 | `static HBox oilFact(OilView.Fact f)` | A line of a wells card (mockup 2's .kv): its words, its figure at the right. |
| 4287 | 15 | `static VBox oilPlatform(OilView.PlatformLine p)` | A platform's lines (mockup 2's .plat): its colour, its name and its figure, and under them its words. |
| 4304 | 22 | `static GridPane oilTable(double[] widths, String[] heads, boolean[] right)` | A table's grid and its heads: a column a width (0: it takes the rest), each column's cells to the right where `right` says. |
| 4328 | 8 | `static HBox oilCell(Node n, boolean right)` | A cell: its node in the cell's padding, to the left or the right; a left cell's words wrap in the column's width. |
| 4338 | 8 | `static Region oilRule()` | A rule under a table's row. |
| 4348 | 8 | `static Label oilPill(String text, String colour)` | A pill (mockup 2's .pill): its words in its colour, an edge of it. |
| 4358 | 33 | `static VBox oilUnits(OilView.View v)` | THE REFINERY: its units as a table, and the refiners' word. |
| 4393 | 24 | `static VBox oilProducts(OilView.View v)` | PRODUCTS: crude and the nine, a row each. |
| 4419 | 38 | `VBox oilReserve(OilView.View v)` | THE STRATEGIC RESERVE: its lines, and its two levers on dial cards; or the words that none stands. |
| 4459 | 12 | `static java.util.function.DoubleFunction<List<Pieces.Effect>> effects(Game g, java.util.function.DoubleFunction<List<OilView.Ef...` | A lever's effects (OilView.Effect) as the dial card's rows: tonnes in the model's words, money in the city's mark. |

### THE SHELF (0.7.45) (lines 4472-4623)

| line | len | member | says |
|---:|---:|---|---|
| 4481 | 4 | **type** `record ShelfWords(boolean counted, double shelf, double floor, double cap, double clearing, boolean clearin...` | THE SHELF's words and figures, worked out without drawing them. |
| 4487 | 7 | `static String wayWords(double speed)` | A share of the way in words: "a sixth", "a quarter", "half". |
| 4495 | 60 | `ShelfWords shelfWords(Retail r)` |  |
| 4557 | 10 | `static String throttleCategory(SectorFlow.Plant plant)` | The Build category that relieves the thinnest of a plant's power, road and health (D13's rule, one plant), or null. |
| 4576 | 47 | `VBox shelfCard(Retail r)` | THE SHELF: what a basket costs, beside the baskets and what limited them. |

### WHAT IT CHARGES (0.7.45) (lines 4624-5048)

| line | len | member | says |
|---:|---:|---|---|
| 4630 | 1 | **type** `record ChargesWords(double floor, double ceiling, double charged, double target, String line, String served...` | WHAT IT CHARGES' words and figures, worked out without drawing them. |
| 4637 | 3 | `static String served(double served, double reach, double wanted)` | "Served nothing" when a month counted a reach or a want and served none of it; a reload counts none of the three until its month runs (they are the month's flows, not saved), and that is not a month that served nothing. |
| 4641 | 9 | `static ChargesWords chargesWords(Restaurants k)` |  |
| 4651 | 9 | `static ChargesWords chargesWords(LuxuryRetail l)` |  |
| 4662 | 14 | `VBox marginCard(ChargesWords w)` | WHAT IT CHARGES: the margin as a run from its floor to its ceiling, the target a dashed rule. |
| 4678 | 15 | `VBox everyLine(Sector sector)` | The old page, whole: Sector.operations() drawn as statement lines. |
| 4695 | 16 | `VBox flowCard(Sector sector, SectorFlow.Flow flow, boolean animate)` | The flow: what goes in, a chevron, the plant, a chevron, what comes out. |
| 4713 | 10 | `static Label chevron()` | A chevron between the flow's columns. |
| 4728 | 42 | `VBox inputsColumn(Sector sector, SectorFlow.Flow flow)` | WHAT GOES IN: each good bought - the city's part and the world's - and each service, by name. |
| 4772 | 9 | `static HBox rowHead(Node name, String info, String money)` | A row's head: its name (and its (i)), and its money at the right. |
| 4783 | 29 | `VBox inputRow(SectorFlow.In i, boolean counted)` | One good bought: its name, the city's part and the world's on a bar, the units and the share, the money; or a service, by name. |
| 4814 | 16 | `static String inputWords(SectorFlow.In i)` | A good bought, in words: "82,647 t · 7% from the city · on hand 3,478 vans". |
| 4832 | 4 | `static String units(double n, Good g)` | A count of a good in its unit, "under 1 van" for a fraction that would read "0 vans". |
| 4844 | 43 | `VBox plantCard(Sector sector, SectorFlow.Flow flow, boolean animate)` | THE PLANT: how many stand and are on site; a ring of the rate it runs at, sweeping up on a new month; the six throttles as a cascade, each its ratio on a bar and the running product after it, the thinnest amber; and i... |
| 4889 | 5 | `static HBox centred(Node n)` | A node centred in a row of its own. |
| 4896 | 22 | `static GridPane cascade(SectorFlow.Plant p)` | The throttle cascade: each of the six, its ratio on a bar, and the rate after it - the thinnest drawn amber. |
| 4920 | 54 | `VBox outputsColumn(SectorFlow.Flow flow)` | WHAT COMES OUT: each good made against what it could make - sold here, shipped, into stock, idled - its price and cost; and work billed. |
| 4976 | 29 | `static String outputWords(SectorFlow.Out o, BuildCard.Note note)` | A good made, in words: "made 78,615 t of 203,400 · 32,018 here · 46,598 shipped"; a seller-priced one against the Build note's capacity. |
| 5007 | 9 | `static String coverWords(BuildCard.Note note)` | What a seller-priced good's capacity is, in the Build note's words. |
| 5018 | 6 | `static String railWords(BuildCard.Note note, boolean counted)` | The railway's work against its track, off Build's note (RAIL {the city's trade in tonnes, the network's capacity}); null for anyone else. |
| 5030 | 5 | `static HBox noteInLayers(String shown, String whole)` | A sector's note in two layers (0.7.21; Sector.Line.note(shown, whole)): the short line where statementNote() would put the paragraph, and the paragraph behind its (i). |
| 5037 | 11 | `static String toneColour(Sector.Line.Tone tone)` | The palette colour a sector's line asked for, or none. |

