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
