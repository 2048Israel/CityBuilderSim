# Exchange.java - 1,167 lines · 87 methods · 23 constants · model

`ham/citybuildersim/Exchange.java` - generated 2026-09-24 by CodeMap; line numbers are as of that run.

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
> AND IT BUYS ONLY ON THE CAPITAL ITS BANK HAS TO SPARE (0.7.8, round 4) -
> ... (54 more lines in the source)

**Uses:** [Equity](Equity.md) (53), [Bank](Bank.md) (11), [HouseholdBalance](HouseholdBalance.md) (5), [Household](Household.md) (1)

**Used by (11):** [BankCheck](BankCheck.md), [BankScreen](BankScreen.md), [ExchangeCheck](ExchangeCheck.md), [Game](Game.md), [HistorySave](HistorySave.md), [HouseholdBalance](HouseholdBalance.md), [LongPlaytest](LongPlaytest.md), [PeopleScreen](PeopleScreen.md), [ReadPathCheck](ReadPathCheck.md), [SectorScreen](SectorScreen.md), [TradeScreen](TradeScreen.md)

## Sections

| line | section |
|---:|---|
| 121 | · the dials |
| 185 | AND A DEAD BAND ON THE LINE ITSELF (2026-09-13) |
| 205 | AND A DEAD BAND ON THE RANKING (2026-09-15) |
| 285 | WHAT COUNTS AS A DEALER HAVING CAPITAL (2026-09-13) |
| 333 | · the book |
| 394 | THE QUOTE |
| 464 | THE DEALS. Each is one trade with the desk at the month's quote; the |
| 675 | THE MONTH |
| 948 | THE DISTRESS SALE, mid-month: a household short of money sells at the |
| 992 | · reading |
| 1055 | · saving |
| 1128 | · AND THE DESK'S OWN ROOM (2026-09-17) |

## Enum constants

