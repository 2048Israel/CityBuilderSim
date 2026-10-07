# FundScreen.java - 2,434 lines · 99 methods · 23 constants · interface

`ham/citybuildersim/ui/FundScreen.java` - generated 2026-10-05 by CodeMap; line numbers are as of that run.

> The city's fund on the Finances tab, as a brokerage (0.7.39): its Portfolio - what it is worth, what it has made and on what, every holding with its average cost and P&L - a Search of the shares and bonds it can buy, a page a security with the order ticket beside the fund's position in it, its Activity, and its Rules & cash.
> 
> WHY. Jerus, the morning after 0.7.38: "when you click buy manually i want
> it to be like wealthsimple trade type kinda like a brokerage, where you can
> search the shares and bonds and see and all, and also the city fund should
> show pnl and acb and all that". The fund was two pages in FinancesScreen:
> Holdings, which said what it held at its marks and nothing of what it had
> cost, and By hand, a company chip, a bond chip, an amount and a Buy at fair
> value - which, in both of the research cities, filled nothing, because
> nobody was asking at or under fair (the project's spec-fund-0739.md, 1.4).
> 
> WHAT IT IS. Four pages under Finances' frame - PAGES - and a page a
> security, reached from any row ("Finances › The city's fund › Automotive"),
> with a way back to the page it was opened from ("‹ Portfolio"). Every
> figure is FundView's, the model's pure door
> to the fund (and so FundLedger's cost basis): nothing here adds a column
> up. The words each page composes are worked out without a node, by the
> static methods under WORDS, so a probe reads every one of them on a played
> city. The cards that were the Holdings page - the dial, the transfer to
> the treasury, the rescue book, WHAT IT HOLDS and the rule's aim - moved
> here whole (Rules & cash, Portfolio; the withdrawal's dial joined Rules &
> cash in 0.7.48); By hand's amount, chips and buttons are the
> ticket, Search and a security's page (the spec's 4.8, nothing lost).
> 
> COLOURS. P&L is a verdict - did this purchase make or lose the city money -
> so it is green and red (Pieces.pnl(), the spec's D7), always with a sign
> and an arrow; nothing else here is: a price, its move, a yield and every
> chart line are in ink or their area's colour.

**Uses:** [Palette](Palette.md) (229), [FundView](FundView.md) (100), [Game](Game.md) (61), [FundLedger](FundLedger.md) (61), [TreasuryFund](TreasuryFund.md) (58), [Icons](Icons.md) (19), [Equity](Equity.md) (18), [TimeChart](TimeChart.md) (17), [ChartModel](ChartModel.md) (16), [OrderBook](OrderBook.md) (11), [FinancesScreen](FinancesScreen.md) (9), [CityCalendar](CityCalendar.md) (8), [CorporateBond](CorporateBond.md) (7), [HistoryScreen](HistoryScreen.md) (7), [Pieces](Pieces.md) (7), [HistorySave](HistorySave.md) (5), [Bank](Bank.md) (4), [PolicyPreview](PolicyPreview.md) (4), [UserInterface](UserInterface.md) (2), [DecisionLog](DecisionLog.md) (2), [Levers](Levers.md) (2), [Exchange](Exchange.md) (1), [BondMarket](BondMarket.md) (1), [SectorScreen](SectorScreen.md) (1), [BuildScreen](BuildScreen.md) (1), [Ladder](Ladder.md) (1)

**Used by (1):** [FinancesScreen](FinancesScreen.md)

## Sections

| line | section |
|---:|---|
| 85 | THE PAGES |
| 119 | · what is kept across the clock's redraws: the page, the box, the ticket |
| 239 | WORDS - every figure and sentence the pages compose, worked out |
| 327 | · the hero |
| 400 | · the holdings |
| 500 | · search |
| 545 | · a security |
| 692 | · the ticket |
| 826 | · the record |
| 953 | PORTFOLIO - what is it worth, has it made money, and on what? |
| 981 | · · the hero: its worth, its return, since it began; the chart |
| 1007 | · · what it holds, and the rule's aim (the old Holdings page's, whole) |
| 1010 | · · every holding |
| 1013 | · · since it began, by kind; closed |
| 1016 | · · the doors |
| 1394 | SEARCH - what can the fund buy, and what is it doing? |
| 1519 | A SECURITY - what is it, what has it done, and what do we have in it? |
| 1528 | · · the left: its head, its chart, its facts, its book, its record |
| 1562 | · · the right: your position, and the ticket |
| 1796 | · the ticket |
| 1828 | · · buy or sell; by amount or by quantity |
| 1847 | · · the figure and its steps |
| 1887 | · · the price |
| 1890 | · · the quote |
| 1896 | · · the action |
| 1909 | · · this security's orders: waiting, and on the book |
| 2051 | ACTIVITY - what has the fund done, and what was done to it? |
| 2134 | RULES & CASH - how does it run, and how do I move money? |
| 2197 | · THE WITHDRAWAL (0.7.48, C2) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 90 | `FundScreen.AREA` | `"The city's fund"` | The area's name on Finances. |
| 93 | `FundScreen.PAGES` | `{ "Portfolio", "Search", "Activity", "Rules & cash" }` | The fund's four pages (0.7.39; "Holdings" and "By hand" until then). |
| 95 | `FundScreen.ICONS` | `{ Icons.SAFE, Icons.EXCHANGE, Icons.REPORTS, Icons.SETTINGS }` | ...and their icons on the chips. |
| 105 | `FundScreen.ACTIVITY_MONTHS` | `60` | The months of the record Activity shows before "show older", and how many more each press adds. |
| 108 | `FundScreen.SECURITY_ROWS` | `12` | The rows of a security's own record under its book. |
| 111 | `FundScreen.CLOSED_ROWS` | `8` | The closed lots the Portfolio's CLOSED card lists before its fold. |
| 114 | `FundScreen.MONEY_STEPS` | `{ 1_000, 10_000, 100_000, 1_000_000 }` | The money steps: the ticket's on a buy by amount and on a bond's face, and PAY IN, DRAW OUT's; a quantity of shares steps by 1, 10, 100 and 1,000 instead. |
| 117 | `FundScreen.LEFT` | `812, RIGHT = 412` | The security page's two columns at the 1,389 window: the picture and its facts, then the position and the ticket. |
| 157 | `FundScreen.FAIR` | `"fair", BID = "bid", ASK = "ask", LAST = "last", OWN = "own"` | The ticket's prices. |
| 414 | `FundScreen.SHARES` | `"COMPANY SHARES", RESCUE = "THE RESCUE BOOK", BONDS = "BONDS"` | The holdings' groups, in the page's order. |
| 968 | `FundScreen.HERO_INFO` | `"Everything the fund holds at the marks every other holder uses, and its cash...` | THE CITY'S FUND's (i). |
| 1081 | `FundScreen.COL_NAME` | `250, COL_UNITS = 120, COL_PRICE = 110, COL_VALUE = 110, COL_AVG = 110, COL_PN...` | HOLDINGS' columns: the name, units, price, worth, average cost, unrealized, and the share of the fund's bar. |
| 1084 | `FundScreen.HOLDINGS_INFO` | `"Every lot the fund holds, at its mark, with what it cost by the average-cost...` | HOLDINGS' (i). |
| 1277 | `FundScreen.POP_MILLIS` | `220` | How long a payment's pop takes each way. |
| 1304 | `FundScreen.KINDS_INFO` | `"Each kind of holding since the fund began: what the city put into it(bought ...` | SINCE IT BEGAN, BY KIND's (i). |
| 1399 | `FundScreen.FILTERS` | `{ "All", "Shares", "Bonds", "Held" }` | Search's chips. |
| 1402 | `FundScreen.SEARCH_INFO` | `"Every company listed on the exchange, every business's bond still outstandin...` | Search's (i): the spec's D1. |
| 1810 | `FundScreen.TICKET_INFO` | `"An order goes on the book at the next month's step, after every other partic...` | THE ORDER TICKET's (i). |
| 2056 | `FundScreen.GROUPS` | `{ "All", FundView.TRADES, FundView.INCOME, FundView.MONEY, FundView.EVENTS }` | Activity's chips: every row, or one group of them (FundView.groupOf()). |
| 2200 | `FundScreen.WITHDRAWAL_LADDER` | `520` | The withdrawal's ladder: the card runs the page's width, the dial at the left and what it would do beside it. |
| 2217 | `FundScreen.WITHDRAWAL_CAVEAT` | `"next month's on the fund as it stands - its prices and what the book takes w...` | The withdrawal's caveat, under what it would do. |
| 2219 | `FundScreen.WITHDRAWAL_CAVEAT_INFO` | `"Next month's withdrawal is struck on what the fund is worth at the top of " ...` | ...and the sentence behind it, its (i). |
| 2324 | `FundScreen.RESCUE_BOOK_INFO` | `"What the city holds from rescuing its bank: the shares it took when it " + "...` | The rescue book's (i). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 77 | `private final UserInterface ui` | The window this screen draws into, and the tab it is drawn on. |
| 78 | `private final FinancesScreen fin` |  |
| 122 | `String security` | The security open, by its lot key ("S:Automotive", "B:142"), or null; and the page it was opened from. |
| 123 | `String cameFrom` |  |
| 126 | `String query` | The search box's words and its chip; Activity's chip and how many months it shows. |
| 127 | `String filter` |  |
| 128 | `String group` |  |
| 129 | `int activityMonths` |  |
| 132 | `final Set<String> open` | Folds and opened rows, by key. |
| 135 | `final ChartModel worthWindow` | The charts' windows and the lines their legends switched off. |
| 136 | `final Set<String> worthHidden` |  |
| 139 | `private TextField box` | The search box, and whether the player was typing in it when the month redrew the page. |
| 148 | `String ticketKey` | THE TICKET, kept on the screen: which security it is for, buy or sell, by amount or by quantity, the figure, the price (FAIR, BID, ASK, LAST, or OWN - fair value moved by `steps` per cent), and whether it is at its re... |
| 149 | `boolean buy` |  |
| 150 | `boolean byAmount` |  |
| 151 | `double amount` |  |
| 152 | `String priceKind` |  |
| 153 | `int steps` |  |
| 154 | `boolean review` |  |
| 167 | `boolean here` | A MONTH LANDING (the spec's 5): whether the tab was showing before this draw (FinancesScreen sets it), the page and the month last drawn, and the worth the hero said then. |
| 168 | `private String drawnPage` |  |
| 169 | `private int drawnMonth` |  |
| 170 | `private double shownWorth` |  |
| 171 | `private boolean landed` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 74 | 2361 | **type** `final class FundScreen` | The city's fund on the Finances tab, as a brokerage (0.7.39): its Portfolio - what it is worth, what it has made and on what, every holding with its average cost and P&L - a Search of the shares and bonds it can buy, ... |
| 80 | 4 | `FundScreen(UserInterface ui, FinancesScreen fin)` |  |

### THE PAGES (lines 85-118)

| line | len | member | says |
|---:|---:|---|---|
| 98 | 5 | `static String pageFor(String page)` | An older door's page by its new name: Holdings is Portfolio, By hand is Search (Government's doors still say Holdings). |

### what is kept across the clock's redraws: the page, the box, the ticket (lines 119-238)

| line | len | member | says |
|---:|---:|---|---|
| 174 | 14 | `void draw(VBox page, String name)` | Draws one of the fund's pages - or the security open - into `page`. |
| 190 | 9 | `void openSecurity(String key)` | Opens a security's page from the page drawn now. |
| 201 | 4 | `void back()` | Back to the page the security was opened from. |
| 207 | 1 | `void go(String page)` | A page of the fund, from a door. |
| 209 | 1 | `private void redraw()` |  |
| 217 | 21 | `HBox head(Node history)` | The head on a security's page: "Finances › The city's fund › Automotive", each but the last a way back; at its right the debt's history door and a way back to the page it was opened from ("‹ Portfolio", "‹ Search"). |

### WORDS - every figure and sentence the pages compose, worked out (lines 239-326)

| line | len | member | says |
|---:|---:|---|---|
| 245 | 1 | `static String sym(Game g)` | The city's money's mark: "D$". |
| 248 | 5 | `static String d(Game g, double thousands)` | Local money with its mark and a true minus: "D$2.4M", "−D$725k". |
| 255 | 6 | `static String signed(Game g, double thousands)` | ...signed, as a movement: "+D$1.2M", "−D$3.5M", "D$0". |
| 263 | 4 | `static String price(Game g, boolean bond, double p)` | A price a unit: a share's money ("D$101,355", "D$0.9415"), or a bond's per 100 ("102.80 per 100"). |
| 269 | 8 | `static String shares(double n)` | Shares: two places from one ("553.73"), four under it ("0.0076"), and none at all as "0". |
| 279 | 3 | `static String units(Game g, boolean bond, double n)` | Units of a holding: shares, or face in money. |
| 284 | 3 | `static String pct1(double share)` | A share as a percentage, to one place, with a true minus. |
| 289 | 4 | `static String signedPct(double share)` | ...signed: "+14.4%". |
| 295 | 6 | `static String move(double share)` | A move in ink with its arrow: "▲ +4.3%", or "unchanged" when it rounds to none - never a verdict's colour. |
| 303 | 6 | `static String left(int now, int maturity)` | The time a bond has left: "3y 5m left", "5 months left", "a month left", "falls due this month". |
| 311 | 1 | `static String month(int m)` | A month as the pages date things: "Apr 2197". |
| 314 | 12 | `static String securityName(Game g, String key)` | A security's name by its key: the company's, "the rescue book", or the bond's. |

### the hero (lines 327-399)

| line | len | member | says |
|---:|---:|---|---|
| 335 | 3 | **type** `record Hero(String value, String ret, String retTone, String since, String sinceInfo, String cash, String c...` | The Portfolio's hero, in words: the fund's worth; its return over the chart's window ("▲ +D$X (+Y%) over 10Y", or since when it was recorded) in P&L's colour; since it began, exactly; and its three small figures - cas... |
| 339 | 49 | `static Hero hero(Game g, ChartModel window)` |  |
| 390 | 3 | `static boolean wholeRange(ChartModel window)` | Whether a window shows the whole of a named range ("over 10Y"): picked by its chip, not moved since, not "All", and the history at least that long - else the window says its months. |
| 395 | 4 | `static String rangeName(int range)` | A range's name: "1Y", "10Y", "All". |

### the holdings (lines 400-499)

| line | len | member | says |
|---:|---:|---|---|
| 409 | 3 | **type** `record Holding(String group, String key, String name, String sub, int company, int bondId, String issuer, S...` | One row of HOLDINGS: its group, the lot's key, its name and the line under it, the units, the price and its staleness, the worth, the average cost and whether it was counted at market value, the unrealized P&L, and th... |
| 416 | 43 | `static List<Holding> holdings(Game g)` |  |
| 461 | 4 | `static String bargain(FundLedger.Lot lot)` | "bought 26% below value": a lot whose buys paid FundLedger.BARGAIN or more under what they bought was worth at the step's value (the spec's 5); null otherwise. |
| 467 | 1 | **type** `record KindLine(String name, String putIn, String gotBack, String worth, String income, Pnl gain, String note)` | The lines of SINCE IT BEGAN, BY KIND, and its total. |
| 469 | 16 | `static List<KindLine> kinds(Game g)` |  |
| 487 | 1 | **type** `record Closed(String key, String name, String when, Pnl realized, String income)` | One closed lot: its name, the month it closed and why it closed, what it realized, what it paid. |
| 489 | 10 | `static List<Closed> closed(Game g)` |  |

