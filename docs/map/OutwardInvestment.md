# OutwardInvestment.java - 706 lines · 46 methods · 5 constants · model

`ham/citybuildersim/OutwardInvestment.java` - generated 2026-10-25 by CodeMap; line numbers are as of that run.

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
> world's rate abroad and comes back as income. Jerus's call, from four
> options: "outward investment".
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

**Uses:** [EconomyManager](EconomyManager.md) (12), [Sectors](Sectors.md) (10), [ForeignAccounts](ForeignAccounts.md) (3), [BusinessDebtManager](BusinessDebtManager.md) (2)

**Used by (15):** [Auction](Auction.md), [AuctionCheck](AuctionCheck.md), [BondCheck](BondCheck.md), [BondMarket](BondMarket.md), [Buyback](Buyback.md), [BuybackCheck](BuybackCheck.md), [CapitalFlowCheck](CapitalFlowCheck.md), [EconomyManager](EconomyManager.md), [Exchange](Exchange.md), [ExchangeCheck](ExchangeCheck.md), [Game](Game.md), [HoldersCheck](HoldersCheck.md), [HouseholdBalance](HouseholdBalance.md), [LongPlaytest](LongPlaytest.md), [TradeScreen](TradeScreen.md)

## Sections

| line | section |
|---:|---|
| 62 | HOW MUCH WANTS TO GO |
| 94 | THE ROUNDING FLOOR, AND WHY IT IS SEEDED (2026-09-13) |
| 126 | · state |
| 166 | · the month |
| 196 | · · the income, first |
| 215 | · · the target |
| 225 | · · the move |
| 295 | · reading |
| 346 | THE CITY'S PAPER (CD2, slice 4, D33) |
| 608 | · save and restore |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 79 | `OutwardInvestment.APPETITE` | `40` | The share of a sector's wealth it holds abroad, per unit of spread. |
| 82 | `OutwardInvestment.MAX_SHARE` | `.9` | Never more than this. |
| 89 | `OutwardInvestment.OUT_SPEED` | `.04` | How much of the gap to its target moves abroad in a month. |
| 92 | `OutwardInvestment.HOME_SPEED` | `.10` | ...and how much comes home in a month, which is faster, because it is needed. |
| 116 | `OutwardInvestment.MIN_MOVE` | `1e-9` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 119 | `private double minMove` | The same floor in today's money. |
| 129 | `private final Map<String, Double> usd` | Held abroad, per sector, in the dollars it is held in. |
| 132 | `private final Map<String, Double> moved` | This month's move, per sector, in local money: positive went abroad, negative came home. |
| 135 | `private final Map<String, Double> recalled` | Brought home on demand earlier this month, per sector, in local money. |
| 138 | `private final Map<String, Double> interest` | This month's income from abroad, per sector, in local money, rolled where it was earned. |
| 147 | `private double lastRate` | Local currency per dollar the stock was last valued at - the rate the month was traded at. |
| 150 | `private final Map<String, Double> paper` | The city's paper held, per sector, at face, in local money (CD2, slice 4). |
| 152 | `private double spread` | what the world pays over the bank, annual |
| 153 | `private double targetShare` | what the world pays over the bank, annual |
| 154 | `private double lifetimeOut, lifetimeHome, lifetimeInterest` | of wealth, abroad |
| 155 | `private double peakUsd` | local money |
| 391 | `private PaperDesk paperDesk` |  |
| 402 | `private double paperRatio` | What a dollar of face of the businesses' paper is worth this month (the businesses' book at the curve over its face, DebtManager.firmBookRatio()) and what that paper yields at today's curve (firmBookYield()): struck o... |
| 403 | `private double paperYield` |  |
| 414 | `private double lifetimePaperBought, lifetimePaperSold` | Bought, and sold to the desk, over the city's life, in cash. |
| 426 | `private final Map<String, Double> paperSpent` | THE MONTH'S CASH ON THE PAPER, per sector, for the sector's books: what it spent on it net of what came back (bought at the settle, less what it sold the desk, was repaid and was paid in a buyback) and the coupons it ... |
| 427 | `private final Map<String, Double> paperCoupons` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 60 | 647 | **type** `public class OutwardInvestment` | Outward investment: what the city's businesses do with money the bank will not pay for. |

### HOW MUCH WANTS TO GO (lines 62-93)

### THE ROUNDING FLOOR, AND WHY IT IS SEEDED (2026-09-13) (lines 94-125)

| line | len | member | says |
|---:|---:|---|---|
| 122 | 3 | `public void seedConstants(double unit)` | Re-seeds the floor at a given unit. |

### state (lines 126-165)

| line | len | member | says |
|---:|---:|---|---|
| 157 | 8 | `public OutwardInvestment()` |  |

### the month (lines 166-294)

| line | len | member | says |
|---:|---:|---|---|
| 181 | 74 | `public void takeMonth(double depositRate, double worldRate, double rate, EconomyManager economy)` | Moves the month's money, in both directions, and rolls the month's income. |
| 275 | 19 | `public double recall(String sector, double local, EconomyManager economy)` | Brings money home because it is needed now - to build, or to pay the owners - rather than because the bank pays more. |

### reading (lines 295-345)

