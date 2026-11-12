# Debt.java - 1,438 lines · 116 methods · 7 constants · model

`ham/citybuildersim/Debt.java` - generated 2026-11-12 by CodeMap; line numbers are as of that run.

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

**Used by (46):** [Auction](Auction.md), [AuctionCheck](AuctionCheck.md), [BankCheck](BankCheck.md), [BookDesk](BookDesk.md), [Buyback](Buyback.md), [BuybackCheck](BuybackCheck.md), [CallCheck](CallCheck.md), [CentralBankCheck](CentralBankCheck.md), [ChartCheck](ChartCheck.md), [CityNeeds](CityNeeds.md), [CreditCheck](CreditCheck.md), [CurrencyCheck](CurrencyCheck.md), [CurveCheck](CurveCheck.md), [DataSave](DataSave.md), [DebtExchangeCheck](DebtExchangeCheck.md), [DebtManager](DebtManager.md), [DebtMoneysCheck](DebtMoneysCheck.md), [DeskCheck](DeskCheck.md), [FinancesScreen](FinancesScreen.md), [FloatLinkCheck](FloatLinkCheck.md), [FloatingRateNote](FloatingRateNote.md), [ForeignDebtCheck](ForeignDebtCheck.md), [FundCheck](FundCheck.md), [Game](Game.md), [GdpCheck](GdpCheck.md), [GovernmentScreen](GovernmentScreen.md), [GovernmentView](GovernmentView.md), [GovernmentViewCheck](GovernmentViewCheck.md), [HoldersCheck](HoldersCheck.md), [InflationLinkedBond](InflationLinkedBond.md), [KeptBench](KeptBench.md), [KeptCheck](KeptCheck.md), [LongPlaytest](LongPlaytest.md), [LongTermBond](LongTermBond.md), [MediumTermBond](MediumTermBond.md), [MoneyCheck](MoneyCheck.md), [NewGameCheck](NewGameCheck.md), [ReadPathCheck](ReadPathCheck.md), [RestructureCheck](RestructureCheck.md), [SaveFileCheck](SaveFileCheck.md), [SectorBooks](SectorBooks.md), [SectorScreen](SectorScreen.md), [SectorStatementCheck](SectorStatementCheck.md), [SectorStatements](SectorStatements.md), [ShortTermTBill](ShortTermTBill.md), [TreasuryCheck](TreasuryCheck.md)

## Sections

| line | section |
|---:|---|
| 37 | WHICH MONEY THIS PAPER IS WRITTEN IN |
| 108 | WHO HOLDS IT (0.7.1) |
| 338 | · THE ALLOTMENT (CD2, spec-city-debt.md section 4) |
| 597 | THE CITY'S BOOKS (CD3, spec-city-debt.md section 5) |
| 797 | WHAT A PIECE REMEMBERS (CD6, spec-city-debt.md section 7) |
| 1030 | · what a subclass declares, in its own currency |
| 1057 | · ...and what the city's books see |
| 1086 | · and the same figures, in dollars |
| 1097 | · what the next month repays (0.7.13) |
| 1116 | · paying for it |

## Enum constants

