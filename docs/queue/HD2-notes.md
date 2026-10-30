# HD2 - the coupon abroad as profit, kept abroad ({next}, 2026-10-30)

HD1-C's way 1, on Jerus's word (2026-10-28: "interest abroad as profit sure";
2026-10-29, of the three ways: "Kept abroad (Recommended)"). Built on WN3's
branch (main 0.8.74, EM1, in), the money lane.

## 1. What it is

HD1's coupon is back (`git revert 40cb44c5`, clean on WN3's tip): the coupon on
a business's dollars abroad (the dollars held x the world's 2% over twelve, at
the month's rate) is struck with the sectors' statements
(`OutwardInvestment.payCoupons()`, before `strikeSectors()`), a line of finance
income below the operating line ("Interest abroad"), inside pre-tax income, so
the business tax and `Equity.PAYOUT` see it.

What changed from HD1 is the cash. `payCoupons()` grows the dollars held by the
coupon, as main's `takeMonth()` rolled it, and `Sector.bank()` puts none of it
in the till: the till pays the tax and the dividend struck on it, and dollars
come home only when the till is short, through the `recall()` calls already in
`Game.payDividends()` and `EconomyManager.settleBusinessCredit()` (no new
recall code). MoneyAudit declares the income credit and the reinvested debit
both ("+ sectors ForeignInterest", "- sectors ForeignInterestReinvested"): a
foreign holding's reinvested earnings, income in and the same out as
investment.

The books follow. `SectorMonth.unexplained()` takes the kept coupon out of what
net income puts in the till; the cash flow statement has a line for it,
"Interest abroad, kept there", in operating (`CASH_KEPT_ABROAD`, minus the
coupon, shown only in a month that has one), and free cash flow takes it out;
`SectorStatements.abroadRevalued()` is main's formula again (the dollars grow
by the coupon, so the revaluation leaves it out). The screens' text says so
(the waterfall, note 6, the equity statement's abroad line, a note for the new
cash-flow line, the Trade screen's holdings).

## 2. The calls (★; each the recommended way, BP1)

- ★ **The share register keeps EM1's single count.** EM1 values a share as its
  spare money at face plus the business on its trailing income, and its
  `Equity.fairValue()` says why that is no double count: net income leaves out
  what the money earns. With the coupon inside net income, feeding the register
  net income would count the dollars abroad twice (at face, and their coupon
  capitalised at the required return), most in the cash-box companies EM1 was
  for. So Game records `SectorMonth.businessNetIncome()` (net income without the
  coupon, after the tax it carried, exactly: the profit tax is
  max(pre-tax x rate, 0) with no other term) and `moneyIncome()` carries
  `foreignInterestAfterTax()` where it carried the coupon before tax. The
  dividend still reads net income (PAYOUT sees the coupon, as the brief says).
  Equity's and Exchange's code untouched; two javadoc sentences say it.
  Recommended and taken: the alternative (the register on net income, the
  coupon out of `moneyIncome`) gets the expected return right and the fair value
  wrong. For EM2: the register's income is the business's, not the statement's.
- ★ **CapitalFlowCheck section 9's twins are topped up first.** Section 9 as
  HD1 left it already failed on this base, on the dollars and the till: since
  EM1 a company over its target equity has the exchange post a buyback
  (`Exchange.postCompanies()`), and a till short of it calls dollars home
  (`cashAvailable` -> `recall()`), so the twin with dollars bought back 481.68k
  of its shares and the twin without could not. Both tills get 1,000k and play
  a month before the dollars are set (a sector's opening cash is last month's
  close, so cash put in between would be NOT ACCOUNTED FOR), and "both twins
  bought back the same shares" is a fixture line. No tolerance moved: the till
  identities hold at 1e-9.
- ★ **ScaleCheck's fixture month.** {scale}
- **MonetaryCheck holds; nothing queued for it.** {monetary-call}

## 3. What moved

**MonetaryCheck, "inflation falls with the rate"** (a founding city of 219
people, months 25-60, on the goods' literals since WN3):

| policy | 3% | 10% | 20% | 40% |
|---|---|---|---|---|
| inflation, 0.8.73 (HD1-C's main) | 10.43% | 9.68% | 8.99% | 8.82% |
| inflation, 0.8.73 with the coupon home (HD1 as built) | 7.92% | 6.25% | 6.76% | 8.17% |
| inflation, WN3's tip (this base) | 10.75% | 9.71% | 9.04% | 8.73% |
| inflation, the coupon home on this base | 10.75% | 9.71% | 9.04% | 8.72% |
| **inflation, HD2** | **10.75%** | **9.71%** | **9.04%** | **8.72%** |
| D$ per US$, WN3's tip | 1.2599 | 1.0168 | 0.8318 | 0.7804 |
| D$ per US$, HD2 | 1.2599 | 1.0168 | 0.8318 | 0.7804 |

The dial keeps its sign, and on this base it would have even paid home: EM1's
market pays a cash box's money out to its owners, so the young city's
businesses hold next to nothing abroad and the coupon on it moves the
currency by less than the table's last digit.

**HD1-C's hoard table**, the default playtest, one run each (dollars abroad and
the tills in a year's GDP; D$ per US$ and the price index at m2,852):

| | abroad m1200 | m2400 | m2852 | m4000 | tills m2852 | D$ m2852 | index m2852 |
|---|---|---|---|---|---|---|---|
| 0.8.73 (HD1-C's main) | 2.05 | 8.95 | 15.22 | 35.43 | 3.86 | 232 | 361 |
| 0.8.73, the coupon home | 0.85 | 6.15 | 10.59 | 17.95 | 3.32 | 143 | 227 |
| WN3's tip (this base) | 0.01 | 0.01 | 0.01 | 0.01 | 0.41 | 218 | 333 |
| the coupon home on this base | 0.00 | 0.01 | 0.01 | 0.01 | 0.40 | 215 | 310 |
| **HD2** | **0.00** | **0.01** | **0.01** | **0.01** | **0.39** | **96** | **143** |

The hoard HD1 found is gone on this base: EM1's buyback finds sellers and the
cash reaches the owners, so the businesses keep 0.4 of a year's GDP in their
tills and a hundredth abroad (HD2 at m600: 0.24 abroad, the base 0.01, the
coupon home 0.18; it is spent by m1,200 in all three). The three runs agree to
the person until about m100 and part there, with $34 abroad (a coupon of six
cents a month): a founding town amplifies any difference into another city, so
one run's D$ at m2,852 (96 against 218) is not a finding; the ensemble's is.

{ensemble}

## 4. The checks

{checks}

## 5. How it was done

One slice on Sonnet 5.5 at xhigh (the kept cash, the books, the register,
CapitalFlowCheck section 9), reviewed as a stranger's; Opus on the brief, the
review, the measurements (the playtest, MonetaryCheck and ScaleCheck on three
snapshots: WN3's tip, HD1 as built on it, HD2), ScaleCheck's rescan (a scratch
copy whose fixture month is a property and which skips sections 1-5, 15 s a
month, and a trace of section 6's town by month) and the ensemble. Not a
process batch: no script, check machinery or rule a session reads changed.
