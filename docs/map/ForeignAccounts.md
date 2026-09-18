# ForeignAccounts.java - 1,112 lines · 68 methods · 14 constants · model

`ham/citybuildersim/ForeignAccounts.java` - generated 2026-09-18 by CodeMap; line numbers are as of that run.

> The city's dealings with the rest of the world: the balance of payments, the
> reserve position, and the exchange rate.
> 
> ==================== WHY THIS IS NEARLY EMPTY ====================
> 
> Almost nothing here is calculated. The balance of payments is read straight
> off MoneyAudit, which has been computing it since the day it was written
> without anybody noticing - it tracks every flow crossing the city's edge and
> reconciles them to the cent every month.
> 
> What was missing was not the arithmetic but the BOUNDARY. "Outside the pools"
> bundled households, who are domestic and simply not modelled as a pool,
> together with the rest of the world. Tag those apart - see MoneyAudit.Scope -
> and the foreign subset of a list that already balances IS the balance of
> payments. This class is the running total of it.
> 
> That is the same move the bank rewrite kept making: find the identity that is
> already there rather than inventing a parallel mechanism that can drift from
> it. There is no second set of books here to disagree with the first.
> 
> ==================== PHASE ONE OF FOUR ====================
> 
> Jerus's plan, in order:
> 
>   1. THIS. The accounting only. Split the boundary, count the reserve, show
>      it on a screen. The exchange rate exists and is pinned at 1.00, and
>      NOTHING BEHAVES DIFFERENTLY - the point is to prove the split is honest
>      against MoneyAudit before anything depends on it.
>   2. The rate moves, on reserve adequacy. Import prices and export revenues
>      start converting through it, and wages get a cost-of-living drift.
>   3. Foreign debt: USD instruments beside the existing ones. This is what
>      closes the circular-capital hole, because the treasury finally has a
>      lender that is not its own bank.
>   4. Capital flows: the carry trade in, sudden stops out, straight into the
>      bank's deposits.
> 
> See claude/foreign-exchange-design.md.
> 
> ==================== WHAT A RESERVE IS, TODAY ====================
> 
> A MEASUREMENT, not a pot of money, and that distinction is what keeps phase
> one honest. The dollars a mill earns exporting steel land in the mill's cash,
> exactly as they did yesterday; nothing was taken away and put somewhere else.
> The reserve is the CUMULATIVE NET FOREIGN POSITION - what the city has earned
> abroad less what it has spent there - and it is deliberately not one of
> MoneyAudit's pools, because adding the same dollar to a pool as well as to the
> sector that earned it would create money.
> 
> In phase two, when the treasury can actually buy and sell foreign currency to
> defend a rate, it becomes a real holding and the flows change. Not yet.
> 
> A NEGATIVE reserve is not a bug. It means the city has bought more abroad than
> it has sold, and is living on foreign credit that phase three has not modelled
> yet. A young city importing its construction materials and exporting nothing
> is exactly that, and the number saying so is the point.

**Uses:** [MoneyAudit](MoneyAudit.md) (2)

**Used by (15):** [CapitalFlowCheck](CapitalFlowCheck.md), [CarryTradeCheck](CarryTradeCheck.md), [CityBasket](CityBasket.md), [FinancesScreen](FinancesScreen.md), [ForeignCheck](ForeignCheck.md), [ForeignDebtCheck](ForeignDebtCheck.md), [Game](Game.md), [HistorySave](HistorySave.md), [HouseholdBalance](HouseholdBalance.md), [LongPlaytest](LongPlaytest.md), [MonetaryCheck](MonetaryCheck.md), [OutwardInvestment](OutwardInvestment.md), [PolicyScreen](PolicyScreen.md), [SummaryScreen](SummaryScreen.md), [TradeScreen](TradeScreen.md)

## Sections

