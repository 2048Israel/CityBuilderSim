<!-- gate-run: /tmp/cbs-gate-HA1-zejvr1PT -->

## changelog
### HA1 ? the gate's harnesses ? 2026-11-13, ready, no tag (process), see docs/queue/HA1-notes.md
Harnesses start longest first from their last durations; soft runs skip
clock-only rounds. The gate's nightly tier runs when directly picked;
nightly reds use main's judge and name their merge range. Five merged fixture
groups keep their assertions. Dead old-format converters, ConversionCheck
and the listed run observations are removed. Current-format land repair
stays; damaged centres are refused before rebuild. Gate records retain the
reds of unrun harnesses. Allocated-land restoration stays AW2's.

## pending
| HA1-CLOCK | Re-measure SaveWriter.CLOCK_MS as exercised by SaveSlotCheck on the quiet gate box and apply the normal budget margin (HA1) | Gate box arrives November 14?15; the 25 ms budget is unchanged until then | [brief](docs/queue/HA1.md) |
| HA1-NIGHTLY | Observe the first real nightly tier run on main and its merge-range report (HA1) | HA1 must be published before the scheduled main run; production selection and common-judge attribution pass selftests | [notes](docs/queue/HA1-notes.md) |
