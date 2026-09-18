# CapitalFlows.java - 593 lines · 38 methods · 14 constants · model

`ham/citybuildersim/CapitalFlows.java` - generated 2026-09-18 by CodeMap; line numbers are as of that run.

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

**Used by (7):** [CapitalFlowCheck](CapitalFlowCheck.md), [CarryTradeCheck](CarryTradeCheck.md), [Game](Game.md), [LongPlaytest](LongPlaytest.md), [MoneyCheck](MoneyCheck.md), [SummaryScreen](SummaryScreen.md), [TradeScreen](TradeScreen.md)

## Sections

| line | section |
|---:|---|
| 35 | HOW MUCH WANTS TO COME |
| 117 | AND WHAT MAKES IT GO |
| 163 | · state |
| 175 | THE OTHER DIRECTION (2026-09-12) |
| 233 | · the month |
| 247 | WHAT THE MONEY WOULD DO AT A DIFFERENT PRICE. |
| 286 | · · is anybody panicking |
| 345 | · · what it is worth |
| 362 | · · and it moves |
| 390 | · reading |
| 393 | · * |
| 525 | · carrying |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 51 | `CapitalFlows.APPETITE` | `3.0` | Foreign money held, per point of excess return, as a multiple of a year's output. |
| 54 | `CapitalFlows.MAX_SPREAD` | `.06` | Excess return above which appetite stops growing. |
| 57 | `CapitalFlows.ARRIVAL_SPEED` | `.08` | How much of the gap to its target the stock closes in a month, coming in. |
| 60 | `CapitalFlows.DEPARTURE_SPEED` | `.20` | ...and going out, which is faster, because leaving is always faster. |
| 74 | `CapitalFlows.MIN_STOCK` | `1` | Below this much foreign money, the flow is not worth modelling. |
| 110 | `CapitalFlows.MATERIAL_MONTHS` | `.5` | Months of output below which the hot money is too small to break anything. |
| 143 | `CapitalFlows.PANIC_BACKING` | `.25` | Reserves needed to back the hot money, as a share of it. |
| 146 | `CapitalFlows.PANIC_DEPRECIATION` | `.12` | A twelve-month fall in the currency past this reads as a run. |
| 149 | `CapitalFlows.PANIC_MONTHS` | `18` | How long a break lasts before money will look at the city again. |
| 161 | `CapitalFlows.PANIC_EXIT` | `.33` | The share that leaves each month while confidence is broken. |
| 214 | `CapitalFlows.CARRY_FULL_SPREAD` | `.02` | At this spread or better, the world wants all the spare book there is. |
| 217 | `CapitalFlows.CARRY_MAX_SHARE` | `.90` | ...and never quite all of it, because a bank at its limit lends to nobody. |
| 220 | `CapitalFlows.CARRY_BORROW_SPEED` | `.06` | How fast the book fills, and empties. |
| 221 | `CapitalFlows.CARRY_REPAY_SPEED` | `.20` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 77 | `private double minStock` | MIN_STOCK in today's money. |
| 165 | `private double stock` |  |
| 166 | `private double target` |  |
| 167 | `private double spread` |  |
| 168 | `private double arrived` | this month, gross |
| 169 | `private double departed` | this month, gross |
| 170 | `private int panicUntil` | this month, gross |
| 171 | `private String panicReason` |  |
| 172 | `private double depositShare` |  |
| 173 | `private double lifetimeArrived, lifetimeDeparted` |  |
| 223 | `private double carryStock` |  |
| 224 | `private double carryTarget` |  |
| 225 | `private double carrySpread` |  |
| 226 | `private double carryBorrowed` | this month, gross |
| 227 | `private double carryRepaid` | this month, gross |
| 228 | `private double lifetimeCarryBorrowed, lifetimeCarryRepaid, lifetimeCarryInterest` | this month, gross |
| 229 | `private double peakCarryStock, peakCarrySpread` |  |
| 230 | `private double peakStock, peakSpread` |  |
| 231 | `private int stopsSuffered` |  |
| 519 | `private int lastMonth` | Told the month, so isStopped() can be asked outside takeMonth(). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 33 | 561 | **type** `public class CapitalFlows` | Hot money: what comes in chasing a spread, and what happens when it leaves. |

