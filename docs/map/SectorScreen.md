# SectorScreen.java - 7,221 lines · 291 methods · 74 constants · interface

`ham/citybuildersim/ui/SectorScreen.java` - generated 2026-11-10 by CodeMap; line numbers are as of that run.

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

**Uses:** [Palette](Palette.md) (900), [Kept](Kept.md) (640), [Sector](Sector.md) (212), [SectorStatements](SectorStatements.md) (143), [FreightView](FreightView.md) (128), [OilView](OilView.md) (104), [Icons](Icons.md) (87), [RefineryView](RefineryView.md) (77), [SectorBooks](SectorBooks.md) (70), [BuildCard](BuildCard.md) (70), [BuildScreen](BuildScreen.md) (63), [StatementView](StatementView.md) (53), [SectorFlow](SectorFlow.md) (44), [IndustryPicture](IndustryPicture.md) (27), [BusinessDebtManager](BusinessDebtManager.md) (19), [BusinessInvestment](BusinessInvestment.md) (18), [ResearchView](ResearchView.md) (16), [CityCalendar](CityCalendar.md) (14), [Equity](Equity.md) (14), [Formats](Formats.md) (13), [Retail](Retail.md) (11), [BuildAdvice](BuildAdvice.md) (10), [Good](Good.md) (10), [OrderBook](OrderBook.md) (7), [Debt](Debt.md) (7), [BuildingsTemplate](BuildingsTemplate.md) (7), [Restaurants](Restaurants.md) (7), [LuxuryRetail](LuxuryRetail.md) (7), [Game](Game.md) (6), [Bank](Bank.md) (6)... and 31 more

