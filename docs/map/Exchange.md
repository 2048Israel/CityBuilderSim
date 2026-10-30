# Exchange.java - 2,078 lines · 161 methods · 27 constants · model

`ham/citybuildersim/Exchange.java` - generated 2026-10-30 by CodeMap; line numbers are as of that run.

> The stock exchange: one order book per company, where every share that
> changes hands meets its buyer, and the price is the last trade.
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
> company's PRICE is its last trade, and its fair value - the register's
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
>   - EMIGRANTS sell on the way out: what the households' pool released and
> ... (60 more lines in the source)

**Uses:** [Equity](Equity.md) (63), [OrderBook](OrderBook.md) (59), [TreasuryFund](TreasuryFund.md) (15), [Household](Household.md) (14), [HouseholdBalance](HouseholdBalance.md) (9), [Bank](Bank.md) (8), [OutwardInvestment](OutwardInvestment.md) (5), [BondMarket](BondMarket.md) (3), [FundLedger](FundLedger.md) (1), [SaveCopy](SaveCopy.md) (1)

**Used by (23):** [BankCheck](BankCheck.md), [BankScreen](BankScreen.md), [DataSave](DataSave.md), [Equity](Equity.md), [ExchangeCheck](ExchangeCheck.md), [FundCheck](FundCheck.md), [FundLedger](FundLedger.md), [FundLedgerCheck](FundLedgerCheck.md), [FundScreen](FundScreen.md), [FundView](FundView.md), [Game](Game.md), [HistoryCheck](HistoryCheck.md), [HistorySave](HistorySave.md), [HouseholdBalance](HouseholdBalance.md), [LongPlaytest](LongPlaytest.md), [MarketCheck](MarketCheck.md), [PeopleScreen](PeopleScreen.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [SaveSlotCheck](SaveSlotCheck.md), [SectorBooks](SectorBooks.md), [SectorScreen](SectorScreen.md), [TreasuryFund](TreasuryFund.md)

## Sections

| line | section |
|---:|---|
| 130 | · the dials |
| 168 | AND A DEAD BAND ON THE LINE ITSELF (2026-09-13) |
| 186 | AND A DEAD BAND ON THE RANKING (2026-09-15) |
| 207 | THE DESK'S OWN CAPS ON SHARES (0.7.12 round 3; Jerus: "Put them back") |
| 300 | WHAT COUNTS AS A DEALER HAVING CAPITAL (2026-09-13) |
| 328 | · the participants, on the book |
| 347 | · the state |
| 443 | THE PRICES |
| 555 | THE MONTH |
| 703 | · the desk |
| 867 | · the emigrants |
| 879 | · the world |
| 942 | · the companies |
| 1004 | · the households |
| 1227 | · the city's fund (0.7.14) |
| 1359 | · the cap and the hand's orders (0.7.39) |
| 1437 | A HOUSEHOLD SHORT OF MONEY SELLS, mid-month, in the waterfall |
| 1494 | one order, settled |
| 1738 | SPLITS |
| 1771 | · reading |
| 1872 | · saving |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 133 | `Exchange.SPREAD` | `.02` | The desk's ask over its bid, as a share of fair value. |
| 136 | `Exchange.STALE_MARK_MONTHS` | `12` | A share's last trade marks the city's holding for a year; older, the register's own fair value, which the rule asks at and bids TreasuryFund.RULE_PREMIUM over (C4). |
| 139 | `Exchange.FOREIGN_SPEED` | `.10` | What the world moves in a month per unit of yield over or under its hurdle, as a share of the float. |
| 142 | `Exchange.FOREIGN_TOLERANCE` | `.10` | ...and it holds still while the yield is within this of the hurdle. |
| 145 | `Exchange.HOUSEHOLD_PREMIUM` | `.01` | ...into a return at least this far over the best of the deposit rate, the dollars abroad and the city's paper, annual. |
| 155 | `Exchange.BAND` | `.05` | A holder trades on a share's value only past this either side of it (EM1, 2026-10-29): it sells when the bid is over value by more, or its expected return is under the best alternative by more, and it buys when the as... |
| 163 | `Exchange.BUYBACK_CUSHION_MONTHS` | `6` | A company keeps this many months of its costs before it buys back: operating cost and, since round 2 of 0.7.11, its debt service - the interest and the principal it repaid (Companies.monthlyDebtService()). |
| 166 | `Exchange.OVER_TARGET` | `.10` | Equity this far past target before a company buys back, as a share of assets. |
| 184 | `Exchange.MIN_EXCESS` | `1e-9` |  |
| 199 | `Exchange.TIED_YIELD` | `1e-9` |  |
| 202 | `Exchange.BUYBACK_PACE` | `.10` | The most a company retires in a year, as a share of what it is worth. |
| 205 | `Exchange.BUYBACK_TOLERANCE` | `.10` | A company bids for its own shares up to this far over fair value, and rests its bid there; past it, it waits and the money stays in the till (round 4). |
| 271 | `Exchange.POSITION_LIMIT` | `.25` | The most of the bank's equity the desk holds in any one company's shares, at fair value: a trading book's single-name limit (see above). |
| 282 | `Exchange.BOOK_LIMIT` | `.50` | ...and in all companies' together: the book's aggregate limit. |
| 285 | `Exchange.SPLIT_AT` | `100` | A share priced at this many times its founding price is split; at one over it, consolidated. |
| 295 | `Exchange.MIN_FAIR` | `1e-9` | The least a share may be worth before the market treats the company as worthless. |
| 311 | `Exchange.MIN_DEALER_EQUITY` | `1.0` |  |
| 331 | `Exchange.DESK` | `"bank"` | The bank's trading desk. |
| 333 | `Exchange.WORLD` | `"world"` | The world's investors. |
| 335 | `Exchange.EMIGRANTS` | `"emigrants"` | The month's leavers, selling on the way out. |
| 337 | `Exchange.CELL` | `BondMarket.CELL` | One household cell: this, then its key (the bond market's prefix, deliberately). |
| 339 | `Exchange.FUND` | `BondMarket.FUND` | The city's fund, by its rule (0.7.14; TreasuryFund) - the bond market's name, deliberately. |
| 341 | `Exchange.FUND_HAND` | `BondMarket.FUND_HAND` | ...and by the player's hand: its own name, so the two never trade with each other and the hand's sale can reach the rescue book. |
| 812 | `Exchange.BOUND_CAPITAL` | `0, BOUND_POSITION = 1, BOUND_BOOK = 2, BOUND_FLOAT = 3` | Which of the desk's limits binds its bid: its capital, POSITION_LIMIT, BOOK_LIMIT, or the company's float. |
| 1547 | `Exchange.BY_DESK` | `0, BY_WORLD = 1, BY_EMIGRANTS = 2, BY_HOUSEHOLDS = 3, BY_FUND = 4` | Who sells, by class: the desk, the world, the leavers, a household cell, and since 0.7.14 the city's fund - for the fill rate by seller. |
| 1875 | `Exchange.SLOTS_BEFORE_SPLITS` | `3` | Slots per company in the old dealer's array before the split factor joined (the exchange's first night): its quote, fair value and demand. |
| 1877 | `Exchange.SLOTS` | `SLOTS_BEFORE_SPLITS + 1` | ...and after: the quote, fair value, demand and split factor, four a company - what a save from before 0.7.12 round 2 carries (restore(String[], double[])). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 298 | `private double minFair` | The same floor in today's money. |
| 314 | `private double minDealerEquity` | The same floor in today's money. |
| 349 | `private final int n` |  |
| 350 | `private OrderBook[] books` |  |
| 351 | `private final double[] fair` | the register's reckoning, per share, struck at the step |
| 359 | `private final double[] spare` | What fair value was struck on, per company, at the step and saved (EM1): its money past the cushion (total, never under nothing), its net income over the last year (total) and what its money earns it a year (total). |
| 361 | `private final double[] emigrantShares` | This month's leavers' shares still offered: held abroad by the register, sold by EMIGRANTS. |
| 363 | `private final double[] buybackBudget` | What each company's buyback bid may still spend, money; the rest of its month's programme. |
| 369 | `private final double[] splitFactor` | Shares today per share at the founding, per company: the product of every split and consolidation - what makes a price HISTORY possible (pricePerFoundingShare()). |
| 370 | `private boolean deskOpen` |  |
| 373 | `private final double[] soldToHouseholds` | this month, cash unless it says shares; cleared by startMonth() |
| 374 | `private final double[] boughtFromHouseholds` | desk -> cells |
| 375 | `private final double[] soldAbroad` | cells -> desk |
| 376 | `private final double[] boughtFromAbroad` | desk -> the world |
| 377 | `private final double[] emigrantsPaid` | the world or emigrants -> desk |
| 378 | `private final double[] buybackToHouseholds` | emigrants, from anybody |
| 379 | `private final double[] buybackToDesk` | cells -> the company |
| 380 | `private final double[] buybackAbroad` | desk -> the company |
| 381 | `private final double[] householdsBoughtAbroad` | the world or emigrants -> the company |
| 382 | `private final double[] householdsSoldAbroad` | the world or emigrants -> cells |
| 383 | `private final double[] betweenHouseholds` | cells -> the world |
| 385 | `private final double[] fundBoughtFromHouseholds` | cell -> cell ...and the city's fund (0.7.14): a pool, so what crosses is with the cells and the world |
| 386 | `private final double[] fundBoughtAbroad` | cells -> the fund |
| 387 | `private final double[] fundSoldToHouseholds` | the world or emigrants -> the fund |
| 388 | `private final double[] fundSoldAbroad` | the fund -> cells |
| 389 | `private final double[] volume` | the fund -> the world |
| 390 | `private final double[] split` | shares |
| 391 | `private final int[] betweenHouseholdsTrades` | this month's split factor, 0 if none |
| 392 | `private double ownBoughtThisMonth, ownSoldThisMonth` | the bank's own shares, for the pace |
| 395 | `private double lastPostedSellValue, lastFilledValue` | the last step's books, all companies, and over the city's life - for the screens and the playtest |
| 396 | `private int lastSellsPosted, lastSellsWaited, lastTrades` |  |
| 397 | `private double lifetimeVolume, lifePostedSellValue, lifeFilledValue, lifeBetweenHouseholds, lifeTurnover` |  |
| 398 | `private double lifeEmigrantsOffered, lifeEmigrantsPaid` |  |
| 399 | `private int lifeSellsPosted, lifeSellsWaited, lifeTrades, lifeBetweenHouseholdsTrades` |  |
| 402 | `private Equity register` | Who the book settles against, set at the step and read by every fill until the next. |
| 403 | `private HouseholdBalance households` |  |
| 404 | `private Bank dealer` |  |
| 405 | `private Companies companies` |  |
| 406 | `private int month` |  |
| 408 | `private TreasuryFund fund` | The city's fund (0.7.14), attached at the founding and on the load path; null in a fixture with none. |
| 410 | `private double fundBondsValue` | What the fund's bonds are worth at this step, for its 70/30 (BondMarket.valueHeld()). |
| 759 | `private final double[] lastExcessOffered` | What the desk offered over its caps at the last step, shares (round 4). |
| 761 | `private double lifeExcessOffered, lifeExcessSold` | ...and over the city's life, at fair value when offered, and what of it sold, at the price paid. |
| 1019 | `private double lifeRebalanceSold` | What the cells asked to rebalance over the city's life, at the price: for the playtest (round 3). |
| 1023 | `private double lifeValueSold` | What the cells asked to sell on a share's value over the city's life, at the price (EM1): a bid over value, or a return under the alternatives. |
| 1192 | `private Household buying` | The cell buying at the step right now, and what it may still spend this month. |
| 1193 | `private double buyingBudget` |  |
| 1491 | `private Household selling` | The cell selling in the waterfall right now, whose proceeds go back to its waterfall rather than into its savings. |
| 1492 | `private double raised` |  |
| 1561 | `private final double[] lifePostedSellBy` | Over the city's life, by seller class: what was offered for sale and what of it sold, both in shares at the company's price when it happened - so the ratio is a fill rate, as the book's own is, and not a comparison of... |
| 1564 | `private final int[] lastDeskBound` | Company-months each of the desk's limits held its bid, in BOUND_ order: the last step's and the city's life (round 3). |
| 1576 | `final int c` |  |
| 1887 | `String[] keys` |  |
| 1888 | `List<OrderBook> books` |  |
| 1889 | `double[] fair, splitFactor, emigrantShares, buybackBudget` |  |
| 1891 | `double[] spare, earnings, moneyIncome` | What fair value was struck on (EM1): each company's spare money, its net income over the last year and what its money earns it, totals. |
| 1892 | `double[] life` |  |
| 1893 | `double[] last` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 128 | 1951 | **type** `public class Exchange` | The stock exchange: one order book per company, where every share that changes hands meets its buyer, and the price is the last trade. |

### the dials (lines 130-167)

### AND A DEAD BAND ON THE LINE ITSELF (2026-09-13) (lines 168-185)

### AND A DEAD BAND ON THE RANKING (2026-09-15) (lines 186-206)

### THE DESK'S OWN CAPS ON SHARES (0.7.12 round 3; Jerus: "Put them back") (lines 207-299)

### WHAT COUNTS AS A DEALER HAVING CAPITAL (2026-09-13) (lines 300-327)

| line | len | member | says |
|---:|---:|---|---|
| 317 | 5 | `public void seedConstants(double unit)` | Re-seeds the floors at a given unit. |
| 324 | 3 | `private boolean hasDealerCapital(Bank bank)` | Whether the bank has capital enough to make a market at all. |

### the participants, on the book (lines 328-346)

| line | len | member | says |
|---:|---:|---|---|
| 345 | 1 | `static boolean isFund(String who)` | True for either of the fund's names. |

### the state (lines 347-442)

| line | len | member | says |
|---:|---:|---|---|
| 413 | 1 | `public void attachFund(TreasuryFund fund)` | The city's fund, which posts on the book by its rule and its hand (0.7.14). |
| 415 | 5 | `public Exchange()` |  |
| 422 | 4 | `public void startMonth(int month)` | Clears the month's flows, in this month. |
| 428 | 14 | `public void startMonth()` | Clears the month's flows. |

### THE PRICES (lines 443-554)

| line | len | member | says |
|---:|---:|---|---|
| 448 | 4 | `public double price(int c)` | A share's price: its last trade, or its fair value before its first. |
| 454 | 1 | `public boolean hasTraded(int c)` | True once a share of this company has traded on the book. |
| 466 | 1 | `public double cityMark(int c)` | WHAT THE CITY'S HOLDING OF A SHARE IS MARKED AT (0.7.48, C4): its last trade while that is no older than STALE_MARK_MONTHS, and fair value after - a price nobody has paid for a year says nothing about what the fund ho... |
| 469 | 3 | `public boolean markedAtFair(int c)` | ...true when that is fair value: a last trade older than STALE_MARK_MONTHS. |
| 474 | 1 | `public double fair(int c)` | The register's reckoning of a share, struck at the step: its money past the cushion at face and its earnings capitalised (Equity.fairValue(), EM1). |
| 477 | 1 | `public double spareOf(int c)` | A company's money past its cushion at the last step, total: its till, its dollars abroad and its bonds, less six months of its costs, never under nothing (EM1). |
| 479 | 1 | `public double earningsOf(int c)` | ...its net income over the last year, total: what its business earned, after tax (EM1). |
| 481 | 1 | `public double moneyIncomeOf(int c)` | ...and what its money earned it over the last year, total: deposit interest, the coupon on its dollars abroad (after its tax) and its bonds' coupons, the month's twelve times (EM1, HD2). |
| 484 | 5 | `public double fairSpare(int c)` | The money past the cushion a share carries in its fair value: the spare over the shares in issue; none until the company is valued on its record (EM1). |
| 496 | 5 | `public double expectedReturn(int c, double price)` | A share's expected return at a price, annual: its earnings plus what its money earns, over its price (EM1) - the yardstick every holder reads, against the other shares, the deposit rate, the dollars abroad and the cit... |
| 503 | 1 | `public double bestBid(int c)` | The best bid resting, or NaN with none. |
| 505 | 1 | `public double bestAsk(int c)` | The best ask resting, or NaN with none. |
| 508 | 1 | `public double mark(int c)` | What the desk carries a share at: the last trade or fair value, whichever is lower - a dealer does not mark its own book up on a price nobody has paid. |
| 511 | 1 | `public OrderBook bookOf(int c)` | The book a company's shares trade on. |
| 514 | 1 | `public boolean isOpen()` | True while the bank's desk has capital to post on the book (hasDealerCapital()); the book is open to everybody else regardless. |
| 523 | 3 | `public boolean quoteSupportsIssue(int c)` | Whether a company would sell new shares at today's price: not while the market has them under fair value by more than the tolerance. |
| 528 | 3 | `public double yieldAt(Equity register, int c, double price)` | Dividend yield, annual, at a price: the dividend the register says it actually pays (Equity.dividendPerShareAnnual(), since 0.7.12 round 2). |
| 533 | 5 | `public double markToMarket(Equity register)` | What the desk holds, at its mark: the bank's securities line. |
| 540 | 5 | `public double marketValueOfHouseholds(Equity register, HouseholdBalance households)` | What the city's households hold, at the price. |
| 546 | 1 | `public double marketCap(Equity register, int c)` |  |
| 549 | 5 | `public double deskCanBuy(Equity register, int c)` | The desk's bid capacity in this company now, shares: what its capital carries (Bank.deskCanCarry()), and of its own shares what it holds over target at the pace. |

### THE MONTH (lines 555-702)

| line | len | member | says |
|---:|---:|---|---|
| 560 | 27 | **type** `public interface Companies` | What the month's trading needs to know about a company that is not on the register. |
| 562 | 1 | `double cashAvailable(int company, double wanted)` _(in Exchange.Companies)_ | Cash in the company's till, after anything it can bring home for this. |
| 564 | 1 | `double till(int company)` _(in Exchange.Companies)_ | ...and what is in its till now, bringing nothing home: what a resting buyback bid may still pay. |
| 566 | 1 | `void payBuyback(int company, double cash)` _(in Exchange.Companies)_ | Debits the company's till for a buyback. |
| 567 | 1 | `double assets(int company)` _(in Exchange.Companies)_ |  |
| 568 | 1 | `double equity(int company)` _(in Exchange.Companies)_ |  |
| 569 | 1 | `double monthlyOperatingCost(int company)` _(in Exchange.Companies)_ |  |
| 576 | 1 | `double monthlyDebtService(int company)` _(in Exchange.Companies)_ | ...and what its debt costs it a month: the interest on its statement and the principal it repaid (0.7.11, round 2). |
| 583 | 1 | `default double money(int company)` _(in Exchange.Companies)_ | ...and the money it holds, at face (EM1): its till, its dollars abroad and the other sectors' bonds. |
| 585 | 1 | `default double moneyIncome(int company)` _(in Exchange.Companies)_ | ...and what that money earns it a year at the month's rates: deposit interest, the coupon on its dollars abroad after its tax (on its statement since HD2, taken out of the earnings) and its bonds' coupons - so not in ... |
| 589 | 4 | `private double cushion(int c)` | What a company keeps in its till before it buys back or counts its money spare: BUYBACK_CUSHION_MONTHS of its operating cost and its debt service (0.7.11, round 2). |
| 603 | 5 | `public void takeMonth(Equity register, HouseholdBalance households, Bank bank, Companies companies, double[] book, double world...` | The exchange's step: last month's orders withdrawn, every share valued, and every participant's orders posted - the desk, the emigrants, the world, the companies, the household cells - then the splits, and the bank's ... |
| 610 | 5 | `public void takeMonth(Equity register, HouseholdBalance households, Bank bank, Companies companies, double[] book, double world...` | ...with what the city's fund's bonds are worth this month (0.7.14); no yield on the city's paper (a fixture with none). |
| 623 | 50 | `public void takeMonth(Equity register, HouseholdBalance households, Bank bank, Companies companies, double[] book, double world...` | ...with what the city's fund's bonds are worth this month (0.7.14), for its mix: its orders are posted last, after the households', by its rule and then its hand (postFund()); and what the city's paper the households ... |
| 679 | 7 | `public void attach(Equity register, HouseholdBalance households, Bank bank, Companies companies, int month)` | Who the book settles against from now until the next step. |
| 688 | 14 | `private void noteBook(int c, OrderBook b)` | Adds a book's month, as the step closed it, to the market's record. |

### the desk (lines 703-866)

| line | len | member | says |
|---:|---:|---|---|
| 706 | 27 | `private void postDesk()` | The desk makes a market around fair value, within its capital; its own shares only by its capital policy. |
| 741 | 16 | `public double[] deskExcess(Equity register)` | What the desk holds over its caps, shares, company by company (round 4): the larger of what is over POSITION_LIMIT in that company and its pro-rata part, by value at fair value, of what the book is over BOOK_LIMIT - b... |
| 762 | 1 | `public double getLastExcessOffered(int c)` |  |
| 763 | 1 | `public double getLifeExcessOffered()` |  |
| 764 | 1 | `public double getLifeExcessSold()` |  |
| 767 | 5 | `public double deskExcessOnOffer(int c)` | What of the desk's excess still rests on the book, at fair value, in one company - its asks at fair value (round 4); the Bank tab's figure. |
| 773 | 5 | `public double deskExcessOnOffer()` | ...in every company. |
| 779 | 3 | `private boolean isExcessPrice(int c, double price)` | An ask of the desk's at fair value, not at its ask half a spread over: the excess. |
| 798 | 12 | `private double deskCapacity(Equity register, int c, double price)` | What the desk can buy at this price, shares: of another company, what its bank's capital carries at the weight its book gives the desk (Bank.deskCanCarry(), the inventory at the mark) and never more than is not alread... |
| 820 | 9 | `private double[] deskLimits(Equity register, int c, double price)` | The desk's four limits in another company's shares at this price, shares, in BOUND_ order (round 3): what its capital carries (0.7.8), its room under POSITION_LIMIT in this company and under BOOK_LIMIT over all of the... |
| 831 | 5 | `private double bookAtFair(Equity register)` | What the desk holds of every other company, at fair value: what BOOK_LIMIT reads. |
| 838 | 4 | `public double deskPositionShare(Equity register, int c)` | The desk's holding of one company at fair value over the bank's equity, and its whole book's - for the screens and the playtest. |
| 842 | 4 | `public double deskBookShare(Equity register)` |  |
| 848 | 7 | `private double deskHolding(int c)` | What the desk can sell of this company: what it holds - and of the bank's own, new shares at the pace while the bank is under its target. |
| 862 | 4 | `private static boolean ownSharesTrade(Equity register)` | ...AND NEVER ON A RECORD THAT IS NEW OR BAD - the register's own reading of the bank's last twelve months (Equity.getRegime()): a bank in its first year, or losing money in half the months of one, neither buys its own... |

### the emigrants (lines 867-878)

| line | len | member | says |
|---:|---:|---|---|
| 870 | 8 | `private void postEmigrants()` | The month's leavers ask what the dealer's bid at fair value used to pay them. |

### the world (lines 879-941)

| line | len | member | says |
|---:|---:|---|---|
| 892 | 24 | `private void postWorld(double worldRate)` | The world, on a share's expected return against its rate plus the premium (EM1: it read the dividend's yield before): a bid up to the price that returns the hurdle plus the tolerance - and never over fair value plus h... |
| 927 | 9 | `private void postWorldPrice()` | THE WORLD SELLS INTO A BID OVER VALUE (EM1, 2026-10-29): where a bid rests more than BAND over a share's fair value and the world holds some, it sells OutwardInvestment.HOME_SPEED of what it holds at fair value plus B... |
| 938 | 3 | `private double worldHolding(int c)` | What the world holds that it can sell: what is held abroad less this month's leavers' shares, which are theirs to sell. |

