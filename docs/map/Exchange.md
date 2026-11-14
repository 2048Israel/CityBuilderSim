# Exchange.java - 2,634 lines · 201 methods · 36 constants · model

`ham/citybuildersim/Exchange.java` - generated 2026-11-14 by CodeMap; line numbers are as of that run.

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
> ... (65 more lines in the source)

**Uses:** [OrderBook](OrderBook.md) (78), [Equity](Equity.md) (66), [TreasuryFund](TreasuryFund.md) (16), [Household](Household.md) (16), [HouseholdBalance](HouseholdBalance.md) (9), [Bank](Bank.md) (8), [OutwardInvestment](OutwardInvestment.md) (5), [OwnerHousehold](OwnerHousehold.md) (5), [BondMarket](BondMarket.md) (3), [PriceLevel](PriceLevel.md) (2), [FundLedger](FundLedger.md) (1), [SaveCopy](SaveCopy.md) (1)

**Used by (27):** [BankCheck](BankCheck.md), [BankFailureCheck](BankFailureCheck.md), [BankScreen](BankScreen.md), [DataSave](DataSave.md), [Equity](Equity.md), [ExchangeCheck](ExchangeCheck.md), [FundCheck](FundCheck.md), [FundLedger](FundLedger.md), [FundLedgerCheck](FundLedgerCheck.md), [FundScreen](FundScreen.md), [FundView](FundView.md), [Game](Game.md), [HistoryCheck](HistoryCheck.md), [HistorySave](HistorySave.md), [HouseholdBalance](HouseholdBalance.md), [LongPlaytest](LongPlaytest.md), [MarketCheck](MarketCheck.md), [OwnerCheck](OwnerCheck.md), [PeopleScreen](PeopleScreen.md), [PrizeCheck](PrizeCheck.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [SaveSlotCheck](SaveSlotCheck.md), [SectorBooks](SectorBooks.md), [SectorScreen](SectorScreen.md), [ShortSellingCheck](ShortSellingCheck.md), [TreasuryFund](TreasuryFund.md)

## Sections

| line | section |
|---:|---|
| 135 | · the dials |
| 173 | AND A DEAD BAND ON THE LINE ITSELF (2026-09-13) |
| 191 | AND A DEAD BAND ON THE RANKING (2026-09-15) |
| 212 | THE WORLD SELLS SHORT, AND THE CITY'S HOLDERS LEND (EM2, 2026-10-30) |
| 273 | THE DESK'S OWN CAPS ON SHARES (0.7.12 round 3; Jerus: "Put them back") |
| 369 | WHAT COUNTS AS A DEALER HAVING CAPITAL (2026-09-13) |
| 399 | · the participants, on the book |
| 420 | · the state |
| 536 | THE PRICES |
| 707 | THE MONTH |
| 879 | · the desk |
| 1043 | · the emigrants |
| 1055 | · the world |
| 1118 | · the shorts (EM2) |
| 1260 | · the companies |
| 1322 | · the households |
| 1360 | · the owner families sell slowly (BO1) |
| 1585 | · the city's fund (0.7.14) |
| 1752 | · the cap and the hand's orders (0.7.39) |
| 1843 | A HOUSEHOLD SHORT OF MONEY SELLS, mid-month, in the waterfall |
| 1900 | one order, settled |
| 2186 | SPLITS |
| 2224 | · reading |
| 2365 | · saving |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 138 | `Exchange.SPREAD` | `.02` | The desk's ask over its bid, as a share of fair value. |
| 141 | `Exchange.STALE_MARK_MONTHS` | `12` | A share's last trade marks the city's holding for a year; older, the register's own fair value, which the rule asks at and bids TreasuryFund.RULE_PREMIUM over (C4). |
| 144 | `Exchange.FOREIGN_SPEED` | `.10` | What the world moves in a month per unit of yield over or under its hurdle, as a share of the float. |
| 147 | `Exchange.FOREIGN_TOLERANCE` | `.10` | ...and it holds still while the yield is within this of the hurdle. |
| 150 | `Exchange.HOUSEHOLD_PREMIUM` | `.01` | ...into a return at least this far over the best of the deposit rate, the dollars abroad and the city's paper, annual. |
| 160 | `Exchange.BAND` | `.05` | A holder trades on a share's value only past this either side of it (EM1, 2026-10-29): it sells when the bid is over value by more, or its expected return is under the best alternative by more, and it buys when the as... |
| 168 | `Exchange.BUYBACK_CUSHION_MONTHS` | `6` | A company keeps this many months of its costs before it buys back: operating cost and, since round 2 of 0.7.11, its debt service - the interest and the principal it repaid (Companies.monthlyDebtService()). |
| 171 | `Exchange.OVER_TARGET` | `.10` | Equity this far past target before a company buys back, as a share of assets. |
| 189 | `Exchange.MIN_EXCESS` | `1e-9` |  |
| 204 | `Exchange.TIED_YIELD` | `1e-9` |  |
| 207 | `Exchange.BUYBACK_PACE` | `.10` | The most a company retires in a year, as a share of what it is worth. |
| 210 | `Exchange.BUYBACK_TOLERANCE` | `.10` | A company bids for its own shares up to this far over fair value, and rests its bid there; past it, it waits and the money stays in the till (round 4). |
| 253 | `Exchange.SHORT_BAND` | `.20` | The world sells short only at this far over fair value; over BUYBACK_TOLERANCE, so a short never sells into a company's bid for its own shares, and past it a share's price is over what its business and money are worth... |
| 256 | `Exchange.INITIAL_MARGIN` | `1.5` | The collateral held against a short at its sale, a multiple of the sale: the proceeds and half as much again (Regulation T). |
| 259 | `Exchange.MAINTENANCE_MARGIN` | `.30` | The account's equity under this share of the short's market value is a margin call (FINRA Rule 4210 for shorts): the short covers at the most it can pay. |
| 262 | `Exchange.MAX_SHORT_INTEREST` | `.20` | The most of a company's outstanding shares that may be short at once. |
| 265 | `Exchange.LENDING_FEE` | `.005` | The general fee a year on the shares lent, at their value: the market's rate is .3% to 1%. |
| 268 | `Exchange.HARD_TO_BORROW` | `.50` | The share of the lendable shares lent past which a share is hard to borrow... |
| 271 | `Exchange.HARD_TO_BORROW_FEE` | `.10` | ...and the fee a year it adds at full utilisation, linearly from HARD_TO_BORROW. |
| 337 | `Exchange.POSITION_LIMIT` | `.25` | The most of the bank's equity the desk holds in any one company's shares, at fair value: a trading book's single-name limit (see above). |
| 348 | `Exchange.BOOK_LIMIT` | `.50` | ...and in all companies' together: the book's aggregate limit. |
| 351 | `Exchange.SPLIT_AT` | `.25` | A share costing more than this share of the city's average monthly wage splits (SX1). |
| 354 | `Exchange.CONSOLIDATE_BELOW` | `.0025` | A share costing less than this fraction of the average monthly wage consolidates (SX1). |
| 364 | `Exchange.MIN_FAIR` | `PriceLevel.atFounding(1e-9)` | The least a share may be worth before the market treats the company as worthless. |
| 382 | `Exchange.MIN_DEALER_EQUITY` | `1.0 * PriceLevel.FACTOR` | The least equity a bank's desk needs to post a market, founding thousands: a thousand dollars, Today's figure, priced at the founding's level (times PriceLevel.FACTOR, EP1). |
| 402 | `Exchange.DESK` | `"bank"` | The bank's trading desk. |
| 404 | `Exchange.WORLD` | `"world"` | The world's investors. |
| 406 | `Exchange.WORLD_SHORT` | `"world, short"` | The world's short seller (EM2): a participant of its own, so a short and the world's long never trade with each other, and never a cell's name (no BondMarket.CELL prefix). |
| 408 | `Exchange.EMIGRANTS` | `"emigrants"` | The month's leavers, selling on the way out. |
| 410 | `Exchange.CELL` | `BondMarket.CELL` | One household cell: this, then its key (the bond market's prefix, deliberately). |
| 412 | `Exchange.FUND` | `BondMarket.FUND` | The city's fund, by its rule (0.7.14; TreasuryFund) - the bond market's name, deliberately. |
| 414 | `Exchange.FUND_HAND` | `BondMarket.FUND_HAND` | ...and by the player's hand: its own name, so the two never trade with each other and the hand's sale can reach the rescue book. |
| 988 | `Exchange.BOUND_CAPITAL` | `0, BOUND_POSITION = 1, BOUND_BOOK = 2, BOUND_FLOAT = 3` | Which of the desk's limits binds its bid: its capital, POSITION_LIMIT, BOOK_LIMIT, or the company's float. |
| 1967 | `Exchange.BY_DESK` | `0, BY_WORLD = 1, BY_EMIGRANTS = 2, BY_HOUSEHOLDS = 3, BY_FUND = 4, BY_OWNERS = 5` | Who sells, by class: the desk, the world, the leavers, a household cell, since 0.7.14 the city's fund, and since BO1 the owner families (their own class: a cell that sells on a rule of its own and not the households')... |
| 2368 | `Exchange.SLOTS_BEFORE_SPLITS` | `3` | Slots per company in the old dealer's array before the split factor joined (the exchange's first night): its quote, fair value and demand. |
| 2370 | `Exchange.SLOTS` | `SLOTS_BEFORE_SPLITS + 1` | ...and after: the quote, fair value, demand and split factor, four a company - what a save from before 0.7.12 round 2 carries (restore(String[], double[])). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 367 | `private double minFair` | The founding-money floor in the current unit. |
| 385 | `private double minDealerEquity` | The same floor in today's money. |
| 422 | `private final int n` |  |
| 423 | `private OrderBook[] books` |  |
| 424 | `private final double[] fair` | the register's reckoning, per share, struck at the step |
| 432 | `private final double[] spare` | What fair value was struck on, per company, at the step and saved (EM1): its money past the cushion (total, never under nothing), its net income over the last year (total) and what its money earns it a year (total). |
| 434 | `private final double[] emigrantShares` | This month's leavers' shares still offered: held abroad by the register, sold by EMIGRANTS. |
| 436 | `private final double[] buybackBudget` | What each company's buyback bid may still spend, money; the rest of its month's programme. |
| 442 | `private final double[] splitFactor` | Shares today per share at the founding, per company: the product of every split and consolidation - what makes a price HISTORY possible (pricePerFoundingShare()). |
| 443 | `private boolean deskOpen` |  |
| 446 | `private final double[] soldToHouseholds` | this month, cash unless it says shares; cleared by startMonth() |
| 447 | `private final double[] boughtFromHouseholds` | desk -> cells |
| 448 | `private final double[] soldAbroad` | cells -> desk |
| 449 | `private final double[] boughtFromAbroad` | desk -> the world |
| 450 | `private final double[] emigrantsPaid` | the world or emigrants -> desk |
| 451 | `private final double[] buybackToHouseholds` | emigrants, from anybody |
| 452 | `private final double[] buybackToDesk` | cells -> the company |
| 453 | `private final double[] buybackAbroad` | desk -> the company |
| 454 | `private final double[] householdsBoughtAbroad` | the world or emigrants -> the company |
| 455 | `private final double[] householdsSoldAbroad` | the world or emigrants -> cells |
| 456 | `private final double[] betweenHouseholds` | cells -> the world |
| 458 | `private final double[] fundBoughtFromHouseholds` | cell -> cell ...and the city's fund (0.7.14): a pool, so what crosses is with the cells and the world |
| 459 | `private final double[] fundBoughtAbroad` | cells -> the fund |
| 460 | `private final double[] fundSoldToHouseholds` | the world or emigrants -> the fund |
| 461 | `private final double[] fundSoldAbroad` | the fund -> cells |
| 462 | `private final double[] volume` | the fund -> the world |
| 463 | `private final double[] split` | shares |
| 464 | `private final int[] betweenHouseholdsTrades` | this month's split factor, 0 if none |
| 465 | `private double ownBoughtThisMonth, ownSoldThisMonth` | the bank's own shares, for the pace |
| 469 | `private boolean shortSelling` | Whether the world sells short at all: on, the default; the checks and -Dplaytest.shorts=off switch it off to see the market without it. |
| 471 | `private final double[] shortShares` | Shares the world has sold short, per company: the S of the rule. |
| 473 | `private final double[] shortCollateral` | The collateral held against them, money: INITIAL_MARGIN times each sale, released pro rata as they are covered. |
| 475 | `private final double[] lentCells` | Of the shares short, those the cells lend and those the fund's market book lends: struck at the step and again at each fill of a short. |
| 477 | `private final double[] quoteBefore` | The quote of the last step's books before it withdrew them (the fee is charged on it), per company with a short. |
| 479 | `private final double[] feeToCells` | The month's lending fee, money, by who was paid: this month's, cleared by startMonth(). |
| 481 | `private double[] restoredShortPays` | What a load read of the short's dividends over the city's life, per company, until attach() hands it to the register (EM2). |
| 483 | `private double lifeShortSold, lifeShortSoldValue, lifeShortCovered, lifeShortCoveredValue, lifeLendingFees` | Over the city's life: shares sold short and their value, covered and their value, company-months an ask was posted and a short was open, margin calls and recalls, fees paid. |
| 484 | `private int lifeShortAsks, lifeShortMonths, lifeMarginCalls, lifeRecalls` |  |
| 487 | `private double lastPostedSellValue, lastFilledValue` | the last step's books, all companies, and over the city's life - for the screens and the playtest |
| 488 | `private int lastSellsPosted, lastSellsWaited, lastTrades` |  |
| 489 | `private double lifetimeVolume, lifePostedSellValue, lifeFilledValue, lifeBetweenHouseholds, lifeTurnover` |  |
| 490 | `private double lifeEmigrantsOffered, lifeEmigrantsPaid` |  |
| 491 | `private int lifeSellsPosted, lifeSellsWaited, lifeTrades, lifeBetweenHouseholdsTrades` |  |
| 494 | `private Equity register` | Who the book settles against, set at the step and read by every fill until the next. |
| 495 | `private HouseholdBalance households` |  |
| 496 | `private Bank dealer` |  |
| 497 | `private Companies companies` |  |
| 498 | `private int month` |  |
| 500 | `private TreasuryFund fund` | The city's fund (0.7.14), attached at the founding and on the load path; null in a fixture with none. |
| 502 | `private double fundBondsValue` | What the fund's bonds are worth at this step, for its 70/30 (BondMarket.valueHeld()). |
| 935 | `private final double[] lastExcessOffered` | What the desk offered over its caps at the last step, shares (round 4). |
| 937 | `private double lifeExcessOffered, lifeExcessSold` | ...and over the city's life, at fair value when offered, and what of it sold, at the price paid. |
| 1346 | `private double lifeRebalanceSold` | What the cells asked to rebalance over the city's life, at the price: for the playtest (round 3). |
| 1350 | `private double lifeValueSold` | What the cells asked to sell on a share's value over the city's life, at the price (EM1): a bid over value, or a return under the alternatives. |
| 1550 | `private Household buying` | The cell buying at the step right now, and what it may still spend this month. |
| 1551 | `private double buyingBudget` |  |
| 1897 | `private Household selling` | The cell selling in the waterfall right now, whose proceeds go back to its waterfall rather than into its savings. |
| 1898 | `private double raised` |  |
| 1982 | `private final double[] lifePostedSellBy` | Over the city's life, by seller class: what was offered for sale and what of it sold, both in shares at the company's price when it happened - so the ratio is a fill rate, as the book's own is, and not a comparison of... |
| 1985 | `private final int[] lastDeskBound` | Company-months each of the desk's limits held its bid, in BOUND_ order: the last step's and the city's life (round 3). |
| 1997 | `final int c` |  |
| 2380 | `String[] keys` |  |
| 2381 | `List<OrderBook> books` |  |
| 2382 | `double[] fair, splitFactor, emigrantShares, buybackBudget` |  |
| 2384 | `double[] spare, earnings, moneyIncome` | What fair value was struck on (EM1): each company's spare money, its net income over the last year and what its money earns it, totals. |
| 2391 | `double[] shortShares, shortCollateral, lentCells, lentFund, feeToCells, feeToFund, shortPays` | The world's short and who lends it (EM2), per company in keys' order: the shares short, the collateral held against them (money), the shares the cells and the fund lend, and the month's lending fee each was paid (mone... |
| 2393 | `double lifeShortSold, lifeShortSoldValue, lifeShortCovered, lifeShortCoveredValue, lifeLendingFees` | ...and over the city's life: shares sold short and their value, covered and their value, the fees paid, the company-months an ask was posted and a short stood open, the margin calls and the recalls. |
| 2394 | `int lifeShortAsks, lifeShortMonths, lifeMarginCalls, lifeRecalls` |  |
| 2395 | `double[] life` |  |
| 2396 | `double[] last` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 133 | 2502 | **type** `public class Exchange` | The stock exchange: one order book per company, where every share that changes hands meets its buyer, and the price is the quote: the middle of the book (EM2). |

