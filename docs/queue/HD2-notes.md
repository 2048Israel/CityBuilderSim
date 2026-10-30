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
- ★ **ScaleCheck's section 6 is red: its town no longer grows into a city,
  and no month holds.** Section 6 copies "a growing city", the town
  `OrderSearchCheck.playtestCity()` plays on the goods' literals with the rate
  pinned, at `FREE_FIXTURE_MONTHS` (1124 since EM1, a window of five months in
  nine after the town took off at m1,075). On HD2 the town is the base's to two
  people at m301, and parts at m731: there the playtest's player builds two
  police stations where the base's built roads and clinics, their running costs
  spend the treasury (937k at m900 against 2.2M), and the build that took the
  base's town off at m1,075 (1.7M of a 2.9M treasury) never comes. The town
  hovers at 2,600 to 4,100 people and is 2,009 at m2,499 (the base's 8,097 at
  m1,201). It is the mechanism, not luck: at m600 HD2's businesses hold 20% less
  cash (99k against 123k), because the till pays the tax and the dividend on a
  coupon that stays abroad, and the base town nudged by $1,000 at its founding
  still takes off at m1,075. A scan with a scratch copy of the check (the month
  a property, sections 1-5 skipped, 15 s a month) over every growing window from
  m306 to m1,965 (every third month; every month about the best) found no month
  that holds every assertion; the fewest misses are two, at m474-476
  (affordability; GDP tracking .167 against .14), m482 (affordability; it
  builds) and m838 (affordability; the free copy orders nothing). HD1 as built
  stalls the same town (2,348 at m2,499). Taken: the month left at 1124 (a month
  that fails fewer lines would hide nothing and fix nothing), the red named for
  the gate on his word for HD1-C ("hd1, same": ship, look later), and the
  fixture pending (HD2-S): EM2 moves this trajectory and rescans it in any case;
  if its town grows again a window may return, and if not, section 6 needs a
  town that grows by its own plan rather than by one look of the advisor.
- **MonetaryCheck holds; nothing queued for it.** The brief's fallback (ship on
  `--red-ok "hd1, same"` with MonetaryCheck's lines, and queue the coupon's part
  beside WN3-R) is not needed: the dial keeps its sign. WN3-R's row already says
  the band's study reads HD2's dial beside its own.

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