### the companies (lines 942-1003)

| line | len | member | says |
|---:|---:|---|---|
| 976 | 27 | `private void postCompanies()` | A company past its equity target by OVER_TARGET, with cash past a cushion of its costs, bids for its own shares at fair value plus BUYBACK_TOLERANCE - never more than BUYBACK_PACE of what it is worth a year - and canc... |

### the households (lines 1004-1226)

| line | len | member | says |
|---:|---:|---|---|
| 1007 | 3 | `static boolean mayBuy(Household c)` | Whether a cell may put money into shares at all: no debt, not locked out, not going short, investing at all. |
| 1012 | 5 | `static double shortOfCushion(Household c)` | How far a cell's savings are under its cushion, every household of it: its excess over its share target (round 3). |
| 1020 | 1 | `public double getLifeRebalanceSold()` |  |
| 1024 | 1 | `public double getLifeValueSold()` |  |
| 1027 | 5 | `static double spareOf(Household c)` | What a cell has past its cushion, in all: savings past SHARE_CUSHION_MONTHS of its take-home, every household of it. |
| 1080 | 110 | `private void postHouseholds(double depositRate, double worldRate, double paperYield)` | THE CELLS REBALANCE THEIR SHARES BY THE BOND RULE (0.7.12 round 3; Jerus: "Yes, same rule"): a cell over its target sells HOME_SPEED of the excess, a cell under it buys OUT_SPEED of the gap, as they do with the busine... |
| 1205 | 21 | `private int bestBuy(double[] inc, double[] limits, double floor, boolean[] tried, boolean offered)` | The company a buyer would put the next dollar in: the highest expected return over the floor (EM1; the dividend's yield before), at the best ask when `offered` (only a company with an ask it would take, under its limi... |

### the city's fund (0.7.14) (lines 1227-1358)

| line | len | member | says |
|---:|---:|---|---|
| 1275 | 83 | `private void postFund()` | THE CITY'S FUND, BY ITS RULE AND THEN BY THE PLAYER'S HAND (TreasuryFund). |

### the cap and the hand's orders (0.7.39) (lines 1359-1436)

| line | len | member | says |
|---:|---:|---|---|
| 1383 | 12 | `public double handOnOrder(TreasuryFund fund, int c)` | The hand's buys on a company, in shares: those resting on its book, and the waiting orders' money over their price (the price named, or fair value as struck now). |
| 1397 | 5 | `public double fundRoom(Equity register, TreasuryFund fund, int c)` | The room a buy of the hand's has under the cap, and the rule's bid at the step: the cap less what the fund holds of the company and the hand's buys on order, never under nothing. |
| 1404 | 4 | `private double ruleRoom(int c)` | ...the most of the rule's bid that may still fill between steps: that room, or no limit with no buy of the hand's on the company. |
| 1415 | 7 | `public double fundCouldHold(Equity register, TreasuryFund fund, int c, double more)` | Everything the fund could hold of a company on its market book were every buy of its on it to fill, with `more` units of a new buy of the hand's among them: what it holds, the hand's buys, and the rule's bid on the bo... |
| 1424 | 5 | `public double cityValue(Equity register)` | What the city's fund holds of every company, both books, at the price: the fund's shares' mark. |
| 1431 | 5 | `public double cityMarketValue(Equity register)` | ...its market book alone. |

### A HOUSEHOLD SHORT OF MONEY SELLS, mid-month, in the waterfall (lines 1437-1493)

| line | len | member | says |
|---:|---:|---|---|
| 1453 | 36 | `double sellForHousehold(Equity register, Bank bank, HouseholdBalance households, Household cell, double needPer)` | A cell short of money asks, in each company it holds, the price at which the dividend it gives up yields its own borrowing rate - pro rata by what it holds at that price, for no more than it is short. |

### one order, settled (lines 1494-1737)

| line | len | member | says |
|---:|---:|---|---|
| 1503 | 3 | `List<OrderBook.Fill> tradeForCheck(int c, String who, OrderBook.Side side, double price, double quantity)` | ONE ORDER ON A COMPANY'S BOOK, SETTLED AGAINST THE CITY, between two steps: for FundLedgerCheck (0.7.39), which causes a seller or a buyer at a price it names and then the fund's order against it, and reads back the f... |
| 1508 | 1 | `void splitForCheck()` | ...and the splits the step would make now: for FundLedgerCheck, after it has traded a share past SPLIT_AT. |
| 1510 | 5 | `private void submit(int c, String who, OrderBook.Side side, double price, double quantity)` |  |
| 1533 | 12 | `private void post(int c, String who, OrderBook.Side side, double reservation, double quantity)` | A PRICE-TAKER'S ORDER: it takes what is on offer up to its reservation - the most a buyer would pay for the yield it wants, the least a seller would take - at the resting orders' prices, and rests what is left AT THE ... |
| 1548 | 7 | `private static int classOf(String who)` |  |
| 1565 | 1 | `public int getLastDeskBound(int which)` |  |
| 1566 | 1 | `public int getLifeDeskBound(int which)` |  |
| 1567 | 1 | `public double getLifePostedSellBy(int who)` |  |
| 1568 | 1 | `public double getLifeFilledSellBy(int who)` |  |
| 1570 | 3 | `private OrderBook.Clearing clearing(int c)` |  |
| 1575 | 157 | **type** `private final class Settle implements OrderBook.Clearing` | Where each participant's money and shares are, for one company's book. |
| 1577 | 1 | `Settle(int c)` _(in Exchange.Settle)_ |  |
| 1579 | 32 | `public double capacity(String who, OrderBook.Side side, double price)` _(in Exchange.Settle)_ |  |
| 1620 | 4 | `public boolean mayTrade(String buyer, String seller)` _(in Exchange.Settle)_ | THE FUND NEVER BUYS A NEW ISSUE (0.7.14, Jerus's "new issues only"): the desk's asks in the bank's own shares are new shares the bank issues (deskHolding(), Bank.issuesOwnShares()), so they are passed over for the fun... |
| 1625 | 106 | `public void settle(String buyer, String seller, double q, double price)` _(in Exchange.Settle)_ |  |
| 1734 | 3 | `private Household cellOf(String who)` | The cell a participant's name is, or null. |

### SPLITS (lines 1738-1770)

| line | len | member | says |
|---:|---:|---|---|
| 1748 | 22 | `private void splitWhatNeedsIt()` | A split or a consolidation, by a power of ten, for any share priced a hundred times its founding price or a hundredth of it. |

### reading (lines 1771-1871)

| line | len | member | says |
|---:|---:|---|---|
| 1773 | 1 | `public double getSoldToHouseholds(int c)` |  |
| 1774 | 1 | `public double getBoughtFromHouseholds(int c)` |  |
| 1775 | 1 | `public double getSoldAbroad(int c)` |  |
| 1776 | 1 | `public double getBoughtFromAbroad(int c)` |  |
| 1777 | 1 | `public double getEmigrantsPaid(int c)` |  |
| 1778 | 1 | `public double getBuybackToHouseholds(int c)` |  |
| 1779 | 1 | `public double getBuybackToDesk(int c)` |  |
| 1780 | 1 | `public double getBuybackAbroad(int c)` |  |
| 1782 | 1 | `public double getHouseholdsBoughtAbroad(int c)` | The households' cells bought from the world or from leavers this month (0.7.12 round 2): their savings out, a financial outflow. |
| 1784 | 1 | `public double getHouseholdsSoldAbroad(int c)` | ...and sold to the world: a financial inflow into their savings. |
| 1786 | 1 | `public double getBetweenHouseholds(int c)` | What one cell paid another for shares this month: a transfer inside the households. |
| 1787 | 1 | `public int getBetweenHouseholdsTrades(int c)` |  |
| 1789 | 1 | `public double getVolume(int c)` | Shares that changed hands this month. |
| 1790 | 1 | `public double getLifetimeVolume()` |  |
| 1792 | 1 | `public double getSplit(int c)` | This month's split factor: 100 for a hundred-for-one, .01 for a consolidation, 0 for none. |
| 1794 | 1 | `public double getSplitFactor(int c)` | Shares today for one share at the founding. |
| 1796 | 1 | `public double pricePerFoundingShare(int c)` | The price per FOUNDING share: continuous through every split. |
| 1798 | 1 | `public double fairPerFoundingShare(int c)` | ...and the register's reckoning, the same way. |
| 1800 | 1 | `public double getEmigrantShares(int c)` | This month's leavers' shares still offered. |
| 1802 | 1 | `public double getBuybackBudget(int c)` | What a company's buyback bid may still spend this month. |
| 1805 | 1 | `public double deskResting(int c, OrderBook.Side side)` | What the desk has resting, shares, on one side of one company's book. |
| 1808 | 4 | `public double bestBidOf(int c, String who)` | The best price a participant has resting on a company's book, bid or ask - NaN with none. |
| 1812 | 4 | `public double bestAskOf(int c, String who)` |  |
| 1820 | 1 | `public double getLastPostedSellValue()` | At the last step, every book together: the value asked to sell (at the price), the value that filled, the sell orders posted and those that waited, and the trades. |
| 1821 | 1 | `public double getLastFilledValue()` |  |
| 1822 | 1 | `public int getLastSellsPosted()` |  |
| 1823 | 1 | `public int getLastSellsWaited()` |  |
| 1824 | 1 | `public int getLastTrades()` |  |
| 1826 | 1 | `public double getLifePostedSellValue()` | ...and over the city's life. |
| 1827 | 1 | `public double getLifeFilledValue()` |  |
| 1828 | 1 | `public int getLifeSellsPosted()` |  |
| 1829 | 1 | `public int getLifeSellsWaited()` |  |
| 1830 | 1 | `public int getLifeTrades()` |  |
| 1831 | 1 | `public double getLifeTurnover()` |  |
| 1832 | 1 | `public double getLifeBetweenHouseholds()` |  |
| 1834 | 1 | `public double getLifeEmigrantsOffered()` | What leavers offered on the way out, at their ask, and what they were paid, over the city's life. |
| 1835 | 1 | `public double getLifeEmigrantsPaid()` |  |
| 1836 | 1 | `public int getLifeBetweenHouseholdsTrades()` |  |
| 1838 | 1 | `private double sum(double[] a)` |  |
| 1839 | 1 | `public double getSoldToHouseholds()` |  |
| 1840 | 1 | `public double getBoughtFromHouseholds()` |  |
| 1841 | 1 | `public double getSoldAbroad()` |  |
| 1842 | 1 | `public double getBoughtFromAbroad()` |  |
| 1843 | 1 | `public double getEmigrantsPaid()` |  |
| 1844 | 1 | `public double getBuybackToHouseholds()` |  |
| 1845 | 1 | `public double getBuybackToDesk()` |  |
| 1846 | 1 | `public double getHouseholdsBoughtAbroad()` |  |
| 1847 | 1 | `public double getHouseholdsSoldAbroad()` |  |
| 1848 | 1 | `public double getBetweenHouseholds()` |  |
| 1849 | 1 | `public int getBetweenHouseholdsTrades()` |  |
| 1856 | 1 | `public double deskSoldToHouseholds()` | The desk's trading in the city's shares, the bank's own left out (0.7.9): each total less the bank's line, because its own shares are capital since 0.7.8 (Bank.buyBackOwnShares()), not the desk's trading. |
| 1858 | 1 | `public double deskSoldAbroad()` | ...sold abroad. |
| 1860 | 1 | `public double deskBoughtFromHouseholds()` | ...bought from the households. |
| 1862 | 1 | `public double deskBoughtFromAbroad()` | ...and bought from abroad. |
| 1863 | 1 | `public double getBuybackAbroad()` |  |
| 1864 | 1 | `public double getVolume()` |  |
| 1867 | 1 | `public double getFundBoughtFromHouseholds()` | The city's fund with the cells and the world this month (0.7.14): what it paid them and what they paid it - MoneyAudit's four lines. |
| 1868 | 1 | `public double getFundBoughtAbroad()` |  |
| 1869 | 1 | `public double getFundSoldToHouseholds()` |  |
| 1870 | 1 | `public double getFundSoldAbroad()` |  |

### saving (lines 1872-2078)

| line | len | member | says |
|---:|---:|---|---|
| 1886 | 36 | **type** `public static final class State implements Cloneable` | The market in a save (0.7.12, round 2), carried by Gson as it stands: every book with the orders resting on it - a cell's bid posted at the step is what the next month's waterfall sells into - fair value and the split... |
| 1902 | 19 | `State copy()` _(in Exchange.State)_ | A copy that shares nothing with this state (UP1, 2026-10-22): toState() lists the exchange's own books, which the months go on trading into while the autosave is written. |
| 1923 | 26 | `public State toState()` |  |
| 1951 | 42 | `public boolean restore(State s)` |  |
| 2002 | 19 | `public boolean restore(String[] keys, double[] saved)` | A save from before the book (0.7.12 round 1 and earlier): the dealer's array, SLOTS a company - its quote, fair value, the demand it carried and the split factor. |
| 2023 | 4 | `public void reopen(Bank bank)` | After a load: whether the desk posts is the bank's to say, read off the bank as the next step will. |
| 2028 | 24 | `public void reset()` |  |
| 2054 | 24 | `public void redenominate(double scale)` | Prices and the month's cash in the new unit; share counts do not move. |

