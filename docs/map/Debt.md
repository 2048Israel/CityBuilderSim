# Debt.java - 869 lines · 73 methods · 2 constants · model

`ham/citybuildersim/Debt.java` - generated 2026-10-23 by CodeMap; line numbers are as of that run.

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

**Uses:** [Game](Game.md) (3), [Auction](Auction.md) (1)

**Used by (30):** [Auction](Auction.md), [AuctionCheck](AuctionCheck.md), [BankCheck](BankCheck.md), [CentralBankCheck](CentralBankCheck.md), [ChartCheck](ChartCheck.md), [CityNeeds](CityNeeds.md), [CreditCheck](CreditCheck.md), [CurveCheck](CurveCheck.md), [DataSave](DataSave.md), [DebtManager](DebtManager.md), [FinancesScreen](FinancesScreen.md), [ForeignDebtCheck](ForeignDebtCheck.md), [FundCheck](FundCheck.md), [Game](Game.md), [GdpCheck](GdpCheck.md), [GovernmentScreen](GovernmentScreen.md), [HoldersCheck](HoldersCheck.md), [LongPlaytest](LongPlaytest.md), [LongTermBond](LongTermBond.md), [MediumTermBond](MediumTermBond.md), [MoneyCheck](MoneyCheck.md), [NewGameCheck](NewGameCheck.md), [ReadPathCheck](ReadPathCheck.md), [RestructureCheck](RestructureCheck.md), [SectorBooks](SectorBooks.md), [SectorScreen](SectorScreen.md), [SectorStatementCheck](SectorStatementCheck.md), [SectorStatements](SectorStatements.md), [ShortTermTBill](ShortTermTBill.md), [TreasuryCheck](TreasuryCheck.md)

## Sections

| line | section |
|---:|---|
| 37 | WHICH MONEY THIS PAPER IS WRITTEN IN |
| 95 | WHO HOLDS IT (0.7.1) |
| 248 | · THE ALLOTMENT (CD2, spec-city-debt.md section 4) |
| 399 | THE CITY'S BOOKS (CD3, spec-city-debt.md section 5) |
| 576 | · what a subclass declares, in its own currency |
| 593 | · ...and what the city's books see |
| 608 | · and the same figures, in dollars |
| 619 | · what the next month repays (0.7.13) |
| 638 | · paying for it |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 451 | `Debt.EFFECTIVE_RATE_FLOOR` | `- 0.5` | The lowest effective rate effectiveRateOf() will report: a premium so deep it costs the issuer less than nothing, a year. |
| 454 | `Debt.EFFECTIVE_RATE_CEILING` | `5.0` | The highest, as getYieldToMaturity()'s bracket always was: 500% a year. |

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
| 127 | `protected double firmPrincipal` | ...and the businesses, at face (CD2, slice 4): the sum of every sector's paper on OutwardInvestment. |
| 130 | `protected double worldPrincipal` | ...and the world, at face (CD2, slice 5): what it bought at the auction and holds on CapitalFlows' paper book, outside the bank's room. |
| 139 | `protected double settleDue` | What the buyers still owe for this paper: the cash the treasury received at issue, which the households and the bank hand over at the next settle (Game, THE BANK SETTLES). |
| 142 | `protected double issueYield` | The market rate it was issued at - the curve's rate for its maturity - which the households weigh against the deposit rate at the settle. |
| 154 | `protected double issueDiscount` | FACE LESS WHAT THE CITY RECEIVED FOR IT, at issue (0.7.1), and the part of it not yet accreted. |
| 155 | `protected double discountLeft` |  |
| 262 | `protected double allotHouseholdsFace, allotHouseholdsCash` | The face the sale still to be settled gave the households, and the cash they pay for it at the settle. |
| 265 | `protected double allotFirmsFace, allotFirmsCash` | ...the businesses' (CD2, slice 4: they pay it from their tills at the settle, Game.firmsTakeTheirShare()). |
| 268 | `protected double allotWorldFace, allotWorldCash` | ...and the world's (CD2, slice 5: it pays it from abroad at the settle, Game.worldTakesItsShare()). |
| 277 | `protected double unsettledFace` | The face the market has bought since the last settle and not yet paid for: the whole piece for a new one, the reopening's own face for a line reopened. |
| 457 | `protected double carryingValue` | What the piece is carried at, in the currency it is written in: what it raised, stepped by the effective interest less the payments. |
| 460 | `protected double effectiveRate` | The yield, a year, compounded monthly, at which the payments still owed are worth carryingValue: the piece's effective rate. |
| 463 | `protected boolean booksOpen` | True once openBooks() has run: what a sold piece's books are opened with, and what a piece built without a sale (a fixture, the founding's land bond) is given at par by its first month. |
| 466 | `protected double lastExpenseOwn, lastCouponOwn` | The month's interest expense and coupon as amortise() last booked them, in the currency the piece is written in. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 28 | 842 | **type** `public abstract class Debt` | One piece of city paper. |

