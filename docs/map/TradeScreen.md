# TradeScreen.java - 4,001 lines · 167 methods · 67 constants · interface

`ham/citybuildersim/ui/TradeScreen.java` - generated 2026-11-14 by CodeMap; line numbers are as of that run.

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

**Uses:** [Palette](Palette.md) (358), [Kept](Kept.md) (211), [ForeignAccounts](ForeignAccounts.md) (193), [Icons](Icons.md) (49), [TradeBand](TradeBand.md) (25), [Currency](Currency.md) (23), [Sectors](Sectors.md) (18), [TimeChart](TimeChart.md) (15), [CapitalFlows](CapitalFlows.md) (14), [SectorScreen](SectorScreen.md) (10), [Good](Good.md) (8), [ChartModel](ChartModel.md) (8), [Money](Money.md) (7), [HistorySave](HistorySave.md) (7), [BuildScreen](BuildScreen.md) (7), [HistoryScreen](HistoryScreen.md) (7), [Sector](Sector.md) (5), [FreightView](FreightView.md) (4), [PolicyScreen](PolicyScreen.md) (3), [EconomyManager](EconomyManager.md) (3), [UserInterface](UserInterface.md) (2), [PartnersPage](PartnersPage.md) (2), [VaultTable](VaultTable.md) (2), [Statement](Statement.md) (2), [GoodsMarket](GoodsMarket.md) (2), [Retail](Retail.md) (2), [RealDollar](RealDollar.md) (2), [YearBook](YearBook.md) (2), [BondMarket](BondMarket.md) (2), [OutwardInvestment](OutwardInvestment.md) (2)... and 9 more

**Used by (7):** [FinancesScreen](FinancesScreen.md), [KeptBench](KeptBench.md), [PartnersPage](PartnersPage.md), [PolicyScreen](PolicyScreen.md), [SummaryScreen](SummaryScreen.md), [UserInterface](UserInterface.md), [VaultTable](VaultTable.md)

## Sections

