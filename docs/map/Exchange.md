# Exchange.java - 2,498 lines · 194 methods · 35 constants · model

`ham/citybuildersim/Exchange.java` - generated 2026-10-30 by CodeMap; line numbers are as of that run.

> The stock exchange: one order book per company, where every share that
> changes hands meets its buyer, and the price is the quote: the middle of the book (EM2).
> 
> ==================== WHY ====================
> 
> The register (Equity) made the city's companies owned. It left every share
> where its first buyer put it: Industry, sold to the world in month 4 when
> the households had six weeks of pay, was foreign-owned for ever; a family
> that left the city took its shares and was paid abroad for 333 years;
> households whose companies had bid up nothing could buy nothing. Jerus:
> "we are to add the exchange now, that will solve the return."
> 
> ==================== THE BANK WAS THE DEALER, UNTIL 0.7.12 ROUND 2 ====================
> 
> The first exchange (0.7.2 to 0.7.12 round 1) made every trade a trade with
> the bank's desk: it quoted a bid and an ask around fair value, took the
> other side of whatever came, and moved its quote with its inventory -
> mid = fair x (1 - PRESSURE x (inventory - unfilled demand) / limit). A
> seller always found a buyer at the desk's bid, and the desk's limits were
> the market's. Jerus, 2026-09-24: "Order book for both" - the bonds went
> onto OrderBook in round 1, and the shares follow in round 2 (the brief:
> the dealer's quote, PRESSURE and the desk's inventory limits are replaced
> by the book; the desk becomes one participant; everything that trades
> shares posts orders).
> 
> ==================== THE BOOK ====================
> 
> One OrderBook per company: price-time priority, a trade at the resting
> order's price, nobody obliged to trade, no trade with oneself. A
> company's PRICE is its quote since EM2 - the middle of the book, or the
> last trade held inside the side that stands (price()) - and its fair value - the register's
> reckoning, Equity.fairValue(): book, or the dividend it actually pays
> capitalised at what the world asks, whichever is more - is beside it,
> not in it. Before its first trade a company is priced at its fair value.
> 
> THE ORDERS ARE GOOD FOR A MONTH, as the bonds' are: at the exchange's step
> (takeMonth(), after the dividends) every order still resting is
> withdrawn - the sellers who waited are counted - and every participant
> posts again from its view of the month. Between two steps the book
> stands, and a household short of money in the next month's waterfall
> sells into what rests there.
> 
> ==================== WHO POSTS, AND WHAT ====================
> 
> In this order at the step, every company in register order within each,
> so an order that crosses what an earlier participant posted trades at
> that resting price. Each rule is the one the participant followed with
> the dealer, applied to a price on the book instead of a quote:
> 
>   - THE DESK, the bank's trading desk, makes a market if it has capital:
>     a bid at fair value less half SPREAD for what its capital carries
>     (Bank.deskCanCarry(), the 0.7.8 limit, Basel's market-risk requirement
>     and the leverage target, and under its two caps since round 3), an ask
>     AT fair value for what it holds over those caps (round 4), and one at
>     fair value plus half SPREAD for the rest of what it holds - never what
>     it does not. The bank's own shares are its capital, not its trading:
>     it bids for them only with what it holds over its target
>     (Bank.buybackRoom(), "buybacks only from spare capital") and asks new
>     ones only while under it (Bank.issuesOwnShares()), each at the buyback
>     pace; bought, they are cancelled, and sold, issued.
> ... (61 more lines in the source)

**Uses:** [OrderBook](OrderBook.md) (71), [Equity](Equity.md) (64), [TreasuryFund](TreasuryFund.md) (15), [Household](Household.md) (14), [HouseholdBalance](HouseholdBalance.md) (9), [Bank](Bank.md) (8), [OutwardInvestment](OutwardInvestment.md) (5), [BondMarket](BondMarket.md) (3), [FundLedger](FundLedger.md) (1), [SaveCopy](SaveCopy.md) (1)