### the dials (lines 135-172)

### AND A DEAD BAND ON THE LINE ITSELF (2026-09-13) (lines 173-190)

### AND A DEAD BAND ON THE RANKING (2026-09-15) (lines 191-211)

### THE WORLD SELLS SHORT, AND THE CITY'S HOLDERS LEND (EM2, 2026-10-30) (lines 212-272)

### THE DESK'S OWN CAPS ON SHARES (0.7.12 round 3; Jerus: "Put them back") (lines 273-368)

### WHAT COUNTS AS A DEALER HAVING CAPITAL (2026-09-13) (lines 369-398)

| line | len | member | says |
|---:|---:|---|---|
| 388 | 5 | `public void seedConstants(double unit)` | Re-seeds the floors at a given unit. |
| 395 | 3 | `private boolean hasDealerCapital(Bank bank)` | Whether the bank has capital enough to make a market at all. |

### the participants, on the book (lines 399-419)

| line | len | member | says |
|---:|---:|---|---|
| 418 | 1 | `static boolean isFund(String who)` | True for either of the fund's names. |

### the state (lines 420-535)

| line | len | member | says |
|---:|---:|---|---|
| 505 | 1 | `public void attachFund(TreasuryFund fund)` | The city's fund, which posts on the book by its rule and its hand (0.7.14). |
| 507 | 5 | `public Exchange()` |  |
| 514 | 4 | `public void startMonth(int month)` | Clears the month's flows, in this month. |
| 520 | 15 | `public void startMonth()` | Clears the month's flows. |

