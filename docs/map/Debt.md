# Debt.java - 1,264 lines · 105 methods · 6 constants · model

`ham/citybuildersim/Debt.java` - generated 2026-10-31 by CodeMap; line numbers are as of that run.

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

**Used by (41):** [Auction](Auction.md), [AuctionCheck](AuctionCheck.md), [BankCheck](BankCheck.md), [BookDesk](BookDesk.md), [Buyback](Buyback.md), [BuybackCheck](BuybackCheck.md), [CallCheck](CallCheck.md), [CentralBankCheck](CentralBankCheck.md), [ChartCheck](ChartCheck.md), [CityNeeds](CityNeeds.md), [CreditCheck](CreditCheck.md), [CurveCheck](CurveCheck.md), [DataSave](DataSave.md), [DebtExchangeCheck](DebtExchangeCheck.md), [DebtManager](DebtManager.md), [DebtMoneysCheck](DebtMoneysCheck.md), [DeskCheck](DeskCheck.md), [FinancesScreen](FinancesScreen.md), [FloatLinkCheck](FloatLinkCheck.md), [FloatingRateNote](FloatingRateNote.md), [ForeignDebtCheck](ForeignDebtCheck.md), [FundCheck](FundCheck.md), [Game](Game.md), [GdpCheck](GdpCheck.md), [GovernmentScreen](GovernmentScreen.md), [HoldersCheck](HoldersCheck.md), [InflationLinkedBond](InflationLinkedBond.md), [LongPlaytest](LongPlaytest.md), [LongTermBond](LongTermBond.md), [MediumTermBond](MediumTermBond.md), [MoneyCheck](MoneyCheck.md), [NewGameCheck](NewGameCheck.md), [ReadPathCheck](ReadPathCheck.md), [RestructureCheck](RestructureCheck.md), [SaveFileCheck](SaveFileCheck.md), [SectorBooks](SectorBooks.md), [SectorScreen](SectorScreen.md), [SectorStatementCheck](SectorStatementCheck.md), [SectorStatements](SectorStatements.md), [ShortTermTBill](ShortTermTBill.md), [TreasuryCheck](TreasuryCheck.md)

## Sections

