# LongTermBond.java - 107 lines · 11 methods · 0 constants · model

`ham/citybuildersim/LongTermBond.java` - generated 2026-09-18 by CodeMap; line numbers are as of that run.

> (no class header - the file explains itself in its section banners)

**Uses:** [Debt](Debt.md) (1), [Game](Game.md) (1)

**Used by (3):** [DebtManager](DebtManager.md), [Game](Game.md), [RestructureCheck](RestructureCheck.md)

## Fields (state)

| line | field | says |
|---:|---|---|
| 9 | `private double monthlyCouponRate` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 7 | 101 | **type** `public class LongTermBond extends Debt` |  |
| 11 | 3 | `public LongTermBond(double faceValue, int months, int monthStarted, double couponRate)` |  |
| 16 | 11 | `public LongTermBond(double faceValue, int months, int monthStarted, double couponRate, boolean foreign)` |  |
| 40 | 9 | `public void processMonth(Game game)` | Long bonds are deliberately a combination instrument: a LOW monthly coupon plus a redemption premium (face value exceeds the cash received). |
| 51 | 3 | `public double getIssuePrice()` |  |
| 56 | 3 | `protected double principalOwed()` |  |
| 61 | 3 | `public int getMaturityMonth()` |  |
| 66 | 3 | `public boolean isMatured()` |  |
| 71 | 3 | `public String getType()` |  |
| 76 | 3 | `protected double couponOwed()` |  |
| 80 | 3 | `public double getCouponRate()` |  |
| 95 | 12 | `protected double[] scheduleOwed()` | Coupon every month, and the whole face at the end. |