| line | section |
|---:|---|
| 62 | · the rate |
| 74 | WHAT MOVES THE RATE |
| 228 | · · PARITY IS A RATE, NOT A RATIO, AND IT NEEDS AN ANCHOR IN MONEY. |
| 510 | · the month's account |
| 518 | · the position |
| 520 | TWO NUMBERS THAT WERE ONE NUMBER |
| 694 | · reading |
| 696 | WHAT THE CITY OWES ABROAD |
| 826 | BUYING AND SELLING THE RESERVE |
| 939 | · carrying |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 72 | `ForeignAccounts.OPENING_RATE` | `1.00` | Local currency per US dollar. |
| 94 | `ForeignAccounts.DEAD_BAND` | `.05` | Deficits smaller than this share of trade are noise, and are ignored. |
| 97 | `ForeignAccounts.COMFORTABLE_COVER` | `6` | Cover at which reserves fully absorb the month's pressure. |
| 100 | `ForeignAccounts.MAX_ABSORPTION` | `.85` | The most of the pressure deep reserves can absorb. |
| 110 | `ForeignAccounts.DRIFT_SPEED` | `.02` | How much of a month's pressure passes into the rate. |
| 130 | `ForeignAccounts.MIN_RATE` | `.01` | UNCAPPED, and these are now a numeric guard rather than a policy. |
| 131 | `ForeignAccounts.MAX_RATE` | `100.0` |  |
| 209 | `ForeignAccounts.OPENING_PARITY` | `1.00` | The rate at which a basket costs the same at home and abroad. |
| 264 | `ForeignAccounts.REVERSION` | `.004` | How hard. |
| 326 | `ForeignAccounts.SETTLING_MONTHS` | `24` | Months before the rate is allowed to move at all. |
| 329 | `ForeignAccounts.MIN_TRADE` | `50` | Below this much trade a month, the exchange rate is not a real price. |
| 384 | `ForeignAccounts.RATE_PULL` | `6.0` | How far the city's own rate is above the world's, and what that is worth to the currency. |
| 387 | `ForeignAccounts.MAX_RATE_PRESSURE` | `.8` | Most of the pressure a rate differential alone can produce. |
| 603 | `ForeignAccounts.COVER_WINDOW` | `12` | Months of imports the reserve would cover. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 145 | `private double minRate` | The same three, in TODAY's money. |
| 146 | `private double maxRate` |  |
| 147 | `private double parityBase` |  |
| 211 | `private double parity` |  |
| 215 | `private double localInflation, worldInflation` | Save slot 19. |
| 266 | `private double rate` |  |
| 267 | `private double lastPressure` |  |
| 268 | `private double lastAbsorption` |  |
| 348 | `private double minTrade` | The same floor, in TODAY's money. |
| 389 | `private double rateDifferential` |  |
| 440 | `private boolean pinned` |  |
| 512 | `private double exports` |  |
| 513 | `private double imports` |  |
| 514 | `private double foreignInterest` |  |
| 515 | `private double financialIn` |  |
| 516 | `private double financialOut` |  |
| 547 | `private double reserves` |  |
| 559 | `private double cumulativeBalance` | THE RECORD. |
| 560 | `private double forgiven` |  |
| 563 | `private double exportsTrailing` | Trailing means, for anything the rate is decided on. |
| 564 | `private double currentTrailing` |  |
| 570 | `private double financialTrailing` | The financial account, trailing, and the gross of it - what crossed in either direction - so the pressure can be struck on the OVERALL balance over everything that crossed. |
| 571 | `private double financialGrossTrailing` |  |
| 583 | `private double lifetimeExports` | SINCE FOUNDING. |
| 584 | `private double lifetimeImports` |  |
| 585 | `private double lifetimeInterest` |  |
| 586 | `private double lifetimeFinancial` |  |
| 587 | `private double importsTrailing` |  |
| 588 | `private int monthsOfHistory` |  |
| 643 | `private double openness` | How much of the economy actually crosses the border. |
| 719 | `private double foreignDebt` | local value of USD paper outstanding |
| 720 | `private double foreignDebtUsd` | local value of USD paper outstanding |
| 721 | `private double lastDebtRate` | ...and in the dollars it is owed in |
| 722 | `private double lastRevaluation` |  |
| 723 | `private double lifetimeRevaluation` |  |
| 821 | `private double repudiated` |  |
| 840 | `private double boughtThisMonth` |  |
| 841 | `private double soldThisMonth` |  |
| 930 | `private double lifetimeIntervention` | What the treasury has bought and sold on its own account, since founding. |
| 937 | `private double lastValuation` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 60 | 1053 | **type** `public class ForeignAccounts` | The city's dealings with the rest of the world: the balance of payments, the reserve position, and the exchange rate. |

### the rate (lines 62-73)

### WHAT MOVES THE RATE (lines 74-509)

| line | len | member | says |
|---:|---:|---|---|
| 149 | 1 | `public double getMinRate()` |  |
| 150 | 1 | `public double getMaxRate()` |  |
| 153 | 6 | `public void seedConstants(double unit)` | Re-seeds the money CONSTANTS at a given unit. |
| 226 | 31 | `public void setParity(double localLevel, double worldLevel, double localRate, double worldRate)` | The two inflation rates the drift is struck from, and the level ratio kept for the screen. |
| 259 | 1 | `public double inflationGap()` | How far the city's inflation is running above the world's. |
| 261 | 1 | `public double getParity()` |  |
| 274 | 43 | `public double pressure()` | The month's depreciation pressure: the current-account deficit as a share of everything the city trades. |
| 357 | 6 | `public double absorption()` | How much of it the reserve absorbs. |
| 392 | 3 | `public void setRateDifferential(double differential)` |  |
| 396 | 1 | `public double getRateDifferential()` |  |
| 399 | 4 | `public double ratePressure()` | Negative when the city pays over the odds: a rate advantage is support. |
| 405 | 5 | `public double effectivePressure()` | The pressure that actually reaches the rate this month. |
| 411 | 1 | `public double getLastPressure()` |  |
| 412 | 1 | `public double getLastAbsorption()` |  |
| 435 | 4 | `public void pinRate(double fixed)` | Holds the exchange rate still. |
| 443 | 1 | `public boolean isPinned()` | True while the rate is being held for a measurement. |
| 445 | 52 | `public void repriceCurrency()` |  |
| 499 | 1 | `public double deviationFromParity()` | How far the currency sits from parity. |
| 502 | 1 | `public double getRate()` | Local currency per US dollar. |
| 505 | 1 | `public double toLocal(double usd)` | What a USD amount is worth in the city's own money. |
| 508 | 1 | `public double toUsd(double local)` | ...and the other way. |

