# QR1 — the distress rule sells plant that pays its running costs

## The finding and the call

The quarter rule in `BusinessInvestment.planDistressRetirement()` reads the
net-loss streak. Interest alone can run that clock out: an overdrawn business
whose plant earns its running costs then sells its biggest holding at
`MAX_RETIREMENT_FRACTION` a month. AQ1 already saved the separate operating-loss
streak, excluding `OldMetal.SOLD`; its lent bleed reads that clock, but the
overdrawn rule did not. AQ1's measurements were on 0.9.134, before R1g made the
1900 city the default. QR1 re-measures both foundings on c72b19ab2 (0.9.208).

★ Take the row's recommended way: require `DISTRESS_LOSS_MONTHS` consecutive
operating losses as well as net losses before distress sells plant, whether
overdrawn or lent. Keep the existing runway, quarter, lent pace and sector
eligibility. Recovery or break-even resets the operating clock. This is a
sector-wide running result, not a valuation of each plant; the spare-capacity
rule remains a separate rule.
The hold reason names the streak, including zero: `noteOpened()` also resets
it, so zero alone must not claim that the previous statement was profitable.

The row's slower quarter would still dismantle paying plant for a financing
problem. Selecting individual loss-making plants would require a different
valuation rule; this batch uses the existing sector-wide operating result and
its saved runway. Keeping main preserves the finding. No call waits on Jerus.