### HOW MUCH WANTS TO COME (lines 35-116)

| line | len | member | says |
|---:|---:|---|---|
| 86 | 3 | `public void seedConstants(double unit)` | Puts the money constants into the city's current unit. |
| 113 | 3 | `public static double maxStockInMonthsOfOutput()` | The largest position these constants can ever produce, in months of output. |

### AND WHAT MAKES IT GO (lines 117-162)

### state (lines 163-174)

### THE OTHER DIRECTION (2026-09-12) (lines 175-232)

### the month (lines 233-246)

### WHAT THE MONEY WOULD DO AT A DIFFERENT PRICE. (lines 247-389)

| line | len | member | says |
|---:|---:|---|---|
| 264 | 6 | `public double stockAt(double depositRate, double cityRate, double worldRate, double countryPremium, double monthlyGdp)` |  |
| 272 | 5 | `public double arrivalsAt(double depositRate, double cityRate, double worldRate, double countryPremium, double monthlyGdp)` | How much of that gap actually arrives in the first month. |
| 278 | 111 | `public void takeMonth(double depositRate, double cityRate, double worldRate, double countryPremium, double monthlyGdp, double r...` |  |

### reading (lines 390-392)

### * (lines 393-524)

| line | len | member | says |
|---:|---:|---|---|
| 412 | 51 | `public double carryTakeMonth(double lendingRate, double worldRate, double countryPremium, double headroom)` |  |
| 465 | 5 | `public double carryInterestOn(double lendingRate)` | What the book earns the bank this month, at the rate they borrowed at. |
| 471 | 1 | `public double getCarryStock()` |  |
| 472 | 1 | `public double getCarryTarget()` |  |
| 473 | 1 | `public double getCarrySpread()` |  |
| 474 | 1 | `public double getCarryBorrowed()` |  |
| 475 | 1 | `public double getCarryRepaid()` |  |
| 476 | 1 | `public double getLifetimeCarryBorrowed()` |  |
| 477 | 1 | `public double getLifetimeCarryRepaid()` |  |
| 478 | 1 | `public double getLifetimeCarryInterest()` |  |
| 479 | 1 | `public double getPeakCarryStock()` |  |
| 480 | 1 | `public double getPeakCarrySpread()` |  |
| 483 | 6 | `public double carryTargetAt(double lendingRate, double worldRate, double countryPremium, double headroom)` | What the trade would want at a given spread, for a screen or a forecast. |
| 490 | 1 | `public double getStock()` |  |
| 493 | 1 | `public double getTarget()` | What would be here if it had all arrived. |
| 496 | 1 | `public double getSpread()` | The excess return that is pulling it, net of what the risk costs. |
| 498 | 1 | `public double getArrived()` |  |
| 499 | 1 | `public double getDeparted()` |  |
| 502 | 1 | `public double netFlow()` | The month's flow, positive when money is coming in. |
| 505 | 1 | `public double getDepositShare()` | Share of the pull that is the bank's deposit rate rather than the city's paper. |
| 507 | 1 | `public boolean isStopped()` |  |
| 508 | 1 | `public String getStopReason()` |  |
| 509 | 1 | `public int getStopsSuffered()` |  |
| 512 | 1 | `public double getPeakStock()` | The most that was ever here, and the widest the gap ever got. |
| 513 | 1 | `public double getPeakSpread()` |  |
| 515 | 1 | `public double getLifetimeArrived()` |  |
| 516 | 1 | `public double getLifetimeDeparted()` |  |
| 520 | 1 | `public void setMonth(int month)` |  |
| 523 | 1 | `public int stopMonthsLeft()` | Months of the stop still to run, or 0. |

### carrying (lines 525-593)

| line | len | member | says |
|---:|---:|---|---|
| 527 | 9 | `public double[] toSaveArray()` |  |
| 537 | 19 | `public void restore(double[] saved)` |  |
| 557 | 13 | `public void reset()` |  |
| 572 | 20 | `public void redenominate(double scale)` | Hot money, in the new unit. |

