# CorporateBond.java - 224 lines · 27 methods · 2 constants · model

`ham/citybuildersim/CorporateBond.java` - generated 2026-09-26 by CodeMap; line numbers are as of that run.

> A corporate bond: a sector's debt to investors, issued at par through
> bookbuilding, paying a fixed coupon every month and its whole face at the
> end.
> 
> ==================== WHY THIS EXISTS ====================
> 
> Jerus, 2026-09-24: "notes are the loans businesses make to the banks, and
> bonds are debts that they issue to investors ... bonds will have a slightly
> higher rate than notes, and households can buy them, but banks also buy
> them as, only if it wants, and yes they are tradeable so they have par
> value and all." Until 0.7.12 a business could borrow only from the one
> bank, so a whole industry's debt sat on one balance sheet: with the
> landlords on insured mortgages (0.7.11) the sector that failed the bank
> owed it five to six times its equity (the-landlords-take-a-mortgage.md,
> section 4). A bond is the second place to go.
> 
> WHY "BANK LOANS" AND NOT "NOTES" ON THE SCREENS. Jerus calls a business's
> bank loan a note. The city's own short paper (ShortTermTBill) and the build
> screen's six-month offer (Game.BUILD_NOTE_MONTHS) are already called notes,
> so a screen that said "notes" would name three different things; the
> screens say "bank loans" for the business's borrowing from the bank, and
> "bonds" for this.
> 
> ==================== THE TERMS ====================
> 
>   ISSUED AT PAR by a sector, the coupon set by bookbuilding - the yield
>   that fills the issue (BondMarket, THE BOOK IS BUILT), so the bond prices
>   at exactly its face the day it is sold.
> 
>   TEN YEARS, bullet: TERM_MONTHS. Ten years is the benchmark tenor of
>   investment-grade corporate issuance - the maturity a corporate spread is
>   quoted against ("the spread to the 10-year Treasury") and the brief's own
>   anchor - and it is the term the model already prices at: the insured
>   mortgage's (Mortgage.MORTGAGE_TERM_MONTHS) and the ten-year point of the
>   city's curve. One standard term rather than a choice among 5, 7, 10 and
>   30: a choice by cost would trade the curve's slope against spreading the
>   issuing costs over more years, and every term chosen is one more order
>   book. 0.7.12 shipped the one term; a choice among them was not built.
> 
>   COUPONS MONTHLY, a twelfth of the annual coupon on the face outstanding
>   (COUPONS_A_YEAR). Real bonds pay twice a year; the model's clock is the
>   month, and a coupon paid monthly is the same money sooner by a few
>   weeks. The coupon is an interest expense on the issuer's income
>   statement, like a loan's (BusinessDebtManager.getMonthlyInterest()).
> 
>   THE WHOLE FACE AT MATURITY, paid from the issuer's till; what it cannot
>   pay is a short month like any other - it sells what it holds, then the
>   shortfall desk lends or a new bond raises the rest, the choice again,
>   and what nobody lends defaults that month (BondMarket.redeemMaturing();
>   BusinessDebtManager, CAN'T PAY MEANS DEFAULT, since round 4).
> 
> ==================== WHO HOLDS IT ====================
> 
> Four holder classes, in face: the households (their face together - since
> round 2 each cell holds its own face of the bond, Household.bondFace, and
> this is the cells' sum; round 1 held one pool with a claim per cell), the
> bank (with what it paid, its cost), each company that bought it by sector,
> and the world. The four always add up to the face outstanding; a default
> writes every one of them down by the same share (writeDown()).
> 
> ... (3 more lines in the source)

**Uses:** [Mortgage](Mortgage.md) (1)

**Used by (8):** [BankScreen](BankScreen.md), [BondCheck](BondCheck.md), [BondMarket](BondMarket.md), [FinancesScreen](FinancesScreen.md), [Game](Game.md), [LongPlaytest](LongPlaytest.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md)

## Sections

| line | section |
|---:|---|
| 149 | its value |
| 192 | a default, and a reform |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 74 | `CorporateBond.TERM_MONTHS` | `Mortgage.MORTGAGE_TERM_MONTHS` | The term every bond is issued at: ten years, the benchmark tenor of investment-grade corporate issuance, and the term the model already prices at (the insured mortgage's, Mortgage.MORTGAGE_TERM_MONTHS). |
| 77 | `CorporateBond.COUPONS_A_YEAR` | `12` | Coupons a year: one a month, the model's month; real bonds pay twice a year. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 79 | `int id` |  |
| 80 | `String issuer` |  |
| 82 | `double issued` | The face it was issued at. |
| 84 | `double face` | The face outstanding: the issue less what defaults wrote off. |
| 86 | `double coupon` | The annual coupon, fixed at issue. |
| 87 | `int issueMonth` |  |
| 88 | `int maturityMonth` |  |
| 91 | `double households` | ---- who holds it, in face ---- |
| 92 | `double bank` |  |
| 94 | `double bankCost` | What the bank paid for what it holds - its carrying value, written down with the face. |
| 95 | `double world` |  |
| 96 | `Map<String, Double> companies` |  |
| 99 | `double writtenOff` | Face written off by defaults over its life. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 71 | 154 | **type** `public class CorporateBond` | A corporate bond: a sector's debt to investors, issued at par through bookbuilding, paying a fixed coupon every month and its whole face at the end. |
| 101 | 1 | `CorporateBond()` |  |
| 103 | 9 | `CorporateBond(int id, String issuer, double face, double coupon, int issueMonth, int termMonths)` |  |
| 113 | 1 | `public int id()` |  |
| 114 | 1 | `public String issuer()` |  |
| 115 | 1 | `public double issued()` |  |
| 116 | 1 | `public double face()` |  |
| 117 | 1 | `public double coupon()` |  |
| 118 | 1 | `public int issueMonth()` |  |
| 119 | 1 | `public int maturityMonth()` |  |
| 120 | 1 | `public double households()` |  |
| 121 | 1 | `public double bank()` |  |
| 122 | 1 | `public double bankCost()` |  |
| 123 | 1 | `public double world()` |  |
| 124 | 1 | `public double writtenOff()` |  |
| 125 | 1 | `public double company(String sector)` |  |
| 126 | 1 | `public Map<String, Double> companies()` |  |
| 128 | 5 | `public double companiesTotal()` | What every company holds together. |
| 135 | 1 | `public String instrument()` | The name its order book trades under. |
| 138 | 1 | `public int remainingMonths(int month)` | Months to maturity at this month, never below nothing. |
| 141 | 1 | `public double monthlyCoupon()` | The coupon due this month on the face outstanding: a twelfth of the annual coupon. |
| 144 | 1 | `public boolean isMatured(int month)` | True once its maturity month has come. |
| 147 | 1 | `public double held()` | What the four holder classes hold, added up: the face outstanding, to the dust. |

### its value (lines 149-191)

| line | len | member | says |
|---:|---:|---|---|
| 161 | 8 | `public static double priceAtYield(double coupon, int months, double yield)` | WHAT A UNIT OF FACE IS WORTH at an annual yield: the coupons left and the face, each discounted monthly at yield / 12 - |
| 176 | 10 | `public static double yieldAtPrice(double coupon, int months, double price)` | ...and the yield a price implies: the one annual yield at which priceAtYield() gives it, found by bisection - the price falls as the yield rises, so there is exactly one - between -50% and 1,000% a year. |
| 188 | 3 | `public double priceAt(double yield, int month)` | What a unit of face of this bond is worth at this yield, this month. |

### a default, and a reform (lines 192-224)

| line | len | member | says |
|---:|---:|---|---|
| 200 | 12 | `double writeDown(double scale)` | Writes the bond down to this share of its face: every holder's face by the same share, and the bank's cost with its face. |
| 214 | 10 | `void redenominate(double scale)` | Every money figure in the new unit; the coupon, the months and the ratios do not move. |

