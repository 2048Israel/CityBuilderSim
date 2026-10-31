# ShortSellingCheck.java - 941 lines · 44 methods · 6 constants · harnesses

`ham/citybuildersim/ShortSellingCheck.java` - generated 2026-10-31 by CodeMap; line numbers are as of that run.

> Verifies a share's price as its quote, and the short selling that brings a bubble down (EM2, 2026-10-30) - each claim on a fixture that
> causes it, not on a city that happens to show it.
> 
> WHY. A share's price was its last trade, and one print far over a thin book stayed the price for as long as nobody traded. Jerus, 2026-10-29:
> "share price should reflect mid price, and if no ask or bid then last traded", over a book where "last traded is 448k but current bids are 4k".
> On main's default playtest Business Services stood 231 months with its last trade over ten times its best bid, and nobody could sell what
> they did not hold, so the high print had no seller to bring it down. The quote is the first half (Exchange.price()); the shorts are the second.
> ExchangeCheck holds what the book does with an order and MarketCheck that the orders are the right ones; this holds the price they are read at,
> and what the shorts do to it.
> 
> WHAT THIS HAS TO PROVE (docs/queue/EM2.md):
> 
>   1. THE QUOTE: the price is the middle of the best bid and the best ask while both rest; with one side empty it is the last trade held inside
>      that side (never over the ask that stands, nor under the bid); with neither it is the last trade; before the first trade it is fair value,
>      held the same way inside a side that stands. A quote set by a bid and an ask, or held inside one, is current however old the last trade:
>      the city marks it, and only a quote that is a last trade older than Exchange.STALE_MARK_MONTHS is marked at fair value.
>   2. A BUBBLE FALLS: one print at a hundred times a share's value over a thin book falls toward value within a stated number of months with
>      the world's shorts and does not without them.
>   3. THE REGISTER IS WHOLE: a short is a position the exchange carries beside the long holdings (the buyer's holding rose as for any
>      purchase, so the holdings add to the shares in issue PLUS what is short), every share short is lent by the cells and the fund's market book
>      pro rata and stays theirs, and the money audit balances through the opening, the carrying and the covering of a short.
>   4. THE LENDER IS PAID: the holder whose shares were lent gets the company's dividend on every share it holds, the short pays the dividend on
>      what is short, and the world pays the lenders the lending fee on the quote the book stood at, more when the share is hard to borrow.
>   5. A MARGIN CALL AND A RECALL COVER: a short whose account is under the maintenance margin buys in all it is short at the market, taking
>      asks up to the most it can pay (the break price), and a short the lenders can no longer lend buys in the difference the same way, each
>      counted; neither pays the break price where the market is under it.
>   6. A SAVE IN THE MIDDLE OF A SHORT: the shorts, their lenders, their collateral and their fees round-trip a save and a load, and the month after
>      is the month a city that never saved would have had.
>   7. HOW OFTEN: over a real run, the months a short opened, the months one was covered and the bubbles the quote stood against, counted - a
>      mechanic that never fires proves nothing.
> 
> Every claim is CAUSED: a book emptied and given one trade, then the orders a line names and no others; a bid or an ask put on it by a participant
> with the means to stand behind it.

**Uses:** [Exchange](Exchange.md) (84), [OrderBook](OrderBook.md) (26), [MarketCheck](MarketCheck.md) (21), [Equity](Equity.md) (18), [Game](Game.md) (15), [ExchangeCheck](ExchangeCheck.md) (9), [LongPlaytest](LongPlaytest.md) (9), [Household](Household.md) (5), [HouseholdBalance](HouseholdBalance.md) (3), [TreasuryFund](TreasuryFund.md) (2), [TempDirs](TempDirs.md) (2), [GameFiles](GameFiles.md) (2), [BuildingsTemplate](BuildingsTemplate.md) (2), [MoneyAudit](MoneyAudit.md) (2), [Sectors](Sectors.md) (1), [Bank](Bank.md) (1), [OrderSearchCheck](OrderSearchCheck.md) (1)

## Sections