### THE PRICES (lines 536-706)

| line | len | member | says |
|---:|---:|---|---|
| 562 | 10 | `public double price(int c)` | A SHARE'S PRICE IS ITS QUOTE (EM2, 2026-10-30): the middle of its book while a bid and an ask both rest; with one side empty, the last trade - never over the ask that stands, nor under the bid; with neither, the last ... |
| 574 | 9 | `public boolean quoteIsLastTrade(int c)` | True when the quote is the book's last trade as it stands: no side resting that sets or bounds it (EM2) - the one quote that can grow stale. |
| 589 | 8 | `public String quoteWords(int c)` | What a share's price is made of now, in words, for the caption beside it (EM2): "the middle of the bid and the ask" while both rest; with one side standing the last trade "held inside the ask" or "held over the bid"; ... |
| 599 | 1 | `public double lastTrade(int c)` | The book's last trade, or NaN before the first: what the screens show beside the quote (EM2). |
| 602 | 1 | `public boolean hasTraded(int c)` | True once a share of this company has traded on the book. |
| 615 | 1 | `public double cityMark(int c)` | WHAT THE CITY'S HOLDING OF A SHARE IS MARKED AT (0.7.48, C4): its quote while that is current, and fair value once the quote is a last trade older than STALE_MARK_MONTHS - a price nobody has paid for a year says nothi... |
| 618 | 3 | `public boolean markedAtFair(int c)` | ...true when that is fair value: a quote that is the last trade, older than STALE_MARK_MONTHS. |
| 623 | 1 | `public double fair(int c)` | The register's reckoning of a share, struck at the step: its money past the cushion at face and its earnings capitalised (Equity.fairValue(), EM1). |
| 626 | 1 | `public boolean atFloor(int c)` | ...true when that is the exchange's floor (minFair), which a company whose book and record are gone sits at (BO1: the owner family's broke rule). |
| 629 | 1 | `public double spareOf(int c)` | A company's money past its cushion at the last step, total: its till, its dollars abroad and its bonds, less six months of its costs, never under nothing (EM1). |
| 631 | 1 | `public double earningsOf(int c)` | ...its net income over the last year, total: what its business earned, after tax (EM1). |
| 633 | 1 | `public double moneyIncomeOf(int c)` | ...and what its money earned it over the last year, total: deposit interest, the coupon on its dollars abroad (after its tax) and its bonds' coupons, the month's twelve times (EM1, HD2). |
| 636 | 5 | `public double fairSpare(int c)` | The money past the cushion a share carries in its fair value: the spare over the shares in issue; none until the company is valued on its record (EM1). |
| 648 | 5 | `public double expectedReturn(int c, double price)` | A share's expected return at a price, annual: its earnings plus what its money earns, over its price (EM1) - the yardstick every holder reads, against the other shares, the deposit rate, the dollars abroad and the cit... |
| 655 | 1 | `public double bestBid(int c)` | The best bid resting, or NaN with none. |
| 657 | 1 | `public double bestAsk(int c)` | The best ask resting, or NaN with none. |
| 660 | 1 | `public double mark(int c)` | What the desk carries a share at: the last trade or fair value, whichever is lower - a dealer does not mark its own book up on a price nobody has paid. |
| 663 | 1 | `public OrderBook bookOf(int c)` | The book a company's shares trade on. |
| 666 | 1 | `public boolean isOpen()` | True while the bank's desk has capital to post on the book (hasDealerCapital()); the book is open to everybody else regardless. |
| 675 | 3 | `public boolean quoteSupportsIssue(int c)` | Whether a company would sell new shares at today's price: not while the market has them under fair value by more than the tolerance. |
| 680 | 3 | `public double yieldAt(Equity register, int c, double price)` | Dividend yield, annual, at a price: the dividend the register says it actually pays (Equity.dividendPerShareAnnual(), since 0.7.12 round 2). |
| 685 | 5 | `public double markToMarket(Equity register)` | What the desk holds, at its mark: the bank's securities line. |
| 692 | 5 | `public double marketValueOfHouseholds(Equity register, HouseholdBalance households)` | What the city's households hold, at the price. |
| 698 | 1 | `public double marketCap(Equity register, int c)` |  |
| 701 | 5 | `public double deskCanBuy(Equity register, int c)` | The desk's bid capacity in this company now, shares: what its capital carries (Bank.deskCanCarry()), and of its own shares what it holds over target at the pace. |