### WHICH MONEY THIS PAPER IS WRITTEN IN (lines 37-94)

| line | len | member | says |
|---:|---:|---|---|
| 74 | 1 | `public boolean isForeign()` |  |
| 77 | 1 | `public double getExchangeRate()` | The rate this paper is currently being valued at. |
| 86 | 3 | `void setExchangeRate(double rate)` | Told to it by DebtManager, every month and on the load path. |
| 91 | 3 | `protected double inLocal(double own)` | USD into local money, for foreign paper; the identity for domestic. |

### WHO HOLDS IT (0.7.1) (lines 95-247)

| line | len | member | says |
|---:|---:|---|---|
| 157 | 1 | `public double householdPrincipal()` |  |
| 158 | 1 | `public double centralBankPrincipal()` |  |
| 159 | 1 | `public double firmPrincipal()` |  |
| 160 | 1 | `public double worldPrincipal()` |  |
| 163 | 5 | `public double bankPrincipal()` | What the commercial bank holds: the rest of the principal after the households, the central bank, the businesses and the world. |
| 169 | 1 | `public double getSettleDue()` |  |
| 170 | 1 | `public double getIssueYield()` |  |
| 171 | 1 | `public double getIssueDiscount()` |  |
| 172 | 1 | `public double getDiscountLeft()` |  |
| 175 | 9 | `void markIssued(double received, double yield)` | Told at issue what it raised and at what yield. |
| 207 | 15 | `void reopen(double face, double proceeds, double yield)` | A SALE INTO A LINE THAT IS ALREADY ON THE BOOKS (CD2, spec-city-debt.md section 2.2): this much more face of the same paper, at its coupon and its date, for `proceeds`. |
| 230 | 8 | `void reopenForCentralBank(double face, double price, double yield)` | The central bank's own par, rolled into a line that is already on the books (CD2): reopen() for this much face at this price, but paid for at once by the central bank (Game, THE CENTRAL BANK ROLLS ITS OWN, AT ISSUE) -... |
| 240 | 7 | `void settled()` | The settle has taken place: nothing is owed for this paper any more, and the allotment is paid. |

### THE ALLOTMENT (CD2, spec-city-debt.md section 4) (lines 248-398)

| line | len | member | says |
|---:|---:|---|---|
| 279 | 1 | `public double allotHouseholdsFace()` |  |
| 280 | 1 | `public double allotHouseholdsCash()` |  |
| 281 | 1 | `public double allotFirmsFace()` |  |
| 282 | 1 | `public double allotFirmsCash()` |  |
| 283 | 1 | `public double allotWorldFace()` |  |
| 284 | 1 | `public double allotWorldCash()` |  |
| 285 | 1 | `public double unsettledFace()` |  |
| 296 | 10 | `void allot(Auction.Fill fills, double sold, double cash)` | Adds a sale's allotment: each bidder's face from the auction's fills, and the cash it pays - its share of what the sale raised, since the buyers pay together what the treasury was paid (settleDue), the fee and the res... |
| 314 | 3 | `public double bankPrincipalPaid()` | What the commercial bank holds of this paper that it has PAID for: its principal less the face sold since the last settle and not yet paid for. |
| 324 | 8 | `public double unaccretedOnPaid()` | The discount the bank has not yet earned on what it has paid for: what is left to accrete on the part of the piece that has settled - the whole of it less the unsettled sale's own - pro rata to the face the bank holds... |
| 334 | 1 | `void moveToHouseholds(double face)` | Moves face between the holders: positive to the households, the central bank, the businesses or the world from the bank, negative back to it. |
| 335 | 1 | `void moveToCentralBank(double face)` |  |
| 336 | 1 | `void moveToFirms(double face)` |  |
| 337 | 1 | `void moveToWorld(double face)` |  |
| 358 | 17 | `void addOnForCentralBank(double face)` | THE CENTRAL BANK'S ADD-ON (0.7.15, round 2): this much more face of the same paper, on the same terms, for the central bank, which holds it from issue - its non-competitive bid, which the Federal Reserve Bank of New Y... |
| 380 | 4 | `public double unaccretedOn(double face)` | The unaccreted discount that rides on this much face - the part a holder gives up, or takes on, when that face changes hands. |
| 392 | 6 | `double accrete()` | This month's accretion: what is left of the discount over the months left, taken off what is left. |

### THE CITY'S BOOKS (CD3, spec-city-debt.md section 5) (lines 399-575)

