# CapitalFlows.java - 869 lines · 55 methods · 17 constants · model

`ham/citybuildersim/CapitalFlows.java` - generated 2026-10-31 by CodeMap; line numbers are as of that run.

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

**Used by (21):** [Auction](Auction.md), [AuctionCheck](AuctionCheck.md), [BondCheck](BondCheck.md), [BondMarket](BondMarket.md), [Buyback](Buyback.md), [BuybackCheck](BuybackCheck.md), [CallCheck](CallCheck.md), [CapitalFlowCheck](CapitalFlowCheck.md), [CarryTradeCheck](CarryTradeCheck.md), [CurrencyCheck](CurrencyCheck.md), [CurrencyRegimeCheck](CurrencyRegimeCheck.md), [FloatLinkCheck](FloatLinkCheck.md), [Game](Game.md), [HoldersCheck](HoldersCheck.md), [InterestAbroadCheck](InterestAbroadCheck.md), [LongPlaytest](LongPlaytest.md), [MoneyCheck](MoneyCheck.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [SummaryScreen](SummaryScreen.md), [TradeScreen](TradeScreen.md)

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
| 519 | · reading |
| 521 | · * |
| 658 | THE RISK-OFF UNWINDS THEM (XR4, 2026-10-31; spec-exchange-rates 3.8) |
| 751 | · carrying |

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
| 683 | `CapitalFlows.RISK_OFF_HOT_UNWIND` | `.05` | The share of the hot money's stock that runs home in a month of a risk-off of size one, scaled by the risk-off's level as it fades: a size-one risk-off takes about a fifth of the stock over the year after it, a size-1... |
| 686 | `CapitalFlows.RISK_OFF_CARRY_UNWIND` | `.50` | The share of the carry book repaid in a month of a risk-off of size one, scaled by the level (est.: the yen carry of July 2024, at a risk-off smaller than one, was mostly gone in six weeks). |
| 693 | `CapitalFlows.RISK_OFF_FELT` | `.05` | The risk-off level under which money no longer runs home, nor is a month of one counted (getRiskOffMonths()) or logged (est.: WorldMoneys.riskOff() fades by half in three months and never reaches 0, so a size-one risk... |

## Fields (state)

| line | field | says |
|---:|---|---|
| 94 | `private double minStock` | MIN_STOCK in today's money. |
| 182 | `private double stock` |  |
| 183 | `private double target` |  |
| 184 | `private double spread` |  |
| 185 | `private double arrived` | this month, gross; saved (FX8) |
| 186 | `private double departed` | this month, gross; saved (FX8) |
| 187 | `private int panicUntil` | this month, gross; saved (FX8) |
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
| 644 | `private double doubt` | The month's break chance as the last reprice left it (ForeignAccounts.getDoubtShare()), the share of the stock that takes itself home on doubt this month. |
| 646 | `private double doubtDeparted, doubtDepartedLifetime` | What left on doubt this month (inside getDeparted()) and since founding (XR2). |
| 696 | `private double riskOff` | The month's risk-off level, as Game handed it. |
| 698 | `private double riskOffDeparted, riskOffDepartedLifetime` | What ran home in a risk-off this month (inside getDeparted()) and since founding; the carry's repaid on it (inside getCarryRepaid()) and since founding; the months a risk-off unwound anything. |
| 699 | `private double carryRiskRepaid, carryRiskRepaidLifetime` |  |
| 700 | `private int riskOffMonths` |  |
| 702 | `private boolean riskOffCounted` | Whether this month's unwinding has been counted in riskOffMonths already (the carry's month and the hot money's are both in it). |
| 745 | `private int lastMonth` | Told the month, so isStopped() can be asked outside takeMonth(). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 33 | 837 | **type** `public class CapitalFlows` | Hot money: what comes in chasing a spread, and what happens when it leaves. |

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

### WHAT THE MONEY WOULD DO AT A DIFFERENT PRICE. (lines 339-518)

| line | len | member | says |
|---:|---:|---|---|
| 359 | 6 | `public double stockAt(double depositRate, double cityRate, double worldRate, double countryPremium, double monthlyGdp)` |  |
| 378 | 140 | `public void takeMonth(double depositRate, double cityRate, double worldRate, double countryPremium, double monthlyGdp, double r...` |  |

### reading (lines 519-520)

### * (lines 521-657)

| line | len | member | says |
|---:|---:|---|---|
| 540 | 64 | `public double carryTakeMonth(double lendingRate, double worldRate, double countryPremium, double headroom)` |  |
| 606 | 5 | `public double carryInterestOn(double lendingRate)` | What the book earns the bank this month, at the rate they borrowed at. |
| 612 | 1 | `public double getCarryStock()` |  |
| 613 | 1 | `public double getCarryTarget()` |  |
| 614 | 1 | `public double getCarrySpread()` |  |
| 615 | 1 | `public double getCarryBorrowed()` |  |
| 616 | 1 | `public double getCarryRepaid()` |  |
| 617 | 1 | `public double getLifetimeCarryBorrowed()` |  |
| 618 | 1 | `public double getLifetimeCarryRepaid()` |  |
| 619 | 1 | `public double getLifetimeCarryInterest()` |  |
| 620 | 1 | `public double getPeakCarryStock()` |  |
| 621 | 1 | `public double getPeakCarrySpread()` |  |
| 624 | 6 | `public double carryTargetAt(double lendingRate, double worldRate, double countryPremium, double headroom)` | What the trade would want at a given spread, for a screen or a forecast. |
| 632 | 1 | `public double getStock()` | Foreign money currently funding the city, in local money. |
| 635 | 1 | `public double getTarget()` | What would be here if it had all arrived. |
| 638 | 1 | `public double getSpread()` | The excess return that is pulling it, net of what the risk costs. |
| 640 | 1 | `public double getArrived()` |  |
| 641 | 1 | `public double getDeparted()` |  |
| 649 | 3 | `public void setDoubt(double share)` | Told the month's doubt before takeMonth(): a share of the stock, 0 to 1. |
| 654 | 1 | `public double getDoubtDeparted()` | What left on doubt this month, inside getDeparted(). |
| 656 | 1 | `public double getDoubtDepartedLifetime()` | ...and since founding. |

### THE RISK-OFF UNWINDS THEM (XR4, 2026-10-31; spec-exchange-rates 3.8) (lines 658-750)

| line | len | member | says |
|---:|---:|---|---|
| 705 | 4 | `public void setRiskOff(double level)` | Told the month's risk-off level (WorldMoneys.riskOff()) before the carry's month and the hot money's; a negative or non-finite level reads 0. |
| 710 | 5 | `private void countTheRiskOffMonth()` |  |
| 717 | 1 | `public double getRiskOffDeparted()` | What ran home in a risk-off this month, inside getDeparted(). |
| 719 | 1 | `public double getRiskOffDepartedLifetime()` | ...and since founding. |
| 721 | 1 | `public double getCarryRiskRepaid()` | What of the carry was repaid in a risk-off this month, inside getCarryRepaid(). |
| 723 | 1 | `public double getCarryRiskRepaidLifetime()` | ...and since founding. |
| 725 | 1 | `public int getRiskOffMonths()` | The months, since founding, in which a risk-off of at least RISK_OFF_FELT unwound any of the hot money or the carry (a month that unwound both counts once; setRiskOff() starts each month). |
| 728 | 1 | `public double netFlow()` | The month's flow, positive when money is coming in. |
| 731 | 1 | `public double getDepositShare()` | Share of the pull that is the bank's deposit rate rather than the city's paper. |
| 733 | 1 | `public boolean isStopped()` |  |
| 734 | 1 | `public String getStopReason()` |  |
| 735 | 1 | `public int getStopsSuffered()` |  |
| 738 | 1 | `public double getPeakStock()` | The most that was ever here, and the widest the gap ever got. |
| 739 | 1 | `public double getPeakSpread()` |  |
| 741 | 1 | `public double getLifetimeArrived()` |  |
| 742 | 1 | `public double getLifetimeDeparted()` |  |
| 746 | 1 | `public void setMonth(int month)` |  |
| 749 | 1 | `public int stopMonthsLeft()` | Months of the stop still to run, or 0. |

### carrying (lines 751-869)

| line | len | member | says |
|---:|---:|---|---|
| 753 | 17 | `public double[] toSaveArray()` |  |
| 771 | 37 | `public void restore(double[] saved)` |  |
| 814 | 3 | `public void restoreStopReason(String reason)` | The words the stop gave, put back on load (FW6, 2026-10-25): a String, so it cannot ride in the array above, and a city saved during a stop showed a blank reason on the Trade screen's card for the rest of it (PANIC_MO... |
| 818 | 19 | `public void reset()` |  |
| 839 | 29 | `public void redenominate(double scale)` | Hot money, in the new unit. |

