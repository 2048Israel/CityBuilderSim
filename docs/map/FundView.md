# FundView.java - 839 lines · 38 methods · 8 constants · model

`ham/citybuildersim/FundView.java` - generated 2026-10-02 by CodeMap; line numbers are as of that run.

> The city's fund as the screens read it (0.7.39): every holding with its average cost and P&L, what the fund has made since it began and by kind, its return over a chart's window, the market to search, a hand order's quote on today's book, and its record - pure, and the screens' only door to the fund's cost basis.
> 
> WHY. Jerus, the morning after 0.7.38: "when you click buy manually i want it
> to be like wealthsimple trade type kinda like a brokerage, where you can
> search the shares and bonds and see and all, and also the city fund should
> show pnl and acb and all that". The Fund page showed what the fund held at
> its marks and nothing of what it had cost; a hand order was a chip and a
> button at fair value, with nothing to say what it would meet on the book.
> The project's spec-fund-0739.md, sections 3.4 and 4.
> 
> EVERY FIGURE IS THE MODEL'S. A holding's units are the register's and the
> bonds' (Equity, CorporateBond.city()), its price the exchange's last trade
> or fair value (Exchange.price()) or the bond market's valuation
> (BondMarket.modelPrice()), its cost FundLedger's (or TreasuryFund's for the
> rescue book), its income the ledger's and the counters'. The fund's gain
> since it began, and by kind, is the counters' alone - exact on any save,
> whatever the ledger knows. Nothing here writes anything: BuildCard's shape,
> so a probe reads every figure a page draws.

**Uses:** [FundLedger](FundLedger.md) (57), [Equity](Equity.md) (19), [Game](Game.md) (17), [HistorySave](HistorySave.md) (13), [BondMarket](BondMarket.md) (10), [Exchange](Exchange.md) (10), [OrderBook](OrderBook.md) (10), [TreasuryFund](TreasuryFund.md) (9), [ChartModel](ChartModel.md) (9), [CorporateBond](CorporateBond.md) (7), [DecisionLog](DecisionLog.md) (7), [Bank](Bank.md) (3), [CityCalendar](CityCalendar.md) (3)

**Used by (4):** [FundLedgerCheck](FundLedgerCheck.md), [FundScreen](FundScreen.md), [LongPlaytest](LongPlaytest.md), [ReadPathCheck](ReadPathCheck.md)

## Sections

