# ForeignAccounts.java - 1,305 lines · 70 methods · 14 constants · model

`ham/citybuildersim/ForeignAccounts.java` - generated 2026-09-21 by CodeMap; line numbers are as of that run.

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
> ALL FOUR ARE BUILT, and the list is kept as the plan it was. The rate
> floats (repriceCurrency(), WHAT MOVES THE RATE); the treasury borrows in
> dollars (WHAT THE CITY OWES ABROAD, and DebtManager's foreign paper); the
> hot money and the carry trade are CapitalFlows.
> 
> See claude/foreign-exchange-design.md.
> 
> ==================== WHAT A RESERVE IS, TODAY ====================
> 
> TWO NUMBERS, which were one number until the split (TWO NUMBERS THAT WERE
> ONE NUMBER, below).
> 
> THE VAULT is a holding: the US dollars the treasury chose to buy and has
> not yet sold. Only the treasury buying and selling moves it (buyReserves(),
> sellReserves()), it cannot go below zero, and it is KEPT IN DOLLARS -
> reservesUsd - so everything local about it, what it is worth, what can be
> sold, the import cover and the net position, is those dollars at today's
> rate. When the currency moves the dollars stay put, and the move in their
> local value is booked beside the vault as a revaluation (revalueVault()),
> in the dollar debt's shape: not cash, and not an audit flow. A new city
> opens with the founders' US$1B in it (Game's THE FOUNDING RESERVE), and it
> damps the pressure on the rate only when the push would weaken the currency
> (A RESERVE DEFENDS A CURRENCY, at effectivePressure()).
> 
> ... (22 more lines in the source)

**Uses:** [MoneyAudit](MoneyAudit.md) (2)

