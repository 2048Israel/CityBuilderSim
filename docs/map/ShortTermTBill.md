# ShortTermTBill.java - 125 lines · 13 methods · 0 constants · model

`ham/citybuildersim/ShortTermTBill.java` - generated 2026-09-24 by CodeMap; line numbers are as of that run.

> A short-term anticipation note: borrow now, repay one lump, no coupon.
> 
> The municipal instrument this models is a tax or bond anticipation note - cash
> to bridge a gap until the taxes arrive or the long-term financing is arranged.
> That is exactly how it is used here, and Jerus's own framing of it is the same
> one a treasurer would use: an emergency-room visit, not a way to live.
> 
> A DISCOUNT INSTRUMENT. There is no interest payment. The lender hands over
> less than the face value and collects the face at maturity, and the gap
> between the two IS the interest. `getMonthlyInterestExpense()` therefore
> returns 0 and that is not an oversight - the debt bar says as much in words,
> because a bare zero under a heading called INTEREST reads as free money.
> 
> THE DISCOUNT NOW KNOWS HOW LONG THE NOTE RUNS
> 
> It did not. The face was `request / (1 - rate)` with the FULL annual rate
> applied whatever the term, so a three-month note and a two-year note were
> discounted identically. A city borrowing for one quarter was charged a year of
> interest, and a city borrowing for two years was charged one.
> 
> Priced on the term now, on the bank-discount convention real bills use:
> 
>     face = request / (1 - rate x months/12)
> 
> That is a SIMPLE discount, while getMarketValue() compounds monthly, so the
> two differ slightly - which is authentic rather than sloppy: a bill's quoted
> discount rate and its true yield genuinely are different numbers, and that
> gap is why bond desks quote bond-equivalent yield separately. Worth being
> precise about the direction, because it is load-bearing: the compound price
> comes out slightly ABOVE the simple-discount proceeds, so buying a note
> straight back costs a little more than it raised. A small loss, never a gain,
> which is the only safe direction for a round trip to fail in.

**Uses:** [Debt](Debt.md) (1), [Game](Game.md) (1)

**Used by (7):** [BankCheck](BankCheck.md), [CentralBankCheck](CentralBankCheck.md), [DebtManager](DebtManager.md), [Game](Game.md), [LongPlaytest](LongPlaytest.md), [RestructureCheck](RestructureCheck.md), [TreasuryCheck](TreasuryCheck.md)

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 37 | 89 | **type** `public class ShortTermTBill extends Debt` | A short-term anticipation note: borrow now, repay one lump, no coupon. |
| 39 | 3 | `public ShortTermTBill(double faceValue, int months, int monthStarted)` |  |
| 44 | 9 | `public ShortTermTBill(double faceValue, int months, int monthStarted, boolean foreign)` |  |
| 63 | 4 | `public static double discountFraction(double annualRate, int months)` | The discount for a note of this term, as a fraction of face. |
| 69 | 3 | `public static double faceFor(double cashWanted, double annualRate, int months)` | Face value needed to raise a given sum for a given term. |
| 74 | 6 | `public void processMonth(Game game)` |  |
| 82 | 3 | `public double getIssuePrice()` |  |
| 87 | 3 | `public int getMonths()` | getters |
| 92 | 3 | `protected double principalOwed()` |  |
| 97 | 3 | `public int getMaturityMonth()` |  |
| 101 | 3 | `public boolean isMatured()` |  |
| 106 | 3 | `public String getType()` |  |
| 111 | 3 | `protected double couponOwed()` |  |
| 117 | 8 | `protected double[] scheduleOwed()` | Nothing until maturity, then the whole face. |

