# CapitalFlows.java - 704 lines · 44 methods · 14 constants · model

`ham/citybuildersim/CapitalFlows.java` - generated 2026-10-21 by CodeMap; line numbers are as of that run.

> Hot money: what comes in chasing a spread, and what happens when it leaves.
> 
> THE CARRY TRADE. A city paying 9% when the world pays 2% is an offer, and
> money takes it. Foreign savers move in, fund the city's credit system, and
> everything gets cheaper and easier - more deposits means more capacity means
> more lending means more building. None of it was earned by selling anything.
> 
> AND THE SUDDEN STOP, which is the same sentence read backwards. The money is
> not owed to anybody and has no maturity, so nothing has to go wrong for it to
> leave; the spread merely has to stop being worth the risk. When it goes it
> takes the bank's funding with it, which is the one thing a bank cannot
> replace quickly, and the credit boom unwinds into whatever was built on it.
> Mexico 1994, Thailand and Korea 1997, and every emerging market since.
> 
> WHY THE TWO CHANNELS ARE ONE CHANNEL HERE, because the design called for
> "both, split by where the yield is" and this looks like only half of it:
> 
> In this game the city's bonds are bought BY ITS OWN BANK - that is the whole
> of the circular-capital story, and it is still true. So a foreigner who wants
> the city's bond yield and a foreigner who wants the bank's deposit rate are
> funding the same balance sheet one step apart, and giving them two separate
> pipes would be modelling the same money twice. Both yields therefore compete
> to decide HOW MUCH comes - the money goes where the return is, and the return
> is the better of the two - and all of it arrives as funding for the bank that
> holds both. The split is reported, because the player should see which offer
> is pulling; it is not two flows, because there is one balance sheet.

**Used by (13):** [Auction](Auction.md), [AuctionCheck](AuctionCheck.md), [BondCheck](BondCheck.md), [BondMarket](BondMarket.md), [CapitalFlowCheck](CapitalFlowCheck.md), [CarryTradeCheck](CarryTradeCheck.md), [CurrencyCheck](CurrencyCheck.md), [Game](Game.md), [HoldersCheck](HoldersCheck.md), [LongPlaytest](LongPlaytest.md), [MoneyCheck](MoneyCheck.md), [SummaryScreen](SummaryScreen.md), [TradeScreen](TradeScreen.md)

## Sections

