# Debt.java - 517 lines · 47 methods · 0 constants · model

`ham/citybuildersim/Debt.java` - generated 2026-10-01 by CodeMap; line numbers are as of that run.

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

**Used by (22):** [BankCheck](BankCheck.md), [CentralBankCheck](CentralBankCheck.md), [ChartCheck](ChartCheck.md), [CreditCheck](CreditCheck.md), [DataSave](DataSave.md), [DebtManager](DebtManager.md), [FinancesScreen](FinancesScreen.md), [ForeignDebtCheck](ForeignDebtCheck.md), [FundCheck](FundCheck.md), [Game](Game.md), [GdpCheck](GdpCheck.md), [GovernmentScreen](GovernmentScreen.md), [HoldersCheck](HoldersCheck.md), [LongPlaytest](LongPlaytest.md), [LongTermBond](LongTermBond.md), [MediumTermBond](MediumTermBond.md), [NewGameCheck](NewGameCheck.md), [ReadPathCheck](ReadPathCheck.md), [RestructureCheck](RestructureCheck.md), [ShortTermTBill](ShortTermTBill.md), [TreasuryCheck](TreasuryCheck.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 37 | WHICH MONEY THIS PAPER IS WRITTEN IN |
| 95 | WHO HOLDS IT (0.7.1) |
| 238 | · what a subclass declares, in its own currency |
| 255 | · ...and what the city's books see |
| 270 | · and the same figures, in dollars |
| 281 | · what the next month repays (0.7.13) |
| 300 | · paying for it |

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
| 121 | `protected double householdPrincipal` | Principal of this paper the city's households hold, in local money. |
| 124 | `protected double centralBankPrincipal` | ...and the central bank, at face (its open-market holdings). |
| 133 | `protected double settleDue` | What the buyers still owe for this paper: the cash the treasury received at issue, which the households and the bank hand over at the next settle (Game, THE BANK SETTLES). |
| 136 | `protected double issueYield` | The market rate it was issued at - the curve's rate for its maturity - which the households weigh against the deposit rate at the settle. |
| 148 | `protected double issueDiscount` | FACE LESS WHAT THE CITY RECEIVED FOR IT, at issue (0.7.1), and the part of it not yet accreted. |
| 149 | `protected double discountLeft` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 28 | 490 | **type** `public abstract class Debt` | One piece of city paper. |

### WHICH MONEY THIS PAPER IS WRITTEN IN (lines 37-94)

| line | len | member | says |
|---:|---:|---|---|
| 74 | 1 | `public boolean isForeign()` |  |
| 77 | 1 | `public double getExchangeRate()` | The rate this paper is currently being valued at. |
| 86 | 3 | `void setExchangeRate(double rate)` | Told to it by DebtManager, every month and on the load path. |
| 91 | 3 | `protected double inLocal(double own)` | USD into local money, for foreign paper; the identity for domestic. |

### WHO HOLDS IT (0.7.1) (lines 95-237)

| line | len | member | says |
|---:|---:|---|---|
| 151 | 1 | `public double householdPrincipal()` |  |
| 152 | 1 | `public double centralBankPrincipal()` |  |
| 155 | 3 | `public double bankPrincipal()` | What the commercial bank holds: the rest of the principal. |
| 159 | 1 | `public double getSettleDue()` |  |
| 160 | 1 | `public double getIssueYield()` |  |
| 161 | 1 | `public double getIssueDiscount()` |  |
| 162 | 1 | `public double getDiscountLeft()` |  |
| 165 | 7 | `void markIssued(double received, double yield)` | Told at issue what it raised and at what yield. |
| 174 | 1 | `void settled()` | The settle has taken place: nothing is owed for this paper any more. |
| 177 | 1 | `void moveToHouseholds(double face)` | Moves face between the holders: positive to the households or the central bank from the bank, negative back to it. |
| 178 | 1 | `void moveToCentralBank(double face)` |  |
| 193 | 11 | `void addOnForCentralBank(double face)` | THE CENTRAL BANK'S ADD-ON (0.7.15, round 2): this much more face of the same paper, on the same terms, for the central bank, which holds it from issue - its non-competitive bid, which the Federal Reserve Bank of New Y... |
| 209 | 4 | `public double unaccretedOn(double face)` | The unaccreted discount that rides on this much face - the part a holder gives up, or takes on, when that face changes hands. |
| 221 | 6 | `double accrete()` | This month's accretion: what is left of the discount over the months left, taken off what is left. |
| 228 | 1 | `public abstract void processMonth(Game game)` |  |
| 230 | 1 | `public abstract double getIssuePrice()` |  |
| 232 | 1 | `public abstract int getMaturityMonth()` |  |
| 234 | 1 | `public abstract boolean isMatured()` |  |
| 236 | 1 | `public abstract String getType()` |  |

### what a subclass declares, in its own currency (lines 238-254)

| line | len | member | says |
|---:|---:|---|---|
| 241 | 1 | `protected abstract double principalOwed()` | Principal still owed, in the currency the paper is written in. |
| 244 | 1 | `protected abstract double couponOwed()` | This month's coupon, in its own currency. |
| 253 | 1 | `protected abstract double[] scheduleOwed()` | Every payment still owed, in order, starting with next month's, in its own currency. |

### ...and what the city's books see (lines 255-269)

| line | len | member | says |
|---:|---:|---|---|
| 257 | 1 | `public final double getOustandingPrincipal()` |  |
| 260 | 1 | `public final double getMonthlyInterestExpense()` | This month's coupon, in local money. |
| 262 | 7 | `public final double[] remainingCashFlows()` |  |

### and the same figures, in dollars (lines 270-280)

| line | len | member | says |
|---:|---:|---|---|
| 273 | 1 | `public final double principalInCurrency()` | Principal still owed, in the currency written on the paper. |
| 276 | 1 | `public final double faceInCurrency()` | Face value in the currency written on the paper. |
| 279 | 1 | `public final double couponInCurrency()` | This month's coupon in the currency written on the paper. |

### what the next month repays (0.7.13) (lines 281-299)

| line | len | member | says |
|---:|---:|---|---|
| 292 | 4 | `public final double principalDueNextMonthInCurrency()` | The principal the next month's payment repays, in the currency written on the paper: the first payment of its own schedule less that month's coupon - a note's or a term bond's whole face in its last month, a serial bo... |
| 298 | 1 | `public final double principalDueNextMonth()` | ...and in local money, at the rate the paper is valued at. |

### paying for it (lines 300-517)

| line | len | member | says |
|---:|---:|---|---|
| 313 | 4 | `protected void payPrincipal(Game game, double owed)` | A repayment of principal, routed by the currency it is owed in. |
| 322 | 4 | `protected void payCoupon(Game game, double owed)` | A coupon, likewise - and at home, split by who holds the paper (0.7.1): see Game.payDomesticCoupon(). |
| 328 | 3 | `public int getRemainingMonths()` | Months of payments still to run. |
| 333 | 3 | `public final double getFaceValue()` | What it says on the bond, in local money. |
| 337 | 3 | `public int getDuration()` |  |
| 341 | 3 | `public int getMonthStarted()` |  |
| 374 | 17 | `public double getMarketValue(double annualMarketRate)` | What this paper is worth today, to somebody buying it. |
| 393 | 25 | `static double presentValue(double[] cashFlows, double annualRate)` | PV of a monthly schedule at an annual nominal rate. |
| 437 | 30 | `public double getYieldToMaturity(double price)` | The yield a buyer earns at a given price - the bond's true cost to the city, as opposed to the coupon printed on it. |
| 469 | 3 | `public double getCurrentYield(double annualMarketRate)` | Yield at what the market would actually pay today. |
| 480 | 5 | `public double getPriceAsPercentOfPar(double annualMarketRate)` | Where this bond trades against par, as a percentage of face. |
| 495 | 15 | `public void redenominate(double scale)` | The instrument in the new unit. |
| 515 | 1 | `protected void redenominateSchedule(double scale)` | Anything a subclass carries in its own currency - a coupon, an amortisation schedule - in the new unit. |