| line | section |
|---:|---|
| 37 | WHICH MONEY THIS PAPER IS WRITTEN IN |
| 108 | WHO HOLDS IT (0.7.1) |
| 283 | · THE ALLOTMENT (CD2, spec-city-debt.md section 4) |
| 440 | THE CITY'S BOOKS (CD3, spec-city-debt.md section 5) |
| 640 | WHAT A PIECE REMEMBERS (CD6, spec-city-debt.md section 7) |
| 873 | · what a subclass declares, in its own currency |
| 900 | · ...and what the city's books see |
| 929 | · and the same figures, in dollars |
| 940 | · what the next month repays (0.7.13) |
| 959 | · paying for it |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 492 | `Debt.EFFECTIVE_RATE_FLOOR` | `- 0.5` | The lowest effective rate effectiveRateOf() will report: a premium so deep it costs the issuer less than nothing, a year. |
| 495 | `Debt.EFFECTIVE_RATE_CEILING` | `5.0` | The highest, as getYieldToMaturity()'s bracket always was: 500% a year. |
| 662 | `Debt.AUCTION` | `"auction"` | How a sale at auction reads on the piece. |
| 665 | `Debt.CENTRAL_BANK_AT_PAR` | `"the central bank, at its par"` | ...the central bank's own par, rolled into a line or a new piece at issue. |
| 668 | `Debt.CENTRAL_BANK_ADD_ON` | `"the central bank's add-on"` | ...and its non-competitive add-on to a sale. |
| 671 | `Debt.ROLLOVER` | `"the rollover"` | ...and the treasury's rollover of what fell due. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 30 | `protected double faceValue` |  |
| 31 | `protected double outstandingPrincipal` |  |
| 32 | `protected int duration` |  |
| 33 | `protected int remainingMonths` |  |
| 34 | `protected int monthStarted` |  |
| 35 | `protected String type` |  |
| 69 | `protected boolean foreign` | True if this paper is written in a foreign money (getMoney()): the US$ or, since CD8, another of the eight. |
| 76 | `protected String money` | The code of the money a foreign piece is written in (CD8: one of WorldMoneys.EIGHT); null for US$ paper and for paper at home. |
| 79 | `protected double exchangeRate` | Local money per unit of the piece's own money (the US$ for a dollar piece), as of the last month tick. |
| 134 | `protected double householdPrincipal` | Principal of this paper the city's households hold, in local money. |
| 137 | `protected double centralBankPrincipal` | ...and the central bank, at face (its open-market holdings). |
| 140 | `protected double firmPrincipal` | ...and the businesses, at face (CD2, slice 4): the sum of every sector's paper on OutwardInvestment. |
| 143 | `protected double worldPrincipal` | ...and the world, at face (CD2, slice 5): what it bought at the auction and holds on CapitalFlows' paper book, outside the bank's room. |
| 152 | `protected double settleDue` | What the buyers still owe for this paper: the cash the treasury received at issue, which the households and the bank hand over at the next settle (Game, THE BANK SETTLES). |
| 155 | `protected double issueYield` | The market rate it was issued at - the curve's rate for its maturity - which the households weigh against the deposit rate at the settle. |
| 167 | `protected double issueDiscount` | FACE LESS WHAT THE CITY RECEIVED FOR IT, at issue (0.7.1), and the part of it not yet accreted. |
| 168 | `protected double discountLeft` |  |
| 297 | `protected double allotHouseholdsFace, allotHouseholdsCash` | The face the sale still to be settled gave the households, and the cash they pay for it at the settle. |
| 300 | `protected double allotFirmsFace, allotFirmsCash` | ...the businesses' (CD2, slice 4: they pay it from their tills at the settle, Game.firmsTakeTheirShare()). |
| 303 | `protected double allotWorldFace, allotWorldCash` | ...and the world's (CD2, slice 5: it pays it from abroad at the settle, Game.worldTakesItsShare()). |
| 312 | `protected double unsettledFace` | The face the market has bought since the last settle and not yet paid for: the whole piece for a new one, the reopening's own face for a line reopened. |
| 498 | `protected double carryingValue` | What the piece is carried at, in the currency it is written in: what it raised, stepped by the effective interest less the payments. |
| 501 | `protected double effectiveRate` | The yield, a year, compounded monthly, at which the payments still owed are worth carryingValue: the piece's effective rate. |
| 504 | `protected boolean booksOpen` | True once openBooks() has run: what a sold piece's books are opened with, and what a piece built without a sale (a fixture, the founding's land bond) is given at par by its first month. |
| 507 | `protected double lastExpenseOwn, lastCouponOwn` | The month's interest expense and coupon as amortise() last booked them, in the currency the piece is written in. |
| 679 | `private int month` |  |
| 680 | `private double face, proceeds, yield` |  |
| 681 | `private String how` |  |
| 683 | `private double rate` | CD7: local money per unit of the piece's own money at the sale (the piece's exchangeRate then); 0 for a sale from a save that did not keep it - unknown. |
| 708 | `private java.util.List<Sale> sales` | The sales into this piece, oldest first; null until the first is noted. |
| 711 | `private int pathFirstMonth` | The month of the first entry of the price path, and the price per 100 of what is owed and the carrying value per 100, one entry a month from it; null until the first month is recorded. |
| 712 | `private double[] pricePath, carriedPath` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 28 | 1237 | **type** `public abstract class Debt` | One piece of city paper. |

### WHICH MONEY THIS PAPER IS WRITTEN IN (lines 37-107)