| line | len | member | says |
|---:|---:|---|---|
| 479 | 13 | `static double effectiveRateOf(double[] flows, double price)` | The yield, a year compounded monthly, at which these payments are worth this price: the bisection getYieldToMaturity() always made, on a bracket that reaches below nothing so that a piece sold well over par (a coupon ... |
| 501 | 6 | `void openBooks(double proceedsOwn)` | The books are opened with what the sale raised, in the currency the piece is written in (the treasury's cash, net of the fixed cost): that is the carrying value, and the effective rate is solved on the payments the pi... |
| 509 | 3 | `private void ensureBooks()` | A piece that was never sold through the treasury (a fixture's, the founding's land bond) is carried at par before anything is added to it or booked on it: its effective rate is its coupon's. |
| 526 | 21 | `double amortise()` | This month's books, once, BEFORE processMonth() pays the month (the payment is read off the schedule as it stands, so the books follow the cash by construction): the interest expense at the effective rate on the carry... |
| 549 | 1 | `public final double carryingInCurrency()` | What the piece is carried at, in the currency written on it; par for a piece whose books were never opened. |
| 552 | 1 | `public final double getCarryingValue()` | ...and in local money, at the rate the paper is valued at (inLocal()). |
| 555 | 1 | `public final double getEffectiveRate()` | The effective rate, a year: the yield the piece's sale made of its payments. |
| 558 | 1 | `public final double getLastInterestExpense()` | This month's interest expense as amortise() booked it, in local money: the carrying value at the effective rate, a month. |
| 561 | 1 | `public final double getLastCoupon()` | ...the coupon that month paid, likewise. |
| 564 | 1 | `public final double getLastAmortised()` | ...and the part of the expense that is not coupon: the discount wearing off, below nothing for a premium. |
| 566 | 1 | `public abstract void processMonth(Game game)` |  |
| 568 | 1 | `public abstract double getIssuePrice()` |  |
| 570 | 1 | `public abstract int getMaturityMonth()` |  |
| 572 | 1 | `public abstract boolean isMatured()` |  |
| 574 | 1 | `public abstract String getType()` |  |

### what a subclass declares, in its own currency (lines 576-592)

| line | len | member | says |
|---:|---:|---|---|
| 579 | 1 | `protected abstract double principalOwed()` | Principal still owed, in the currency the paper is written in. |
| 582 | 1 | `protected abstract double couponOwed()` | This month's coupon, in its own currency. |
| 591 | 1 | `protected abstract double[] scheduleOwed()` | Every payment still owed, in order, starting with next month's, in its own currency. |

### ...and what the city's books see (lines 593-607)

| line | len | member | says |
|---:|---:|---|---|
| 595 | 1 | `public final double getOustandingPrincipal()` |  |
| 598 | 1 | `public final double getMonthlyInterestExpense()` | This month's coupon, in local money. |
| 600 | 7 | `public final double[] remainingCashFlows()` |  |

### and the same figures, in dollars (lines 608-618)

| line | len | member | says |
|---:|---:|---|---|
| 611 | 1 | `public final double principalInCurrency()` | Principal still owed, in the currency written on the paper. |
| 614 | 1 | `public final double faceInCurrency()` | Face value in the currency written on the paper. |
| 617 | 1 | `public final double couponInCurrency()` | This month's coupon in the currency written on the paper. |

### what the next month repays (0.7.13) (lines 619-637)

| line | len | member | says |
|---:|---:|---|---|
| 630 | 4 | `public final double principalDueNextMonthInCurrency()` | The principal the next month's payment repays, in the currency written on the paper: the first payment of its own schedule less that month's coupon - a note's or a term bond's whole face in its last month, a serial bo... |
| 636 | 1 | `public final double principalDueNextMonth()` | ...and in local money, at the rate the paper is valued at. |

### paying for it (lines 638-869)

| line | len | member | says |
|---:|---:|---|---|
| 651 | 4 | `protected void payPrincipal(Game game, double owed)` | A repayment of principal, routed by the currency it is owed in. |
| 660 | 4 | `protected void payCoupon(Game game, double owed)` | A coupon, likewise - and at home, split by who holds the paper (0.7.1): see Game.payDomesticCoupon(). |
| 666 | 3 | `public int getRemainingMonths()` | Months of payments still to run. |
| 671 | 3 | `public final double getFaceValue()` | What it says on the bond, in local money. |
| 675 | 3 | `public int getDuration()` |  |
| 679 | 3 | `public int getMonthStarted()` |  |
| 712 | 17 | `public double getMarketValue(double annualMarketRate)` | What this paper is worth today, to somebody buying it. |
| 731 | 25 | `static double presentValue(double[] cashFlows, double annualRate)` | PV of a monthly schedule at an annual nominal rate. |
| 775 | 30 | `public double getYieldToMaturity(double price)` | The yield a buyer earns at a given price - the bond's true cost to the city, as opposed to the coupon printed on it. |
| 807 | 3 | `public double getCurrentYield(double annualMarketRate)` | Yield at what the market would actually pay today. |
| 818 | 5 | `public double getPriceAsPercentOfPar(double annualMarketRate)` | Where this bond trades against par, as a percentage of face. |
| 833 | 29 | `public void redenominate(double scale)` | The instrument in the new unit. |
| 867 | 1 | `protected void redenominateSchedule(double scale)` | Anything a subclass carries in its own currency - a coupon, an amortisation schedule - in the new unit. |