**Used by (24):** [BankCheck](BankCheck.md), [BankScreen](BankScreen.md), [DataSave](DataSave.md), [Equity](Equity.md), [ExchangeCheck](ExchangeCheck.md), [FundCheck](FundCheck.md), [FundLedger](FundLedger.md), [FundLedgerCheck](FundLedgerCheck.md), [FundScreen](FundScreen.md), [FundView](FundView.md), [Game](Game.md), [HistoryCheck](HistoryCheck.md), [HistorySave](HistorySave.md), [HouseholdBalance](HouseholdBalance.md), [LongPlaytest](LongPlaytest.md), [MarketCheck](MarketCheck.md), [PeopleScreen](PeopleScreen.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [SaveSlotCheck](SaveSlotCheck.md), [SectorBooks](SectorBooks.md), [SectorScreen](SectorScreen.md), [ShortSellingCheck](ShortSellingCheck.md), [TreasuryFund](TreasuryFund.md)

## Sections

| line | section |
|---:|---|
| 131 | · the dials |
| 169 | AND A DEAD BAND ON THE LINE ITSELF (2026-09-13) |
| 187 | AND A DEAD BAND ON THE RANKING (2026-09-15) |
| 208 | THE WORLD SELLS SHORT, AND THE CITY'S HOLDERS LEND (EM2, 2026-10-30) |
| 269 | THE DESK'S OWN CAPS ON SHARES (0.7.12 round 3; Jerus: "Put them back") |
| 362 | WHAT COUNTS AS A DEALER HAVING CAPITAL (2026-09-13) |
| 390 | · the participants, on the book |
| 411 | · the state |
| 527 | THE PRICES |
| 695 | THE MONTH |
| 861 | · the desk |
| 1025 | · the emigrants |
| 1037 | · the world |
| 1100 | · the shorts (EM2) |
| 1222 | · the companies |
| 1284 | · the households |
| 1507 | · the city's fund (0.7.14) |
| 1639 | · the cap and the hand's orders (0.7.39) |
| 1717 | A HOUSEHOLD SHORT OF MONEY SELLS, mid-month, in the waterfall |
| 1774 | one order, settled |
| 2056 | SPLITS |
| 2091 | · reading |
| 2232 | · saving |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 134 | `Exchange.SPREAD` | `.02` | The desk's ask over its bid, as a share of fair value. |
| 137 | `Exchange.STALE_MARK_MONTHS` | `12` | A share's last trade marks the city's holding for a year; older, the register's own fair value, which the rule asks at and bids TreasuryFund.RULE_PREMIUM over (C4). |
| 140 | `Exchange.FOREIGN_SPEED` | `.10` | What the world moves in a month per unit of yield over or under its hurdle, as a share of the float. |
| 143 | `Exchange.FOREIGN_TOLERANCE` | `.10` | ...and it holds still while the yield is within this of the hurdle. |
| 146 | `Exchange.HOUSEHOLD_PREMIUM` | `.01` | ...into a return at least this far over the best of the deposit rate, the dollars abroad and the city's paper, annual. |
| 156 | `Exchange.BAND` | `.05` | A holder trades on a share's value only past this either side of it (EM1, 2026-10-29): it sells when the bid is over value by more, or its expected return is under the best alternative by more, and it buys when the as... |
| 164 | `Exchange.BUYBACK_CUSHION_MONTHS` | `6` | A company keeps this many months of its costs before it buys back: operating cost and, since round 2 of 0.7.11, its debt service - the interest and the principal it repaid (Companies.monthlyDebtService()). |
| 167 | `Exchange.OVER_TARGET` | `.10` | Equity this far past target before a company buys back, as a share of assets. |
| 185 | `Exchange.MIN_EXCESS` | `1e-9` |  |
| 200 | `Exchange.TIED_YIELD` | `1e-9` |  |
| 203 | `Exchange.BUYBACK_PACE` | `.10` | The most a company retires in a year, as a share of what it is worth. |
| 206 | `Exchange.BUYBACK_TOLERANCE` | `.10` | A company bids for its own shares up to this far over fair value, and rests its bid there; past it, it waits and the money stays in the till (round 4). |
| 249 | `Exchange.SHORT_BAND` | `.20` | The world sells short only at this far over fair value; over BUYBACK_TOLERANCE, so a short never sells into a company's bid for its own shares, and past it a share's price is over what its business and money are worth... |
| 252 | `Exchange.INITIAL_MARGIN` | `1.5` | The collateral held against a short at its sale, a multiple of the sale: the proceeds and half as much again (Regulation T). |
| 255 | `Exchange.MAINTENANCE_MARGIN` | `.30` | The account's equity under this share of the short's market value is a margin call (FINRA Rule 4210 for shorts): the short covers at the most it can pay. |
| 258 | `Exchange.MAX_SHORT_INTEREST` | `.20` | The most of a company's outstanding shares that may be short at once. |
| 261 | `Exchange.LENDING_FEE` | `.005` | The general fee a year on the shares lent, at their value: the market's rate is .3% to 1%. |
| 264 | `Exchange.HARD_TO_BORROW` | `.50` | The share of the lendable shares lent past which a share is hard to borrow... |
| 267 | `Exchange.HARD_TO_BORROW_FEE` | `.10` | ...and the fee a year it adds at full utilisation, linearly from HARD_TO_BORROW. |
| 333 | `Exchange.POSITION_LIMIT` | `.25` | The most of the bank's equity the desk holds in any one company's shares, at fair value: a trading book's single-name limit (see above). |
| 344 | `Exchange.BOOK_LIMIT` | `.50` | ...and in all companies' together: the book's aggregate limit. |
| 347 | `Exchange.SPLIT_AT` | `100` | A share priced at this many times its founding price is split; at one over it, consolidated. |
| 357 | `Exchange.MIN_FAIR` | `1e-9` | The least a share may be worth before the market treats the company as worthless. |
| 373 | `Exchange.MIN_DEALER_EQUITY` | `1.0` |  |
| 393 | `Exchange.DESK` | `"bank"` | The bank's trading desk. |
| 395 | `Exchange.WORLD` | `"world"` | The world's investors. |
| 397 | `Exchange.WORLD_SHORT` | `"world, short"` | The world's short seller (EM2): a participant of its own, so a short and the world's long never trade with each other, and never a cell's name (no BondMarket.CELL prefix). |
| 399 | `Exchange.EMIGRANTS` | `"emigrants"` | The month's leavers, selling on the way out. |
| 401 | `Exchange.CELL` | `BondMarket.CELL` | One household cell: this, then its key (the bond market's prefix, deliberately). |
| 403 | `Exchange.FUND` | `BondMarket.FUND` | The city's fund, by its rule (0.7.14; TreasuryFund) - the bond market's name, deliberately. |
| 405 | `Exchange.FUND_HAND` | `BondMarket.FUND_HAND` | ...and by the player's hand: its own name, so the two never trade with each other and the hand's sale can reach the rescue book. |
| 970 | `Exchange.BOUND_CAPITAL` | `0, BOUND_POSITION = 1, BOUND_BOOK = 2, BOUND_FLOAT = 3` | Which of the desk's limits binds its bid: its capital, POSITION_LIMIT, BOOK_LIMIT, or the company's float. |
| 1838 | `Exchange.BY_DESK` | `0, BY_WORLD = 1, BY_EMIGRANTS = 2, BY_HOUSEHOLDS = 3, BY_FUND = 4` | Who sells, by class: the desk, the world, the leavers, a household cell, and since 0.7.14 the city's fund - for the fill rate by seller. |
| 2235 | `Exchange.SLOTS_BEFORE_SPLITS` | `3` | Slots per company in the old dealer's array before the split factor joined (the exchange's first night): its quote, fair value and demand. |
| 2237 | `Exchange.SLOTS` | `SLOTS_BEFORE_SPLITS + 1` | ...and after: the quote, fair value, demand and split factor, four a company - what a save from before 0.7.12 round 2 carries (restore(String[], double[])). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 360 | `private double minFair` | The same floor in today's money. |
| 376 | `private double minDealerEquity` | The same floor in today's money. |
| 413 | `private final int n` |  |
| 414 | `private OrderBook[] books` |  |
| 415 | `private final double[] fair` | the register's reckoning, per share, struck at the step |
| 423 | `private final double[] spare` | What fair value was struck on, per company, at the step and saved (EM1): its money past the cushion (total, never under nothing), its net income over the last year (total) and what its money earns it a year (total). |
| 425 | `private final double[] emigrantShares` | This month's leavers' shares still offered: held abroad by the register, sold by EMIGRANTS. |
| 427 | `private final double[] buybackBudget` | What each company's buyback bid may still spend, money; the rest of its month's programme. |
| 433 | `private final double[] splitFactor` | Shares today per share at the founding, per company: the product of every split and consolidation - what makes a price HISTORY possible (pricePerFoundingShare()). |
| 434 | `private boolean deskOpen` |  |
| 437 | `private final double[] soldToHouseholds` | this month, cash unless it says shares; cleared by startMonth() |
| 438 | `private final double[] boughtFromHouseholds` | desk -> cells |
| 439 | `private final double[] soldAbroad` | cells -> desk |
| 440 | `private final double[] boughtFromAbroad` | desk -> the world |
| 441 | `private final double[] emigrantsPaid` | the world or emigrants -> desk |
| 442 | `private final double[] buybackToHouseholds` | emigrants, from anybody |
| 443 | `private final double[] buybackToDesk` | cells -> the company |
| 444 | `private final double[] buybackAbroad` | desk -> the company |
| 445 | `private final double[] householdsBoughtAbroad` | the world or emigrants -> the company |
| 446 | `private final double[] householdsSoldAbroad` | the world or emigrants -> cells |
| 447 | `private final double[] betweenHouseholds` | cells -> the world |
| 449 | `private final double[] fundBoughtFromHouseholds` | cell -> cell ...and the city's fund (0.7.14): a pool, so what crosses is with the cells and the world |
| 450 | `private final double[] fundBoughtAbroad` | cells -> the fund |
| 451 | `private final double[] fundSoldToHouseholds` | the world or emigrants -> the fund |
| 452 | `private final double[] fundSoldAbroad` | the fund -> cells |
| 453 | `private final double[] volume` | the fund -> the world |
| 454 | `private final double[] split` | shares |
| 455 | `private final int[] betweenHouseholdsTrades` | this month's split factor, 0 if none |
| 456 | `private double ownBoughtThisMonth, ownSoldThisMonth` | the bank's own shares, for the pace |
| 460 | `private boolean shortSelling` | Whether the world sells short at all: on, the default; the checks and -Dplaytest.shorts=off switch it off to see the market without it. |
| 462 | `private final double[] shortShares` | Shares the world has sold short, per company: the S of the rule. |
| 464 | `private final double[] shortCollateral` | The collateral held against them, money: INITIAL_MARGIN times each sale, released pro rata as they are covered. |
| 466 | `private final double[] lentCells` | Of the shares short, those the cells lend and those the fund's market book lends: struck at the step and again at each fill of a short. |
| 468 | `private final double[] quoteBefore` | The quote of the last step's books before it withdrew them (the fee is charged on it), per company with a short. |
| 470 | `private final double[] feeToCells` | The month's lending fee, money, by who was paid: this month's, cleared by startMonth(). |
| 472 | `private double[] restoredShortPays` | What a load read of the short's dividends over the city's life, per company, until attach() hands it to the register (EM2). |
| 474 | `private double lifeShortSold, lifeShortSoldValue, lifeShortCovered, lifeShortCoveredValue, lifeLendingFees` | Over the city's life: shares sold short and their value, covered and their value, company-months an ask was posted and a short was open, margin calls and recalls, fees paid. |
| 475 | `private int lifeShortAsks, lifeShortMonths, lifeMarginCalls, lifeRecalls` |  |
| 478 | `private double lastPostedSellValue, lastFilledValue` | the last step's books, all companies, and over the city's life - for the screens and the playtest |
| 479 | `private int lastSellsPosted, lastSellsWaited, lastTrades` |  |
| 480 | `private double lifetimeVolume, lifePostedSellValue, lifeFilledValue, lifeBetweenHouseholds, lifeTurnover` |  |
| 481 | `private double lifeEmigrantsOffered, lifeEmigrantsPaid` |  |
| 482 | `private int lifeSellsPosted, lifeSellsWaited, lifeTrades, lifeBetweenHouseholdsTrades` |  |
| 485 | `private Equity register` | Who the book settles against, set at the step and read by every fill until the next. |
| 486 | `private HouseholdBalance households` |  |
| 487 | `private Bank dealer` |  |
| 488 | `private Companies companies` |  |
| 489 | `private int month` |  |
| 491 | `private TreasuryFund fund` | The city's fund (0.7.14), attached at the founding and on the load path; null in a fixture with none. |
| 493 | `private double fundBondsValue` | What the fund's bonds are worth at this step, for its 70/30 (BondMarket.valueHeld()). |
| 917 | `private final double[] lastExcessOffered` | What the desk offered over its caps at the last step, shares (round 4). |
| 919 | `private double lifeExcessOffered, lifeExcessSold` | ...and over the city's life, at fair value when offered, and what of it sold, at the price paid. |
| 1299 | `private double lifeRebalanceSold` | What the cells asked to rebalance over the city's life, at the price: for the playtest (round 3). |
| 1303 | `private double lifeValueSold` | What the cells asked to sell on a share's value over the city's life, at the price (EM1): a bid over value, or a return under the alternatives. |
| 1472 | `private Household buying` | The cell buying at the step right now, and what it may still spend this month. |
| 1473 | `private double buyingBudget` |  |
| 1771 | `private Household selling` | The cell selling in the waterfall right now, whose proceeds go back to its waterfall rather than into its savings. |
| 1772 | `private double raised` |  |
| 1852 | `private final double[] lifePostedSellBy` | Over the city's life, by seller class: what was offered for sale and what of it sold, both in shares at the company's price when it happened - so the ratio is a fill rate, as the book's own is, and not a comparison of... |
| 1855 | `private final int[] lastDeskBound` | Company-months each of the desk's limits held its bid, in BOUND_ order: the last step's and the city's life (round 3). |
| 1867 | `final int c` |  |
| 2247 | `String[] keys` |  |
| 2248 | `List<OrderBook> books` |  |
| 2249 | `double[] fair, splitFactor, emigrantShares, buybackBudget` |  |
| 2251 | `double[] spare, earnings, moneyIncome` | What fair value was struck on (EM1): each company's spare money, its net income over the last year and what its money earns it, totals. |
| 2258 | `double[] shortShares, shortCollateral, lentCells, lentFund, feeToCells, feeToFund, shortPays` | The world's short and who lends it (EM2), per company in keys' order: the shares short, the collateral held against them (money), the shares the cells and the fund lend, and the month's lending fee each was paid (mone... |
| 2260 | `double lifeShortSold, lifeShortSoldValue, lifeShortCovered, lifeShortCoveredValue, lifeLendingFees` | ...and over the city's life: shares sold short and their value, covered and their value, the fees paid, the company-months an ask was posted and a short stood open, the margin calls and the recalls. |
| 2261 | `int lifeShortAsks, lifeShortMonths, lifeMarginCalls, lifeRecalls` |  |
| 2262 | `double[] life` |  |
| 2263 | `double[] last` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 129 | 2370 | **type** `public class Exchange` | The stock exchange: one order book per company, where every share that changes hands meets its buyer, and the price is the quote: the middle of the book (EM2). |

### the dials (lines 131-168)

### AND A DEAD BAND ON THE LINE ITSELF (2026-09-13) (lines 169-186)

### AND A DEAD BAND ON THE RANKING (2026-09-15) (lines 187-207)

### THE WORLD SELLS SHORT, AND THE CITY'S HOLDERS LEND (EM2, 2026-10-30) (lines 208-268)

### THE DESK'S OWN CAPS ON SHARES (0.7.12 round 3; Jerus: "Put them back") (lines 269-361)

### WHAT COUNTS AS A DEALER HAVING CAPITAL (2026-09-13) (lines 362-389)

| line | len | member | says |
|---:|---:|---|---|
| 379 | 5 | `public void seedConstants(double unit)` | Re-seeds the floors at a given unit. |
| 386 | 3 | `private boolean hasDealerCapital(Bank bank)` | Whether the bank has capital enough to make a market at all. |

### the participants, on the book (lines 390-410)

| line | len | member | says |
|---:|---:|---|---|
| 409 | 1 | `static boolean isFund(String who)` | True for either of the fund's names. |

### the state (lines 411-526)

| line | len | member | says |
|---:|---:|---|---|
| 496 | 1 | `public void attachFund(TreasuryFund fund)` | The city's fund, which posts on the book by its rule and its hand (0.7.14). |
| 498 | 5 | `public Exchange()` |  |
| 505 | 4 | `public void startMonth(int month)` | Clears the month's flows, in this month. |
| 511 | 15 | `public void startMonth()` | Clears the month's flows. |

### THE PRICES (lines 527-694)

| line | len | member | says |
|---:|---:|---|---|
| 553 | 10 | `public double price(int c)` | A SHARE'S PRICE IS ITS QUOTE (EM2, 2026-10-30): the middle of its book while a bid and an ask both rest; with one side empty, the last trade - never over the ask that stands, nor under the bid; with neither, the last ... |
| 565 | 9 | `public boolean quoteIsLastTrade(int c)` | True when the quote is the book's last trade as it stands: no side resting that sets or bounds it (EM2) - the one quote that can grow stale. |
| 580 | 8 | `public String quoteWords(int c)` | What a share's price is made of now, in words, for the caption beside it (EM2): "the middle of the bid and the ask" while both rest; with one side standing the last trade "held inside the ask" or "held over the bid"; ... |
| 590 | 1 | `public double lastTrade(int c)` | The book's last trade, or NaN before the first: what the screens show beside the quote (EM2). |
| 593 | 1 | `public boolean hasTraded(int c)` | True once a share of this company has traded on the book. |
| 606 | 1 | `public double cityMark(int c)` | WHAT THE CITY'S HOLDING OF A SHARE IS MARKED AT (0.7.48, C4): its quote while that is current, and fair value once the quote is a last trade older than STALE_MARK_MONTHS - a price nobody has paid for a year says nothi... |
| 609 | 3 | `public boolean markedAtFair(int c)` | ...true when that is fair value: a quote that is the last trade, older than STALE_MARK_MONTHS. |
| 614 | 1 | `public double fair(int c)` | The register's reckoning of a share, struck at the step: its money past the cushion at face and its earnings capitalised (Equity.fairValue(), EM1). |
| 617 | 1 | `public double spareOf(int c)` | A company's money past its cushion at the last step, total: its till, its dollars abroad and its bonds, less six months of its costs, never under nothing (EM1). |
| 619 | 1 | `public double earningsOf(int c)` | ...its net income over the last year, total: what its business earned, after tax (EM1). |
| 621 | 1 | `public double moneyIncomeOf(int c)` | ...and what its money earned it over the last year, total: deposit interest, the coupon on its dollars abroad (after its tax) and its bonds' coupons, the month's twelve times (EM1, HD2). |
| 624 | 5 | `public double fairSpare(int c)` | The money past the cushion a share carries in its fair value: the spare over the shares in issue; none until the company is valued on its record (EM1). |
| 636 | 5 | `public double expectedReturn(int c, double price)` | A share's expected return at a price, annual: its earnings plus what its money earns, over its price (EM1) - the yardstick every holder reads, against the other shares, the deposit rate, the dollars abroad and the cit... |
| 643 | 1 | `public double bestBid(int c)` | The best bid resting, or NaN with none. |
| 645 | 1 | `public double bestAsk(int c)` | The best ask resting, or NaN with none. |
| 648 | 1 | `public double mark(int c)` | What the desk carries a share at: the last trade or fair value, whichever is lower - a dealer does not mark its own book up on a price nobody has paid. |
| 651 | 1 | `public OrderBook bookOf(int c)` | The book a company's shares trade on. |
| 654 | 1 | `public boolean isOpen()` | True while the bank's desk has capital to post on the book (hasDealerCapital()); the book is open to everybody else regardless. |
| 663 | 3 | `public boolean quoteSupportsIssue(int c)` | Whether a company would sell new shares at today's price: not while the market has them under fair value by more than the tolerance. |
| 668 | 3 | `public double yieldAt(Equity register, int c, double price)` | Dividend yield, annual, at a price: the dividend the register says it actually pays (Equity.dividendPerShareAnnual(), since 0.7.12 round 2). |
| 673 | 5 | `public double markToMarket(Equity register)` | What the desk holds, at its mark: the bank's securities line. |
| 680 | 5 | `public double marketValueOfHouseholds(Equity register, HouseholdBalance households)` | What the city's households hold, at the price. |
| 686 | 1 | `public double marketCap(Equity register, int c)` |  |
| 689 | 5 | `public double deskCanBuy(Equity register, int c)` | The desk's bid capacity in this company now, shares: what its capital carries (Bank.deskCanCarry()), and of its own shares what it holds over target at the pace. |

### THE MONTH (lines 695-860)

| line | len | member | says |
|---:|---:|---|---|
| 700 | 27 | **type** `public interface Companies` | What the month's trading needs to know about a company that is not on the register. |
| 702 | 1 | `double cashAvailable(int company, double wanted)` _(in Exchange.Companies)_ | Cash in the company's till, after anything it can bring home for this. |
| 704 | 1 | `double till(int company)` _(in Exchange.Companies)_ | ...and what is in its till now, bringing nothing home: what a resting buyback bid may still pay. |
| 706 | 1 | `void payBuyback(int company, double cash)` _(in Exchange.Companies)_ | Debits the company's till for a buyback. |
| 707 | 1 | `double assets(int company)` _(in Exchange.Companies)_ |  |
| 708 | 1 | `double equity(int company)` _(in Exchange.Companies)_ |  |
| 709 | 1 | `double monthlyOperatingCost(int company)` _(in Exchange.Companies)_ |  |
| 716 | 1 | `double monthlyDebtService(int company)` _(in Exchange.Companies)_ | ...and what its debt costs it a month: the interest on its statement and the principal it repaid (0.7.11, round 2). |
| 723 | 1 | `default double money(int company)` _(in Exchange.Companies)_ | ...and the money it holds, at face (EM1): its till, its dollars abroad and the other sectors' bonds. |
| 725 | 1 | `default double moneyIncome(int company)` _(in Exchange.Companies)_ | ...and what that money earns it a year at the month's rates: deposit interest, the coupon on its dollars abroad after its tax (on its statement since HD2, taken out of the earnings) and its bonds' coupons - so not in ... |
| 729 | 4 | `private double cushion(int c)` | What a company keeps in its till before it buys back or counts its money spare: BUYBACK_CUSHION_MONTHS of its operating cost and its debt service (0.7.11, round 2). |
| 743 | 5 | `public void takeMonth(Equity register, HouseholdBalance households, Bank bank, Companies companies, double[] book, double world...` | The exchange's step: last month's orders withdrawn, every share valued, and every participant's orders posted - the desk, the emigrants, the world, the companies, the household cells - then the splits, and the bank's ... |
| 750 | 5 | `public void takeMonth(Equity register, HouseholdBalance households, Bank bank, Companies companies, double[] book, double world...` | ...with what the city's fund's bonds are worth this month (0.7.14); no yield on the city's paper (a fixture with none). |
| 763 | 56 | `public void takeMonth(Equity register, HouseholdBalance households, Bank bank, Companies companies, double[] book, double world...` | ...with what the city's fund's bonds are worth this month (0.7.14), for its mix: its orders are posted last, after the households', by its rule and then its hand (postFund()); and what the city's paper the households ... |
| 825 | 8 | `public void attach(Equity register, HouseholdBalance households, Bank bank, Companies companies, int month)` | Who the book settles against from now until the next step. |
| 839 | 5 | `private void handShortPaysToRegister()` | What the short has paid at the dividends over the city's life is the register's (Equity.getLifetimeShortPays()) and is saved here with the rest of the short (EM2): a load reads it into restoredShortPays, and the regis... |
| 846 | 14 | `private void noteBook(int c, OrderBook b)` | Adds a book's month, as the step closed it, to the market's record. |

### the desk (lines 861-1024)

| line | len | member | says |
|---:|---:|---|---|
| 864 | 27 | `private void postDesk()` | The desk makes a market around fair value, within its capital; its own shares only by its capital policy. |
| 899 | 16 | `public double[] deskExcess(Equity register)` | What the desk holds over its caps, shares, company by company (round 4): the larger of what is over POSITION_LIMIT in that company and its pro-rata part, by value at fair value, of what the book is over BOOK_LIMIT - b... |
| 920 | 1 | `public double getLastExcessOffered(int c)` |  |
| 921 | 1 | `public double getLifeExcessOffered()` |  |
| 922 | 1 | `public double getLifeExcessSold()` |  |
| 925 | 5 | `public double deskExcessOnOffer(int c)` | What of the desk's excess still rests on the book, at fair value, in one company - its asks at fair value (round 4); the Bank tab's figure. |
| 931 | 5 | `public double deskExcessOnOffer()` | ...in every company. |
| 937 | 3 | `private boolean isExcessPrice(int c, double price)` | An ask of the desk's at fair value, not at its ask half a spread over: the excess. |
| 956 | 12 | `private double deskCapacity(Equity register, int c, double price)` | What the desk can buy at this price, shares: of another company, what its bank's capital carries at the weight its book gives the desk (Bank.deskCanCarry(), the inventory at the mark) and never more than is not alread... |
| 978 | 9 | `private double[] deskLimits(Equity register, int c, double price)` | The desk's four limits in another company's shares at this price, shares, in BOUND_ order (round 3): what its capital carries (0.7.8), its room under POSITION_LIMIT in this company and under BOOK_LIMIT over all of the... |
| 989 | 5 | `private double bookAtFair(Equity register)` | What the desk holds of every other company, at fair value: what BOOK_LIMIT reads. |
| 996 | 4 | `public double deskPositionShare(Equity register, int c)` | The desk's holding of one company at fair value over the bank's equity, and its whole book's - for the screens and the playtest. |
| 1000 | 4 | `public double deskBookShare(Equity register)` |  |
| 1006 | 7 | `private double deskHolding(int c)` | What the desk can sell of this company: what it holds - and of the bank's own, new shares at the pace while the bank is under its target. |
| 1020 | 4 | `private static boolean ownSharesTrade(Equity register)` | ...AND NEVER ON A RECORD THAT IS NEW OR BAD - the register's own reading of the bank's last twelve months (Equity.getRegime()): a bank in its first year, or losing money in half the months of one, neither buys its own... |

### the emigrants (lines 1025-1036)

| line | len | member | says |
|---:|---:|---|---|
| 1028 | 8 | `private void postEmigrants()` | The month's leavers ask what the dealer's bid at fair value used to pay them. |

### the world (lines 1037-1099)

| line | len | member | says |
|---:|---:|---|---|
| 1050 | 24 | `private void postWorld(double worldRate)` | The world, on a share's expected return against its rate plus the premium (EM1: it read the dividend's yield before): a bid up to the price that returns the hurdle plus the tolerance - and never over fair value plus h... |
| 1085 | 9 | `private void postWorldPrice()` | THE WORLD SELLS INTO A BID OVER VALUE (EM1, 2026-10-29): where a bid rests more than BAND over a share's fair value and the world holds some, it sells OutwardInvestment.HOME_SPEED of what it holds at fair value plus B... |
| 1096 | 3 | `private double worldHolding(int c)` | What the world holds that it can sell: what is held abroad less this month's leavers' shares, which are theirs to sell. |