| line | section |
|---:|---|
| 35 | HOW MUCH WANTS TO COME |
| 134 | AND WHAT MAKES IT GO |
| 180 | · state |
| 192 | THE CITY'S PAPER THE WORLD HOLDS (CD2, slice 5, D7; star 2) |
| 276 | THE OTHER DIRECTION (2026-09-12) |
| 337 | · the month |
| 339 | WHAT THE MONEY WOULD DO AT A DIFFERENT PRICE. |
| 386 | · · is anybody panicking |
| 445 | · · what it is worth |
| 462 | · · and it moves |
| 490 | · reading |
| 492 | · * |
| 625 | · carrying |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 51 | `CapitalFlows.APPETITE` | `3.0` | Foreign money held, per point of excess return, as a multiple of a year's output. |
| 71 | `CapitalFlows.MAX_SPREAD` | `.25` | Excess return above which appetite stops growing: 25 points since 0.7.2, so a 30% dial in a 5% world actually draws money and cutting it sends that money home (provisional, Jerus's number to settle). |
| 74 | `CapitalFlows.ARRIVAL_SPEED` | `.08` | How much of the gap to its target the stock closes in a month, coming in. |
| 77 | `CapitalFlows.DEPARTURE_SPEED` | `.20` | ...and going out, which is faster, because leaving is always faster. |
| 91 | `CapitalFlows.MIN_STOCK` | `1` | Below this much foreign money, the flow is not worth modelling. |
| 127 | `CapitalFlows.MATERIAL_MONTHS` | `.5` | Months of output below which the hot money is too small to break anything. |
| 160 | `CapitalFlows.PANIC_BACKING` | `.25` | Reserves needed to back the hot money, as a share of it. |
| 163 | `CapitalFlows.PANIC_DEPRECIATION` | `.12` | A twelve-month fall in the currency past this reads as a run. |
| 166 | `CapitalFlows.PANIC_MONTHS` | `18` | How long a break lasts before money will look at the city again. |
| 178 | `CapitalFlows.PANIC_EXIT` | `.33` | The share that leaves each month while confidence is broken. |
| 318 | `CapitalFlows.CARRY_FULL_SPREAD` | `.02` | At this spread or better, the world wants all the spare book there is. |
| 321 | `CapitalFlows.CARRY_MAX_SHARE` | `.90` | ...and never quite all of it, because a bank at its limit lends to nobody. |
| 324 | `CapitalFlows.CARRY_BORROW_SPEED` | `.06` | How fast the book fills, and empties. |
| 325 | `CapitalFlows.CARRY_REPAY_SPEED` | `.20` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 94 | `private double minStock` | MIN_STOCK in today's money. |
| 182 | `private double stock` |  |
| 183 | `private double target` |  |
| 184 | `private double spread` |  |
| 185 | `private double arrived` | this month, gross |
| 186 | `private double departed` | this month, gross |
| 187 | `private int panicUntil` | this month, gross |
| 188 | `private String panicReason` |  |
| 189 | `private double depositShare` |  |
| 190 | `private double lifetimeArrived, lifetimeDeparted` |  |
| 220 | `private double paper` | The city's paper the world holds, at face, local money: DebtManager.worldPrincipal() from the other side. |
| 222 | `private double lifetimePaperBought, lifetimePaperSold` | What the world has paid for the paper and been paid for it since founding: lifetime figures, for the run's line and the harness. |
| 327 | `private double carryStock` |  |
| 328 | `private double carryTarget` |  |
| 329 | `private double carrySpread` |  |
| 330 | `private double carryBorrowed` | this month, gross |
| 331 | `private double carryRepaid` | this month, gross |
| 332 | `private double lifetimeCarryBorrowed, lifetimeCarryRepaid, lifetimeCarryInterest` | this month, gross |
| 333 | `private double peakCarryStock, peakCarrySpread` |  |
| 334 | `private double peakStock, peakSpread` |  |
| 335 | `private int stopsSuffered` |  |
| 619 | `private int lastMonth` | Told the month, so isStopped() can be asked outside takeMonth(). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 33 | 672 | **type** `public class CapitalFlows` | Hot money: what comes in chasing a spread, and what happens when it leaves. |

### HOW MUCH WANTS TO COME (lines 35-133)

| line | len | member | says |
|---:|---:|---|---|
| 103 | 3 | `public void seedConstants(double unit)` | Puts the money constants into the city's current unit. |
| 130 | 3 | `public static double maxStockInMonthsOfOutput()` | The largest position these constants can ever produce, in months of output. |

### AND WHAT MAKES IT GO (lines 134-179)

### state (lines 180-191)

### THE CITY'S PAPER THE WORLD HOLDS (CD2, slice 5, D7; star 2) (lines 192-275)

| line | len | member | says |
|---:|---:|---|---|
| 225 | 1 | `public double getPaper()` | The city's paper the world holds, at face. |
| 227 | 1 | `public double getLifetimePaperBought()` |  |
| 228 | 1 | `public double getLifetimePaperSold()` |  |
| 231 | 5 | `public void takePaper(double cash, double face)` | The world pays this cash at a settle for this face of the city's paper (Game.worldTakesItsShare()). |
| 238 | 4 | `public void paperRepaid(double face)` | Principal repaid, or a bond bought back: this much of its face is gone from the book, paid as the treasury pays it. |
| 244 | 5 | `public void sellPaper(double face, double cash)` | It sold this face to the bank's desk for this cash. |
| 267 | 8 | `public double paperLeaving(double stockBefore, double paperRatio)` | The face of its paper that leaves this month with the hot money, asked after takeMonth(). |

### THE OTHER DIRECTION (2026-09-12) (lines 276-336)

### the month (lines 337-338)

### WHAT THE MONEY WOULD DO AT A DIFFERENT PRICE. (lines 339-489)

| line | len | member | says |
|---:|---:|---|---|
| 359 | 6 | `public double stockAt(double depositRate, double cityRate, double worldRate, double countryPremium, double monthlyGdp)` |  |
| 378 | 111 | `public void takeMonth(double depositRate, double cityRate, double worldRate, double countryPremium, double monthlyGdp, double r...` |  |

### reading (lines 490-491)

### * (lines 492-624)

| line | len | member | says |
|---:|---:|---|---|
| 511 | 51 | `public double carryTakeMonth(double lendingRate, double worldRate, double countryPremium, double headroom)` |  |
| 564 | 5 | `public double carryInterestOn(double lendingRate)` | What the book earns the bank this month, at the rate they borrowed at. |
| 570 | 1 | `public double getCarryStock()` |  |
| 571 | 1 | `public double getCarryTarget()` |  |
| 572 | 1 | `public double getCarrySpread()` |  |
| 573 | 1 | `public double getCarryBorrowed()` |  |
| 574 | 1 | `public double getCarryRepaid()` |  |
| 575 | 1 | `public double getLifetimeCarryBorrowed()` |  |
| 576 | 1 | `public double getLifetimeCarryRepaid()` |  |
| 577 | 1 | `public double getLifetimeCarryInterest()` |  |
| 578 | 1 | `public double getPeakCarryStock()` |  |
| 579 | 1 | `public double getPeakCarrySpread()` |  |
| 582 | 6 | `public double carryTargetAt(double lendingRate, double worldRate, double countryPremium, double headroom)` | What the trade would want at a given spread, for a screen or a forecast. |
| 590 | 1 | `public double getStock()` | Foreign money currently funding the city, in local money. |
| 593 | 1 | `public double getTarget()` | What would be here if it had all arrived. |
| 596 | 1 | `public double getSpread()` | The excess return that is pulling it, net of what the risk costs. |
| 598 | 1 | `public double getArrived()` |  |
| 599 | 1 | `public double getDeparted()` |  |
| 602 | 1 | `public double netFlow()` | The month's flow, positive when money is coming in. |
| 605 | 1 | `public double getDepositShare()` | Share of the pull that is the bank's deposit rate rather than the city's paper. |
| 607 | 1 | `public boolean isStopped()` |  |
| 608 | 1 | `public String getStopReason()` |  |
| 609 | 1 | `public int getStopsSuffered()` |  |
| 612 | 1 | `public double getPeakStock()` | The most that was ever here, and the widest the gap ever got. |
| 613 | 1 | `public double getPeakSpread()` |  |
| 615 | 1 | `public double getLifetimeArrived()` |  |
| 616 | 1 | `public double getLifetimeDeparted()` |  |
| 620 | 1 | `public void setMonth(int month)` |  |
| 623 | 1 | `public int stopMonthsLeft()` | Months of the stop still to run, or 0. |

### carrying (lines 625-704)

| line | len | member | says |
|---:|---:|---|---|
| 627 | 11 | `public double[] toSaveArray()` |  |
| 639 | 24 | `public void restore(double[] saved)` |  |
| 664 | 14 | `public void reset()` |  |
| 680 | 23 | `public void redenominate(double scale)` | Hot money, in the new unit. |

