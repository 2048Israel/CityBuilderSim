# OutwardInvestment.java - 753 lines · 47 methods · 5 constants · model

`ham/citybuildersim/OutwardInvestment.java` - generated 2026-10-30 by CodeMap; line numbers are as of that run.

> Outward investment: what the city's businesses do with money the bank will
> not pay for.
> 
> THE MIRROR OF HOT MONEY. CapitalFlows is foreign savings coming here for a
> spread; this is the city's own savings going abroad for one. A treasury
> sitting on a billion that the bank pays nothing on, while the world pays two
> percent, does what every corporate treasurer does with idle cash - it buys
> the world's paper - and brings it home when the bank pays better again.
> 
> WHY IT EXISTS (2026-09-10). Every long run in this game was a steel economy
> and every steel economy cycled on the currency: the city exported ore and
> steel, imported scrap and a little food, and ran a surplus of 30-50% of GDP
> that NOTHING recycled - no household imported anything but food, no company
> paid a dividend, no saver could hold a foreign asset, and the treasury only
> bought reserves to back hot money it did not have. So the float did the one
> thing a float can do with a surplus nobody recycles: it appreciated at ten
> percent a year until the exporters were dead, the bank ate their loans, the
> city recapitalised it, the price recovered and it went round again. Twenty
> cycles in 4,000 months; $4.5-7.9bn written off a run. The same eight seeds
> with the rate pinned produced a city twice the size with the spread across
> seeds collapsed. See claude/the-surplus-has-nowhere-to-go.md.
> 
> In the balance of payments this is a financial outflow, which is the thing
> that offsets a current-account surplus in every real surplus economy - the
> Norwegians, the Swiss, the Singaporeans all run one. It reaches the currency
> through ForeignAccounts, which prices the rate on the overall balance now
> rather than the trade balance alone, and the money that leaves earns the
> world's rate abroad; that coupon is struck as a line of the business's
> profit and kept abroad, growing the dollars it holds (HD2, 2026-10-30;
> rolled where it was earned, in no statement, from 2026-09-10 to HD1, and
> paid home into the till by HD1). Jerus's call, from four options:
> "outward investment".
> 
> WHAT IT IS NOT. Not a dividend - the sector still owns the money, abroad
> rather than at the counter, and its balance sheet carries it. Not hot money
> in reverse - hot money is a stranger's money with a panic clock; this is the
> owner's, and the owner is in no hurry either way. And not the household's
> savings, yet: the $2bn households hold is real and should follow the same
> rule, but it lives inside HouseholdBalance's own machinery and is a second
> change.
> 
> WHO GOES. A sector with money in the bank and nothing owed to it. A sector
> carrying a loan at six percent has no business lending abroad at two, and a
> sector in overdraft brings what it has home first - so the rule is also the
> rule that keeps a borrower liquid, which is worth having on its own.
> 
> AND THE CITY'S OWN PAPER (CD2, slice 4, D33). The same treasurer has a third
> place for idle cash: the paper the city's own treasury sells at auction
> (Auction.firmsAt() is this class's rule with that paper beside the deposit
> and the world). The sector buys it at the settle out of its till, is paid
> its coupon and its principal into the till, and sells it to the bank's desk
> when the spread has gone, when it borrows, or when it needs the money - by
> the rule that sends the dollars home. See THE CITY'S PAPER.

**Uses:** [EconomyManager](EconomyManager.md) (13), [Sectors](Sectors.md) (11), [ForeignAccounts](ForeignAccounts.md) (3), [BusinessDebtManager](BusinessDebtManager.md) (2), [Sector](Sector.md) (1)

**Used by (16):** [Auction](Auction.md), [AuctionCheck](AuctionCheck.md), [BondCheck](BondCheck.md), [BondMarket](BondMarket.md), [Buyback](Buyback.md), [BuybackCheck](BuybackCheck.md), [CapitalFlowCheck](CapitalFlowCheck.md), [EconomyManager](EconomyManager.md), [Exchange](Exchange.md), [ExchangeCheck](ExchangeCheck.md), [Game](Game.md), [HoldersCheck](HoldersCheck.md), [HouseholdBalance](HouseholdBalance.md), [LongPlaytest](LongPlaytest.md), [MarketCheck](MarketCheck.md), [TradeScreen](TradeScreen.md)

## Sections

