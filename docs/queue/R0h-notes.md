# R0h - the 1918 influenza's sick counted once

**Final checks complete.** Launcher exit 0 (suite exit 3, five inherited known-red
harnesses), traced playtest exit 0, sealed report equivalent, no trace difference
before January 1919. The main-proven seed-12 housing finding is queued as HN1;
its raw ensemble F1 failures remain recorded and it has no standing gate exemption.

## What changed

Health exposes its capped sick rate without influenza. Sickness meets that rate
with its mortality ring and keeps the difference between each band's full and
chronic shares beside it. The visible sick share, total sick and youngest Services
bar include those acute cases; long sickness and death rates read only the ring.
Both the initial seed and monthly handover in Game pass the two rates.
SimulationEngine.simulateMonth calls Game.advanceDemographics, which calls the same
health-service step; it has no separate Health-to-Sickness handover to change.

The saved sicknessAcute array travels through DataSave and Game's write/load paths,
using the save's band names. Missing state on an old save restores none. The existing
ring format and four-argument seed/advanceMonth callers remain intact.

## Calls

- ★1 Carry the acute share separately, as the brief specifies: influenza's own
  deaths are already in CausesOfDeath, but its time off work and visible sick must
  remain. Compute the difference after each band's sickness ceiling, so neither
  babies nor elders exceed the full-rate share.
- ★2 Cap Health's sum without influenza independently. Subtracting influenza from
  the capped full rate loses chronic sickness when the full rate reaches its cap.
- ★3 Keep the ring's saved state unchanged and save acute shares in a named field.
  Restore by saved band names and clear missing/malformed arrays. No format bump:
  the brief defines an old save's missing value as none. There is no history series
  for this monthly flow.
- ★4 Preserve R0g's stocked-city September 1918 twins and hold only Health's
  influenza in one, with causes on in both. Replay each twin's observed chronic
  Health rate and the care coverage the month actually read into a separate ring.
  Their opening populations times those chronic rates measure each month's own
  economic drift. January-April's extra ring dead beyond that drift must be zero
  within rounding. The fixture separately proves competing-risk caps did not
  reduce those projected ring deaths. Dates follow INFLUENZA_FIRST; the save,
  city, holds and four tested months are unchanged.

## Before the fix

Base: origin/main 9c36a155b (0.9.119). New unit and twin fixtures ran against the
old model, with temporary five-argument bridges that used its existing full rate.
The unit failed ring equality, death/long-sick equality and acute people in the
shortest bar (3 failures). Every January-April twin assertion failed (4 failures):

| Month | Current main's extra ring dead | R0g's historical main figure |
|---|---:|---:|
| January 1919 | 2.547992 | 2.24 |
| February 1919 | 2.736714 | 2.65 |
| March 1919 | 1.198201 | 1.12 |
| April 1919 | 0.369071 | 0.38 |

The current twins' May-December drift was 0.219441; R0g recorded about 0.3.
The old run's fast completeness verdict was exit 1 due to an orphaned fixture
javadoc, corrected before the final checks. Logs: target/r0h-before.log,
target/r0h-before-sickness.log and target/r0h-before-causes.log.

## Verification

The first focused run passed SicknessCheck, HealthCheck, SaveFileCheck and
ReadPathCheck, but correctly rejected a poor drift proxy in the new twin fixture:
April's economic difference was 0.082317, larger than the 0.062454 maximum in
May-December. A later quiet period cannot bound each earlier month's economy.
The fixture now measures the chronic drift from each month's actual inputs,
rather than widening that tolerance or moving the original twin premise. The
final fixture was rerun with a negative control restoring the old full-rate ring.
It failed all four January-April laws again, by 2.549064, 2.559092, 0.713350 and
0.209324 deaths beyond their measured chronic drift (exit 4). The unit again
failed the ring/death/people laws. The competing-risk cap premise passed.
Logs: target/r0h-negative.log, target/r0h-negative-sickness.log and
target/r0h-negative-causes.log. The negative control is restored out of source
before compiling the final model.