| line | section |
|---:|---|
| 133 | THE BENCH (sections 2 to 5): the same market as Table, but valued on its record, with holders to lend and a fund to b... |
| 227 | · 1. the quote |
| 321 | THE LINES OF SECTIONS 2 TO 5 |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 77 | `ShortSellingCheck.INDUSTRY` | `Equity.indexOf(Sectors.INDUSTRY)` |  |
| 137 | `ShortSellingCheck.W` | `ExchangeCheck.W` |  |
| 138 | `ShortSellingCheck.SHARES` | `1_000, YEARLY = 30` |  |
| 221 | `ShortSellingCheck.LAST` | `1.5` | What the last trade of the lines below was, fair value's $1 being neither it nor the middle of anything they rest. |
| 334 | `ShortSellingCheck.BUBBLE_MONTHS` | `24` |  |
| 849 | `ShortSellingCheck.RUN_START` | `60, RUN_MONTHS = 1000` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 44 | `static int fails` |  |
| 45 | `static int checks` |  |
| 46 | `static PrintStream out` |  |
| 47 | `static PrintStream quiet` |  |
| 66 | `static int sectionStart` |  |
| 85 | `final Equity reg` |  |
| 86 | `final HouseholdBalance town` |  |
| 87 | `final Bank bank` |  |
| 88 | `final Exchange ex` |  |
| 89 | `final String cell` |  |
| 148 | `final MarketCheck.City city` |  |
| 149 | `final Equity reg` |  |
| 150 | `final Exchange ex` |  |
| 151 | `final HouseholdBalance hb` |  |
| 152 | `final Household poor, rich` |  |
| 153 | `final TreasuryFund fund` |  |
| 154 | `final double fair` |  |
| 496 | `final Game city` |  |
| 497 | `final int firm` |  |
| 853 | `Exchange ex` |  |
| 854 | `Equity reg` |  |
| 855 | `int months, auditBad, driftBad, shortCompanyMonths, bubbleMonths, bankBubbleMonths` |  |
| 856 | `double worstAudit, worstDrift, mostShort` |  |
| 857 | `String worstWhere` |  |
| 858 | `long seconds` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 42 | 900 | **type** `public class ShortSellingCheck` | Verifies a share's price as its quote, and the short selling that brings a bubble down (EM2, 2026-10-30) - each claim on a fixture that causes it, not on a city that happens to show it. |
| 49 | 5 | `static void assertTrue(String label, boolean ok)` |  |
| 55 | 10 | `static void close(String label, double actual, double expected, double tol)` |  |
| 68 | 4 | `static void section(String title)` |  |
| 73 | 3 | `static void sectionEnd()` |  |
| 84 | 48 | **type** `static final class Table` | A one-company market on a table: the register with the company listed (200 shares to each of a hundred households, $1 a share), a bank for the desk, an exchange stepped once at month 1 so that fair value is struck - a... |
| 91 | 6 | `Table()` _(in ShortSellingCheck.Table)_ |  |
| 98 | 1 | `double fair()` _(in ShortSellingCheck.Table)_ |  |
| 101 | 5 | `Table trade(double price)` _(in ShortSellingCheck.Table)_ | One share changes hands at this price, the cell's to the world's bid: the book's last trade, and nothing left resting. |
| 108 | 4 | `Table bid(double price)` _(in ShortSellingCheck.Table)_ | A bid rests at this price: the world's, which can stand behind any. |
| 114 | 4 | `Table ask(double price)` _(in ShortSellingCheck.Table)_ | An ask rests at this price: a cell's, which holds the shares. |
| 120 | 4 | `Table clockAt(int month)` _(in ShortSellingCheck.Table)_ | The exchange's clock moved on to this month, its book left alone. |
| 125 | 1 | `double price()` _(in ShortSellingCheck.Table)_ |  |
| 126 | 1 | `double bestBid()` _(in ShortSellingCheck.Table)_ |  |
| 127 | 1 | `double bestAsk()` _(in ShortSellingCheck.Table)_ |  |
| 128 | 1 | `double lastTrade()` _(in ShortSellingCheck.Table)_ |  |
| 129 | 1 | `boolean noBid()` _(in ShortSellingCheck.Table)_ |  |
| 130 | 1 | `boolean noAsk()` _(in ShortSellingCheck.Table)_ |  |

### THE BENCH (sections 2 to 5): the same market as Table, but valued on its record, with holders to lend and a fund to b... (lines 133-320)

