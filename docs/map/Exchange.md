# Exchange.java - 1,006 lines · 73 methods · 23 constants · model

`ham/citybuildersim/Exchange.java` - generated 2026-09-23 by CodeMap; line numbers are as of that run.

> The stock exchange: where a share changes hands, and at what price.
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
> ==================== THE BANK IS THE DEALER ====================
> 
> Jerus: "liquidity, that's going to be an issue, we need to solve it
> realistically via bank something, right?" Right. Every trade here is
> with the bank's trading desk. It quotes a bid and an ask around what a
> share is worth, takes the other side of whatever comes, holds what it
> bought, and moves its quotes as its book fills: long, it quotes lower to
> find buyers; with buyers it could not fill, it quotes higher to find
> sellers. It earns the spread, it is paid dividends on what it holds, and
> it loses money on a crash the way a dealer does. The desk is an asset on
> the bank's balance sheet, weighted against its capital dearer than a loan
> (Bank.RISK_EQUITY), so a bank stuffed with shares lends less - which is
> what a bank stuffed with shares does.
> 
> WHAT A SHARE IS WORTH is the register's own reckoning - book, or the
> dividend capitalised at what the world asks, whichever is more (see
> Equity.fairValue) - and the desk's book pushes the quote away from it:
> 
>     mid = fair x (1 - PRESSURE x (inventory - unfilled demand) / limit)
> 
> where the limit is the position the bank's capital allows in this company
> and the unfilled demand is what buyers wanted and could not get, fading by
> half a month. THE DESK NEVER SELLS WHAT IT DOES NOT HOLD. The first
> version let it run short to its limit, and a short position in a company
> whose book compounds is a liability that compounds: marked at the ceiling
> as fair value rose, seven companies' worth of it took the bank's equity to
> minus five trillion, its creditors absorbed the hole every month, and the
> desk paid dividends on shares that did not exist out of money that did not
> either - $4M a month to the households, a currency at its ceiling.
> Measured, months 1,200-1,500. A buyer the desk cannot fill waits, and the
> price it waits at goes up until somebody sells - the world into strength,
> a household short of money, or the company itself, at the market's price.
> 
> THE DESK CARRIES WHAT IT HOLDS AT THE QUOTE OR AT FAIR VALUE, WHICHEVER IS
> LOWER. A dealer does not mark its own book up on a quote nobody has paid
> yet. The second version marked at the mid, and the mid, lifted to five
> times fair value by demand the desk could not fill, was gone the month the
> demand was forgotten: $75M of trading gain one month, $75M of loss the
> next, on $55M of inventory, for four hundred months - and every loss was
> made good by the city, $40bn of it, while the gains had paid the bank's
> dividend. Measured, seed 0, month 3,540 on. Marked low, a rising quote is
> a gain when it is sold into and not before.
> 
> The desk buys up to twice its limit and then stops: a dealer that kept
> buying in a crash would be the crash. A failed bank quotes nothing, and a
> city with a dead bank has no market: an emigrant takes their shares with
> them, as before there was one.
> 
> ==================== WHO TRADES, AND WHY ====================
> ... (43 more lines in the source)

**Uses:** [Equity](Equity.md) (37), [Bank](Bank.md) (8), [HouseholdBalance](HouseholdBalance.md) (5), [Household](Household.md) (1)

**Used by (10):** [BankCheck](BankCheck.md), [BankScreen](BankScreen.md), [ExchangeCheck](ExchangeCheck.md), [Game](Game.md), [HistorySave](HistorySave.md), [HouseholdBalance](HouseholdBalance.md), [LongPlaytest](LongPlaytest.md), [PeopleScreen](PeopleScreen.md), [SectorScreen](SectorScreen.md), [TradeScreen](TradeScreen.md)

## Sections

