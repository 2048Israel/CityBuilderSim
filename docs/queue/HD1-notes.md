# HD1 - the hoard: business bonds in the history ({next}, 2026-10-29)

The brief: [HD1.md](HD1.md), from a study of his San Francisco city (businesses
holding 59x GDP in cash and dollars abroad). Jerus, 2026-10-28: "interest
abroad as profit sure", "gotta add workers to real estate", "Yes, queue it".
Started 2026-10-29 beside BK1, with EM1 to build on this branch.

**What ships is item 3 alone.** Items 1 and 2 were built, checked and taken
back with revert commits: **the orchestrator's call (2026-10-29), on this
session's recommendation**, because each changes how the game plays past what
a fixture can absorb, and both are Jerus's calls (he is away today). They wait
as [HD1-C](HD1-C.md) (pending, a call) and [RE1](RE1.md) (queued).

## 1. Business bonds in the history (shipped)

`HistorySave` records `businessBonds`, `BondMarket.totalFace()` (every business
bond's face outstanding), beside `businessDebt`, which is the loans and
mortgages only, so the fade of the bonds can be charted: the copy on load, the
screens' map, the redenomination's money list, the History screen's CREDIT
traces ("Business bonds") and the year book. History series ride in
`HistorySave` alone (`DataSave` carries none). SaveFileCheck 13b, "a bond in the
saved history": a city that owes bonds, the series' last point at the face
outstanding to the cent of a thousand, the same points after a reload, and a
hundred-for-one reform scaling it as it scales the debt beside it.

The model is main's: the default playtest's eight trace files are
byte-identical to main's and its report differs in its run time alone, so **no
ensemble** for what ships. The month trace (`<prefix>-month.csv`) carries four
more columns on its end, the businesses' tills, dollars abroad, loans and
bonds (`bizCash`, `bizAbroad`, `bizDebt`, `bizBonds`), compared like for like.

## 2. The coupon abroad as profit (built, taken back: HD1-C)

Built as the brief recommended: struck with the statements
(`OutwardInvestment.payCoupons()`), a line of finance income inside pre-tax
income, taxed, in the dividend, paid into the till; revaluation out of income.
It halved the hoard abroad by m4,000 (35.4x GDP to 18.0x) and strengthened the
D$ (232 to 143 per US$ at m2,852). It also inverted MonetaryCheck's rate dial in
a young city (inflation 7.92/6.25/6.76/8.17% at 3/10/20/40% against main's
10.43/9.68/8.99/8.82%): the coupon home lifts the D$ at low rates more than
the dial restrains. **Recommended: counted as profit, kept abroad**, the
till paying the tax and the dividend and recalling dollars only when short.
The figures and the other ways out are in [HD1-C](HD1-C.md).

## 3. Real Estate's workers (built, taken back: RE1)

Two DIPLOMA posts per Studio and Low-Rise by the NAA's staffing rule, a House
none; the landlords skip the staffing gate (homes before workers); the payroll
in the rent floor. The ensemble on the coupon and the posts together scored
22/25 against 0.8.68's 23/25 (A3 flips on one seed's 12-month deflation
spell) with the medians held, but seed 4 stalled near 25,000 people from m1,100
to m1,800 and ended at 44,157 against main's 249,634. A bisect put it on the
posts: the coupon alone ends at 241,339. Without the payroll in the rent floor
it still stalls to m1,500 and ends at 145,033. The posts also broke
ScaleCheck's tracking, HealthCheck, OrderSearchCheck and SaveFileCheck's bank.
**Recommended: its own batch, the stall's cause proved first** on seed 4, which
is the fixture. The figures are in [RE1](RE1.md).

## 4. Look, and report only

- **Luxury Retail's labour** (payroll 1% of its value added; Construction's
  5%). A Boutique's 7 posts for 320 pieces a month is a real boutique's staff,
  and the builders are as low, so the 1% reads as wages trailing prices over a
  long run, not Luxury's posts: in the US, compensation is about half of value
  added economy-wide (BEA). Recommended: a labour-share study (each sector's
  payroll over its value added through a run, against BEA's compensation
  shares by industry) before any post or markup moves. Pending as HD1-L.
- **The hoard on main's default playtest** (a year's GDP each):

  | month | tills | dollars abroad | business loans | business bonds |
  |---|---|---|---|---|
  | 600 | 2.10 | 2.07 | 1.10 | 1.31 |
  | 1,200 | 1.20 | 2.05 | 0.10 | 0.25 |
  | 2,400 | 2.42 | 8.95 | 0.09 | 0.14 |
  | 2,852 | 3.86 | 15.22 | 0.07 | 0.05 |
  | 4,000 | 8.88 | 35.43 | 0.02 | 0.04 |

  The playtest's city keeps some bonds to the end (his runs' last bond was
  m1,533). The after, with the coupon home, is in HD1-C; with the posts as
  well: abroad 8.6x at m2,852, bonds 0.012x.
- **A latent map fault** (pending as HD1-M, for a map batch): on the coupon and
  the posts' trajectory LongPlaytest's layout watch (CL3) flagged 3 Convenience
  Stores moving at m3,998 in district (-1,-2), the type's drawn count 623 to
  622: boxes moved when one of their type left, outside the spill. HD1 touches
  no map code and its shipped trace is main's, so it does not show here: the
  bigger city exposing the map, as WN3 found. That run is its fixture (commits
  3dea900f and fec704a7, the default playtest).

## 5. Checks and how it was done

The full suite on the shipped tree, both data files named: exit 0, 152
harnesses in 763 s (timing soft, beside BK1's and EM1's runs). The ensemble's
run on the coupon and the posts is not a baseline (what ships moves no trace)
and is not committed. With the
coupon and the posts the suite exited 4 (MonetaryCheck, HealthCheck,
ScaleCheck, OrderSearchCheck, LongPlaytest's map flag); each is listed in
HD1-C or RE1 with the slice that caused it.

Two slices on Sonnet 5.5 at xhigh (the coupon; the posts and the series, the
second in its own worktree after a first try could not write a sibling one),
reviewed as a stranger's; Opus on the briefs, the review, the ensemble, the
bisects (seed 4 on main, the coupon alone and the floor out) and two scratch
tools for ScaleCheck (a month-by-month scanner of section 6's town and a
line-by-line diff of section 9's K = 1 and K = 1000 months).