### the month's account (lines 510-517)

### the position (lines 518-519)

### TWO NUMBERS THAT WERE ONE NUMBER (lines 520-693)

| line | len | member | says |
|---:|---:|---|---|
| 605 | 6 | `public double importCover()` |  |
| 613 | 1 | `public double monthlyImports()` | The trailing monthly import bill the cover is measured against. |
| 616 | 1 | `public double monthlyExports()` | ...and the trailing figures the rate is decided on. |
| 617 | 1 | `public double monthlyCurrentAccount()` |  |
| 619 | 1 | `public double monthlyFinancialAccount()` | The financial account, trailing: positive is money coming in. |
| 645 | 1 | `public double getOpenness()` |  |
| 647 | 3 | `public void takeMonth(MoneyAudit.Result month)` |  |
| 652 | 41 | `public void takeMonth(MoneyAudit.Result month, double monthlyGdp)` |  |

### reading (lines 694-695)

### WHAT THE CITY OWES ABROAD (lines 696-825)

| line | len | member | says |
|---:|---:|---|---|
| 729 | 8 | `public void takeForeignDebt(double usdOutstanding, double rateNow)` |  |
| 739 | 1 | `public double getForeignDebt()` | What the city owes abroad, in local money at today's rate. |
| 740 | 1 | `public double getForeignDebtUsd()` |  |
| 743 | 1 | `public double getLastRevaluation()` | What the currency did to that debt this month. |
| 744 | 1 | `public double getLifetimeRevaluation()` |  |
| 754 | 1 | `public double getReservesUsd()` | The reserve position in dollars. |
| 763 | 1 | `public double netForeignPosition()` | Claims abroad less what is owed abroad: the city's net position. |
| 766 | 1 | `public double getExports()` | Goods and services sold abroad. |
| 769 | 1 | `public double tradeImports()` | Goods bought abroad, interest excluded. |
| 772 | 1 | `public double getForeignInterest()` | Interest paid to foreign creditors. |
| 775 | 1 | `public double tradeBalance()` | Exports less imports. |
| 778 | 1 | `public double currentAccount()` | ...less what was paid to foreign lenders. |
| 780 | 1 | `public double getFinancialIn()` |  |
| 781 | 1 | `public double getFinancialOut()` |  |
| 782 | 1 | `public double financialAccount()` |  |
| 785 | 1 | `public double balance()` | The month's change in the city's foreign position. |
| 789 | 1 | `public double getReserves()` | What the treasury holds in foreign money, and could spend today. |
| 792 | 1 | `public double getCumulativeBalance()` | Every month's balance of payments since founding, added up. |
| 803 | 1 | `public boolean inDeficit()` | Whether the city owes the world more than it holds there. |
| 806 | 1 | `public double getForgiven()` | Claims the world has written off, cumulatively. |
| 815 | 5 | `public void forgiveDebt(double localAmount)` | Debt the city refused to pay, and its creditors will not see again. |
| 824 | 1 | `public double getRepudiated()` | What the city has walked away from, since founding. |

### BUYING AND SELLING THE RESERVE (lines 826-938)

| line | len | member | says |
|---:|---:|---|---|
| 844 | 4 | `public void startMonth()` | Clears the month's intervention. |
| 858 | 6 | `public void buyReserves(double localAmount)` | Local currency spent buying foreign money. |
| 882 | 8 | `public double sellReserves(double localAmount)` | Foreign money sold for local currency. |
| 892 | 1 | `public double sellableReserves()` | The most the treasury could sell right now. |
| 894 | 1 | `public double getBoughtThisMonth()` |  |
| 895 | 1 | `public double getSoldThisMonth()` |  |
| 897 | 1 | `public double getLifetimeExports()` |  |
| 898 | 1 | `public double getLifetimeImports()` |  |
| 899 | 1 | `public double getLifetimeInterest()` |  |
| 900 | 1 | `public double getLifetimeFinancial()` |  |
| 918 | 3 | `public double balanceFromFlows()` | The cumulative balance, rebuilt from the flows that made it. |
| 932 | 1 | `public double getLifetimeIntervention()` |  |
| 935 | 1 | `public double valuationChange()` | This month's part of that. |

### carrying (lines 939-1112)

| line | len | member | says |
|---:|---:|---|---|
| 947 | 44 | `public double[] toSaveArray()` | A STOCK, so it is saved. |
| 992 | 35 | `public void restore(double[] saved)` |  |
| 1028 | 25 | `public void reset()` |  |
| 1072 | 39 | `public void redenominate(double scale)` | Every figure in the city's foreign accounts, in the new unit. |