**Used by (17):** [CapitalFlowCheck](CapitalFlowCheck.md), [CarryTradeCheck](CarryTradeCheck.md), [CityBasket](CityBasket.md), [FinancesScreen](FinancesScreen.md), [ForeignCheck](ForeignCheck.md), [ForeignDebtCheck](ForeignDebtCheck.md), [Game](Game.md), [HistorySave](HistorySave.md), [HouseholdBalance](HouseholdBalance.md), [LongPlaytest](LongPlaytest.md), [MonetaryCheck](MonetaryCheck.md), [NewGameCheck](NewGameCheck.md), [OutwardInvestment](OutwardInvestment.md), [PolicyScreen](PolicyScreen.md), [SummaryScreen](SummaryScreen.md), [TradeScreen](TradeScreen.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 89 | · the rate |
| 101 | WHAT MOVES THE RATE |
| 253 | · · PARITY IS A RATE, NOT A RATIO, AND IT NEEDS AN ANCHOR IN MONEY. |
| 573 | · the month's account |
| 581 | · the position |
| 583 | TWO NUMBERS THAT WERE ONE NUMBER |
| 797 | · reading |
| 799 | WHAT THE CITY OWES ABROAD |
| 931 | BUYING AND SELLING THE RESERVE |
| 1010 | · what the currency did to the vault |
| 1082 | · carrying |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 99 | `ForeignAccounts.OPENING_RATE` | `1.00` | Local currency per US dollar. |
| 121 | `ForeignAccounts.DEAD_BAND` | `.05` | Deficits smaller than this share of trade are noise, and are ignored. |
| 124 | `ForeignAccounts.COMFORTABLE_COVER` | `6` | Cover at which reserves fully absorb the month's pressure. |
| 127 | `ForeignAccounts.MAX_ABSORPTION` | `.85` | The most of the pressure deep reserves can absorb. |
| 137 | `ForeignAccounts.DRIFT_SPEED` | `.02` | How much of a month's pressure passes into the rate. |
| 157 | `ForeignAccounts.MIN_RATE` | `.01` | UNCAPPED, and these are now a numeric guard rather than a policy. |
| 158 | `ForeignAccounts.MAX_RATE` | `100.0` |  |
| 235 | `ForeignAccounts.OPENING_PARITY` | `1.00` | The rate at which a basket costs the same at home and abroad. |
| 289 | `ForeignAccounts.REVERSION` | `.004` | How hard. |
| 351 | `ForeignAccounts.SETTLING_MONTHS` | `24` | Months before the rate is allowed to move at all. |
| 354 | `ForeignAccounts.MIN_TRADE` | `50` | Below this much trade a month, the exchange rate is not a real price. |
| 413 | `ForeignAccounts.RATE_PULL` | `6.0` | How far the city's own rate is above the world's, and what that is worth to the currency. |
| 416 | `ForeignAccounts.MAX_RATE_PRESSURE` | `.8` | Most of the pressure a rate differential alone can produce. |
| 688 | `ForeignAccounts.COVER_WINDOW` | `12` | How many months the trailing figures are averaged over: the import bill the cover is measured against, and the balances the pressure reads. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 172 | `private double minRate` | The same three, in TODAY's money. |
| 173 | `private double maxRate` |  |
| 174 | `private double parityBase` |  |
| 238 | `private double parity` | Save slot 18. |
| 240 | `private double localInflation, worldInflation` |  |
| 291 | `private double rate` |  |
| 292 | `private double lastPressure` |  |
| 293 | `private double lastAbsorption` |  |
| 373 | `private double minTrade` | The same floor, in TODAY's money. |
| 418 | `private double rateDifferential` |  |
| 496 | `private boolean pinned` |  |
| 575 | `private double exports` |  |
| 576 | `private double imports` |  |
| 577 | `private double foreignInterest` |  |
| 578 | `private double financialIn` |  |
| 579 | `private double financialOut` |  |
| 633 | `private double reservesUsd` |  |
| 645 | `private double cumulativeBalance` | THE RECORD. |
| 646 | `private double forgiven` |  |
| 649 | `private double exportsTrailing` | Trailing means, for anything the rate is decided on. |
| 650 | `private double currentTrailing` |  |
| 656 | `private double financialTrailing` | The financial account, trailing, and the gross of it - what crossed in either direction - so the pressure can be struck on the OVERALL balance over everything that crossed. |
| 657 | `private double financialGrossTrailing` |  |
| 671 | `private double lifetimeExports` | SINCE FOUNDING. |
| 672 | `private double lifetimeImports` |  |
| 673 | `private double lifetimeInterest` |  |
| 674 | `private double lifetimeFinancial` |  |
| 675 | `private double importsTrailing` |  |
| 676 | `private int monthsOfHistory` |  |
| 737 | `private double openness` | How much of the economy actually crosses the border. |
| 822 | `private double foreignDebt` | local value of USD paper outstanding |
| 823 | `private double foreignDebtUsd` | local value of USD paper outstanding |
| 824 | `private double lastDebtRate` | ...and in the dollars it is owed in |
| 825 | `private double lastRevaluation` |  |
| 826 | `private double lifetimeRevaluation` |  |
| 926 | `private double repudiated` |  |
| 945 | `private double boughtThisMonth` |  |
| 946 | `private double soldThisMonth` |  |
| 1030 | `private double lastVaultRate` |  |
| 1031 | `private double lastVaultRevaluation` |  |
| 1073 | `private double lifetimeIntervention` | What the treasury has bought and sold on its own account, since founding. |
| 1080 | `private double lastValuation` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 87 | 1219 | **type** `public class ForeignAccounts` | The city's dealings with the rest of the world: the balance of payments, the reserve position, and the exchange rate. |

### the rate (lines 89-100)

### WHAT MOVES THE RATE (lines 101-572)

| line | len | member | says |
|---:|---:|---|---|
| 176 | 1 | `public double getMinRate()` |  |
| 177 | 1 | `public double getMaxRate()` |  |
| 180 | 6 | `public void seedConstants(double unit)` | Re-seeds the money CONSTANTS at a given unit. |
| 251 | 31 | `public void setParity(double localLevel, double worldLevel, double localRate, double worldRate)` | The two inflation rates the drift is struck from, and the level ratio kept for the screen. |
| 284 | 1 | `public double inflationGap()` | How far the city's inflation is running above the world's. |
| 286 | 1 | `public double getParity()` |  |
| 299 | 43 | `public double pressure()` | The month's depreciation pressure: the current-account deficit as a share of everything the city trades. |
| 386 | 6 | `public double absorption()` | How much of it the reserve absorbs. |
| 421 | 3 | `public void setRateDifferential(double differential)` |  |
| 425 | 1 | `public double getRateDifferential()` |  |
| 428 | 4 | `public double ratePressure()` | Negative when the city pays over the odds: a rate advantage is support. |
| 468 | 5 | `public double effectivePressure()` | The pressure that actually reaches the rate this month. |
| 474 | 1 | `public double getLastPressure()` |  |
| 475 | 1 | `public double getLastAbsorption()` |  |
| 491 | 4 | `public void pinRate(double fixed)` | Holds the exchange rate still. |
| 499 | 1 | `public boolean isPinned()` | True while the rate is being held for a measurement. |
| 508 | 52 | `public void repriceCurrency()` | Moves the rate on the month just taken. |
| 562 | 1 | `public double deviationFromParity()` | How far the currency sits from parity. |
| 565 | 1 | `public double getRate()` | Local currency per US dollar. |
| 568 | 1 | `public double toLocal(double usd)` | What a USD amount is worth in the city's own money. |
| 571 | 1 | `public double toUsd(double local)` | ...and the other way. |

### the month's account (lines 573-580)

### the position (lines 581-582)

### TWO NUMBERS THAT WERE ONE NUMBER (lines 583-796)

| line | len | member | says |
|---:|---:|---|---|
| 699 | 14 | `public double importCover()` | Months of imports the reserve would cover. |
| 715 | 1 | `public double monthlyImports()` | The trailing monthly import bill the cover is measured against. |
| 718 | 1 | `public double monthlyExports()` | ...and the trailing figures the rate is decided on. |
| 719 | 1 | `public double monthlyCurrentAccount()` |  |
| 721 | 1 | `public double monthlyFinancialAccount()` | The financial account, trailing: positive is money coming in. |
| 739 | 1 | `public double getOpenness()` |  |
| 741 | 3 | `public void takeMonth(MoneyAudit.Result month)` |  |
| 755 | 41 | `public void takeMonth(MoneyAudit.Result month, double monthlyGdp)` | Takes the month off the audit that has just been struck. |

### reading (lines 797-798)

### WHAT THE CITY OWES ABROAD (lines 799-930)

| line | len | member | says |
|---:|---:|---|---|
| 832 | 8 | `public void takeForeignDebt(double usdOutstanding, double rateNow)` |  |
| 842 | 1 | `public double getForeignDebt()` | What the city owes abroad, in local money at today's rate. |
| 843 | 1 | `public double getForeignDebtUsd()` |  |
| 846 | 1 | `public double getLastRevaluation()` | What the currency did to that debt this month. |
| 847 | 1 | `public double getLifetimeRevaluation()` |  |
| 857 | 1 | `public double getReservesUsd()` | The reserve position in dollars - the stock itself. |
| 867 | 1 | `public double netForeignPosition()` | Claims abroad less what is owed abroad: the city's net position. |
| 870 | 1 | `public double getExports()` | Goods and services sold abroad. |
| 873 | 1 | `public double tradeImports()` | Goods bought abroad, interest excluded. |
| 876 | 1 | `public double getForeignInterest()` | Interest paid to foreign creditors. |
| 879 | 1 | `public double tradeBalance()` | Exports less imports. |
| 882 | 1 | `public double currentAccount()` | ...less what was paid to foreign lenders. |
| 884 | 1 | `public double getFinancialIn()` |  |
| 885 | 1 | `public double getFinancialOut()` |  |
| 886 | 1 | `public double financialAccount()` |  |
| 889 | 1 | `public double balance()` | The month's change in the city's foreign position. |
| 895 | 1 | `public double getReserves()` | What the treasury holds in foreign money, and could spend today - in local money, at today's rate. |
| 898 | 1 | `public double getCumulativeBalance()` | Every month's balance of payments since founding, added up. |
| 908 | 1 | `public boolean inDeficit()` | Whether the city owes the world more than it holds there. |
| 911 | 1 | `public double getForgiven()` | Claims the world has written off, cumulatively. |
| 920 | 5 | `public void forgiveDebt(double localAmount)` | Debt the city refused to pay, and its creditors will not see again. |
| 929 | 1 | `public double getRepudiated()` | What the city has walked away from, since founding. |

### BUYING AND SELLING THE RESERVE (lines 931-1009)

| line | len | member | says |
|---:|---:|---|---|
| 949 | 4 | `public void startMonth()` | Clears the month's intervention. |
| 967 | 6 | `public void buyReserves(double localAmount)` | Local currency spent buying foreign money. |
| 997 | 9 | `public double sellReserves(double localAmount)` | Foreign money sold for local currency. |
| 1008 | 1 | `public double sellableReserves()` | The most the treasury could sell right now, in local money at today's rate. |

### what the currency did to the vault (lines 1010-1081)

| line | len | member | says |
|---:|---:|---|---|
| 1034 | 5 | `public void revalueVault()` | Values the vault at today's rate. |
| 1041 | 1 | `public double getLastVaultRevaluation()` | What the currency did to the vault this month, in local money. |
| 1043 | 1 | `public double getBoughtThisMonth()` |  |
| 1044 | 1 | `public double getSoldThisMonth()` |  |
| 1046 | 1 | `public double getLifetimeExports()` |  |
| 1047 | 1 | `public double getLifetimeImports()` |  |
| 1048 | 1 | `public double getLifetimeInterest()` |  |
| 1049 | 1 | `public double getLifetimeFinancial()` |  |
| 1061 | 3 | `public double balanceFromFlows()` | The cumulative balance, rebuilt from the flows that made it. |
| 1075 | 1 | `public double getLifetimeIntervention()` |  |
| 1078 | 1 | `public double valuationChange()` | This month's part of that. |

### carrying (lines 1082-1305)

| line | len | member | says |
|---:|---:|---|---|
| 1094 | 67 | `public double[] toSaveArray()` | A STOCK, so it is saved. |
| 1162 | 53 | `public void restore(double[] saved)` |  |
| 1216 | 27 | `public void reset()` |  |
| 1264 | 40 | `public void redenominate(double scale)` | Every figure in the city's foreign accounts, in the new unit. |

