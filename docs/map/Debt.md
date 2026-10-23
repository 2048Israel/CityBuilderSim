# Debt.java - 681 lines · 63 methods · 0 constants · model

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

**Used by (29):** [Auction](Auction.md), [AuctionCheck](AuctionCheck.md), [BankCheck](BankCheck.md), [CentralBankCheck](CentralBankCheck.md), [ChartCheck](ChartCheck.md), [CityNeeds](CityNeeds.md), [CreditCheck](CreditCheck.md), [CurveCheck](CurveCheck.md), [DataSave](DataSave.md), [DebtManager](DebtManager.md), [FinancesScreen](FinancesScreen.md), [ForeignDebtCheck](ForeignDebtCheck.md), [FundCheck](FundCheck.md), [Game](Game.md), [GdpCheck](GdpCheck.md), [GovernmentScreen](GovernmentScreen.md), [HoldersCheck](HoldersCheck.md), [LongPlaytest](LongPlaytest.md), [LongTermBond](LongTermBond.md), [MediumTermBond](MediumTermBond.md), [NewGameCheck](NewGameCheck.md), [ReadPathCheck](ReadPathCheck.md), [RestructureCheck](RestructureCheck.md), [SectorBooks](SectorBooks.md), [SectorScreen](SectorScreen.md), [SectorStatementCheck](SectorStatementCheck.md), [SectorStatements](SectorStatements.md), [ShortTermTBill](ShortTermTBill.md), [TreasuryCheck](TreasuryCheck.md)

## Sections

| line | section |
|---:|---|
| 37 | WHICH MONEY THIS PAPER IS WRITTEN IN |
| 95 | WHO HOLDS IT (0.7.1) |
| 239 | · THE ALLOTMENT (CD2, spec-city-debt.md section 4) |
| 393 | · what a subclass declares, in its own currency |
| 410 | · ...and what the city's books see |
| 425 | · and the same figures, in dollars |
| 436 | · what the next month repays (0.7.13) |
| 455 | · paying for it |

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
| 253 | `protected double allotHouseholdsFace, allotHouseholdsCash` | The face the sale still to be settled gave the households, and the cash they pay for it at the settle. |
| 256 | `protected double allotFirmsFace, allotFirmsCash` | ...the businesses' (CD2, slice 4: they pay it from their tills at the settle, Game.firmsTakeTheirShare()). |
| 259 | `protected double allotWorldFace, allotWorldCash` | ...and the world's (CD2, slice 5: it pays it from abroad at the settle, Game.worldTakesItsShare()). |
| 268 | `protected double unsettledFace` | The face the market has bought since the last settle and not yet paid for: the whole piece for a new one, the reopening's own face for a line reopened. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 28 | 654 | **type** `public abstract class Debt` | One piece of city paper. |

### WHICH MONEY THIS PAPER IS WRITTEN IN (lines 37-94)

| line | len | member | says |
|---:|---:|---|---|
| 74 | 1 | `public boolean isForeign()` |  |
| 77 | 1 | `public double getExchangeRate()` | The rate this paper is currently being valued at. |
| 86 | 3 | `void setExchangeRate(double rate)` | Told to it by DebtManager, every month and on the load path. |
| 91 | 3 | `protected double inLocal(double own)` | USD into local money, for foreign paper; the identity for domestic. |

### WHO HOLDS IT (0.7.1) (lines 95-238)

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
| 175 | 8 | `void markIssued(double received, double yield)` | Told at issue what it raised and at what yield. |
| 201 | 12 | `void reopen(double face, double proceeds, double yield)` | A SALE INTO A LINE THAT IS ALREADY ON THE BOOKS (CD2, spec-city-debt.md section 2.2): this much more face of the same paper, at its coupon and its date, for `proceeds`. |
| 221 | 8 | `void reopenForCentralBank(double face, double price, double yield)` | The central bank's own par, rolled into a line that is already on the books (CD2): reopen() for this much face at this price, but paid for at once by the central bank (Game, THE CENTRAL BANK ROLLS ITS OWN, AT ISSUE) -... |
| 231 | 7 | `void settled()` | The settle has taken place: nothing is owed for this paper any more, and the allotment is paid. |

### THE ALLOTMENT (CD2, spec-city-debt.md section 4) (lines 239-392)