| line | section |
|---:|---|
| 65 | HOW MUCH WANTS TO GO |
| 97 | THE ROUNDING FLOOR, AND WHY IT IS SEEDED (2026-09-13) |
| 129 | · state |
| 169 | · the month |
| 171 | A LINE OF PROFIT, KEPT ABROAD (HD2, 2026-10-30) |
| 263 | · · the target |
| 273 | · · the move |
| 342 | · reading |
| 393 | THE CITY'S PAPER (CD2, slice 4, D33) |
| 655 | · save and restore |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 82 | `OutwardInvestment.APPETITE` | `40` | The share of a sector's wealth it holds abroad, per unit of spread. |
| 85 | `OutwardInvestment.MAX_SHARE` | `.9` | Never more than this. |
| 92 | `OutwardInvestment.OUT_SPEED` | `.04` | How much of the gap to its target moves abroad in a month. |
| 95 | `OutwardInvestment.HOME_SPEED` | `.10` | ...and how much comes home in a month, which is faster, because it is needed. |
| 119 | `OutwardInvestment.MIN_MOVE` | `1e-9` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 122 | `private double minMove` | The same floor in today's money. |
| 132 | `private final Map<String, Double> usd` | Held abroad, per sector, in the dollars it is held in. |
| 135 | `private final Map<String, Double> moved` | This month's move, per sector, in local money: positive went abroad, negative came home. |
| 138 | `private final Map<String, Double> recalled` | Brought home on demand earlier this month, per sector, in local money. |
| 141 | `private final Map<String, Double> interest` | This month's income from abroad, per sector, in local money: the coupon payCoupons() struck as a line of the profit and kept abroad (HD2). |
| 150 | `private double lastRate` | Local currency per dollar the stock was last valued at - the rate the month was traded at. |
| 153 | `private final Map<String, Double> paper` | The city's paper held, per sector, at face, in local money (CD2, slice 4). |
| 155 | `private double spread` | what the world pays over the bank, annual |
| 156 | `private double targetShare` | what the world pays over the bank, annual |
| 157 | `private double lifetimeOut, lifetimeHome, lifetimeInterest` | of wealth, abroad |
| 158 | `private double peakUsd` | local money |
| 438 | `private PaperDesk paperDesk` |  |
| 449 | `private double paperRatio` | What a dollar of face of the businesses' paper is worth this month (the businesses' book at the curve over its face, DebtManager.firmBookRatio()) and what that paper yields at today's curve (firmBookYield()): struck o... |
| 450 | `private double paperYield` |  |
| 461 | `private double lifetimePaperBought, lifetimePaperSold` | Bought, and sold to the desk, over the city's life, in cash. |
| 473 | `private final Map<String, Double> paperSpent` | THE MONTH'S CASH ON THE PAPER, per sector, for the sector's books: what it spent on it net of what came back (bought at the settle, less what it sold the desk, was repaid and was paid in a buyback) and the coupons it ... |
| 474 | `private final Map<String, Double> paperCoupons` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 63 | 691 | **type** `public class OutwardInvestment` | Outward investment: what the city's businesses do with money the bank will not pay for. |

### HOW MUCH WANTS TO GO (lines 65-96)

### THE ROUNDING FLOOR, AND WHY IT IS SEEDED (2026-09-13) (lines 97-128)

| line | len | member | says |
|---:|---:|---|---|
| 125 | 3 | `public void seedConstants(double unit)` | Re-seeds the floor at a given unit. |

### state (lines 129-168)

| line | len | member | says |
|---:|---:|---|---|
| 160 | 8 | `public OutwardInvestment()` |  |

### the month (lines 169-170)

### A LINE OF PROFIT, KEPT ABROAD (HD2, 2026-10-30) (lines 171-341)

| line | len | member | says |
|---:|---:|---|---|
| 218 | 14 | `public void payCoupons(double worldRate, double rate, EconomyManager economy)` | Strikes the month's coupon on every sector's dollars abroad and hands it to the sector, to be a line of its statement, inside its profit: the dollars held times the world's rate over twelve, at this month's rate. |
| 248 | 54 | `public void takeMonth(double depositRate, double worldRate, double rate, EconomyManager economy)` | Moves the month's money, in both directions. |
| 322 | 19 | `public double recall(String sector, double local, EconomyManager economy)` | Brings money home because it is needed now - to build, or to pay the owners - rather than because the bank pays more. |

### reading (lines 342-392)

| line | len | member | says |
|---:|---:|---|---|
| 345 | 1 | `public double getUsd(String sector)` | Dollars held abroad by one sector. |
| 348 | 1 | `public double localValue(String sector)` | ...and what they are worth in the city's money, at the rate they were last valued at. |
| 350 | 5 | `public double totalUsd()` |  |
| 356 | 1 | `public double totalLocalValue()` |  |
| 359 | 1 | `public double getMovedThisMonth(String sector)` | This month's move for one sector, local money: positive went abroad. |
| 362 | 1 | `public double getInterestThisMonth(String sector)` | This month's income from abroad for one sector, local money - the coupon payCoupons() struck: inside its profit and kept abroad, not in its till (HD2). |
| 365 | 5 | `public double getInvestedAbroadThisMonth()` | Everything that went abroad this month, all sectors, local money. |
| 372 | 5 | `public double getBroughtHomeThisMonth()` | Everything that came home this month, all sectors, local money. |
| 379 | 5 | `public double getInterestThisMonth()` | Everything the world paid the city's businesses this month, local money, inside their profits and kept abroad (HD2). |
| 385 | 1 | `public double getSpread()` |  |
| 386 | 1 | `public double getTargetShare()` |  |
| 387 | 1 | `public double getLastRate()` |  |
| 388 | 1 | `public double getLifetimeOut()` |  |
| 389 | 1 | `public double getLifetimeHome()` |  |
| 390 | 1 | `public double getLifetimeInterest()` |  |
| 391 | 1 | `public double getPeakUsd()` |  |