| line | section |
|---:|---|
| 100 | TRADE & THE WORLD (0.7.35): THE FRAME |
| 196 | · one way to write each number |
| 262 | · the frame |
| 474 | · the five figures |
| 579 | · the action cards |
| 650 | · the screen's own pieces |
| 849 | THE OVERVIEW (0.7.35; the landing's four rows until then) |
| 887 | · what we trade, as bars |
| 1133 | · the currency, as a card |
| 1282 | · the three gauges |
| 1380 | THE MONTH (0.7.35; "The picture" and "The two accounts" until then) |
| 1587 | · the river |
| 1791 | · what is held where |
| 1865 | · the statement, under details |
| 1971 | WHAT WE TRADE (0.7.35; "In and out" and "Since founding" until then) |
| 2207 | · THE BAND IN LEGS (FR4, 2026-10-20) |
| 2517 | THE CURRENCY (0.7.35; "The rate" and "What is moving it, next month" |
| 2740 | · what moved it |
| 3083 | · where the fundamentals hold it |
| 3268 | · what the rate is doing |
| 3325 | THE RESERVES (0.7.35; "What is yours", "Cover" and "Exchange" until then) |
| 3626 | · the exchange |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 115 | `TradeScreen.OVERVIEW` | `"Overview", MONTH = "The month", GOODS = "What we trade", PARTNERS = "Partner...` | The six pages, in the strip's order (Partners since WN6). |
| 119 | `TradeScreen.PAGES` | `{ OVERVIEW, MONTH, GOODS, PARTNERS, CURRENCY, RESERVES }` | ...as the strip lists them. |
| 122 | `TradeScreen.PAGE_ICONS` | `{ Icons.OVERVIEW, Icons.COIN, Icons.LORRY, Icons.FLAG, Icons.EXCHANGE, Icons....` | ...and each one's icon: the tiles, the coin, the lorry, the flag, the two arrows, the safe. |
| 125 | `TradeScreen.TRADE_HOME` | `OVERVIEW` | The page the tab opens on, and the one the rail's trade icon resets to. |
| 140 | `TradeScreen.TRADE_MONEY_PAGES` | `{ "The rate", "What is moving it" }` | The currency's two pages until 0.7.35: a door that names the first lands on The currency. |
| 143 | `TradeScreen.TRADE_VAULT_PAGES` | `{ "What is yours", "Cover", "Exchange" }` | The reserves' three pages until 0.7.35: a door that names the first lands on The reserves. |
| 181 | `TradeScreen.FRAME_CHROME` | `200` | How much of the stage the fixed frame takes above the page's scroller - the head, the chips and the five figures - until the frame is laid out and its own height is read. |
| 184 | `TradeScreen.STAGE_REST` | `36` | What the menu spends around the frame and the page: its padding over and under them and the gap between, and the four pixels of slack its height is held to (UserInterface's rootMenu) - BankScreen's, for the same frame. |
| 187 | `TradeScreen.GROW_MILLIS` | `500` | How long the bars take to grow out of their axis when a month lands. |
| 190 | `TradeScreen.OVERVIEW_GOODS` | `8, PAGE_GOODS = 12` | The Overview shows this many goods; What we trade this many before "+N more" (the spec's D6). |
| 193 | `TradeScreen.EXCHANGE_CARD` | `"#exchange", HOLDINGS = "#holdings", FORCES = "#forces", DEBT = "#debt", LEAV...` | The scroll targets a door can land on. |
| 411 | `TradeScreen.BOUND_TO_FRAME` | `"trade.frame", BOUND_TO_SCROLLER = "trade.scroller"` | The key in a kept scroller's properties for the frame its height is bound to, and in a kept page's for the scroller its width is bound to (frameOver()). |
| 427 | `TradeScreen.LEAD_INFO` | `"What the city sells, what it buys, and what it owes in somebody else's money...` | The tab's (i): the landing's lead (the spec's L2), and what the Overview shows. |
| 432 | `TradeScreen.PAGE_INFO` | `{ "Every dollar that crossed the city's edge this month, and which way: a wal...` | Each page's (i), in PAGES' order after the Overview: what it holds (the landing's row blurbs until 0.7.35). |
| 480 | `TradeScreen.NOT_COUNTED` | `"not counted yet"` | What a figure of the month says until a month has turned since the city was loaded or founded (ForeignAccounts.isMonthCounted()). |
| 483 | `TradeScreen.NOT_COUNTED_NOTE` | `"since the city was founded, or loaded from a save before 0.7.46: a month on,...` | ...and its note. |
| 882 | `TradeScreen.GAUGES_INFO` | `"Always here, deliberately understated, and the same three every month - so a...` | The section's (i): the banner's reason (the spec's L8). |
| 1046 | `TradeScreen.TRADE_INFO` | `"Every good the city's businesses sold abroad and bought abroad this month, f...` | WHAT WE TRADE's (i). |
| 1054 | `TradeScreen.NOTHING_CROSSED` | `"Nothing crossed the city's edge this month.No exports, no imports, nothing "...` | The empty month's whole (the spec's M2). |
| 1124 | `TradeScreen.PUMP_WORDS` | `String.format("The grocers' filling stations buy the drivers' petrol at whole...` | Petrol's popover, under the pump price (0.7.83): where it comes from - the fuel duty on the sign since 0.7.103. |
| 1130 | `TradeScreen.FREIGHT_AFTER_LOAD` | `"Not counted yet: the railway's freight on each good is struck when the month...` | The prices before a month is counted - a city just founded, or loaded from a save before 0.7.46 (the spec's B14; a newer save carries the month's trade the freight is struck on, A1). |
| 1196 | `TradeScreen.RATE_INFO` | `"How many of the city's dollars one US dollar costs.Higher is a weaker curren...` | THE CURRENCY's (i): the unit, and parity (the spec's Q1, Q3). |
| 1203 | `TradeScreen.REAL_USD_INFO` | `"The game has two US dollars.The one above is the in-game one, whose prices m...` | The real 2015 US$'s (i) (D29): the game has two US dollars, and this is the one the nations' output is counted in. |
| 1209 | `TradeScreen.STRUCK_INFO` | `String.format("What the city's traded goods are priced at: the dock's imports...` | THE RATE's line for the D$ the goods are struck at (AN2): what the traded goods are priced at, and how it follows the rate (AN1's PASS_THROUGH). |
| 1216 | `TradeScreen.REAL_EFFECTIVE_INFO` | `"The city's dollar against the moneys of the nations it trades with, each wei...` | The real effective rate's (i) (D29). |
| 1222 | `TradeScreen.WEIGHS_NAMED` | `4` | How many partners the line under the real effective rate names before it says how many more there are (WN5b). |
| 1303 | `TradeScreen.COVER_SCALE` | `ForeignAccounts.COMFORTABLE_COVER * 2` | The cover gauge's scale: twice the comfortable line, a year of imports. |
| 1330 | `TradeScreen.PARITY_SCALE` | `ForeignAccounts.PARITY_FAR * 1.5` | The parity gauge's scale either side: half as far again as PARITY_FAR, so the red band shows. |
| 1364 | `TradeScreen.COVER_INFO` | `String.format("Import cover is the oldest test there is: if every dollar of "...` | The gauges' (i)s: what each one measures (the cover sentence, the spec's C1; the others the landing's notes). |
| 1370 | `TradeScreen.BACKING_INFO` | `String.format("The vault against the foreign money parked in the city's bank,...` | ...the backing gauge's: the vault against the money that can leave, and where a run becomes likely. |
| 1374 | `TradeScreen.PARITY_INFO` | `String.format("How far the rate sits from parity - where a basket costs the s...` | ...and the parity gauge's: the one parity rule's two lines, and where else they are read. |
| 1408 | `TradeScreen.NOTHING` | `.0005` | Under half a dollar is nothing: a step that small is named, not drawn. |
| 1480 | `TradeScreen.CURRENT_INFO` | `"What the city earned from the world by selling it things, less what it spent...` | The current account's note (the spec's A1). |
| 1485 | `TradeScreen.FINANCIAL_INFO` | `"Borrowing abroad and foreign money parking here are both inflows, and neithe...` | The financial account's note (A2). |
| 1490 | `TradeScreen.INCOME_INFO` | `"Interest and dividends: what the businesses' and the households' paper abroa...` | The income line's note (B3). |
| 1495 | `TradeScreen.SURPLUS_INFO` | `"A surplus month: the world owes the city a little more than it did, and that...` | The month's closing words (the spec's D17). |
| 1499 | `TradeScreen.DEFICIT_INFO` | `"A deficit month has to be settled in somebody else's money: out of the vault...` | ...and a deficit month's. |
| 1566 | `TradeScreen.MONTH_INFO` | `"Every dollar that crossed the city's edge this month, and which way.Steps: e...` | The hero's (i). |
| 1572 | `TradeScreen.NOT_SAVED_INFO` | `"The month's flows across the edge - what was sold and bought abroad, the inc...` | Why a city reads nothing yet (the spec's B1): founded, or loaded from a save before 0.7.46, which did not carry the month's flows (D4, built as A2). |
| 1578 | `TradeScreen.HAND_INFO` | `"Below the line, and deliberately: an intervention does not earn or spend any...` | The treasury's hand (the spec's A6). |
| 1583 | `TradeScreen.VALUATION_INFO` | `"%s of foreign claims were written off this month.It improves what the city o...` | The valuation change (M5, A4; D15: a chip, not an alert). |
| 1726 | `TradeScreen.RIVER_INFO` | `"Band width is money.The two sides balance because they must: what came in an...` | The river's foot (the spec's section 4: one line, the rest in the (i), no colour named). |
| 1747 | `TradeScreen.DEFAULT_TIP_MILLIS` | `1000` | How long a tooltip waits before it shows when the code names no delay: JavaFX's own default, a second (what Tooltip.install gave the river's bars). |
| 1859 | `TradeScreen.HOLDINGS_INFO` | `"The stocks the flows add up to - the rough shape of an international investm...` | The holdings' (i) (the spec's A5). |
| 2101 | `TradeScreen.RECORD_INFO` | `"One month says whether a mill was staffed.The run says whether the city earn...` | The record's sentence (the spec's H1). |
| 2164 | `TradeScreen.WORLD_INFO` | `"Every world price is quoted in the world's money and converted at the rate.S...` | The world prices' note (the spec's G3). |
| 2225 | `TradeScreen.BAND_LEGS_INFO` | `"Between what the world pays for a good and what it charges to bring one in "...` | The card's (i): the legs, who each is paid to, and the two readings. |
| 2236 | `TradeScreen.NO_BAND` | `"No good the city trades has both a world buyer and a world seller yet, so th...` | The card's line while no good has a band: the world will not both buy and sell anything the city makes. |
| 2240 | `TradeScreen.NO_BAND_TRADE` | `"Nothing has crossed the edge in the month the books last struck, so there is...` | ...and while the books have no trade to weigh it by (a city just founded, or a month with nothing across the edge). |
| 2396 | `TradeScreen.BandBar.BAND` | `26` | The bar's height, in pixels: 26, the Freight page's LegBar's. |
| 2734 | `TradeScreen.FAIR_INFO` | `String.format("The fair level is where the fundamentals alone put the currenc...` | The fair-level gauge's (i): what the level is and why its gauge has no verdict. |
| 3010 | `TradeScreen.HOLD_INFO` | `"The part of the month's move the vault's hold made: under a peg the rate sta...` | THE HOLD's (i): what it is, why the six do not stand on their own under it, and what it is outside a band and a peg. |
| 3029 | `TradeScreen.FORCES_INFO` | `"These parts add to the month's move, in log points of the rate.Parity's tren...` | The forces card's (i): the parts and what each is, the world's among them, and under a band or a peg the hold. |
| 3039 | `TradeScreen.TREND_INFO` | `"The city's price level against the world's, on the basket with rent held to ...` | ...and each force's own (i): what it is, and the dials behind it. |
| 3042 | `TradeScreen.REAL_RATE_INFO` | `String.format("A real rate over the world's brings money here and holds the "...` |  |
| 3047 | `TradeScreen.BALANCE_INFO` | `String.format("A year of the current and the financial account together, agai...` |  |
| 3051 | `TradeScreen.COVER_FORCE_INFO` | `String.format("An empty vault costs the currency a %.0f%% discount, falling t...` |  |
| 3054 | `TradeScreen.INTERVENTION_INFO` | `String.format("Dollars the treasury buys cheapen the currency and dollars it ...` |  |
| 3061 | `TradeScreen.WORLD_FORCE_INFO` | `String.format("The dollar's own swing against every money, money running to s...` |  |
| 3072 | `TradeScreen.MARKET_INFO` | `String.format("The currency's own swing, drawn from the city's seed so the sa...` |  |
| 3077 | `TradeScreen.GATE_INFO` | `String.format("How much of the market's swing and of the fundamentals - the r...` | The gate's (i): what it lets through, and what it does not gate. |
| 3235 | `TradeScreen.FUNDAMENTALS_INFO` | `"The rate built up from parity, one step at a time, each row the rate after "...` | The steps card's (i): how to read the table. |
| 3243 | `TradeScreen.RULE` | `String.format("The rate is parity's trend, times where the fundamentals put t...` | THE RULE, folded at the page's foot: the board's paragraph in the model's words and dials. |
| 3320 | `TradeScreen.VAULT_MOVED_INFO` | `"The vault is kept in dollars, so its dollar figure stays put and its local f...` | What the currency does to the vault (the spec's E2 note, both ways). |
| 3401 | `TradeScreen.ONE_POT_INFO` | `"The vault is one pot — the game does not tag a dollar as borrowed or earned,...` | WHOSE IT IS's (i) (the spec's R4, and B11: the method's name is out of it). |
| 3710 | `TradeScreen.MONEY_CHIP` | `{ "US$", "Euro", "Yen", "Pound", "Yuan", "C$", "A$", "Franc", "Gold" }` | The names on the exchange's chips, and the mark each money's amounts carry (WN4), in ForeignAccounts' order: WorldMoneys.EIGHT's, then gold's (GD2, the ninth: its mark is ounces, which ownMoney writes after the figure). |
| 3711 | `TradeScreen.MONEY_MARK` | `{ "US$", "€", "¥", "£", "CN¥", "C$", "A$", "CHF ", "XAU " }` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 78 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 81 | `final PartnersPage partners` | The Partners page and the nation's behind it (WN6): its choices - the nation, the span, the good - survive the redraw in its fields. |
| 84 | `private final VaultTable vault` | The reserves in eight (WN6): the vault's table of the eight moneys and the years behind it. |
| 93 | `final Kept.Pool pool` | The nodes this screen made at its last draw, handed back to it at the next (KP6; study UP4, docs/studies/spec-screens-in-place.md). |
| 128 | `String tradePage` | The page open: one of PAGES. |
| 137 | `String tradeArea` | A page asked for by name from outside, read once and then null: the nine pages lived under four areas until 0.7.35, and FinancesScreen's doors still set the area ("The currency", "The reserves") with its first page (T... |
| 146 | `final Set<String> openLines` | What the player has opened - a fold, the goods past the first twelve - by key, so a redraw on the clock leaves it open. |
| 149 | `boolean showRiver` | The month page draws the river rather than the steps (the spec's D2: the steps lead, the river one toggle away), kept while the game runs. |
| 152 | `boolean byBusiness` | What we trade draws a row a business rather than a row a good (D19), kept while the game runs. |
| 155 | `boolean tradeBuying` | The exchange: buying foreign money rather than selling it, and how much is asked, in local money. |
| 156 | `double tradeExchange` |  |
| 159 | `String tradeMoney` | ...and in which money (WN4): a code of the nine (the eight and "XAU"), or null for the reserve mix when buying and the largest holding when selling. |
| 162 | `private String scrollTarget` | Where the page is to be scrolled to once it is drawn - a card's key - or null for its top. |
| 165 | `private Good popGood` | A good whose popover opens once What we trade is drawn - an Overview row's click - or null. |
| 168 | `private final Map<String, Node> targets` | The nodes a door on this tab can scroll to, by key, as the page draws them. |
| 171 | `private javafx.scene.control.ScrollPane body` | The page's scroller. |
| 174 | `private final ChartModel rateWindow` | The currency chart's window and the lines its legend has hidden, kept across the month's redraw so a chart dragged back stays where it was put. |
| 175 | `private final Set<String> rateHidden` |  |
| 178 | `private int drawnMonth` | The month the tab last drew: a new month landing is when its figures count up and its bars grow. |
| 2397 | `private List<TradeBand.Piece> pieces` |  |
| 2398 | `private double scale` |  |
| 2399 | `private boolean named` |  |
| 2401 | `private String shape, laidOut` | What the parts were made for at the last set() (see shapeOf()), and what the layout read (see layoutKey()): null before the first. |
| 2402 | `private final Region track` |  |
| 2403 | `private final List<javafx.scene.shape.Rectangle> blocks` |  |
| 2404 | `private final List<Label> labels` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 75 | 3927 | **type** `final class TradeScreen` | The Trade & the world tab: how the city stands against the world this month - what it sells and buys abroad, the month's balance of payments, what a US dollar costs and which way it is going, and the vault - on an Ove... |
| 86 | 5 | `TradeScreen(UserInterface ui)` |  |
| 96 | 3 | `void forget()` | Every node is made new at the next draw: another city opened (UserInterface.anotherCity()). |

### TRADE & THE WORLD (0.7.35): THE FRAME (lines 100-195)

### one way to write each number (lines 196-261)

| line | len | member | says |
|---:|---:|---|---|
| 199 | 1 | `String sym()` | The city's money's mark: "D$". |
| 202 | 1 | `String symFig(String number)` | A figure the city's money has, an already formatted number, with the qualified mark put where the city placed its sign: "D$0.7072", "0.7072 kr" (FM1). |
| 205 | 1 | `String qualified(String shown)` | A figure the city's money wrote ("$2.4M", "2.4M kr"), with the qualified mark in the sign's place: "D$2.4M", "2.4M kr" (FM1). |
| 208 | 4 | `String d(double thousands)` | Local money with its mark and a true minus: "D$2.4M", "−D$725k" (FinancesScreen's, the spec's D9). |
| 214 | 4 | `String dFull(double thousands)` | ...with every digit. |
| 220 | 5 | `String signedD(double thousands)` | ...signed, as a movement: "+D$1.2M", "−D$3.5M", "D$0". |
| 227 | 1 | `String unitD(double thousands)` | A price a unit, with its mark: "D$29,559", "D$1.31". |
| 230 | 1 | `String perUsd(double rate)` | An exchange rate with its unit, the header's way: "D$0.7072 per US$" (the spec's B4). |
| 233 | 5 | `String oneOfOurs()` | One of ours in theirs: "D$1 = US$1.41", "D$1 = US¢0.27" for a currency worth under a cent of theirs. |
| 240 | 4 | `static String parityWords(ForeignAccounts fx)` | How far from parity, in words: "26.2% stronger than parity", "at parity", "held fixed" - and "1.2M times parity" for a rate at its guard (farFrom()). |
| 246 | 3 | `static String parityTone(int level)` | A parity level's colour: plain near, the watch amber, far red (ForeignAccounts.parityLevel()). |
| 251 | 3 | `static String coverTone(int level)` | A cover level's colour: red thin, amber under comfortable, plain over it (ForeignAccounts.coverLevel()). |
| 256 | 5 | `static String moveWords(double move)` | A move as a fraction, in words with its direction - a rate up is a weaker currency: "0.229% weaker", "0.142% stronger", "no move". |

### the frame (lines 262-473)

| line | len | member | says |
|---:|---:|---|---|
| 270 | 22 | `void showForeignMenu()` | The tab's entry point: the Overview, or the page the player was on. |
| 300 | 41 | `void drawPage()` | The page itself, put in the window through setPage(): what keepMenu runs as a kept draw from the pool, and what KeptBench runs kept and built new for KeptCheck - the page `tradePage` names (a nation's, on Partners, wh... |
| 343 | 4 | `void open(String page)` | One of the six pages, at its top: a chip picked, or a door. |
| 349 | 4 | `void openNation(int nation)` | A nation's page on Partners (the map card's "Its trade with us", the Overview's partners strip, a row of the table), at its top. |
| 355 | 8 | `void open(String page, String target)` | ...scrolled to a card on it (the doors land where the figure is taken apart). |
| 369 | 3 | `private void toTop()` | The page on show scrolled to its top (KP6): a chip or a door picked draws its page from the top, and another page is a scope of its own with a scroller of its own, which opens there; but the page the pick names may be... |
| 374 | 1 | `void openOverview()` | The Overview, at its top. |
| 377 | 5 | `void openExchange(boolean buying)` | The exchange card, buying or selling - an ask the other way cleared: the cover alert's "Buy reserves". |
| 384 | 25 | `private void frameOver(VBox frame, VBox page)` | The fixed frame over a scrolling page, at the page's width, the page as tall as what is left under the frame as laid out (BankScreen's). |
| 414 | 11 | `void scrollTo(Node target)` | Scroll the page to a node on it (InfrastructureScreen's). |
| 451 | 4 | `static String pageInfo(String page)` | A page's (i) by its name. |
| 461 | 12 | `HBox head()` | The head: "Trade & the world" with the business violet's swatch, or the breadcrumb "Trade › The currency", "Trade" a way back to the Overview; at its right a door to City History on the page's own lines. |

### the five figures (lines 474-578)

| line | len | member | says |
|---:|---:|---|---|
| 477 | 1 | **type** `record Cell(String label, String value, String note, String tone, String where)` | One figure of the strip, worked out without drawing it: its label, its figure, its note, its colour, and where its click goes. |
| 493 | 34 | `List<Cell> kpiCells()` | THE FIVE (pure: the probe reads them as the strip shows them). |
| 529 | 25 | `HBox kpiStrip(boolean fresh)` | The strip: the five as limit cells, each a door; SOLD and BOUGHT count up from last month's when a month lands (the spec's section 5). |
| 560 | 11 | `private void countFrom(VBox cell, HistorySave h, String series, double now, boolean counts)` | A cell's figure counted up from the month before's, History's second-last point (SectorScreen.countUp()) when `counts`; else (KP6) the count the last draw gave the figure stopped, for a draw that does not count - a cl... |
| 573 | 5 | `String rateMove()` | The rate's move on last month, from History's last two points: "▲ 0.100% weaker this month"; null with fewer than two or no move to three places (the spec's section 5). |

### the action cards (lines 579-649)

| line | len | member | says |
|---:|---:|---|---|
| 588 | 44 | `List<Node> actionCards()` | What the player must act on or know of first, at the top of every page (the landing's four loud blocks, the spec's D18): the money leaving, under three months of cover, the hot money barely backed - red - and more owe... |
| 634 | 15 | `static VBox actionCard(String svg, String tone, String title, String line, String whole, Node act)` | One action card: its icon square, its title, one line with the whole behind an (i), and its door (null: none), on the raised ground with a 3 px top edge in `tone` (BankScreen's). |

### the screen's own pieces (lines 650-848)

| line | len | member | says |
|---:|---:|---|---|
| 653 | 3 | `static Label words(String text, double size, String tone)` | Words that wrap, at a size, in a colour. |
| 658 | 7 | `static Label wordsIn(String text, String style)` | Words that wrap, in a style of their own (KP2b: a total's, strong): made so, not made in the plain style and set again. |
| 667 | 7 | `static Label figure(String text, double size, String tone)` | A figure that is never cut, at a size, in a colour. |
| 676 | 17 | `static HBox cardHead(String svg, String colour, String title, String info, Node right)` | A card's head: its icon in a square tinted in its colour, its title in capitals, an (i) when `info` is not null, and at its right `right` (null: nothing) - BankScreen's. |
| 695 | 7 | `static VBox card(Node...rows)` | A card on Build's ground: RAISED, radius 8, a 1 px edge, padding 12. |
| 704 | 4 | `static String cardStyle(String edge)` | The card's ground, with its edge in a colour. |
| 714 | 12 | `static VBox doorCard(Runnable go, Node...rows)` | A card that opens somewhere: the accent edge under the pointer, and a click goes. |
| 728 | 3 | `static HBox cardLine(String label, String value, String tone)` | A line of a card: words at the left, wrapping, and a figure at the right. |
| 733 | 14 | `static HBox cardLine(String label, String value, String tone, String info)` | ...with an (i) after its words. |
| 749 | 3 | `static HBox noteLine(String shown, String whole, double wide)` | A muted line with its whole behind an (i), wrapping at `wide`. |
| 754 | 4 | `static Node line(String text, String whole)` | A plain line of words in a card, wrapping, with its whole behind an (i) when `whole` is not null. |
| 760 | 1 | `static Label muted(String text)` | ...muted. |
| 763 | 5 | `static Label quiet(String text)` | A heading's quiet words at its right, never cut (Pieces.hint() at its own width). |
| 770 | 9 | `static HBox chipInfo(String text, String colour, String whole)` | A chip, and after it an (i) holding `whole` when that is not null. |
| 781 | 5 | `static FlowPane chips(Node...cs)` | A row of chips, wrapping. |
| 788 | 6 | `static VBox column(Node...rows)` | A statement column, for a fold: the old rows at their old width. |
| 796 | 3 | `static Label statementNote(String text)` | A statement's note in a scope of its own (KP6): a note drawn only sometimes, or a table's rows in a number that changes, shifts no label after it. |
| 801 | 4 | `VBox fold(String key, String caption, java.util.function.Supplier<Node> inside)` | A fold kept on this screen: "details ▸ caption". |
| 807 | 12 | `static GridPane row(Node...cards)` | Cards side by side in equal columns, each as tall as the tallest. |
| 821 | 1 | `static HBox swatch(String colour, String name)` | A key's entry: a swatch and a name. |
| 824 | 5 | `static FlowPane keyRow(Node...entries)` | A row of a key: entries with a gap, wrapping. |
| 831 | 3 | `static Segment seg(double amount, String colour, String tip)` | A stretch of a segment bar with its tooltip. |
| 836 | 1 | `Sector sector(String key)` | A sector by its key, or null. |
| 839 | 4 | `void openSector(String key)` | One business's books, on its Operations page (Sectors › it). |
| 845 | 3 | `HBox sectorDoor(String words, String key)` | A door to one business: a pill in the business violet with its icon. |

### THE OVERVIEW (0.7.35; the landing's four rows until then) (lines 849-886)

| line | len | member | says |
|---:|---:|---|---|
| 861 | 19 | `void overviewPage(VBox page, boolean fresh)` |  |

### what we trade, as bars (lines 887-1132)

| line | len | member | says |
|---:|---:|---|---|
| 895 | 2 | **type** `record GoodRow(String icon, String name, double bought, double sold, double net, String tip, Good good, Str...` | One row of the goods or the businesses, worked out without drawing it (pure: the probe reads them): its icon, its name, what it bought and sold abroad, its net, its tooltip, and the good or the sector key it opens (on... |
| 908 | 18 | `List<GoodRow> goodRows(Sectors.TradeByGood t)` | Every good that crossed the edge this month, largest side first (the spec's D6: a good with nothing either way is not a row), then what was bought abroad with no good behind it, by the sector that bought it (pure). |
| 928 | 7 | `String goodTip(Sectors.GoodTrade g)` | A good's tooltip: what crossed each way, and who. |
| 937 | 10 | `String whoWords(Map<String, Double> who)` | "Automotive D$288.4M, Manufacturing D$37.0M" - by sector, largest first. |
| 949 | 3 | `static String who(String key)` | A buyer's or seller's name in words: a sector's key, "the households", and since 0.7.85 "the city's reserve" (Sectors.CITY: its crude). |
| 954 | 3 | `static boolean hasPage(String key)` | ...and whether it has a sector's page to open: not the households, not the city (0.7.85). |
| 964 | 19 | `List<GoodRow> businessRows(Sectors.TradeByGood t)` | A row a business (pure, the spec's D19): every sector that sold or bought abroad this month, off its statement's exports and imports, and the households' cars when they bought any - and their fuel (0.7.49), on the sam... |
| 985 | 21 | `MirrorRows mirror(List<GoodRow> rows, String leftHead, String rightHead, boolean nets, boolean toPage)` | The rows as mirrored bars, each a click: a good's popover, a business's books. |
| 1012 | 32 | `VBox tradeCard(int shown, boolean fresh, boolean page)` | WHAT WE TRADE, as a card: the goods as mirrored bars, the first `shown` of them, the rest folded into one line; on the Overview a door to What we trade (a row there opens What we trade with its popover). |
| 1064 | 51 | `Node goodPopover(Good good)` | A good's popover (the spec's section 3): who sold it and who bought it, by sector; what the world pays for one and charges to bring one in, the market's own prices (GoodsMarket.exportPrice(), importPrice() - the railw... |
| 1117 | 5 | `static List<Map.Entry<String, Double>> sorted(Map<String, Double> who)` | Who, largest first. |

### the currency, as a card (lines 1133-1281)

| line | len | member | says |
|---:|---:|---|---|
| 1142 | 21 | `VBox currencyCard()` | THE CURRENCY, as a card: what a US dollar costs at 28 px with its unit, one of ours in theirs and parity, the distance from parity as a chip in its level, the rate's last ten years on a small chart with parity beside ... |
| 1169 | 15 | `String regimeLine()` | How the D$ is held, in one muted line, when it is not Lean (XR2; pure): "Held at a peg of D$1.300", "In a band of D$1.261-D$1.339", "Floating freely". |
| 1186 | 8 | `void addRegimeLine(VBox card)` | The regime's line and its door to Policy › Money › The currency, on a card of the rate; nothing under Lean. |
| 1230 | 12 | `static String weighsWords(List<RealDollar.Weighed> weighed, Nations nations, boolean moneys)` | The line under the real effective rate (WN5b): "Weighs Germany 50% · Japan 25% · United States 25%", the WEIGHS_NAMED largest shares in whole percents, most weight first and a tie by nation number, then " · 12 more" f... |
| 1254 | 27 | `TimeChart rateChart(HistorySave h, ChartModel window, boolean main, double width, double height)` | The rate and parity on a chart: History's fxRate in the money blue and fxParity (recorded since 0.7.35; since XR1 the basket with rent held to a third) in its light step, and on the big one fxFair (recorded since XR1)... |

### the three gauges (lines 1282-1379)

| line | len | member | says |
|---:|---:|---|---|
| 1289 | 1 | **type** `record Gauge(String reading, String chip, String chipTone, double at, boolean muted)` | One gauge's words, worked out without drawing it (pure: the probe reads them): its reading, its chip and the chip's colour (null: none), where its mark sits (0 to 1), and whether the reading does not apply. |
| 1292 | 9 | `Gauge cover()` | Import cover on 0 to 12 months: red under THIN_COVER "crisis pricing", amber under COMFORTABLE_COVER "thin", green "comfortable" (pure). |
| 1306 | 10 | `Gauge backing()` | What backs the money that can leave, on 0 to 200%: red under PANIC_BACKING "a run likely", amber under 100% "thin", green "covered" (pure). |
| 1318 | 10 | `Gauge parity()` | How far the rate has run from parity, either side, on 0 to 1.5 times PARITY_FAR: "near" plain, "far" amber past PARITY_WATCH, "very far" red past PARITY_FAR (D8; pure). |
| 1332 | 10 | `VBox coverGauge()` |  |
| 1343 | 9 | `VBox backingGauge()` |  |
| 1353 | 9 | `VBox parityGauge()` |  |

### THE MONTH (0.7.35; "The picture" and "The two accounts" until then) (lines 1380-1586)

| line | len | member | says |
|---:|---:|---|---|
| 1394 | 9 | `void monthPage(VBox page)` |  |
| 1405 | 1 | **type** `record Walk(List<Step> steps, List<String> nothing)` | The walk worked out without drawing it (pure: the probe reads it): its steps, and the steps at nothing named in a line. |
| 1419 | 27 | `Walk monthWalk()` | THE MONTH'S STEPS (pure): sold abroad up, bought abroad down, the trade balance; the income from abroad, net - up in the money blue when the city received more than it paid, down in its darker step when it paid more (... |
| 1448 | 4 | `static void step(List<Step> steps, List<String> nothing, String name, double amount, String colour, String tip)` | A plain step, or its name in the line of nothing when it is under NOTHING. |
| 1454 | 3 | `static String incomeName(double received)` | The income line's name follows its sign (B3): received, or paid. |
| 1459 | 5 | `String signedFull(double thousands)` | "+D$1,234,567" or "−D$…", every digit. |
| 1466 | 12 | `String topGoods(Sectors.TradeByGood t, boolean sold)` | The two goods that moved most one way, for a tooltip and the river's note (the spec's B8: the words were static and untrue). |
| 1508 | 56 | `VBox monthHero()` | THE MONTH ACROSS THE EDGE: the two accounts as chips at the right, the toggle "steps · river", the picture, the month in one sentence, and the treasury's hand and the claims written off as chips when there were any. |

### the river (lines 1587-1790)

| line | len | member | says |
|---:|---:|---|---|
| 1590 | 1 | **type** `record Flow(String name, String note, double amount, String colour)` | One band of the river: its name, its note, its money and its colour. |
| 1593 | 1 | **type** `record River(List<Flow> in, List<Flow> out)` | The river's two sides, worked out without drawing it (pure: the probe reads them). |
| 1606 | 19 | `River river()` | THE RIVER'S BANDS (pure). |
| 1640 | 84 | `VBox bopRiver(List<Flow> in, List<Flow> out, double W, double H, double LABEL)` | The river itself (the Trade tab's since 0.6.x, at the page's width since 0.7.35). |
| 1731 | 14 | `Region riverBar(Flow f, double x, double y, double w, double h)` | One source or use bar, with its tooltip; the closing band outlined. |
| 1754 | 16 | `javafx.scene.shape.Path ribbon(double x0, double y0, double x1, double y1, double h, String colour)` | One ribbon: a filled cubic band of constant height between two columns. |
| 1772 | 18 | `VBox bandLabel(Flow f, double x, double y, double width, boolean rightAlign)` | A band's name and figure, beside its bar; the name wraps rather than end in "…". |

### what is held where (lines 1791-1864)

| line | len | member | says |
|---:|---:|---|---|
| 1801 | 43 | `VBox holdingsCard()` | WHAT THE CITY HOLDS ABROAD · WHAT THE WORLD HOLDS HERE: two bars on one scale - abroad, the businesses' paper, the households' and the vault; here, the city's shares in the world's hands, the businesses' bonds it hold... |
| 1846 | 11 | `static HBox keyLine(String colour, String name, String figures)` | A key's row: its swatch, its name, and its figures at the right. |

### the statement, under details (lines 1865-1970)

| line | len | member | says |
|---:|---:|---|---|
| 1876 | 94 | `VBox ledger()` | THE BALANCE OF PAYMENTS, as the statement it was (the old "The two accounts" page, verbatim but for these: the income line's label follows its sign, and the current account's note counts that line either way (B3); eve... |

### WHAT WE TRADE (0.7.35; "In and out" and "Since founding" until then) (lines 1971-2206)

| line | len | member | says |
|---:|---:|---|---|
| 1988 | 9 | `void goodsPage(VBox page, boolean fresh)` |  |
| 1999 | 50 | `VBox goodsHero(boolean fresh)` | The hero: the head with the toggle and its three figures, the bars, and the rest folded. |
| 2051 | 25 | `VBox tenYearsCard()` | THE LAST TEN YEARS: History's exports and imports abroad on one small chart, and the last twelve months in figures. |
| 2082 | 17 | `Walk recordWalk()` | SINCE FOUNDING (pure): sold, bought, the income from abroad - its name following its sign (B3: it read "paid abroad" for D$159.4B received) - capital, and the whole record, the model's balanceFromFlows(). |
| 2104 | 36 | `VBox sinceFoundingCard()` |  |
| 2149 | 13 | `VBox worldPrices()` | THE WORLD'S PRICES in one line (the spec's G3) and, under "details", every good the world trades with the city: what it pays for one and what it charges to bring one in - the market's own prices, freight in them (B5) ... |
| 2169 | 37 | `Node priceGrid()` | The grid under "details". |

### THE BAND IN LEGS (FR4, 2026-10-20) (lines 2207-2516)

| line | len | member | says |
|---:|---:|---|---|
| 2244 | 9 | `static String legColour(TradeBand.Part part)` | The colour of a leg's bar: the Freight page's mode colours where it has one, and a grey of its own for the world's margin. |
| 2255 | 25 | `VBox bandInLegsCard()` | THE BAND IN LEGS: the month's trade in the legs as one bar with its key, and under details each good's own gap. |
| 2282 | 5 | `static BandBar bandBar(List<TradeBand.Piece> pieces, List<String> tips, double scale, boolean named)` | A band's bar (KP6): a picture on a kept page is told its pieces by set(); anywhere else this is a new bar, as it was. |
| 2289 | 4 | `String wholeTip(TradeBand.Whole whole, TradeBand.Piece p)` | One leg's tooltip on the whole's bar: its name, its money, its share of the gap, and who is paid. |
| 2295 | 16 | `FlowPane bandKey(TradeBand.Whole whole)` | The bar's key: a swatch a leg - hatched where it is paid abroad - with its name, its money and its share of the band. |
| 2313 | 16 | `static javafx.scene.layout.StackPane legSwatch(TradeBand.Part part)` | A leg's key swatch: its colour, hatched over where the money is paid abroad (the Freight page's key). |
| 2331 | 51 | `Node bandGoods(TradeBand.Band band)` | Under details: a row a good - those that crossed first - its gap in legs as a share of what the world charges to bring one in, all on one scale. |
| 2394 | 122 | **type** `static final class BandBar extends javafx.scene.layout.Pane` | A band's bar: its pieces laid end to end to a scale (0 or less: their sum), each in its leg's colour and hatched where the money is paid abroad, each with a tooltip and, when asked, its name inside where it fits - the... |
| 2407 | 10 | `BandBar()` _(in TradeScreen.BandBar)_ | The track and the bar's own size, made once in its life; the pieces come with set(). |
| 2419 | 23 | `void set(List<TradeBand.Piece> pieces, List<String> tips, double scale, boolean named)` _(in TradeScreen.BandBar)_ | What the bar draws now: the pieces and each one's tooltip, the scale (0 or less: their sum), and whether the legs are named inside. |
| 2444 | 5 | `private String shapeOf()` _(in TradeScreen.BandBar)_ | What the parts depend on: whether the legs are named inside, and which leg each piece is. |
| 2451 | 5 | `private String layoutKey()` _(in TradeScreen.BandBar)_ | Every figure the layout reads, in one string: laid out again only when it differs. |
| 2458 | 30 | `private void build()` _(in TradeScreen.BandBar)_ | The parts made afresh, a block, a hatched stripe and (when named) a label a piece. |
| 2489 | 26 | `protected void layoutChildren()` _(in TradeScreen.BandBar)_ |  |

### THE CURRENCY (0.7.35; "The rate" and "What is moving it, next month" (lines 2517-2739)

| line | len | member | says |
|---:|---:|---|---|
| 2552 | 25 | `void currencyPage(VBox page)` |  |
| 2579 | 31 | `private VBox overTime(HistorySave h, ForeignAccounts fx)` | The rate over time, beside THE RATE: the big chart on the tab's own window, with the notes on what History recorded since which build. |
| 2620 | 59 | `VBox rateCard()` | THE RATE: what a US dollar costs at 28 px with its unit; one of ours in theirs; this month's move; the key to reading it (a rising figure is a cheaper currency); then two gauges of one scale either side of their centr... |
| 2681 | 4 | `static double gaugeAt(double deviation)` | Where a deviation sits on the two gauges, 0 to 1 across: the centre at nothing, PARITY_SCALE either side at the ends. |
| 2687 | 9 | `static HBox gaugeEnds()` | The two gauges' ends, named in the scale they share: "‹ 75% stronger" and "75% weaker ›". |
| 2698 | 7 | `static String compact(double v)` | A number past a thousand written short, so a currency at its guard fits a card: "1,234.5", "12,346", "1.2M", "1.0B". |
| 2707 | 5 | `static String farFrom(double deviation, String anchor)` | How far a rate sits from a level, in words: "8.3% weaker than the fair level", "at parity", and past a hundredfold "1.2M times parity" - the rate at its guard fits the card. |
| 2714 | 18 | `static String parityLineInfo(ForeignAccounts fx)` | The parity gauge's (i): which way the rate sits and what that does, then what parity is and what follows it (the spec's Q3). |

### what moved it (lines 2740-3082)

| line | len | member | says |
|---:|---:|---|---|
| 2752 | 18 | `VBox forcesCard()` | WHAT MOVED IT THIS MONTH (XR1; board 1): the month's move by force as the model struck it - parity's trend, the real rate, the balance, the vault's cover, interventions and the market (ForeignAccounts.getMoveTrend() a... |
| 2778 | 15 | `List<Force> moveForces()` | The seven forces and their sum as the bars' rows (pure): each its sentence under its name, the (i) behind what the force is. |
| 2795 | 3 | `boolean showsWorld(ForeignAccounts fx)` | Whether THE WORLD is a row: the city has the world's moneys, or the world moved the rate last month (a pure read of the foreign accounts and the game). |
| 2800 | 4 | `static boolean showsHold(ForeignAccounts fx)` | Whether THE HOLD is a row: the regime is a band, a peg or the gold, or the hold moved the rate last month (a peg that broke, a regime since left). |
| 2806 | 6 | `static String pointsOfRate(double move)` | A log move as percentage points of the rate, signed with a true minus: "+0.440%", "−0.120%", and whole past a hundred points so a currency at its guard fits the figure column. |
| 2814 | 7 | `static String gateWords(ForeignAccounts fx)` | How much of the market and the fundamentals the city's trade lets reach the rate, in words (pure). |
| 2823 | 5 | `static String heldWords(double log, String subject)` | What a log shift does to the rate, as a sentence's verb: "holds it about 2.4% dearer", "holds the D$ about 10.5% cheaper", "leaves it where it stands". |
| 2830 | 6 | `static String overUnder(double gap)` | A real rate gap in the sentence's words: "1.0 point over the world's real rate", "level with the world's real rate". |
| 2838 | 7 | `static String balancePhrase(ForeignAccounts fx)` | The overall balance in words: "a deficit of 0.2% of GDP", "a surplus of 1.4% of GDP", "a balance within 0.05% of GDP", or that no year of output is known yet. |
| 2847 | 5 | `static String coverPhrase(ForeignAccounts fx)` | The vault in the sentence's words: "an empty vault", "a vault holding 6.2 months of imports", "a vault with no import bill to cover". |
| 2854 | 10 | `String trendSentence()` | PARITY'S TREND (pure): which way the city's prices ran against the world's this month, and that the rate follows them one for one. |
| 2866 | 8 | `String realRateSentence()` | THE REAL RATE (pure): the dial, the inflation people expect, the gap to the world's real rate, where that holds the currency, and how fast. |
| 2876 | 6 | `String balanceSentence()` | THE BALANCE (pure): trade and capital over twelve months against a year of output, and where that holds the currency. |
| 2884 | 17 | `String coverSentence()` | THE VAULT'S COVER (pure): how many months of imports it holds, the market's discount for the shortfall from THIN_COVER, and whether it moved anything this month. |
| 2903 | 14 | `String interventionSentence()` | INTERVENTIONS (pure): what the treasury bought or sold this month, what the central bank sold defending the currency, and what is still felt - or that nothing was dealt. |
| 2922 | 12 | `String worldSentence()` | THE WORLD (WN4; pure): the dollar's own swing against every money, money running to safety, and the prices of what the city sells abroad, each as what it has the currency standing at now (gated by the city's trade, as... |
| 2936 | 9 | `String marketSentence()` | THE MARKET (pure): the currency's own swing, what is left of the month's move, where it stands and how fast it fades. |
| 2951 | 57 | `String holdSentence()` | THE HOLD (XR2; pure): what the vault did to the rate beside where the float's rule put it - a peg held, a band's edge held, or the peg letting go - in the month's own figures; or that nothing was held. |
| 3018 | 9 | `String totalSentence()` | THE MONTH'S MOVE (pure): where US$1 stood and where it stands, which the forces (and the hold, when it is a row) add up to. |

### where the fundamentals hold it (lines 3083-3267)

| line | len | member | says |
|---:|---:|---|---|
| 3086 | 1 | **type** `record FairStep(String name, String what, double after, boolean total)` | One step of the build-up: its name, what it does in words (null: the name spans), the rate after it, and whether it is a result (the fair level, the rate). |
| 3096 | 18 | `VBox fundamentalsCard()` | WHERE THE FUNDAMENTALS HOLD IT (XR1; board 1): US$1 in the city's money built up step by step - parity, the real rate, the vault's cover, the balance, parity's trend ahead of (or behind) the month's parity, which make... |
| 3123 | 40 | `List<FairStep> fairSteps()` | The steps from parity to the rate (pure): each computed from the model's getters, the fair level reached by them and set to getFairLevel() itself (they are equal but for rounding); the market's step is what the rate h... |
| 3165 | 5 | `static double fairBase(List<FairStep> steps)` | Where the table's bars start: a rate of 1, or the lowest step when a step runs under it. |
| 3172 | 47 | `Node fairTable(List<FairStep> steps, double base)` | The steps as a table: step, what it does, a thin bar from `base` on a log scale, and the rate after it. |
| 3221 | 12 | `String fadeSentence()` | How fast a market swing fades, and where it would be halfway back (pure): the rate halfway to the fair level in logs, by the half-life's month. |
| 3263 | 4 | `static String signedPct3(double v)` | A fraction as a signed per cent to three places, with a true minus: "−0.229%". |

### what the rate is doing (lines 3268-3324)

| line | len | member | says |
|---:|---:|---|---|
| 3271 | 30 | `VBox dollarDebtCard()` | THE DOLLAR DEBT (the spec's Q4): owed abroad in US$, what that is at this rate, and what the currency did to it - or one line when nothing is owed. |
| 3303 | 15 | `VBox vaultCard()` | THE VAULT (E2): its dollars, what they are worth at this rate, and what the currency did to them - a door to The reserves. |

### THE RESERVES (0.7.35; "What is yours", "Cover" and "Exchange" until then) (lines 3325-3625)

| line | len | member | says |
|---:|---:|---|---|
| 3339 | 19 | `void reservesPage(VBox page)` |  |
| 3367 | 32 | `VBox whoseCard()` | WHOSE IT IS: the claim bar - the dollar paper in the money blue's dark step, the parked money sand, the city's own in the money blue (B7: they were amber, red and green, verdicts on parts of one pot) - and a line a pa... |
| 3415 | 15 | `VBox claimBar(double gross, double debt, double parked, double own)` | One bar of the vault with the claims against it eaten out of the left (the Trade tab's since 0.7.0), at any width: drawn as one stock with bites taken out, because the question is what is left; when the claims run pas... |
| 3437 | 29 | `VBox coverCard()` | HOW LONG IT WOULD LAST: the cover gauge on 0 to 12 months with ticks at THIN_COVER and COMFORTABLE_COVER, its reading at 28 px, the vault against a month of imports, and what THIN_COVER months would need (D12) when it... |
| 3468 | 33 | `VBox leaveCard()` | THE MONEY THAT CAN LEAVE (the spec's C2): what is parked here, what pulls it, and what it has done before. |
| 3506 | 15 | `List<Node> riskOffLines(CapitalFlows hot)` | ...and what the risk-offs did to it (XR4; pure): when one has ever sent the hot money or the carry home, a line of the local money it took, the months counted, and this month's share of it where this month was one. |
| 3523 | 1 | **type** `record Moved(String words, String tone, String info)` | One of WHAT ELSE MOVED THE VAULT's chips, worked out without drawing it (pure): its words, its colour, its (i). |
| 3531 | 78 | `List<Moved> moved()` | WHAT ELSE MOVED THE VAULT (pure; the spec's E2-E5): the currency's move on its dollars, the central bank's defence - amber the month it sold, an event (section 5) - the land office's dollars out of it, and on a young ... |
| 3610 | 15 | `VBox movedCard()` |  |

### the exchange (lines 3626-4001)

| line | len | member | says |
|---:|---:|---|---|
| 3629 | 1 | **type** `record PastBound(String title, String line, String whole)` | The warning past the bound (XI2): its title, its line and what sits behind its (i). |
| 3638 | 50 | `PastBound pastBound()` | The warning for an ask that takes the month's dealing past INTERVENTION_LINEAR of a year's output, or null (pure: the model's figures only). |
| 3690 | 15 | `Press exchangePress()` | What a press on the exchange's button says (pure: the probe reads it): GO with the amount; CHOOSE with nothing asked yet - a press asks the smallest step and prices it, and never trades. |
| 3717 | 3 | `static String ownMoney(int k, double thousands)` | A balance of one of the nine in its own money, abbreviated and marked: "€3.4M". |
| 3722 | 3 | `static String goldPrice(double usdPerOunce, String unit)` | Gold's price this month, US$ an ounce (ForeignAccounts.getMoneyUsd(GOLD)), as the screens say it: "US$20.67 an ounce", or with the unit "oz", "US$20.67 an oz". |
| 3731 | 15 | `FlowPane moneyPicker(ForeignAccounts fx)` | THE MONEY PICKER (WN4, under the toggle): "By the mix" when buying and "The largest" when selling, then the eight that have a rate this month (just the US$ with no world file), and Gold last when it has a price (GD2: ... |
| 3753 | 42 | `VBox reserveMix(ForeignAccounts fx)` | THE RESERVE MIX (WN4, D20): where a purchase of the treasury's, the strong edge's or a peg's goes - All US$ (the default), Match our trade, The world's mix, or Choose - read from and written to ForeignAccounts; under ... |
| 3797 | 10 | `List<double[]> exchangeSteps(double ceiling)` | The exchange's "to N months" steps (pure, D12): buying and short, each cover line the cash can reach, as {months, local money}; the shares, all of it and clear are the card's own. |
| 3816 | 156 | `VBox exchangeCard()` | BUY OR SELL FOREIGN MONEY (the Exchange page until 0.7.35): the toggle, the first sentence with the rest behind the (i), what can be spent or sold, the ask and its steps, what it would do - before and after, the cover... |
| 3974 | 27 | `Node claimsGrid()` | The two claims, side by side (the spec's R5, under "details"), and what was walked away from (R6). |

