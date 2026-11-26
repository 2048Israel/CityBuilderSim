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
dry run follow review, using `docs/ensemble/0.9.206.md` or a newer 1900
baseline if main ships one.

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
