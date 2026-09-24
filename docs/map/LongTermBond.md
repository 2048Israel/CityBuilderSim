# LongTermBond.java - 133 lines · 12 methods · 2 constants · model

`ham/citybuildersim/LongTermBond.java` - generated 2026-09-24 by CodeMap; line numbers are as of that run.

> A term loan: a coupon every month on the whole face, and the whole face at
> the end - issued only at the five maturities in MATURITIES (0.7.1).

**Uses:** [Debt](Debt.md) (1), [Game](Game.md) (1)

**Used by (6):** [BankCheck](BankCheck.md), [CreditCheck](CreditCheck.md), [DebtManager](DebtManager.md), [Game](Game.md), [ReadPathCheck](ReadPathCheck.md), [RestructureCheck](RestructureCheck.md)

## Sections

| line | section |
|---:|---|
| 11 | FIVE MATURITIES (0.7.1) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 24 | `LongTermBond.MATURITIES` | `{ 10, 20, 30, 40, 50 }` | The only terms a term loan is issued at, in years: five benchmark maturities, so the curve is five points a player can read. |
| 27 | `LongTermBond.REFUSAL` | `"Term loans are issued at 10, 20, 30, 40 or 50 years."` | What the treasury is told when it asks for any other term. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 35 | `private double monthlyCouponRate` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 9 | 125 | **type** `public class LongTermBond extends Debt` | A term loan: a coupon every month on the whole face, and the whole face at the end - issued only at the five maturities in MATURITIES (0.7.1). |

### FIVE MATURITIES (0.7.1) (lines 11-133)

| line | len | member | says |
|---:|---:|---|---|
| 30 | 4 | `public static boolean isIssuable(int years)` | True for one of the five. |
| 37 | 3 | `public LongTermBond(double faceValue, int months, int monthStarted, double couponRate)` |  |
| 42 | 11 | `public LongTermBond(double faceValue, int months, int monthStarted, double couponRate, boolean foreign)` |  |
| 66 | 9 | `public void processMonth(Game game)` | Long bonds are deliberately a combination instrument: a LOW monthly coupon plus a redemption premium (face value exceeds the cash received). |
| 77 | 3 | `public double getIssuePrice()` |  |
| 82 | 3 | `protected double principalOwed()` |  |
| 87 | 3 | `public int getMaturityMonth()` |  |
| 92 | 3 | `public boolean isMatured()` |  |
| 97 | 3 | `public String getType()` |  |
| 102 | 3 | `protected double couponOwed()` |  |
| 106 | 3 | `public double getCouponRate()` |  |
| 121 | 12 | `protected double[] scheduleOwed()` | Coupon every month, and the whole face at the end. |