| line | constant | says |
|---:|---|---|
| 521 | `Exchange.Seller.EMIGRANT` |  |
| 521 | `Exchange.Seller.WORLD` |  |
| 521 | `Exchange.Seller.HOUSEHOLD` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 124 | `Exchange.SPREAD` | `.02` | Ask over bid, as a share of the mid. |
| 127 | `Exchange.PRESSURE` | `.25` | How far a full position moves the quote from fair value. |
| 130 | `Exchange.POSITION_LIMIT` | `.25` | The desk's position in one company, as a share of the bank's equity at fair value. |
| 142 | `Exchange.BOOK_LIMIT` | `.50` | ...and its whole book, all companies together, as a share of the bank's equity at fair value. |
| 145 | `Exchange.FLOOR` | `.50, CEILING = 5.0` | The quote never leaves this band round fair value, whatever the book. |
| 154 | `Exchange.MIN_LIMIT_OF_FLOAT` | `.02` | The quote reads the desk's position against at least this share of the float, however small the bank. |
| 157 | `Exchange.CAPACITY` | `2.0` | The most the desk will hold, as a multiple of its position limit. |
| 160 | `Exchange.DEMAND_DECAY` | `.50` | What is left of a month's unfilled demand the next month, in the quote. |
| 163 | `Exchange.FOREIGN_SPEED` | `.10` | What the world moves in a month per unit of yield over or under its hurdle, as a share of the float. |
| 166 | `Exchange.FOREIGN_TOLERANCE` | `.10` | ...and it holds still while the yield is within this of the hurdle. |
| 169 | `Exchange.MONTHLY_SHARE_OF_EXCESS` | `.05` | A household's monthly buying: this share of what is past its cushion. |
| 172 | `Exchange.HOUSEHOLD_PREMIUM` | `.01` | ...into a yield at least this far over the deposit rate, annual. |
| 180 | `Exchange.BUYBACK_CUSHION_MONTHS` | `6` | A company keeps this many months of its costs before it buys back: operating cost and, since round 2 of 0.7.11, its debt service - the interest and the principal it repaid (Companies.monthlyDebtService()). |
| 183 | `Exchange.OVER_TARGET` | `.10` | Equity this far past target before a company buys back, as a share of assets. |
| 203 | `Exchange.MIN_EXCESS` | `1e-9` |  |
| 245 | `Exchange.TIED_YIELD` | `1e-9` |  |
| 248 | `Exchange.BUYBACK_PACE` | `.10` | The most a company retires in a year, as a share of what it is worth. |
| 251 | `Exchange.BUYBACK_TOLERANCE` | `.10` | A company buys back only while the ask is within this of fair value; past it, the money is a special dividend. |
| 254 | `Exchange.SPLIT_AT` | `100` | A share quoted at this many times its founding price is split; at one over it, consolidated. |
| 280 | `Exchange.MIN_FAIR` | `1e-9` | The least a share may be worth before the desk treats the company as worthless. |
| 316 | `Exchange.MIN_DEALER_EQUITY` | `1.0` |  |
| 1058 | `Exchange.SLOTS_BEFORE_SPLITS` | `3` | Slots per company before the split factor joined (the exchange's first night). |
| 1065 | `Exchange.SLOTS` | `SLOTS_BEFORE_SPLITS + 1` | The quote, the demand it carries and the split factor are STOCKS: the next month trades at the first two before anything re-quotes, and the price history is read through the third. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 283 | `private double minFair` | The same floor in today's money. |
| 319 | `private double minDealerEquity` | The same floor in today's money. |
| 335 | `private final int n` |  |
| 336 | `private final double[] fair` | register's reckoning, per share |
| 337 | `private final double[] mid` | register's reckoning, per share |
| 338 | `private final double[] limit` | the desk's quote, per share |
| 339 | `private final double[] demand` | position limit, in shares |
| 342 | `private final double[] soldToHouseholds` | this month |
| 343 | `private final double[] boughtFromHouseholds` | cash |
| 344 | `private final double[] soldAbroad` |  |
| 345 | `private final double[] boughtFromAbroad` |  |
| 346 | `private final double[] emigrantsPaid` |  |
| 347 | `private final double[] buybackToHouseholds` |  |
| 348 | `private final double[] buybackToDesk` |  |
| 349 | `private final double[] buybackAbroad` |  |
| 350 | `private final double[] specialDividend` |  |
| 351 | `private final double[] volume` | shares |
| 352 | `private final double[] lastMid` | shares |
| 353 | `private final double[] unfilled` | shares wanted and not sold, this month |
| 354 | `private final double[] split` | shares wanted and not sold, this month |
| 362 | `private final double[] splitFactor` | Shares today per share at the founding, per company: the product of every split and consolidation. |
| 363 | `private double lifetimeVolume` |  |
| 364 | `private boolean open` |  |
| 528 | `private final double[] ownRefused` | What the desk would have bought, at the bid, and did not because its bank's capital would not carry it - of the bank's own shares, and of other companies' - by who was selling. |
| 529 | `private final double[] deskRefused` |  |
| 560 | `private double bookLimit` | The desk's own room, in money, and it is a POSITION not a rate. |
| 561 | `private double ownBoughtThisMonth` |  |
| 572 | `private Bank dealer` | The bank the desk is, for its own shares: whether it buys them back (Bank.buysBackOwnShares()) and whether it issues them (Bank.issuesOwnShares()), read off it live at every deal since 0.7.8 - the rule is the bank's o... |
| 791 | `private int bestBuy` |  |
| 792 | `private double householdBuying` |  |
| 882 | `private double ownSoldThisMonth` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 119 | 1049 | **type** `public class Exchange` | The stock exchange: where a share changes hands, and at what price. |

### the dials (lines 121-184)

### AND A DEAD BAND ON THE LINE ITSELF (2026-09-13) (lines 185-204)

### AND A DEAD BAND ON THE RANKING (2026-09-15) (lines 205-284)

### WHAT COUNTS AS A DEALER HAVING CAPITAL (2026-09-13) (lines 285-332)

| line | len | member | says |
|---:|---:|---|---|
| 322 | 5 | `public void seedConstants(double unit)` | Re-seeds the floors at a given unit. |
| 329 | 3 | `private boolean hasDealerCapital(double bankEquity)` | Whether the bank has capital enough to make a market at all. |

### the book (lines 333-393)

| line | len | member | says |
|---:|---:|---|---|
| 366 | 6 | `public Exchange()` |  |
| 374 | 19 | `public void startMonth()` | Clears the month's flows and lets last month's unfilled demand fade. |

### THE QUOTE (lines 394-463)

| line | len | member | says |
|---:|---:|---|---|
| 406 | 13 | `public void quote(Equity register, double[] book, double bankEquity, double worldRate)` | Re-reads what every share is worth and what the desk can carry, and quotes accordingly. |
| 420 | 1 | `public boolean isOpen()` |  |
| 421 | 1 | `public double mid(int c)` |  |
| 422 | 1 | `public double bid(int c)` |  |
| 423 | 1 | `public double ask(int c)` |  |
| 424 | 1 | `public double fair(int c)` |  |
| 425 | 1 | `public double limit(int c)` |  |
| 426 | 1 | `public double lastMid(int c)` |  |
| 428 | 1 | `public double mark(int c)` | What the desk carries a share at: the quote or fair value, whichever is lower. |
| 439 | 3 | `public boolean quoteSupportsIssue(int c)` | Whether a company would sell new shares at today's quote: not while the market has them under fair value by more than the tolerance. |
| 444 | 3 | `public double yieldAt(Equity register, int c, double price)` | Dividend yield, annual, at a price. |
| 449 | 5 | `public double markToMarket(Equity register)` | What the desk holds, at its mark: the bank's securities line. |
| 456 | 5 | `public double marketValueOfHouseholds(Equity register, HouseholdBalance households)` | What the city's households hold, at the quote. |
| 462 | 1 | `public double marketCap(Equity register, int c)` |  |

### THE DEALS. Each is one trade with the desk at the month's quote; the (lines 464-674)

| line | len | member | says |
|---:|---:|---|---|
| 474 | 3 | `double deskCanBuy(Equity register, int c)` | Shares the desk will still buy in this company: the tighter of its dealing limits (dealingRoom()) and its bank's capital (capitalRoom()). |
| 479 | 19 | `private double dealingRoom(Equity register, int c)` | ...its dealing limits alone: up to its capacity, and of its own only while the bank is at or over its capital target (Bank.buysBackOwnShares()), at the buyback pace. |
| 510 | 6 | `private double capitalRoom(Equity register, int c)` | ...and its bank's capital alone (0.7.8, round 4): of its own shares, what the bank holds over its target at the bid (Bank.buybackRoom()); of any other company's, what that capital carries at the weight the weighted bo... |
| 521 | 1 | **type** `public enum Seller` | Who was selling when the desk would not buy for want of capital: an emigrant on the way out, the world, or a household short of money. |
| 535 | 8 | `private double deskTakes(Equity register, int c, double shares, Seller who)` | The shares the desk takes of an offer: the tighter of its limits, and what the capital rule alone turned away is counted against the seller. |
| 545 | 5 | `private double bookAtFair(Equity register)` | What the desk holds in every company at fair value. |
| 580 | 12 | `double deskBuysFromHousehold(Equity register, Bank bank, int c, double shares)` | The desk buys shares from a household: the household's cell is debited by the caller. |
| 599 | 11 | `double deskSellsToHouseholds(Equity register, Bank bank, int c, double cash)` | The desk sells shares to households for cash: the caller credits the cells. |
| 611 | 13 | `private double deskBuysFromAbroad(Equity register, Bank bank, int c, double shares, boolean emigrant)` |  |
| 625 | 11 | `private double deskSellsAbroad(Equity register, Bank bank, int c, double shares)` |  |
| 660 | 4 | `private static boolean ownSharesTrade(Equity register)` | ...AND NEVER ON A RECORD THAT IS NEW OR BAD - the register's own reading of the bank's last twelve months (Equity.getRegime()), which the old "flush" test carried and the capital rule keeps: a bank in its first year, ... |
| 665 | 4 | `private void payForShares(Bank bank, int c, double cash)` |  |
| 670 | 4 | `private void takeForShares(Bank bank, int c, double cash)` |  |

### THE MONTH (lines 675-947)

| line | len | member | says |
|---:|---:|---|---|
| 680 | 18 | **type** `public interface Companies` | What the month's trading needs to know about a company that is not on the register. |
| 682 | 1 | `double cashAvailable(int company, double wanted)` _(in Exchange.Companies)_ | Cash in the company's till, after anything it can bring home for this. |
| 684 | 1 | `void payBuyback(int company, double cash)` _(in Exchange.Companies)_ | Debits the company's till for a buyback. |
| 686 | 1 | `void paySpecialDividend(int company, double cash)` _(in Exchange.Companies)_ | Debits the company's till for a special dividend and books it as a dividend paid; the exchange pays the holders. |
| 687 | 1 | `double assets(int company)` _(in Exchange.Companies)_ |  |
| 688 | 1 | `double equity(int company)` _(in Exchange.Companies)_ |  |
| 689 | 1 | `double monthlyOperatingCost(int company)` _(in Exchange.Companies)_ |  |
| 696 | 1 | `double monthlyDebtService(int company)` _(in Exchange.Companies)_ | ...and what its debt costs it a month: the interest on its statement and the principal it repaid (0.7.11, round 2). |
| 706 | 84 | `public void takeMonth(Equity register, HouseholdBalance households, Bank bank, Companies companies, double[] book, double world...` | Runs the month: emigrants, the world, the households, the companies - then re-quotes, splits what needs splitting, and hands the bank its mark. |
| 801 | 55 | `private void buyForHouseholds(Equity register, HouseholdBalance households, Bank bank, double depositRate)` | The households' month: every company yielding more than the deposit rate plus the premium, best first, and what the desk cannot sell them of one they put into the next. |
| 863 | 18 | `private double deskCanSell(Equity register, int c)` | Shares the desk can still sell in this company: what it holds, and nothing it does not. |
| 885 | 3 | `private void noteUnfilled(int c, double shares)` | A buyer the desk could not fill: remembered, and the next quote goes up to find a seller. |
| 893 | 28 | `private void tender(Equity register, HouseholdBalance households, Bank bank, Companies companies, int c, double spend)` | A tender: the company buys back this much, at the ask, from every holder pro rata, and cancels the shares. |
| 928 | 19 | `private void splitWhatNeedsIt(Equity register, HouseholdBalance households)` | A split or a consolidation, by a power of ten, for any share quoted a hundred times its founding price or a hundredth of it. |

### THE DISTRESS SALE, mid-month: a household short of money sells at the (lines 948-991)

| line | len | member | says |
|---:|---:|---|---|
| 959 | 32 | `double sellForHousehold(Equity register, Bank bank, Household cell, double needPer)` | Sells a cell's shares for cash, pro rata across what it holds by value. |

### reading (lines 992-1054)

| line | len | member | says |
|---:|---:|---|---|
| 994 | 1 | `public double getSoldToHouseholds(int c)` |  |
| 995 | 1 | `public double getBoughtFromHouseholds(int c)` |  |
| 996 | 1 | `public double getSoldAbroad(int c)` |  |
| 997 | 1 | `public double getBoughtFromAbroad(int c)` |  |
| 998 | 1 | `public double getEmigrantsPaid(int c)` |  |
| 999 | 1 | `public double getBuybackToHouseholds(int c)` |  |
| 1000 | 1 | `public double getBuybackToDesk(int c)` |  |
| 1001 | 1 | `public double getBuybackAbroad(int c)` |  |
| 1002 | 1 | `public double getSpecialDividend(int c)` |  |
| 1003 | 1 | `public double getVolume(int c)` |  |
| 1004 | 1 | `public double getLifetimeVolume()` |  |
| 1005 | 1 | `public int getBestBuy()` |  |
| 1007 | 1 | `public double getUnfilled(int c)` | Shares buyers wanted and could not get this month. |
| 1009 | 1 | `public double getDemand(int c)` | ...and what of that, this month's and earlier, the quote still carries. |
| 1010 | 1 | `public double getHouseholdBuying()` |  |
| 1012 | 1 | `public double getOwnRefused(Seller who)` | Of the bank's own shares, what the desk turned away this month for want of capital over the bank's target, at the bid, from this seller (0.7.8, round 4). |
| 1014 | 1 | `public double getDeskRefused(Seller who)` | ...and of other companies' shares, for want of capital to carry them. |
| 1016 | 1 | `public double getOwnRefused()` | ...every seller together. |
| 1018 | 1 | `public double getDeskRefused()` | ...every seller together. |
| 1020 | 1 | `public double getSplit(int c)` | This month's split factor: 100 for a hundred-for-one, .01 for a consolidation, 0 for none. |
| 1022 | 1 | `public double getSplitFactor(int c)` | Shares today for one share at the founding. |
| 1024 | 1 | `public double midPerFoundingShare(int c)` | The quote per FOUNDING share: continuous through every split. |
| 1026 | 1 | `public double fairPerFoundingShare(int c)` | ...and the register's reckoning, the same way. |
| 1028 | 1 | `private double sum(double[] a)` |  |
| 1029 | 1 | `public double getSoldToHouseholds()` |  |
| 1030 | 1 | `public double getBoughtFromHouseholds()` |  |
| 1031 | 1 | `public double getSoldAbroad()` |  |
| 1032 | 1 | `public double getBoughtFromAbroad()` |  |
| 1033 | 1 | `public double getEmigrantsPaid()` |  |
| 1034 | 1 | `public double getBuybackToHouseholds()` |  |
| 1035 | 1 | `public double getBuybackToDesk()` |  |
| 1044 | 1 | `public double deskSoldToHouseholds()` | The desk's trading in the city's shares, the bank's own left out (0.7.9): each total less the bank's line, because its own shares are capital since 0.7.8 (Bank.buyBackOwnShares()), not the desk's trading. |
| 1046 | 1 | `public double deskSoldAbroad()` | ...sold abroad. |
| 1048 | 1 | `public double deskBoughtFromHouseholds()` | ...bought from the households. |
| 1050 | 1 | `public double deskBoughtFromAbroad()` | ...and bought from abroad. |
| 1051 | 1 | `public double getBuybackAbroad()` |  |
| 1052 | 1 | `public double getSpecialDividend()` |  |
| 1053 | 1 | `public double getVolume()` |  |

### saving (lines 1055-1167)

| line | len | member | says |
|---:|---:|---|---|
| 1067 | 9 | `public double[] toSaveArray()` |  |
| 1077 | 24 | `public boolean restore(String[] keys, double[] saved)` |  |
| 1108 | 3 | `public void reopen(double bankEquity)` | After a load: the market is open exactly when the bank has capital, as the next month's quote will say. |
| 1112 | 11 | `public void reset()` |  |
| 1125 | 42 | `public void redenominate(double scale)` | Prices and the month's cash in the new unit; share counts do not move. |