**Used by (8):** [BankScreen](BankScreen.md), [FundScreen](FundScreen.md), [HistoryScreen](HistoryScreen.md), [KeptBench](KeptBench.md), [LandScreen](LandScreen.md), [StatementView](StatementView.md), [TradeScreen](TradeScreen.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 63 | THE SECTOR ECONOMY. |
| 223 | · THE LIST (0.7.30) |
| 701 | · THE INVESTORS' WORD, IN A KIND (0.7.30) |
| 911 | ONE BUSINESS, FIVE PAGES |
| 1107 | · the screen's own pieces |
| 1328 | A STATEMENT LINE THAT OPENS |
| 1417 | · THE INCOME STATEMENT |
| 1424 | · what the two big lines open into |
| 1515 | · INCOME · THE SHIPPERS BY DIVISION (FR4, 2026-10-20) |
| 1868 | INCOME (0.7.30; 0.7.74): A WATERFALL, IN SHORT, AND THE STATEMENT |
| 2117 | THE BALANCE SHEET (0.7.30; 0.7.74): TWO BARS, IN SHORT, ITS EQUITY'S |
| 2571 | CASH AND CREDIT (0.7.30; 0.7.74): A BRIDGE IN THREE STEPS, AND WHAT |
| 2999 | STATEMENT VIEWS (0.7.74): A SUMMARY AND A STATEMENT |
| 3117 | · Income |
| 3174 | · Balance sheet |
| 3272 | · Cash & debt |
| 3486 | · Investors |
| 3852 | THE INVESTORS (0.7.30): WHY ISN'T IT BUILDING, AND WHAT WOULD LET IT? |
| 3885 | · · what it decided |
| 3897 | · · its buildings |
| 3907 | · · its equipment (R5) |
| 3915 | · · and what stops it |
| 3946 | · · the rules |
| 3959 | · · what you control |
| 4234 | ITS EQUIPMENT (R5, slice B2; research-design 2.2 and 5.5, mockup 4) |
| 4435 | OPERATIONS (0.7.30): INPUTS → THE PLANT → OUTPUTS |
| 4504 | · OPERATIONS · HOW ITS TONNES MOVE (FR4, 2026-10-20) |
| 4798 | · ...and Iron & Steel's Pelletising Plant, before and after a siding |
| 4836 | · OPERATIONS · THE REFINERY (0.7.95) |
| 5215 | · OPERATIONS · THE MATERIALS INDUSTRIES (0.7.143) |
| 5435 | · OPERATIONS · THE OIL INDUSTRY (0.7.96) |
| 5925 | · OPERATIONS · FREIGHT (FR4, 2026-10-19) |
| 6063 | · ALL |
| 6328 | · HAULAGE |
| 6469 | · RAIL |
| 6526 | · PORTS |
| 6599 | · THE SHELF (0.7.45) |
| 6757 | · WHAT IT CHARGES (0.7.45) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 101 | `SectorScreen.SECTOR_HOME` | `"Operations"` | The page a business opens on, and falls back to for a page it does not know. |
| 108 | `SectorScreen.SECTOR_PAGES` | `{ "Operations", "Income", "Balance sheet", "Cash & debt", "Investors" }` | The five pages, in the strip's order: the names other screens open a business's books by. |
| 112 | `SectorScreen.PAGE_ICONS` | `{ Icons.INDUSTRY, Icons.COIN, Icons.FINANCES, Icons.BANK, Icons.SECTOR }` | Each page's icon on its chip (0.7.30): the plant, the coin, the ledger, the bank, the investors. |
| 124 | `SectorScreen.FRAME_CHROME` | `276` | How much of the stage a business's fixed frame takes above its page's scroller - the head, the five figures, the investors' line, the pages, and their gaps - until the frame is laid out and its own height is read (0.7... |
| 127 | `SectorScreen.LIST_CHROME` | `190` | ...and the list's: its head and its four figures (0.7.30). |
| 130 | `SectorScreen.STAGE_REST` | `36` | What the menu spends around the frame and the page: its padding over and under them and the gap between, and the four pixels of slack its height is held to (UserInterface's rootMenu) - so the page is what is left unde... |
| 133 | `SectorScreen.COUNT_MILLIS` | `400` | How long a figure takes to count from last month's to this month's (0.7.30). |
| 136 | `SectorScreen.SWEEP_MILLIS` | `700` | ...and the plant's ring to sweep up from nothing (0.7.30). |
| 218 | `SectorScreen.BOUND_TO_FRAME` | `"sector.frame", BOUND_TO_SCROLLER = "sector.scroller"` | The key in a kept scroller's properties for the frame its height is bound to, and in a kept page's for the scroller its width is bound to (frameOver()). |
| 228 | `SectorScreen.LIST_INFO` | `"What the city's businesses kept this month, after tax - each card one busine...` | What the list's (i) holds: the old caption, and what a card does. |
| 235 | `SectorScreen.NOTHING_YET_LIST` | `"The sector books are written when a month closes.This city has not " + "clos...` | The empty books' sentence (the old list's alert). |
| 239 | `SectorScreen.NOTHING_YET_SECTOR` | `"This city has not closed a month since it was loaded, so there is no " + "st...` | ...and a business's (the old page's alert). |
| 243 | `SectorScreen.NO_WORD_YET_LIST` | `"Nothing recorded since the city was loaded or founded: a month on, each " + ...` | The list's one line while no business has a word for the month (0.7.34), in place of each card's own. |
| 421 | `SectorScreen.RUNNING_INFO` | `"How much of what its plants could make each business made this month: its " ...` | RUNNING AT's (i). |
| 553 | `SectorScreen.SPARK_WIDTH` | `120` | The width the sparkline takes on a card (0.7.30; 90 on the old list). |
| 556 | `SectorScreen.SPARK_HEIGHT` | `28` | ...and its height (0.7.30; 22 on the old list). |
| 559 | `SectorScreen.SPARK_MONTHS` | `24` | How many months of net income the sparkline draws (0.7.4): two years. |
| 1532 | `SectorScreen.SHIPPER_COLUMN` | `80` | The width of one figure column of the shippers table. |
| 1535 | `SectorScreen.SHIPPER_HEADS` | `{ ham.citybuildersim.sectors.Haulage.BILLED_AS, FreightView.RAIL_BILLED_AS, h...` | The shippers table's column heads: the lines the bills are raised under, then the sum. |
| 1885 | `SectorScreen.OPERATING_INFO` | `"What the business made from trading, after its sales tax and the ground it "...` | The operating line's (i): what it is, and the model's own operating income, which is before the ground and the sales tax (D4, D5). |
| 1889 | `SectorScreen.REFUND_INFO` | `"The sales tax line is a REFUND this month: the credit on what this sector " ...` | The sales tax refund's (i). |
| 1894 | `SectorScreen.FALL_INFO` | `"Revenue at the left, less the sales tax it remitted and what it bought, to i...` | The waterfall's (i). |
| 2201 | `SectorScreen.OWNERS_DOOR` | `"its owners, its share's price and what it pays: Investors"` | The Balance sheet's door to its owners' card (D7). |
| 3021 | `SectorScreen.INCOME` | `"income", SHEET = "sheet", EQUITY = "equity", CASH = "cash"` | Each statement's key in a business's open set: an open note on one is "income:note:5" in linesOpen(). |
| 3024 | `SectorScreen.OUTSIDE_INFO` | `"Money that reached its books outside its trading: the city's subsidy and the...` | Note 6, and the equity statement's outside line: what the outside lines are (F1). |
| 3033 | `SectorScreen.DERIVED_INFO` | `"Derived when the city was loaded: its save was made before share capital was...` | Share capital's line and note, when a save from before 0.7.75 was loaded (R3). |
| 3039 | `SectorScreen.CAPITAL_INFO` | `"What its owners put in: the book its founders' shares were issued against, a...` | Share capital's line and note (R3). |
| 3044 | `SectorScreen.STOCK_WORDS` | `"Stock is held at what it would fetch today, so a price collapse shrinks this...` | Note 7: the stock. |
| 3048 | `SectorScreen.LAND_WORDS` | `"Its land at today's prices, by the ground it stands on: a share of the city'...` | Note 11 (ET3, spec-earth2-terrain 5.3): its land by the ground it stands on. |
| 3066 | `SectorScreen.ABROAD_WORDS` | `"What it has sent abroad for the world's rate, in the city's money at the rat...` | Note 9: what it holds abroad. |
| 3071 | `SectorScreen.DUE_WORDS` | `"What its loans, bonds and mortgages ask at the twelve settles ahead: a loan ...` | What falls due within a year (R2), behind its line's and its card's (i). |
| 3076 | `SectorScreen.NOT_SPLIT_WORDS` | `"When it falls due is counted at the next month's books: a load keeps the deb...` | The month after a load, R2's line. |
| 3221 | `SectorScreen.EQUITY_HEADS` | `{ "share capital", "kept, revalued", "total" }` | The equity statement's columns (R3). |
| 3224 | `SectorScreen.REVALUED_INFO` | `"Whatever else moved its equity, worked out as what is left: a building bough...` | The equity statement's remainder's (i). |
| 3292 | `SectorScreen.SCHEDULE_RATE` | `70, SCHEDULE_RUNS = 80` | The debt schedule's columns (R7): the kind (DEBT_KIND), five figures (DEBT_FIGURE), the rate and when the last of it falls due. |
| 3295 | `SectorScreen.SCHEDULE_INFO` | `"What it owed of each kind at last month's sheet, what it borrowed, repaid an...` | The schedule's (i). |
| 3413 | `SectorScreen.DEBT_KIND` | `130, DEBT_FIGURE = 100` | The debt card's columns: the kind, then owed, within a year, in one to five, after five, and the month's interest. |
| 3506 | `SectorScreen.EVERY_GATE_INFO` | `"Each building it can put up at every gate investors ask - ore, a licence, " ...` | EVERY BUILDING AT EVERY GATE's (i): since 0.7.122 the last gate the interest on its whole cost, consider()'s brake (batch C1). |
| 3517 | `SectorScreen.GATE_NAME` | `200, GATE_MARK = 44, GATE_FIGURE = 96, GATE_PAYBACK = 84, GATE_PAYS = 210` | The grid's columns: the building, a gate's mark, a figure, the payback, how it would pay. |
| 3520 | `SectorScreen.GATE_HEADS` | `{ "ore", "licence", "staff", "land", "pays", "interest", "lender" }` | The gates' heads, in BuildCard.GateKind's order: the sixth, since 0.7.122, the interest on its whole cost, and the seventh (I13-L) the mortgage lender's test - a column only where a building of the sector is a landlor... |
| 3710 | `SectorScreen.SHARE_ROWS` | `{ { "Shares", "n" }, { "Earnings a share, the month", "$" }, { "Dividend a sh...` | The report's rows: {label, kind} - "n" a count, "$" money a share, "%" a share, "x" times, "M" money. |
| 3754 | `SectorScreen.SHARE_LABEL` | `220, SHARE_FIGURE = 120` | The report's columns. |
| 3870 | `SectorScreen.CONTROL_INFO` | `"None of this is yours to set.These are private companies deciding for " + "t...` | WHAT YOU CONTROL's (i): the old page's last paragraph. |
| 3875 | `SectorScreen.WAITING_INFO` | `"This business wants to build and there is nowhere to put it.It is the one " ...` | The waiting-on-ground alert's (i). |
| 4254 | `SectorScreen.EQUIPMENT_INFO` | `"A piece is fitted only when it pays over its own life: what it saves a month...` | ITS EQUIPMENT's (i): research-design 2.2's rule, in two sentences. |
| 4459 | `SectorScreen.FLOW_INFO` | `"Each row's money is the month the books closed on - the Income page's - and ...` | The flow's money and units: which month each is (SectorFlow's two months). |
| 4545 | `SectorScreen.TONNES_INFO` | `"How this business's freight went in the month billed, good by good.Tonnes it...` | What the strip's (i) says. |
| 4555 | `SectorScreen.PELLET_INFO` | `"The Pelletising Plant's city legs at nameplate - every tonne of the bulk goo...` | The siding line's (i) is its own sentence; the plant card's. |
| 4562 | `SectorScreen.MOVE_NAME` | `150` | The width of a line's name column. |
| 4854 | `SectorScreen.REFINERY_HEAD` | `"THE REFINERY THIS MONTH"` | The card's heading, its line, and what its (i) says. |
| 4856 | `SectorScreen.REFINERY_SUB` | `"where the crude went · ribbons to scale"` | ...the line beside the heading... |
| 4858 | `SectorScreen.REFINERY_INFO` | `"The month's crude comes in on the left, from the wells, the reserve or the w...` | ...and the (i)'s words: the picture read from left to right. |
| 5136 | `SectorScreen.HATCHES` | `new java.util.HashMap<>()` | The hatchings made so far, by colour and opacity (hatch()'s cache). |
| 5233 | `SectorScreen.INDUSTRY_INFO` | `"On the left is what the month worked: a mine's fields, each a vessel filled ...` | What the materials card's (i) says: the picture read from left to right. |
| 5453 | `SectorScreen.OIL_WELLS_INFO` | `"The city's wells by kind.A land well lifts the ground pool - the oil under t...` | What the oil industry's (i)s say. |
| 5459 | `SectorScreen.OIL_UNITS_INFO` | `"Every kind of refinery unit.Its spread is what it makes of a litre of its fe...` | ...THE REFINERY's, its units' table... |
| 5464 | `SectorScreen.OIL_PRODUCTS_INFO` | `"Every product of the refinery, and crude: its price here and the world's, th...` | ...PRODUCTS', crude's and the nine products' table... |
| 5468 | `SectorScreen.OIL_RESERVE_INFO` | `"The city's own crude, in its Strategic Reserves' tanks.Fill orders crude for...` | ...and THE STRATEGIC RESERVE's, which the Fill and Release cards' (i)s say too. |
| 5474 | `SectorScreen.RESERVE_CAVEAT` | `"bought at the next clearing at what crude then costs; what the room cannot t...` | The Fill card's caveat under its effects... |
| 5476 | `SectorScreen.RESERVE_CAVEAT_INFO` | `"A fill is an order for the next clearing: the wells' crude pro rata with the...` | ...and what its (i) says: an order for the next clearing, paid at the next strike. |
| 5480 | `SectorScreen.RELEASE_CAVEAT` | `"offered to the refiners; what they do not take stays in the tanks"` | The Release card's caveat; its (i) is OIL_RESERVE_INFO. |
| 5483 | `SectorScreen.RESERVE_LADDER` | `380` | The lever's ladder width on its dial card. |
| 5959 | `SectorScreen.FREIGHT_STRIP_INFO` | `"Freight is three businesses in one set of books: Haulage's trucks, the " + "...` | What the division strip's (i) says. |
| 5965 | `SectorScreen.FREIGHT_LEGS_INFO` | `"Every tonne that crossed the city's boundary went the long haul by the " + "...` | The legs card's (i). |
| 5973 | `SectorScreen.FREIGHT_SHIPPERS_INFO` | `"The sectors billed by this division this month, largest bill first: the " + ...` | The shippers card's (i). |
| 6179 | `SectorScreen.LEG_NAME` | `120` | The width of a leg's name column. |
| 6218 | `SectorScreen.LegBar.BAND` | `26` | The bar's height, in pixels: 26. |
| 6367 | `SectorScreen.FREIGHT_TRUCKS_INFO` | `"The trucks Haulage owns against the trucks the plant standing asks for to " ...` | Haulage's fleet card's (i). |
| 6372 | `SectorScreen.FREIGHT_HAULAGE_QUOTE_INFO` | `"What a tonne's city leg costs the shipper.The rule: Haulage's running " + "c...` | Haulage's price card's (i). |
| 6515 | `SectorScreen.FREIGHT_RAIL_INFO` | `"The wagon sets the railway owns against the sets its track standing needs." ...` | The railway's fleet card's (i). |
| 6522 | `SectorScreen.FREIGHT_RAIL_QUOTE_INFO` | `"What the railway charges a tonne, as a share of what a lorry charges the " +...` | The railway's price card's (i). |
| 6552 | `SectorScreen.FREIGHT_PORTS_INFO` | `"The terminals standing by kind, the tonnes each kind handled this month " + ...` | The berths card's (i). |
| 6696 | `SectorScreen.SHELF_INFO` | `"A basket is one person's groceries for a month.The shelf moves a sixth of th...` | THE SHELF's (i). |
| 6864 | `SectorScreen.FOLD_PAST` | `6` | The goods a column folds into one row past this many (the shops' and the kitchens' thirteen foods). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 51 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 56 | `final Kept.Pool pool` | The nodes this screen made at its last draw, handed back to it at the next (KP4; study UP4, docs/studies/spec-screens-in-place.md). |
| 99 | `Sector openSector` | Which sector's books are open, or null for the list. |
| 102 | `String sectorPage` |  |
| 105 | `boolean sectorsExpanded` | Whether every card on the list is open to its second row (0.7.4) - kept while the game runs, like the page, and never saved. |
| 115 | `final java.util.Set<String> detailsOpen` | Which "details" folds are open - kept while the game runs, not saved (0.7.30). |
| 118 | `private final java.util.Map<String, java.util.Set<String>> linesOpen` | Which statement lines are open, by sector and then by label (0.7.30: a waterfall's bar opens its line). |
| 121 | `private final java.util.Map<String, Integer> drawnAt` | The month each view was last drawn at: its figures count up and its ring sweeps once a month, not on every redraw. |
| 221 | `private javafx.scene.control.ScrollPane body` | The page's scroller. |
| 3015 | `boolean statementView` | Whether the pages draw the formal statement rather than the summary (D1): kept while the game runs, never saved; the Bank tab's two statements read it too. |
| 3018 | `boolean showChange` | The Statement view's change and common-size columns, each on or off from its toolbar, on every statement at once. |
| 4196 | `private String fitRefusedKey, fitRefused` | The player's Fit last refused (R1b): the building and piece it was asked of ("template id:piece id"), the words, and the month, shown under its pill in that month only. |
| 4197 | `private int fitRefusedMonth` |  |
| 4894 | `private Sectors sectors` |  |
| 4895 | `private RefineryView.View view` |  |
| 4896 | `RefineryView.Picture painted` |  |
| 4897 | `private double drawnAt` |  |
| 4899 | `private boolean moved` | Whether the view moved since the last paint. |
| 4900 | `private final Kept.Pool shapes` |  |
| 5282 | `private Game game` |  |
| 5283 | `private IndustryView.View view` |  |
| 5284 | `IndustryPicture.Picture painted` |  |
| 5285 | `private double drawnAt` |  |
| 5287 | `private boolean moved` | Whether the view moved since the last paint. |
| 5288 | `private final Kept.Pool shapes` |  |
| 5977 | `FreightView.Division freightDivision` | The division the Freight sector's Operations page is open on - kept while the game runs, not saved (FR4); InfrastructureScreen's doors may set it before opening the page. |
| 6219 | `private List<FreightView.Row> rows` |  |
| 6220 | `private double scale` |  |
| 6221 | `private java.util.function.Consumer<FreightView.Division> door` |  |
| 6222 | `private final Region track` |  |
| 6223 | `private final List<javafx.scene.shape.Rectangle> blocks` |  |
| 6224 | `private final List<Label> labels` |  |
| 6226 | `private String laidOut` | What the layout read at the last set(): see layoutKey(). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 48 | 7174 | **type** `final class SectorScreen` | The sector economy: every business in the city as a card, and each one's five pages - what goes in and what comes out, the income statement with last month beside it, the balance sheet and its owners, cash and credit,... |
| 53 | 1 | `SectorScreen(UserInterface ui)` |  |
| 59 | 3 | `void forget()` | Every node is made new at the next draw: another city opened (UserInterface.anotherCity()). |

### THE SECTOR ECONOMY. (lines 63-222)

| line | len | member | says |
|---:|---:|---|---|
| 139 | 6 | `void openSectorBooks(Sector sector, String page)` | Open one business's books from somewhere else in the game. |
| 147 | 5 | `void open(String page)` | One of the open business's pages, at its top. |
| 154 | 5 | `void backToList()` | Back to the list, at its top. |
| 161 | 4 | `void showSectorMenu()` | The tab lands here: what every business in the city earned. |
| 171 | 8 | `void drawPage()` | The page itself, put in the window through setPage(): what keepMenu runs as a kept draw from the pool, and what KeptBench runs kept and built new for KeptCheck - the list, or the business and page `openSector` and `se... |
| 181 | 4 | `private boolean freshMonth(String view)` | Whether this draw of a view is the first since its month changed - the count-ups and the sweep run then and on no other redraw (and, with UserInterface.countsUp(), only at a clock speed a figure can count at). |
| 187 | 29 | `private void frameOver(VBox frame, VBox page, double chrome)` | The fixed frame over a scrolling page, at the page's width: the shape every redrawn tab has (Infrastructure's, 0.7.29). |

### THE LIST (0.7.30) (lines 223-700)

| line | len | member | says |
|---:|---:|---|---|
| 246 | 40 | `void listScreen()` |  |
| 293 | 6 | `static boolean noWordYet(Game game, List<Sector> order)` | Whether no business has a word for the month (0.7.34): every sector's Game.getLastInvestment() empty, as it is from a load or a founding until a month runs. |
| 301 | 7 | `List<Sector> marketOrder()` | The sectors in Build's market order: by the group their own buildings are under (BuildCard.groupRank()), then as Sectors lists them. |
| 316 | 23 | `HBox listVitals(SectorBooks books, List<Sector> order, boolean animate)` | The list's four figures: what they all kept, with its move on last month; how many lost, the two that lost most named; who works there, against the posts; and the range they run at, over every business with a plant an... |
| 347 | 3 | **type** `record ListFigures(double kept, double before, boolean compared, String moved, String moveTone, String kept...` | The list's four figures as words, off the books and the sectors' plants - a pure read, for the strip and the probes: what they kept and their move, the sectors losing and the words naming them, the workers and posts, ... |
| 351 | 68 | `static ListFigures listFigures(SectorBooks books, List<Sector> order)` |  |
| 437 | 71 | `VBox sectorCard(Sector sector, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then, List<? extends Number> netIncomeSerie...` | One business on the list (0.7.30): its icon, its name with the Build group under it and its blurb on hover, what it kept and its move; its last two years, its workers, and how much of its plant runs with what cuts it ... |
| 510 | 8 | `static String runningWords(Sector sector, SectorFlow.Plant plant)` | ...the grocers' in theirs (0.7.45): "handed over 58% of what was asked · roads 70%". |
| 520 | 7 | `static String runningWords(SectorFlow.Plant plant)` | A card's plant in words: "running at 39% · roads 64%" - the throttle that cuts it most, when one is under 100% - or the homes let, or "no plant standing". |
| 529 | 6 | `String groupWords(Sector sector)` | "Industry · food mills": the Build category and the group a sector's own buildings are under, once when they are one. |
| 537 | 3 | `static Label caption(String text, String tone)` | A line of caption that wraps, in a colour. |
| 542 | 9 | `static Label caption(String text, String tone, String more)` | ...with more style (KP2b: a table's note, with its room), given where the caption is made and not added to its style after. |
| 577 | 4 | `javafx.scene.Node sparkline(List<? extends Number> series)` | A sector's net income as a line (0.7.4): the last SPARK_MONTHS of the history's netIncome:<sector>, the zero line faint under it. |
| 582 | 73 | `private javafx.scene.Node sparklineOf(List<? extends Number> series)` |  |
| 661 | 18 | `GridPane sectorCardMore(Sector sector, SectorBooks.SectorMonth now)` | The card's five figures more, with the list opened (0.7.4; three and two since 0.7.30): revenue, margin and cash; what it owes and its posts filled - each off SectorMonth or the sector, nothing recomputed. |
| 681 | 11 | `static VBox moreCell(String word, String figure, String tone)` | One labelled figure on a card's second row: the word over the figure, 120 wide. |
| 694 | 6 | `static String sectorBlurb(Sector sector)` | What the business actually does - the sector's own first sentence, whole (cut at 72 until 0.7.20). |

### THE INVESTORS' WORD, IN A KIND (0.7.30) (lines 701-910)

| line | len | member | says |
|---:|---:|---|---|
| 713 | 15 | `static String kindIcon(BuildCard.WordKind k)` |  |
| 730 | 8 | `static String kindColour(BuildCard.WordKind k)` | The kind's colour: its edge and its icon. |
| 740 | 16 | `static String kindWord(BuildCard.WordKind k)` | ...and its word, before the sector's. |
| 758 | 6 | `String sectorWords(BuildCard.SectorInvestors si)` | The words after the kind: what is on site of theirs, the build card's "nothing recorded" before a month has run, or the word with "Holding: " gone. |
| 766 | 5 | `static String shortWord(String words)` | A word's short form for a card: a "Built" or "Sold" word up to its " - " (the frame's line and the tooltip have the rest). |
| 773 | 10 | `static String builtTag(String word)` | "+2 Industrial Bakery": what a "Built" word built, for the month's flash; null for any other word. |
| 788 | 31 | `Node investorsMini(BuildCard.SectorInvestors si, boolean blank)` | The word on a card: the kind's icon at 12, its word, the sector's - wrapping, never cut; a click opens the Investors page. |
| 821 | 4 | `static String textColour(BuildCard.WordKind k)` | A kind's word in a colour that reads as text: the grey kinds' edge is too faint for words. |
| 834 | 3 | `VBox investorsLine(BuildCard.SectorInvestors si, boolean animate, double iconBox)` | The investors' line (0.7.30): under every page's strip, 36 high on the raised ground with a 3 px edge in the kind's colour - its icon in a tinted square, the kind in a word, the sector's word whole, and "Investors ›" ... |
| 839 | 36 | `VBox investorsLine(BuildCard.SectorInvestors si, boolean animate, double iconBox, String room)` | ...with the room round it (the decision card's is more: KP2b, given where the line is made, not replaced in its style after). |
| 877 | 11 | `static HBox kindRow(BuildCard.WordKind k, String words, double box)` | One kind's row: its icon square, its word and the words after it, wrapping. |
| 890 | 13 | `static void flash(Region r)` | A region's one flash in the building pink: a "Built" month landing (0.7.30). |
| 905 | 5 | `static void noFlash(Region r)` | ...and where none is due: the region's effect is set to nothing and a fade still running on it from the last draw stopped (set on both sides, KP4). |

### ONE BUSINESS, FIVE PAGES (lines 911-1106)

| line | len | member | says |
|---:|---:|---|---|
| 923 | 64 | `void drawSectorScreen()` |  |
| 989 | 35 | `HBox sectorHead(Sector sector)` | The head: "Sectors ›" - the way back - the business's icon and name, its blurb behind an (i), and a door to its buildings on Build. |
| 1032 | 35 | `HBox sectorVitals(Sector sector, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then, SectorFlow.Plant plant, boolean ani...` | The five figures: KEPT with its move, MARGIN, CASH, OWES - "owes nothing" when it owes nothing, the rate and leverage when it owes something (B8) - and RUNNING AT with the throttle that cuts it most, or the homes let ... |
| 1075 | 8 | `static String[] handedCell(Retail shops, SectorFlow.Plant plant)` | The grocers' fifth figure (0.7.45; the UI spec's D8), {label, figure, note}: the share of the baskets asked for at the price that the shops handed over (Retail.getHouseholdShare()) - for groceries the outcome, as LET ... |
| 1085 | 4 | `static String handedTone(Retail shops)` | ...its verdict: what went unhanded, on GOING SHORT's own lines (the UI spec's D10) - one hunger rule, not a second. |
| 1091 | 5 | `static String owesWords(boolean blocked, int blockedMonths, SectorBooks.SectorMonth now)` | OWES's note: the ban, "owes nothing" when it owes nothing (B8: it quoted a rate on no debt), or its rate and leverage. |
| 1098 | 8 | `static String[] runningCell(SectorFlow.Plant plant)` | The frame's fifth figure, {label, figure, note}: what it runs at and what cuts it most; the landlords' homes let; or no plant. |

### the screen's own pieces (lines 1107-1327)

| line | len | member | says |
|---:|---:|---|---|
| 1110 | 1 | `static String m(double thousands)` | Money in the model's thousands, as the statements write it: "$13.6M". |
| 1113 | 1 | `static String marginWords(double share)` | A margin to a tenth of a per cent, with no sign on one that rounds to nothing (B11). |
| 1116 | 3 | `static VBox kpi(String label, String value, String note, String tone, String info, String where, Runnable go)` | A limit cell with an (i) after its label when `info` is not null. |
| 1121 | 14 | `static VBox kpi(String label, String value, String note, String tone, String noteTone, String info, String where, Runnable go)` | ...with the note in a colour of its own (a move on last month, up or down), given where the cell is made (KP2b: it was set after). |
| 1137 | 1 | `static Label figureOf(VBox cell)` | A limit cell's figure, for its count-up. |
| 1141 | 17 | `static void countUp(Label label, double from, double to, java.util.function.DoubleFunction<String> words)` | A figure counted from last month's to this month's over COUNT_MILLIS, ending on this month's exactly (0.7.30). |
| 1164 | 4 | `static void stopCounting(javafx.scene.Node figure)` | A figure on a kept label that does not count at this draw has no count-up left running from the last (City History's stopCounting, KP1): the clock too fast to follow (UserInterface.countsUp()), or a redraw in the same... |
| 1173 | 16 | `static Region sweep(Region ring)` | A ring (Pieces.ring()) whose arc sweeps up from nothing over SWEEP_MILLIS - there is no last month's rate to sweep from (0.7.30). |
| 1191 | 7 | `static Region spring()` | A spacer that takes the room left in a row - a scope of its own (KP4), so one drawn only sometimes shifts no other region's node (a swatch would inherit its grow). |
| 1200 | 14 | `static HBox alertLine(String svg, String colour, String text, String info, Node right)` | One line that something is wrong or waiting: its icon in a colour, a few words, and the whole of it behind an (i). |
| 1216 | 9 | `static VBox card(Node...children)` | A card on the raised ground: a picture's frame. |
| 1227 | 12 | `static HBox head(String text, String info, Node right)` | A picture's caption in capitals, with an (i) after it when `info` is not null, and something at its right. |
| 1241 | 9 | `static Label figure(String text, double size, String tone)` | A figure, never cut, at a size, in a colour. |
| 1252 | 9 | `static Node withInfo(Node line, String info)` | An (i) after a statement line's label: the paragraph that sat under it (0.7.30). |
| 1263 | 3 | `java.util.Set<String> linesOpen(Sector sector)` | The statement lines open on one business's pages. |
| 1268 | 15 | `static HBox ratioRow(String name, String value, String colour, String info)` | A ratio as a chip in a row: its name, the chip, and its (i). |
| 1285 | 3 | `static VBox ratioColumn(String title, Node...rows)` | A ratio column, 300 wide, beside a statement. |
| 1290 | 11 | `static VBox ratioColumn(double pref, double max, String title, Node...rows)` | ...at a width of its own (KP2b: the borrowing and financing columns', given where the column is made, not set again after it). |
| 1303 | 17 | `static HBox keyPart(String colour, String name, String figure)` | One part of a bar's key: its swatch, its name and its figure. |
| 1322 | 5 | `static javafx.scene.layout.FlowPane key(List<HBox> parts)` | A bar's key, wrapping. |

### A STATEMENT LINE THAT OPENS (lines 1328-1416)

| line | len | member | says |
|---:|---:|---|---|
| 1361 | 4 | `HBox bookDetailRow(String label, String value, boolean indented, String tone)` | One line inside an opened statement line. |
| 1366 | 30 | `private HBox bookDetailRowOf(String label, String value, boolean indented, String tone)` |  |
| 1397 | 3 | `HBox bookDetailRow(String label, double amount, boolean indented)` |  |
| 1402 | 11 | `Label bookDetailNote(String text)` | A sentence at the bottom of an opened line, when the split has something to say. |

### THE INCOME STATEMENT (lines 1417-1423)

| line | len | member | says |
|---:|---:|---|---|
| 1420 | 3 | `static String inputLabel(Sector sector)` | What this sector's direct cost is actually called - the sector says. |

### what the two big lines open into (lines 1424-1514)

| line | len | member | says |
|---:|---:|---|---|
| 1437 | 5 | `VBox revenueDetail(Sector sector)` | Revenue, by good, each split into what the city took and what was shipped. |
| 1443 | 71 | `private VBox revenueDetailOf(Sector sector)` |  |

### INCOME · THE SHIPPERS BY DIVISION (FR4, 2026-10-20) (lines 1515-1867)

| line | len | member | says |
|---:|---:|---|---|
| 1539 | 60 | `Node shippersByDivision()` | The shippers billed this month, a column a division, or null when no haul has billed anyone yet. |
| 1601 | 19 | `HBox shipperRow(Node first, Node[] cells, String style)` | One row of the shippers table: its first column, then the figure columns at their width. |
| 1622 | 5 | `Label shipperTotal(double amount)` | A footer figure of the shippers table. |
| 1629 | 15 | `Label shipperFigure(double amount, Sector sector, String tone, String word)` | A shipper's figure for a division: a door to its input line, or a dash that opens nothing where the division billed it nothing. |
| 1646 | 5 | `void openInputLine(Sector shipper)` | A shipper's input line: its Income page, the Statement view, the cost of sales opened. |
| 1663 | 4 | `VBox inputsDetail(Sector sector)` | The cost of sales, by good, each split into what the city grew or made and what was landed. |
| 1668 | 77 | `private VBox inputsDetailOf(Sector sector)` |  |
| 1747 | 4 | `VBox wagesDetail(Sector sector)` | Wages by pay tier - which kind of worker this business is actually paying. |
| 1752 | 53 | `private VBox wagesDetailOf(Sector sector)` |  |
| 1817 | 4 | `VBox salesTaxDetail(Sector sector)` | The sales tax, as the ledger actually strikes it: charged on what was sold here, credited for what suppliers already remitted, and the difference remitted. |
| 1822 | 45 | `private VBox salesTaxDetailOf(Sector sector)` |  |

### INCOME (0.7.30; 0.7.74): A WATERFALL, IN SHORT, AND THE STATEMENT (lines 1868-2116)

| line | len | member | says |
|---:|---:|---|---|
| 1899 | 20 | `void incomePage(VBox page, Sector sector, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then, boolean animate)` |  |
| 1921 | 3 | `private static HBox ratio(String name, String value, String colour, String info)` | A ratio's row, a scope keyed by its name (KP4): a ratio that comes and goes with the month's figures shifts no other row's nodes. |
| 1926 | 35 | `List<Step> incomeSteps(Sector sector, SectorStatements.Table t, SectorBooks.SectorMonth now)` | The waterfall's steps: revenue, each cost down to what it kept, the statement's subtotals between them (D14); a bar with a note opens it in the Statement view. |
| 1963 | 3 | `static Step fallTotal(String name, double amount)` | A waterfall's total: the business's violet, red under nothing. |
| 1968 | 10 | `static VBox incomeShort(Sector sector, SectorStatements.Table t)` | IN SHORT on Income: revenue, the middle line, operating profit, profit before tax and the profit - the statement's own lines (D14). |
| 1980 | 5 | `static double[] nows(SectorStatements.Table t, String[] ids)` | A table's figures this month, by id. |
| 1987 | 5 | `static double[] thens(SectorStatements.Table t, String[] ids)` | ...and last month. |
| 1994 | 3 | `static String pctWords(double share)` | A share as a ratio chip writes it: a tenth of a per cent, a dash where it means nothing. |
| 1999 | 3 | `static String timesWords(double x)` | Times over, a dash where it means nothing. |
| 2011 | 63 | `VBox incomeRatios(Sector sector, SectorStatements.Table t, SectorBooks.SectorMonth now)` | The Income page's ratios, both views: gross, operating and net margin, interest cover on the statement's operating profit (D10, F6), the effective tax rate, the wages' take and earnings a share - and its format's own:... |
| 2080 | 30 | `VBox everyDollar(SectorBooks.SectorMonth now)` | OF EVERY DOLLAR IT TOOK (the Income summary): each cost as cents of a dollar of its revenue, and what it kept, on one bar - a refund is not a cost and is left out, so the cents can come to more than a dollar. |
| 2112 | 4 | `void openNote(Sector sector, String table, int note)` | A summary's door into the Statement view with one note open: the waterfall's bars. |

### THE BALANCE SHEET (0.7.30; 0.7.74): TWO BARS, IN SHORT, ITS EQUITY'S (lines 2117-2570)

| line | len | member | says |
|---:|---:|---|---|
| 2133 | 66 | `void balancePage(VBox page, Sector sector, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then)` |  |
| 2210 | 39 | `VBox balanceRatios(Sector sector, SectorStatements.Table t, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then)` | The Balance sheet's ratios, both views: current and quick (what falls due within a year needs R2, so they read a dash the month after a load), debt to equity and to assets, the owners' share, return on equity a year o... |
| 2251 | 23 | `Node equityBridge(SectorBooks.SectorMonth now, SectorBooks.SectorMonth then)` | ITS EQUITY THIS MONTH (the Balance summary): the statement of changes in equity as a bridge, or null without last month's sheet. |
| 2276 | 19 | `static VBox sheetRow(String name, List<Segment> parts, double scale, String total, List<HBox> keyParts)` | One bar of the sheet: its name, the bar on the sheet's scale, its total, and its key under it. |
| 2318 | 123 | `VBox ownersCard(int company, double bookEquity, double netIncome, boolean wide)` | Who owns a company, what a share is worth, and what it pays - as a card (0.7.30, D11), on a sector's Investors page since 0.7.75 (D7; its Balance sheet until then, which keeps a door to it - `wide`: the figures beside... |
| 2443 | 29 | `String ownersPolicy(int company)` | The owners card's (i): its regime and payout policy, and what it has raised and paid since founding. |
| 2474 | 19 | `static VBox miniFigure(String name, String value, String under, String tone, String info)` | One small figure in a strip: its name over it, a line under it, and an (i) when `info` is not null. |
| 2495 | 28 | `VBox orderBook(OrderBook shareBook)` | The order book's two sides, five levels each, as the old block's tables - in plain figures (B10). |
| 2529 | 6 | `void ownersBlock(VBox column, int company, double bookEquity, double netIncome)` | Who owns a company, for the Bank's Owners page: the owners card, in its statement column (0.7.30, D11; the block of statement lines this drew until then is the card's now). |
| 2542 | 28 | `Node sharePriceChart(int company)` | One company's share price over the city's life: the quote (the last trade until EM2, the desk's quote until 0.7.12 round 2) against what the register says a share is worth, both per founding share - the trend chart, w... |

### CASH AND CREDIT (0.7.30; 0.7.74): A BRIDGE IN THREE STEPS, AND WHAT (lines 2571-2998)

| line | len | member | says |
|---:|---:|---|---|
| 2588 | 63 | `void cashAndDebtPage(VBox page, Sector sector, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then)` |  |
| 2653 | 18 | `static List<Step> cashSteps(SectorStatements.Table cash)` | The bridge's steps: the cash flow statement's three sections, each with its three biggest items as its tooltip, and NOT ACCOUNTED FOR when there is any. |
| 2673 | 17 | `static String biggest(SectorStatements.Table t, String head, String total)` | A section's three biggest lines this month, in words: "Profit for the month +$1.2M\nIts suppliers' credit, net −$310k". |
| 2696 | 19 | `VBox carryCard(Sector sector, SectorStatements.Table inc, SectorStatements.Table cash, SectorBooks.SectorMonth now)` | CAN IT CARRY WHAT IT OWES (the Cash & debt summary): interest cover on the statement's operating profit (D10), its debt over a year of what its trading brings in, and whether it can borrow at all. |
| 2717 | 28 | `List<Node> whenDue(Sector sector)` | WHEN IT FALLS DUE (R2), under what it owes by kind: within a year, in one to five, after five - the city debt's ladder colours; or not counted yet. |
| 2747 | 11 | `static VBox endFigure(String name, String value, String tone)` | A figure at a bridge's end: its name over it. |
| 2760 | 25 | `String spreadInfo(String key)` | The spread paragraph, behind the rate bar's (i) (the old page's note under "It pays"). |
| 2792 | 85 | `VBox creditPictures(Sector sector, SectorBooks.SectorMonth now)` | Beside the reconciliation: its rate in parts - prime, its own risk, its record, the book's concentration (BusinessDebtManager's pricing); its leverage on a scale to the default point, with the bank's watch line and ow... |
| 2879 | 119 | `List<Sector.Line> creditLines(Sector sector, SectorBooks.SectorMonth now)` | Every credit line the page had, as lines for the fact grid - its paragraphs as the notes under them (the grid's (i)s). |

### STATEMENT VIEWS (0.7.74): A SUMMARY AND A STATEMENT (lines 2999-3116)

| line | len | member | says |
|---:|---:|---|---|
| 3055 | 9 | `List<SectorStatements.Row> landNoteRows(List<SectorStatements.Row> rows)` | Note 11's rows for the screen: SectorStatements.landNote()'s, each labelled with its square kilometres and its price a square metre (LandClassView.landNoteLabel()); the value is the statement's own column. |
| 3080 | 5 | `void pickView(boolean statement)` | The switch picked: this page in the other view, at its top. |
| 3087 | 3 | `SectorStatements.Table incomeTable(Sector sector, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then)` | One business's income statement this month, its finance note from R1. |
| 3092 | 4 | `SectorStatements.Table sheetTable(Sector sector, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then)` | ...and its sheet, its debt by when it falls due from R2. |
| 3098 | 3 | `StatementView.Columns columns(String shareWord)` | A statement's columns as the toolbar left them; `shareWord` null for one with no common size. |
| 3103 | 7 | `HBox toolbar(SectorStatements.Table t, String table, StatementView.Columns c, java.util.Set<String> open, java.util.function.In...` | A statement's toolbar: its two columns, and its notes opened and closed together (none without notes). |
| 3112 | 1 | `static String period(SectorBooks.SectorMonth now)` | "For the month of January 2200". |
| 3115 | 1 | `static String asAt(SectorBooks.SectorMonth now)` | "As January 2200 closed". |

### Income (lines 3117-3173)

| line | len | member | says |
|---:|---:|---|---|
| 3120 | 16 | `void incomeStatementView(VBox page, Sector sector, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then)` | The statement of profit or loss, its notes, and its ratios beside it. |
| 3138 | 16 | `java.util.function.IntFunction<Node> incomeNotes(Sector sector, SectorStatements.Table t, boolean millions)` | What each note of a business's income statement opens on, by its number (null for a number it has none). |
| 3156 | 17 | `static String incomeInfo(String id, SectorBooks.SectorMonth now)` | An income statement row's (i), by id. |

### Balance sheet (lines 3174-3271)

| line | len | member | says |
|---:|---:|---|---|
| 3177 | 42 | `void balanceStatementView(VBox page, Sector sector, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then)` | The classified sheet with its ratios, then the statement of changes in equity. |
| 3229 | 22 | `static String equityInfo(String id, SectorBooks.SectorMonth now)` | An equity statement row's (i), by id: R6's four parts, the founders' shares and the rest. |
| 3253 | 18 | `static String sheetInfo(String id, SectorBooks.SectorMonth now)` | A sheet row's (i), by id. |

### Cash & debt (lines 3272-3485)

| line | len | member | says |
|---:|---:|---|---|
| 3275 | 15 | `void cashStatementView(VBox page, Sector sector, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then)` | The statement of cash flows, then what it owes by kind and by when it falls due. |
| 3311 | 47 | `VBox scheduleCard(Sector sector, boolean millions)` | ITS DEBT THIS MONTH, BY KIND (the Cash & debt statement; R7): the roll-forward from last month's sheet to this month's - at the start, borrowed, repaid, written off, at the end - with each kind's rate and when the las... |
| 3360 | 9 | `private static HBox scheduleRow(String kind, double[] v, double rate, double runsTo, boolean millions, boolean total)` | One kind's row of the schedule: its five figures, its rate and when the last of it falls due. |
| 3370 | 18 | `private static HBox scheduleRow(String[] words, String labelStyle, String figureStyle)` |  |
| 3390 | 21 | `static String cashInfo(String id, SectorBooks.SectorMonth now)` | A cash flow row's (i), by id. |
| 3422 | 33 | `VBox debtCard(Sector sector, boolean millions)` | ITS DEBT, BY KIND AND BY WHEN IT FALLS DUE (the Cash & debt statement): each kind it owes on (R2) with when it falls due and the month's interest on it (R1), its suppliers on a line at no interest, and the city debt's... |
| 3456 | 7 | `private static HBox debtRow(String kind, double[] v, boolean millions, boolean total)` |  |
| 3464 | 21 | `private static HBox debtRow(String[] words, String labelStyle, String figureStyle)` |  |

### Investors (lines 3486-3851)

| line | len | member | says |
|---:|---:|---|---|
| 3494 | 10 | `void investorStatementView(VBox page, Sector sector, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then, BuildCard.Secto...` | The investor report (spec 4.4): a share this month against last, who holds it and what it has raised and paid, how it is financed against the equity share it aims for, and since 0.7.75 every building at every gate (R4... |
| 3523 | 6 | `VBox everyGate(Sector sector)` | Every building it owns at every gate (R4): one row each, and what it means under it. |
| 3530 | 80 | `private VBox everyGateOf(List<BuildCard.Appraisal> all)` |  |
| 3612 | 11 | `Node gateMark(BuildCard.GateMark g)` | One gate's mark: a tick, a cross with why behind a tooltip, or a dash where it is not one this building has. |
| 3625 | 6 | `static String paybackWords(double months)` | The months its estimate would take to earn what one costs, in months under two years and years past them; "never" when it would not pay. |
| 3633 | 11 | `static String paysWords(BuildCard.Appraisal a)` | How one would be paid for, as shares of its cost: "40% shares · 12% its own · 48% borrowed", the parts that are not nothing. |
| 3646 | 13 | `static String gateShort(BuildCard.GateMark g)` | A failed gate in two or three words, for the "so" line's second refusal: an INTEREST gate's a borrowing ban where that is why (0.7.122). |
| 3669 | 29 | `String soWords(BuildCard.Appraisal a)` | What a building's gates mean (R4's "so"): it passes every one; ground is all that stops it, which the player can buy; it would not pay, or not carry its interest on its whole cost (0.7.122), which no gate opening woul... |
| 3700 | 8 | `static double[] shareFigures(SectorBooks.Shares s, SectorBooks.SectorMonth m)` | A share's figures, this month or last, as the report's rows: {shares, EPS, DPS, payout, book, price, fair, P/E, P/B, yield, market value}; NaN where it means nothing. |
| 3716 | 24 | `VBox shareReport(Sector sector, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then)` | A SHARE: the report's first block, this month against last; saved since FW6, so counted after a load too (a save from before it reads "not counted" until a month has run). |
| 3742 | 10 | `static String shareWords(double v, String kind)` | One of the report's figures in words, by its kind. |
| 3756 | 19 | `private static HBox shareRow(String label, String now, String then, String labelStyle, String figureStyle)` |  |
| 3777 | 25 | `VBox whoHoldsIt(int company)` | WHO HOLDS IT: the households, the world and the bank's desk, and what it has raised from them and paid them since founding. |
| 3804 | 15 | `VBox howFinanced(int company, SectorBooks.SectorMonth now)` | HOW IT IS FINANCED: what it owes and its owners' equity, each a share of what it owns, against the equity share it aims for. |
| 3826 | 25 | `VBox forShareholders(Sector sector, SectorBooks.SectorMonth now)` | FOR ITS SHAREHOLDERS (the Investors summary): the quote and fair value, earnings and the dividend a share, the yield, price to earnings and to book, and who holds it - the reads Equity and Exchange already keep, as th... |

### THE INVESTORS (0.7.30): WHY ISN'T IT BUILDING, AND WHAT WOULD LET IT? (lines 3852-4233)

| line | len | member | says |
|---:|---:|---|---|
| 3878 | 93 | `void investorPage(VBox page, Sector sector, SectorBooks.SectorMonth now, BuildCard.SectorInvestors si, boolean animate)` |  |
| 3973 | 78 | `List<String[]> rules(Sector sector, SectorBooks.SectorMonth now)` | The rules it builds by, as {chip, its old note or null}: the landlords' mortgage tests or everybody else's interest test, the horizon and trend, the population, the headroom, the order rule, and the builders' exception. |
| 4053 | 19 | `static String losingInfo(int lossMonths, boolean nothingToSell)` | The LOSING tile's (i): where it stands against the rule, in the rule's own number (B9's "six" was a literal). |
| 4078 | 13 | `VBox decisionCard(BuildCard.SectorInvestors si, boolean animate)` | Its decision as a card, 56 high: the kind's icon in a square, the kind in a word, and the word whole; Retail's bank branches a second line; a "Built" month flashing once with what it built. |
| 4093 | 101 | `VBox buildingsList(BuildCard.SectorInvestors si)` | Each building it can put up: its icon and name, how many stand and are on site, its value added, the investors' estimate, and its first gate. |
| 4208 | 25 | `private Node fitPill(BuildingsTemplate t)` | The player's Fit (R1b): one small pill on a building's row, "Fit <piece> - <cost>" at the verdict's cost, for the first piece known to the city (at its month, finds included: R5) with a building of the stack left to f... |

### ITS EQUIPMENT (R5, slice B2; research-design 2.2 and 5.5, mockup 4) (lines 4234-4434)

| line | len | member | says |
|---:|---:|---|---|
| 4260 | 1 | **type** `private record VerdictWords(String word, String line, String tone)` | How a piece's card says its verdict: the word, the line under it (null for none) and the colour of both. |
| 4267 | 11 | `private static VerdictWords verdictWords(EquipmentFit.Verdict v)` | A verdict in words: FITTED "all fitted"; PAYS "next: it pays" with what it earns back over its life against what it costs ("earns back $52k of $68k over 10 years"); NOT_YET "not worth it yet" with the same line, in th... |
| 4285 | 11 | `static String originWords(ResearchView.PieceRow row)` | Where a piece came from: "found here, May 1973 · 1.5 years before the world" for one the city's researchers found first, "from abroad, June 1974" for one the world had on history's date (a discovery, or a year later t... |
| 4298 | 10 | `VBox equipmentCards(List<ResearchView.PieceRow> rows)` | A card for each piece the rows hold, in the order the pieces first appear (a piece is the same on every kind of building that carries it). |
| 4310 | 18 | `private VBox equipmentCard(List<ResearchView.PieceRow> rows)` | One piece's card: its name, where it came from, what it does (a find's own words) and a row for each kind of building that carries it. |
| 4330 | 46 | `private GridPane equipmentRows(List<ResearchView.PieceRow> rows)` | A piece's rows, one for each kind of building: its name, how many carry the piece (and how many are being fitted), and the verdict. |
| 4378 | 11 | `static String gateIcon(BuildCard.GateKind k)` | A gate's icon: the deposit's pick, the licence's cap, the staffing test's person, the land, the money - and the bank for the interest on its whole cost (0.7.122). |
| 4391 | 12 | `static VBox tile(String name, Node figure, String line, String edge, String info)` | One of WHAT STOPS IT's tiles: its name, its figure, a line, a colour for its edge when it is the stop, and an (i). |
| 4405 | 10 | `static HBox rule(String text, String info)` | A rule as a chip, with its old note behind an (i). |
| 4417 | 8 | `static HBox controlDoor(String svg, String words, Runnable go)` | A door to a control the player holds: its icon, and where it goes. |
| 4427 | 7 | `void openPolicy(String area, String page)` | Policy, on one of its pages, at its top. |

### OPERATIONS (0.7.30): INPUTS → THE PLANT → OUTPUTS (lines 4435-4503)

| line | len | member | says |
|---:|---:|---|---|
| 4464 | 39 | `void operationsPage(VBox page, Sector sector, boolean animate)` |  |

### OPERATIONS · HOW ITS TONNES MOVE (FR4, 2026-10-20) (lines 4504-4797)

| line | len | member | says |
|---:|---:|---|---|
| 4565 | 13 | `List<Node> tonnesPanels(Sector sector)` | The strip's cards for a business: none for one with no freight. |
| 4580 | 5 | `void openFreightAt(FreightView.Division division)` | A division on Freight's page, from another business's: its books open on the division chosen. |
| 4587 | 10 | `Label worksDoor(String words, FreightView.Division division)` | A door to a division on Freight's page, in the accent: "Haulage ›". |
| 4599 | 18 | `VBox tonnesCard(FreightView.Tonnes t)` | HOW ITS TONNES MOVE: a line a good and way, the key, the month's bill by division, and what a siding saves. |
| 4619 | 46 | `HBox moveRow(Haulage.Move move)` | One good's way: its name and tonnes, then its legs in the order the tonnes travel. |
| 4667 | 9 | `VBox legCell(String caption, List<FreightView.Row> rows, double scale)` | A leg's bar under its caption, to a line's own tonnes, each mode a door to its division on Freight's page. |
| 4678 | 3 | `static FreightView.Row tonnesRow(FreightView.Leg leg, FreightView.Mode mode, double tonnes)` | One bar of a leg: a mode's tonnes, paid abroad where the mode is, and the division it opens. |
| 4683 | 19 | `javafx.scene.layout.FlowPane tonnesKey(FreightView.Tonnes t)` | The key under the lines: a swatch a mode it used, with its tonnes in the month, and the hatching. |
| 4704 | 8 | `HBox modeKey(FreightView.Mode mode, double tonnes)` | One item of a key: a mode's swatch (hatched where paid abroad), its name and its tonnes. |
| 4714 | 32 | `Node freightBill(FreightView.Tonnes t)` | THE MONTH'S FREIGHT BILL, by division: a bar of the three statement lines, a row each, and the sum; null with no bill. |
| 4748 | 22 | `HBox billRow(FreightView.Division division, double bill, String why)` | A division's row of the bill: its dot and door, what it was for, and the money; a dash where it billed nothing. |
| 4772 | 25 | `Node sidingLine(FreightView.Tonnes t)` | What a siding saves: what the one it has already carries, or - said to be a "would" - what one would; null where the trucks carry nothing. |

### ...and Iron & Steel's Pelletising Plant, before and after a siding (lines 4798-4835)

| line | len | member | says |
|---:|---:|---|---|
| 4801 | 16 | `VBox pelletCard(FreightView.Plant plant)` | THE PELLETISING PLANT'S CITY LEGS, at nameplate: all by truck, and with a siding, side by side. |
| 4819 | 16 | `VBox planHalf(String tag, FreightView.Plan plan, double tonnes)` | One way of moving the plant's tonnes: its tag, the trucks it takes, the tonnes by mode and what a month of haulage costs. |

### OPERATIONS · THE REFINERY (0.7.95) (lines 4836-5214)

| line | len | member | says |
|---:|---:|---|---|
| 4867 | 20 | `VBox refineryCard(RefineryView.View v)` | Refining's picture and strip, or a line saying why there is nothing to draw. |
| 4893 | 38 | **type** `static final class RefineryPicture extends javafx.scene.layout.Pane` | The picture: RefineryView.picture() at the pane's width, painted. |
| 4902 | 6 | `RefineryPicture()` _(in SectorScreen.RefineryPicture)_ |  |
| 4910 | 7 | `void set(RefineryView.View view, Sectors sectors)` _(in SectorScreen.RefineryPicture)_ | The month's view: painted again only where it moved (records compare by value) or the width does. |
| 4918 | 12 | `private void draw(double width)` _(in SectorScreen.RefineryPicture)_ |  |
| 4933 | 14 | `static List<Node> paint(RefineryView.Picture p, Sectors sectors)` | A picture's shapes as nodes, in its painting order: the ribbons, the boxes over them, the outlines, lines, icons and words. |
| 4954 | 17 | `static List<Node> shapes(List<RefineryView.Ribbon> ribbons, List<RefineryView.Box> boxes, List<RefineryView.Outline> outlines, ...` | A picture's ribbons, the boxes over them, its outlines and lines, as nodes in that order (the refinery's and, since 0.7.143, the materials industries'). |
| 4973 | 18 | `private static Node ribbon(RefineryView.Ribbon r)` | A ribbon: a path of two curves, filled in its colours (hatched where imported). |
| 4993 | 24 | `private static Node box(RefineryView.Box b)` | A box: a rectangle, with the words it says under the pointer where it has any, and out of the pointer's way where it has none. |
| 5019 | 8 | `private static Node outline(RefineryView.Outline o)` | An outline: an SVG path, filled and stroked as it says. |
| 5029 | 10 | `private static Node line(RefineryView.Line l)` | A line, in its colour. |
| 5041 | 21 | `static List<Node> text(RefineryView.Words w)` | A word and the run after it on its line, from its baseline, aligned on its x; over a halo of the ground if it asks. |
| 5064 | 9 | `private static javafx.scene.text.Text run(RefineryView.Words r)` | One run of words: its text, in its face and colour (a scope of its own: the halo's stroke is on a halo alone). |
| 5075 | 15 | `private static javafx.scene.text.Text halo(javafx.scene.text.Text t, double x, double y)` | A run's halo: the same words in the ground's colour, stroked round, under it. |
| 5092 | 9 | `static javafx.scene.text.Font font(RefineryView.Face f, double size)` | A picture's face at a size: Plex Sans at its three weights, Plex Mono at two. |
| 5103 | 19 | `static String iconOf(RefineryView.Mark m, Sectors sectors)` | What an icon of the picture's is drawn as. |
| 5124 | 10 | `static javafx.scene.paint.ImagePattern hatch(String colour, double opacity)` | The import's hatching in a colour (mockup 1's: a stripe at .85 every eight pixels on a ground at .14), at an opacity; one pattern a colour and opacity. |
| 5139 | 21 | `static VBox refineryStrip(RefineryView.View v)` | THIS MONTH, BY PRODUCT: its heading, and a cell a product in equal columns. |
| 5162 | 52 | `static VBox stripCell(RefineryView.Cell c)` | One product's cell: its swatch and name, its bar, and its five lines. |

### OPERATIONS · THE MATERIALS INDUSTRIES (0.7.143) (lines 5215-5434)

| line | len | member | says |
|---:|---:|---|---|
| 5244 | 23 | `VBox industryCard(IndustryView.View v)` | A materials industry's picture and strip, or a line saying why there is nothing to draw. |
| 5269 | 6 | `static Label monoCaption(String words)` | A picture's scale in words, in mono at the picture's size (the refinery's and the materials industries'). |
| 5281 | 38 | **type** `static final class IndustryPane extends javafx.scene.layout.Pane` | The picture: IndustryPicture.picture() at the pane's width, painted. |
| 5290 | 6 | `IndustryPane()` _(in SectorScreen.IndustryPane)_ |  |
| 5298 | 7 | `void set(Game game, IndustryView.View view)` _(in SectorScreen.IndustryPane)_ | The month's view: painted again only where it moved (records compare by value) or the width does. |
| 5306 | 12 | `private void draw(double width)` _(in SectorScreen.IndustryPane)_ |  |
| 5321 | 14 | `static List<Node> paintIndustry(IndustryPicture.Picture p, Sectors sectors)` | A materials picture's shapes as nodes, in its painting order: the ribbons, the boxes over them, the outlines, lines, icons and words. |
| 5337 | 23 | `static String iconOf(IndustryPicture.Mark m, Sectors sectors)` | What an icon of a materials picture's is drawn as: a field by its resource, the works by their roof. |
| 5362 | 21 | `static VBox industryStrip(IndustryView.View v)` | THIS MONTH, CELL BY CELL: its heading, and a cell a field, the forest, a product, the old metal or the power, in equal columns. |
| 5385 | 49 | `static VBox industryCell(IndustryPicture.Cell c)` | One cell: its swatch and name, its bar in its segments, and its lines. |

### OPERATIONS · THE OIL INDUSTRY (0.7.96) (lines 5435-5924)

| line | len | member | says |
|---:|---:|---|---|
| 5486 | 18 | `List<Node> oilPanels(OilView.View v)` | Oil's panels, in the page's order. |
| 5512 | 10 | `static VBox oilBox(Node...rows)` | A box of the page (mockup 2's .box): the panel's ground, an edge, its rows. |
| 5524 | 20 | `static HBox oilHead(String title, String line, String info, Node right)` | A box's heading: its title, its line, an (i), and something at its right. |
| 5546 | 9 | `static Label oilWords(String text, double size, String colour)` | Words at a size in a colour, wrapping. |
| 5557 | 3 | `static String oilWordsStyle(double size, String colour)` | The style oilWords() gives its label, whole: a caller that adds to it sets the sum, never reads the label's own. |
| 5562 | 8 | `static Label oilFigure(String text, double size, String colour)` | A figure at a size in a colour, the regular weight, never cut. |
| 5572 | 7 | `static void oilTip(Node n, String tip)` | A tooltip on a node. |
| 5581 | 9 | `static Region oilSwatch(String colour)` | A swatch: a product's or a series' colour in a small square. |
| 5592 | 29 | `static GridPane oilFigures(OilView.View v)` | The four figures across the page (mockup 2's .figs). |
| 5623 | 54 | `VBox oilWells(OilView.View v)` | THE WELLS: the two cards by kind, the chart with its legend, the investors' words. |
| 5679 | 11 | `static VBox oilCard(String svg, String name, String count)` | A wells card (mockup 2's .wc): its icon, its name, its count at the right. |
| 5692 | 11 | `static HBox oilFact(OilView.Fact f)` | A line of a wells card (mockup 2's .kv): its words, its figure at the right. |
| 5705 | 19 | `static VBox oilPlatform(OilView.PlatformLine p)` | A platform's lines (mockup 2's .plat): its colour, its name and its figure, and under them its words. |
| 5726 | 25 | `static GridPane oilTable(double[] widths, String[] heads, boolean[] right)` | A table's grid and its heads: a column a width (0: it takes the rest), each column's cells to the right where `right` says. |
| 5753 | 11 | `static HBox oilCell(Node n, boolean right)` | A cell: its node in the cell's padding, to the left or the right; a left cell's words wrap in the column's width. |
| 5766 | 10 | `static Region oilRule()` | A rule under a table's row. |
| 5778 | 10 | `static Label oilPill(String text, String colour)` | A pill (mockup 2's .pill): its words in its colour, an edge of it. |
| 5790 | 16 | `static VBox oilUnits(OilView.View v)` | THE REFINERY: its units as a table, and the refiners' word. |
| 5808 | 23 | `private static void unitRow(GridPane t, OilView.UnitLine u, int row)` | One unit's two grid rows, a rule and its cells, from grid row `row` of the table. |
| 5833 | 13 | `static VBox oilProducts(OilView.View v)` | PRODUCTS: crude and the nine, a row each. |
| 5848 | 17 | `private static void productRow(GridPane t, OilView.ProductLine p, int row)` | One product's two grid rows, a rule and its cells, from grid row `row` of the table. |
| 5867 | 43 | `VBox oilReserve(OilView.View v)` | THE STRATEGIC RESERVE: its lines, and its two levers on dial cards; or the words that none stands. |
| 5912 | 12 | `static java.util.function.DoubleFunction<List<Pieces.Effect>> effects(Game g, java.util.function.DoubleFunction<List<OilView.Ef...` | A lever's effects (OilView.Effect) as the dial card's rows: tonnes in the model's words, money in the city's mark. |

### OPERATIONS · FREIGHT (FR4, 2026-10-19) (lines 5925-6062)

| line | len | member | says |
|---:|---:|---|---|
| 5980 | 9 | `static String modeColour(FreightView.Mode mode)` | The colour a mode's bar is drawn in: the world's lorries slate, the railway steel, the ships blue, Haulage's trucks sand, the sidings a step darker than the railway. |
| 5991 | 8 | `static String divisionColour(FreightView.Division division)` | The dot a division's chip wears: the mode it is mostly about, and steel for All. |
| 6001 | 1 | `static String n0(double v)` | A whole number with its thousands grouped. |
| 6004 | 1 | `static String tShort(double t)` | Tonnes in the short form: "412k t". |
| 6007 | 11 | `List<Node> freightPanels(FreightView.View v)` | Freight's cards, in the page's order: the division strip, then the chosen division. |
| 6020 | 5 | `void openFreightDivision(FreightView.Division division)` | One of the divisions, from the top of the page. |
| 6027 | 23 | `HBox freightStrip()` | DIVISION, and its four chips: All, Haulage, Rail, Ports, each with the dot of its mode. |
| 6052 | 10 | `Label freightDoor(String words, FreightView.Division division)` | A door to a division in the accent: "Haulage ›". |

### ALL (lines 6063-6327)

| line | len | member | says |
|---:|---:|---|---|
| 6066 | 28 | `List<Node> freightAll(FreightView.View v)` | The legs card, then a card a division. |
| 6096 | 4 | `VBox divisionCard(String title, FreightView.Division division, List<Sector.Line> lines)` | A division's summary: its name, a door into it, and its few figures. |
| 6102 | 4 | `static double legTonnes(FreightView.Legs legs, FreightView.Mode mode)` | The tonnes the legs chart keeps for a mode. |
| 6108 | 4 | `static String quoteWords(FreightView.Quote q)` | A quote as the division states it: a price a tonne, or a share of what a lorry charges; "estimated" until a month has struck it. |
| 6114 | 4 | `static String say(FreightView.Quote q, double value)` | One figure of a quote in its unit. |
| 6120 | 40 | `VBox freightLegs(FreightView.Legs legs)` | THE CITY'S FREIGHT THIS MONTH, IN LEGS: a bar a leg, its modes in segments, and the key. |
| 6162 | 6 | `static javafx.scene.layout.StackPane modeSwatch(FreightView.Mode mode, boolean abroad)` | A mode's swatch: its colour in a small block, hatched over where the tonnes are paid abroad. |
| 6170 | 7 | `private static javafx.scene.shape.Rectangle swatchBlock(javafx.scene.paint.Paint paint)` | One 14 by 10 block of a swatch, in a paint. |
| 6182 | 16 | `HBox legRow(FreightView.Leg leg, double total, List<FreightView.Row> rows, double scale)` | A leg: its name and tonnes, and its bar. |
| 6200 | 5 | `static LegBar legBar(List<FreightView.Row> rows, double scale, java.util.function.Consumer<FreightView.Division> door)` | A leg's bar, kept: the one the last draw made, told its rows and its scale (Kept: a picture is told its figures by set()). |
| 6216 | 111 | **type** `static final class LegBar extends javafx.scene.layout.Pane` | A leg's bar: its modes' tonnes end to end to the scale given, each in its mode's colour, hatched where the tonnes are paid abroad, named when the name fits and always in a tooltip, and a door to its division where it ... |
| 6228 | 10 | `LegBar()` _(in SectorScreen.LegBar)_ |  |
| 6240 | 27 | `void set(List<FreightView.Row> rows, double scale, java.util.function.Consumer<FreightView.Division> door)` _(in SectorScreen.LegBar)_ | The month's rows, scale and door: the parts are kept while the rows are as many as they were. |
| 6269 | 25 | `private void build()` _(in SectorScreen.LegBar)_ | The parts made afresh, a block, its stripe and its label a row, over the track. |
| 6296 | 5 | `private String layoutKey()` _(in SectorScreen.LegBar)_ | Every figure and word the layout reads, in one string: laid out again only when it differs. |
| 6302 | 24 | `protected void layoutChildren()` _(in SectorScreen.LegBar)_ |  |

### HAULAGE (lines 6328-6468)

| line | len | member | says |
|---:|---:|---|---|
| 6331 | 34 | `List<Node> haulagePanels(FreightView.View v)` | Haulage: its fleet, its price, its shippers. |
| 6378 | 10 | `VBox fleetCard(String title, String info, String sub, double have, double need, double asked, String colour, String words, Stri...` | A fleet card: its words, a bar against what is needed, and the fact grid under. |
| 6395 | 13 | `VBox quoteCard(String title, String info, String sub, FreightView.Quote q, String floorIs, String targetIs)` | A division's price: its bar against the rule's floor and target where the model keeps them, and the three figures under it - FLOOR, TARGET, QUOTE - a dash for the one it does not keep, "estimated" until a month has st... |
| 6410 | 9 | `static VBox quoteTile(String name, String is, String figure)` | One of a quote's three figures: its name, the figure, what it is. |
| 6421 | 29 | `VBox shippersCard(FreightView.View v, java.util.function.ToDoubleFunction<FreightView.Shipper> part, double total)` | SHIPPERS BILLED: the sectors this division billed, largest first, each a door to that sector's operations, with the total. |
| 6452 | 16 | `private HBox shipperRow(FreightView.Shipper s, java.util.function.ToDoubleFunction<FreightView.Shipper> part, double widest)` | One shipper's row: its name (a door to its operations where it is a sector), its bar and its bill. |

### RAIL (lines 6469-6525)

| line | len | member | says |
|---:|---:|---|---|
| 6472 | 41 | `List<Node> railPanels(FreightView.View v)` | Rail: its wagon sets, its quote, its shippers. |

### PORTS (lines 6526-6598)

| line | len | member | says |
|---:|---:|---|---|
| 6529 | 21 | `List<Node> portsPanels(FreightView.View v)` | Ports: the berths by kind, and the shippers billed for handling. |
| 6562 | 3 | `static Object berthScope(FreightView.Berth b)` | The scope a berth kind's row is made in (KP2b): its kind, and another for a kind with no berth standing - its quote's second line says nothing, and a wrapped label that had words and is emptied keeps the wrapping widt... |
| 6567 | 31 | `HBox berthRow(FreightView.Berth b)` | One kind of berth: its name, the berths, the fill bar with its tonnes, and the handling quote. |

### THE SHELF (0.7.45) (lines 6599-6756)

| line | len | member | says |
|---:|---:|---|---|
| 6608 | 4 | **type** `record ShelfWords(boolean counted, double shelf, double floor, double cap, double clearing, boolean clearin...` | THE SHELF's words and figures, worked out without drawing them. |
| 6614 | 7 | `static String wayWords(double speed)` | A share of the way in words: "a sixth", "a quarter", "half". |
| 6622 | 60 | `ShelfWords shelfWords(Retail r)` |  |
| 6684 | 10 | `static String throttleCategory(SectorFlow.Plant plant)` | The Build category that relieves the thinnest of a plant's power, road and health (D13's rule, one plant), or null. |
| 6703 | 53 | `VBox shelfCard(Retail r)` | THE SHELF: what a basket costs, beside the baskets and what limited them. |

### WHAT IT CHARGES (0.7.45) (lines 6757-7221)

| line | len | member | says |
|---:|---:|---|---|
| 6763 | 1 | **type** `record ChargesWords(double floor, double ceiling, double charged, double target, String line, String served...` | WHAT IT CHARGES' words and figures, worked out without drawing them. |
| 6770 | 3 | `static String served(double served, double reach, double wanted)` | "Served nothing" when a month counted a reach or a want and served none of it; a reload counts none of the three until its month runs (they are the month's flows, not saved), and that is not a month that served nothing. |
| 6774 | 9 | `static ChargesWords chargesWords(Restaurants k)` |  |
| 6784 | 9 | `static ChargesWords chargesWords(LuxuryRetail l)` |  |
| 6795 | 15 | `VBox marginCard(ChargesWords w)` | WHAT IT CHARGES: the margin as a run from its floor to its ceiling, the target a dashed rule. |
| 6812 | 15 | `VBox everyLine(Sector sector)` | The old page, whole: Sector.operations() drawn as statement lines. |
| 6829 | 18 | `VBox flowCard(Sector sector, SectorFlow.Flow flow, boolean animate)` | The flow: what goes in, a chevron, the plant, a chevron, what comes out. |
| 6849 | 13 | `static Label chevron()` | A chevron between the flow's columns. |
| 6867 | 49 | `VBox inputsColumn(Sector sector, SectorFlow.Flow flow)` | WHAT GOES IN: each good bought - the city's part and the world's - and each service, by name. |
| 6918 | 9 | `static HBox rowHead(Node name, String info, String money)` | A row's head: its name (and its (i)), and its money at the right. |
| 6929 | 29 | `VBox inputRow(SectorFlow.In i, boolean counted)` | One good bought: its name, the city's part and the world's on a bar, the units and the share, the money; or a service, by name. |
| 6960 | 16 | `static String inputWords(SectorFlow.In i)` | A good bought, in words: "82,647 t · 7% from the city · on hand 3,478 vans". |
| 6978 | 4 | `static String units(double n, Good g)` | A count of a good in its unit, "under 1 van" for a fraction that would read "0 vans". |
| 6990 | 57 | `VBox plantCard(Sector sector, SectorFlow.Flow flow, boolean animate)` | THE PLANT: how many stand and are on site; a ring of the rate it runs at, sweeping up on a new month; the six throttles as a cascade, each its ratio on a bar and the running product after it, the thinnest amber; and i... |
| 7049 | 8 | `static HBox centred(Node n)` | A node centred in a row of its own. |
| 7059 | 26 | `static GridPane cascade(SectorFlow.Plant p)` | The throttle cascade: each of the six, its ratio on a bar, and the rate after it - the thinnest drawn amber. |
| 7087 | 17 | `VBox outputsColumn(SectorFlow.Flow flow)` | WHAT COMES OUT: each good made against what it could make - sold here, shipped, into stock, idled - its price and cost; and work billed. |
| 7106 | 41 | `private VBox outputRow(SectorFlow.Out o, SectorFlow.Flow flow)` | One good made: its name, price and money, its bar of where it went, and its words. |
| 7149 | 29 | `static String outputWords(SectorFlow.Out o, BuildCard.Note note)` | A good made, in words: "made 78,615 t of 203,400 · 32,018 here · 46,598 shipped"; a seller-priced one against the Build note's capacity. |
| 7180 | 9 | `static String coverWords(BuildCard.Note note)` | What a seller-priced good's capacity is, in the Build note's words. |
| 7191 | 6 | `static String railWords(BuildCard.Note note, boolean counted)` | The railway's work against its track, off Build's note (RAIL {the city's trade in tonnes, the network's capacity}); null for anyone else. |
| 7203 | 5 | `static HBox noteInLayers(String shown, String whole)` | A sector's note in two layers (0.7.21; Sector.Line.note(shown, whole)): the short line where statementNote() would put the paragraph, and the paragraph behind its (i). |
| 7210 | 11 | `static String toneColour(Sector.Line.Tone tone)` | The palette colour a sector's line asked for, or none. |