### THE CITY'S PAPER (CD2, slice 4, D33) (lines 393-654)

| line | len | member | says |
|---:|---:|---|---|
| 433 | 4 | **type** `public interface PaperDesk` | Where the businesses' paper is sold: Game's desk books the bank's end and takes the face off every piece's businesses' share pro rata. |
| 435 | 1 | `void buy(double face, double cash)` _(in OutwardInvestment.PaperDesk)_ | The desk buys this much face of the businesses' paper for this much cash. |
| 440 | 1 | `public void setPaperDesk(PaperDesk desk)` |  |
| 452 | 4 | `public void setPaperBook(double ratio, double bookYield)` |  |
| 457 | 1 | `public double getPaperRatio()` |  |
| 458 | 1 | `public double getPaperYield()` |  |
| 477 | 4 | `public void startMonth()` | Starts the month's figures over: Game calls it as the month opens. |
| 483 | 1 | `public double getPaperSpentThisMonth(String sector)` | Cash a sector spent on the city's paper this month, net: positive bought, negative sold or repaid. |
| 486 | 1 | `public double getPaperCouponsThisMonth(String sector)` | Coupons the city paid a sector on its paper this month. |
| 488 | 1 | `public double getLifetimePaperBought()` |  |
| 489 | 1 | `public double getLifetimePaperSold()` |  |
| 492 | 1 | `public double paperOf(String sector)` | The city's paper one sector holds, at face. |
| 495 | 5 | `public double totalPaper()` | ...and every sector's, at face: DebtManager.firmPrincipal() from the other side. |
| 502 | 5 | `private boolean mayHold(String sector, double cash, EconomyManager economy)` | True when a sector may hold more of it: the rule that sends dollars abroad - money in the till and nothing owed (WHO GOES). |
| 513 | 10 | `public double idleWealth(EconomyManager economy)` | The businesses' idle money, for the auction (Auction.Bidders.firmsIdle): the tills and the dollars abroad, at the rate they were last valued at, of every sector that may hold the city's paper. |
| 531 | 9 | `public double bidOf(String sector, double yield, double depositRate, double worldRate, EconomyManager economy)` | What one sector would bid for the city's paper at this yield: Auction.firmsAt() for the one sector - a share of its idle money, APPETITE per unit of what the paper pays over the better of the deposit rate and the worl... |
| 548 | 20 | `public double[] allotmentWeights(double yield, double depositRate, double worldRate, EconomyManager economy)` | How a sale at this yield is shared among the sectors, in Sectors.KEYS order, summing to one: each sector's bid over all of them. |
| 570 | 7 | `public void takeAllotment(String sector, double cash, double face, EconomyManager economy)` | A sector pays this cash out of its till at the settle for this face of the city's paper (Game.firmsTakeTheirShare(), which has capped it at the till). |
| 579 | 11 | `public void creditPaperCoupon(double total, EconomyManager economy)` | A coupon on the businesses' share, into the tills pro rata to the paper each sector holds. |
| 592 | 3 | `public void creditPaperPrincipal(double face, EconomyManager economy)` | Principal on their share, into the tills with the paper down by the same, pro rata. |
| 601 | 12 | `public void creditPaperBuyback(double face, double price, EconomyManager economy)` | A bond bought back: this much of the businesses' face off the paper, this much of the price into the tills, each sector's in proportion to the paper it holds. |
| 620 | 13 | `private double sellPaper(String sector, double face, EconomyManager economy)` | A sector sells up to this much face of the city's paper to the desk, at the month's market value, into its till. |
| 643 | 11 | `private void sellExcessPaper(String sector, double depositRate, double worldRate, double rate, boolean borrower, EconomyManager...` | THE SPREAD GONE, and the borrower: a sector's target for the city's paper is its rule at the paper's book yield - a share of its idle money for each unit the paper pays over the better of the deposit rate and the worl... |

### save and restore (lines 655-753)

| line | len | member | says |
|---:|---:|---|---|
| 666 | 23 | `public double[] toSaveArray()` | Seven headline figures, then three per sector in SECTORS order - and since CD2 (slice 4) the city's paper: one per sector, then the ratio and the yield it was struck at and what the businesses have bought and sold ove... |
| 690 | 24 | `public void restore(double[] saved)` |  |
| 715 | 15 | `public void reset()` |  |
| 736 | 17 | `public void redenominate(double scale)` | A reform divides the local money and leaves the dollars alone. |