Corrected SicknessCheck and CausesOfDeathCheck: exit 0, including all four
January-April laws, the reload played through March 1919 to the bit, care-cap
premises and epidemic visibility. Fast check: exit 0, zero firm Stale findings.
SaveFileCheck, ReadPathCheck and HealthCheck passed in the earlier focused run;
HealthCheck reported one existing played-path OBSERVED condition, not a failure.
Final logs: target/r0h-corrected.log, target/r0h-final-causes.log and
target/r0h-final-sickness.log. Source whitespace check passed; generated maps keep
the generator's existing Markdown hard-break whitespace.

The selection is ALL (Game.java has 171 readers, 207 harnesses in this main),
so no local full suite or local ALL pick is run. The normal launcher dry run
will execute and judge that full selection. The final ensemble, launcher and trace results are recorded below. The default trace
changes because it crosses 1918; an ensemble is required. No current known-reds
entry names R0h. No process machinery was changed, so the one-in/one-out count
does not apply.

## Stranger review: the founding rate

REV-R0h-1 reviewed b822ded0 and found that a fresh Health records sickRate as
zero while baselineRate starts at WELL_SERVED_RATE. The new getter originally
summed that baseline before the first advance and seeded phantom long sickness
outside the influenza. Accepted and mended: when influenzaRate is zero, return
the recorded sickRate exactly; only an influenza month needs the separately
capped chronic sum. This also preserves bit equality outside the episode.

★5 The founding must preserve the recorded zero rather than invent a baseline
history. A fresh real city's first month is compared slot-for-slot with main's
four-argument seed and handover. Its initial and advanced no-influenza rates
must match the full rate exactly. The fixture runs before and after the mend.
A shortened default trace through December 1919 compares all eight CSVs against
main's original Health, Sickness, Game and DataSave classes; it is a bounded
onset diagnostic, not a replacement ensemble baseline.

The new founding fixture failed both initial-rate equality (0.03 versus 0) and
first-month ring equality on b822ded0's getter (exit 4), then passed after the
mend. SicknessCheck and CausesOfDeathCheck together: exit 0, zero firm Stale
findings. Logs: target/r0h-founding-before.log,
target/r0h-founding-before-sickness.log, target/r0h-founding-after.log,
target/r0h-reviewed-sickness.log and target/r0h-reviewed-causes.log.

Both shortened default LongPlaytest runs (main and fixed) exited 0. Every trace
CSV is identical through December 1918 (month 228), including all the years
before influenza. The first differences are January 1919 (month 229) in month,
population, household, build and bonds; bank and labour first differ at 230;
borrow is identical for the whole diagnostic. Thus no founding or pre-episode
drift remains: the first trace change is the delayed mortality correction in
1919, not an earlier 1918 economic change. The changed lives naturally affect
later months. Logs and all eight pairs: target/r0h-trace/; comparison provenance:
main's 9c36a155 original Health, Sickness, Game and DataSave over the otherwise
identical compiled branch, JDK/data settings as the focused checks.

After the founding mend, the January-April twins' raw ring differences are
-0.001072, +0.180864, +0.491282 and +0.165453 deaths. Their independently replayed
chronic drift is exactly the same in each month; residual excess is zero within
1e-9 in all four. Influenza still books about two deaths in each of those months.

The orchestrator accepted 4a6f3ebf's mend and closed REV-R0h-1 on 2026-11-15.
The authorized ensemble uses that classes snapshot against current main
9c36a155 (0.9.119), with the newest stored baseline 0.9.117. The two intervening
versions are kept-screen repairs and do not change the default model trace.