| line | len | member | says |
|---:|---:|---|---|
| 81 | 1 | `public boolean isForeign()` |  |
| 84 | 1 | `public String getMoney()` | The code of the money this paper is written in: "USD" or another of the eight for foreign paper, null for paper at home (CD8). |
| 87 | 1 | `void setMoney(String code)` | Told to it by DebtManager when it books the piece, before anything reads it: a code other than "USD" is kept, "USD" and null are stored as null. |
| 90 | 1 | `public double getExchangeRate()` | The rate this paper is currently being valued at: local money per unit of its own money. |
| 99 | 3 | `void setExchangeRate(double rate)` | Told to it by DebtManager, every month and on the load path. |
| 104 | 3 | `protected double inLocal(double own)` | The paper's own money into local money, for foreign paper; the identity for domestic. |

### WHO HOLDS IT (0.7.1) (lines 108-282)

| line | len | member | says |
|---:|---:|---|---|
| 170 | 1 | `public double householdPrincipal()` |  |
| 171 | 1 | `public double centralBankPrincipal()` |  |
| 172 | 1 | `public double firmPrincipal()` |  |
| 173 | 1 | `public double worldPrincipal()` |  |
| 176 | 5 | `public double bankPrincipal()` | What the commercial bank holds: the rest of the principal after the households, the central bank, the businesses and the world. |
| 182 | 1 | `public double getSettleDue()` |  |
| 183 | 1 | `public double getIssueYield()` |  |
| 184 | 1 | `public double getIssueDiscount()` |  |
| 185 | 1 | `public double getDiscountLeft()` |  |
| 188 | 3 | `void markIssued(double received, double yield)` | Told at issue what it raised and at what yield. |
| 193 | 10 | `void markIssued(double received, double yield, String how)` | ...and said how it was sold (CD6): the sale the piece records reads "auction", or what the rollover and the central bank are. |
| 226 | 3 | `void reopen(double face, double proceeds, double yield)` | A SALE INTO A LINE THAT IS ALREADY ON THE BOOKS (CD2, spec-city-debt.md section 2.2): this much more face of the same paper, at its coupon and its date, for `proceeds`. |
| 236 | 16 | `void reopen(double face, double proceeds, double yield, int month, String how)` | ...in the month the caller knows, sold the way it says (CD6): the sale the piece remembers (getSales()). |
| 260 | 3 | `void reopenForCentralBank(double face, double price, double yield)` | The central bank's own par, rolled into a line that is already on the books (CD2): reopen() for this much face at this price, but paid for at once by the central bank (Game, THE CENTRAL BANK ROLLS ITS OWN, AT ISSUE) -... |
| 265 | 8 | `void reopenForCentralBank(double face, double price, double yield, int month)` | ...in the month the caller knows, recorded as the central bank's own sale (CD6). |
| 275 | 7 | `void settled()` | The settle has taken place: nothing is owed for this paper any more, and the allotment is paid. |

### THE ALLOTMENT (CD2, spec-city-debt.md section 4) (lines 283-439)

| line | len | member | says |
|---:|---:|---|---|
| 314 | 1 | `public double allotHouseholdsFace()` |  |
| 315 | 1 | `public double allotHouseholdsCash()` |  |
| 316 | 1 | `public double allotFirmsFace()` |  |
| 317 | 1 | `public double allotFirmsCash()` |  |
| 318 | 1 | `public double allotWorldFace()` |  |
| 319 | 1 | `public double allotWorldCash()` |  |
| 320 | 1 | `public double unsettledFace()` |  |
| 331 | 10 | `void allot(Auction.Fill fills, double sold, double cash)` | Adds a sale's allotment: each bidder's face from the auction's fills, and the cash it pays - its share of what the sale raised, since the buyers pay together what the treasury was paid (settleDue), the fee and the res... |
| 349 | 3 | `public double bankPrincipalPaid()` | What the commercial bank holds of this paper that it has PAID for: its principal less the face sold since the last settle and not yet paid for. |
| 359 | 8 | `public double unaccretedOnPaid()` | The discount the bank has not yet earned on what it has paid for: what is left to accrete on the part of the piece that has settled - the whole of it less the unsettled sale's own - pro rata to the face the bank holds... |
| 369 | 1 | `void moveToHouseholds(double face)` | Moves face between the holders: positive to the households, the central bank, the businesses or the world from the bank, negative back to it. |
| 370 | 1 | `void moveToCentralBank(double face)` |  |
| 371 | 1 | `void moveToFirms(double face)` |  |
| 372 | 1 | `void moveToWorld(double face)` |  |
| 393 | 3 | `void addOnForCentralBank(double face)` | THE CENTRAL BANK'S ADD-ON (0.7.15, round 2): this much more face of the same paper, on the same terms, for the central bank, which holds it from issue - its non-competitive bid, which the Federal Reserve Bank of New Y... |
| 398 | 18 | `void addOnForCentralBank(double face, int month)` | ...in the month the caller knows (CD6): recorded as the add-on it is, at the price of the sale it rides on and the piece's issue yield. |
| 421 | 4 | `public double unaccretedOn(double face)` | The unaccreted discount that rides on this much face - the part a holder gives up, or takes on, when that face changes hands. |
| 433 | 6 | `double accrete()` | This month's accretion: what is left of the discount over the months left, taken off what is left. |