| line | section |
|---:|---|
| 110 | · the dials |
| 169 | AND A DEAD BAND ON THE LINE ITSELF (2026-09-13) |
| 189 | AND A DEAD BAND ON THE RANKING (2026-09-15) |
| 269 | WHAT COUNTS AS A DEALER HAVING CAPITAL (2026-09-13) |
| 317 | · the book |
| 376 | THE QUOTE |
| 446 | THE DEALS. Each is one trade with the desk at the month's quote; the |
| 553 | THE MONTH |
| 812 | THE DISTRESS SALE, mid-month: a household short of money sells at the |
| 855 | · reading |
| 895 | · saving |
| 968 | · AND THE DESK'S OWN ROOM (2026-09-17) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 113 | `Exchange.SPREAD` | `.02` | Ask over bid, as a share of the mid. |
| 116 | `Exchange.PRESSURE` | `.25` | How far a full position moves the quote from fair value. |
| 119 | `Exchange.POSITION_LIMIT` | `.25` | The desk's position in one company, as a share of the bank's equity at fair value. |
| 131 | `Exchange.BOOK_LIMIT` | `.50` | ...and its whole book, all companies together, as a share of the bank's equity at fair value. |
| 134 | `Exchange.FLOOR` | `.50, CEILING = 5.0` | The quote never leaves this band round fair value, whatever the book. |
| 143 | `Exchange.MIN_LIMIT_OF_FLOAT` | `.02` | The quote reads the desk's position against at least this share of the float, however small the bank. |
| 146 | `Exchange.CAPACITY` | `2.0` | The most the desk will hold, as a multiple of its position limit. |
| 149 | `Exchange.DEMAND_DECAY` | `.50` | What is left of a month's unfilled demand the next month, in the quote. |
| 152 | `Exchange.FOREIGN_SPEED` | `.10` | What the world moves in a month per unit of yield over or under its hurdle, as a share of the float. |
| 155 | `Exchange.FOREIGN_TOLERANCE` | `.10` | ...and it holds still while the yield is within this of the hurdle. |
| 158 | `Exchange.MONTHLY_SHARE_OF_EXCESS` | `.05` | A household's monthly buying: this share of what is past its cushion. |
| 161 | `Exchange.HOUSEHOLD_PREMIUM` | `.01` | ...into a yield at least this far over the deposit rate, annual. |
| 164 | `Exchange.BUYBACK_CUSHION_MONTHS` | `6` | A company keeps this many months of operating cost before it buys back. |
| 167 | `Exchange.OVER_TARGET` | `.10` | Equity this far past target before a company buys back, as a share of assets. |
| 187 | `Exchange.MIN_EXCESS` | `1e-9` |  |
| 229 | `Exchange.TIED_YIELD` | `1e-9` |  |
| 232 | `Exchange.BUYBACK_PACE` | `.10` | The most a company retires in a year, as a share of what it is worth. |
| 235 | `Exchange.BUYBACK_TOLERANCE` | `.10` | A company buys back only while the ask is within this of fair value; past it, the money is a special dividend. |
| 238 | `Exchange.SPLIT_AT` | `100` | A share quoted at this many times its founding price is split; at one over it, consolidated. |
| 264 | `Exchange.MIN_FAIR` | `1e-9` | The least a share may be worth before the desk treats the company as worthless. |
| 300 | `Exchange.MIN_DEALER_EQUITY` | `1.0` |  |
| 898 | `Exchange.SLOTS_BEFORE_SPLITS` | `3` | Slots per company before the split factor joined (the exchange's first night). |
| 905 | `Exchange.SLOTS` | `SLOTS_BEFORE_SPLITS + 1` | The quote, the demand it carries and the split factor are STOCKS: the next month trades at the first two before anything re-quotes, and the price history is read through the third. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 267 | `private double minFair` | The same floor in today's money. |
| 303 | `private double minDealerEquity` | The same floor in today's money. |
| 319 | `private final int n` |  |
| 320 | `private final double[] fair` | register's reckoning, per share |
| 321 | `private final double[] mid` | register's reckoning, per share |
| 322 | `private final double[] limit` | the desk's quote, per share |
| 323 | `private final double[] demand` | position limit, in shares |
| 326 | `private final double[] soldToHouseholds` | this month |
| 327 | `private final double[] boughtFromHouseholds` | cash |
| 328 | `private final double[] soldAbroad` |  |
| 329 | `private final double[] boughtFromAbroad` |  |
| 330 | `private final double[] emigrantsPaid` |  |
| 331 | `private final double[] buybackToHouseholds` |  |
| 332 | `private final double[] buybackToDesk` |  |
| 333 | `private final double[] buybackAbroad` |  |
| 334 | `private final double[] specialDividend` |  |
| 335 | `private final double[] volume` | shares |
| 336 | `private final double[] lastMid` | shares |
| 337 | `private final double[] unfilled` | shares wanted and not sold, this month |
| 338 | `private final double[] split` | shares wanted and not sold, this month |
| 346 | `private final double[] splitFactor` | Shares today per share at the founding, per company: the product of every split and consolidation. |
| 347 | `private double lifetimeVolume` |  |
| 348 | `private boolean open` |  |
| 485 | `private double bookLimit` | The desk's own room, in money, and it is a POSITION not a rate. |
| 486 | `private double ownBoughtThisMonth` |  |
| 488 | `private boolean bankFlush` |  |
| 662 | `private int bestBuy` |  |
| 663 | `private double householdBuying` |  |
| 746 | `private double ownSoldThisMonth` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 108 | 899 | **type** `public class Exchange` | The stock exchange: where a share changes hands, and at what price. |

### the dials (lines 110-168)

### AND A DEAD BAND ON THE LINE ITSELF (2026-09-13) (lines 169-188)

### AND A DEAD BAND ON THE RANKING (2026-09-15) (lines 189-268)

### WHAT COUNTS AS A DEALER HAVING CAPITAL (2026-09-13) (lines 269-316)

| line | len | member | says |
|---:|---:|---|---|
| 306 | 5 | `public void seedConstants(double unit)` | Re-seeds the floors at a given unit. |
| 313 | 3 | `private boolean hasDealerCapital(double bankEquity)` | Whether the bank has capital enough to make a market at all. |

### the book (lines 317-375)

| line | len | member | says |
|---:|---:|---|---|
| 350 | 6 | `public Exchange()` |  |
| 358 | 17 | `public void startMonth()` | Clears the month's flows and lets last month's unfilled demand fade. |

### THE QUOTE (lines 376-445)

| line | len | member | says |
|---:|---:|---|---|
| 388 | 13 | `public void quote(Equity register, double[] book, double bankEquity, double worldRate)` | Re-reads what every share is worth and what the desk can carry, and quotes accordingly. |
| 402 | 1 | `public boolean isOpen()` |  |
| 403 | 1 | `public double mid(int c)` |  |
| 404 | 1 | `public double bid(int c)` |  |
| 405 | 1 | `public double ask(int c)` |  |
| 406 | 1 | `public double fair(int c)` |  |
| 407 | 1 | `public double limit(int c)` |  |
| 408 | 1 | `public double lastMid(int c)` |  |
| 410 | 1 | `public double mark(int c)` | What the desk carries a share at: the quote or fair value, whichever is lower. |
| 421 | 3 | `public boolean quoteSupportsIssue(int c)` | Whether a company would sell new shares at today's quote: not while the market has them under fair value by more than the tolerance. |
| 426 | 3 | `public double yieldAt(Equity register, int c, double price)` | Dividend yield, annual, at a price. |
| 431 | 5 | `public double markToMarket(Equity register)` | What the desk holds, at its mark: the bank's securities line. |
| 438 | 5 | `public double marketValueOfHouseholds(Equity register, HouseholdBalance households)` | What the city's households hold, at the quote. |
| 444 | 1 | `public double marketCap(Equity register, int c)` |  |

### THE DEALS. Each is one trade with the desk at the month's quote; the (lines 446-552)

| line | len | member | says |
|---:|---:|---|---|
| 453 | 15 | `double deskCanBuy(Equity register, int c)` | Shares the desk will still buy in this company: up to its capacity, and none of its own unless flush. |
| 470 | 5 | `private double bookAtFair(Equity register)` | What the desk holds in every company at fair value. |
| 496 | 12 | `double deskBuysFromHousehold(Equity register, Bank bank, int c, double shares)` | The desk buys shares from a household: the household's cell is debited by the caller. |
| 515 | 11 | `double deskSellsToHouseholds(Equity register, Bank bank, int c, double cash)` | The desk sells shares to households for cash: the caller credits the cells. |
| 527 | 13 | `private double deskBuysFromAbroad(Equity register, Bank bank, int c, double shares, boolean emigrant)` |  |
| 541 | 11 | `private double deskSellsAbroad(Equity register, Bank bank, int c, double shares)` |  |

### THE MONTH (lines 553-811)

| line | len | member | says |
|---:|---:|---|---|
| 558 | 13 | **type** `public interface Companies` | What the month's trading needs to know about a company that is not on the register. |
| 560 | 1 | `double cashAvailable(int company, double wanted)` _(in Exchange.Companies)_ | Cash in the company's till, after anything it can bring home for this. |
| 562 | 1 | `void payBuyback(int company, double cash)` _(in Exchange.Companies)_ | Debits the company's till for a buyback. |
| 564 | 1 | `void paySpecialDividend(int company, double cash)` _(in Exchange.Companies)_ | Debits the company's till for a special dividend and books it as a dividend paid; the exchange pays the holders. |
| 565 | 1 | `double assets(int company)` _(in Exchange.Companies)_ |  |
| 566 | 1 | `double equity(int company)` _(in Exchange.Companies)_ |  |
| 567 | 1 | `double monthlyOperatingCost(int company)` _(in Exchange.Companies)_ |  |
| 569 | 1 | `boolean bankFlush()` _(in Exchange.Companies)_ | True when the bank may buy its own shares: capital well past what it must hold. |
| 579 | 82 | `public void takeMonth(Equity register, HouseholdBalance households, Bank bank, Companies companies, double[] book, double world...` | Runs the month: emigrants, the world, the households, the companies - then re-quotes, splits what needs splitting, and hands the bank its mark. |
| 672 | 55 | `private void buyForHouseholds(Equity register, HouseholdBalance households, Bank bank, double depositRate)` | The households' month: every company yielding more than the deposit rate plus the premium, best first, and what the desk cannot sell them of one they put into the next. |
| 733 | 12 | `private double deskCanSell(Equity register, int c)` | Shares the desk can still sell in this company: what it holds, and nothing it does not. |
| 749 | 3 | `private void noteUnfilled(int c, double shares)` | A buyer the desk could not fill: remembered, and the next quote goes up to find a seller. |
| 757 | 28 | `private void tender(Equity register, HouseholdBalance households, Bank bank, Companies companies, int c, double spend)` | A tender: the company buys back this much, at the ask, from every holder pro rata, and cancels the shares. |
| 792 | 19 | `private void splitWhatNeedsIt(Equity register, HouseholdBalance households)` | A split or a consolidation, by a power of ten, for any share quoted a hundred times its founding price or a hundredth of it. |

### THE DISTRESS SALE, mid-month: a household short of money sells at the (lines 812-854)

| line | len | member | says |
|---:|---:|---|---|
| 823 | 31 | `double sellForHousehold(Equity register, Bank bank, Household cell, double needPer)` | Sells a cell's shares for cash, pro rata across what it holds by value. |

### reading (lines 855-894)

| line | len | member | says |
|---:|---:|---|---|
| 857 | 1 | `public double getSoldToHouseholds(int c)` |  |
| 858 | 1 | `public double getBoughtFromHouseholds(int c)` |  |
| 859 | 1 | `public double getSoldAbroad(int c)` |  |
| 860 | 1 | `public double getBoughtFromAbroad(int c)` |  |
| 861 | 1 | `public double getEmigrantsPaid(int c)` |  |
| 862 | 1 | `public double getBuybackToHouseholds(int c)` |  |
| 863 | 1 | `public double getBuybackToDesk(int c)` |  |
| 864 | 1 | `public double getBuybackAbroad(int c)` |  |
| 865 | 1 | `public double getSpecialDividend(int c)` |  |
| 866 | 1 | `public double getVolume(int c)` |  |
| 867 | 1 | `public double getLifetimeVolume()` |  |
| 868 | 1 | `public int getBestBuy()` |  |
| 870 | 1 | `public double getUnfilled(int c)` | Shares buyers wanted and could not get this month. |
| 872 | 1 | `public double getDemand(int c)` | ...and what of that, this month's and earlier, the quote still carries. |
| 873 | 1 | `public double getHouseholdBuying()` |  |
| 875 | 1 | `public double getSplit(int c)` | This month's split factor: 100 for a hundred-for-one, .01 for a consolidation, 0 for none. |
| 877 | 1 | `public double getSplitFactor(int c)` | Shares today for one share at the founding. |
| 879 | 1 | `public double midPerFoundingShare(int c)` | The quote per FOUNDING share: continuous through every split. |
| 881 | 1 | `public double fairPerFoundingShare(int c)` | ...and the register's reckoning, the same way. |
| 883 | 1 | `private double sum(double[] a)` |  |
| 884 | 1 | `public double getSoldToHouseholds()` |  |
| 885 | 1 | `public double getBoughtFromHouseholds()` |  |
| 886 | 1 | `public double getSoldAbroad()` |  |
| 887 | 1 | `public double getBoughtFromAbroad()` |  |
| 888 | 1 | `public double getEmigrantsPaid()` |  |
| 889 | 1 | `public double getBuybackToHouseholds()` |  |
| 890 | 1 | `public double getBuybackToDesk()` |  |
| 891 | 1 | `public double getBuybackAbroad()` |  |
| 892 | 1 | `public double getSpecialDividend()` |  |
| 893 | 1 | `public double getVolume()` |  |

### saving (lines 895-1006)

| line | len | member | says |
|---:|---:|---|---|
| 907 | 9 | `public double[] toSaveArray()` |  |
| 917 | 24 | `public boolean restore(String[] keys, double[] saved)` |  |
| 948 | 3 | `public void reopen(double bankEquity)` | After a load: the market is open exactly when the bank has capital, as the next month's quote will say. |
| 952 | 11 | `public void reset()` |  |
| 965 | 41 | `public void redenominate(double scale)` | Prices and the month's cash in the new unit; share counts do not move. |

