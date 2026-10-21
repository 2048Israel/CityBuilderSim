# MediumTermBond.java - 226 lines · 16 methods · 1 constants · model

`ham/citybuildersim/MediumTermBond.java` - generated 2026-10-21 by CodeMap; line numbers are as of that run.

> The amortizing bond: the workhorse of municipal finance - at 5 to 30 years in
> fives, one coupon, each sale its own line (CD2). It was the serial bond; the
> class keeps its name (spec-city-debt.md, star 3).
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
> ONE COUPON, THE STOP (CD2). The coupon used to be the curve's rate for the
> final maturity, charged on every slice - 7 to 24 basis points too dear on a
> rising curve. The auction sets it now: the clearing yield itself, which
> prices the whole schedule at 100 (Auction.amortizingPer100()). An amortizing
> line is never reopened, because its slices have already started to fall.
> 
> NOT A MORTGAGE. Level debt service - equal total payments, like a house - is
> the other common municipal structure and deliberately not this one: it hides
> how much principal is left behind a flat number, and Jerus asked for the
> mechanics to stay legible.

**Uses:** [Debt](Debt.md) (1), [Auction](Auction.md) (1), [Game](Game.md) (1)

**Used by (7):** [AuctionCheck](AuctionCheck.md), [BankCheck](BankCheck.md), [CentralBankCheck](CentralBankCheck.md), [DebtManager](DebtManager.md), [FinancesScreen](FinancesScreen.md), [Game](Game.md), [RestructureCheck](RestructureCheck.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 45 | `MediumTermBond.YEARS` | `Auction.AMORTIZING_YEARS` | The terms an amortizing bond is sold at, in years (D3): Auction.AMORTIZING_YEARS. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 54 | `private double monthlyCouponRate` |  |
| 62 | `private double principalPerSlice` | Principal repaid on each anniversary. |
| 65 | `private int slicesRemaining` | Anniversaries left to pay, including the one at maturity. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 42 | 185 | **type** `public class MediumTermBond extends Debt` | The amortizing bond: the workhorse of municipal finance - at 5 to 30 years in fives, one coupon, each sale its own line (CD2). |
| 48 | 5 | `public static int nearestYears(int years)` | The one of YEARS nearest this many years; the shorter on a tie. |
| 57 | 3 | `public double couponRate()` | The one coupon, a year, that the auction set at issue (the stop itself): the screen's name for the bond, "20-year amortizing bond at 4.21%". |
| 67 | 3 | `public MediumTermBond(double faceValue, int months, int monthStarted, double couponRate)` |  |
| 72 | 16 | `public MediumTermBond(double faceValue, int months, int monthStarted, double couponRate, boolean foreign)` |  |
| 105 | 7 | `void repairAfterLoad()` | Rebuilds the amortisation schedule after a load. |
| 121 | 20 | `public void processMonth(Game game)` | Coupon every month; principal on each anniversary. |
| 143 | 3 | `public double getIssuePrice()` |  |
| 148 | 3 | `protected double principalOwed()` |  |
| 153 | 3 | `public int getMaturityMonth()` |  |
| 158 | 3 | `public boolean isMatured()` |  |
| 163 | 3 | `public String getType()` |  |
| 169 | 3 | `protected double couponOwed()` | On what is still out, not on the original face. |
| 174 | 3 | `public double getPrincipalPerSlice()` | How much principal is retired on each anniversary. |
| 178 | 3 | `public int getSlicesRemaining()` |  |
| 191 | 28 | `protected double[] scheduleOwed()` | Coupon on the declining balance, with a principal slice each anniversary. |
| 222 | 3 | `protected void redenominateSchedule(double scale)` | The amortisation slice, in the new unit. |