### the shorts (EM2) (lines 1100-1221)

| line | len | member | says |
|---:|---:|---|---|
| 1103 | 5 | `private double lendable(int c)` | What the city's long holders can lend of a company now, shares: the cells' holding and the fund's market book (never its rescue book). |
| 1110 | 3 | `private double shortRoom(int c)` | What the world may still sell short of a company, shares: under MAX_SHORT_INTEREST of what is outstanding and under what the lenders have left to lend. |
| 1120 | 4 | `public static double lendingRate(double utilisation)` | The fee a year on the shares lent, at a utilisation (the shares short over the shares lendable): LENDING_FEE up to HARD_TO_BORROW, rising linearly from there to LENDING_FEE + HARD_TO_BORROW_FEE when all of them are le... |
| 1138 | 23 | `private void strikeLending()` | THE LENDING BOOK AND ITS FEE, at each step before anybody posts: what is short is lent by the cells and the fund's market book pro rata to what each holds, and the world pays them lendingRate() of it a year, a twelfth... |
| 1163 | 9 | `private void restrikeLentBook(int c)` | The lending book as the shorts' fills leave it (EM2): what is short, lent by the cells and the fund pro rata, none with nothing to lend. |
| 1196 | 25 | `private void postShorts()` | THE WORLD SELLS SHORT (EM2), in every company valued on its record and worth something, the bank's own never: it reads the quote now, after the others have posted, and the position S it is short, and does the first of... |