WHY, reality checked 2026-11-25: financial distress does not itself make a
productive business worth liquidating. The [U.S. Courts' Chapter 11
explanation](https://www.uscourts.gov/court-programs/bankruptcy/bankruptcy-basics/chapter-11-bankruptcy-basics)
describes continued operation during reorganization and adjustment of debts;
its [bankruptcy process
explanation](https://www.uscourts.gov/court-programs/bankruptcy/bankruptcy-basics/process-bankruptcy-basics)
distinguishes that from Chapter 7's asset liquidation. Canada's [Office of the
Superintendent of Bankruptcy on the
CCAA](https://ised-isde.canada.ca/site/office-superintendent-bankruptcy/en/you-are-owed-money/you-are-owed-money-companies-creditors-arrangement-act)
describes continuing operations, wages and service during creditor protection
while an insolvent company seeks a viable arrangement. These are available
routes, not guarantees that every profitable plant must survive; the model
choice is to send an interest-only shortfall to its existing debt resolution
instead of ordering a mechanical quarter sale.

The bank was read before the mend: `restructureInsolventSectors()` defaults
the cash-short share, offers ranked interim financing, and, on refusal, calls
`restructure(sector, true)`. That backstop reduces principal to
`RESTRUCTURE_TARGET` of assets, closes the overdraft, records the event and
blocks borrowing; inside an existing ban it closes the overdraft without
counting another event. `EconomyManager` collects the forgiveness through its
existing settlement. QR1 adds no forgiveness or money transfer and changes no
bank rule. The harness also exercises the existing forced restructuring with
surviving plant.

## The proof

`DistressBleedCheck` section 6 causes the finding on a scratch town: eight
Contact Centres, negative cash, remaining restructured debt with fresh credit
blocked, monthly positive operating statements whose interest makes net income
negative. The fixture first exercises the existing forced debt restructuring,
checks the debt target and closed overdraft, and then causes a new overdraft
inside the borrowing ban. It records both clocks
through the model for `DISTRESS_LOSS_MONTHS + BLEED_MONTHS`, then asks the
distress rule each month. No paying plant may be sold. It then causes fresh
operating losses: nothing one month short of the dial, the quarter at the
dial, and no sale on recovery or break-even while net income still loses.
The existing quarter's size, biggest-holding and faster-than-bleed assertions
remain. Freight supplies the same paying-plant boundary for a sector that
does not bleed while lent.

The old section 6 explicitly set the operating streak to zero and demanded a
sale from the net streak alone. That is the defect QR1 changes, not a premise
to preserve: the real law above replaces it, and the new statement-driven
assertions prove the effect. Other retirement fixtures must explicitly cause
the operating losses their descriptions already claim; their plant-protection
and sale assertions remain.

No new state or save fields: both streaks already round-trip. No save-format
bump or conversion; old saves have no field reinterpreted.

## Checks and re-measurement

The helper has its own detached c72b19ab2 worktree at
`C:/Users/Jerus/AppData/Local/Temp/QR1-measure-20261125` and runs the two named
`LongPlaytest` measurements serially; builder JVMs wait until it has finished.
Only observation is added to the retirement call; model decisions are
unchanged. MemWatch committed memory was 79.8% when it was started. No full
main suite is run.

The **1900 default** named check exits **0**, all checks passed, 1603 seconds.
With 2400 months requested, the default player ends at month 2405. Its primary
city records **16 actual distress sales, 518 buildings**, in Copper Refining,
Cement & Concrete and Refining. **13 sales have a positive current operating
result excluding old metal; all 16 have an operating-loss streak under 24**.
Thus the finding survives on the new default, with different sectors from
AQ1's old evidence. Those 13 paying-plant events sell 280 buildings. Exact
windows: Copper Refining m1777–1778 and m1874–1875 (4 events, 14 buildings);
Cement & Concrete m1830–1831 and m1833–1839 (9, 501); Refining m1853, m2091,
m2307 (3, 3). Examples from the observer (all income/cash here is in
local-money thousands): Cement & Concrete m1839, 14 of 53 Lime Kilns,
operating result +754,804.875, operating streak 0, net streak 33, cash
-3,696,397.155; Refining m1853, its last Crude Unit, operating result
+23,550.175, operating streak 0, net streak 25, cash -258,621.159.

The primary-city events are kept apart from LongPlaytest's later
MoneyFixtures cities: the 1900 observer's first primary-city segment is
extracted before the month resets; today's observer filters on the actual
Game identity. Main's copied baseline is `target/qr1-main-1900.txt`, events
`target/qr1-1900-primary-decisions.tsv`; the helper's normalized stdout hash
(JVM option notice and runtime fields removed) is
`d5f3e1d131c649b3eda9fdad6f01184a90805286a6c3f31e067414d9591eff7d`.

The **today founding**, `-Dplaytest.founded=today`, also shows it: **117 actual
sales, 1,017 buildings; 109 operating-positive events, 837 buildings; 112
events with operating-loss streaks under 24, 843 buildings**. All are
overdrawn. Sector event/building counts and observed windows (the TSV keeps
each exact month):

| sector | events / buildings | month windows |
|---|---:|---|
| Retail | 3 / 47 | 328–329, 448 |
| Materials | 1 / 1 | 357 |
| Heavy Industry | 50 / 187 | 807–811, 916, 1273–1295, 1508–1533 |
| Cement & Concrete | 42 / 571 | 915–931, 1220–1236, 1532–1563 |
| Oil | 15 / 205 | 1512–1526 |
| Refining | 5 / 5 | 1240, 1242, 1299, 1522, 1925 |
| Forestry | 1 / 1 | 1523 |

For example, the last Lime Kiln goes at m931 on operating result +589.810,
net/operating streaks 59/0 and cash -1,676.211. Heavy Industry's paying-plant
sales at m1273–1295 have operating results +103,702.626 to +399,067.231,
operating streak 0. Refining's m1299 sale has current operating loss
-3,824.062 but a fresh streak of only 1: the whole runway matters too.

**Today is a partial measurement, not a passing 2400-month check.** The
unchanged-model named `LongPlaytest` exits 4 when AllChecks' fixed 1740-second
deadline kills it, without a final shutdown summary. The month CSV has full
rows through 2370 and a partial 2371; the bond/house CSVs retain complete rows
through 2387. These are the flushed observations before termination, not a
claim of a completed check. No rerun, timeout increase or relaxed assertion:
the finding is already observed on both cities, and QR1's canonical full
suite is still owed at its dry run. No known-red waiver is added for this
diagnostic configuration. Both primary cities show no lender-covered bleed
and no Manufacturing distress/MR1 condition; MR1 is left to its row.

The helper's detailed tables and records are copied to this worktree's
`target/qr1-today-primary-decisions.tsv`, `qr1-today-short-streak-sales.tsv`,
`qr1-metrics.txt`, `qr1-measurement-readme.txt`, `qr1-main-today.txt` and
`qr1-today-check.txt`. Its own target also keeps `qr1-instrumentation.patch`
and `Qr1DistressMeasure.java` for the observer. The source was restored
cleanly before the red proof; no model fix or extra city was run there.

The first red proof finds the five intended QR1 boundary failures and two
fixture failures: section 8 reused section 6's town, which now retains debt,
violating the old own-cash premise. Mended by giving section 8 a fresh
debt-free town and retaining every assertion. The new restructuring,
no-lender, one-short and at-dial premise assertions already pass on main.
The repeated named check on restored c72b19ab2, using harness 833ca8ef6,
exits 4 (harness exit 5): **exactly the five intended QR1 failures, zero
fixture failures**. They are the interest-only hold, the full operating-loss
runway, recovery to +10 and to break-even, and the non-bleeding sector's
paying-plant hold. All original controls pass, including own cash (25 asks,
zero sales before lending, one centre once lent), the quarter at the dial,
restructuring and refused fresh credit. Logs copied to
`target/qr1-red2-check.txt`, `qr1-red2-DistressBleedCheck.txt`,
`qr1-red2-failing-labels.txt` and `qr1-red2-verification.txt`. The helper's
worktree is clean, its model/resource hashes unchanged and all its JVMs ended.
Branch checks on 833ca8ef6, after the docs-only main merge, each run in its own
`./batch-check.sh` call with the required main-worktree Earth/world paths:

| check | exit | result |
|---|---:|---|
| `--fast` | 0 | compile, generated maps and Stale green |
| `DistressBleedCheck` | 0 | all new boundaries and original controls green |
| `InvestCheck` | 3 | only the existing recorded HT1-H wage/rebate fixture red |
| `FoundingYardCheck` | 0 | protected last yard and the two-building sale green |
| `RailDepotCheck` | 0 | last-station guards and the below-threshold sale green |

No new reds. InvestCheck's approved line is the fixture that the builders'
wages moved since founding and the founding-ladder rebate lies either side of
the line; it is already in main's gate/known-red record, owned by HT1-H. No
approval, conservation assertion or save assertion is changed. Stale has
zero firm findings. Kept per-call summaries are `target/QR1-fast.txt` and
`QR1-<harness>.txt`; the harness runs also keep their
`QR1-<harness>-suite.txt`. The serial runner is
`target/QR1-local-checks.sh`, with combined output in
`target/QR1-local-checks.txt` and individual exit files.

The read-only maps helper found 640 pure generated-date changes and nine
substantive generated files. `SourceTree` uses `LocalDate.now()` in the JVM's
default zone: HEAD's Nov26 headers become the laptop's Nov25. After the last
check, restored only tracked, unstaged maps whose UTF-8 contents are byte
equivalent to HEAD after replacing the date in their single generation
header; additions/deletions or any other differing byte are excluded. The
selection is kept in `target/QR1-date-only-paths.txt`. Keep all nine real
outputs: the five source maps, map README, dials, harnesses and month order.
The check only tests Maps' successful exit, not header dates, so no JVM rerun
is owed solely for this cleanup. This is before any dry run; no kept gate
snapshot is altered. Generated month-order Markdown retains its intentional
two-space line breaks (the new link line is flagged by `git diff --check`).

There is no after-fix city run at this stage: the first affected real call
and the new guard establish path divergence. The final-model ensemble and
dry run follow review, using `docs/ensemble/0.9.208.md`, as the orchestrator
directed after REV-QR1.

## Publication order

Merged main's docs-only 3bf83d20e (AP5 queue/brief) before branch validation;
its model is still c72b19ab2's 0.9.208.

`sh tools/pick.sh origin/main HEAD` selects **ALL 257 harnesses** because
`BusinessInvestment.java` has 67 readers. Its review line is
`review not needed - no money, saves or threading in the diff`; QR1's brief
explicitly requires a stranger's money review of distress and the existing
restructuring boundary, so that review is owed despite the script's floor.

The helper has already observed affected sales in the default 1900 run. The
first such sale is suppressed by QR1, so the default model trace moves and an
ensemble is owed. On the stranger's SHIP, merge origin/main and run the final
model's ensemble on the gate box against the newest 1900 baseline. Then ask
the orchestrator for the ALL dry-run slot. The provisional marker stays until
those steps are complete and is removed last in its own commit. No local full
pick or dry run is started before that go.

## REV-QR1 — the mends, 2026-11-26

The stranger accepted the debt path, money, three repaired fixtures and
reality case, but found P1 in BankCheck 15: its net-only `restoreStreak()`
left the new operating gate closed. The null-sale return then skipped the
distress-sale cent-level money audit, salvage-use audit and broke-builders
case. This was missed by the first four named checks. It is QR1's fixture
defect, never a known red.

`restoreDistressStreaks()` now restores both histories for RET while keeping
other sectors' histories. That alone is insufficient: the played month is
currently operating-profitable and resets the clock. The next attempt adds a
real repair bill for the owned shop, sized from its already-booked revenue
using `EconomyManager.maintenanceBillFor()`. Its temporary repair rate makes
the bill exceed revenue with headroom, then is restored in `finally` after
each audited month. Materials are consumed and builders paid through the
existing model, rather than injecting an income-statement loss. The played
month must actually lose on running costs and keep the full streak. The
lone-shop/no-spare-capacity, stock/cost/cash and broke-builders assertions,
return guards and original audit tolerances remain.

The first `batch-check.sh BankCheck` call exposed another wrapper fact:
substring selection also starts CentralBankCheck and YoungBankCheck. Bank's
first attempted mend fails its two new fixture assertions; YoungBank passes.
CentralBank continues independently, so it is stopped by its verified owned
PID 46116, not by image name; that incomplete extra is not a green check or
a known-red waiver. No selector/machinery is changed. Subsequent local named
runs use `-Dcbs.parallel=1`, and BankCheck's exact existing main is invoked
directly after `--fast`, with AllChecks' own 2 GB/64 MB, soft-timing,
observation and UTF-8 flags. The ALL dry run still runs the full registry.

On source **97c748cf6**, final `--fast` **0**, exact **BankCheck whole 0**,
**EnsembleCheck 0**. The read-only helper verifies **37 executed section-15
assertions, all OK**, not merely an empty early return: the first retirement
at month 5 sells one Convenience Store, four material units for $1.8k; its
cent-level audit executes, the material is subsequently used and expensed at
its original cost and that cent-level audit executes too. The second actual
retirement occurs with the builders overdrawn; they buy/pay nothing and the
seller receives zero. Both played operating-loss premises and new receipt
observations pass. Logs: `target/QR1-BankCheck-whole-final.log`, its `.exit`,
`QR1-review-fast-final.txt`, `QR1-review-EnsembleCheck.txt` and
`QR1-review-final-checks.txt`. This closes P1. The bank episode comment now
allows paying plant to continue operating instead of asserting every banned
sector is selling plant monthly.

P2 is taken. Forgiveness already has a saved sector cumulative in
EconomyManager; `LongPlaytest.forgivenTotal()` sums it. The playtest's
`backstopInBanSum` is a **subset**, not an amount to add to forgiveness.
Existing Salvage records omit plot cash and retirements with no saleable
material, so a new optional, transient `Game.watchRetirements()` observer
receives actual completed retirement events: sector, actual buildings,
distress flag, plot cash and material cash, gross before debt repayment,
and the running result captured before disposal changes the sector.
It reads the amounts already paid in `retire()` and moves no money, changes
no decision and adds no saved field. The two BankCheck fixtures also verify
its actual sale/material cash and zero-material-payment case.

The ensemble exports cumulative forgiveness, in-ban backstop, all immediate
retirement receipts, distress building count and distress plot/material/gross
receipts, plus the in-ban sector-month count. Per-retirement TSV sidecars
retain sector, time, cash, running income and both streaks, so the 13/109
paying-plant events can be identified directly. New cumulative fields retain
17 significant digits; existing
columns keep their original format. Money is nominal local-currency
thousands. Immediate receipts mean plot buybacks plus the builders' payment
for material, before financing settlement; later old-steel inventory trades
are separate. Compare each matched run at the same endpoint. P2 uses the
four default-city measurements approved below, rather than historical policy
ensembles. Report both all-retirement and distress-only receipts. Extra
forgiveness above avoided receipts is to be mailed before the dry-run request.

P2's historical-data limitation is real: both retained OA1 sets (80 raw CSVs
locally at `C:/Users/Jerus/AppData/Local/Temp/cbs-kept-OA1/target/ensemble/OA1`
and on the box) lack all three flow families; their summary and progress logs
cannot recover them. The exact OA1 model is dc7f9cc7d (snapshot calls itself
0.9.206, published baseline is 0.9.208). Prepared observation-only probe
`probe/QR1-OA1-money` **c99f2e56b** on that commit, changing only Game's observer
and the identical ensemble exports: its `--fast` **0**, `EnsembleCheck` **0**.
No 80-run historical replay was started. The orchestrator declined that
proposal and authorized four paired default runs instead: main **3bf83d20e**
and QR1, each founded the 1900 way and under `-Dplaytest.founded=today`, the
same seed and months. Prepared `probe/QR1-main-money` **5c05e7290** on that
exact main with the identical observer/exports; its `--fast` **0** and
`EnsembleCheck` **0**. The unused OA1 probe will not run. QR1's formal 80-run
ensemble remains separate, against `docs/ensemble/0.9.208.md`, with M1/F1
judged on its own. Paired work uses the box's heavy lock, or the laptop only
if the box is busy, and never overlaps another heavy job of this builder.
The cash comparison and any required excess-forgiveness mail precede the
dry-run request.

The paired runs use `-Densemble.defaultPlaytest=true`: the driver invokes
`LongPlaytest.main()` itself, leaving its policy actions, save/reload,
overshoot and final checks intact. Optional primary-created and state-sample
callbacks are null in ordinary harnesses and the formal policy ensemble.
They capture only the primary Game's contiguous calendar months 1–2400, reinstall the
transient retirement observer after round trips, then detach it before later
fixture cities. Each pair has its own label; an AUTO2 policy run is not a
substitute for the default player. The exporter also sums actual monthly
forgiveness independently of the saved cumulative and records forgiveness
in sector-months with an actual in-ban backstop. Its DONE marker certifies
complete, error-free exports only: the full playtest's assertions remain in
the log and retain their own verdict. No extra historical 80-run replay runs.

The first paired attempt (`QR1-main-1900`, main probe **7b940d073**) failed the
exporter's continuity assertion immediately at calendar month 2; it supplies
no usable cash totals. A helper traced Game's convention: month 1 is the
unsimulated founding baseline, `Game.run()` initializes it, and the first
played tick ends at month 2. The capture now explicitly emits and labels that
one baseline after `g.run()`, with zero accumulated monthly flows, followed
by 2–2400 played rows. It asserts `!hasMonthRun()` on the baseline and
`hasMonthRun()` thereafter as well as exact calendar continuity. It adds no
simulation or audit and changes no calendar. The failed attempt ended on
its own after the post-primary checks. Fresh labels keep its failed snapshot
separate from the corrected captures.

Box admission twice found PW9 running and RES3's complete ensemble waiting
(GB3 FULL=2); no QR1 formal model ran. The launcher is a `/bin/sh` script:
an initial `bash tools/gate-box.sh` parse failure was corrected to its normal
`sh` invocation without changing the publisher. Read-only helper snapshot
05:24 UTC found PW9 55/80 and RES3 0/80 waiting. The orchestrator was told;
the explicitly approved PC-if-busy path runs paired defaults here, `JOBS=1`,
one at a time. The formal 80 remains for the box. Retained capacity evidence:
`target/QR1-box-ensemble-capacity.log` and its `.exit`.

The final event/sector export refinement on **3f87c257c** repeats `--fast`,
exact BankCheck whole and EnsembleCheck: **all 0**. Its new event running
result is also checked negative in both BankCheck sale fixtures. No decision
rule, audit tolerance or cash movement changes with the observations.

The default-player implementation at **9f360541f** passed `--fast`, exact
whole BankCheck and EnsembleCheck (**all 0**, after 01:11 local on
2026-11-26), before the startup capture correction above. The corrected
source **1803d5cbc** repeats all three: **all 0**, finished by 01:35.
Matching main probe **4f0492990** repeats `--fast` and EnsembleCheck:
**both 0**, by 01:38. The founding row leaves both audit fields unavailable;
the empty initial audit sentinel cannot masquerade as an executed audit.
The matching main probe adds the same optional observations, never the QR1
gate. Both source snapshots are pushed before any corrected measurement.

### P2 paired cash measurements (calendar 1–2400)

The first completed pair member is main/1900, label `QR1-main-1900-v2`,
observation-only snapshot **4f049299028a7cf1061015a0aab2fefcf99ba9ac** on
main **3bf83d20e**. It ran on Jerusalem with Oracle JDK 21.0.4+8-LTS-274,
the configured earth-2/world-1 data paths above, seed 0 and the unchanged
Standard 1900 default player (generated city, no Earth-site flag).
LongPlaytest **PASS**: primary ends at m2405 in 1725 s, findings “Nothing”,
every monthly audit and reload passed; all **79** HA1 post-primary fixture
assertions pass. Its final export DONE follows those fixtures, 1779.7 s.
The runner does not separately retain the child JVM exit status. The policy
scorer's separate exit is not this standalone playtest's verdict; the formal
80-run M1/F1 comparison is still required.

Auxiliary policy scorer result is retained as observed: wrapper **exit 4**,
AUTO2 **M1 FAIL, 0 failed audits (1 unread)**. The sole unread row is the
unsimulated founding baseline, calendar 1; all played rows 2–2400 have
`audit_ok=1`, residual 0. `score.py` counts every blank, including this new
founding row, as unread. F1 is “info, 0/1 findings”, no baseline, and this
single default/AUTO2 carrier has no other policy runs. ★ Keep the founding
audit blank and this auxiliary scorer failure visible: no fabricated audit,
scorer change, waiver or known-red entry. These four default cash
measurements are separate from the required formal policy ensemble, whose
ordinary rows are played months and whose **M1/F1 must pass independently**.

Exactly 2400 contiguous calendar rows, one founding baseline and 2399 played
rows; overshoot and fixture cities excluded. Full precision saved forgiveness
equals the independent monthly sum at the endpoint. The maximum interim
difference is 1.4551915228366852e-11, from addition order. All retirement cash
and count cumulatives reconcile to the event sidecar at every row; no invalid
numeric value, missing played-month audit or unexplained-money finding.

All amounts below are nominal local-currency **thousands**, gross retirement
receipts before debt repayment. In-ban forgiveness/backstop are a **subset**
of total forgiveness, never added to it.

| City/model | Forgiven F | In-ban forgiven Fban | Actual in-ban backstop B | Distress receipts D | All retirement receipts R |
|---|---:|---:|---:|---:|---:|
| 1900/main | 1003070.5468181840 | 230623.13222283978 | 230623.13222283978 | 159142294.59705200 | 2525125039.9677700 |
| 1900/QR1 | 232136.36881434554 | 230623.13222283978 | 230623.13222283978 | 52130432.027748300 | 2495023485.2011690 |

Main/1900 has **853** in-ban sector-months. Distress receipts comprise plot
**130786384.42549273**, material **28355910.171559263**. All retirement:
**757** events/**17586** buildings. Distress: **16** events/**518** buildings;
operating-positive distress: **13**/**280**, gross **85325583.69961971**.
This independently reproduces the initial complete 1900 measurement.

| Main/1900 sector | Distress events/buildings | Operating-positive events/buildings | Distress months |
|---|---:|---:|---|
| Copper Refining | 4/14 | 4/14 | 1777–1778, 1874–1875 |
| Cement & Concrete | 9/501 | 7/264 | 1830–1831, 1833–1839 |
| Refining | 3/3 | 2/2 | 1853, 2091, 2307 |

The sixteen sales' net-loss streaks are 24–33; operating-loss streaks 0–18,
all below the dial. Raw CSV/sidecar/log/snapshot:
`C:/Users/Jerus/AppData/Local/Temp/QR1-main-money-20261126/target/ensemble/QR1-main-1900-v2`.
Full-precision helper evidence copied to `target/qr1-main1900-report.json`
and `target/qr1-main1900-final-report.txt`.

QR1/1900 completed on the same PC and settings, snapshot **44630ff6e**,
model/default capture **1803d5cbc**, label `QR1-pair-1900-v2`.
LongPlaytest **PASS**, primary m2405 in 1792 s; **79/79** post-primary money
fixtures pass. All **78** shared CSV fields match main exactly through
calendar 1776; the first economic/money difference is exactly main's first
distress sale, m1777. Thus the observations and default player agree before
the rule first changes a sale.

QR1 retires **237** buildings in **two** Cement & Concrete distress sales,
m1837–1838: both have negative running income, operating-loss streaks
**24–25**, net-loss **27–28**, both at or beyond the dial. **Zero**
operating-positive distress sales. Distress plot cash **42012455.467058810**,
material **10117976.560689492**. All retirement: **748** events/**17302**
buildings. Its saved and independent cumulative F agree at the endpoint,
maximum interim difference 1.4551915228366852e-11; Fban and B agree, still
**853** in-ban sector-months. All five retirement cumulatives reconcile
exactly to their event-order sidecar at every month; all played audits pass,
no findings, numeric defects or gaps. Its auxiliary policy scorer also
retains exit 4 for the sole unaudited founding row, as explained above.

Matched 1900 result: **extra F = −770934.17800383846**,
**avoided D = 107011862.569303700**, nominal local-currency thousands.
Forgiveness falls, so the condition “extra F > 0 and greater than the
distress receipts it replaces” is **false**. Main's paying-only distress
receipts **85325583.69961971** remain descriptive context, not a substitute
for the actual all-distress difference. Both R totals are reported because
ordinary retirements also respond to the changed city.

QR1 raw files: `target/ensemble/QR1-pair-1900-v2`; derived full-precision
proof copied to `target/qr1-pair1900-report.json`. The two today measurements
remain pending; the formal ensemble is complete below.

### Formal 80-run ensemble

**PASS, exit 0**, 80/80 complete, source snapshot **4ead0c597**, executable
tree identical to **1803d5cbc**, base main **3bf83d20e** (fresh merge was
unchanged). Requested 02:44, admitted about 02:59 after the nightly;
finished/copied/scored 03:34:37 local on 2026-11-26, box runtime 34.8 min.
No laptop fallback or overlapping heavy job of this builder. The box ran
Eclipse Adoptium/OpenJDK 21.0.13 LTS. Native data paths are named in the
committed baseline; hashes equal the PC's data:
earth-2 `c26de66da2bdc2c831a27c59e4203f66`,
world-1 `c7ac48229b39a4a48c668b4a5fe5c8a8`.

| Score | 0.9.208 baseline | QR1 |
|---|---:|---:|
| M1/F1 hard criteria | 10/10 PASS | 10/10 PASS |
| Overall criteria | 23/26 | 24/26 |

AUTO2, ZERO_SLACK, ZERO_HOT, SHOCK and HIGH each have **M1: 0 failed,
0 unread** and **F1: 0/16 finding runs, no names**, matching all baseline
hard readings. Independent inspection of all eighty raw CSVs agrees.
Every policy has the exact seeds 0–15, all requested months, 80 DONE,
zero missing/short/NO-DONE/throw/exception logs. This ordinary policy
ensemble has only played rows; it is independent of the auxiliary default
measurements' unread founding baseline.

Only changed information verdict: ZERO_HOT H2 **FAIL → PASS**. Excess
inflation median **0.18% [−3.95%, 2.02%]**, 15/16 below their AUTO2 twin;
baseline **0.67% [−5.09%, 4.25%]**. AUTO2 A1 remains FAIL (2.93%
[2.50%, 3.44%]); A2 remains FAIL (14 [7, 24] months). H1 remains retired,
one retired criterion; none vacuous. These information scores are retained,
not changed into shipping requirements.

Committed score/provenance/summary: `docs/ensemble/QR1.md`. Full kept run:
`target/ensemble/QR1` (baseline, score, summary, snapshot, progress, times,
80 CSVs, 80 retirement sidecars, 80 logs and timing files); transport log
`target/QR1-box-ensemble.log`, exit file `target/QR1-box-ensemble.exit`.

### Today measurements and scheduled restart

The orchestrator announced a 05:00 restart, new turns held from 04:30,
work pushed by 04:30 and no heavy work that cannot end by 04:50.
After the formal ensemble, `QR1-main-today-v2` requested the box at 03:36,
but RES3-lodge-20261126 had acquired the ensemble lock. A helper verified
that it was another full 80-run matrix, seven complete at 03:45, with an
uncertain 20–40 minute queue remaining. Our default run had not started.
Waiting plus a full default playtest endangered the restart window.

★ Use the explicitly authorized PC-if-busy path, with one heavy job only.
First stop our queued request. Source review confirmed the TERM trap is on
`--ensemble-here`, not on `--serve`. At 03:48 the builder verified exact
command and owner of its own child PID **1233915**, then sent TERM to that
PID only. The wrapper returned **143**. Separate verification found its
serve **1233851**, child **1233915**, tail **1233916** and sed **1233917**
gone, no own-label processes or model JVM. RES3's same eight JVMs continued.
No model, snapshot or money export ran in this cancelled attempt, and no
totals from it are claimed. Evidence copied to
`target/qr1-main-today-v2-cancellation-proof.json`.

Fresh `QR1-main-today-v3` starts at 03:50 on the PC, unchanged main plus
observation source **4f0492990**, seed 0/Standard/today, the same native data
and `cbs.parallel=1`, one JVM. Its raw files are under
`C:/Users/Jerus/AppData/Local/Temp/QR1-main-money-20261126/target/ensemble/QR1-main-today-v3`;
parent log/exit are `target/QR1-main-today-v3-local.log` and `.exit`.
The counterpart must use the same settings/platform. The full 2400-calendar
window and post-primary assertions remain required; the old truncated
109-paying-event trace is context only. No dry-run request or ready-marker
removal before the paired table is complete. Remaining work resumes after
boot if it cannot fit safely.

P3 considered and not taken, as instructed: shorten or deny a fresh full
operating-loss runway after a long interest-only net-loss spell.
[11 USC 1112(b)(4)(A)](https://uscode.house.gov/us/usc/119-120/t11/s1112/)
identifies substantial/continuing estate loss together with lack of a
reasonable rehabilitation prospect as cause for conversion/dismissal. It
does not prescribe this model's twenty-four-month timer or make operating
profit a guarantee of viability. QR1 retains the separate saved operating
runway and existing debt resolution; no new viability rule or queue row is
added. The P2 measurements make the resulting support visible.