| line | constant | says |
|---:|---|---|
| 170 | `Debt.Holder.BANK` |  |
| 170 | `Debt.Holder.HOUSEHOLDS` |  |
| 170 | `Debt.Holder.CENTRAL_BANK` |  |
| 170 | `Debt.Holder.FIRMS` |  |
| 170 | `Debt.Holder.WORLD` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 500 | `Debt.HOLDING_ROUNDING` | `1e-12` | The share of the face moved under which what a holder is left with is the subtraction's rounding, and none (RT1). |
| 649 | `Debt.EFFECTIVE_RATE_FLOOR` | `- 0.5` | The lowest effective rate effectiveRateOf() will report: a premium so deep it costs the issuer less than nothing, a year. |
| 652 | `Debt.EFFECTIVE_RATE_CEILING` | `5.0` | The highest, as getYieldToMaturity()'s bracket always was: 500% a year. |
| 819 | `Debt.AUCTION` | `"auction"` | How a sale at auction reads on the piece. |
| 822 | `Debt.CENTRAL_BANK_AT_PAR` | `"the central bank, at its par"` | ...the central bank's own par, rolled into a line or a new piece at issue. |
| 825 | `Debt.CENTRAL_BANK_ADD_ON` | `"the central bank's add-on"` | ...and its non-competitive add-on to a sale. |
| 828 | `Debt.ROLLOVER` | `"the rollover"` | ...and the treasury's rollover of what fell due. |

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
| 180 | `protected double bankDiscount, householdDiscount, centralBankDiscount, firmDiscount, worldDiscount` | THE UNACCRETED DISCOUNT, EACH HOLDER'S OWN (BK2, 2026-11-08): what each holder has not yet earned of the discount on what it holds, set from the price it paid, and carried with the face when the face changes hands (pa... |
| 181 | `protected double unsettledDiscount` |  |
| 352 | `protected double allotHouseholdsFace, allotHouseholdsCash` | The face the sale still to be settled gave the households, and the cash they pay for it at the settle. |
| 355 | `protected double allotFirmsFace, allotFirmsCash` | ...the businesses' (CD2, slice 4: they pay it from their tills at the settle, Game.firmsTakeTheirShare()). |
| 358 | `protected double allotWorldFace, allotWorldCash` | ...and the world's (CD2, slice 5: it pays it from abroad at the settle, Game.worldTakesItsShare()). |
| 367 | `protected double unsettledFace` | The face the market has bought since the last settle and not yet paid for: the whole piece for a new one, the reopening's own face for a line reopened. |
| 655 | `protected double carryingValue` | What the piece is carried at, in the currency it is written in: what it raised, stepped by the effective interest less the payments. |
| 658 | `protected double effectiveRate` | The yield, a year, compounded monthly, at which the payments still owed are worth carryingValue: the piece's effective rate. |
| 661 | `protected boolean booksOpen` | True once openBooks() has run: what a sold piece's books are opened with, and what a piece built without a sale (a fixture, the founding's land bond) is given at par by its first month. |
| 664 | `protected double lastExpenseOwn, lastCouponOwn` | The month's interest expense and coupon as amortise() last booked them, in the currency the piece is written in. |
| 836 | `private int month` |  |
| 837 | `private double face, proceeds, yield` |  |
| 838 | `private String how` |  |
| 840 | `private double rate` | CD7: local money per unit of the piece's own money at the sale (the piece's exchangeRate then); 0 for a sale from a save that did not keep it - unknown. |
| 865 | `private java.util.List<Sale> sales` | The sales into this piece, oldest first; null until the first is noted. |
| 868 | `private int pathFirstMonth` | The month of the first entry of the price path, and the price per 100 of what is owed and the carrying value per 100, one entry a month from it; null until the first month is recorded. |
| 869 | `private double[] pricePath, carriedPath` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 28 | 1411 | **type** `public abstract class Debt` | One piece of city paper. |

### WHICH MONEY THIS PAPER IS WRITTEN IN (lines 37-107)

| line | len | member | says |
|---:|---:|---|---|
| 81 | 1 | `public boolean isForeign()` |  |
| 84 | 1 | `public String getMoney()` | The code of the money this paper is written in: "USD" or another of the eight for foreign paper, null for paper at home (CD8). |
| 87 | 1 | `void setMoney(String code)` | Told to it by DebtManager when it books the piece, before anything reads it: a code other than "USD" is kept, "USD" and null are stored as null. |
| 90 | 1 | `public double getExchangeRate()` | The rate this paper is currently being valued at: local money per unit of its own money. |
| 99 | 3 | `void setExchangeRate(double rate)` | Told to it by DebtManager, every month and on the load path. |
| 104 | 3 | `protected double inLocal(double own)` | The paper's own money into local money, for foreign paper; the identity for domestic. |

### WHO HOLDS IT (0.7.1) (lines 108-337)