| line | section |
|---:|---|
| 32 | the dials |
| 52 | a position |
| 220 | the fund |
| 266 | the return over a window |
| 313 | a security's history |
| 386 | search |
| 560 | the quote |
| 772 | the record |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 35 | `FundView.STALE_MONTHS` | `3` | A share's last trade older than this many months is called stale on every page that shows it (the spec's B3: a price is its last trade, however old). |
| 38 | `FundView.MIN_RECORDED_PRICE` | `.01` | The least a price per founding share, as the history records it to four places, can be and still carry three significant figures: a move off less is not shown (the spec's B6 - a consolidated share records 0.0000). |
| 41 | `FundView.FOLD_UNDER` | `1` | A holding worth less than this, money, is folded into "and N more" on the Portfolio page: a thousand dollars. |
| 44 | `FundView.MOVE_MONTHS` | `12` | The months a hit's sparkline and its move cover: a year. |
| 47 | `FundView.SHARE` | `"share", RESCUE = "rescue", BOND = "bond", PREFERRED = "preferred", WARRANTS ...` | What a holding is. |
| 50 | `FundView.CLOSED` | `"closed"` | A search hit that is a lot the fund closed on a bond no longer on any book: found only when named (the spec's D1). |
| 389 | `FundView.HELD` | `"YOUR HOLDINGS", SHARES_SECTION = "SHARES", BONDS_SECTION = "BONDS"` | What a search hit is for: the market's sections when the box is empty. |
| 775 | `FundView.TRADES` | `"Trades", INCOME = "Income", MONEY = "Money in and out", EVENTS = "Events"` | The activity page's chips: what each kind of row is filed under. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 28 | 812 | **type** `public final class FundView` | The city's fund as the screens read it (0.7.39): every holding with its average cost and P&L, what the fund has made since it began and by kind, its return over a chart's window, the market to search, a hand order's q... |
| 30 | 1 | `private FundView()` |  |

### the dials (lines 32-51)

### a position (lines 52-219)

| line | len | member | says |
|---:|---:|---|---|
| 66 | 14 | **type** `public record Position(String key, String name, String kind, int company, int bondId, double units, double ...` | One holding, as the pages draw it: its key and name, what it is (and for a share the company's index, for a bond its id); the units held (shares, or face), its price a unit and the month of that price (-1: fair value,... |
| 72 | 3 | `public double totalReturn()` _(in FundView.Position)_ | Unrealized, realized and income together: what the holding has made since its cost began. |
| 75 | 1 | `public boolean isShare()` _(in FundView.Position)_ |  |
| 76 | 1 | `public boolean isBond()` _(in FundView.Position)_ |  |
| 78 | 1 | `public boolean stale(int month)` _(in FundView.Position)_ | True when its price is a last trade older than STALE_MONTHS. |
| 82 | 18 | `public static List<Position> positions(Game g)` | Every holding the fund has now, each lot its own row: the market books, the rescue book, each bond, the preferred and the warrants while held - in register order, then bonds by id. |
| 102 | 22 | `public static Position sharePosition(Game g, int c, boolean rescue)` | One company's market book (rescue false) or rescue book, or null when it holds none. |
| 126 | 15 | `public static Position bondPosition(Game g, CorporateBond b)` | One bond the fund holds face of, or null. |
| 143 | 10 | `public static Position preferredPosition(Game g)` | The bank's preferred while any is outstanding: at par, its cost (the treasury paid par), its dividends; realized what a failure cancelled. |
| 155 | 8 | `public static Position warrantsPosition(Game g)` | The warrants on the bank's shares while any are out: they cost nothing, so all they are worth is unrealized and all they brought back realized. |
| 165 | 5 | `static double preferredCancelled(TreasuryFund f)` | The par a failure cancelled, every resolution together. |
| 172 | 4 | `public static String bondName(CorporateBond b)` | A bond's name: "#142 Automotive 2.81% 2059". |
| 178 | 12 | `public static Position position(Game g, String key)` | One position by its key, held now, or null. |
| 192 | 7 | `public static int companyOf(String key)` | The company a share key names, or -1. |
| 201 | 4 | `public static int bondIdOf(String key)` | The bond a bond key names, or -1. |
| 207 | 6 | `public static List<FundLedger.Lot> closedLots(Game g)` | A lot the fund has closed - sold out, repaid, written off, passed to the rescue book - with what it realized and paid. |
| 215 | 4 | `public static double yieldOnCost(Game g, Position p)` | The dividend a share pays a year over what a share cost: the rescue book's yield on cost (the spec's 5). |

### the fund (lines 220-265)

| line | len | member | says |
|---:|---:|---|---|
| 223 | 1 | **type** `public record Kind(String name, double putIn, double gotBack, double worth, double income, double gain, Str...` | One kind's line of SINCE IT BEGAN, BY KIND: what went into it, what came back out, what it is worth, its income and its gain. |
| 233 | 4 | **type** `public record Portfolio(double value, double cash, double incomeLastMonth, double transferLastMonth, double...` | The fund as a whole: its worth and cash; last month's income and its transfer (and what of it went unpaid); since it began - what the city put in (pay-ins, rescues paid, preferred bought), what it took out (transfers,... |
| 238 | 27 | `public static Portfolio portfolio(Game g)` |  |

### the return over a window (lines 266-312)

| line | len | member | says |
|---:|---:|---|---|
| 276 | 3 | **type** `public record RangeReturn(double gain, double pct, int fromMonth, int toMonth, int recordedFrom)` | What the fund made over a window of months: the gain in money - its worth at the end less at the start less what was put in net between - and as a return, Modified Dietz (each month's net flow weighted by the share of... |
| 277 | 1 | `public boolean recorded()` _(in FundView.RangeReturn)_ |  |
| 280 | 24 | `public static RangeReturn rangeReturn(HistorySave h, int fromMonth, int toMonth)` |  |
| 306 | 6 | `public static double[] netPutIn(HistorySave h)` | The fund's net put in, month by month - what went in less what came out, cumulative (HistorySave's fundPutIn less fundTakenOut) - for the Portfolio chart's second line; NaN where not recorded. |

### a security's history (lines 313-385)

| line | len | member | says |
|---:|---:|---|---|
| 323 | 20 | `public static double[][] sharePrices(Game g, int c)` | A company's price history a share as it is today: the last trade and fair value per founding share (the history's sharePrice and shareValue) over today's split factor, so the line runs through every split at today's c... |
| 345 | 1 | **type** `public record YearFlow(int year, double coupons, double face, double fundCoupons, double fundFace)` | One calendar year of what a bond still pays: the coupons and the face, the whole bond's and the fund's part. |
| 353 | 19 | `public static List<YearFlow> bondCashFlows(Game g, CorporateBond b)` | WHAT A BOND STILL PAYS, a calendar year at a time (the spec's D9: no bond's price is kept month by month, so its chart is its cash): a twelfth of its coupon on its face every month it has left, and the face in its las... |
| 374 | 11 | `public static List<ChartModel.Flag> flagsFor(Game g, String words)` | The FUND decisions that name a company or a bond - its own orders - as flags on its chart; the founding month's on the axis. |

### search (lines 386-559)

| line | len | member | says |
|---:|---:|---|---|
| 401 | 3 | **type** `public record Hit(String key, String name, String kind, int company, int bondId, double price, int priceMon...` | One hit: its key and name, what it is, the company or bond, its price a unit and the month of it, its move over MOVE_MONTHS (a share's, per founding share so a split is no move; NaN when its record is shorter or not p... |
| 417 | 47 | `public static List<Hit> search(Game g, String query, String filter)` | The market, searched (the spec's D1): every listed company, every bond outstanding, and the preferred and warrants while the fund holds them. |
| 465 | 4 | `private static Hit withSection(Hit h, String section)` |  |
| 470 | 5 | `private static boolean startsWith(Hit h, String q)` |  |
| 477 | 16 | `static boolean matches(Hit h, String q)` | Whether a hit answers a query: its name or issuer, "#id", a coupon, a maturity year, or a kind word. |
| 495 | 29 | `public static List<Hit> market(Game g)` | Every instrument the fund could look at: the listed companies, the bonds outstanding, and its own preferred and warrants while it holds them. |
| 531 | 7 | `public static double move(HistorySave h, String company, int months)` | A share's move over the last `months` months, per founding share (the history's), so a split is no move: NaN when either end was not recorded or was recorded under MIN_RECORDED_PRICE (four places of a consolidated sha... |
| 546 | 6 | `public static boolean recordShort(HistorySave h, String company, int months)` | ...and why there is none, when the reason is that the record does not reach back `months` months: a city younger than that (its history has fewer months), or a price whose series began since (an end not recorded at all). |
| 554 | 5 | `public static double[] spark(HistorySave h, String company, int months)` | ...and its last `months` + 1 recorded prices, for a sparkline (NaN where not recorded). |

### the quote (lines 560-771)

| line | len | member | says |
|---:|---:|---|---|
| 568 | 1 | **type** `public record Order(String key, boolean buy, boolean byAmount, double figure, double limit)` | An order the ticket would place: the lot's key, buy or sell, the figure - money on a buy by amount, units (shares or face) otherwise - and the price a unit it is to post at (0: fair value, the rule's own; a bond's: it... |
| 597 | 7 | **type** `public record Quote(boolean buy, double price, double fair, double units, double money, double offeredUnits...` | WHAT THE ORDER WOULD DO, NOW, ON TODAY'S BOOK (the spec's 4.4). |
| 605 | 92 | `public static Quote quote(Game g, Order o)` |  |
| 699 | 72 | `static Quote bondQuote(Game g, Order o)` | ...a bond's: at its value a unit of face, or the price named; no cap (the rule's 30% is of its book, not of the bond). |

### the record (lines 772-839)

| line | len | member | says |
|---:|---:|---|---|
| 782 | 4 | **type** `public record Line(int month, String kind, String group, FundLedger.Activity row, DecisionLog.Entry entry, ...` | One line of the record, newest first: a ledger row, or before the ledger began one of the save's own dated records - a resolution, a FUND or BANK decision - marked `fromLog`. |
| 784 | 1 | `public boolean fromLog()` _(in FundView.Line)_ |  |
| 788 | 8 | `public static String groupOf(String kind)` | The group a ledger row is filed under. |
| 803 | 28 | `public static List<Line> activity(Game g, String group, String key)` | The record, newest first, filtered by group ("All" for every one) and, when `key` is not null, to one lot's rows (a share's market and rescue books together). |
| 833 | 6 | `public static boolean sameLot(String key, String rowKey)` | Whether a row's key is the lot a security page shows: a share's books together. |