**The ensemble** (80 of 80, 2 at once, 16.9 min; committed as
`docs/ensemble/HD2.md`, against WN3's, `docs/ensemble/0.8.75.md`): 23 of 25
counted against WN3's 23 of 26, and no criterion that passed on WN3 fails.

| | WN3 (0.8.75) | HD2 |
|---|---|---|
| T1, every AUTO2 seed past 40,000 | 16/16, m1,101 [874 to 1,154] | 16/16, m1,156 [1,076 to 1,429] |
| T2, AUTO2 at m2,400 | 292,982 [198,117 to 375,661] | 280,956 [241,817 to 336,963] |
| A1, inflation from m240 | 1.95% | 2.15% (16/16 in band) |
| A2, longest spell over 10% | 12 [9 to 15], FAIL | 10 [6 to 17], FAIL |
| S1, zero-rate slack | longest spell 36, FAIL | longest spell 60, FAIL |
| R1, 15% against its twin | -2.12% | -2.95% |
| R3, the rate's sign at m660-720 | FAIL, 3 seeds over +1 pt | PASS, 1 seed over (median -3.04%) |
| H2 | PASS, 14/16 | VACUOUS: no burst to stop (1st half +1.40% over the twin, WN3's +4.53%) |
| T2, HIGH / SHOCK / ZERO_HOT / ZERO_SLACK | 10,504 / 20,686 / 5,466 / 213,162 | 8,136 / 17,884 / 4,555 / 181,971 |
| F1, M1 | clean | clean |

The rate lever is the stronger for it (R1, R3), and the young cities are a
little smaller: the four short policies end 10-20% under WN3's medians, AUTO2
4% under at m2,400 with a narrower spread, and T1 comes 55 months later in the
median - the coupon's tax and dividend leave the businesses' tills while its
cash stays abroad, as in ScaleCheck's town above. The two reds are WN3's.

## 4. The checks

The full suite on the merged tip (main 0.8.75, WN3, merged in; no `earth-2.bin`
here, so the Earth harnesses' full-file sections did not run; timing soft):
**154 harnesses, 152 passed, 2 red, exit 4** (MapCheck's one SOFT timing
figure not counted). Every other harness is green, MonetaryCheck among them; the
default playtest passed every month's audit and every reload. The slice's own
runs: CapitalFlowCheck, SectorBooksCheck, SectorStatementCheck, MoneyCheck,
EquityCheck and ExchangeCheck green. After the suite, ScaleCheck's
`FREE_FIXTURE_MONTHS` javadoc took a paragraph for this finding (a comment; the
maps regenerated, Stale 0 firm).

The red lines, word for word, for the gate (`--red-ok "hd1, same"`, a
`--red-line` each):

```
PassThroughCheck | exit 1
PassThroughCheck | ...and so does the price index (mean gap 0.74% against 0.51%)            FAIL
PassThroughCheck | 1 FAILED
ScaleCheck | exit 1
ScaleCheck | fixture: the city is growing - more people than a year before                        FAIL
ScaleCheck | fixture: ...and its own rents price no migrant out in the months its copies are read against (the affordability pull 1 in all 6) FAIL
ScaleCheck | ...and it builds: more homes at the end than at the start                            FAIL
ScaleCheck | fixture: the free copy ordered - an order sized from more than a million needed, and one judged on its interest FAIL
ScaleCheck | ...and it builds: more homes at the end than at the start                            FAIL
ScaleCheck | fixture: the free copy ordered - an order sized from more than a million needed, and one judged on its interest FAIL
ScaleCheck | ...cash: the copy is K times the city's within 0.0125 in all 6 months (worst 1.20e+00) FAIL
ScaleCheck | ...savings: the copy is K times the city's within 0.0160 in all 6 months (worst 3.54e-02) FAIL
ScaleCheck | ...deposits: the copy is K times the city's within 0.0260 in all 6 months (worst 4.46e-02) FAIL
ScaleCheck | ...prices: the copy is equal to the city's within 0.00400 in all 6 months (worst 9.01e-03) FAIL
ScaleCheck | ...unemployment: the copy is equal to the city's within 0.0330 in all 6 months (worst 1.00e-01) FAIL
ScaleCheck | 11 FAILED
```

- **ScaleCheck**: section 6's town, §2 above (HD2-S).
- **PassThroughCheck**, section 4's last line, "...and so does the price
  index": over the swing's 37 months the price index's mean gap between the
  twins is 0.74% at `PASS_THROUGH` against 0.51% one for one (the base: 0.24%
  against 0.87%), while the goods' own rate, the line above it, holds widely
  (0.58% against 3.54%). A probe of the twins month by month: HD2's base town
  is a slightly other one (1,393 people at the load, the base's 1,376); in both
  twins 105 homes come down at month 31 (the base's at 24), and from month 34
  the shock twin's price index stands 3.4% to 5.5% over its twin's while the
  goods' gap is -0.1%. Something other than the goods' rate moved the index
  inside the horizon; the coupon in that town is 0.37 a month. The check's own
  history says the same of its ten-year form ("a gap is the economy amplifying a
  small difference either way"), which is why it reads the horizon. Not moved:
  its premise is not this batch's to change. Pending as HD2-P, with WN3-R, whose
  study puts PassThroughCheck back on the band in any case.

## 5. How it was done

One slice on Sonnet 5.5 at xhigh (the kept cash, the books, the register,
CapitalFlowCheck section 9), reviewed as a stranger's; Opus on the brief, the
review, the measurements (the playtest, MonetaryCheck and ScaleCheck on three
snapshots: WN3's tip, HD1 as built on it, HD2), ScaleCheck's rescan (a scratch
copy whose fixture month is a property and which skips sections 1-5, 15 s a
month, and a trace of section 6's town by month) and the ensemble. Not a
process batch: no script, check machinery or rule a session reads changed.
