# ShortTermTBill.java - 121 lines · 14 methods · 1 constants · model

`ham/citybuildersim/ShortTermTBill.java` - generated 2026-10-26 by CodeMap; line numbers are as of that run.

> A bill: borrow now, repay one lump, no coupon - at 1, 3, 6 or 12 months,
> each sale its own line (CD2).
> 
> The municipal instrument this models is a tax or bond anticipation note - cash
> to bridge a gap until the taxes arrive or the long-term financing is arranged.
> That is exactly how it is used here, and Jerus's own framing of it is the same
> one a treasurer would use: an emergency-room visit, not a way to live.
> 
> A DISCOUNT INSTRUMENT. There is no interest payment. The lender hands over
> less than the face value and collects the face at maturity, and the gap
> between the two IS the interest. `getMonthlyInterestExpense()` therefore
> returns 0 and that is not an oversight - the COUPON figure on Finances (the
> NEXT DUE card's totals until 0.7.32, the debt bar's until 0.7.24) says as
> much in words, because a bare zero under a heading called INTEREST reads
> as free money.
> 
> WHY THE PRICE IS NOT HERE ANY MORE. The discount used to be a simple one,
> face = request / (1 - rate x months/12), struck by this class and by Game,
> while getMarketValue() compounds monthly - so the issue and the buyback
> priced the same paper two ways, and the gap between them was a small loss
> on a round trip by luck of the direction. A bill is sold at auction now
> (Auction): one clearing yield, the price the face discounted at that yield
> monthly, the same sum DebtManager.marketValue() reads back, so the round
> trip is a loss of the fee and the tail by construction. The quote carries
> the price (Game.quoteTBill()); this class holds the lump.

**Uses:** [Debt](Debt.md) (1), [Auction](Auction.md) (1), [Game](Game.md) (1)

**Used by (11):** [AuctionCheck](AuctionCheck.md), [BankCheck](BankCheck.md), [CentralBankCheck](CentralBankCheck.md), [DebtManager](DebtManager.md), [DeskCheck](DeskCheck.md), [FinancesScreen](FinancesScreen.md), [FloatLinkCheck](FloatLinkCheck.md), [Game](Game.md), [LongPlaytest](LongPlaytest.md), [RestructureCheck](RestructureCheck.md), [TreasuryCheck](TreasuryCheck.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 48 | `ShortTermTBill.TERMS` | `Auction.BILL_MONTHS` | The terms a bill is sold at, in months (D3): Auction.BILL_MONTHS. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 30 | 92 | **type** `public class ShortTermTBill extends Debt` | A bill: borrow now, repay one lump, no coupon - at 1, 3, 6 or 12 months, each sale its own line (CD2). |
| 32 | 3 | `public ShortTermTBill(double faceValue, int months, int monthStarted)` |  |
| 37 | 9 | `public ShortTermTBill(double faceValue, int months, int monthStarted, boolean foreign)` |  |
| 51 | 4 | `public static boolean isIssuable(int months)` | True for one of the terms a bill is sold at. |
| 57 | 5 | `public static int nearestTerm(int months)` | The one of TERMS nearest this many months; the shorter on a tie. |
| 64 | 6 | `public void processMonth(Game game)` |  |
| 72 | 3 | `public double getIssuePrice()` |  |
| 77 | 3 | `public int getMonths()` | getters |
| 82 | 3 | `protected double principalOwed()` |  |
| 87 | 3 | `public int getMaturityMonth()` |  |
| 91 | 3 | `public boolean isMatured()` |  |
| 96 | 3 | `public String getType()` |  |
| 101 | 3 | `protected double couponOwed()` |  |
| 107 | 8 | `protected double[] scheduleOwed()` | Nothing until maturity, then the whole face. |
| 118 | 3 | `protected double[] couponScheduleOwed()` | A bill pays no coupon: its cost is its discount. |

