# SX1 — The exchange's prices readable

REV-SX1-1 validation passed: selected checks exit 0 (11 harnesses), replacement
fresh merge gate exit 0 (188 harnesses; only three existing red harnesses).
The corrected city fixture observes 40 of 240 split months, worst split-month
audit 1.96e-14. Both actual played-city assertions fail when adjustments are
disabled. Readiness was withdrawn before the repair; the previous 240-month
claim is withdrawn. The simulation and the 80-run ensemble are unchanged.

## Built and calls

- ★ The wage band is `Exchange.CONSOLIDATE_BELOW=.0025` through
  `Exchange.SPLIT_AT=.25` of PopulationManager's average monthly wage:
  roughly a fraction of an hour through five working days of pay. Adjustments
  use powers of ten against the band's geometric midpoint. Exact boundaries
  stand; no paid wage means wait. The gap prevents monthly oscillation.
- The exchange keeps its existing adjustment path: register, households,
  resting orders, fair prices, emigrants, shorts, lending, volume and the fund.
  Founding-share chart prices retain the cumulative factor.
- ★ `Formats.LARGE_PERCENT=10` (1,000%) controls the shared formatter.
  Price changes with an actual span of at least twelve months use compound
  annual growth, computed with log1p/expm1. Large ratios and cost-basis returns
  use compact multiples. A fund's purchases have different dates: inventing
  a CAGR from the lot's first purchase would misrepresent its return.
  A gain multiple includes the original cost; a ratio multiple does not.
- Shared P&L, fund chart changes, City History changes, model percentages and
  common screen percentage/rate helpers delegate to Formats. Ordinary figures
  keep their existing precision, grouping and signs.
- No save schema changes: the existing split factor, holdings and ledger
  already save every state changed here. No migration or format bump needed.
- ★ The only HW1 overlap is two one-line helper bodies in ui/BuildScreen:
  pct and pct1 delegate to Formats. Infrastructure and Services delegate to
  that helper too, so leaving it would leave three screens outside the common
  rule. No builder planning, advice, cards or fixtures changed; the orchestrator
  was notified over cbs-bus before the hunk. Game has only the three-line wage
  getter in its exchange adapter; no month-order changes.
- The same formatter also covers the debt closing gain, fiscal ratios, sector
  margins, people ratios, policy/FX changes and GDP growth. The screen helpers
  keep their old precision; none owns a separate size threshold.

## Checks so far

- `batch-check.sh --fast`: compile, generated maps, Stale green.
- Named ExchangeCheck, FundLedgerCheck, FormatsCheck, MoneyWordsCheck and
  HistoryCheck runs: green (the Exchange filter also selects DebtExchangeCheck).
- ExchangeCheck and FundLedgerCheck section 4 retain exact factor, count,
  inverse-price, cost and value assertions, now derived from the wage dials.
- ExchangeCheck's century fixture crosses both triggers, keeps every quote in
  band and preserves household value and every founding-share chart point.
  Exact boundaries and missing/invalid wages are checked.
- Corrected by REV-SX1-1: the played 240-month fixture has 40 months with
  actual adjustments, zero live quotes outside the wage band, and a worst
  split-month relative MoneyAudit residual of 1.96e-14. The original claim
  of 240 split months / 6.00e-14 used the wrong no-split sentinel and is
  withdrawn. Save/reload retains factors, founding-share prices, register,
  fund and household holdings.
- FormatsCheck checks unchanged percentages through the dial, the closed-form
  yearly rate, short spans, ratios versus gains, named trillions, extreme
  values, missing values and losses beyond cost.

- Visual QA: rendered the actual FundScreen.windowMove and Pieces.pnl outputs
  into a 612-pixel JavaFX card and inspected target/sx1-preview/readable-percent.png.
  The long chart reads about 5.0% a year; both large position returns fit as
  multiples; the ordinary +12.3% gain keeps its former form. The target-only
  preview also asserts the chart uses a yearly rate and the positions use no %.
- The orchestrator approved the exact two-line BuildScreen delegation in mail
  20261114-033847-483-orchestrator-bf01. Whichever batch ships second resolves
  that small merge. No other HW1 file is changed.
- A target-only JavaFX routing check exercised the large ratio through Build,
  Sector, Finances, Policy and Money helpers, and large changes through Fund,
  Policy, Trade and Vault helpers. All match Formats exactly; signed negative
  zero retains the old +0.0% in the fund.
- Money review: the wage is a read of the already-struck monthly average in the
  existing exchange adapter. No pool, payment, month phase, investment choice,
  cost basis or currency unit was added. The split settlement remains intact;
  only its dimensionless trigger and normalization yardstick changed.
