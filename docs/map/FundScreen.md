# FundScreen.java - 2,324 lines · 94 methods · 20 constants · interface

`ham/citybuildersim/ui/FundScreen.java` - generated 2026-10-02 by CodeMap; line numbers are as of that run.

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
> city. The cards that were the Holdings page - the dial, the 3% transfer,
> the rescue book, WHAT IT HOLDS and the rule's aim - moved here whole
> (Rules & cash, Portfolio); By hand's amount, chips and buttons are the
> ticket, Search and a security's page (the spec's 4.8, nothing lost).
> 
> COLOURS. P&L is a verdict - did this purchase make or lose the city money -
> so it is green and red (Pieces.pnl(), the spec's D7), always with a sign
> and an arrow; nothing else here is: a price, its move, a yield and every
> chart line are in ink or their area's colour.

**Uses:** [Palette](Palette.md) (228), [FundView](FundView.md) (100), [FundLedger](FundLedger.md) (61), [Game](Game.md) (59), [TreasuryFund](TreasuryFund.md) (40), [Icons](Icons.md) (18), [Equity](Equity.md) (18), [TimeChart](TimeChart.md) (17), [ChartModel](ChartModel.md) (16), [OrderBook](OrderBook.md) (11), [FinancesScreen](FinancesScreen.md) (9), [CityCalendar](CityCalendar.md) (8), [CorporateBond](CorporateBond.md) (7), [HistoryScreen](HistoryScreen.md) (7), [HistorySave](HistorySave.md) (5), [Bank](Bank.md) (4), [UserInterface](UserInterface.md) (2), [DecisionLog](DecisionLog.md) (2), [Exchange](Exchange.md) (1), [BondMarket](BondMarket.md) (1), [SectorScreen](SectorScreen.md) (1), [Pieces](Pieces.md) (1), [BuildScreen](BuildScreen.md) (1)

**Used by (1):** [FinancesScreen](FinancesScreen.md)

## Sections

| line | section |
|---:|---|
| 84 | THE PAGES |
| 118 | · what is kept across the clock's redraws: the page, the box, the ticket |
| 238 | WORDS - every figure and sentence the pages compose, worked out |
| 326 | · the hero |
| 398 | · the holdings |
| 497 | · search |
| 542 | · a security |
| 687 | · the ticket |
| 820 | · the record |
| 947 | PORTFOLIO - what is it worth, has it made money, and on what? |
| 975 | · · the hero: its worth, its return, since it began; the chart |
| 1001 | · · what it holds, and the rule's aim (the old Holdings page's, whole) |
| 1004 | · · every holding |
| 1007 | · · since it began, by kind; closed |
| 1010 | · · the doors |
| 1388 | SEARCH - what can the fund buy, and what is it doing? |
| 1513 | A SECURITY - what is it, what has it done, and what do we have in it? |
| 1522 | · · the left: its head, its chart, its facts, its book, its record |
| 1556 | · · the right: your position, and the ticket |
| 1790 | · the ticket |
| 1821 | · · buy or sell; by amount or by quantity |
| 1840 | · · the figure and its steps |
| 1880 | · · the price |
| 1883 | · · the quote |
| 1889 | · · the action |
| 1902 | · · this security's orders: waiting, and on the book |
| 2044 | ACTIVITY - what has the fund done, and what was done to it? |
| 2127 | RULES & CASH - how does it run, and how do I move money? |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 89 | `FundScreen.AREA` | `"The city's fund"` | The area's name on Finances. |
| 92 | `FundScreen.PAGES` | `{ "Portfolio", "Search", "Activity", "Rules & cash" }` | The fund's four pages (0.7.39; "Holdings" and "By hand" until then). |
| 94 | `FundScreen.ICONS` | `{ Icons.SAFE, Icons.EXCHANGE, Icons.REPORTS, Icons.SETTINGS }` | ...and their icons on the chips. |
| 104 | `FundScreen.ACTIVITY_MONTHS` | `60` | The months of the record Activity shows before "show older", and how many more each press adds. |
| 107 | `FundScreen.SECURITY_ROWS` | `12` | The rows of a security's own record under its book. |
| 110 | `FundScreen.CLOSED_ROWS` | `8` | The closed lots the Portfolio's CLOSED card lists before its fold. |
| 113 | `FundScreen.MONEY_STEPS` | `{ 1_000, 10_000, 100_000, 1_000_000 }` | The money steps: the ticket's on a buy by amount and on a bond's face, and PAY IN, DRAW OUT's; a quantity of shares steps by 1, 10, 100 and 1,000 instead. |
| 116 | `FundScreen.LEFT` | `812, RIGHT = 412` | The security page's two columns at the 1,389 window: the picture and its facts, then the position and the ticket. |
| 156 | `FundScreen.FAIR` | `"fair", BID = "bid", ASK = "ask", LAST = "last", OWN = "own"` | The ticket's prices. |
| 412 | `FundScreen.SHARES` | `"COMPANY SHARES", RESCUE = "THE RESCUE BOOK", BONDS = "BONDS"` | The holdings' groups, in the page's order. |
| 962 | `FundScreen.HERO_INFO` | `"Everything the fund holds at the marks every other holder uses, and its cash...` | THE CITY'S FUND's (i). |
| 1075 | `FundScreen.COL_NAME` | `250, COL_UNITS = 120, COL_PRICE = 110, COL_VALUE = 110, COL_AVG = 110, COL_PN...` | HOLDINGS' columns: the name, units, price, worth, average cost, unrealized, and the share of the fund's bar. |
| 1078 | `FundScreen.HOLDINGS_INFO` | `"Every lot the fund holds, at its mark, with what it cost by the average-cost...` | HOLDINGS' (i). |
| 1271 | `FundScreen.POP_MILLIS` | `220` | How long a payment's pop takes each way. |
| 1298 | `FundScreen.KINDS_INFO` | `"Each kind of holding since the fund began: what the city put into it(bought ...` | SINCE IT BEGAN, BY KIND's (i). |
| 1393 | `FundScreen.FILTERS` | `{ "All", "Shares", "Bonds", "Held" }` | Search's chips. |
| 1396 | `FundScreen.SEARCH_INFO` | `"Every company listed on the exchange, every business's bond still outstandin...` | Search's (i): the spec's D1. |
| 1804 | `FundScreen.TICKET_INFO` | `"An order goes on the book at the next month's step, after every other partic...` | THE ORDER TICKET's (i). |
| 2049 | `FundScreen.GROUPS` | `{ "All", FundView.TRADES, FundView.INCOME, FundView.MONEY, FundView.EVENTS }` | Activity's chips: every row, or one group of them (FundView.groupOf()). |
| 2215 | `FundScreen.RESCUE_BOOK_INFO` | `"What the city holds from rescuing its bank: the shares it took when it " + "...` | The rescue book's (i). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 76 | `private final UserInterface ui` | The window this screen draws into, and the tab it is drawn on. |
| 77 | `private final FinancesScreen fin` |  |
| 121 | `String security` | The security open, by its lot key ("S:Automotive", "B:142"), or null; and the page it was opened from. |
| 122 | `String cameFrom` |  |
| 125 | `String query` | The search box's words and its chip; Activity's chip and how many months it shows. |
| 126 | `String filter` |  |
| 127 | `String group` |  |
| 128 | `int activityMonths` |  |
| 131 | `final Set<String> open` | Folds and opened rows, by key. |
| 134 | `final ChartModel worthWindow` | The charts' windows and the lines their legends switched off. |
| 135 | `final Set<String> worthHidden` |  |
| 138 | `private TextField box` | The search box, and whether the player was typing in it when the month redrew the page. |
| 147 | `String ticketKey` | THE TICKET, kept on the screen: which security it is for, buy or sell, by amount or by quantity, the figure, the price (FAIR, BID, ASK, LAST, or OWN - fair value moved by `steps` per cent), and whether it is at its re... |
| 148 | `boolean buy` |  |
| 149 | `boolean byAmount` |  |
| 150 | `double amount` |  |
| 151 | `String priceKind` |  |
| 152 | `int steps` |  |
| 153 | `boolean review` |  |
| 166 | `boolean here` | A MONTH LANDING (the spec's 5): whether the tab was showing before this draw (FinancesScreen sets it), the page and the month last drawn, and the worth the hero said then. |
| 167 | `private String drawnPage` |  |
| 168 | `private int drawnMonth` |  |
| 169 | `private double shownWorth` |  |
| 170 | `private boolean landed` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 73 | 2252 | **type** `final class FundScreen` | The city's fund on the Finances tab, as a brokerage (0.7.39): its Portfolio - what it is worth, what it has made and on what, every holding with its average cost and P&L - a Search of the shares and bonds it can buy, ... |
| 79 | 4 | `FundScreen(UserInterface ui, FinancesScreen fin)` |  |

### THE PAGES (lines 84-117)

| line | len | member | says |
|---:|---:|---|---|
| 97 | 5 | `static String pageFor(String page)` | An older door's page by its new name: Holdings is Portfolio, By hand is Search (Government's doors still say Holdings). |

