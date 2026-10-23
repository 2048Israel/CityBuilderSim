# LongTermBond.java - 136 lines · 13 methods · 2 constants · model

`ham/citybuildersim/LongTermBond.java` - generated 2026-10-23 by CodeMap; line numbers are as of that run.

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

**Uses:** [Debt](Debt.md) (1), [Game](Game.md) (1)

**Used by (13):** [AuctionCheck](AuctionCheck.md), [BankCheck](BankCheck.md), [CentralBankCheck](CentralBankCheck.md), [CreditCheck](CreditCheck.md), [DebtManager](DebtManager.md), [FinancesScreen](FinancesScreen.md), [FundCheck](FundCheck.md), [Game](Game.md), [LongPlaytest](LongPlaytest.md), [NewGameCheck](NewGameCheck.md), [ReadPathCheck](ReadPathCheck.md), [RestructureCheck](RestructureCheck.md), [TreasuryCheck](TreasuryCheck.md)

## Sections

| line | section |
|---:|---|
| 21 | SIX MATURITIES (CD2; five from 0.7.1) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 32 | `LongTermBond.MATURITIES` | `{ 2, 5, 10, 20, 30, 50 }` | The only terms a bond is issued at, in years: six benchmark maturities, so each trades as a line. |
| 35 | `LongTermBond.REFUSAL` | `"Bonds are issued at 2, 5, 10, 20, 30 or 50 years."` | What the treasury is told when it asks for any other term. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 43 | `private double monthlyCouponRate` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 19 | 118 | **type** `public class LongTermBond extends Debt` | A bond: a fixed coupon every month on the whole face, and the face at the end - at 2, 5, 10, 20, 30 or 50 years, sold in lines that reopen (CD2). |

### SIX MATURITIES (CD2; five from 0.7.1) (lines 21-136)

| line | len | member | says |
|---:|---:|---|---|
| 38 | 4 | `public static boolean isIssuable(int years)` | True for one of the six. |
| 45 | 3 | `public LongTermBond(double faceValue, int months, int monthStarted, double couponRate)` |  |
| 50 | 11 | `public LongTermBond(double faceValue, int months, int monthStarted, double couponRate, boolean foreign)` |  |
| 64 | 9 | `public void processMonth(Game game)` | A coupon on the whole face, every month; the face in the last. |
| 75 | 3 | `public double getIssuePrice()` |  |
| 80 | 3 | `protected double principalOwed()` |  |
| 85 | 3 | `public int getMaturityMonth()` |  |
| 90 | 3 | `public boolean isMatured()` |  |
| 95 | 3 | `public String getType()` |  |
| 100 | 3 | `protected double couponOwed()` |  |
| 104 | 3 | `public double getCouponRate()` |  |
| 109 | 3 | `public double couponRate()` | The coupon, a year, as the auction set it at issue (Auction.COUPON_STEP): the same figure getCouponRate() gives. |
| 124 | 12 | `protected double[] scheduleOwed()` | Coupon every month, and the whole face at the end. |

