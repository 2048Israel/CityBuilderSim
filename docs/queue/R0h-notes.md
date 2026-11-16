# R0h - the 1918 influenza's sick counted once

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
will execute and judge that full selection. Ensemble and dry run pending. The default trace
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
default five jobs, no parallelism override. Raw score: **18/25**, against the
newest main baseline docs/ensemble/0.9.117.md. The raw score, every changed verdict
and full summary are committed in docs/ensemble/R0h.md before the dry run.

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
