# ShortSellingCheck.java - 1,456 lines · 52 methods · 7 constants · harnesses

`ham/citybuildersim/ShortSellingCheck.java` - generated 2026-11-21 by CodeMap; line numbers are as of that run.

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
>      is the month a city that never saved would have had. FS14: openShort()'s short, held standing (the world's own orders off) through the
>      months it waits for its fee and a dividend.
>   7. HOW OFTEN: over a real run, the months a short opened, the months one was covered and the bubbles the quote stood against, counted - a
>      mechanic that never fires proves nothing. LF1b: and the months the cells traded shares with the world, each month's audit lines for
>      them read against the exchange's figures; SX2 (FS14): in the run's first month (TRADE_ABROAD_MONTH) a cell's sale to the world and
>      purchase from it are caused as section 9 causes them, so those lines are read on a month with both over nought. The 2,000 months'
>      audit, register and shorts-off identities stay played.
>   8. THE FEE ON THE LENDERS' BOOKS (LF1): the households' fee is a pair of lines in the audit, income from the world and banked; the fund's is
>      its own income counter and a row in its ledger; the fee is charged on the shares the lenders hold and no more; emigrants are not dropped
>      while a short is open; a short at a company with no shares in issue takes what it sold with it; a short at a company worth nothing is
>      still called in. LF1c: that cancelling follows the month's order (a company with no book, which the month's listing leaves unlisted), and
>      the two claims it takes across the border, the world's own shares in the company and the short closed for nothing, are valuation lines of
>      the audit ("forgiven" counts them, the books still foot); the Fund screen says a fee's row, a share's write-off and the income a share
>      lot's page holds in words.
>   9. THE CELLS' SHARES ABROAD (LF1b): a cell selling a share to the world and buying one from it, in the real month, are each a pair of
>      lines in the audit, a financial flow and its domestic half, as the cells' bonds are: the month foots, and the balance of payments'
>      financial inflow holds the sale and its outflow the purchase.
> 
> Every claim is CAUSED: a book emptied and given one trade, then the orders a line names and no others; a bid or an ask put on it by a participant
> with the means to stand behind it.

**Uses:** [Exchange](Exchange.md) (110), [Equity](Equity.md) (40), [OrderBook](OrderBook.md) (40), [MarketCheck](MarketCheck.md) (23), [Game](Game.md) (21), [MoneyAudit](MoneyAudit.md) (21), [KeptBench](KeptBench.md) (21), [FundLedger](FundLedger.md) (19), [ExchangeCheck](ExchangeCheck.md) (17), [Household](Household.md) (17), [FundView](FundView.md) (15), [LongPlaytest](LongPlaytest.md) (10), [HouseholdBalance](HouseholdBalance.md) (8), [TreasuryFund](TreasuryFund.md) (4), [TempDirs](TempDirs.md) (2), [GameFiles](GameFiles.md) (2), [BuildingsTemplate](BuildingsTemplate.md) (2), [ForeignAccounts](ForeignAccounts.md) (2), [OwnerHousehold](OwnerHousehold.md) (2), [Sectors](Sectors.md) (1), [Bank](Bank.md) (1), [OrderSearchCheck](OrderSearchCheck.md) (1), [FamilyStructure](FamilyStructure.md) (1), [PayTier](PayTier.md) (1)

**Used by (4):** [FundCheck](FundCheck.md), [FundLedgerCheck](FundLedgerCheck.md), [HoldersCheck](HoldersCheck.md), [RefineryCheck](RefineryCheck.md)

## Sections