### what is kept across the clock's redraws: the page, the box, the ticket (lines 118-237)

| line | len | member | says |
|---:|---:|---|---|
| 173 | 14 | `void draw(VBox page, String name)` | Draws one of the fund's pages - or the security open - into `page`. |
| 189 | 9 | `void openSecurity(String key)` | Opens a security's page from the page drawn now. |
| 200 | 4 | `void back()` | Back to the page the security was opened from. |
| 206 | 1 | `void go(String page)` | A page of the fund, from a door. |
| 208 | 1 | `private void redraw()` |  |
| 216 | 21 | `HBox head(Node history)` | The head on a security's page: "Finances › The city's fund › Automotive", each but the last a way back; at its right the debt's history door and a way back to the page it was opened from ("‹ Portfolio", "‹ Search"). |

### WORDS - every figure and sentence the pages compose, worked out (lines 238-325)

| line | len | member | says |
|---:|---:|---|---|
| 244 | 1 | `static String sym(Game g)` | The city's money's mark: "D$". |
| 247 | 5 | `static String d(Game g, double thousands)` | Local money with its mark and a true minus: "D$2.4M", "−D$725k". |
| 254 | 6 | `static String signed(Game g, double thousands)` | ...signed, as a movement: "+D$1.2M", "−D$3.5M", "D$0". |
| 262 | 4 | `static String price(Game g, boolean bond, double p)` | A price a unit: a share's money ("D$101,355", "D$0.9415"), or a bond's per 100 ("102.80 per 100"). |
| 268 | 8 | `static String shares(double n)` | Shares: two places from one ("553.73"), four under it ("0.0076"), and none at all as "0". |
| 278 | 3 | `static String units(Game g, boolean bond, double n)` | Units of a holding: shares, or face in money. |
| 283 | 3 | `static String pct1(double share)` | A share as a percentage, to one place, with a true minus. |
| 288 | 4 | `static String signedPct(double share)` | ...signed: "+14.4%". |
| 294 | 6 | `static String move(double share)` | A move in ink with its arrow: "▲ +4.3%", or "unchanged" when it rounds to none - never a verdict's colour. |
| 302 | 6 | `static String left(int now, int maturity)` | The time a bond has left: "3y 5m left", "5 months left", "a month left", "falls due this month". |
| 310 | 1 | `static String month(int m)` | A month as the pages date things: "Apr 2197". |
| 313 | 12 | `static String securityName(Game g, String key)` | A security's name by its key: the company's, "the rescue book", or the bond's. |

