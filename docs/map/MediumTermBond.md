# MediumTermBond.java - 203 lines · 14 methods · 0 constants · model

`ham/citybuildersim/MediumTermBond.java` - generated 2026-09-26 by CodeMap; line numbers are as of that run.

> A serial bond: the workhorse of municipal finance.
> 
> A single issue is really a stack of maturities. A city raising $10M over ten
> years does not sell one bond due in 2036; it sells a slice due each year, and
> pays each slice off as it comes. Principal falls the whole way, so the coupon
> falls with it, and there is no balloon at the end because there is no lump
> left to balloon.
> 
> WHAT THIS REPLACES, AND WHY IT MATTERS MORE THAN IT SOUNDS
> 
> This was a bullet: coupon on the full face for the whole term, then the entire
> face out of the treasury in one month. Two things follow from amortising
> instead, and the second is the one worth having:
> 
>   1. The city's debt actually goes DOWN as it pays. Outstanding principal is
>      what the rate curve prices against (getPricedDebt), so a serial bond
>      gently improves the city's credit over its life, where a bullet held the
>      rate up until the day it vanished.
>   2. The coupon falls every year. A serial bond starts dearer than a bullet of
>      the same size and ends far cheaper, which is exactly the real trade and
>      is now a real decision against the term bond.
> 
> ANNUAL SLICES, MONTHLY COUPON, which is how these are actually structured -
> principal on an anniversary, interest every period on whatever is still out.
> 
> NOT A MORTGAGE. Level debt service - equal total payments, like a house - is
> the other common municipal structure and deliberately not this one: it hides
> how much principal is left behind a flat number, and Jerus asked for the
> mechanics to stay legible.

**Uses:** [Debt](Debt.md) (1), [Game](Game.md) (1)

**Used by (4):** [BankCheck](BankCheck.md), [DebtManager](DebtManager.md), [Game](Game.md), [RestructureCheck](RestructureCheck.md)

## Fields (state)

| line | field | says |
|---:|---|---|
| 36 | `private double monthlyCouponRate` |  |
| 39 | `private double principalPerSlice` | Principal repaid on each anniversary. |
| 42 | `private int slicesRemaining` | Anniversaries left to pay, including the one at maturity. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 34 | 170 | **type** `public class MediumTermBond extends Debt` | A serial bond: the workhorse of municipal finance. |
| 44 | 3 | `public MediumTermBond(double faceValue, int months, int monthStarted, double couponRate)` |  |
| 49 | 16 | `public MediumTermBond(double faceValue, int months, int monthStarted, double couponRate, boolean foreign)` |  |
| 82 | 7 | `void repairAfterLoad()` | Rebuilds the amortisation schedule after a load. |
| 98 | 20 | `public void processMonth(Game game)` | Coupon every month; principal on each anniversary. |
| 120 | 3 | `public double getIssuePrice()` |  |
| 125 | 3 | `protected double principalOwed()` |  |
| 130 | 3 | `public int getMaturityMonth()` |  |
| 135 | 3 | `public boolean isMatured()` |  |
| 140 | 3 | `public String getType()` |  |
| 146 | 3 | `protected double couponOwed()` | On what is still out, not on the original face. |
| 151 | 3 | `public double getPrincipalPerSlice()` | How much principal is retired on each anniversary. |
| 155 | 3 | `public int getSlicesRemaining()` |  |
| 168 | 28 | `protected double[] scheduleOwed()` | Coupon on the declining balance, with a principal slice each anniversary. |
| 199 | 3 | `protected void redenominateSchedule(double scale)` | The amortisation slice, in the new unit. |