### THE MONTH (lines 707-878)

| line | len | member | says |
|---:|---:|---|---|
| 712 | 29 | **type** `public interface Companies` | What the month's trading needs to know about a company that is not on the register. |
| 714 | 1 | `default double averageMonthlyWage()` _(in Exchange.Companies)_ | The city's average monthly wage, in the quote's money unit; no paid jobs means no wage band yet. |
| 716 | 1 | `double cashAvailable(int company, double wanted)` _(in Exchange.Companies)_ | Cash in the company's till, after anything it can bring home for this. |
| 718 | 1 | `double till(int company)` _(in Exchange.Companies)_ | ...and what is in its till now, bringing nothing home: what a resting buyback bid may still pay. |
| 720 | 1 | `void payBuyback(int company, double cash)` _(in Exchange.Companies)_ | Debits the company's till for a buyback. |
| 721 | 1 | `double assets(int company)` _(in Exchange.Companies)_ |  |
| 722 | 1 | `double equity(int company)` _(in Exchange.Companies)_ |  |
| 723 | 1 | `double monthlyOperatingCost(int company)` _(in Exchange.Companies)_ |  |
| 730 | 1 | `double monthlyDebtService(int company)` _(in Exchange.Companies)_ | ...and what its debt costs it a month: the interest on its statement and the principal it repaid (0.7.11, round 2). |
| 737 | 1 | `default double money(int company)` _(in Exchange.Companies)_ | ...and the money it holds, at face (EM1): its till, its dollars abroad and the other sectors' bonds. |
| 739 | 1 | `default double moneyIncome(int company)` _(in Exchange.Companies)_ | ...and what that money earns it a year at the month's rates: deposit interest, the coupon on its dollars abroad after its tax (on its statement since HD2, taken out of the earnings) and its bonds' coupons - so not in ... |
| 743 | 4 | `private double cushion(int c)` | What a company keeps in its till before it buys back or counts its money spare: BUYBACK_CUSHION_MONTHS of its operating cost and its debt service (0.7.11, round 2). |
| 757 | 5 | `public void takeMonth(Equity register, HouseholdBalance households, Bank bank, Companies companies, double[] book, double world...` | The exchange's step: last month's orders withdrawn, every share valued, and every participant's orders posted - the desk, the emigrants, the world, the companies, the household cells - then the splits, and the bank's ... |
| 764 | 5 | `public void takeMonth(Equity register, HouseholdBalance households, Bank bank, Companies companies, double[] book, double world...` | ...with what the city's fund's bonds are worth this month (0.7.14); no yield on the city's paper (a fixture with none). |
| 777 | 60 | `public void takeMonth(Equity register, HouseholdBalance households, Bank bank, Companies companies, double[] book, double world...` | ...with what the city's fund's bonds are worth this month (0.7.14), for its mix: its orders are posted last, after the households', by its rule and then its hand (postFund()); and what the city's paper the households ... |
| 843 | 8 | `public void attach(Equity register, HouseholdBalance households, Bank bank, Companies companies, int month)` | Who the book settles against from now until the next step. |
| 857 | 5 | `private void handShortPaysToRegister()` | What the short has paid at the dividends over the city's life is the register's (Equity.getLifetimeShortPays()) and is saved here with the rest of the short (EM2): a load reads it into restoredShortPays, and the regis... |
| 864 | 14 | `private void noteBook(int c, OrderBook b)` | Adds a book's month, as the step closed it, to the market's record. |