| line | section |
|---:|---|
| 150 | THE BENCH (sections 2 to 5): the same market as Table, but valued on its record, with holders to lend and a fund to b... |
| 244 | · 1. the quote |
| 340 | THE LINES OF SECTIONS 2 TO 5 |
| 1324 | THE CELLS' SHARES ABROAD (LF1b), SECTION 9 |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 94 | `ShortSellingCheck.INDUSTRY` | `Equity.indexOf(Sectors.INDUSTRY)` |  |
| 154 | `ShortSellingCheck.W` | `ExchangeCheck.W` |  |
| 155 | `ShortSellingCheck.SHARES` | `1_000, YEARLY = 30` |  |
| 238 | `ShortSellingCheck.LAST` | `1.5` | What the last trade of the lines below was, fair value's $1 being neither it nor the middle of anything they rest. |
| 353 | `ShortSellingCheck.BUBBLE_MONTHS` | `24` |  |
| 1060 | `ShortSellingCheck.RUN_START` | `60, RUN_MONTHS = 1000` |  |
| 1068 | `ShortSellingCheck.TRADE_ABROAD_MONTH` | `RUN_START + 1` | SX2 (FS14): the month of the run in which the cells' trades with the world are caused, as section 9 causes them, the run's first: the ordinary cell holding the most of the company the cells hold most of by value (the ... |

## Fields (state)

| line | field | says |
|---:|---|---|
| 61 | `static int fails` |  |
| 62 | `static int checks` |  |
| 63 | `static PrintStream out` |  |
| 64 | `static PrintStream quiet` |  |
| 83 | `static int sectionStart` |  |
| 102 | `final Equity reg` |  |
| 103 | `final HouseholdBalance town` |  |
| 104 | `final Bank bank` |  |
| 105 | `final Exchange ex` |  |
| 106 | `final String cell` |  |
| 165 | `final MarketCheck.City city` |  |
| 166 | `final Equity reg` |  |
| 167 | `final Exchange ex` |  |
| 168 | `final HouseholdBalance hb` |  |
| 169 | `final Household poor, rich` |  |
| 170 | `final TreasuryFund fund` |  |
| 171 | `final double fair` |  |
| 530 | `final Game city` |  |
| 531 | `final int firm` |  |
| 1104 | `Exchange ex` |  |
| 1105 | `Equity reg` |  |
| 1106 | `int months, auditBad, driftBad, shortCompanyMonths, bubbleMonths, bankBubbleMonths` |  |
| 1107 | `double worstAudit, worstDrift, mostShort` |  |
| 1109 | `int abroadSoldMonths, abroadBoughtMonths, abroadLinesBad` | ...and the cells' share trades with the world (LF1b): the months each was made, what it came to, and the months a line was not its figure. |
| 1110 | `double abroadSold, abroadBought` |  |
| 1112 | `final double[] causedAbroad` | ...and SX2's caused month (TRADE_ABROAD_MONTH): the shares the cell sold the world and bought back, and the company (-1 if none was traded). |
| 1113 | `double causedSold, causedBought` |  |
| 1114 | `String worstWhere` |  |
| 1115 | `long seconds` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 59 | 1398 | **type** `public class ShortSellingCheck` | Verifies a share's price as its quote, and the short selling that brings a bubble down (EM2, 2026-10-30) - each claim on a fixture that causes it, not on a city that happens to show it. |
| 66 | 5 | `static void assertTrue(String label, boolean ok)` |  |
| 72 | 10 | `static void close(String label, double actual, double expected, double tol)` |  |
| 85 | 4 | `static void section(String title)` |  |
| 90 | 3 | `static void sectionEnd()` |  |
| 101 | 48 | **type** `static final class Table` | A one-company market on a table: the register with the company listed (200 shares to each of a hundred households, $1 a share), a bank for the desk, an exchange stepped once at month 1 so that fair value is struck - a... |
| 108 | 6 | `Table()` _(in ShortSellingCheck.Table)_ |  |
| 115 | 1 | `double fair()` _(in ShortSellingCheck.Table)_ |  |
| 118 | 5 | `Table trade(double price)` _(in ShortSellingCheck.Table)_ | One share changes hands at this price, the cell's to the world's bid: the book's last trade, and nothing left resting. |
| 125 | 4 | `Table bid(double price)` _(in ShortSellingCheck.Table)_ | A bid rests at this price: the world's, which can stand behind any. |
| 131 | 4 | `Table ask(double price)` _(in ShortSellingCheck.Table)_ | An ask rests at this price: a cell's, which holds the shares. |
| 137 | 4 | `Table clockAt(int month)` _(in ShortSellingCheck.Table)_ | The exchange's clock moved on to this month, its book left alone. |
| 142 | 1 | `double price()` _(in ShortSellingCheck.Table)_ |  |
| 143 | 1 | `double bestBid()` _(in ShortSellingCheck.Table)_ |  |
| 144 | 1 | `double bestAsk()` _(in ShortSellingCheck.Table)_ |  |
| 145 | 1 | `double lastTrade()` _(in ShortSellingCheck.Table)_ |  |
| 146 | 1 | `boolean noBid()` _(in ShortSellingCheck.Table)_ |  |
| 147 | 1 | `boolean noAsk()` _(in ShortSellingCheck.Table)_ |  |

### THE BENCH (sections 2 to 5): the same market as Table, but valued on its record, with holders to lend and a fund to b... (lines 150-339)

| line | len | member | says |
|---:|---:|---|---|
| 164 | 59 | **type** `static final class Bench` | A thousand shares of one company with a year of earnings on its record, so the world and the cells value it on it (EM1) and the shorts may sell it; a fair value the first step strikes; and holders who say what they ho... |
| 173 | 20 | `Bench(double poorHolds, double richHolds, double worldHolds, double fundHolds, double fundCash)` _(in ShortSellingCheck.Bench)_ |  |
| 194 | 1 | `String poorName()` _(in ShortSellingCheck.Bench)_ |  |
| 195 | 1 | `String richName()` _(in ShortSellingCheck.Bench)_ |  |
| 196 | 1 | `double price()` _(in ShortSellingCheck.Bench)_ |  |
| 197 | 1 | `double lastTrade()` _(in ShortSellingCheck.Bench)_ |  |
| 198 | 1 | `double shortShares()` _(in ShortSellingCheck.Bench)_ |  |
| 201 | 3 | `double holdings()` _(in ShortSellingCheck.Bench)_ | What everybody holds, the cells, the desk, the world and the fund: the shares in issue plus what the world is short, to the drift the register allows. |
| 206 | 5 | `Bench print(double price)` _(in ShortSellingCheck.Bench)_ | One share of the poor cell's changes hands at this price, to the world's bid: the book's last trade, and nothing resting. |
| 213 | 6 | `double open(String buyer, double price, double q)` _(in ShortSellingCheck.Bench)_ | The world sells this many shares short at this price to this buyer's bid: the short's ask rests, the buyer's bid takes it. |
| 221 | 1 | `double shortAsk()` _(in ShortSellingCheck.Bench)_ | The world's short asks at fair value plus SHORT_BAND. |
| 224 | 5 | `static double filled(java.util.List<OrderBook.Fill> fills)` |  |
| 231 | 5 | `static double rateAt(double utilisation)` | The fee a year the lenders are paid at this many shares short of this many lendable (Exchange.lendingRate()), as the dials say. |
| 240 | 99 | `public static void main(String[] args) throws Exception` |  |

### THE LINES OF SECTIONS 2 TO 5 (lines 340-1323)

| line | len | member | says |
|---:|---:|---|---|
| 345 | 7 | `static Bench bubble(boolean shorts)` | A bench with every share in one cell under its cushion, the desk bidding near value and holding none, and one print at 100 times fair value. |
| 355 | 50 | `static void theBubble()` |  |
| 407 | 1 | `static Bench standard()` | A thousand-share bench, the holders as in the sections below: poor 300, rich 400, the world 200 and the fund 100. |
| 409 | 81 | `static void theRegister() throws Exception` |  |
| 496 | 1 | `static Game lineOff(Game g)` | The city with the owner line off (BO1-S): this harness's subject is the shorts, and its towns are young - under OwnerHousehold.OWNER_ADULT_SHARE a founding town of a few adults has an empty owner cell, so its companie... |
| 499 | 21 | `static Game town(String label, int months)` | The playtest's town, as FundLedgerCheck builds it, `months` months old. |
| 521 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 529 | 5 | **type** `static final class Shorted` | A town with the world short of one company (openShort()): the town, and the company's index. |
| 532 | 1 | `Shorted(Game city, int firm)` _(in ShortSellingCheck.Shorted)_ |  |
| 540 | 39 | `static Shorted openShort(String label)` | The playtest's town, 48 months old, with the world short of the company that pays a dividend and has most in the households' hands: the fund comes to hold 4% of it, bought from the cell that holds most, and the world ... |
| 581 | 68 | `static void theCity()` | A whole month of a city, with a short open, a dividend paid on it and the lending fee: the audit balances. |
| 656 | 80 | `static void theWipe(Game city, int firm)` | A company with no shares in issue and no book, with a short open and the world holding some of it, in the real month (LF1c): the month's listing finds no book and leaves it unlisted, the exchange's step cancels what t... |
| 742 | 35 | `static void theWords(Game city, int firm, double feesRowed)` | The Fund screen's words for what the short's lending fee added to the fund's record (LF1c): the row of the month's fees says what it is instead of its kind's name, a share's write-off is not a bond's ("its face gone")... |
| 779 | 9 | `static double lineOf(String detail, String label)` | The number a line of the audit's detail ends in (its label, then the amount at two places), 0 when the month has no such line. |
| 789 | 76 | `static void theLender()` |  |
| 866 | 88 | `static void theCalls()` |  |
| 959 | 33 | `static void sameShort(String when, Game live, Game back, int f, double tol)` | Section 6's two cities, the saved and the loaded, read the same way: every figure of the short the exchange carries (Exchange.State), on the company the world is short of and over all of them, to this tolerance of eac... |
| 993 | 66 | `static void theSave() throws Exception` |  |
| 1071 | 30 | `static java.util.function.Consumer<Boolean> tradeAbroad(Game city, double[] done)` | SX2: the settle probe that makes section 7's caused trades with the world (TRADE_ABROAD_MONTH): [0] the shares sold, [1] bought, [2] the company. |
| 1103 | 14 | **type** `static final class Run` | What a run of the playtest's city counted. |
| 1122 | 3 | `static Run play(boolean shorts, String label)` | The playtest's city (the default founding, its player's rhythm) played to RUN_MONTHS, with the world's shorts on or off, every month audited and every company's register checked: the holdings against the shares in iss... |
| 1127 | 68 | `static Run play(boolean shorts, boolean tradeAbroad, String label)` | ...and with SX2's trades with the world caused in TRADE_ABROAD_MONTH when `tradeAbroad`. |
| 1202 | 121 | `static void theBooks() throws Exception` | Section 8 (LF1): the lending fee on the lenders' books - the fund's counters and its ledger's row - and the four edges the stranger's review found at EM2's gate, each caused by a fixture first: the fee charged on shar... |

### THE CELLS' SHARES ABROAD (LF1b), SECTION 9 (lines 1324-1456)

| line | len | member | says |
|---:|---:|---|---|
| 1336 | 85 | `static void theCellsAbroad()` | Section 9 (LF1b): a cell selling a share to the world and a cell buying one from it, made by the fixture inside the real month (Game.settleProbeForTest: after the exchange's month has opened, before its step and the a... |
| 1422 | 34 | `static void howOften()` |  |