### the hero (lines 326-397)

| line | len | member | says |
|---:|---:|---|---|
| 334 | 3 | **type** `record Hero(String value, String ret, String retTone, String since, String sinceInfo, String cash, String c...` | The Portfolio's hero, in words: the fund's worth; its return over the chart's window ("▲ +D$X (+Y%) over 10Y", or since when it was recorded) in P&L's colour; since it began, exactly; and its three small figures - cas... |
| 338 | 48 | `static Hero hero(Game g, ChartModel window)` |  |
| 388 | 3 | `static boolean wholeRange(ChartModel window)` | Whether a window shows the whole of a named range ("over 10Y"): picked by its chip, not moved since, not "All", and the history at least that long - else the window says its months. |
| 393 | 4 | `static String rangeName(int range)` | A range's name: "1Y", "10Y", "All". |

### the holdings (lines 398-496)

| line | len | member | says |
|---:|---:|---|---|
| 407 | 3 | **type** `record Holding(String group, String key, String name, String sub, int company, int bondId, String issuer, S...` | One row of HOLDINGS: its group, the lot's key, its name and the line under it, the units, the price and its staleness, the worth, the average cost and whether it was counted at market value, the unrealized P&L, and th... |
| 414 | 42 | `static List<Holding> holdings(Game g)` |  |
| 458 | 4 | `static String bargain(FundLedger.Lot lot)` | "bought 26% below value": a lot whose buys paid FundLedger.BARGAIN or more under what they bought was worth at the step's value (the spec's 5); null otherwise. |
| 464 | 1 | **type** `record KindLine(String name, String putIn, String gotBack, String worth, String income, Pnl gain, String note)` | The lines of SINCE IT BEGAN, BY KIND, and its total. |
| 466 | 16 | `static List<KindLine> kinds(Game g)` |  |
| 484 | 1 | **type** `record Closed(String key, String name, String when, Pnl realized, String income)` | One closed lot: its name, the month it closed and why it closed, what it realized, what it paid. |
| 486 | 10 | `static List<Closed> closed(Game g)` |  |

