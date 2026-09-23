# CapitalFlows.java - 609 lines · 38 methods · 14 constants · model

`ham/citybuildersim/CapitalFlows.java` - generated 2026-09-23 by CodeMap; line numbers are as of that run.

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

**Used by (8):** [CapitalFlowCheck](CapitalFlowCheck.md), [CarryTradeCheck](CarryTradeCheck.md), [CurrencyCheck](CurrencyCheck.md), [Game](Game.md), [LongPlaytest](LongPlaytest.md), [MoneyCheck](MoneyCheck.md), [SummaryScreen](SummaryScreen.md), [TradeScreen](TradeScreen.md)

## Sections

| line | section |
|---:|---|
| 35 | HOW MUCH WANTS TO COME |
| 133 | AND WHAT MAKES IT GO |
| 179 | · state |
| 191 | THE OTHER DIRECTION (2026-09-12) |
| 249 | · the month |
| 251 | WHAT THE MONEY WOULD DO AT A DIFFERENT PRICE. |
| 302 | · · is anybody panicking |
| 361 | · · what it is worth |
| 378 | · · and it moves |
| 406 | · reading |
| 408 | · * |
| 541 | · carrying |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 51 | `CapitalFlows.APPETITE` | `3.0` | Foreign money held, per point of excess return, as a multiple of a year's output. |
| 70 | `CapitalFlows.MAX_SPREAD` | `.25` | Excess return above which appetite stops growing: 25 points since 0.7.2, so a 30% dial in a 5% world actually draws money and cutting it sends that money home (provisional, Jerus's number to settle). |
| 73 | `CapitalFlows.ARRIVAL_SPEED` | `.08` | How much of the gap to its target the stock closes in a month, coming in. |
| 76 | `CapitalFlows.DEPARTURE_SPEED` | `.20` | ...and going out, which is faster, because leaving is always faster. |
| 90 | `CapitalFlows.MIN_STOCK` | `1` | Below this much foreign money, the flow is not worth modelling. |
| 126 | `CapitalFlows.MATERIAL_MONTHS` | `.5` | Months of output below which the hot money is too small to break anything. |
| 159 | `CapitalFlows.PANIC_BACKING` | `.25` | Reserves needed to back the hot money, as a share of it. |
| 162 | `CapitalFlows.PANIC_DEPRECIATION` | `.12` | A twelve-month fall in the currency past this reads as a run. |
| 165 | `CapitalFlows.PANIC_MONTHS` | `18` | How long a break lasts before money will look at the city again. |
| 177 | `CapitalFlows.PANIC_EXIT` | `.33` | The share that leaves each month while confidence is broken. |
| 230 | `CapitalFlows.CARRY_FULL_SPREAD` | `.02` | At this spread or better, the world wants all the spare book there is. |
| 233 | `CapitalFlows.CARRY_MAX_SHARE` | `.90` | ...and never quite all of it, because a bank at its limit lends to nobody. |
| 236 | `CapitalFlows.CARRY_BORROW_SPEED` | `.06` | How fast the book fills, and empties. |
| 237 | `CapitalFlows.CARRY_REPAY_SPEED` | `.20` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 93 | `private double minStock` | MIN_STOCK in today's money. |
| 181 | `private double stock` |  |
| 182 | `private double target` |  |
| 183 | `private double spread` |  |
| 184 | `private double arrived` | this month, gross |
| 185 | `private double departed` | this month, gross |
| 186 | `private int panicUntil` | this month, gross |
| 187 | `private String panicReason` |  |
| 188 | `private double depositShare` |  |
| 189 | `private double lifetimeArrived, lifetimeDeparted` |  |
| 239 | `private double carryStock` |  |
| 240 | `private double carryTarget` |  |
| 241 | `private double carrySpread` |  |
| 242 | `private double carryBorrowed` | this month, gross |
| 243 | `private double carryRepaid` | this month, gross |
| 244 | `private double lifetimeCarryBorrowed, lifetimeCarryRepaid, lifetimeCarryInterest` | this month, gross |
| 245 | `private double peakCarryStock, peakCarrySpread` |  |
| 246 | `private double peakStock, peakSpread` |  |
| 247 | `private int stopsSuffered` |  |
| 535 | `private int lastMonth` | Told the month, so isStopped() can be asked outside takeMonth(). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 33 | 577 | **type** `public class CapitalFlows` | Hot money: what comes in chasing a spread, and what happens when it leaves. |

### HOW MUCH WANTS TO COME (lines 35-132)

| line | len | member | says |
|---:|---:|---|---|
| 102 | 3 | `public void seedConstants(double unit)` | Puts the money constants into the city's current unit. |
| 129 | 3 | `public static double maxStockInMonthsOfOutput()` | The largest position these constants can ever produce, in months of output. |

### AND WHAT MAKES IT GO (lines 133-178)

### state (lines 179-190)

### THE OTHER DIRECTION (2026-09-12) (lines 191-248)

### the month (lines 249-250)

### WHAT THE MONEY WOULD DO AT A DIFFERENT PRICE. (lines 251-405)

| line | len | member | says |
|---:|---:|---|---|
| 268 | 6 | `public double stockAt(double depositRate, double cityRate, double worldRate, double countryPremium, double monthlyGdp)` |  |
| 276 | 5 | `public double arrivalsAt(double depositRate, double cityRate, double worldRate, double countryPremium, double monthlyGdp)` | How much of that gap actually arrives in the first month. |
| 294 | 111 | `public void takeMonth(double depositRate, double cityRate, double worldRate, double countryPremium, double monthlyGdp, double r...` |  |

### reading (lines 406-407)

### * (lines 408-540)

| line | len | member | says |
|---:|---:|---|---|
| 427 | 51 | `public double carryTakeMonth(double lendingRate, double worldRate, double countryPremium, double headroom)` |  |
| 480 | 5 | `public double carryInterestOn(double lendingRate)` | What the book earns the bank this month, at the rate they borrowed at. |
| 486 | 1 | `public double getCarryStock()` |  |
| 487 | 1 | `public double getCarryTarget()` |  |
| 488 | 1 | `public double getCarrySpread()` |  |
| 489 | 1 | `public double getCarryBorrowed()` |  |
| 490 | 1 | `public double getCarryRepaid()` |  |
| 491 | 1 | `public double getLifetimeCarryBorrowed()` |  |
| 492 | 1 | `public double getLifetimeCarryRepaid()` |  |
| 493 | 1 | `public double getLifetimeCarryInterest()` |  |
| 494 | 1 | `public double getPeakCarryStock()` |  |
| 495 | 1 | `public double getPeakCarrySpread()` |  |
| 498 | 6 | `public double carryTargetAt(double lendingRate, double worldRate, double countryPremium, double headroom)` | What the trade would want at a given spread, for a screen or a forecast. |
| 506 | 1 | `public double getStock()` | Foreign money currently funding the city, in local money. |
| 509 | 1 | `public double getTarget()` | What would be here if it had all arrived. |
| 512 | 1 | `public double getSpread()` | The excess return that is pulling it, net of what the risk costs. |
| 514 | 1 | `public double getArrived()` |  |
| 515 | 1 | `public double getDeparted()` |  |
| 518 | 1 | `public double netFlow()` | The month's flow, positive when money is coming in. |
| 521 | 1 | `public double getDepositShare()` | Share of the pull that is the bank's deposit rate rather than the city's paper. |
| 523 | 1 | `public boolean isStopped()` |  |
| 524 | 1 | `public String getStopReason()` |  |
| 525 | 1 | `public int getStopsSuffered()` |  |
| 528 | 1 | `public double getPeakStock()` | The most that was ever here, and the widest the gap ever got. |
| 529 | 1 | `public double getPeakSpread()` |  |
| 531 | 1 | `public double getLifetimeArrived()` |  |
| 532 | 1 | `public double getLifetimeDeparted()` |  |
| 536 | 1 | `public void setMonth(int month)` |  |
| 539 | 1 | `public int stopMonthsLeft()` | Months of the stop still to run, or 0. |

### carrying (lines 541-609)

| line | len | member | says |
|---:|---:|---|---|
| 543 | 9 | `public double[] toSaveArray()` |  |
| 553 | 19 | `public void restore(double[] saved)` |  |
| 573 | 13 | `public void reset()` |  |
| 588 | 20 | `public void redenominate(double scale)` | Hot money, in the new unit. |