### search (lines 500-544)

| line | len | member | says |
|---:|---:|---|---|
| 503 | 2 | **type** `record Found(String section, String key, String name, String sub, String tag, String price, String move, St...` | One row of Search: its section, key, name and the line under it, its kind tag, price, move or yield, what the fund holds of it, and its sparkline. |
| 506 | 38 | `static List<Found> found(Game g, String query, String filter)` |  |

### a security (lines 545-691)

| line | len | member | says |
|---:|---:|---|---|
| 548 | 1 | **type** `record Security(String name, String tag, String price, String priceNote, List<String[]> facts)` | A security's head and its eight facts, in words. |
| 550 | 62 | `static Security security(Game g, String key)` |  |
| 614 | 1 | **type** `record PositionLine(String label, String value, String tone, String note)` | The lines of YOUR POSITION: a label, a figure, its colour (null: the head's ink), and a muted line under it (null: none). |
| 616 | 75 | `static List<PositionLine> position(Game g, String key)` |  |

### the ticket (lines 692-825)

| line | len | member | says |
|---:|---:|---|---|
| 695 | 1 | **type** `record Ticket(FundView.Quote quote, List<PositionLine> lines, Press press, String held, double limit)` | What the ticket says: its quote's lines, the press, and the sentence under the button (null: none). |
| 698 | 14 | `static double limitFor(Game g, String key, boolean buy, String kind, int steps)` | The price a unit the ticket would post at for its kind, and 0 for fair value (what the rule asks at; a bond's value is its bid too). |
| 722 | 103 | `static Ticket ticket(Game g, String key, boolean buy, boolean byAmount, double figure, String kind, int steps, boolean review)` | THE TICKET'S WORDS (the spec's 4.4): FundView.quote() on today's book, said line by line - what is offered at the price and what would rest, what stands ahead of it, the price over fair value, no fee, the free cash af... |

### the record (lines 826-952)

| line | len | member | says |
|---:|---:|---|---|
| 829 | 2 | **type** `record Act(int month, String what, String by, String units, String money, Pnl realized, String key, boolean...` | One line of Activity: its month, what happened, who, the units, the money, what it realized, the lot to open, and lines it holds (a month's rule trades together). |
| 833 | 40 | `static List<Act> acts(Game g, String group, String key, int months)` | The record's lines, newest first, grouped: a month's rule trades on one side of one market are one line with the lots inside. |
| 875 | 68 | `static Act act(Game g, FundView.Line l)` | One line, in words. |
| 945 | 7 | `static String markAfter(Game g, FundLedger.Activity a)` | A hand trade in shares, after it: the month's last trade is every holding's mark - "your own trade moves the mark" (the spec's 5) - while that month's is still the last. |

### PORTFOLIO - what is it worth, has it made money, and on what? (lines 953-1393)

| line | len | member | says |
|---:|---:|---|---|
| 958 | 8 | `static String holdsInfo()` | WHAT IT HOLDS' (i). |
| 973 | 50 | `void portfolioPage(VBox page)` |  |
| 1025 | 9 | `static VBox mini(String cap, String value, String note, String tone)` | A small figure under the hero's: its caption, the figure, and a line under it. |
| 1036 | 17 | `TimeChart worthChart(Game g)` | The fund's worth and the net put in on City History's big chart, the FUND and BANK decisions as flags. |
| 1055 | 24 | `VBox holdsCard(Game g)` | WHAT IT HOLDS: the four parts as one bar, and the shares against the rule's aim (the Holdings page's until 0.7.39, moved whole). |
| 1091 | 47 | `VBox holdingsCard(Game g)` |  |
| 1140 | 12 | `static HBox headRow()` | The column heads over the holdings. |
| 1154 | 10 | `static Label cell(String text, double width, boolean right)` | A column head. |
| 1165 | 5 | `static Label groupHead(String text)` |  |
| 1172 | 24 | `static VBox figureCell(String value, String tone, String note, double width)` | A figure in a column, right-aligned, wrapping rather than cut, with a muted line under it (null: none). |
| 1198 | 7 | `String iconOf(String key)` | The icon a holding is drawn with: its sector's, the bank's, a bond's paper, the safe for the preferred. |
| 1207 | 6 | `static String colourOf(String key)` | ...and its colour. |
| 1215 | 50 | `HBox holdingRow(Game g, Holding hd, boolean indented)` | One holding as a row: a click opens its security. |
| 1267 | 8 | `static void pop(Label l)` | A label grown a little and back, once, as a month lands on it. |
| 1280 | 22 | `HBox issuerRow(Game g, String issuer, int bonds, double worth, boolean shown, Runnable toggle)` | An issuer's bonds folded into one row: "▸ Automotive · 8 bonds", its worth. |
| 1310 | 43 | `VBox kindsCard(Game g)` |  |
| 1354 | 23 | `VBox closedCard(Game g)` |  |
| 1378 | 15 | `HBox closedRow(Closed c)` |  |

### SEARCH - what can the fund buy, and what is it doing? (lines 1394-1518)

| line | len | member | says |
|---:|---:|---|---|
| 1408 | 27 | `void searchPage(VBox page)` |  |
| 1437 | 23 | `void fillResults(Game g, VBox results)` | The results, refilled in place as the box is typed in (a redraw would lose the next key). |
| 1461 | 44 | `HBox foundRow(Game g, Found f)` |  |
| 1506 | 6 | `static Region spacer(double w)` |  |
| 1514 | 4 | `static List<Integer> months(Game g, int n)` | The last `n` months of the history's axis, for a sparkline of n points. |

### A SECURITY - what is it, what has it done, and what do we have in it? (lines 1519-1795)

| line | len | member | says |
|---:|---:|---|---|
| 1523 | 54 | `void securityPage(VBox page)` |  |
| 1579 | 11 | `static String windowMove(double[] series, List<Integer> axis, ChartModel window)` | The move over a chart's window, in ink: "▲ 4.3% over 10Y". |
| 1592 | 19 | `TimeChart priceChart(Game g, int c)` | A company's price a share today, its fair value, and the fund's average cost, on City History's chart. |
| 1613 | 23 | `VBox cashFlowCard(Game g, CorporateBond b)` | A bond's chart is what it still pays (the spec's D9): a column a calendar year, its coupons and its face, the fund's part solid. |
| 1638 | 28 | `GridPane factsGrid(Game g, String key, List<String[]> facts)` | KEY STATS: eight tiles, four across; the market book against the cap as a bullet bar. |
| 1668 | 28 | `Node bookCard(Game g, String key)` | THE BOOK: today's depth, asks over bids, a row a price level, each with what rests there. |
| 1697 | 29 | `Node depthRow(Game g, OrderBook.Level l, boolean bid, double most, boolean bond)` |  |
| 1728 | 13 | `VBox recordCard(Game g, String key)` | This security's own record: its newest SECURITY_ROWS lines. |
| 1743 | 17 | `VBox positionCard(Game g, String key)` | YOUR POSITION. |
| 1768 | 16 | `static HBox narrowLine(String label, String value, String tone)` | A line in the security page's right column, 412 at the 1,389 window: its words at the left and its figure at the right, both wrapping rather than cut - FinancesScreen.cardLine()'s figure is never cut, and a long quote... |
| 1786 | 9 | `VBox stakeCard(Game g)` | The city's stake in its bank as a ring, with a door to the Bank tab's owners (the rescue book's card's, here too). |

### the ticket (lines 1796-2050)

| line | len | member | says |
|---:|---:|---|---|
| 1799 | 9 | `void resetTicket(String key)` | A new security's ticket: buy, by amount, nothing chosen, at fair value. |
| 1822 | 92 | `VBox ticketCard(Game g, String key)` |  |
| 1916 | 20 | `Node priceChips(Game g, String key, boolean bond)` | The price chips: fair value (the rule's ask; a bond's value, the rule's), the best bid, the best ask, the last trade, and a step of 1% of fair either way. |
| 1938 | 13 | `static Map<String, String> priceChoices(Game g, String key)` | The price chips' words and the kind each picks: fair value (the rule's ask; a bond's value, the rule's price both ways) first, then the best bid, the best ask and the last trade where the book has them. |
| 1953 | 7 | `static String[] ticketFigure(Game g, Ticket t, boolean bond, boolean byAmount, double amount)` | The ticket's figure in words, and the other unit it comes to: "D$1.0M" and "≈ 9.87 shares". |
| 1962 | 15 | `void press(Game g, String key, Ticket t)` | The action button's press: review, then place. |
| 1979 | 13 | `static void place(Game g, String key, boolean buy, Ticket t)` | Places the ticket's order through Game's own calls - what the reviewed press does (the probe's path too): a buy of the quote's money, a sale of its units, at the ticket's price (0 is fair value). |
| 1994 | 25 | `VBox waitingFor(Game g, String key)` | The player's orders for one security - or all, with `key` null: waiting for the step, each with Cancel, and on the book, filled so far. |
| 2020 | 5 | `static Label captionLine(String text)` |  |
| 2026 | 9 | `HBox orderRow(String words, String act, Runnable go)` |  |
| 2037 | 13 | `static String orderWords(Game g, TreasuryFund.HandOrder o, boolean posted)` | An order of the player's, in words: "Buy D$4.0M of Construction at fair value", or posted, "...: 0 of 39.47 filled". |

### ACTIVITY - what has the fund done, and what was done to it? (lines 2051-2133)

| line | len | member | says |
|---:|---:|---|---|
| 2058 | 43 | `void activityPage(VBox page)` |  |
| 2103 | 30 | `HBox actRow(Game g, Act a, boolean dated)` | One line of the record: what, by whom, units, money, realized, and "›" to its security; a grouped line opens. |

### RULES & CASH - how does it run, and how do I move money? (lines 2134-2196)

| line | len | member | says |
|---:|---:|---|---|
| 2142 | 14 | `void rulesPage(VBox page)` |  |
| 2158 | 6 | `static String dialInfo()` | The dial's (i). |
| 2166 | 30 | `VBox dialCard(Game g)` | THE DIAL: its share of the year's surplus, the steps, and what it reads against. |

### THE WITHDRAWAL (0.7.48, C2) (lines 2197-2434)

| line | len | member | says |
|---:|---:|---|---|
| 2203 | 12 | `static String withdrawalInfo()` | The withdrawal's (i). |
| 2229 | 12 | `static String withdrawalWords(double rate)` | The withdrawal's status line (pure: the probe reads it): nothing at 0, Norway's rule at the default, and otherwise the year it makes - twelve times the rate, the rule's own arithmetic - and how soon, earning nothing, ... |
| 2243 | 3 | `static String withdrawalShare(double rate)` | The withdrawal in a few words, as the page's head reads it: "0.25% of its worth a month". |
| 2255 | 14 | `static java.util.function.DoubleFunction<List<Pieces.Effect>> withdrawalEffects(Game g)` | What the withdrawal would do at any value of its thumb (pure: the probe reads them), each PolicyPreview.fundWithdrawalAt() against the dial in force: next month's to the treasury, what its cash pays of it, the rest - ... |
| 2271 | 13 | `VBox withdrawalCard(Game g)` | THE WITHDRAWAL (0.7.48, C2): the dial on a Levers card, as the fare's on Infrastructure - its reading, its status, the ladder, what it would do, and its Apply once staged. |
| 2286 | 8 | `static String transferInfo(TreasuryFund fund)` | The transfer's (i): the withdrawal dial in force, read. |
| 2296 | 26 | `VBox transferCard(Game g)` | TO THE TREASURY: last month's, paid and not, this year's, next month's, and what came in. |
| 2329 | 15 | `VBox rescueBookCard(Game g)` | THE RESCUE BOOK: the city's stake in its bank as a ring, the book's lines, and the preferred's terms (0.7.39). |
| 2346 | 15 | `static List<String> rescueTerms(Game g)` | The preferred's terms and its warrants', a line each (0.7.39). |
| 2363 | 30 | `VBox moveCard(Game g)` | PAY IN, DRAW OUT: the amount and its steps (FinancesScreen.fundAsk), and the two moves. |
| 2395 | 5 | `static Press payInPress(Game g, double ask)` | PAY IN's press: choose, nothing in the treasury, or what it would move. |
| 2402 | 5 | `static Press drawOutPress(Game g, double ask)` | DRAW OUT's press: only the fund's free cash - not what the player's buys hold - can be drawn. |
| 2409 | 9 | `static String capRule()` | THE RULE's sentence on the cap. |
| 2420 | 14 | `VBox ruleCard(Game g)` | THE RULE: the shares against the aim, the cap in words, and the treasury's rescue setting. |