### search (lines 497-541)

| line | len | member | says |
|---:|---:|---|---|
| 500 | 2 | **type** `record Found(String section, String key, String name, String sub, String tag, String price, String move, St...` | One row of Search: its section, key, name and the line under it, its kind tag, price, move or yield, what the fund holds of it, and its sparkline. |
| 503 | 38 | `static List<Found> found(Game g, String query, String filter)` |  |

### a security (lines 542-686)

| line | len | member | says |
|---:|---:|---|---|
| 545 | 1 | **type** `record Security(String name, String tag, String price, String priceNote, List<String[]> facts)` | A security's head and its eight facts, in words. |
| 547 | 61 | `static Security security(Game g, String key)` |  |
| 610 | 1 | **type** `record PositionLine(String label, String value, String tone, String note)` | The lines of YOUR POSITION: a label, a figure, its colour (null: the head's ink), and a muted line under it (null: none). |
| 612 | 74 | `static List<PositionLine> position(Game g, String key)` |  |

### the ticket (lines 687-819)

| line | len | member | says |
|---:|---:|---|---|
| 690 | 1 | **type** `record Ticket(FundView.Quote quote, List<PositionLine> lines, Press press, String held, double limit)` | What the ticket says: its quote's lines, the press, and the sentence under the button (null: none). |
| 693 | 14 | `static double limitFor(Game g, String key, boolean buy, String kind, int steps)` | The price a unit the ticket would post at for its kind, and 0 for fair value (the rule's own). |
| 717 | 102 | `static Ticket ticket(Game g, String key, boolean buy, boolean byAmount, double figure, String kind, int steps, boolean review)` | THE TICKET'S WORDS (the spec's 4.4): FundView.quote() on today's book, said line by line - what is offered at the price and what would rest, what stands ahead of it, the price over fair value, no fee, the free cash af... |

### the record (lines 820-946)

| line | len | member | says |
|---:|---:|---|---|
| 823 | 2 | **type** `record Act(int month, String what, String by, String units, String money, Pnl realized, String key, boolean...` | One line of Activity: its month, what happened, who, the units, the money, what it realized, the lot to open, and lines it holds (a month's rule trades together). |
| 827 | 40 | `static List<Act> acts(Game g, String group, String key, int months)` | The record's lines, newest first, grouped: a month's rule trades on one side of one market are one line with the lots inside. |
| 869 | 68 | `static Act act(Game g, FundView.Line l)` | One line, in words. |
| 939 | 7 | `static String markAfter(Game g, FundLedger.Activity a)` | A hand trade in shares, after it: the month's last trade is every holding's mark - "your own trade moves the mark" (the spec's 5) - while that month's is still the last. |

### PORTFOLIO - what is it worth, has it made money, and on what? (lines 947-1387)

| line | len | member | says |
|---:|---:|---|---|
| 952 | 8 | `static String holdsInfo()` | WHAT IT HOLDS' (i). |
| 967 | 50 | `void portfolioPage(VBox page)` |  |
| 1019 | 9 | `static VBox mini(String cap, String value, String note, String tone)` | A small figure under the hero's: its caption, the figure, and a line under it. |
| 1030 | 17 | `TimeChart worthChart(Game g)` | The fund's worth and the net put in on City History's big chart, the FUND and BANK decisions as flags. |
| 1049 | 24 | `VBox holdsCard(Game g)` | WHAT IT HOLDS: the four parts as one bar, and the shares against the rule's aim (the Holdings page's until 0.7.39, moved whole). |
| 1085 | 47 | `VBox holdingsCard(Game g)` |  |
| 1134 | 12 | `static HBox headRow()` | The column heads over the holdings. |
| 1148 | 10 | `static Label cell(String text, double width, boolean right)` | A column head. |
| 1159 | 5 | `static Label groupHead(String text)` |  |
| 1166 | 24 | `static VBox figureCell(String value, String tone, String note, double width)` | A figure in a column, right-aligned, wrapping rather than cut, with a muted line under it (null: none). |
| 1192 | 7 | `String iconOf(String key)` | The icon a holding is drawn with: its sector's, the bank's, a bond's paper, the safe for the preferred. |
| 1201 | 6 | `static String colourOf(String key)` | ...and its colour. |
| 1209 | 50 | `HBox holdingRow(Game g, Holding hd, boolean indented)` | One holding as a row: a click opens its security. |
| 1261 | 8 | `static void pop(Label l)` | A label grown a little and back, once, as a month lands on it. |
| 1274 | 22 | `HBox issuerRow(Game g, String issuer, int bonds, double worth, boolean shown, Runnable toggle)` | An issuer's bonds folded into one row: "▸ Automotive · 8 bonds", its worth. |
| 1304 | 43 | `VBox kindsCard(Game g)` |  |
| 1348 | 23 | `VBox closedCard(Game g)` |  |
| 1372 | 15 | `HBox closedRow(Closed c)` |  |

### SEARCH - what can the fund buy, and what is it doing? (lines 1388-1512)

| line | len | member | says |
|---:|---:|---|---|
| 1402 | 27 | `void searchPage(VBox page)` |  |
| 1431 | 23 | `void fillResults(Game g, VBox results)` | The results, refilled in place as the box is typed in (a redraw would lose the next key). |
| 1455 | 44 | `HBox foundRow(Game g, Found f)` |  |
| 1500 | 6 | `static Region spacer(double w)` |  |
| 1508 | 4 | `static List<Integer> months(Game g, int n)` | The last `n` months of the history's axis, for a sparkline of n points. |

### A SECURITY - what is it, what has it done, and what do we have in it? (lines 1513-1789)

| line | len | member | says |
|---:|---:|---|---|
| 1517 | 54 | `void securityPage(VBox page)` |  |
| 1573 | 11 | `static String windowMove(double[] series, List<Integer> axis, ChartModel window)` | The move over a chart's window, in ink: "▲ 4.3% over 10Y". |
| 1586 | 19 | `TimeChart priceChart(Game g, int c)` | A company's price a share today, its fair value, and the fund's average cost, on City History's chart. |
| 1607 | 23 | `VBox cashFlowCard(Game g, CorporateBond b)` | A bond's chart is what it still pays (the spec's D9): a column a calendar year, its coupons and its face, the fund's part solid. |
| 1632 | 28 | `GridPane factsGrid(Game g, String key, List<String[]> facts)` | KEY STATS: eight tiles, four across; the market book against the cap as a bullet bar. |
| 1662 | 28 | `Node bookCard(Game g, String key)` | THE BOOK: today's depth, asks over bids, a row a price level, each with what rests there. |
| 1691 | 29 | `Node depthRow(Game g, OrderBook.Level l, boolean bid, double most, boolean bond)` |  |
| 1722 | 13 | `VBox recordCard(Game g, String key)` | This security's own record: its newest SECURITY_ROWS lines. |
| 1737 | 17 | `VBox positionCard(Game g, String key)` | YOUR POSITION. |
| 1762 | 16 | `static HBox narrowLine(String label, String value, String tone)` | A line in the security page's right column, 412 at the 1,389 window: its words at the left and its figure at the right, both wrapping rather than cut - FinancesScreen.cardLine()'s figure is never cut, and a long quote... |
| 1780 | 9 | `VBox stakeCard(Game g)` | The city's stake in its bank as a ring, with a door to the Bank tab's owners (the rescue book's card's, here too). |

### the ticket (lines 1790-2043)

| line | len | member | says |
|---:|---:|---|---|
| 1793 | 9 | `void resetTicket(String key)` | A new security's ticket: buy, by amount, nothing chosen, at fair value. |
| 1815 | 92 | `VBox ticketCard(Game g, String key)` |  |
| 1909 | 20 | `Node priceChips(Game g, String key, boolean bond)` | The price chips: fair value (the rule's), the best bid, the best ask, the last trade, and a step of 1% of fair either way. |
| 1931 | 13 | `static Map<String, String> priceChoices(Game g, String key)` | The price chips' words and the kind each picks: fair value (the rule's) first, then the best bid, the best ask and the last trade where the book has them. |
| 1946 | 7 | `static String[] ticketFigure(Game g, Ticket t, boolean bond, boolean byAmount, double amount)` | The ticket's figure in words, and the other unit it comes to: "D$1.0M" and "≈ 9.87 shares". |
| 1955 | 15 | `void press(Game g, String key, Ticket t)` | The action button's press: review, then place. |
| 1972 | 13 | `static void place(Game g, String key, boolean buy, Ticket t)` | Places the ticket's order through Game's own calls - what the reviewed press does (the probe's path too): a buy of the quote's money, a sale of its units, at the ticket's price (0 is fair value). |
| 1987 | 25 | `VBox waitingFor(Game g, String key)` | The player's orders for one security - or all, with `key` null: waiting for the step, each with Cancel, and on the book, filled so far. |
| 2013 | 5 | `static Label captionLine(String text)` |  |
| 2019 | 9 | `HBox orderRow(String words, String act, Runnable go)` |  |
| 2030 | 13 | `static String orderWords(Game g, TreasuryFund.HandOrder o, boolean posted)` | An order of the player's, in words: "Buy D$4.0M of Construction at fair value", or posted, "...: 0 of 39.47 filled". |

### ACTIVITY - what has the fund done, and what was done to it? (lines 2044-2126)

| line | len | member | says |
|---:|---:|---|---|
| 2051 | 43 | `void activityPage(VBox page)` |  |
| 2096 | 30 | `HBox actRow(Game g, Act a, boolean dated)` | One line of the record: what, by whom, units, money, realized, and "›" to its security; a grouped line opens. |

### RULES & CASH - how does it run, and how do I move money? (lines 2127-2324)

| line | len | member | says |
|---:|---:|---|---|
| 2133 | 13 | `void rulesPage(VBox page)` |  |
| 2148 | 6 | `static String dialInfo()` | The dial's (i). |
| 2156 | 30 | `VBox dialCard(Game g)` | THE DIAL: its share of the year's surplus, the steps, and what it reads against. |
| 2188 | 5 | `static String transferInfo()` | The transfer's (i). |
| 2195 | 18 | `VBox transferCard(Game g)` | ITS 3% TO THE TREASURY: last month's, paid and not, this year's, next month's, and what came in. |
| 2220 | 15 | `VBox rescueBookCard(Game g)` | THE RESCUE BOOK: the city's stake in its bank as a ring, the book's lines, and the preferred's terms (0.7.39). |
| 2237 | 15 | `static List<String> rescueTerms(Game g)` | The preferred's terms and its warrants', a line each (0.7.39). |
| 2254 | 30 | `VBox moveCard(Game g)` | PAY IN, DRAW OUT: the amount and its steps (FinancesScreen.fundAsk), and the two moves. |
| 2286 | 5 | `static Press payInPress(Game g, double ask)` | PAY IN's press: choose, nothing in the treasury, or what it would move. |
| 2293 | 5 | `static Press drawOutPress(Game g, double ask)` | DRAW OUT's press: only the fund's free cash - not what the player's buys hold - can be drawn. |
| 2300 | 8 | `static String capRule()` | THE RULE's sentence on the cap. |
| 2310 | 14 | `VBox ruleCard(Game g)` | THE RULE: the shares against the aim, the cap in words, and the treasury's rescue setting. |