### the companies (lines 1222-1283)

| line | len | member | says |
|---:|---:|---|---|
| 1256 | 27 | `private void postCompanies()` | A company past its equity target by OVER_TARGET, with cash past a cushion of its costs, bids for its own shares at fair value plus BUYBACK_TOLERANCE - never more than BUYBACK_PACE of what it is worth a year - and canc... |

### the households (lines 1284-1506)

| line | len | member | says |
|---:|---:|---|---|
| 1287 | 3 | `static boolean mayBuy(Household c)` | Whether a cell may put money into shares at all: no debt, not locked out, not going short, investing at all. |
| 1292 | 5 | `static double shortOfCushion(Household c)` | How far a cell's savings are under its cushion, every household of it: its excess over its share target (round 3). |
| 1300 | 1 | `public double getLifeRebalanceSold()` |  |
| 1304 | 1 | `public double getLifeValueSold()` |  |
| 1307 | 5 | `static double spareOf(Household c)` | What a cell has past its cushion, in all: savings past SHARE_CUSHION_MONTHS of its take-home, every household of it. |
| 1360 | 110 | `private void postHouseholds(double depositRate, double worldRate, double paperYield)` | THE CELLS REBALANCE THEIR SHARES BY THE BOND RULE (0.7.12 round 3; Jerus: "Yes, same rule"): a cell over its target sells HOME_SPEED of the excess, a cell under it buys OUT_SPEED of the gap, as they do with the busine... |
| 1485 | 21 | `private int bestBuy(double[] inc, double[] limits, double floor, boolean[] tried, boolean offered)` | The company a buyer would put the next dollar in: the highest expected return over the floor (EM1; the dividend's yield before), at the best ask when `offered` (only a company with an ask it would take, under its limi... |

### the city's fund (0.7.14) (lines 1507-1638)

| line | len | member | says |
|---:|---:|---|---|
| 1555 | 83 | `private void postFund()` | THE CITY'S FUND, BY ITS RULE AND THEN BY THE PLAYER'S HAND (TreasuryFund). |

### the cap and the hand's orders (0.7.39) (lines 1639-1716)

| line | len | member | says |
|---:|---:|---|---|
| 1663 | 12 | `public double handOnOrder(TreasuryFund fund, int c)` | The hand's buys on a company, in shares: those resting on its book, and the waiting orders' money over their price (the price named, or fair value as struck now). |
| 1677 | 5 | `public double fundRoom(Equity register, TreasuryFund fund, int c)` | The room a buy of the hand's has under the cap, and the rule's bid at the step: the cap less what the fund holds of the company and the hand's buys on order, never under nothing. |
| 1684 | 4 | `private double ruleRoom(int c)` | ...the most of the rule's bid that may still fill between steps: that room, or no limit with no buy of the hand's on the company. |
| 1695 | 7 | `public double fundCouldHold(Equity register, TreasuryFund fund, int c, double more)` | Everything the fund could hold of a company on its market book were every buy of its on it to fill, with `more` units of a new buy of the hand's among them: what it holds, the hand's buys, and the rule's bid on the bo... |
| 1704 | 5 | `public double cityValue(Equity register)` | What the city's fund holds of every company, both books, at the price: the fund's shares' mark. |
| 1711 | 5 | `public double cityMarketValue(Equity register)` | ...its market book alone. |

### A HOUSEHOLD SHORT OF MONEY SELLS, mid-month, in the waterfall (lines 1717-1773)

| line | len | member | says |
|---:|---:|---|---|
| 1733 | 36 | `double sellForHousehold(Equity register, Bank bank, HouseholdBalance households, Household cell, double needPer)` | A cell short of money asks, in each company it holds, the price at which the dividend it gives up yields its own borrowing rate - pro rata by what it holds at that price, for no more than it is short. |

### one order, settled (lines 1774-2055)

| line | len | member | says |
|---:|---:|---|---|
| 1783 | 3 | `List<OrderBook.Fill> tradeForCheck(int c, String who, OrderBook.Side side, double price, double quantity)` | ONE ORDER ON A COMPANY'S BOOK, SETTLED AGAINST THE CITY, between two steps: for FundLedgerCheck (0.7.39), which causes a seller or a buyer at a price it names and then the fund's order against it, and reads back the f... |
| 1792 | 5 | `void stampShortRecordForCheck(int c)` | A short's record set to figures a save has to carry back, none of them the same as another's, for SaveFileCheck's fixture (EM2): one company's lending fees this month and the life counters - a margin call, a recall an... |
| 1799 | 1 | `void splitForCheck()` | ...and the splits the step would make now: for FundLedgerCheck, after it has traded a share past SPLIT_AT. |
| 1801 | 5 | `private void submit(int c, String who, OrderBook.Side side, double price, double quantity)` |  |
| 1824 | 12 | `private void post(int c, String who, OrderBook.Side side, double reservation, double quantity)` | A PRICE-TAKER'S ORDER: it takes what is on offer up to its reservation - the most a buyer would pay for the yield it wants, the least a seller would take - at the resting orders' prices, and rests what is left AT THE ... |
| 1839 | 7 | `private static int classOf(String who)` |  |
| 1856 | 1 | `public int getLastDeskBound(int which)` |  |
| 1857 | 1 | `public int getLifeDeskBound(int which)` |  |
| 1858 | 1 | `public double getLifePostedSellBy(int who)` |  |
| 1859 | 1 | `public double getLifeFilledSellBy(int who)` |  |
| 1861 | 3 | `private OrderBook.Clearing clearing(int c)` |  |
| 1866 | 184 | **type** `private final class Settle implements OrderBook.Clearing` | Where each participant's money and shares are, for one company's book. |
| 1868 | 1 | `Settle(int c)` _(in Exchange.Settle)_ |  |
| 1870 | 36 | `public double capacity(String who, OrderBook.Side side, double price)` _(in Exchange.Settle)_ |  |
| 1915 | 6 | `public boolean mayTrade(String buyer, String seller)` _(in Exchange.Settle)_ | THE FUND NEVER BUYS A NEW ISSUE (0.7.14, Jerus's "new issues only"): the desk's asks in the bank's own shares are new shares the bank issues (deskHolding(), Bank.issuesOwnShares()), so they are passed over for the fun... |
| 1922 | 127 | `public void settle(String buyer, String seller, double q, double price)` _(in Exchange.Settle)_ |  |
| 2052 | 3 | `private Household cellOf(String who)` | The cell a participant's name is, or null. |

### SPLITS (lines 2056-2090)

| line | len | member | says |
|---:|---:|---|---|
| 2066 | 24 | `private void splitWhatNeedsIt()` | A split or a consolidation, by a power of ten, for any share priced a hundred times its founding price or a hundredth of it. |

### reading (lines 2091-2231)

| line | len | member | says |
|---:|---:|---|---|
| 2093 | 1 | `public double getSoldToHouseholds(int c)` |  |
| 2094 | 1 | `public double getBoughtFromHouseholds(int c)` |  |
| 2095 | 1 | `public double getSoldAbroad(int c)` |  |
| 2096 | 1 | `public double getBoughtFromAbroad(int c)` |  |
| 2097 | 1 | `public double getEmigrantsPaid(int c)` |  |
| 2098 | 1 | `public double getBuybackToHouseholds(int c)` |  |
| 2099 | 1 | `public double getBuybackToDesk(int c)` |  |
| 2100 | 1 | `public double getBuybackAbroad(int c)` |  |
| 2102 | 1 | `public double getHouseholdsBoughtAbroad(int c)` | The households' cells bought from the world or from leavers this month (0.7.12 round 2): their savings out, a financial outflow. |
| 2104 | 1 | `public double getHouseholdsSoldAbroad(int c)` | ...and sold to the world: a financial inflow into their savings. |
| 2106 | 1 | `public double getBetweenHouseholds(int c)` | What one cell paid another for shares this month: a transfer inside the households. |
| 2107 | 1 | `public int getBetweenHouseholdsTrades(int c)` |  |
| 2109 | 1 | `public double getVolume(int c)` | Shares that changed hands this month. |
| 2110 | 1 | `public double getLifetimeVolume()` |  |
| 2112 | 1 | `public double getSplit(int c)` | This month's split factor: 100 for a hundred-for-one, .01 for a consolidation, 0 for none. |
| 2114 | 1 | `public double getSplitFactor(int c)` | Shares today for one share at the founding. |
| 2116 | 1 | `public double pricePerFoundingShare(int c)` | The price per FOUNDING share: continuous through every split. |
| 2118 | 1 | `public double fairPerFoundingShare(int c)` | ...and the register's reckoning, the same way. |
| 2120 | 1 | `public double getEmigrantShares(int c)` | This month's leavers' shares still offered. |
| 2122 | 1 | `public double getBuybackBudget(int c)` | What a company's buyback bid may still spend this month. |
| 2125 | 1 | `public double deskResting(int c, OrderBook.Side side)` | What the desk has resting, shares, on one side of one company's book. |
| 2128 | 4 | `public double bestBidOf(int c, String who)` | The best price a participant has resting on a company's book, bid or ask - NaN with none. |
| 2132 | 4 | `public double bestAskOf(int c, String who)` |  |
| 2140 | 1 | `public double getLastPostedSellValue()` | At the last step, every book together: the value asked to sell (at the price), the value that filled, the sell orders posted and those that waited, and the trades. |
| 2141 | 1 | `public double getLastFilledValue()` |  |
| 2142 | 1 | `public int getLastSellsPosted()` |  |
| 2143 | 1 | `public int getLastSellsWaited()` |  |
| 2144 | 1 | `public int getLastTrades()` |  |
| 2146 | 1 | `public double getLifePostedSellValue()` | ...and over the city's life. |
| 2147 | 1 | `public double getLifeFilledValue()` |  |
| 2148 | 1 | `public int getLifeSellsPosted()` |  |
| 2149 | 1 | `public int getLifeSellsWaited()` |  |
| 2150 | 1 | `public int getLifeTrades()` |  |
| 2151 | 1 | `public double getLifeTurnover()` |  |
| 2152 | 1 | `public double getLifeBetweenHouseholds()` |  |
| 2154 | 1 | `public double getLifeEmigrantsOffered()` | What leavers offered on the way out, at their ask, and what they were paid, over the city's life. |
| 2155 | 1 | `public double getLifeEmigrantsPaid()` |  |
| 2156 | 1 | `public int getLifeBetweenHouseholdsTrades()` |  |
| 2158 | 1 | `private double sum(double[] a)` |  |
| 2159 | 1 | `public double getSoldToHouseholds()` |  |
| 2160 | 1 | `public double getBoughtFromHouseholds()` |  |
| 2161 | 1 | `public double getSoldAbroad()` |  |
| 2162 | 1 | `public double getBoughtFromAbroad()` |  |
| 2163 | 1 | `public double getEmigrantsPaid()` |  |
| 2164 | 1 | `public double getBuybackToHouseholds()` |  |
| 2165 | 1 | `public double getBuybackToDesk()` |  |
| 2166 | 1 | `public double getHouseholdsBoughtAbroad()` |  |
| 2167 | 1 | `public double getHouseholdsSoldAbroad()` |  |
| 2168 | 1 | `public double getBetweenHouseholds()` |  |
| 2169 | 1 | `public int getBetweenHouseholdsTrades()` |  |
| 2176 | 1 | `public double deskSoldToHouseholds()` | The desk's trading in the city's shares, the bank's own left out (0.7.9): each total less the bank's line, because its own shares are capital since 0.7.8 (Bank.buyBackOwnShares()), not the desk's trading. |
| 2178 | 1 | `public double deskSoldAbroad()` | ...sold abroad. |
| 2180 | 1 | `public double deskBoughtFromHouseholds()` | ...bought from the households. |
| 2182 | 1 | `public double deskBoughtFromAbroad()` | ...and bought from abroad. |
| 2183 | 1 | `public double getBuybackAbroad()` |  |
| 2184 | 1 | `public double getVolume()` |  |
| 2187 | 1 | `public double getFundBoughtFromHouseholds()` | The city's fund with the cells and the world this month (0.7.14): what it paid them and what they paid it - MoneyAudit's four lines. |
| 2188 | 1 | `public double getFundBoughtAbroad()` |  |
| 2189 | 1 | `public double getFundSoldToHouseholds()` |  |
| 2190 | 1 | `public double getFundSoldAbroad()` |  |
| 2195 | 1 | `public boolean isShortSelling()` | Whether the world sells short at all. |
| 2197 | 1 | `void setShortSelling(boolean on)` | Switches the world's short selling on or off: for the checks, and -Dplaytest.shorts=off, to see the market without it. |
| 2199 | 1 | `public double shortShares(int c)` | Shares the world is short of one company, and the collateral held against them. |
| 2200 | 1 | `public double shortCollateral(int c)` |  |
| 2202 | 1 | `public double lentByCells(int c)` | ...of those, the shares the households' cells lend and the fund's market book lends: they add to the shares short (lent by none, with none to lend). |
| 2203 | 1 | `public double lentByFund(int c)` |  |
| 2205 | 4 | `public double shortInterest(int c)` | The shares the world is short of one company as a share of those outstanding, for the sector screen (EM2): 0 with none short. |
| 2210 | 4 | `public double lendingRateOf(int c)` | The fee a year the lenders are paid on a company now: lendingRate() at the shares short over those lendable. |
| 2215 | 1 | `public double lendingFeeToCells(int c)` | What the world paid the cells and the fund this month for the shares lent, money. |
| 2216 | 1 | `public double lendingFeeToFund(int c)` |  |
| 2217 | 1 | `public double lendingFeeThisMonth(int c)` |  |
| 2219 | 1 | `public double getLendingFeeToFund()` | ...and in all companies, for MoneyAudit: the fund's part is cash in a pool, the cells' goes to their savings. |
| 2220 | 1 | `public double getLendingFeeToCells()` |  |
| 2222 | 1 | `public double getLifeShortSold()` | Over the city's life: shares sold short and what they fetched, shares covered and what that cost, company-months an ask was posted and a short stood open, margin calls and recalls, and the fees paid. |
| 2223 | 1 | `public double getLifeShortSoldValue()` |  |
| 2224 | 1 | `public double getLifeShortCovered()` |  |
| 2225 | 1 | `public double getLifeShortCoveredValue()` |  |
| 2226 | 1 | `public int getLifeShortAsks()` |  |
| 2227 | 1 | `public int getLifeShortMonths()` |  |
| 2228 | 1 | `public int getLifeMarginCalls()` |  |
| 2229 | 1 | `public int getLifeRecalls()` |  |
| 2230 | 1 | `public double getLifeLendingFees()` |  |

### saving (lines 2232-2498)

| line | len | member | says |
|---:|---:|---|---|
| 2246 | 53 | **type** `public static final class State implements Cloneable` | The market in a save (0.7.12, round 2), carried by Gson as it stands: every book with the orders resting on it - a cell's bid posted at the step is what the next month's waterfall sells into - fair value and the split... |
| 2272 | 26 | `State copy()` _(in Exchange.State)_ | A copy that shares nothing with this state (UP1, 2026-10-22): toState() lists the exchange's own books, which the months go on trading into while the autosave is written. |
| 2300 | 41 | `public State toState()` |  |
| 2343 | 58 | `public boolean restore(State s)` |  |
| 2410 | 19 | `public boolean restore(String[] keys, double[] saved)` | A save from before the book (0.7.12 round 1 and earlier): the dealer's array, SLOTS a company - its quote, fair value, the demand it carried and the split factor. |
| 2431 | 4 | `public void reopen(Bank bank)` | After a load: whether the desk posts is the bank's to say, read off the bank as the next step will. |
| 2436 | 32 | `public void reset()` |  |
| 2470 | 28 | `public void redenominate(double scale)` | Prices and the month's cash in the new unit; share counts do not move. |