| line | len | member | says |
|---:|---:|---|---|
| 170 | 1 | **type** `public enum Holder` | Who holds a piece of this paper, in the order the discount and the principal are kept by (BK2). |
| 183 | 1 | `public double householdPrincipal()` |  |
| 184 | 1 | `public double centralBankPrincipal()` |  |
| 185 | 1 | `public double firmPrincipal()` |  |
| 186 | 1 | `public double worldPrincipal()` |  |
| 189 | 5 | `public double bankPrincipal()` | What the commercial bank holds: the rest of the principal after the households, the central bank, the businesses and the world. |
| 195 | 1 | `public double getSettleDue()` |  |
| 196 | 1 | `public double getIssueYield()` |  |
| 197 | 1 | `public double getIssueDiscount()` |  |
| 200 | 4 | `public double getDiscountLeft()` | What is left to accrete on the line, all holders': each one's own and the unsettled sales' (if they are a discount). |
| 206 | 10 | `public double discountOf(Holder h)` | The unaccreted discount this holder has on the line (BK2). |
| 218 | 1 | `public double unsettledDiscount()` | The unsettled sales' face less what they raised, signed: a premium reads below nothing. |
| 221 | 10 | `double heldBy(Holder h)` | The face this holder holds, for the discount's pro rata: the bank's is what it has paid for (bankPrincipalPaid()). |
| 233 | 3 | `void markIssued(double received, double yield)` | Told at issue what it raised and at what yield. |
| 238 | 11 | `void markIssued(double received, double yield, String how)` | ...and said how it was sold (CD6): the sale the piece records reads "auction", or what the rollover and the central bank are. |
| 276 | 3 | `void reopen(double face, double proceeds, double yield)` | A SALE INTO A LINE THAT IS ALREADY ON THE BOOKS (CD2, spec-city-debt.md section 2.2): this much more face of the same paper, at its coupon and its date, for `proceeds`. |
| 286 | 16 | `void reopen(double face, double proceeds, double yield, int month, String how)` | ...in the month the caller knows, sold the way it says (CD6): the sale the piece remembers (getSales()). |
| 310 | 3 | `void reopenForCentralBank(double face, double price, double yield)` | The central bank's own par, rolled into a line that is already on the books (CD2): reopen() for this much face at this price, but paid for at once by the central bank (Game, THE CENTRAL BANK ROLLS ITS OWN, AT ISSUE) -... |
| 315 | 10 | `void reopenForCentralBank(double face, double price, double yield, int month)` | ...in the month the caller knows, recorded as the central bank's own sale (CD6). |
| 327 | 10 | `void settled()` | The settle has taken place: nothing is owed for this paper any more, and the allotment is paid. |

### THE ALLOTMENT (CD2, spec-city-debt.md section 4) (lines 338-596)

| line | len | member | says |
|---:|---:|---|---|
| 369 | 1 | `public double allotHouseholdsFace()` |  |
| 370 | 1 | `public double allotHouseholdsCash()` |  |
| 371 | 1 | `public double allotFirmsFace()` |  |
| 372 | 1 | `public double allotFirmsCash()` |  |
| 373 | 1 | `public double allotWorldFace()` |  |
| 374 | 1 | `public double allotWorldCash()` |  |
| 375 | 1 | `public double unsettledFace()` |  |
| 386 | 10 | `void allot(Auction.Fill fills, double sold, double cash)` | Adds a sale's allotment: each bidder's face from the auction's fills, and the cash it pays - its share of what the sale raised, since the buyers pay together what the treasury was paid (settleDue), the fee and the res... |
| 404 | 3 | `public double bankPrincipalPaid()` | What the commercial bank holds of this paper that it has PAID for: its principal less the face sold since the last settle and not yet paid for. |
| 412 | 3 | `public double unaccretedOnPaid()` | The discount the bank has not yet earned on what it has paid for: its own (BK2), which the unsettled sale's is not in until the settle gives the bank what is left of it (settled()). |
| 420 | 1 | `double moveToHouseholds(double face)` | Moves face between the holders: positive to the households, the central bank, the businesses or the world from the bank, negative back to it - and the seller's discount per face with it (pass()). |
| 421 | 1 | `double moveToCentralBank(double face)` |  |
| 422 | 1 | `double moveToFirms(double face)` |  |
| 423 | 1 | `double moveToWorld(double face)` |  |
| 433 | 12 | `double pass(Holder from, Holder to, double face)` | FACE CHANGES HANDS (BK2): `face` of the line goes from one holder to another with the SELLER's own discount per face on it, and that discount is returned. |
| 452 | 6 | `void takeAtSettle(Holder h, double face, double cash)` | A SALE'S SHARE TAKEN AT THE SETTLE (BK2): this holder (never the bank, which is the residual) takes `face` of the unsettled sale for `cash`. |
| 463 | 4 | `void repay(Holder h, double face)` | Principal repaid off a holder (BK2): its face falls by `face` and its own discount stays - every holder's face falls in proportion, so their discount per face moves alike, as the pool's did. |
| 468 | 9 | `private double principalField(Holder h)` |  |
| 478 | 9 | `private void setPrincipal(Holder h, double value)` |  |
| 488 | 10 | `private void setDiscount(Holder h, double value)` |  |
| 512 | 4 | `static double moved(double held, double face)` | What a holder holds after a move of face: nothing below nothing, as it always was, and nothing for a remainder within HOLDING_ROUNDING of the face moved. |
| 536 | 3 | `void addOnForCentralBank(double face)` | THE CENTRAL BANK'S ADD-ON (0.7.15, round 2): this much more face of the same paper, on the same terms, for the central bank, which holds it from issue - its non-competitive bid, which the Federal Reserve Bank of New Y... |
| 541 | 18 | `void addOnForCentralBank(double face, int month)` | ...in the month the caller knows (CD6): recorded as the add-on it is, at the price of the sale it rides on and the piece's issue yield. |
| 566 | 9 | `public double unaccretedOn(Holder h, double face)` | The unaccreted discount that rides on this much of a holder's face - the part it gives up, or takes on, when that face changes hands (pass()): its own discount pro rata to the face it holds. |
| 582 | 10 | `double accrete()` | This month's accretion: what is left of each holder's own discount over the months left, taken off what is left. |
| 593 | 3 | `private double accretionOf(double own)` |  |

