# LongTermBond.java - 208 lines · 21 methods · 2 constants · model

`ham/citybuildersim/LongTermBond.java` - generated 2026-10-29 by CodeMap; line numbers are as of that run.

> A bond: a fixed coupon every month on the whole face, and the face at the
> end - at 2, 5, 10, 20, 30 or 50 years, sold in lines that reopen (CD2).
> 
> WHY: until CD2 this was a deep-discount "term loan" - a coupon of about a
> third of the market rate, the face grossed up until its payments were worth
> the cash - which no treasury sells and which showed the player a coupon
> that was not the price. A bond is sold at auction now (Auction): its coupon
> is the clearing yield rounded down to an eighth of a point, set at issue
> and never changed, so a new line sells just under 100, and a sale at the
> same term while a line has nine tenths of it left goes into that line
> (Debt.reopen(), DebtManager.benchmarkLine()). The class keeps its name
> (spec-city-debt.md, star 3): it is any bond of 2 to 50 years.
> 
> CALLABLE (CD4): a bond of ten years or more may be sold with the city's
> right to call the whole line at 100 from a first call date (callable(),
> isCallableNow()); the coupon carries the right (CallTree prices it) and the
> flag and the date ride through the save with the piece.

**Uses:** [Debt](Debt.md) (1), [Game](Game.md) (1)

**Used by (18):** [AuctionCheck](AuctionCheck.md), [BankCheck](BankCheck.md), [BookDesk](BookDesk.md), [CallCheck](CallCheck.md), [CallTree](CallTree.md), [CentralBankCheck](CentralBankCheck.md), [CreditCheck](CreditCheck.md), [DebtManager](DebtManager.md), [DeskCheck](DeskCheck.md), [FinancesScreen](FinancesScreen.md), [FloatLinkCheck](FloatLinkCheck.md), [FundCheck](FundCheck.md), [Game](Game.md), [LongPlaytest](LongPlaytest.md), [NewGameCheck](NewGameCheck.md), [ReadPathCheck](ReadPathCheck.md), [RestructureCheck](RestructureCheck.md), [TreasuryCheck](TreasuryCheck.md)

## Sections

| line | section |
|---:|---|
| 26 | SIX MATURITIES (CD2; five from 0.7.1) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 37 | `LongTermBond.MATURITIES` | `{ 2, 5, 10, 20, 30, 50 }` | The only terms a bond is issued at, in years: six benchmark maturities, so each trades as a line. |
| 40 | `LongTermBond.REFUSAL` | `"Bonds are issued at 2, 5, 10, 20, 30 or 50 years."` | What the treasury is told when it asks for any other term. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 48 | `private double monthlyCouponRate` |  |
| 51 | `private boolean callable` | True for a bond sold callable (CD4, D22): the city may call it whole at 100 from firstCallMonth on. |
| 54 | `private int firstCallMonth` | The game month from which a callable bond may be called; -1 when it is not callable. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 24 | 185 | **type** `public class LongTermBond extends Debt` | A bond: a fixed coupon every month on the whole face, and the face at the end - at 2, 5, 10, 20, 30 or 50 years, sold in lines that reopen (CD2). |

### SIX MATURITIES (CD2; five from 0.7.1) (lines 26-208)

| line | len | member | says |
|---:|---:|---|---|
| 43 | 4 | `public static boolean isIssuable(int years)` | True for one of the six. |
| 56 | 3 | `public LongTermBond(double faceValue, int months, int monthStarted, double couponRate)` |  |
| 61 | 11 | `public LongTermBond(double faceValue, int months, int monthStarted, double couponRate, boolean foreign)` |  |
| 79 | 6 | `public static LongTermBond callable(double faceValue, int months, int monthStarted, double couponRate, int firstCallMonth)` | A bond sold callable (CD4, spec-city-debt.md section 2.6): the same bond, with the right to call it whole at 100 from the game month firstCallMonth on. |
| 87 | 3 | `public boolean isCallable()` | True for a bond sold callable. |
| 92 | 3 | `public int getFirstCallMonth()` | The game month from which a callable bond may be called; -1 for a bond that is not callable. |
| 97 | 3 | `public boolean isCallableNow(int month)` | True when this bond may be called in this game month: callable, and the month is its first call month or later. |
| 108 | 4 | `public int monthsToFirstCall()` | Months from now to the first call, read off the paper's own schedule - the months its call date sits after its start, less the months it has run (its term less what is left) - so a valuation needs no clock; 0 once it ... |
| 114 | 3 | `public int callTermMonths()` | Months from the day it was sold to its first call date, as the sale set them: what two lines must share to be one line (DebtManager.benchmarkLine()); -1 for a bond that is not callable. |
| 119 | 3 | `void moveFirstCallForTest(int month)` | A fixture's: moves the first call month, so the call can be exercised without playing years to reach it. |
| 125 | 9 | `public void processMonth(Game game)` | A coupon on the whole face, every month; the face in the last. |
| 136 | 3 | `public double getIssuePrice()` |  |
| 141 | 3 | `protected double principalOwed()` |  |
| 146 | 3 | `public int getMaturityMonth()` |  |
| 151 | 3 | `public boolean isMatured()` |  |
| 156 | 3 | `public String getType()` |  |
| 161 | 3 | `protected double couponOwed()` |  |
| 165 | 3 | `public double getCouponRate()` |  |
| 170 | 3 | `public double couponRate()` | The coupon, a year, as the auction set it at issue (Auction.COUPON_STEP): the same figure getCouponRate() gives. |
| 185 | 12 | `protected double[] scheduleOwed()` | Coupon every month, and the whole face at the end. |
| 200 | 8 | `protected double[] couponScheduleOwed()` | The same coupon every month, couponOwed(). |