### THE CITY'S BOOKS (CD3, spec-city-debt.md section 5) (lines 440-639)

| line | len | member | says |
|---:|---:|---|---|
| 520 | 13 | `static double effectiveRateOf(double[] flows, double price)` | The yield, a year compounded monthly, at which these payments are worth this price: the bisection getYieldToMaturity() always made, on a bracket that reaches below nothing so that a piece sold well over par (a coupon ... |
| 542 | 6 | `void openBooks(double proceedsOwn)` | The books are opened with what the sale raised, in the currency the piece is written in (the treasury's cash, net of the fixed cost): that is the carrying value, and the effective rate is solved on the payments the pi... |
| 550 | 3 | `private void ensureBooks()` | A piece that was never sold through the treasury (a fixture's, the founding's land bond) is carried at par before anything is added to it or booked on it: its effective rate is its coupon's. |
| 563 | 4 | `protected final void reestimateRate()` | A FLOATING PIECE'S RATE RESETS WITH ITS COUPON (CD5, spec-city-debt.md section 5.3): the effective rate is solved again on the schedule as it now stands, at the carrying value as it now stands. |
| 578 | 8 | `protected final double reestimateCarrying()` | AN INDEXED PIECE'S BOOKS CATCH UP (CD5, IFRS 9 B5.4.6): the carrying value becomes the present value of the schedule as it now stands at the effective rate it has (unchanged), and the change is returned - the month's ... |
| 600 | 21 | `double amortise()` | This month's books, once, BEFORE processMonth() pays the month (the payment is read off the schedule as it stands, so the books follow the cash by construction): the interest expense at the effective rate on the carry... |
| 623 | 1 | `public final double carryingInCurrency()` | What the piece is carried at, in the currency written on it; par for a piece whose books were never opened. |
| 626 | 1 | `public final double getCarryingValue()` | ...and in local money, at the rate the paper is valued at (inLocal()). |
| 629 | 1 | `public final double getEffectiveRate()` | The effective rate, a year: the yield the piece's sale made of its payments. |
| 632 | 1 | `public final double getLastInterestExpense()` | This month's interest expense as amortise() booked it, in local money: the carrying value at the effective rate, a month. |
| 635 | 1 | `public final double getLastCoupon()` | ...the coupon that month paid, likewise. |
| 638 | 1 | `public final double getLastAmortised()` | ...and the part of the expense that is not coupon: the discount wearing off, below nothing for a premium. |

### WHAT A PIECE REMEMBERS (CD6, spec-city-debt.md section 7) (lines 640-872)

| line | len | member | says |
|---:|---:|---|---|
| 678 | 28 | **type** `public static final class Sale` | ONE SALE INTO A PIECE, as it was made: the month, the face sold and what it raised (in the currency the piece is written in), the yield it cleared at and how it was sold. |
| 685 | 8 | `Sale(int month, double face, double proceeds, double yield, String how, double rate)` _(in Debt.Sale)_ |  |
| 694 | 1 | `public int getMonth()` _(in Debt.Sale)_ |  |
| 695 | 1 | `public double getFace()` _(in Debt.Sale)_ |  |
| 696 | 1 | `public double getProceeds()` _(in Debt.Sale)_ |  |
| 697 | 1 | `public double getYield()` _(in Debt.Sale)_ |  |
| 698 | 1 | `public String getHow()` _(in Debt.Sale)_ |  |
| 701 | 1 | `public double getRate()` _(in Debt.Sale)_ | The rate the sale was made at (CD7): local money per unit of the money the piece is written in, as the piece was valued then; 0 when a save did not keep it. |
| 704 | 1 | `public double getPricePer100()` _(in Debt.Sale)_ | The price the sale made, for each 100 of face (CD6): what it raised over its face, times 100; 0 for a sale of no face. |
| 714 | 1 | `private static double finiteOrZero(double v)` |  |
| 722 | 5 | `void noteSale(int month, double face, double proceeds, double yield, String how)` | Remembers a sale, and the rate the piece was valued at when it was made (CD7: a dollar piece's exchangeRate then, which DebtManager.book() had already told it). |
| 736 | 11 | `public final double soldAtRate()` | THE RATE THE PIECE WAS SOLD AT (CD7): the mean of its sales' rates, each weighted by what the sale raised, over the sales that kept a rate and raised something. |
| 756 | 4 | `public final double exchangeSinceSold()` | WHAT THE EXCHANGE HAS DONE TO WHAT IS STILL CARRIED, since the sale (CD7), in local money, a gain positive: the carrying value in the piece's own money times the sale's rate less the rate now. |
| 768 | 3 | `public java.util.List<Sale> getSales()` | The sales into this piece, oldest first (a reopened line is one line with several): read-only. |
| 780 | 27 | `void recordPath(int month, double pricePer100, double carriedPer100)` | Appends this month's entry to the price path: where the piece trades and what it is carried at, each per 100 of what is owed. |
| 809 | 1 | `public int getPathFirstMonth()` | The month the price path starts at: the first month recorded. |
| 812 | 1 | `public double[] getPricePath()` | Where the piece traded against par at each month's close since the first recorded, per 100 of what was owed: a copy, empty before the first. |
| 815 | 1 | `public double[] getCarriedPath()` | ...and what it was carried at, per 100 of what was owed, month for month with getPricePath(). |
| 824 | 1 | **type** `public record BooksMonth(int ahead, double opening, double interest, double coupon, double amortised, doubl...` | ONE MONTH OF THE BOOKS TO COME, in local money: what the piece opens the month carried at, the interest at the effective rate, the coupon, the discount that wears off (the interest less the coupon; below nothing for a... |
| 838 | 24 | `public java.util.List<BooksMonth> booksAhead(int months)` | THE NEXT MONTHS ON THE BOOKS: amortise() projected on the schedule as it stands (scheduleOwed(), couponScheduleOwed()) at the effective rate as it stands, exactly as amortise() strikes a month - the interest on the ca... |
| 863 | 1 | `public abstract void processMonth(Game game)` |  |
| 865 | 1 | `public abstract double getIssuePrice()` |  |
| 867 | 1 | `public abstract int getMaturityMonth()` |  |
| 869 | 1 | `public abstract boolean isMatured()` |  |
| 871 | 1 | `public abstract String getType()` |  |

### what a subclass declares, in its own currency (lines 873-899)

| line | len | member | says |
|---:|---:|---|---|
| 876 | 1 | `protected abstract double principalOwed()` | Principal still owed, in the currency the paper is written in. |
| 879 | 1 | `protected abstract double couponOwed()` | This month's coupon, in its own currency. |
| 888 | 1 | `protected abstract double[] scheduleOwed()` | Every payment still owed, in order, starting with next month's, in its own currency. |
| 898 | 1 | `protected abstract double[] couponScheduleOwed()` | The coupon part of every payment still owed, in order, starting with next month's, in its own currency: scheduleOwed() less the principal each payment repays, so the first is couponOwed() to the bit. |

### ...and what the city's books see (lines 900-928)

| line | len | member | says |
|---:|---:|---|---|
| 902 | 1 | `public final double getOustandingPrincipal()` |  |
| 905 | 1 | `public final double getMonthlyInterestExpense()` | This month's coupon, in local money. |
| 913 | 1 | `protected double upliftOwed()` | The part of couponOwed() that is a linked bond's inflation uplift, in the piece's own currency: nothing for every other piece. |
| 916 | 1 | `public final double getMonthlyCoupon()` | This month's coupon in local money, without the uplift a linked bond pays with its last: the coupon proper. |
| 919 | 1 | `public final double getUpliftDue()` | The inflation uplift the coupon path pays this month, in local money: a linked bond's last month, nothing in any other piece or month. |
| 921 | 7 | `public final double[] remainingCashFlows()` |  |

### and the same figures, in dollars (lines 929-939)

| line | len | member | says |
|---:|---:|---|---|
| 932 | 1 | `public final double principalInCurrency()` | Principal still owed, in the currency written on the paper. |
| 935 | 1 | `public final double faceInCurrency()` | Face value in the currency written on the paper. |
| 938 | 1 | `public final double couponInCurrency()` | This month's coupon in the currency written on the paper. |

### what the next month repays (0.7.13) (lines 940-958)

| line | len | member | says |
|---:|---:|---|---|
| 951 | 4 | `public final double principalDueNextMonthInCurrency()` | The principal the next month's payment repays, in the currency written on the paper: the first payment of its own schedule less that month's coupon - a note's or a term bond's whole face in its last month, a serial bo... |
| 957 | 1 | `public final double principalDueNextMonth()` | ...and in local money, at the rate the paper is valued at. |

### paying for it (lines 959-1264)

| line | len | member | says |
|---:|---:|---|---|
| 972 | 4 | `protected void payPrincipal(Game game, double owed)` | A repayment of principal, routed by the currency it is owed in. |
| 981 | 4 | `protected void payCoupon(Game game, double owed)` | A coupon, likewise - and at home, split by who holds the paper (0.7.1): see Game.payDomesticCoupon(). |
| 987 | 3 | `public int getRemainingMonths()` | Months of payments still to run. |
| 992 | 3 | `public final double getFaceValue()` | What it says on the bond, in local money. |
| 996 | 3 | `public int getDuration()` |  |
| 1000 | 3 | `public int getMonthStarted()` |  |
| 1033 | 17 | `public double getMarketValue(double annualMarketRate)` | What this paper is worth today, to somebody buying it. |
| 1052 | 25 | `static double presentValue(double[] cashFlows, double annualRate)` | PV of a monthly schedule at an annual nominal rate. |
| 1096 | 30 | `public double getYieldToMaturity(double price)` | The yield a buyer earns at a given price - the bond's true cost to the city, as opposed to the coupon printed on it. |
| 1128 | 3 | `public double getCurrentYield(double annualMarketRate)` | Yield at what the market would actually pay today. |
| 1139 | 5 | `public double getPriceAsPercentOfPar(double annualMarketRate)` | Where this bond trades against par, as a percentage of face. |
| 1154 | 40 | `public void redenominate(double scale)` | The instrument in the new unit. |
| 1199 | 1 | `protected void redenominateSchedule(double scale)` | Anything a subclass carries in its own currency - a coupon, an amortisation schedule - in the new unit. |
| 1228 | 25 | `void retirePart(double bankFace, double householdFace, double firmFace, double worldFace)` | PART OF THE PIECE BOUGHT BACK (CD4, spec-city-debt.md section 6.2): this much face taken off it, by holder. |
| 1262 | 1 | `protected void shrinkSchedule(double k)` | What a subclass carries in its own currency that scales with the piece when part of it is bought back (retirePart()): a serial bond's slice. |