| line | len | member | says |
|---:|---:|---|---|
| 298 | 1 | `public double getUsd(String sector)` | Dollars held abroad by one sector. |
| 301 | 1 | `public double localValue(String sector)` | ...and what they are worth in the city's money, at the rate they were last valued at. |
| 303 | 5 | `public double totalUsd()` |  |
| 309 | 1 | `public double totalLocalValue()` |  |
| 312 | 1 | `public double getMovedThisMonth(String sector)` | This month's move for one sector, local money: positive went abroad. |
| 315 | 1 | `public double getInterestThisMonth(String sector)` | This month's income from abroad for one sector, local money - earned and reinvested there, not in the till. |
| 318 | 5 | `public double getInvestedAbroadThisMonth()` | Everything that went abroad this month, all sectors, local money. |
| 325 | 5 | `public double getBroughtHomeThisMonth()` | Everything that came home this month, all sectors, local money. |
| 332 | 5 | `public double getInterestThisMonth()` | Everything the world paid the city's businesses this month, local money, reinvested where it was paid. |
| 338 | 1 | `public double getSpread()` |  |
| 339 | 1 | `public double getTargetShare()` |  |
| 340 | 1 | `public double getLastRate()` |  |
| 341 | 1 | `public double getLifetimeOut()` |  |
| 342 | 1 | `public double getLifetimeHome()` |  |
| 343 | 1 | `public double getLifetimeInterest()` |  |
| 344 | 1 | `public double getPeakUsd()` |  |

### THE CITY'S PAPER (CD2, slice 4, D33) (lines 346-607)

| line | len | member | says |
|---:|---:|---|---|
| 386 | 4 | **type** `public interface PaperDesk` | Where the businesses' paper is sold: Game's desk books the bank's end and takes the face off every piece's businesses' share pro rata. |
| 388 | 1 | `void buy(double face, double cash)` _(in OutwardInvestment.PaperDesk)_ | The desk buys this much face of the businesses' paper for this much cash. |
| 393 | 1 | `public void setPaperDesk(PaperDesk desk)` |  |
| 405 | 4 | `public void setPaperBook(double ratio, double bookYield)` |  |
| 410 | 1 | `public double getPaperRatio()` |  |
| 411 | 1 | `public double getPaperYield()` |  |
| 430 | 4 | `public void startMonth()` | Starts the month's figures over: Game calls it as the month opens. |
| 436 | 1 | `public double getPaperSpentThisMonth(String sector)` | Cash a sector spent on the city's paper this month, net: positive bought, negative sold or repaid. |
| 439 | 1 | `public double getPaperCouponsThisMonth(String sector)` | Coupons the city paid a sector on its paper this month. |
| 441 | 1 | `public double getLifetimePaperBought()` |  |
| 442 | 1 | `public double getLifetimePaperSold()` |  |
| 445 | 1 | `public double paperOf(String sector)` | The city's paper one sector holds, at face. |
| 448 | 5 | `public double totalPaper()` | ...and every sector's, at face: DebtManager.firmPrincipal() from the other side. |
| 455 | 5 | `private boolean mayHold(String sector, double cash, EconomyManager economy)` | True when a sector may hold more of it: the rule that sends dollars abroad - money in the till and nothing owed (WHO GOES). |
| 466 | 10 | `public double idleWealth(EconomyManager economy)` | The businesses' idle money, for the auction (Auction.Bidders.firmsIdle): the tills and the dollars abroad, at the rate they were last valued at, of every sector that may hold the city's paper. |
| 484 | 9 | `public double bidOf(String sector, double yield, double depositRate, double worldRate, EconomyManager economy)` | What one sector would bid for the city's paper at this yield: Auction.firmsAt() for the one sector - a share of its idle money, APPETITE per unit of what the paper pays over the better of the deposit rate and the worl... |
| 501 | 20 | `public double[] allotmentWeights(double yield, double depositRate, double worldRate, EconomyManager economy)` | How a sale at this yield is shared among the sectors, in Sectors.KEYS order, summing to one: each sector's bid over all of them. |
| 523 | 7 | `public void takeAllotment(String sector, double cash, double face, EconomyManager economy)` | A sector pays this cash out of its till at the settle for this face of the city's paper (Game.firmsTakeTheirShare(), which has capped it at the till). |
| 532 | 11 | `public void creditPaperCoupon(double total, EconomyManager economy)` | A coupon on the businesses' share, into the tills pro rata to the paper each sector holds. |
| 545 | 3 | `public void creditPaperPrincipal(double face, EconomyManager economy)` | Principal on their share, into the tills with the paper down by the same, pro rata. |
| 554 | 12 | `public void creditPaperBuyback(double face, double price, EconomyManager economy)` | A bond bought back: this much of the businesses' face off the paper, this much of the price into the tills, each sector's in proportion to the paper it holds. |
| 573 | 13 | `private double sellPaper(String sector, double face, EconomyManager economy)` | A sector sells up to this much face of the city's paper to the desk, at the month's market value, into its till. |
| 596 | 11 | `private void sellExcessPaper(String sector, double depositRate, double worldRate, double rate, boolean borrower, EconomyManager...` | THE SPREAD GONE, and the borrower: a sector's target for the city's paper is its rule at the paper's book yield - a share of its idle money for each unit the paper pays over the better of the deposit rate and the worl... |

### save and restore (lines 608-706)

| line | len | member | says |
|---:|---:|---|---|
| 619 | 23 | `public double[] toSaveArray()` | Seven headline figures, then three per sector in SECTORS order - and since CD2 (slice 4) the city's paper: one per sector, then the ratio and the yield it was struck at and what the businesses have bought and sold ove... |
| 643 | 24 | `public void restore(double[] saved)` |  |
| 668 | 15 | `public void reset()` |  |
| 689 | 17 | `public void redenominate(double scale)` | A reform divides the local money and leaves the dollars alone. |