Main moved to 6114b00f (ER1) during the ensemble. Merged as requested; the only
Java changes on that main increment are ER1Check and AllChecks' registration.
LongPlaytest and the model are unchanged, so the default trace does not move
and the in-flight 4a6f3ebf ensemble remains valid. Three generated-index conflicts
(harnesses, LongPlaytest's map and the map README) took main temporarily and are
regenerated from the merged sources after the ensemble, before the dry run.

## Ensemble: final accepted model

80/80 runs completed, none short or missing, in 92.0 minutes. Snapshot 4a6f3ebf7,
label R0h, Earth earth-2.bin and world-1.bin at the instructed native paths,
default five jobs, no parallelism override. Raw score: **18/25**, against
**23/25** on the newest main baseline docs/ensemble/0.9.117.md (hard 8/10
against its 10/10). The raw score, every changed verdict
and full summary are committed in docs/ensemble/{next}.md before the dry run.

M1 passes for all five policies: no failed or unread money audits. Raw hard
verdict: **8/10, FAIL**, exit 4, because AUTO2 F1 and ZERO_SLACK F1 each have one
finding run against the baseline's zero. Both are seed 12, the same
"households with no home" at m744 (repeated through m747); their policies have
not diverged at that date. No other finding type occurred. A same-state main
counterfactual is required before disposition; the watch's threshold stays >1.
The orchestrator requires a capacity go before that probe, after the ensemble.

Informational PASS-to-FAIL changes: AUTO2 A3, AUTO2 T2 and ZERO_SLACK T2.
They retain their raw values in the baseline; the only shipping criteria are
M1 and F1. Logs: target/r0h-ensemble.log; score, times, all CSVs and snapshot:
target/ensemble/R0h/. No main ensemble was rerun.

## Same-state F1 probe

Authorized after the ensemble, one job with sequential JVMs, exit 0. Replayed
AUTO2 seed 12 to m748 with a checkpoint taken at m743. **Every CSV field of the
whole replayed prefix equals the kept 80-run ensemble's prefix**, so taking the
checkpoint did not move its premise. From the same checkpoint, R0h's accepted
model and main **6114b00f's complete model** load and play 744-748 identically.

The main runtime uses the original Health, Sickness, Game (including its nested
classes) and DataSave sources from 6114b00f. These are the only four changed model
classes between main and the accepted 4a6f3ebf snapshot; every other source
difference is a harness/registration (source-diff.txt). Thus this is the whole
main model, with its original saved field list and original handovers. It is
not the temporary negative-control bridge used for the unit fixture.

| Month | Main unplaced households | R0h unplaced households | Homes |
|---|---:|---:|---:|
| 743 (load) | 0 | 0 | 3,044 |
| 744 | 3.592771208418526 | 3.592771208418526 | 3,044 |
| 745 | 7.848705090851581 | 7.848705090851581 | 3,044 |
| 746 | 12.491581939075331 | 12.491581939075331 | 3,044 |
| 747 | 17.197984450481272 | 17.197984450481272 | 3,044 |
| 748 | 0 | 0 | 3,107 |

The original >1 homeless watch fires on main in all four months. Fingerprints,
all money pools, full family state, sickness rings, cohorts, Health and causes
are equal to the bit in every one of the five months. The saved acute shares
and Health influenza rate are all zero at 743. Both AUTO2 and ZERO_SLACK have
the exact same first finding at 744, before their policies diverge.

★6 Following the orchestrator's conditional disposition, this is main's latent
housing fault reached by R0h's path, not a defect in the acute share. Queue **HN1**
(batch-id.sh accepted, exit 0) to diagnose housing/placement and the late matching
homes. Its brief and ship-file queue/known-reds rows retain the hard watch; raw
F1 failures stay in the baseline. No housing model, fixture premise or tolerance
was changed. No main ensemble was rerun.

Full evidence: target/r0h-homeless/result.log, capture.log, fixed.log, main.log,
replayed.csv, fixed-state.txt, main-state.txt, source-diff.txt, src/, snapshot/.
The one-seed prefix took 69.9 seconds; the main and fixed replays are five months
each. Data settings are the same named Earth/world files as the ensemble.

Main then moved to **01402a54 (0.9.120, BK2b)** and was merged at c9fb4bd0.
BK2b's notes section "Full merge gate and trace" records identical normalized
playtest logs (SHA256 01a23171750932ce03e3ee4f8920d4a3c542fa39bec532b1c58ed9716533ffe4).
Its rounding repair, Game comment and HoldersCheck do not move the default trace;
the orchestrator explicitly retained the ensemble and allowed the 6114 model
for the counterfactual. The final fast check regenerates all merged indexes.

Final merged fast check: **exit 0**, compile, regenerated indexes and zero firm
Stale findings (target/r0h-pre-gate.log), against main 01402a54. Its model trace
is unchanged from the main used by the ensemble, as recorded above. The pick is
ALL and is reserved for the normal launcher dry run; no local full suite ran.

★7 Before the dry run, the orchestrator removed the broad LongPlaytest
"households with no home" exemption from the ship file. The ensemble is not
judged through known-reds and the default gate seed does not print this line;
a registry row would exempt any later cause until HN1 ships. Keep HN1 queued
and the raw F1 evidence, with no standing registry cover. If the default gate
actually prints it, report that specific line for disposition. The conditional
main-proven F1 acceptance stands; this narrows the records, not the watch.

Dry-run authorization also confirmed our source patch-id is identical to the
accepted 4a6f3ebf source, apart from merged main. The baseline was committed
before the launcher. No push is made while the dry run is active.

## Kept launcher and full default trace

Normal route: sh tools/gate.sh R0h --dry-run, detached and awaited, default
suite resource pool, native Earth/world properties, no serial override.
**Launcher exit 0; suite exit 3 in 1,989 seconds.** The default gate selection
ran 194 harnesses in 1,915 seconds: 189 passed, five inherited known-red harnesses
(DeathRecordCheck, InvestCheck, MapCheck, MoneyDatesCheck, ResearchViewCheck).
No NEW/unnamed lines: unnamed.txt is empty. LongPlaytest and every changed harness
pass. Fourteen timing assertions are soft and three played-path premises are
OBSERVED, as the normal gate reports; Stale has zero firm findings.

Kept run (for reuse):
C:/Users/Jerus/AppData/Local/Temp/cbs-gate-R0h-NrRv6F33

The two inputs are:
- branch: **8e09577ab11e7d0e27a7cd1f9cedc27576e640cc**
- main: **01402a54e58881a5283a84933e766a38b5f1a089**

Only this notes file and the ship file advance after that run. The kept
checks/generated SHA256 manifest verifies before and after the trace work
(exit 0); all trace artifacts are in the separate traces/ folder.

The trace flag was omitted from the launcher environment. The orchestrator
reserved one sequential traced LongPlaytest after ALL. The initial helper found
that gate.sh deletes the successful clone, including its classes, and stopped
before Java. The orchestrator authorized the final-fast worktree classes from
identical pinned source instead: git diff 8e09577ab HEAD -- src and git status
--porcelain -- src are both empty (traces/source-diff.txt and source-status.txt).
The source tree/commit manifests are beside them. This build says 0.9.120 while
the publisher's dry merge says 0.9.121; the model source/resources are the same.

The one actual traced playtest exits 0. Its full report equals the sealed
checks/LongPlaytest.log in all **480 lines** after removing the JDK banner,
wall-clock prefixes, the same two runtime measurements used by BK2b, and the
two build-version strings. Normalized SHA256, both reports:
4f7636e21e472d8c99bfb3ceabb4b835c0975a15e5162d15c4b897c8385c051a

The native standalone stdout used CP1252 (AllChecks explicitly requests UTF-8).
It is preserved as traces/LongPlaytest.cp1252.log and losslessly transcoded to
UTF-8 as LongPlaytest.log and LongPlaytest.utf8.log before comparison. No model
value, assertion, or other report text is normalized. equivalence.py,
equivalence.json and the empty equivalence.diff retain the exact check.
The orchestrator was told as soon as the JVM exited, freeing FA1's slot.

All eight CSVs compare against the orchestrator's current main reference:
C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/.claude/worktrees/codex-XR7/target/checks/XR7-postmerge/trace-*.csv
R2d, KP2c, ER1 and BK2b preserve that reference. The first moved month is:

| CSV | First changed month |
|---|---|
| bank | 230 - February 1919 |
| bonds | 229 - January 1919 |
| borrow | 273 - September 1922 |
| build | 229 - January 1919 |
| house | 229 - January 1919 |
| labour | 230 - February 1919 |
| month | 229 - January 1919 |
| pop | 229 - January 1919 |

Nothing moves before the influenza or during its initial 1918 sick/work months.
The first demographic/money change is the corrected delayed ring mortality in
January 1919. Borrowing first exposes the later changed rate at 273. The seven
monthly series each contain 2,405 CSV rows including the header; borrowing has
52 baseline rows and 55 current rows. All eight files, exact per-file hashes,
byte/row counts and first differing columns are retained in traces/comparison.json.
There was no rerun of main. The raw 80-run score and HN1 disposition stand.