### the desk (lines 879-1042)

| line | len | member | says |
|---:|---:|---|---|
| 882 | 27 | `private void postDesk()` | The desk makes a market around fair value, within its capital; its own shares only by its capital policy. |
| 917 | 16 | `public double[] deskExcess(Equity register)` | What the desk holds over its caps, shares, company by company (round 4): the larger of what is over POSITION_LIMIT in that company and its pro-rata part, by value at fair value, of what the book is over BOOK_LIMIT - b... |
| 938 | 1 | `public double getLastExcessOffered(int c)` |  |
| 939 | 1 | `public double getLifeExcessOffered()` |  |
| 940 | 1 | `public double getLifeExcessSold()` |  |
| 943 | 5 | `public double deskExcessOnOffer(int c)` | What of the desk's excess still rests on the book, at fair value, in one company - its asks at fair value (round 4); the Bank tab's figure. |
| 949 | 5 | `public double deskExcessOnOffer()` | ...in every company. |
| 955 | 3 | `private boolean isExcessPrice(int c, double price)` | An ask of the desk's at fair value, not at its ask half a spread over: the excess. |
| 974 | 12 | `private double deskCapacity(Equity register, int c, double price)` | What the desk can buy at this price, shares: of another company, what its bank's capital carries at the weight its book gives the desk (Bank.deskCanCarry(), the inventory at the mark) and never more than is not alread... |
| 996 | 9 | `private double[] deskLimits(Equity register, int c, double price)` | The desk's four limits in another company's shares at this price, shares, in BOUND_ order (round 3): what its capital carries (0.7.8), its room under POSITION_LIMIT in this company and under BOOK_LIMIT over all of the... |
| 1007 | 5 | `private double bookAtFair(Equity register)` | What the desk holds of every other company, at fair value: what BOOK_LIMIT reads. |
| 1014 | 4 | `public double deskPositionShare(Equity register, int c)` | The desk's holding of one company at fair value over the bank's equity, and its whole book's - for the screens and the playtest. |
| 1018 | 4 | `public double deskBookShare(Equity register)` |  |
| 1024 | 7 | `private double deskHolding(int c)` | What the desk can sell of this company: what it holds - and of the bank's own, new shares at the pace while the bank is under its target. |
| 1038 | 4 | `private static boolean ownSharesTrade(Equity register)` | ...AND NEVER ON A RECORD THAT IS NEW OR BAD - the register's own reading of the bank's last twelve months (Equity.getRegime()): a bank in its first year, or losing money in half the months of one, neither buys its own... |

### the emigrants (lines 1043-1054)

| line | len | member | says |
|---:|---:|---|---|
| 1046 | 8 | `private void postEmigrants()` | The month's leavers ask what the dealer's bid at fair value used to pay them. |

### the world (lines 1055-1117)

| line | len | member | says |
|---:|---:|---|---|
| 1068 | 24 | `private void postWorld(double worldRate)` | The world, on a share's expected return against its rate plus the premium (EM1: it read the dividend's yield before): a bid up to the price that returns the hurdle plus the tolerance - and never over fair value plus h... |
| 1103 | 9 | `private void postWorldPrice()` | THE WORLD SELLS INTO A BID OVER VALUE (EM1, 2026-10-29): where a bid rests more than BAND over a share's fair value and the world holds some, it sells OutwardInvestment.HOME_SPEED of what it holds at fair value plus B... |
| 1114 | 3 | `private double worldHolding(int c)` | What the world holds that it can sell: what is held abroad less this month's leavers' shares, which are theirs to sell. |

### the shorts (EM2) (lines 1118-1259)