| line | len | member | says |
|---:|---:|---|---|
| 270 | 1 | `public double allotHouseholdsFace()` |  |
| 271 | 1 | `public double allotHouseholdsCash()` |  |
| 272 | 1 | `public double allotFirmsFace()` |  |
| 273 | 1 | `public double allotFirmsCash()` |  |
| 274 | 1 | `public double allotWorldFace()` |  |
| 275 | 1 | `public double allotWorldCash()` |  |
| 276 | 1 | `public double unsettledFace()` |  |
| 287 | 10 | `void allot(Auction.Fill fills, double sold, double cash)` | Adds a sale's allotment: each bidder's face from the auction's fills, and the cash it pays - its share of what the sale raised, since the buyers pay together what the treasury was paid (settleDue), the fee and the res... |
| 305 | 3 | `public double bankPrincipalPaid()` | What the commercial bank holds of this paper that it has PAID for: its principal less the face sold since the last settle and not yet paid for. |
| 315 | 8 | `public double unaccretedOnPaid()` | The discount the bank has not yet earned on what it has paid for: what is left to accrete on the part of the piece that has settled - the whole of it less the unsettled sale's own - pro rata to the face the bank holds... |
| 325 | 1 | `void moveToHouseholds(double face)` | Moves face between the holders: positive to the households, the central bank, the businesses or the world from the bank, negative back to it. |
| 326 | 1 | `void moveToCentralBank(double face)` |  |
| 327 | 1 | `void moveToFirms(double face)` |  |
| 328 | 1 | `void moveToWorld(double face)` |  |
| 345 | 14 | `void addOnForCentralBank(double face)` | THE CENTRAL BANK'S ADD-ON (0.7.15, round 2): this much more face of the same paper, on the same terms, for the central bank, which holds it from issue - its non-competitive bid, which the Federal Reserve Bank of New Y... |
| 364 | 4 | `public double unaccretedOn(double face)` | The unaccreted discount that rides on this much face - the part a holder gives up, or takes on, when that face changes hands. |
| 376 | 6 | `double accrete()` | This month's accretion: what is left of the discount over the months left, taken off what is left. |
| 383 | 1 | `public abstract void processMonth(Game game)` |  |
| 385 | 1 | `public abstract double getIssuePrice()` |  |
| 387 | 1 | `public abstract int getMaturityMonth()` |  |
| 389 | 1 | `public abstract boolean isMatured()` |  |
| 391 | 1 | `public abstract String getType()` |  |

### what a subclass declares, in its own currency (lines 393-409)

| line | len | member | says |
|---:|---:|---|---|
| 396 | 1 | `protected abstract double principalOwed()` | Principal still owed, in the currency the paper is written in. |
| 399 | 1 | `protected abstract double couponOwed()` | This month's coupon, in its own currency. |
| 408 | 1 | `protected abstract double[] scheduleOwed()` | Every payment still owed, in order, starting with next month's, in its own currency. |

### ...and what the city's books see (lines 410-424)

| line | len | member | says |
|---:|---:|---|---|
| 412 | 1 | `public final double getOustandingPrincipal()` |  |
| 415 | 1 | `public final double getMonthlyInterestExpense()` | This month's coupon, in local money. |
| 417 | 7 | `public final double[] remainingCashFlows()` |  |

### and the same figures, in dollars (lines 425-435)

| line | len | member | says |
|---:|---:|---|---|
| 428 | 1 | `public final double principalInCurrency()` | Principal still owed, in the currency written on the paper. |
| 431 | 1 | `public final double faceInCurrency()` | Face value in the currency written on the paper. |
| 434 | 1 | `public final double couponInCurrency()` | This month's coupon in the currency written on the paper. |

### what the next month repays (0.7.13) (lines 436-454)

| line | len | member | says |
|---:|---:|---|---|
| 447 | 4 | `public final double principalDueNextMonthInCurrency()` | The principal the next month's payment repays, in the currency written on the paper: the first payment of its own schedule less that month's coupon - a note's or a term bond's whole face in its last month, a serial bo... |
| 453 | 1 | `public final double principalDueNextMonth()` | ...and in local money, at the rate the paper is valued at. |

### paying for it (lines 455-681)

| line | len | member | says |
|---:|---:|---|---|
| 468 | 4 | `protected void payPrincipal(Game game, double owed)` | A repayment of principal, routed by the currency it is owed in. |
| 477 | 4 | `protected void payCoupon(Game game, double owed)` | A coupon, likewise - and at home, split by who holds the paper (0.7.1): see Game.payDomesticCoupon(). |
| 483 | 3 | `public int getRemainingMonths()` | Months of payments still to run. |
| 488 | 3 | `public final double getFaceValue()` | What it says on the bond, in local money. |
| 492 | 3 | `public int getDuration()` |  |
| 496 | 3 | `public int getMonthStarted()` |  |
| 529 | 17 | `public double getMarketValue(double annualMarketRate)` | What this paper is worth today, to somebody buying it. |
| 548 | 25 | `static double presentValue(double[] cashFlows, double annualRate)` | PV of a monthly schedule at an annual nominal rate. |
| 592 | 30 | `public double getYieldToMaturity(double price)` | The yield a buyer earns at a given price - the bond's true cost to the city, as opposed to the coupon printed on it. |
| 624 | 3 | `public double getCurrentYield(double annualMarketRate)` | Yield at what the market would actually pay today. |
| 635 | 5 | `public double getPriceAsPercentOfPar(double annualMarketRate)` | Where this bond trades against par, as a percentage of face. |
| 650 | 24 | `public void redenominate(double scale)` | The instrument in the new unit. |
| 679 | 1 | `protected void redenominateSchedule(double scale)` | Anything a subclass carries in its own currency - a coupon, an amortisation schedule - in the new unit. |