| line | len | member | says |
|---:|---:|---|---|
| 147 | 59 | **type** `static final class Bench` | A thousand shares of one company with a year of earnings on its record, so the world and the cells value it on it (EM1) and the shorts may sell it; a fair value the first step strikes; and holders who say what they ho... |
| 156 | 20 | `Bench(double poorHolds, double richHolds, double worldHolds, double fundHolds, double fundCash)` _(in ShortSellingCheck.Bench)_ |  |
| 177 | 1 | `String poorName()` _(in ShortSellingCheck.Bench)_ |  |
| 178 | 1 | `String richName()` _(in ShortSellingCheck.Bench)_ |  |
| 179 | 1 | `double price()` _(in ShortSellingCheck.Bench)_ |  |
| 180 | 1 | `double lastTrade()` _(in ShortSellingCheck.Bench)_ |  |
| 181 | 1 | `double shortShares()` _(in ShortSellingCheck.Bench)_ |  |
| 184 | 3 | `double holdings()` _(in ShortSellingCheck.Bench)_ | What everybody holds, the cells, the desk, the world and the fund: the shares in issue plus what the world is short, to the drift the register allows. |
| 189 | 5 | `Bench print(double price)` _(in ShortSellingCheck.Bench)_ | One share of the poor cell's changes hands at this price, to the world's bid: the book's last trade, and nothing resting. |
| 196 | 6 | `double open(String buyer, double price, double q)` _(in ShortSellingCheck.Bench)_ | The world sells this many shares short at this price to this buyer's bid: the short's ask rests, the buyer's bid takes it. |
| 204 | 1 | `double shortAsk()` _(in ShortSellingCheck.Bench)_ | The world's short asks at fair value plus SHORT_BAND. |
| 207 | 5 | `static double filled(java.util.List<OrderBook.Fill> fills)` |  |
| 214 | 5 | `static double rateAt(double utilisation)` | The fee a year the lenders are paid at this many shares short of this many lendable (Exchange.lendingRate()), as the dials say. |
| 223 | 97 | `public static void main(String[] args) throws Exception` |  |

### THE LINES OF SECTIONS 2 TO 5 (lines 321-941)

| line | len | member | says |
|---:|---:|---|---|
| 326 | 7 | `static Bench bubble(boolean shorts)` | A bench with every share in one cell under its cushion, the desk bidding near value and holding none, and one print at 100 times fair value. |
| 336 | 50 | `static void theBubble()` |  |
| 388 | 1 | `static Bench standard()` | A thousand-share bench, the holders as in the sections below: poor 300, rich 400, the world 200 and the fund 100. |
| 390 | 73 | `static void theRegister() throws Exception` |  |
| 465 | 21 | `static Game town(String label, int months)` | The playtest's town, as FundLedgerCheck builds it, `months` months old. |
| 487 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 495 | 5 | **type** `static final class Shorted` | A town with the world short of one company (openShort()): the town, and the company's index. |
| 498 | 1 | `Shorted(Game city, int firm)` _(in ShortSellingCheck.Shorted)_ |  |
| 506 | 39 | `static Shorted openShort(String label)` | The playtest's town, 48 months old, with the world short of the company that pays a dividend and has most in the households' hands: the fund comes to hold 4% of it, bought from the cell that holds most, and the world ... |
| 547 | 36 | `static void theCity()` | A whole month of a city, with a short open, a dividend paid on it and the lending fee: the audit balances. |
| 584 | 76 | `static void theLender()` |  |
| 661 | 88 | `static void theCalls()` |  |
| 754 | 33 | `static void sameShort(String when, Game live, Game back, int f, double tol)` | Section 6's two cities, the saved and the loaded, read the same way: every figure of the short the exchange carries (Exchange.State), on the company the world is short of and over all of them, to this tolerance of eac... |
| 788 | 60 | `static void theSave() throws Exception` |  |
| 852 | 8 | **type** `static final class Run` | What a run of the playtest's city counted. |
| 865 | 52 | `static Run play(boolean shorts, String label)` | The playtest's city (the default founding, its player's rhythm) played to RUN_MONTHS, with the world's shorts on or off, every month audited and every company's register checked: the holdings against the shares in iss... |
| 918 | 23 | `static void howOften()` |  |

