# Debt.java - 351 lines · 31 methods · 0 constants · model

`ham/citybuildersim/Debt.java` - generated 2026-09-21 by CodeMap; line numbers are as of that run.

> One piece of city paper.
> 
> PRICED OFF ITS OWN CASH FLOWS, NOT OFF ITS SHAPE
> 
> Everything a bond is worth, and everything it yields, comes from one thing:
> the money it still owes and when. So that is what a subclass declares -
> `remainingCashFlows()`, one figure per remaining month - and market value and
> yield to maturity are computed here, once, from that.
> 
> The previous version used the closed-form annuity instead:
> 
>     MV = c x [1 - (1+r)^-n] / r  +  F / (1+r)^n
> 
> which is exactly right for a bullet and silently wrong for anything else. A
> serial bond repays principal in slices, so its coupon falls every year and its
> principal arrives in pieces; the annuity formula would have priced it as
> though the whole face were sitting at the end, over-valuing it badly. Adding
> the serial bond is what made the shape assumption visible, but the assumption
> was already there.
> 
> A schedule cannot be wrong about a shape it does not know about. The next
> instrument - a revenue bond, a serial with a deferred first slice, anything -
> gets correct pricing and yield with no change here at all.

**Uses:** [Game](Game.md) (3)

**Used by (14):** [CreditCheck](CreditCheck.md), [DataSave](DataSave.md), [DebtManager](DebtManager.md), [FinancesScreen](FinancesScreen.md), [ForeignDebtCheck](ForeignDebtCheck.md), [Game](Game.md), [GdpCheck](GdpCheck.md), [GovernmentScreen](GovernmentScreen.md), [LongTermBond](LongTermBond.md), [MediumTermBond](MediumTermBond.md), [RestructureCheck](RestructureCheck.md), [ShortTermTBill](ShortTermTBill.md), [TreasuryCheck](TreasuryCheck.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 37 | WHICH MONEY THIS PAPER IS WRITTEN IN |
| 105 | · what a subclass declares, in its own currency |
| 122 | · ...and what the city's books see |
| 137 | · and the same figures, in dollars |
| 148 | · paying for it |

## Fields (state)

| line | field | says |
|---:|---|---|
| 30 | `protected double faceValue` |  |
| 31 | `protected double outstandingPrincipal` |  |
| 32 | `protected int duration` |  |
| 33 | `protected int remainingMonths` |  |
| 34 | `protected int monthStarted` |  |
| 35 | `protected String type` |  |
| 69 | `protected boolean foreign` | True if this paper is written in USD. |
| 72 | `protected double exchangeRate` | Local currency per USD, as of the last month tick. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 28 | 324 | **type** `public abstract class Debt` | One piece of city paper. |

### WHICH MONEY THIS PAPER IS WRITTEN IN (lines 37-104)

| line | len | member | says |
|---:|---:|---|---|
| 74 | 1 | `public boolean isForeign()` |  |
| 77 | 1 | `public double getExchangeRate()` | The rate this paper is currently being valued at. |
| 86 | 3 | `void setExchangeRate(double rate)` | Told to it by DebtManager, every month and on the load path. |
| 91 | 3 | `protected double inLocal(double own)` | USD into local money, for foreign paper; the identity for domestic. |
| 95 | 1 | `public abstract void processMonth(Game game)` |  |
| 97 | 1 | `public abstract double getIssuePrice()` |  |
| 99 | 1 | `public abstract int getMaturityMonth()` |  |
| 101 | 1 | `public abstract boolean isMatured()` |  |
| 103 | 1 | `public abstract String getType()` |  |

### what a subclass declares, in its own currency (lines 105-121)

| line | len | member | says |
|---:|---:|---|---|
| 108 | 1 | `protected abstract double principalOwed()` | Principal still owed, in the currency the paper is written in. |
| 111 | 1 | `protected abstract double couponOwed()` | This month's coupon, in its own currency. |
| 120 | 1 | `protected abstract double[] scheduleOwed()` | Every payment still owed, in order, starting with next month's, in its own currency. |

### ...and what the city's books see (lines 122-136)

| line | len | member | says |
|---:|---:|---|---|
| 124 | 1 | `public final double getOustandingPrincipal()` |  |
| 127 | 1 | `public final double getMonthlyInterestExpense()` | This month's coupon, in local money. |
| 129 | 7 | `public final double[] remainingCashFlows()` |  |

### and the same figures, in dollars (lines 137-147)

| line | len | member | says |
|---:|---:|---|---|
| 140 | 1 | `public final double principalInCurrency()` | Principal still owed, in the currency written on the paper. |
| 143 | 1 | `public final double faceInCurrency()` | Face value in the currency written on the paper. |
| 146 | 1 | `public final double couponInCurrency()` | This month's coupon in the currency written on the paper. |

### paying for it (lines 148-351)

| line | len | member | says |
|---:|---:|---|---|
| 161 | 4 | `protected void payPrincipal(Game game, double owed)` | A repayment of principal, routed by the currency it is owed in. |
| 167 | 4 | `protected void payCoupon(Game game, double owed)` | A coupon, likewise. |
| 173 | 3 | `public int getRemainingMonths()` | Months of payments still to run. |
| 178 | 3 | `public final double getFaceValue()` | What it says on the bond, in local money. |
| 182 | 3 | `public int getDuration()` |  |
| 186 | 3 | `public int getMonthStarted()` |  |
| 215 | 17 | `public double getMarketValue(double annualMarketRate)` | What this paper is worth today, to somebody buying it. |
| 234 | 25 | `static double presentValue(double[] cashFlows, double annualRate)` | PV of a monthly schedule at an annual nominal rate. |
| 278 | 30 | `public double getYieldToMaturity(double price)` | The yield a buyer earns at a given price - the bond's true cost to the city, as opposed to the coupon printed on it. |
| 310 | 3 | `public double getCurrentYield(double annualMarketRate)` | Yield at what the market would actually pay today. |
| 321 | 5 | `public double getPriceAsPercentOfPar(double annualMarketRate)` | Where this bond trades against par, as a percentage of face. |
| 336 | 8 | `public void redenominate(double scale)` | The instrument in the new unit. |
| 349 | 1 | `protected void redenominateSchedule(double scale)` | Anything a subclass carries in its own currency - a coupon, an amortisation schedule - in the new unit. |

