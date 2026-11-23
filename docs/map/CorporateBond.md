# CorporateBond.java - 254 lines · 30 methods · 2 constants · model

`ham/citybuildersim/CorporateBond.java` - generated 2026-11-23 by CodeMap; line numbers are as of that run.

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
> and the world - and since 0.7.14 a fifth, the city's fund (TreasuryFund),
> which buys on the book and never at issue. HT1 adds a sixth, passive
> unclaimed-estate custody, separate from that fund. They always add up to the face
> ... (6 more lines in the source)

**Uses:** [Mortgage](Mortgage.md) (1), [SaveCopy](SaveCopy.md) (1)

**Used by (20):** [BankFailureCheck](BankFailureCheck.md), [BankScreen](BankScreen.md), [BondCheck](BondCheck.md), [BondMarket](BondMarket.md), [CapitalFlowCheck](CapitalFlowCheck.md), [CapitalGainsCheck](CapitalGainsCheck.md), [EstateCustody](EstateCustody.md), [FinancesScreen](FinancesScreen.md), [FundCheck](FundCheck.md), [FundLedger](FundLedger.md), [FundLedgerCheck](FundLedgerCheck.md), [FundScreen](FundScreen.md), [FundView](FundView.md), [Game](Game.md), [KeptBench](KeptBench.md), [KeptCheck](KeptCheck.md), [KeptTimingCheck](KeptTimingCheck.md), [LongPlaytest](LongPlaytest.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md)

## Sections

| line | section |
|---:|---|
| 175 | its value |
| 218 | a default, and a reform |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 77 | `CorporateBond.TERM_MONTHS` | `Mortgage.MORTGAGE_TERM_MONTHS` | The term every bond is issued at: ten years, the benchmark tenor of investment-grade corporate issuance, and the term the model already prices at (the insured mortgage's, Mortgage.MORTGAGE_TERM_MONTHS). |
| 80 | `CorporateBond.COUPONS_A_YEAR` | `12` | Coupons a year: one a month, the model's month; real bonds pay twice a year. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 82 | `int id` |  |
| 83 | `String issuer` |  |
| 85 | `double issued` | The face it was issued at. |
| 87 | `double face` | The face outstanding: the issue less what defaults wrote off. |
| 89 | `double coupon` | The annual coupon, fixed at issue. |
| 90 | `int issueMonth` |  |
| 91 | `int maturityMonth` |  |
| 94 | `double households` | ---- who holds it, in face ---- |
| 95 | `double bank` |  |
| 97 | `double bankCost` | What the bank paid for what it holds - its carrying value, written down with the face. |
| 98 | `double world` |  |
| 100 | `double city` | The city's fund's face (0.7.14): bought on the book by its rule or the player's hand, never at issue. |
| 102 | `double custody` | Passive unclaimed-estate owner, separate from the fund. |
| 104 | `Map<String, Double> companies` |  |
| 107 | `double writtenOff` | Face written off by defaults over its life. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 74 | 181 | **type** `public class CorporateBond implements Cloneable` | A corporate bond: a sector's debt to investors, issued at par through bookbuilding, paying a fixed coupon every month and its whole face at the end. |
| 103 | 1 | `public double custody()` |  |
| 109 | 1 | `CorporateBond()` |  |
| 117 | 9 | `CorporateBond copy()` | A copy that shares nothing with this bond (UP1, 2026-10-22): the autosave's, written while the months go on paying, trading in and writing down the live one. |
| 127 | 9 | `CorporateBond(int id, String issuer, double face, double coupon, int issueMonth, int termMonths)` |  |
| 137 | 1 | `public int id()` |  |
| 138 | 1 | `public String issuer()` |  |
| 139 | 1 | `public double issued()` |  |
| 140 | 1 | `public double face()` |  |
| 141 | 1 | `public double coupon()` |  |
| 142 | 1 | `public int issueMonth()` |  |
| 143 | 1 | `public int maturityMonth()` |  |
| 144 | 1 | `public double households()` |  |
| 145 | 1 | `public double bank()` |  |
| 146 | 1 | `public double bankCost()` |  |
| 147 | 1 | `public double world()` |  |
| 149 | 1 | `public double city()` | The city's fund's face (0.7.14). |
| 150 | 1 | `public double writtenOff()` |  |
| 151 | 1 | `public double company(String sector)` |  |
| 152 | 1 | `public Map<String, Double> companies()` |  |
| 154 | 5 | `public double companiesTotal()` | What every company holds together. |
| 161 | 1 | `public String instrument()` | The name its order book trades under. |
| 164 | 1 | `public int remainingMonths(int month)` | Months to maturity at this month, never below nothing. |
| 167 | 1 | `public double monthlyCoupon()` | The coupon due this month on the face outstanding: a twelfth of the annual coupon. |
| 170 | 1 | `public boolean isMatured(int month)` | True once its maturity month has come. |
| 173 | 1 | `public double held()` | What the holder classes hold, added up - the city's fund among them since 0.7.14: the face outstanding, to the dust. |

### its value (lines 175-217)

| line | len | member | says |
|---:|---:|---|---|
| 187 | 8 | `public static double priceAtYield(double coupon, int months, double yield)` | WHAT A UNIT OF FACE IS WORTH at an annual yield: the coupons left and the face, each discounted monthly at yield / 12 - |
| 202 | 10 | `public static double yieldAtPrice(double coupon, int months, double price)` | ...and the yield a price implies: the one annual yield at which priceAtYield() gives it, found by bisection - the price falls as the yield rises, so there is exactly one - between -50% and 1,000% a year. |
| 214 | 3 | `public double priceAt(double yield, int month)` | What a unit of face of this bond is worth at this yield, this month. |

### a default, and a reform (lines 218-254)

| line | len | member | says |
|---:|---:|---|---|
| 226 | 14 | `double writeDown(double scale)` | Writes the bond down to this share of its face: every holder's face by the same share, and the bank's cost with its face. |
| 242 | 12 | `void redenominate(double scale)` | Every money figure in the new unit; the coupon, the months and the ratios do not move. |