| line | len | member | says |
|---:|---:|---|---|
| 1121 | 5 | `private double lendable(int c)` | What the city's long holders can lend of a company now, shares: the cells' holding and the fund's market book (never its rescue book). |
| 1128 | 3 | `private double shortRoom(int c)` | What the world may still sell short of a company, shares: under MAX_SHORT_INTEREST of what is outstanding and under what the lenders have left to lend. |
| 1138 | 4 | `public static double lendingRate(double utilisation)` | The fee a year on the shares lent, at a utilisation (the shares short over the shares lendable): LENDING_FEE up to HARD_TO_BORROW, rising linearly from there to LENDING_FEE + HARD_TO_BORROW_FEE when all of them are le... |
| 1158 | 26 | `private void strikeLending()` | THE LENDING BOOK AND ITS FEE, at each step before anybody posts: what is short is lent by the cells and the fund's market book pro rata to what each holds, and the world pays them lendingRate() of it a year, a twelfth... |
| 1190 | 5 | `private void cancelShortHeld(int c)` | A company with no shares in issue and a short open: every share still held is one the world sold short and nobody issued, so the cells', the desk's, the world's and the fund's market book go for nothing (Equity.cancel... |
| 1197 | 10 | `private void restrikeLentBook(int c)` | The lending book as the shorts' fills leave it (EM2): what is short, up to what they hold (LF1), lent by the cells and the fund pro rata, none with nothing to lend. |
| 1231 | 28 | `private void postShorts()` | THE WORLD SELLS SHORT (EM2), in every company valued on its record and worth something, the bank's own never: it reads the quote now, after the others have posted, and the position S it is short, and does the first of... |

### the companies (lines 1260-1321)

| line | len | member | says |
|---:|---:|---|---|
| 1294 | 27 | `private void postCompanies()` | A company past its equity target by OVER_TARGET, with cash past a cushion of its costs, bids for its own shares at fair value plus BUYBACK_TOLERANCE - never more than BUYBACK_PACE of what it is worth a year - and canc... |

### the households (lines 1322-1359)

| line | len | member | says |
|---:|---:|---|---|
| 1331 | 3 | `boolean mayBuy(Household c)` | Whether a cell may put money into shares at all: no debt, not locked out, not going short, investing at all. |
| 1336 | 1 | `private boolean owes(Household c)` | Whether a cell owes anything (mayBuy()): past the households' rounding floor where the balance is attached, else anything above nothing. |
| 1339 | 5 | `static double shortOfCushion(Household c)` | How far a cell's savings are under its cushion, every household of it: its excess over its share target (round 3). |
| 1347 | 1 | `public double getLifeRebalanceSold()` |  |
| 1351 | 1 | `public double getLifeValueSold()` |  |
| 1354 | 5 | `double spareOf(Household c)` | What a cell has past its cushion, in all: savings past SHARE_CUSHION_MONTHS of its take-home, every household of it. |

### the owner families sell slowly (BO1) (lines 1360-1584)

| line | len | member | says |
|---:|---:|---|---|
| 1375 | 10 | `private void postOwners()` | THE OWNER FAMILIES SELL SLOWLY. |
| 1433 | 115 | `private void postHouseholds(double depositRate, double worldRate, double paperYield)` | THE CELLS REBALANCE THEIR SHARES BY THE BOND RULE (0.7.12 round 3; Jerus: "Yes, same rule"): a cell over its target sells HOME_SPEED of the excess, a cell under it buys OUT_SPEED of the gap, as they do with the busine... |
| 1563 | 21 | `private int bestBuy(double[] inc, double[] limits, double floor, boolean[] tried, boolean offered)` | The company a buyer would put the next dollar in: the highest expected return over the floor (EM1; the dividend's yield before), at the best ask when `offered` (only a company with an ask it would take, under its limi... |

### the city's fund (0.7.14) (lines 1585-1751)

| line | len | member | says |
|---:|---:|---|---|
| 1605 | 10 | `private void postFundOverLimit()` | THE FUND HOLDS TO ITS 10% (RB1). |
| 1665 | 86 | `private void postFund()` | THE CITY'S FUND, BY ITS RULE AND THEN BY THE PLAYER'S HAND (TreasuryFund). |

### the cap and the hand's orders (0.7.39) (lines 1752-1842)

| line | len | member | says |
|---:|---:|---|---|
| 1777 | 12 | `public double handOnOrder(TreasuryFund fund, int c)` | The hand's buys on a company, in shares: those resting on its book, and the waiting orders' money over their price (the price named, or fair value as struck now). |
| 1791 | 5 | `public double fundRoom(Equity register, TreasuryFund fund, int c)` | The room a buy of the hand's has under the cap, and the rule's bid at the step: the cap less what the fund holds of the company and the hand's buys on order, never under nothing. |
| 1798 | 4 | `private double ruleRoom(int c)` | ...the most of the rule's bid that may still fill between steps: that room, or no limit with no buy of the hand's on the company. |
| 1804 | 10 | `public double fundShareRoom()` | The money the rule's share bids could still take under the caps (RB1): the room fundRoom() leaves on every listed company at the bid the rule posts, unlimited when there is no register or fund to read, so nothing is f... |
| 1821 | 7 | `public double fundCouldHold(Equity register, TreasuryFund fund, int c, double more)` | Everything the fund could hold of a company on its market book were every buy of its on it to fill, with `more` units of a new buy of the hand's among them: what it holds, the hand's buys, and the rule's bid on the bo... |
| 1830 | 5 | `public double cityValue(Equity register)` | What the city's fund holds of every company, both books, at the price: the fund's shares' mark. |
| 1837 | 5 | `public double cityMarketValue(Equity register)` | ...its market book alone. |

### A HOUSEHOLD SHORT OF MONEY SELLS, mid-month, in the waterfall (lines 1843-1899)

| line | len | member | says |
|---:|---:|---|---|
| 1859 | 36 | `double sellForHousehold(Equity register, Bank bank, HouseholdBalance households, Household cell, double needPer)` | A cell short of money asks, in each company it holds, the price at which the dividend it gives up yields its own borrowing rate - pro rata by what it holds at that price, for no more than it is short. |

### one order, settled (lines 1900-2185)

| line | len | member | says |
|---:|---:|---|---|
| 1909 | 3 | `List<OrderBook.Fill> tradeForCheck(int c, String who, OrderBook.Side side, double price, double quantity)` | ONE ORDER ON A COMPANY'S BOOK, SETTLED AGAINST THE CITY, between two steps: for FundLedgerCheck (0.7.39), which causes a seller or a buyer at a price it names and then the fund's order against it, and reads back the f... |
| 1918 | 5 | `void stampShortRecordForCheck(int c)` | A short's record set to figures a save has to carry back, none of them the same as another's, for SaveFileCheck's fixture (EM2): one company's lending fees this month and the life counters - a margin call, a recall an... |
| 1925 | 1 | `void splitForCheck()` | ...and the splits the step would make now: for FundLedgerCheck, after it has traded a share past SPLIT_AT. |
| 1927 | 5 | `private void submit(int c, String who, OrderBook.Side side, double price, double quantity)` |  |
| 1950 | 12 | `private void post(int c, String who, OrderBook.Side side, double reservation, double quantity)` | A PRICE-TAKER'S ORDER: it takes what is on offer up to its reservation - the most a buyer would pay for the yield it wants, the least a seller would take - at the resting orders' prices, and rests what is left AT THE ... |
| 1968 | 8 | `private static int classOf(String who)` |  |
| 1986 | 1 | `public int getLastDeskBound(int which)` |  |
| 1987 | 1 | `public int getLifeDeskBound(int which)` |  |
| 1988 | 1 | `public double getLifePostedSellBy(int who)` |  |
| 1989 | 1 | `public double getLifeFilledSellBy(int who)` |  |
| 1991 | 3 | `private OrderBook.Clearing clearing(int c)` |  |
| 1996 | 184 | **type** `private final class Settle implements OrderBook.Clearing` | Where each participant's money and shares are, for one company's book. |
| 1998 | 1 | `Settle(int c)` _(in Exchange.Settle)_ |  |
| 2000 | 36 | `public double capacity(String who, OrderBook.Side side, double price)` _(in Exchange.Settle)_ |  |
| 2045 | 6 | `public boolean mayTrade(String buyer, String seller)` _(in Exchange.Settle)_ | THE FUND NEVER BUYS A NEW ISSUE (0.7.14, Jerus's "new issues only"): the desk's asks in the bank's own shares are new shares the bank issues (deskHolding(), Bank.issuesOwnShares()), so they are passed over for the fun... |
| 2052 | 127 | `public void settle(String buyer, String seller, double q, double price)` _(in Exchange.Settle)_ |  |
| 2182 | 3 | `private Household cellOf(String who)` | The cell a participant's name is, or null. |

### SPLITS (lines 2186-2223)

| line | len | member | says |
|---:|---:|---|---|
| 2196 | 27 | `private void splitWhatNeedsIt()` | A split or a consolidation, by a power of ten, outside the wage band. |

### reading (lines 2224-2364)

| line | len | member | says |
|---:|---:|---|---|
| 2226 | 1 | `public double getSoldToHouseholds(int c)` |  |
| 2227 | 1 | `public double getBoughtFromHouseholds(int c)` |  |
| 2228 | 1 | `public double getSoldAbroad(int c)` |  |
| 2229 | 1 | `public double getBoughtFromAbroad(int c)` |  |
| 2230 | 1 | `public double getEmigrantsPaid(int c)` |  |
| 2231 | 1 | `public double getBuybackToHouseholds(int c)` |  |
| 2232 | 1 | `public double getBuybackToDesk(int c)` |  |
| 2233 | 1 | `public double getBuybackAbroad(int c)` |  |
| 2235 | 1 | `public double getHouseholdsBoughtAbroad(int c)` | The households' cells bought from the world or from leavers this month (0.7.12 round 2): their savings out, a financial outflow. |
| 2237 | 1 | `public double getHouseholdsSoldAbroad(int c)` | ...and sold to the world: a financial inflow into their savings. |
| 2239 | 1 | `public double getBetweenHouseholds(int c)` | What one cell paid another for shares this month: a transfer inside the households. |
| 2240 | 1 | `public int getBetweenHouseholdsTrades(int c)` |  |
| 2242 | 1 | `public double getVolume(int c)` | Shares that changed hands this month. |
| 2243 | 1 | `public double getLifetimeVolume()` |  |
| 2245 | 1 | `public double getSplit(int c)` | This month's split factor: 100 for a hundred-for-one, .01 for a consolidation, 0 for none. |
| 2247 | 1 | `public double getSplitFactor(int c)` | Shares today for one share at the founding. |
| 2249 | 1 | `public double pricePerFoundingShare(int c)` | The price per FOUNDING share: continuous through every split. |
| 2251 | 1 | `public double fairPerFoundingShare(int c)` | ...and the register's reckoning, the same way. |
| 2253 | 1 | `public double getEmigrantShares(int c)` | This month's leavers' shares still offered. |
| 2255 | 1 | `public double getBuybackBudget(int c)` | What a company's buyback bid may still spend this month. |
| 2258 | 1 | `public double deskResting(int c, OrderBook.Side side)` | What the desk has resting, shares, on one side of one company's book. |
| 2261 | 4 | `public double bestBidOf(int c, String who)` | The best price a participant has resting on a company's book, bid or ask - NaN with none. |
| 2265 | 4 | `public double bestAskOf(int c, String who)` |  |
| 2273 | 1 | `public double getLastPostedSellValue()` | At the last step, every book together: the value asked to sell (at the price), the value that filled, the sell orders posted and those that waited, and the trades. |
| 2274 | 1 | `public double getLastFilledValue()` |  |
| 2275 | 1 | `public int getLastSellsPosted()` |  |
| 2276 | 1 | `public int getLastSellsWaited()` |  |
| 2277 | 1 | `public int getLastTrades()` |  |
| 2279 | 1 | `public double getLifePostedSellValue()` | ...and over the city's life. |
| 2280 | 1 | `public double getLifeFilledValue()` |  |
| 2281 | 1 | `public int getLifeSellsPosted()` |  |
| 2282 | 1 | `public int getLifeSellsWaited()` |  |
| 2283 | 1 | `public int getLifeTrades()` |  |
| 2284 | 1 | `public double getLifeTurnover()` |  |
| 2285 | 1 | `public double getLifeBetweenHouseholds()` |  |
| 2287 | 1 | `public double getLifeEmigrantsOffered()` | What leavers offered on the way out, at their ask, and what they were paid, over the city's life. |
| 2288 | 1 | `public double getLifeEmigrantsPaid()` |  |
| 2289 | 1 | `public int getLifeBetweenHouseholdsTrades()` |  |
| 2291 | 1 | `private double sum(double[] a)` |  |
| 2292 | 1 | `public double getSoldToHouseholds()` |  |
| 2293 | 1 | `public double getBoughtFromHouseholds()` |  |
| 2294 | 1 | `public double getSoldAbroad()` |  |
| 2295 | 1 | `public double getBoughtFromAbroad()` |  |
| 2296 | 1 | `public double getEmigrantsPaid()` |  |
| 2297 | 1 | `public double getBuybackToHouseholds()` |  |
| 2298 | 1 | `public double getBuybackToDesk()` |  |
| 2299 | 1 | `public double getHouseholdsBoughtAbroad()` |  |
| 2300 | 1 | `public double getHouseholdsSoldAbroad()` |  |
| 2301 | 1 | `public double getBetweenHouseholds()` |  |
| 2302 | 1 | `public int getBetweenHouseholdsTrades()` |  |
| 2309 | 1 | `public double deskSoldToHouseholds()` | The desk's trading in the city's shares, the bank's own left out (0.7.9): each total less the bank's line, because its own shares are capital since 0.7.8 (Bank.buyBackOwnShares()), not the desk's trading. |
| 2311 | 1 | `public double deskSoldAbroad()` | ...sold abroad. |
| 2313 | 1 | `public double deskBoughtFromHouseholds()` | ...bought from the households. |
| 2315 | 1 | `public double deskBoughtFromAbroad()` | ...and bought from abroad. |
| 2316 | 1 | `public double getBuybackAbroad()` |  |
| 2317 | 1 | `public double getVolume()` |  |
| 2320 | 1 | `public double getFundBoughtFromHouseholds()` | The city's fund with the cells and the world this month (0.7.14): what it paid them and what they paid it - MoneyAudit's four lines. |
| 2321 | 1 | `public double getFundBoughtAbroad()` |  |
| 2322 | 1 | `public double getFundSoldToHouseholds()` |  |
| 2323 | 1 | `public double getFundSoldAbroad()` |  |
| 2328 | 1 | `public boolean isShortSelling()` | Whether the world sells short at all. |
| 2330 | 1 | `void setShortSelling(boolean on)` | Switches the world's short selling on or off: for the checks, and -Dplaytest.shorts=off, to see the market without it. |
| 2332 | 1 | `public double shortShares(int c)` | Shares the world is short of one company, and the collateral held against them. |
| 2333 | 1 | `public double shortCollateral(int c)` |  |
| 2335 | 1 | `public double lentByCells(int c)` | ...of those, the shares the households' cells lend and the fund's market book lends: they add to the shares short (lent by none, with none to lend). |
| 2336 | 1 | `public double lentByFund(int c)` |  |
| 2338 | 4 | `public double shortInterest(int c)` | The shares the world is short of one company as a share of those outstanding, for the sector screen (EM2): 0 with none short. |
| 2343 | 4 | `public double lendingRateOf(int c)` | The fee a year the lenders are paid on a company now: lendingRate() at the shares short over those lendable. |
| 2348 | 1 | `public double lendingFeeToCells(int c)` | What the world paid the cells and the fund this month for the shares lent, money. |
| 2349 | 1 | `public double lendingFeeToFund(int c)` |  |
| 2350 | 1 | `public double lendingFeeThisMonth(int c)` |  |
| 2352 | 1 | `public double getLendingFeeToFund()` | ...and in all companies, for MoneyAudit: the fund's part is cash in a pool, the cells' goes to their savings. |
| 2353 | 1 | `public double getLendingFeeToCells()` |  |
| 2355 | 1 | `public double getLifeShortSold()` | Over the city's life: shares sold short and what they fetched, shares covered and what that cost, company-months an ask was posted and a short stood open, margin calls and recalls, and the fees paid. |
| 2356 | 1 | `public double getLifeShortSoldValue()` |  |
| 2357 | 1 | `public double getLifeShortCovered()` |  |
| 2358 | 1 | `public double getLifeShortCoveredValue()` |  |
| 2359 | 1 | `public int getLifeShortAsks()` |  |
| 2360 | 1 | `public int getLifeShortMonths()` |  |
| 2361 | 1 | `public int getLifeMarginCalls()` |  |
| 2362 | 1 | `public int getLifeRecalls()` |  |
| 2363 | 1 | `public double getLifeLendingFees()` |  |

### saving (lines 2365-2634)

| line | len | member | says |
|---:|---:|---|---|
| 2379 | 53 | **type** `public static final class State implements Cloneable` | The market in a save (0.7.12, round 2), carried by Gson as it stands: every book with the orders resting on it - a cell's bid posted at the step is what the next month's waterfall sells into - fair value and the split... |
| 2405 | 26 | `State copy()` _(in Exchange.State)_ | A copy that shares nothing with this state (UP1, 2026-10-22): toState() lists the exchange's own books, which the months go on trading into while the autosave is written. |
| 2433 | 43 | `public State toState()` |  |
| 2478 | 59 | `public boolean restore(State s)` |  |
| 2546 | 19 | `public boolean restore(String[] keys, double[] saved)` | A save from before the book (0.7.12 round 1 and earlier): the dealer's array, SLOTS a company - its quote, fair value, the demand it carried and the split factor. |
| 2567 | 4 | `public void reopen(Bank bank)` | After a load: whether the desk posts is the bank's to say, read off the bank as the next step will. |
| 2572 | 32 | `public void reset()` |  |
| 2606 | 28 | `public void redenominate(double scale)` | Prices and the month's cash in the new unit; share counts do not move. |