### THE CITY'S BOOKS (CD3, spec-city-debt.md section 5) (lines 597-796)

| line | len | member | says |
|---:|---:|---|---|
| 677 | 13 | `static double effectiveRateOf(double[] flows, double price)` | The yield, a year compounded monthly, at which these payments are worth this price: the bisection getYieldToMaturity() always made, on a bracket that reaches below nothing so that a piece sold well over par (a coupon ... |
| 699 | 6 | `void openBooks(double proceedsOwn)` | The books are opened with what the sale raised, in the currency the piece is written in (the treasury's cash, net of the fixed cost): that is the carrying value, and the effective rate is solved on the payments the pi... |
| 707 | 3 | `private void ensureBooks()` | A piece that was never sold through the treasury (a fixture's, the founding's land bond) is carried at par before anything is added to it or booked on it: its effective rate is its coupon's. |
| 720 | 4 | `protected final void reestimateRate()` | A FLOATING PIECE'S RATE RESETS WITH ITS COUPON (CD5, spec-city-debt.md section 5.3): the effective rate is solved again on the schedule as it now stands, at the carrying value as it now stands. |
| 735 | 8 | `protected final double reestimateCarrying()` | AN INDEXED PIECE'S BOOKS CATCH UP (CD5, IFRS 9 B5.4.6): the carrying value becomes the present value of the schedule as it now stands at the effective rate it has (unchanged), and the change is returned - the month's ... |
| 757 | 21 | `double amortise()` | This month's books, once, BEFORE processMonth() pays the month (the payment is read off the schedule as it stands, so the books follow the cash by construction): the interest expense at the effective rate on the carry... |
| 780 | 1 | `public final double carryingInCurrency()` | What the piece is carried at, in the currency written on it; par for a piece whose books were never opened. |
| 783 | 1 | `public final double getCarryingValue()` | ...and in local money, at the rate the paper is valued at (inLocal()). |
| 786 | 1 | `public final double getEffectiveRate()` | The effective rate, a year: the yield the piece's sale made of its payments. |
| 789 | 1 | `public final double getLastInterestExpense()` | This month's interest expense as amortise() booked it, in local money: the carrying value at the effective rate, a month. |
| 792 | 1 | `public final double getLastCoupon()` | ...the coupon that month paid, likewise. |
| 795 | 1 | `public final double getLastAmortised()` | ...and the part of the expense that is not coupon: the discount wearing off, below nothing for a premium. |

### WHAT A PIECE REMEMBERS (CD6, spec-city-debt.md section 7) (lines 797-1029)

| line | len | member | says |
|---:|---:|---|---|
| 835 | 28 | **type** `public static final class Sale` | ONE SALE INTO A PIECE, as it was made: the month, the face sold and what it raised (in the currency the piece is written in), the yield it cleared at and how it was sold. |
| 842 | 8 | `Sale(int month, double face, double proceeds, double yield, String how, double rate)` _(in Debt.Sale)_ |  |
| 851 | 1 | `public int getMonth()` _(in Debt.Sale)_ |  |
| 852 | 1 | `public double getFace()` _(in Debt.Sale)_ |  |
| 853 | 1 | `public double getProceeds()` _(in Debt.Sale)_ |  |
| 854 | 1 | `public double getYield()` _(in Debt.Sale)_ |  |
| 855 | 1 | `public String getHow()` _(in Debt.Sale)_ |  |
| 858 | 1 | `public double getRate()` _(in Debt.Sale)_ | The rate the sale was made at (CD7): local money per unit of the money the piece is written in, as the piece was valued then; 0 when a save did not keep it. |
| 861 | 1 | `public double getPricePer100()` _(in Debt.Sale)_ | The price the sale made, for each 100 of face (CD6): what it raised over its face, times 100; 0 for a sale of no face. |
| 871 | 1 | `private static double finiteOrZero(double v)` |  |
| 879 | 5 | `void noteSale(int month, double face, double proceeds, double yield, String how)` | Remembers a sale, and the rate the piece was valued at when it was made (CD7: a dollar piece's exchangeRate then, which DebtManager.book() had already told it). |
| 893 | 11 | `public final double soldAtRate()` | THE RATE THE PIECE WAS SOLD AT (CD7): the mean of its sales' rates, each weighted by what the sale raised, over the sales that kept a rate and raised something. |
| 913 | 4 | `public final double exchangeSinceSold()` | WHAT THE EXCHANGE HAS DONE TO WHAT IS STILL CARRIED, since the sale (CD7), in local money, a gain positive: the carrying value in the piece's own money times the sale's rate less the rate now. |
| 925 | 3 | `public java.util.List<Sale> getSales()` | The sales into this piece, oldest first (a reopened line is one line with several): read-only. |
| 937 | 27 | `void recordPath(int month, double pricePer100, double carriedPer100)` | Appends this month's entry to the price path: where the piece trades and what it is carried at, each per 100 of what is owed. |
| 966 | 1 | `public int getPathFirstMonth()` | The month the price path starts at: the first month recorded. |
| 969 | 1 | `public double[] getPricePath()` | Where the piece traded against par at each month's close since the first recorded, per 100 of what was owed: a copy, empty before the first. |
| 972 | 1 | `public double[] getCarriedPath()` | ...and what it was carried at, per 100 of what was owed, month for month with getPricePath(). |
| 981 | 1 | **type** `public record BooksMonth(int ahead, double opening, double interest, double coupon, double amortised, doubl...` | ONE MONTH OF THE BOOKS TO COME, in local money: what the piece opens the month carried at, the interest at the effective rate, the coupon, the discount that wears off (the interest less the coupon; below nothing for a... |
| 995 | 24 | `public java.util.List<BooksMonth> booksAhead(int months)` | THE NEXT MONTHS ON THE BOOKS: amortise() projected on the schedule as it stands (scheduleOwed(), couponScheduleOwed()) at the effective rate as it stands, exactly as amortise() strikes a month - the interest on the ca... |
| 1020 | 1 | `public abstract void processMonth(Game game)` |  |
| 1022 | 1 | `public abstract double getIssuePrice()` |  |
| 1024 | 1 | `public abstract int getMaturityMonth()` |  |
| 1026 | 1 | `public abstract boolean isMatured()` |  |
| 1028 | 1 | `public abstract String getType()` |  |

### what a subclass declares, in its own currency (lines 1030-1056)

| line | len | member | says |
|---:|---:|---|---|
| 1033 | 1 | `protected abstract double principalOwed()` | Principal still owed, in the currency the paper is written in. |
| 1036 | 1 | `protected abstract double couponOwed()` | This month's coupon, in its own currency. |
| 1045 | 1 | `protected abstract double[] scheduleOwed()` | Every payment still owed, in order, starting with next month's, in its own currency. |
| 1055 | 1 | `protected abstract double[] couponScheduleOwed()` | The coupon part of every payment still owed, in order, starting with next month's, in its own currency: scheduleOwed() less the principal each payment repays, so the first is couponOwed() to the bit. |

### ...and what the city's books see (lines 1057-1085)

| line | len | member | says |
|---:|---:|---|---|
| 1059 | 1 | `public final double getOustandingPrincipal()` |  |
| 1062 | 1 | `public final double getMonthlyInterestExpense()` | This month's coupon, in local money. |
| 1070 | 1 | `protected double upliftOwed()` | The part of couponOwed() that is a linked bond's inflation uplift, in the piece's own currency: nothing for every other piece. |
| 1073 | 1 | `public final double getMonthlyCoupon()` | This month's coupon in local money, without the uplift a linked bond pays with its last: the coupon proper. |
| 1076 | 1 | `public final double getUpliftDue()` | The inflation uplift the coupon path pays this month, in local money: a linked bond's last month, nothing in any other piece or month. |
| 1078 | 7 | `public final double[] remainingCashFlows()` |  |

### and the same figures, in dollars (lines 1086-1096)

| line | len | member | says |
|---:|---:|---|---|
| 1089 | 1 | `public final double principalInCurrency()` | Principal still owed, in the currency written on the paper. |
| 1092 | 1 | `public final double faceInCurrency()` | Face value in the currency written on the paper. |
| 1095 | 1 | `public final double couponInCurrency()` | This month's coupon in the currency written on the paper. |

### what the next month repays (0.7.13) (lines 1097-1115)

| line | len | member | says |
|---:|---:|---|---|
| 1108 | 4 | `public final double principalDueNextMonthInCurrency()` | The principal the next month's payment repays, in the currency written on the paper: the first payment of its own schedule less that month's coupon - a note's or a term bond's whole face in its last month, a serial bo... |
| 1114 | 1 | `public final double principalDueNextMonth()` | ...and in local money, at the rate the paper is valued at. |

### paying for it (lines 1116-1438)

| line | len | member | says |
|---:|---:|---|---|
| 1129 | 4 | `protected void payPrincipal(Game game, double owed)` | A repayment of principal, routed by the currency it is owed in. |
| 1138 | 4 | `protected void payCoupon(Game game, double owed)` | A coupon, likewise - and at home, split by who holds the paper (0.7.1): see Game.payDomesticCoupon(). |
| 1144 | 3 | `public int getRemainingMonths()` | Months of payments still to run. |
| 1149 | 3 | `public final double getFaceValue()` | What it says on the bond, in local money. |
| 1153 | 3 | `public int getDuration()` |  |
| 1157 | 3 | `public int getMonthStarted()` |  |
| 1190 | 17 | `public double getMarketValue(double annualMarketRate)` | What this paper is worth today, to somebody buying it. |
| 1209 | 25 | `static double presentValue(double[] cashFlows, double annualRate)` | PV of a monthly schedule at an annual nominal rate. |
| 1254 | 34 | `public double getYieldToMaturity(double price)` | The yield a buyer earns at a given price - the bond's true cost to the city, as opposed to the coupon printed on it. |
| 1290 | 3 | `public double getCurrentYield(double annualMarketRate)` | Yield at what the market would actually pay today. |
| 1301 | 5 | `public double getPriceAsPercentOfPar(double annualMarketRate)` | Where this bond trades against par, as a percentage of face. |
| 1316 | 45 | `public void redenominate(double scale)` | The instrument in the new unit. |
| 1366 | 1 | `protected void redenominateSchedule(double scale)` | Anything a subclass carries in its own currency - a coupon, an amortisation schedule - in the new unit. |
| 1396 | 31 | `void retirePart(double bankFace, double householdFace, double firmFace, double worldFace)` | PART OF THE PIECE BOUGHT BACK (CD4, spec-city-debt.md section 6.2): this much face taken off it, by holder. |
| 1436 | 1 | `protected void shrinkSchedule(double k)` | What a subclass carries in its own currency that scales with the piece when part of it is bought back (retirePart()): a serial bond's slice. |