- There are no known-reds registry entries naming SX1 in this checkout.
- The first exploratory full pick was discarded: a subsequent UI compile
  replaced target/classes while it was running, and KeptCheck failed to load
  a class and left its FX thread alive. I identified the SX1 JVMs by jcmd's
  user.dir and their native parent IDs, stopped only that owned check tree,
  and queued a clean final pick with the source/classes frozen. This run is
  not validation evidence and none of its interrupted lines is re-filed as
  a known red. The ensemble copies its own classes at startup and was unaffected.

## Final-model ensemble

The split months and units change the exchange trace, so the 80-run ensemble
was run once, on the final simulation model (classes snapshot addd636c; later
commits only route UI strings and update records/indexes). It completed all
80/80 runs in 20.3 minutes against docs/ensemble/0.9.100.md. The baseline is
committed as docs/ensemble/SX1.md for the publisher to version.

| Reading | SX1 | Baseline 0.9.100 |
|---|---|---|
| scored criteria | 24/26 | 24/26 |
| hard verdict | PASS, 10/10 | PASS |
| M1, all five policies | zero failed/unread audit months | zero |
| F1, all five policies | zero findings in every group of 16 runs | zero |
| AUTO2 A1, info | FAIL, 11/16 in band; median 2.89%, range 2.49–3.36% | PASS; median 2.79%, range 2.46–3.59% |
| AUTO2 A3, info | PASS; deflation spells 0 [0–7] months; final/peak population 100% [97.17–100%] | FAIL; spells 0 [0–12]; population 100% [98.09–100%] |

These are the only changed verdicts: A1 PASS→FAIL and A3 FAIL→PASS. A2
remains informational FAIL (long inflation spells). Only M1 and F1 are ship
stops, as the brief directs. The committed baseline retains every policy's
reading beside its predecessor, including unchanged informational verdicts.
Logs and CSVs: target/ensemble/SX1/; no main baseline was rerun.

## Final validation record

- The final source is frozen: the full pick and fresh-clone gate use separate
  class directories, with no rebuild into either while its suite runs.
- The first launcher run (failed, not reusable) is retained at /tmp/cbs-gate-SX1-3y0dHtPu.
  It merged batch commit b6ae5b0c40dd03866587ed9fbf29236408d1960b onto main
  ef5cfe589277dd14e1a970ddb78212e82b571ee9. It exited 1 because ShortSellingCheck still used the old trigger; the repair and rerun are below.
- The picker selected ALL (199 registered harnesses, using the repository's
  gate tier and directly selected nightly checks). Its money/UI-wide review
  flags were reviewed: FundScreen changes only strings; UserInterface changes
  only GDP/inflation headline strings, with no threads, callbacks, timer or
  node lifecycle changes.
- Both KeptCheck runs finished with the sole, existing KF63 assertion:
  “a month's redraw after each choice hands back nine in ten of the page's
  nodes, or more”. No KeptCheck assertion is relaxed or re-filed.


## The full gate's finding and repair

The first clean full pick exited 4 and its fresh-clone gate exited 1. The only
NEW lines were ShortSellingCheck's split fixture and its two dependent share
count assertions. Its MarketCheck.City reuses ExchangeCheck.Firms, whose wage
was unspecified (zero), so no wage-triggered split could occur. This is SX1's
fixture to re-derive, not an unrelated red to name away.

The 10,000-times-fair print is retained. The fixture now explicitly places its
initial fair quote at the wage band's midpoint, asserts that the original
print crosses the wage dial, and asserts the exact power-of-ten factor derived
from the dials. The original exact short-share, fund-lending, collateral and
register-identity laws remain. This repairs only a harness: no simulation or
formatting code changed, so the completed final-model ensemble remains valid.
- The repaired ShortSellingCheck passed alone (batch-check exit 0), including
  the exact factor, both scaled share counts, unchanged collateral and the
  whole register identity. Compile, regenerated maps and Stale also passed.


## Pre-review full pick and merge gate (superseded by REV-SX1-1)

- Final `batch-check.sh --pick`: exit 3; ALL selected. 188 harnesses ran,
  185 passed. The existing reds are PlanCheck (PX1: district checks and
  avenues), KeptCheck (KF63: redraw node reuse), and DeathRecordCheck
  (OX1: the orphan section). No NEW red, no new registry entry.
- Final `sh tools/gate.sh SX1 --dry-run`: exit 0. Its suite exited 3 with
  exactly those same known red harnesses. Compile, maps and Stale passed
  (zero firm findings). Seven timing assertions were soft, and eight
  played-path misses were informational; no correctness law was relaxed.
- Keep /tmp/cbs-gate-SX1-x0V9WEWg, including ship.log, checks/, generated/,
  checks.sha256 and checks.complete. It checked batch
  5740fe0eb35cb390e5c4277f7c221b2141954cf1 against main
  ef5cfe589277dd14e1a970ddb78212e82b571ee9. This replaces the failed first run.
