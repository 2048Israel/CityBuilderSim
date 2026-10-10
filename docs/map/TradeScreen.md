# TradeScreen.java - 2,748 lines · 119 methods · 44 constants · interface

`ham/citybuildersim/ui/TradeScreen.java` - generated 2026-10-10 by CodeMap; line numbers are as of that run.

> The Trade & the world tab: how the city stands against the world this
> month - what it sells and buys abroad, the month's balance of payments,
> what a US dollar costs and which way it is going, and the vault - on an
> Overview and four pages behind it, a picture a page.
> 
> WHY THIS SHAPE (0.7.35). It was a 560 px statement column in a 1,270 px
> centre: a landing of four rows and three quiet gauges, then nine pages
> under four areas, each a ledger with a paragraph under every line (the
> river of the month the one picture). Jerus, on the screens not yet redone:
> "the others are still full of text and the design could be more intuitive
> and fun". Redrawn in Build's style (the project's spec-trade-0734.md): one
> strip of five pages - Overview, The month, What we trade, The currency,
> The reserves - the five figures across the top of every one, each a door;
> the Overview leads with what the city trades as mirrored bars a good,
> bought to the left and sold to the right, beside the rate on its own
> chart, and the three gauges that decide whether a shock is survivable;
> The month a waterfall from exports to the month's balance with the river
> one toggle away; What we trade every good, or every business, with the
> ten years and the record since founding; The currency the rate, its
> chart and the forces on it as diverging bars; The reserves whose the
> vault is, how long it would last, and the exchange. Every paragraph is
> behind an (i); every statement the pages printed is behind "details".
> 
> THE GOODS ARE THE BUSINESSES' OWN BOOKS (Game.getTradeByGood()): each
> sector's statement split by good, home and abroad, which foots to the
> balance of payments to the bit (ForeignCheck), the railway's fuel and the households' named;
> not the markets' tally, which does not (the spec's B13). Colours say the
> kind - exports the business violet, imports its darker step, income and
> capital the money blues - and the verdict colours stay on the gauges,
> THE CURRENCY and IN THE VAULT, the alerts and what was walked away from
> (the spec's D7); one parity rule (ForeignAccounts.PARITY_WATCH and
> PARITY_FAR) for this tab, the drawer's THE CURRENCY row and the
> header's rate line (D8).
> 
> BEFORE A MONTH IS COUNTED (ForeignAccounts.isMonthCounted()) - a city
> just founded, or loaded from a save before 0.7.46 - the tab says "not
> counted yet" until a month turns, rather than printing the flows' zeros
> as the month's figures. Since 0.7.46 the save carries the month's flows
> (the spec's D4, A2 in the model fixes), so a newer save loads counted.
> 
> The shell reads which page is open (tradePage) for the rail and the
> scroll memory, and FinancesScreen's doors set tradeArea, which names a
> page; the panel is rebuilt on the clock, so the page, the scroll position,
> the folds, the river's toggle, the goods' toggle and the exchange's ask
> all survive a redraw.

**Uses:** [Palette](Palette.md) (309), [ForeignAccounts](ForeignAccounts.md) (86), [Icons](Icons.md) (44), [Sectors](Sectors.md) (18), [TimeChart](TimeChart.md) (12), [CapitalFlows](CapitalFlows.md) (11), [Currency](Currency.md) (10), [Good](Good.md) (8), [ChartModel](ChartModel.md) (8), [HistorySave](HistorySave.md) (6), [HistoryScreen](HistoryScreen.md) (6), [Sector](Sector.md) (5), [UserInterface](UserInterface.md) (2), [SectorScreen](SectorScreen.md) (2), [BuildScreen](BuildScreen.md) (2), [GoodsMarket](GoodsMarket.md) (2), [Retail](Retail.md) (2), [YearBook](YearBook.md) (2), [BondMarket](BondMarket.md) (2), [OutwardInvestment](OutwardInvestment.md) (2), [HouseholdBalance](HouseholdBalance.md) (2), [PolicyScreen](PolicyScreen.md) (2), [Statement](Statement.md) (1), [DecisionLog](DecisionLog.md) (1), [WorldEconomy](WorldEconomy.md) (1), [DebtManager](DebtManager.md) (1), [Bank](Bank.md) (1), [Game](Game.md) (1)

**Used by (4):** [FinancesScreen](FinancesScreen.md), [PolicyScreen](PolicyScreen.md), [SummaryScreen](SummaryScreen.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 83 | TRADE & THE WORLD (0.7.35): THE FRAME |
| 176 | · one way to write each number |
| 244 | · the frame |
| 399 | · the five figures |
| 491 | · the action cards |
| 561 | · the screen's own pieces |
| 735 | THE OVERVIEW (0.7.35; the landing's four rows until then) |
| 768 | · what we trade, as bars |
| 1009 | · the currency, as a card |
| 1073 | · the three gauges |
| 1170 | THE MONTH (0.7.35; "The picture" and "The two accounts" until then) |
| 1373 | · the river |
| 1565 | · what is held where |
| 1636 | · the statement, under details |
| 1742 | WHAT WE TRADE (0.7.35; "In and out" and "Since founding" until then) |
| 1961 | THE CURRENCY (0.7.35; "The rate" and "What is moving it" until then) |
| 2078 | · what is moving it |
| 2286 | · what the rate is doing |
| 2336 | THE RESERVES (0.7.35; "What is yours", "Cover" and "Exchange" until then) |
| 2572 | · the exchange |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 98 | `TradeScreen.OVERVIEW` | `"Overview", MONTH = "The month", GOODS = "What we trade", CURRENCY = "The cur...` | The five pages, in the strip's order. |
| 102 | `TradeScreen.PAGES` | `{ OVERVIEW, MONTH, GOODS, CURRENCY, RESERVES }` | ...as the strip lists them. |
| 105 | `TradeScreen.PAGE_ICONS` | `{ Icons.OVERVIEW, Icons.COIN, Icons.LORRY, Icons.EXCHANGE, Icons.SAFE }` | ...and each one's icon: the tiles, the coin, the lorry, the two arrows, the safe. |
| 108 | `TradeScreen.TRADE_HOME` | `OVERVIEW` | The page the tab opens on, and the one the rail's trade icon resets to. |
| 123 | `TradeScreen.TRADE_MONEY_PAGES` | `{ "The rate", "What is moving it" }` | The currency's two pages until 0.7.35: a door that names the first lands on The currency. |
| 126 | `TradeScreen.TRADE_VAULT_PAGES` | `{ "What is yours", "Cover", "Exchange" }` | The reserves' three pages until 0.7.35: a door that names the first lands on The reserves. |
| 161 | `TradeScreen.FRAME_CHROME` | `200` | How much of the stage the fixed frame takes above the page's scroller - the head, the chips and the five figures - until the frame is laid out and its own height is read. |
| 164 | `TradeScreen.STAGE_REST` | `36` | What the menu spends around the frame and the page: its padding over and under them and the gap between, and the four pixels of slack its height is held to (UserInterface's rootMenu) - BankScreen's, for the same frame. |
| 167 | `TradeScreen.GROW_MILLIS` | `500` | How long the bars take to grow out of their axis when a month lands. |
| 170 | `TradeScreen.OVERVIEW_GOODS` | `8, PAGE_GOODS = 12` | The Overview shows this many goods; What we trade this many before "+N more" (the spec's D6). |
| 173 | `TradeScreen.EXCHANGE_CARD` | `"#exchange", HOLDINGS = "#holdings", FORCES = "#forces", DEBT = "#debt", LEAV...` | The scroll targets a door can land on. |
| 356 | `TradeScreen.LEAD_INFO` | `"What the city sells, what it buys, and what it owes in somebody else's money...` | The tab's (i): the landing's lead (the spec's L2), and what the Overview shows. |
| 361 | `TradeScreen.PAGE_INFO` | `{ "Every dollar that crossed the city's edge this month, and which way: a wal...` | Each page's (i), in PAGES' order after the Overview: what it holds (the landing's row blurbs until 0.7.35). |
| 405 | `TradeScreen.NOT_COUNTED` | `"not counted yet"` | What a figure of the month says until a month has turned since the city was loaded or founded (ForeignAccounts.isMonthCounted()). |
| 408 | `TradeScreen.NOT_COUNTED_NOTE` | `"since the city was founded, or loaded from a save before 0.7.46: a month on,...` | ...and its note. |
| 763 | `TradeScreen.GAUGES_INFO` | `"Always here, deliberately understated, and the same three every month - so a...` | The section's (i): the banner's reason (the spec's L8). |
| 923 | `TradeScreen.TRADE_INFO` | `"Every good the city's businesses sold abroad and bought abroad this month, f...` | WHAT WE TRADE's (i). |
| 931 | `TradeScreen.NOTHING_CROSSED` | `"Nothing crossed the city's edge this month.No exports, no imports, nothing "...` | The empty month's whole (the spec's M2). |
| 1001 | `TradeScreen.PUMP_WORDS` | `String.format("The grocers' filling stations buy the drivers' petrol at whole...` | Petrol's popover, under the pump price (0.7.83): where it comes from. |
| 1006 | `TradeScreen.FREIGHT_AFTER_LOAD` | `"Not counted yet: the railway's freight on each good is struck when the month...` | The prices before a month is counted - a city just founded, or loaded from a save before 0.7.46 (the spec's B14; a newer save carries the month's trade the freight is struck on, A1). |
| 1040 | `TradeScreen.RATE_INFO` | `"How many of the city's dollars one US dollar costs.Higher is a weaker curren...` | THE CURRENCY's (i): the unit, and parity (the spec's Q1, Q3). |
| 1094 | `TradeScreen.COVER_SCALE` | `ForeignAccounts.COMFORTABLE_COVER * 2` | The cover gauge's scale: twice the comfortable line, a year of imports. |
| 1121 | `TradeScreen.PARITY_SCALE` | `ForeignAccounts.PARITY_FAR * 1.5` | The parity gauge's scale either side: half as far again as PARITY_FAR, so the red band shows. |
| 1155 | `TradeScreen.COVER_INFO` | `String.format("Import cover is the oldest test there is: if every dollar of "...` | The gauges' (i)s: what each one measures (the cover sentence, the spec's C1; the others the landing's notes). |
| 1161 | `TradeScreen.BACKING_INFO` | `String.format("The vault against the foreign money parked in the city's bank,...` | ...the backing gauge's: the vault against the money that can leave, and where a run becomes likely. |
| 1165 | `TradeScreen.PARITY_INFO` | `String.format("How far the rate sits from parity - where a basket costs the s...` | ...and the parity gauge's: the one parity rule's two lines, and where else they are read. |
| 1197 | `TradeScreen.NOTHING` | `.0005` | Under half a dollar is nothing: a step that small is named, not drawn. |
| 1269 | `TradeScreen.CURRENT_INFO` | `"What the city earned from the world by selling it things, less what it spent...` | The current account's note (the spec's A1). |
| 1274 | `TradeScreen.FINANCIAL_INFO` | `"Borrowing abroad and foreign money parking here are both inflows, and neithe...` | The financial account's note (A2). |
| 1279 | `TradeScreen.INCOME_INFO` | `"Interest and dividends: what the businesses' and the households' paper abroa...` | The income line's note (B3). |
| 1284 | `TradeScreen.SURPLUS_INFO` | `"A surplus month: the world owes the city a little more than it did, and that...` | The month's closing words (the spec's D17). |
| 1288 | `TradeScreen.DEFICIT_INFO` | `"A deficit month has to be settled in somebody else's money: out of the vault...` | ...and a deficit month's. |
| 1352 | `TradeScreen.MONTH_INFO` | `"Every dollar that crossed the city's edge this month, and which way.Steps: e...` | The hero's (i). |
| 1358 | `TradeScreen.NOT_SAVED_INFO` | `"The month's flows across the edge - what was sold and bought abroad, the inc...` | Why a city reads nothing yet (the spec's B1): founded, or loaded from a save before 0.7.46, which did not carry the month's flows (D4, built as A2). |
| 1364 | `TradeScreen.HAND_INFO` | `"Below the line, and deliberately: an intervention does not earn or spend any...` | The treasury's hand (the spec's A6). |
| 1369 | `TradeScreen.VALUATION_INFO` | `"%s of foreign claims were written off this month.It improves what the city o...` | The valuation change (M5, A4; D15: a chip, not an alert). |
| 1505 | `TradeScreen.RIVER_INFO` | `"Band width is money.The two sides balance because they must: what came in an...` | The river's foot (the spec's section 4: one line, the rest in the (i), no colour named). |
| 1630 | `TradeScreen.HOLDINGS_INFO` | `"The stocks the flows add up to - the rough shape of an international investm...` | The holdings' (i) (the spec's A5). |
| 1862 | `TradeScreen.RECORD_INFO` | `"One month says whether a mill was staffed.The run says whether the city earn...` | The record's sentence (the spec's H1). |
| 1923 | `TradeScreen.WORLD_INFO` | `"Every world price is quoted in the world's money and converted at the rate.S...` | The world prices' note (the spec's G3). |
| 2207 | `TradeScreen.FORCES_INFO` | `"One reading: what the next month does to the rate, on the accounts as they s...` | The forces card's (i) (the spec's F1). |
| 2215 | `TradeScreen.COMES_TO_INFO` | `"The push is what the month is doing to the currency; the drift is the slide ...` | WHICH COMES TO's (i) (F3's two notes). |
| 2331 | `TradeScreen.VAULT_MOVED_INFO` | `"The vault is kept in dollars, so its dollar figure stays put and its local f...` | What the currency does to the vault (the spec's E2 note, both ways). |
| 2405 | `TradeScreen.ONE_POT_INFO` | `"The vault is one pot — the game does not tag a dollar as borrowed or earned,...` | WHOSE IT IS's (i) (the spec's R4, and B11: the method's name is out of it). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 79 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 111 | `String tradePage` | The page open: one of PAGES. |
| 120 | `String tradeArea` | A page asked for by name from outside, read once and then null: the nine pages lived under four areas until 0.7.35, and FinancesScreen's doors still set the area ("The currency", "The reserves") with its first page (T... |
| 129 | `final Set<String> openLines` | What the player has opened - a fold, the goods past the first twelve - by key, so a redraw on the clock leaves it open. |
| 132 | `boolean showRiver` | The month page draws the river rather than the steps (the spec's D2: the steps lead, the river one toggle away), kept while the game runs. |
| 135 | `boolean byBusiness` | What we trade draws a row a business rather than a row a good (D19), kept while the game runs. |
| 138 | `boolean tradeBuying` | The exchange: buying foreign money rather than selling it, and how much is asked, in local money. |
| 139 | `double tradeExchange` |  |
| 142 | `private String scrollTarget` | Where the page is to be scrolled to once it is drawn - a card's key - or null for its top. |
| 145 | `private Good popGood` | A good whose popover opens once What we trade is drawn - an Overview row's click - or null. |
| 148 | `private final Map<String, Node> targets` | The nodes a door on this tab can scroll to, by key, as the page draws them. |
| 151 | `private javafx.scene.control.ScrollPane body` | The page's scroller. |
| 154 | `private final ChartModel rateWindow` | The currency chart's window and the lines its legend has hidden, kept across the month's redraw so a chart dragged back stays where it was put. |
| 155 | `private final Set<String> rateHidden` |  |
| 158 | `private int drawnMonth` | The month the tab last drew: a new month landing is when its figures count up and its bars grow. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 76 | 2673 | **type** `final class TradeScreen` | The Trade & the world tab: how the city stands against the world this month - what it sells and buys abroad, the month's balance of payments, what a US dollar costs and which way it is going, and the vault - on an Ove... |
| 81 | 1 | `TradeScreen(UserInterface ui)` |  |

### TRADE & THE WORLD (0.7.35): THE FRAME (lines 83-175)

### one way to write each number (lines 176-243)

| line | len | member | says |
|---:|---:|---|---|
| 179 | 1 | `String sym()` | The city's money's mark: "D$". |
| 182 | 4 | `String d(double thousands)` | Local money with its mark and a true minus: "D$2.4M", "−D$725k" (FinancesScreen's, the spec's D9). |
| 188 | 4 | `String dFull(double thousands)` | ...with every digit. |
| 194 | 5 | `String signedD(double thousands)` | ...signed, as a movement: "+D$1.2M", "−D$3.5M", "D$0". |
| 201 | 1 | `String unitD(double thousands)` | A price a unit, with its mark: "D$29,559", "D$1.31". |
| 204 | 1 | `String perUsd(double rate)` | An exchange rate with its unit, the header's way: "D$0.7072 per US$" (the spec's B4). |
| 207 | 5 | `String oneOfOurs()` | One of ours in theirs: "D$1 = US$1.41", "D$1 = US¢0.27" for a currency worth under a cent of theirs. |
| 214 | 6 | `static String parityWords(ForeignAccounts fx)` | How far from parity, in words: "26.2% stronger than parity", "at parity", "held fixed". |
| 222 | 3 | `static String parityTone(int level)` | A parity level's colour: plain near, the watch amber, far red (ForeignAccounts.parityLevel()). |
| 227 | 3 | `static String coverTone(int level)` | A cover level's colour: red thin, amber under comfortable, plain over it (ForeignAccounts.coverLevel()). |
| 232 | 5 | `static String moveWords(double move)` | A move as a fraction, in words with its direction - a rate up is a weaker currency: "0.229% weaker", "0.142% stronger", "no move". |
| 239 | 4 | `static String pushWords(double pressure)` | A pressure as a share of the currency, with its direction: "15.5% weaker", "none". |

### the frame (lines 244-398)

| line | len | member | says |
|---:|---:|---|---|
| 252 | 47 | `void showForeignMenu()` | The tab's entry point: the Overview, or the page the player was on. |
| 301 | 3 | `void open(String page)` | One of the five pages, at its top: a chip picked, or a door. |
| 306 | 7 | `void open(String page, String target)` | ...scrolled to a card on it (the doors land where the figure is taken apart). |
| 315 | 1 | `void openOverview()` | The Overview, at its top. |
| 318 | 5 | `void openExchange(boolean buying)` | The exchange card, buying or selling - an ask the other way cleared: the cover alert's "Buy reserves". |
| 325 | 16 | `private void frameOver(VBox frame, VBox page)` | The fixed frame over a scrolling page, at the page's width, the page as tall as what is left under the frame as laid out (BankScreen's). |
| 343 | 11 | `void scrollTo(Node target)` | Scroll the page to a node on it (InfrastructureScreen's). |
| 376 | 4 | `static String pageInfo(String page)` | A page's (i) by its name. |
| 386 | 12 | `HBox head()` | The head: "Trade & the world" with the business violet's swatch, or the breadcrumb "Trade › The currency", "Trade" a way back to the Overview; at its right a door to City History on the page's own lines. |

### the five figures (lines 399-490)

| line | len | member | says |
|---:|---:|---|---|
| 402 | 1 | **type** `record Cell(String label, String value, String note, String tone, String where)` | One figure of the strip, worked out without drawing it: its label, its figure, its note, its colour, and where its click goes. |
| 418 | 32 | `List<Cell> kpiCells()` | THE FIVE (pure: the probe reads them as the strip shows them). |
| 452 | 23 | `HBox kpiStrip(boolean fresh)` | The strip: the five as limit cells, each a door; SOLD and BOUGHT count up from last month's when a month lands (the spec's section 5). |
| 477 | 6 | `private void countFrom(VBox cell, HistorySave h, String series, double now)` | A cell's figure counted up from the month before's, History's second-last point (SectorScreen.countUp()). |
| 485 | 5 | `String rateMove()` | The rate's move on last month, from History's last two points: "▲ 0.100% weaker this month"; null with fewer than two or no move to three places (the spec's section 5). |

### the action cards (lines 491-560)

| line | len | member | says |
|---:|---:|---|---|
| 500 | 43 | `List<Node> actionCards()` | What the player must act on or know of first, at the top of every page (the landing's four loud blocks, the spec's D18): the money leaving, under three months of cover, the hot money barely backed - red - and more owe... |
| 545 | 15 | `static VBox actionCard(String svg, String tone, String title, String line, String whole, Node act)` | One action card: its icon square, its title, one line with the whole behind an (i), and its door (null: none), on the raised ground with a 3 px top edge in `tone` (BankScreen's). |

### the screen's own pieces (lines 561-734)

| line | len | member | says |
|---:|---:|---|---|
| 564 | 7 | `static Label words(String text, double size, String tone)` | Words that wrap, at a size, in a colour. |
| 573 | 6 | `static Label figure(String text, double size, String tone)` | A figure that is never cut, at a size, in a colour. |
| 581 | 17 | `static HBox cardHead(String svg, String colour, String title, String info, Node right)` | A card's head: its icon in a square tinted in its colour, its title in capitals, an (i) when `info` is not null, and at its right `right` (null: nothing) - BankScreen's. |
| 600 | 7 | `static VBox card(Node...rows)` | A card on Build's ground: RAISED, radius 8, a 1 px edge, padding 12. |
| 609 | 4 | `static String cardStyle(String edge)` | The card's ground, with its edge in a colour. |
| 615 | 9 | `static VBox doorCard(VBox c, Runnable go)` | A card that opens somewhere: the accent edge under the pointer, and a click goes. |
| 626 | 9 | `static HBox cardLine(String label, String value, String tone)` | A line of a card: words at the left, wrapping, and a figure at the right. |
| 637 | 5 | `static HBox cardLine(String label, String value, String tone, String info)` | ...with an (i) after its words. |
| 644 | 3 | `static HBox noteLine(String shown, String whole, double wide)` | A muted line with its whole behind an (i), wrapping at `wide`. |
| 649 | 4 | `static Node line(String text, String whole)` | A plain line of words in a card, wrapping, with its whole behind an (i) when `whole` is not null. |
| 655 | 1 | `static Label muted(String text)` | ...muted. |
| 658 | 5 | `static Label quiet(String text)` | A heading's quiet words at its right, never cut (Pieces.hint() at its own width). |
| 665 | 6 | `static HBox chipInfo(String text, String colour, String whole)` | A chip, and after it an (i) holding `whole` when that is not null. |
| 673 | 5 | `static FlowPane chips(Node...cs)` | A row of chips, wrapping. |
| 680 | 6 | `static VBox column(Node...rows)` | A statement column, for a fold: the old rows at their old width. |
| 688 | 3 | `VBox fold(String key, String caption, java.util.function.Supplier<Node> inside)` | A fold kept on this screen: "details ▸ caption". |
| 693 | 12 | `static GridPane row(Node...cards)` | Cards side by side in equal columns, each as tall as the tallest. |
| 707 | 1 | `static HBox swatch(String colour, String name)` | A key's entry: a swatch and a name. |
| 710 | 5 | `static FlowPane keyRow(Node...entries)` | A row of a key: entries with a gap, wrapping. |
| 717 | 3 | `static Segment seg(double amount, String colour, String tip)` | A stretch of a segment bar with its tooltip. |
| 722 | 1 | `Sector sector(String key)` | A sector by its key, or null. |
| 725 | 4 | `void openSector(String key)` | One business's books, on its Operations page (Sectors › it). |
| 731 | 3 | `HBox sectorDoor(String words, String key)` | A door to one business: a pill in the business violet with its icon. |

### THE OVERVIEW (0.7.35; the landing's four rows until then) (lines 735-767)

| line | len | member | says |
|---:|---:|---|---|
| 747 | 14 | `void overviewPage(VBox page, boolean fresh)` |  |

### what we trade, as bars (lines 768-1008)

| line | len | member | says |
|---:|---:|---|---|
| 776 | 2 | **type** `record GoodRow(String icon, String name, double bought, double sold, double net, String tip, Good good, Str...` | One row of the goods or the businesses, worked out without drawing it (pure: the probe reads them): its icon, its name, what it bought and sold abroad, its net, its tooltip, and the good or the sector key it opens (on... |
| 789 | 18 | `List<GoodRow> goodRows(Sectors.TradeByGood t)` | Every good that crossed the edge this month, largest side first (the spec's D6: a good with nothing either way is not a row), then what was bought abroad with no good behind it, by the sector that bought it (pure). |
| 809 | 7 | `String goodTip(Sectors.GoodTrade g)` | A good's tooltip: what crossed each way, and who. |
| 818 | 10 | `String whoWords(Map<String, Double> who)` | "Automotive D$288.4M, Manufacturing D$37.0M" - by sector, largest first. |
| 830 | 3 | `static String who(String key)` | A buyer's or seller's name in words: a sector's key, "the households", and since 0.7.85 "the city's reserve" (Sectors.CITY: its crude). |
| 835 | 3 | `static boolean hasPage(String key)` | ...and whether it has a sector's page to open: not the households, not the city (0.7.85). |
| 845 | 19 | `List<GoodRow> businessRows(Sectors.TradeByGood t)` | A row a business (pure, the spec's D19): every sector that sold or bought abroad this month, off its statement's exports and imports, and the households' cars when they bought any - and their fuel (0.7.49), on the sam... |
| 866 | 21 | `MirrorRows mirror(List<GoodRow> rows, String leftHead, String rightHead, boolean nets, boolean toPage)` | The rows as mirrored bars, each a click: a good's popover, a business's books. |
| 893 | 28 | `VBox tradeCard(int shown, boolean fresh, boolean page)` | WHAT WE TRADE, as a card: the goods as mirrored bars, the first `shown` of them, the rest folded into one line; on the Overview a door to What we trade (a row there opens What we trade with its popover). |
| 941 | 51 | `Node goodPopover(Good good)` | A good's popover (the spec's section 3): who sold it and who bought it, by sector; what the world pays for one and charges to bring one in, the market's own prices (GoodsMarket.exportPrice(), importPrice() - the railw... |
| 994 | 5 | `static List<Map.Entry<String, Double>> sorted(Map<String, Double> who)` | Who, largest first. |

### the currency, as a card (lines 1009-1072)

| line | len | member | says |
|---:|---:|---|---|
| 1018 | 20 | `VBox currencyCard()` | THE CURRENCY, as a card: what a US dollar costs at 28 px with its unit, one of ours in theirs and parity, the distance from parity as a chip in its level, the rate's last ten years on a small chart with parity beside ... |
| 1054 | 18 | `TimeChart rateChart(HistorySave h, ChartModel window, boolean main, double width, double height)` | The rate and parity on a chart: History's fxRate in the money blue and fxParity (recorded since 0.7.35) in its light step. |

### the three gauges (lines 1073-1169)

| line | len | member | says |
|---:|---:|---|---|
| 1080 | 1 | **type** `record Gauge(String reading, String chip, String chipTone, double at, boolean muted)` | One gauge's words, worked out without drawing it (pure: the probe reads them): its reading, its chip and the chip's colour (null: none), where its mark sits (0 to 1), and whether the reading does not apply. |
| 1083 | 9 | `Gauge cover()` | Import cover on 0 to 12 months: red under THIN_COVER "crisis pricing", amber under COMFORTABLE_COVER "thin", green "comfortable" (pure). |
| 1097 | 10 | `Gauge backing()` | What backs the money that can leave, on 0 to 200%: red under PANIC_BACKING "a run likely", amber under 100% "thin", green "covered" (pure). |
| 1109 | 10 | `Gauge parity()` | How far the rate has run from parity, either side, on 0 to 1.5 times PARITY_FAR: "near" plain, "far" amber past PARITY_WATCH, "very far" red past PARITY_FAR (D8; pure). |
| 1123 | 10 | `VBox coverGauge()` |  |
| 1134 | 9 | `VBox backingGauge()` |  |
| 1144 | 9 | `VBox parityGauge()` |  |

### THE MONTH (0.7.35; "The picture" and "The two accounts" until then) (lines 1170-1372)

| line | len | member | says |
|---:|---:|---|---|
| 1184 | 8 | `void monthPage(VBox page)` |  |
| 1194 | 1 | **type** `record Walk(List<Step> steps, List<String> nothing)` | The walk worked out without drawing it (pure: the probe reads it): its steps, and the steps at nothing named in a line. |
| 1208 | 27 | `Walk monthWalk()` | THE MONTH'S STEPS (pure): sold abroad up, bought abroad down, the trade balance; the income from abroad, net - up in the money blue when the city received more than it paid, down in its darker step when it paid more (... |
| 1237 | 4 | `static void step(List<Step> steps, List<String> nothing, String name, double amount, String colour, String tip)` | A plain step, or its name in the line of nothing when it is under NOTHING. |
| 1243 | 3 | `static String incomeName(double received)` | The income line's name follows its sign (B3): received, or paid. |
| 1248 | 5 | `String signedFull(double thousands)` | "+D$1,234,567" or "−D$…", every digit. |
| 1255 | 12 | `String topGoods(Sectors.TradeByGood t, boolean sold)` | The two goods that moved most one way, for a tooltip and the river's note (the spec's B8: the words were static and untrue). |
| 1297 | 53 | `VBox monthHero()` | THE MONTH ACROSS THE EDGE: the two accounts as chips at the right, the toggle "steps · river", the picture, the month in one sentence, and the treasury's hand and the claims written off as chips when there were any. |

### the river (lines 1373-1564)

| line | len | member | says |
|---:|---:|---|---|
| 1376 | 1 | **type** `record Flow(String name, String note, double amount, String colour)` | One band of the river: its name, its note, its money and its colour. |
| 1379 | 1 | **type** `record River(List<Flow> in, List<Flow> out)` | The river's two sides, worked out without drawing it (pure: the probe reads them). |
| 1392 | 19 | `River river()` | THE RIVER'S BANDS (pure). |
| 1426 | 77 | `VBox bopRiver(List<Flow> in, List<Flow> out, double W, double H, double LABEL)` | The river itself (the Trade tab's since 0.6.x, at the page's width since 0.7.35). |
| 1510 | 13 | `Region riverBar(Flow f, double x, double y, double w, double h)` | One source or use bar, with its tooltip; the closing band outlined. |
| 1529 | 15 | `javafx.scene.shape.Path ribbon(double x0, double y0, double x1, double y1, double h, String colour)` | One ribbon: a filled cubic band of constant height between two columns. |
| 1546 | 18 | `VBox bandLabel(Flow f, double x, double y, double width, boolean rightAlign)` | A band's name and figure, beside its bar; the name wraps rather than end in "…". |

### what is held where (lines 1565-1635)

| line | len | member | says |
|---:|---:|---|---|
| 1575 | 43 | `VBox holdingsCard()` | WHAT THE CITY HOLDS ABROAD · WHAT THE WORLD HOLDS HERE: two bars on one scale - abroad, the businesses' paper, the households' and the vault; here, the city's shares in the world's hands, the businesses' bonds it hold... |
| 1620 | 8 | `static HBox keyLine(String colour, String name, String figures)` | A key's row: its swatch, its name, and its figures at the right. |

### the statement, under details (lines 1636-1741)

| line | len | member | says |
|---:|---:|---|---|
| 1647 | 94 | `VBox ledger()` | THE BALANCE OF PAYMENTS, as the statement it was (the old "The two accounts" page, verbatim but for these: the income line's label follows its sign, and the current account's note counts that line either way (B3); eve... |

### WHAT WE TRADE (0.7.35; "In and out" and "Since founding" until then) (lines 1742-1960)

| line | len | member | says |
|---:|---:|---|---|
| 1757 | 7 | `void goodsPage(VBox page, boolean fresh)` |  |
| 1766 | 45 | `VBox goodsHero(boolean fresh)` | The hero: the head with the toggle and its three figures, the bars, and the rest folded. |
| 1813 | 24 | `VBox tenYearsCard()` | THE LAST TEN YEARS: History's exports and imports abroad on one small chart, and the last twelve months in figures. |
| 1843 | 17 | `Walk recordWalk()` | SINCE FOUNDING (pure): sold, bought, the income from abroad - its name following its sign (B3: it read "paid abroad" for D$159.4B received) - capital, and the whole record, the model's balanceFromFlows(). |
| 1865 | 36 | `VBox sinceFoundingCard()` |  |
| 1910 | 11 | `VBox worldPrices()` | THE WORLD'S PRICES in one line (the spec's G3) and, under "details", every good the world trades with the city: what it pays for one and what it charges to bring one in - the market's own prices, freight in them (B5) ... |
| 1928 | 32 | `Node priceGrid()` | The grid under "details". |

### THE CURRENCY (0.7.35; "The rate" and "What is moving it" until then) (lines 1961-2077)

| line | len | member | says |
|---:|---:|---|---|
| 1978 | 36 | `void currencyPage(VBox page)` |  |
| 2022 | 36 | `VBox rateCard()` | THE RATE: what a US dollar costs at 28 px with its unit; one of ours in theirs; this month's move; parity with its line; the parity gauge either side of parity - its bands the one rule's (amber past PARITY_WATCH, red ... |
| 2060 | 17 | `static String parityLineInfo(ForeignAccounts fx)` | Where the rate sits against parity, and what that does (the spec's Q3 and the parity note). |

### what is moving it (lines 2078-2285)

| line | len | member | says |
|---:|---:|---|---|
| 2087 | 13 | `List<Force> pressureForces()` | THE PRESSURE (pure): the trade term - both accounts' trailing imbalance over everything that crossed (ForeignAccounts.pressure()) - and the real rate's term (ratePressure()), and the two together, the model's previewR... |
| 2110 | 17 | `List<Force> comesToForces()` | WHICH COMES TO (pure): the month's push - the pressure less what the vault meets, times the openness, at DRIFT_SPEED - the anchored drift (0.7.45; the UI spec's B6, D18: the month applies it, and the preview left it o... |
| 2129 | 22 | `String tradeTermInfo(ForeignAccounts fx)` | The trade term's sentences (the spec's F2: the current account's, the financial account's and the trade term's, verbatim). |
| 2158 | 8 | `String realRateInfo(ForeignAccounts fx)` | The real rate's sentence (F2), with the dial, the inflation people expect and the world's rate - ex ante since 0.7.42, as the model strikes it (Game.realRateDifferential()); it printed the year's inflation as the subt... |
| 2173 | 32 | `VBox forcesCard()` | WHAT IS MOVING IT, NEXT MONTH: a banner when the rate is pinned; two halves - THE PRESSURE on its own scale, the vault's defence and the openness under it; WHICH COMES TO on its own - and the old page's readings under... |
| 2222 | 9 | `String vaultWords(ForeignAccounts fx)` | The vault's defence in words: what it would sell, or nothing - or not counted yet (a city just founded, or loaded from a save before 0.7.46), when the month's deficit decides it (B1). |
| 2233 | 11 | `String vaultInfo(ForeignAccounts fx)` | ...and its sentence (F2). |
| 2246 | 4 | `static String opennessWords(ForeignAccounts fx)` | Openness, clamped at 100% by the model (B17, D20): "trade ≥ output (100%)" when it is. |
| 2252 | 22 | `Node forceReadings()` | The old page's readings, under "details": each reading and its sentence (the statement it was, B19's colours plain). |
| 2276 | 3 | `static void forceLine(VBox column, String label, String value)` | A reading on its statement line. |
| 2281 | 4 | `static String signedPct3(double v)` | A fraction as a signed per cent to three places, with a true minus: "−0.229%". |

### what the rate is doing (lines 2286-2335)

| line | len | member | says |
|---:|---:|---|---|
| 2289 | 27 | `VBox dollarDebtCard()` | THE DOLLAR DEBT (the spec's Q4): owed abroad in US$, what that is at this rate, and what the currency did to it - or one line when nothing is owed. |
| 2318 | 11 | `VBox vaultCard()` | THE VAULT (E2): its dollars, what they are worth at this rate, and what the currency did to them - a door to The reserves. |

### THE RESERVES (0.7.35; "What is yours", "Cover" and "Exchange" until then) (lines 2336-2571)

| line | len | member | says |
|---:|---:|---|---|
| 2350 | 12 | `void reservesPage(VBox page)` |  |
| 2371 | 32 | `VBox whoseCard()` | WHOSE IT IS: the claim bar - the dollar paper in the money blue's dark step, the parked money sand, the city's own in the money blue (B7: they were amber, red and green, verdicts on parts of one pot) - and a line a pa... |
| 2419 | 15 | `VBox claimBar(double gross, double debt, double parked, double own)` | One bar of the vault with the claims against it eaten out of the left (the Trade tab's since 0.7.0), at any width: drawn as one stock with bites taken out, because the question is what is left; when the claims run pas... |
| 2441 | 28 | `VBox coverCard()` | HOW LONG IT WOULD LAST: the cover gauge on 0 to 12 months with ticks at THIN_COVER and COMFORTABLE_COVER, its reading at 28 px, the vault against a month of imports, and what THIN_COVER months would need (D12) when it... |
| 2471 | 27 | `VBox leaveCard()` | THE MONEY THAT CAN LEAVE (the spec's C2): what is parked here, what pulls it, and what it has done before. |
| 2500 | 1 | **type** `record Moved(String words, String tone, String info)` | One of WHAT ELSE MOVED THE VAULT's chips, worked out without drawing it (pure): its words, its colour, its (i). |
| 2508 | 47 | `List<Moved> moved()` | WHAT ELSE MOVED THE VAULT (pure; the spec's E2-E5): the currency's move on its dollars, the central bank's defence - amber the month it sold, an event (section 5) - the land office's dollars out of it, and on a young ... |
| 2556 | 15 | `VBox movedCard()` |  |

### the exchange (lines 2572-2748)

| line | len | member | says |
|---:|---:|---|---|
| 2575 | 9 | `Press exchangePress()` | What a press on the exchange's button says (pure: the probe reads it): GO with the amount; CHOOSE with nothing asked yet - a press asks the smallest step and prices it, and never trades. |
| 2586 | 10 | `List<double[]> exchangeSteps(double ceiling)` | The exchange's "to N months" steps (pure, D12): buying and short, each cover line the cash can reach, as {months, local money}; the shares, all of it and clear are the card's own. |
| 2605 | 116 | `VBox exchangeCard()` | BUY OR SELL FOREIGN MONEY (the Exchange page until 0.7.35): the toggle, the first sentence with the rest behind the (i), what can be spent or sold, the ask and its steps, what it would do - before and after, the cover... |
| 2723 | 25 | `Node claimsGrid()` | The two claims, side by side (the spec's R5, under "details"), and what was walked away from (R6). |