- Main still matched that commit at final validation. All changes after
  the passed run are confined to these notes and the ship file.
- ★ The final regeneration's one remaining index offset
  (ShortSellingCheck.RUN_START, 895→903 in docs/dials.md) is in the gate's
  hash-bound generated indexes. The publisher restores and commits those
  indexes when reusing the run (tools/gate-reuse.sh). The local generated
  copy was restored to HEAD, honoring the explicit post-gate records-only
  rule while retaining the correct index for publication.
- The record-only branch advance was dry-run again and exited 0, explicitly
  reusing /tmp/cbs-gate-SX1-x0V9WEWg and rejudging its three known red harnesses.
  This replay is kept at /tmp/cbs-gate-SX1-0gB2BKln/ship.log; it was of branch
  bdeaa29e22b0001a2a249c4cffbdfcb63d514a44 and main
  ef5cfe589277dd14e1a970ddb78212e82b571ee9. It confirms the final records,
  reusable evidence and publisher-owned generated indexes remain valid.


## REV-SX1-1 — non-vacuous split-month audit

The review correctly found the sentinel error: Exchange.startMonth() writes
zero into each split slot; Exchange.getSplit() returns that slot unchanged.
Readiness was taken off first in e0536d8d and the old gate-run marker removed.
The older kept runs above are historical and will not be reused after this
harness change.

- ★ The shared SplitAudit observer counts a month only when at least one factor
  is nonzero, and uses that same observation for the occurrence and split-month
  conservation assertions. The original 1e-9 audit bound is unchanged.
- A twelve-step exchange run with quotes inside the wage band produces zero
  split months. The exact conditions used by the played-city assertions both
  reject it, even with a perfect zero audit residual.
- The existing forced, real split counts exactly one month and passes both
  conditions. The actual 240-month city has 40 split months; every one closes
  the real MoneyAudit, worst relative residual 1.96e-14. Its quotes remain
  inside the wage band, and the save/reload checks still pass.
- The picker against readiness commit 9bc2cfc0 selected BankCheck, ExchangeCheck,
  MarketCheck, MortgageCheck, OrderBookCheck, OwnerCheck, ShortSellingCheck and
  TimingCheck. The name filters ran 11 harnesses; batch-check exit 0, all green.
  Compile, regenerated maps and Stale (zero firm findings) also passed.
- Regression proof: an isolated target/rev-sx1-1-mutant classpath restores only
  the old != 1 comparison. It exits 1 with exactly three failed assertions:
  zero split months, no-split occurrence rejection, and no-split audit rejection.
  It reproduces the old false 240-month count and 6.00e-14. The fixed code
  reports zero for the negative control, one for the real-split control, and
  forty in the city. The mutant does not alter source or target/classes.
- Logs: /tmp/sx1-rev1-pick.txt; target/checks/ExchangeCheck.log;
  target/rev-sx1-1-mutant/run.txt. No model or formatting code changed, so the
  completed ensemble stands, as the send-back directs.
- All regenerated indexes are committed before the replacement gate, including
  the previously publisher-owned ShortSellingCheck.RUN_START offset in dials.
- Additional direct failure proof uses only target/rev-sx1-1-no-split: a
  private compiled copy of Exchange disables adjustments in that JVM. The
  real played-city assertions both print FAIL (“the played city actually
  splits shares” and “MoneyAudit closes in every month with a split”); the
  count is zero, its split residual is zero, and ExchangeCheck exits 1.
  Normal compiled source passes those same assertions with forty observed
  months. This experiment changes neither source nor target/classes, and
  its intentional reds are not part of the shipping model or registry.


## REV-SX1-1 replacement gate and readiness

- Fresh `sh tools/gate.sh SX1 --dry-run` exited 0, with no reuse of the old
  evidence. Its suite exited 3: 188 ran, 185 passed; only existing PlanCheck
  (PX1), KeptCheck (KF63) and DeathRecordCheck (OX1) reds. No NEW red or registry
  change. Compile, generated maps and Stale passed, with zero firm findings.
  Eight timing assertions were soft and eight path misses informational.
- Keep /tmp/cbs-gate-SX1-IHgxFMHv, including ship.log, checks/, generated/,
  checks.sha256 and checks.complete. It was of branch
  d665c4859b3d1e75b1d57b09cb77b50d82819db5 and current main
  ef5cfe589277dd14e1a970ddb78212e82b571ee9. The ship file names this run, replacing
  the pre-review runs. Main still matched this identity at final verification.
- The fresh gate's ExchangeCheck independently reports 40 real split months,
  zero quotes outside the band, worst split-month audit 1.96e-14; its negative
  control rejects both conditions and its actual split control accepts both.
- After this run only the notes and ship file advance. The ship file is pushed
  last with the provisional marker removed. Ensemble evidence is retained
  unchanged, per the send-back: this repair changes harness code alone.
